package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.SiteUserDAO;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.LoginService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jasypt.digest.StandardStringDigester;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implements loginService.
 * 
 * @author Adam Russell (18816)
 * @author Satinder Gill (18922)
 * @see loginService
 */
@Transactional
public class LoginServiceImpl extends BaseServiceImpl implements LoginService {
    private StandardStringDigester digester;
    private final UserService userService;
    private static final long INACTIVE_DAYS_THRESHOLD = 240;
    private static final String LOGIN_ERRORS = "loginErrors";
    private static final String USER_NAME = "userName";
    private static final String DESCRIPTION = "description";
    private static final String USER_CHANGING_PASSWORD = "userChangingPassword";
    private static final String ISEAVS = "ISEAVS";
    private static final String AUTHLOG_SUCCESS = "Successful login";
    private static final String AUTHLOG_FAIL = "Invalid login attempt";
    private static final String AUTHLOG_FAKE = "Fake user login attempt";
	
    @Setter @Getter private String systemInfoId;
    @Setter @Getter private String jndiNytdAuth;
    @Getter @Setter private SiteUserDAO siteUserDAO;

    // constructor
    public LoginServiceImpl(UserService userService) {
        super();
        this.userService = userService;
    }
	
    /**
     * @see LoginService#processLogin(String, String, Map<String, Object>)
     */
    //TODO: Sonarcube - Cognitive Complexity of methods should not be too high. 
    @Override
    public final SiteUser processLogin(final String username, final String password, Map<String, Object> session) {
                Session dbSession = null; 
                SessionFactory sessionFactory = getSessionFactory();
                if (sessionFactory != null) {
                    dbSession = sessionFactory.getCurrentSession();
                } else {
                    throw new IllegalStateException("session factory is null in processLogin()");
                }
		SiteUser siteUser = null;
		FakeUser fakeUser = null;

                //TODO: Move hibernate query to DAO. 
		CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
		log.info("Inside ProcessLogin");

		clearLoginErrors(session);
		LinkedList<String> loginErrors = new LinkedList<>();
		session.put(LOGIN_ERRORS, loginErrors);
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);

		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(criteriaBuilder.equal(criteriaBuilder.lower(root.get(USER_NAME)), username.toLowerCase()));

		root.fetch("state", JoinType.LEFT);
		root.fetch("region", JoinType.LEFT);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = dbSession.createQuery(criteriaQuery);
		try {
			siteUser = q.getSingleResult();
		} catch (HibernateException e) {
			log.error(e.getMessage());
		}
		
		log.info("comparing user");
		if (siteUser != null) {
                    log.info( "Checking whether user: "+ siteUser.getUserName() + " should be allowed to log in.");
                    
                    // First, check user role and ask federal user to use PIV login
                    if(siteUser.getPrimaryUserRole()!= null 
                            && (siteUser.getPrimaryUserRole().getId() == 2 || siteUser.getPrimaryUserRole().getId() == 3 )) {
			log.info("Federal user attempt to use NYTD credential login");
			loginErrors.add("Federal user can only login via Federal User tab");
			return null;
                    }
                    log.info("User PWCHANGEKEY: " + siteUser.getPwChangeKey());
                    log.info("User PWCHANGEDATE: " + DateUtil.toDateString(siteUser.getPwChangedDate()));
                    log.info("User PWTEMPORARY: " + siteUser.getPwTemporary());
                    
                    if(!siteUser.isDeleted()) {
			log.info("1 - Site user: "+ siteUser.getUserName() +" is not marked as deleted");
			if (!siteUser.isLocked()) {
                            log.info("2 - Site user: "+ siteUser.getUserName() +" is not locked");
                            if (!siteUser.isTemporarilyLocked()) {
                                log.info("3 - Site user: "+ siteUser.getUserName() +" is not templocked");
						
				//check for inactive user
				if (inactiveUser(siteUser.getUserName())) {
                                    log.info("4 - Site user: "+ siteUser.getUserName() +" is inactive making it locked");
                                    saveLock(siteUser);
                                    this.signalInactiveUserPermaLocked(loginErrors);
                                    return null;							
				}
						
				Boolean isEAVS =(Boolean) session.get(ISEAVS);
				session.remove(ISEAVS);
						
				//NYTD-59: Enforce password change at first log on
				if (siteUser.getPwTemporary()) {
                                    log.info("Password temporary for site user: "+ siteUser.getUserName() +" enforcing password change on first login");
                                    session.put(USER_CHANGING_PASSWORD, siteUser);
                                    session.put(ISEAVS, isEAVS);
                                    return siteUser;
				}
						
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -60);
				Date sixtyDaysAgo = cal.getTime();
						
				if(siteUser.getPrimaryUserRole()!=null 
                                        && ((!isEAVS.booleanValue() && siteUser.getPrimaryUserRole().getId() == 1) 
                                        || siteUser.getPrimaryUserRole().getId() == 4 )) {
                                    if (digester.matches(password, siteUser.getPassword())) {
                                        //NYTD-9: If existing password does not meet password strength requirement, force password change
					if (!userService.passwordIsStrong(password)) {
                                            log.info("Password not strong for site user: "+ siteUser.getUserName() 
                                                    +" enforcing password change as existing password does not meet password strength requirement");
                                            session.put(USER_CHANGING_PASSWORD, siteUser);
                                            session.put(ISEAVS, isEAVS);
                                            return siteUser;
					}
					//NYTD-57: Enforce password changes for non-federal user accounts after 60 days 
					else if (siteUser.getPwChangedDate().getTime().before(sixtyDaysAgo)) {
                                            log.info("Password enforcing for site user: "+ siteUser.getUserName() 
                                                    +" enforcing password change for non-federal user accounts after 60 days");
                                            session.put(USER_CHANGING_PASSWORD, siteUser);
                                            session.put(ISEAVS, isEAVS);
                                            return siteUser;
					}
								
					this.removeAuthLogEntries(siteUser.getUserName(), AUTHLOG_SUCCESS, AUTHLOG_FAIL);
					this.removeTimeLock(siteUser);
					setLoggedInUser(session, siteUser);
					saveAuthLogEntry(siteUser.getUserName(), new Timestamp((new Date()).getTime()), session, AUTHLOG_SUCCESS);
					log.info("5 - Site user: "+ siteUser.getUserName() + " and primary role as: "
                                                + siteUser.getPrimaryUserRole().getName() + " is logged in successfully");
					return siteUser;
                                    } else {
					this.processUserAuthLog(siteUser, session);
					return null;
                                    }
                                } else if(siteUser.getPrimaryUserRole() != null 
                                        && (siteUser.getPrimaryUserRole().getId() == 2 || siteUser.getPrimaryUserRole().getId() == 3 
                                                ||(isEAVS.booleanValue() && siteUser.getPrimaryUserRole().getId() == 1))) {
                                    //NYTD-57: Enforce password changes for non-federal user accounts after 60 days 
                                    if(siteUser.getPrimaryUserRole().getId() != 2 && siteUser.getPwChangedDate().getTime().before(sixtyDaysAgo)) {
                                        log.info("For site user: "+ siteUser.getUserName() +" enforcing password change for non-federal user accounts after 60 days");
					session.put(USER_CHANGING_PASSWORD, siteUser);
					session.put(ISEAVS, isEAVS);
					return siteUser;
                                    }

                                    this.removeAuthLogEntries(siteUser.getUserName(), AUTHLOG_SUCCESS, AUTHLOG_FAIL);
                                    this.removeTimeLock(siteUser);
                                    setLoggedInUser(session, siteUser);
                                    saveAuthLogEntry(siteUser.getUserName(), new Timestamp((new Date()).getTime()), session, AUTHLOG_SUCCESS);
                                    log.info("6 - Site user: "+ siteUser.getUserName() +" and primary role as: "+siteUser.getPrimaryUserRole().getName() +" logged in successfully");
                                    return siteUser;
				} else {
                                    return null; 
                                }
                            } else {
                                this.signalTimeLocked(loginErrors);
                                return null;
                            }
			} else {
                            this.signalPermaLocked(loginErrors);
                            return null;
			}
                    } else {
			log.info("No such user exists in the system.");
			loginErrors.add("No such user exists in the system");
			return null;
                    }
		} else {
                    //TODO: Move hibernate query to DAO. 
                    CriteriaQuery<FakeUser> cq = criteriaBuilder.createQuery(FakeUser.class);
                    Root<FakeUser> r = cq.from(FakeUser.class);
                    cq.where(
			criteriaBuilder.equal(r.get(USER_NAME), username)
                    );
                    cq.select(r);
                    TypedQuery<FakeUser> query = dbSession.createQuery(cq);				
                    try {
			if(!query.getResultList().isEmpty()) {
                            fakeUser = query.getSingleResult();
			}			
                    } catch (Exception e) {
			log.warn("Inside exception of fake user processlogin");
			log.error(e.getMessage());
                    }
                    log.info("check Fake User");
                    if (fakeUser != null) {
                        if (!fakeUser.isLocked()) {
                            if (!fakeUser.isTemporarilyLocked()) {
                                this.processUserAuthLog(fakeUser, session);
				return null;
                            } else {
				this.signalTimeLocked(loginErrors);
				return null;
                            }
			} else {
                            this.signalPermaLocked(loginErrors);
                            return null;
			}
                    } else {
			log.info("User is fake");
			this.saveFakeUser(username);
			this.saveAuthLogEntry(username, new Timestamp((new Date()).getTime()), session, AUTHLOG_FAKE);
			return null;
                    }
		}
    }
	
    /**
     * @see LoginService#getFederalUserByEmail(String, Map<String, Object>)
     */
    @Override
    public final SiteUser getFederalUserByEmail(final String email, Map<String, Object> session) {
        SiteUser siteUser = null;
            
        // Reset previous login errors
        clearLoginErrors(session);
        LinkedList<String> loginErrors = new LinkedList<>();
        session.put(LOGIN_ERRORS, loginErrors);
        List<SiteUser> siteUserList = new ArrayList<>();
		
        // Get federal user list on matching email
        try {
            siteUserList = getSiteUserDAO().getFederalUserListByEmail(email);
        } catch (Exception e) {
            log.error("Error getting federal user list by email: " + email);
            log.error(e.getMessage(), e);
            return null;
        }
            
        // Display error in UI and return null if none or multiple users exist
        if (siteUserList.isEmpty()) {
            log.error("No such federal user exists in the system.");
            loginErrors.add("No such user exists in the system");
            return null;
        } else if (siteUserList.size() > 1) {
            log.error("Multiple emails for the federal user exist in the system.");
            loginErrors.add("Multiple emails for the user exist in the system");
            return null;
        }
		
        // Only one user in the list, get the user
        siteUser = siteUserList.get(0);
        log.info("federal siteUser is: "+ siteUser);
		
        return siteUser;
    }
	
    /**
     * @see LoginService#getAllActiveUserListByEmail(String)
     */
    @Override
    public final List<SiteUser> getAllActiveUserListByEmail(final String email) {
        List<SiteUser> siteUserList = new ArrayList<>();
        try {
            siteUserList = getSiteUserDAO().getAllUserListByEmail(email);
        } catch (Exception e) {
            log.error("Error in getAllActiveUserListByEmail(), email: " + email);
            log.error(e.getMessage(), e);
            return siteUserList;
        }
        return siteUserList;
    }
        
    /**
     * @see LoginService#inactiveUser(String)
     */
    @Override
    public boolean inactiveUser(String username) {
        Session session = null; 
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in inactiveUser()");
        }
        boolean result = false;
        AuthLog logEntry = null;
	//TODO: Move hibernate query to DAO. 
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<AuthLog> criteriaQuery = criteriaBuilder.createQuery(AuthLog.class);
        Root<AuthLog> root = criteriaQuery.from(AuthLog.class);
        criteriaQuery.where(
            criteriaBuilder.equal(root.get(USER_NAME), username),
            criteriaBuilder.equal(root.get(DESCRIPTION), AUTHLOG_SUCCESS)
        );
        criteriaQuery.select(root);
        TypedQuery<AuthLog> q = session.createQuery(criteriaQuery);		
		
        try {
            if(!q.getResultList().isEmpty()) {
                logEntry = q.getSingleResult();
            }			
        } catch (Exception e) {
            log.warn("Inside exception of logEntry");
            log.error(e.getMessage());
        }
		
        if (logEntry != null) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long lastLogin = logEntry.getAttemptTime().getTime();
            long diff = currentTime - lastLogin;
            if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > INACTIVE_DAYS_THRESHOLD) {
                result = true;
            }
        }	
        return result;
    }

    /**
     * @see loginService#processUserAuthLog(T)
     */
    public <T> void processUserAuthLog(T user, Map<String, Object> session) {
		Session dbSession = null; 
                SessionFactory sessionFactory = getSessionFactory();
                if (sessionFactory != null) {
                    dbSession = sessionFactory.getCurrentSession();
                } else {
                    throw new IllegalStateException("session factory is null in processUserAuthLog()");
                }
		List<AuthLog> authLogEntries;
		LinkedList<String> loginErrors = getLoginErrors(session);
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		int numAuthLogEntries;
		boolean userWasTimeLocked = false;
		String username = "";
		long currentWindow;
		int windowThreshold = 600000; // 10 minutes

		if (user instanceof SiteUser)
		{
			username = ((SiteUser) user).getUserName();
		}
		else if (user instanceof FakeUser)
		{
			username = ((FakeUser) user).getUserName();
		}
		//TODO: Move hibernate query to DAO. 
		CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
		CriteriaQuery<AuthLog> criteriaQuery = criteriaBuilder.createQuery(AuthLog.class);
		Root<AuthLog> root = criteriaQuery.from(AuthLog.class);
		criteriaQuery.where(
					criteriaBuilder.equal(root.get(USER_NAME), username),
					criteriaBuilder.equal(root.get(DESCRIPTION), AUTHLOG_FAIL)
		);
		criteriaQuery.select(root);
		TypedQuery<AuthLog> q = dbSession.createQuery(criteriaQuery);	
		authLogEntries = q.getResultList();
		
		numAuthLogEntries = authLogEntries.size();

		if (numAuthLogEntries < 4)
		{
			log.info("Username: "+ username +" "+ AUTHLOG_FAIL);
			this.saveAuthLogEntry(username, currentTime, session, AUTHLOG_FAIL);
			return;
		}

		currentWindow = currentTime.getTime()
				- authLogEntries.get(numAuthLogEntries - 4).getAttemptTime().getTime();

		if (currentWindow <= windowThreshold)
		{
			if (user instanceof SiteUser)
			{
				userWasTimeLocked = ((SiteUser) user).getTimeLocked() != null;
			}
			else if (user instanceof FakeUser)
			{
				userWasTimeLocked = ((FakeUser) user).getTimeLocked() != null;
			}
			if (userWasTimeLocked)
			{
				this.saveLock(user);
				this.signalPermaLocked(loginErrors);
			}
			else
			{
				this.saveTimeLock(user, currentTime);
				this.signalTimeLocked(loginErrors);
			}
		}
		else
		{
			log.info("Username: "+ username +" "+ AUTHLOG_FAIL);
			this.saveAuthLogEntry(username, currentTime, session, AUTHLOG_FAIL);
		}
    }
	
    /**
     * @see loginService#saveFakeUser(String)
     */
    public void saveFakeUser(final String username) {
		Session session = null; 
                SessionFactory sessionFactory = getSessionFactory();
                if (sessionFactory != null) {
                    session = sessionFactory.getCurrentSession();
                } else {
                    throw new IllegalStateException("session factory is null in saveFakeUser()");
                }
		FakeUser fakeUser = null;
                //TODO: Move hibernate query to DAO. 
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<FakeUser> criteriaQuery = criteriaBuilder.createQuery(FakeUser.class);
		Root<FakeUser> root = criteriaQuery.from(FakeUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get(USER_NAME), username)
		);
		criteriaQuery.select(root);
		TypedQuery<FakeUser> q = session.createQuery(criteriaQuery);	
		

		try {
			if(!q.getResultList().isEmpty()) {
				fakeUser = q.getSingleResult();
			}			
		} catch (Exception e) {
			log.warn("Inside exception of saveFakeUser");
			log.error(e.getMessage());
		}
		if (fakeUser != null)
		{
			return;
		}

		fakeUser = new FakeUser();
		fakeUser.setUserName(username);
		session.saveOrUpdate(fakeUser);
    }
	
    /**
     * @see loginService#removeTimeLock(SiteUser)
     */
    public void removeTimeLock(SiteUser user) {
        Session session = null; 
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in removeTimeLock() ");
        }
        user.removeTimeLock();
        session.saveOrUpdate(user);
    }

    /**
     * @see loginService#saveTimeLock(T, Timestamp)
     */
    public <T> void saveTimeLock(T user, Timestamp lockTime) {
        if (user instanceof SiteUser || user instanceof FakeUser) {
            Session session = null; 
            SessionFactory sessionFactory = getSessionFactory();
            if (sessionFactory != null) {
                session = sessionFactory.getCurrentSession();
            } else {
                throw new IllegalStateException("session factory is null in saveTimeLock()");
            }
            if (user instanceof SiteUser) {
                ((SiteUser) user).setTimeLocked(lockTime);
                session.saveOrUpdate((SiteUser) user);
            } else if (user instanceof FakeUser) {
                ((FakeUser) user).setTimeLocked(lockTime);
                session.saveOrUpdate((FakeUser) user);
            }
        } else {
            throw new IllegalArgumentException("Invalid user instance: " + user);
        }
    }

    /**
     * @see loginService#saveLock(T)
     */
    public <T> void saveLock(T user) {
        if (user instanceof SiteUser || user instanceof FakeUser) {
            Session session = null; 
            SessionFactory sessionFactory = getSessionFactory();
            if (sessionFactory != null) {
                session = sessionFactory.getCurrentSession();
            } else {
                throw new IllegalStateException("session factory is null in saveLock()");
            }
            if (user instanceof SiteUser) {
                ((SiteUser) user).setLocked(true);
                session.saveOrUpdate((SiteUser) user);
            } else if (user instanceof FakeUser) {
                ((FakeUser) user).setLocked(true);
                session.saveOrUpdate((FakeUser) user);
            }
        } else {
            throw new IllegalArgumentException("Invalid user instance: " + user);
        }
    }

    /**
     * @see loginService#saveAuthLogEntry(String, Timestamp)
     */
    public void saveAuthLogEntry(String username, Timestamp attemptTime, Map<String, Object> session, String description) {
        Session dbSession = null; 
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            dbSession = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in saveAuthLogEntry()");
        }
        AuthLog authLog = new AuthLog();
        authLog.setUserName(username);
        authLog.setAttemptTime(attemptTime);
        authLog.setDescription(description);
        dbSession.saveOrUpdate(authLog);
		
        if (!description.equals(AUTHLOG_SUCCESS)) {
            this.signalBadAttempt(getLoginErrors(session));
        }
    }

    /**
     * @see loginService#removeAuthLogEntries(String)
     */
    public boolean removeAuthLogEntries(String username, String...description) {
		Session session = null; 
                SessionFactory sessionFactory = getSessionFactory();
                if (sessionFactory != null) {
                    session = sessionFactory.getCurrentSession();
                } else {
                    throw new IllegalStateException("session factory is null in removeAuthLogEntries()");
                }
		boolean result = false;
		List<AuthLog> authLogEntries;
                //TODO: Move hibernate query to DAO. 
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<AuthLog> criteriaQuery = criteriaBuilder.createQuery(AuthLog.class);
		Root<AuthLog> root = criteriaQuery.from(AuthLog.class);
		if (description != null) {
			criteriaQuery.where(
					criteriaBuilder.equal(root.get(USER_NAME), username),
					root.get(DESCRIPTION).in(Arrays.asList(description))
			);
		} else {
			criteriaQuery.where(
					criteriaBuilder.equal(root.get(USER_NAME), username)
			);
		}
		criteriaQuery.select(root);
		TypedQuery<AuthLog> q = session.createQuery(criteriaQuery);	
		authLogEntries = q.getResultList();
		
		if (!authLogEntries.isEmpty())
		{
			result = true;
		}
		for (AuthLog authLog : authLogEntries)
		{
			session.delete(authLog);
		}

		return result;
    }

    /**
     * @see loginService#clearLoginErrors
     */
    public void clearLoginErrors(Map<String, Object> session) {
        if (session.containsKey(LOGIN_ERRORS)) {
            ((LinkedList<String>) session.get(LOGIN_ERRORS)).clear();
            session.remove(LOGIN_ERRORS);
        }
    }

    /**
     * @see loginService#loadPrivileges(SiteUser)
     */
    //TODO: Move hibernate query to DAO. 
    //Sonarcube - Provide the parametrized type for this generic. 
    //Sonarcube - Remove this use of "Query"; it is deprecated. 
    public HashSet<String> loadPrivileges(SiteUser user) {
		Session dbSession = null; 
                SessionFactory sessionFactory = getSessionFactory();
                if (sessionFactory != null) {
                    dbSession = sessionFactory.getCurrentSession();
                } else {
                    throw new IllegalStateException("session factory is null in loadPrivileges()");
                }
		HashSet<String> loadedPrivileges = new HashSet<>();
		DerivedRole derivedRole;
                Query query;
		String queryString;
		
		// Get basic privileges requiring no secondary user role.
		queryString = "select derivedRole "
		            + "from DerivedRole as derivedRole "
		            + "join derivedRole.primaryUserRole as primaryUserRole "
		            + "join fetch derivedRole.privileges "
		            + "where primaryUserRole.id = :primaryUserRoleId "
		            + "and derivedRole.secondaryUserRole is null";
		query = dbSession.createQuery(queryString)
		        .setParameter("primaryUserRoleId", user.getPrimaryUserRole().getId());
		derivedRole = (DerivedRole) query.uniqueResult();
		if (derivedRole != null)
		{
			for (Privilege privilege : derivedRole.getPrivileges())
			{
				loadedPrivileges.add(privilege.getPrivilegeType());
			}
		}
		
		// Get privileges granted by secondary user roles.
		for (SiteUserSecondaryUserRole siteUserSecondaryUserRole : user.getSiteUserSecondaryUserRoles())
		{
			SecondaryUserRole secondaryUserRole = siteUserSecondaryUserRole.getSecondaryUserRole();
			
			queryString = "select derivedRole "
			            + "from DerivedRole as derivedRole "
			            + "join derivedRole.primaryUserRole as primaryUserRole "
			            + "join derivedRole.secondaryUserRole as secondaryUserRole "
			            + "join fetch derivedRole.privileges "
			            + "where primaryUserRole.id = :primaryUserRoleId "
			            + "and secondaryUserRole.id = :secondaryUserRoleId";
			query = dbSession.createQuery(queryString)
			        .setParameter("primaryUserRoleId", user.getPrimaryUserRole().getId())
			        .setParameter("secondaryUserRoleId", secondaryUserRole.getId());
			derivedRole = (DerivedRole) query.uniqueResult();
			if (derivedRole != null)
			{
				for (Privilege privilege : derivedRole.getPrivileges())
				{
					loadedPrivileges.add(privilege.getPrivilegeType());
				}
			}
		}
		
		user.setPrivileges(loadedPrivileges);
		return loadedPrivileges;
    }
    
    /**
     * @return the digester
     */
    public StandardStringDigester getDigester() {
        return digester;
    }

    /**
     * @param digester
     *           the digester to set
     */
    public void setDigester(StandardStringDigester digester) {
        this.digester = digester;
    }

    /**
     * @see loginService#getLoginErrors
     */
    public LinkedList<String> getLoginErrors(Map<String, Object> session) {
        return (LinkedList<String>) session.get(LOGIN_ERRORS);
    }

    /**
     * Set login user.
     * 
     * @param Map session
     * @param SiteUser siteUser
     */
    private void setLoggedInUser(Map<String, Object> session, SiteUser siteUser) {
        if (siteUser == null) {
            session.remove("loggedIn");
            session.remove("siteUser");
        } else {
            session.put("loggedIn", "true");
            loadPrivileges(siteUser);
            session.put("siteUser", siteUser);
        }
    }
        
    /**
     * Add error that user entered invalid username or password.
     * 
     * @param loginErrors
     *           current list of login errors
     * @return whatever LinkedList#add(Object) returns
     * @see LinkedList#add(Object)
     */
    private boolean signalBadAttempt(LinkedList<String> loginErrors) {
        if (loginErrors == null) {
            return false;
        }
            log.info("Your login attempt was not successful. ");
            return loginErrors.add("Your login attempt was not successful. Please try again.");
    }

    /**
     * Add error that user is locked temporarily.
     * 
     * @param loginErrors
     *           current list of login errors
     * @return whatever LinkedList#add(Object) returns
     * @see LinkedList#add(Object)
     */
    private boolean signalTimeLocked(LinkedList<String> loginErrors) {
        if (loginErrors == null) {
            return false;
        }
        //04/06/17 -Task144 -Based on Miguel's feedback to change messages
        log.info("Too many invalid login attempts. The account is locked temporarily.");
        return loginErrors.add("Too many invalid login attempts. The account is locked temporarily. Please login after 30 minutes.");
    }
	
    /**
     * Added a new method to include error message if user is inactive for more than 240 days
     * 04/06/17 -Task144 -Based on Miguel's feedback to change messages
     * @param loginErrors
     * @return
     */
    private boolean signalInactiveUserPermaLocked(LinkedList<String> loginErrors) {
        if (loginErrors == null) {
            return false;
        }
        log.info("This account has been disabled due to inactivity.");
        return loginErrors.add ("This account has been disabled due to inactivity. To enable access to your account, please contact NYTDHelp@acf.hhs.gov.");
    }

    /**
     * Add error that user is permanently locked.
     * 
     * @param loginErrors
     *           current list of login errors
     * @return whatever LinkedList#add(Object) returns
     * @see LinkedList#add(Object)
     */
    private boolean signalPermaLocked(LinkedList<String> loginErrors) {
        if (loginErrors == null) {
            return false;
        }
        log.info("This account has been locked permanently.");
        return loginErrors.add("This account has been locked permanently. To unlock this account, please contact NYTDHelp@acf.hhs.gov.");
    }

}
