/**
 * Filename: UserServiceImpl.java
 *
 * Copyright 2009, ICF International Created: May 30, 2009 Author: 18816
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import gov.hhs.acf.cb.nytd.util.ValidationException;
import gov.hhs.acf.cb.nytd.util.ValidationResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jasypt.digest.StandardStringDigester;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Implements UserService.
 * 
 * @author Adam Russell (18816)
 * @author Satinder Gill (18922)
 * @see UserService
 */
@Transactional
public class UserServiceImpl extends BaseServiceImpl implements UserService
{
	private StandardStringDigester digester;
	private Random randomNumberGenerator = new Random();
	private String rootURL;
	
	private static long INACTIVE_DAYS_THRESHOLD = 240;
	
	@Setter @Getter  MessageService messageService;
	@Setter @Getter private String inbox_nytd;
	@Setter @Getter private String um_state_sysadmin_email;

	@Override
	public final boolean checkPassword(final String username, final String password)
	{
		log.info("Check Password");
		Session session = getSessionFactory().getCurrentSession();
		SiteUser siteUser = null;
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("userName"), username)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
		
		try {
			if(!q.getResultList().isEmpty()) {
				siteUser = (SiteUser) q.getSingleResult();
			}			
		} catch (Exception e) {
			log.warn("Inside exception of checkPassword");
			e.printStackTrace();
		}
		
		if (siteUser != null)
		{
			if (!siteUser.isLocked())
			{
				log.info("Site user: "+ siteUser.getUserName()+" is not locked");
				if (!siteUser.isTemporarilyLocked())
				{
					log.info("Site user: "+ siteUser.getUserName()+" is not temporary locked");
					if (digester.matches(password, siteUser.getPassword()))
					{
						log.info("Site user: "+ siteUser.getUserName()+" password matched");
						log.info("User PWCHANGEKEY: " + siteUser.getPwChangeKey());
						log.info("User PWCHANGEDATE: " + DateUtil.toDateString(siteUser.getPwChangedDate()));
						log.info("User PWTEMPORARY: " + siteUser.getPwTemporary());
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean passwordIsStrong (String password) {
		/*
		(?=.*[0-9])       # a digit must occur at least once
		(?=.*[a-z])       # a lower case letter must occur at least once
		(?=.*[A-Z])       # an upper case letter must occur at least once
		(?=.*[@#$%^&+=!\"'(),-./:;<>?\\Q[]\\E_`{|}~])  # a special character must occur at least once
				without escapes: !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ 
		(?=\S+$)          # no whitespace allowed in the entire string
		.{8,}             # anything, at least eight places though
		*/
		if (password == null || password.isEmpty()) return false;
		
		Pattern validPwd = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\"'(),-./:;<>?\\Q[]\\E_`{|}~])(?=\\S+$).{8,}$");
		Matcher matcher = validPwd.matcher(password);
		return matcher.matches();
	}
	
	@Override
	public boolean passwordNotChanged (SiteUser siteUser, String password) {
		return digester.matches(password, siteUser.getPassword());
	}
	

	private void setLoggedInUser(Map<String, Object> session, SiteUser siteUser)
	{
		if (siteUser == null)
		{
			session.remove("loggedIn");
			session.remove("siteUser");
		}
		else
		{
			
			session.put("loggedIn", "true");
			loadPrivileges(siteUser, session);
			session.put("siteUser", siteUser);
		
		}
	}

	private SiteUser getLoggedInUser(Map<String, Object> session)
	{
		return (SiteUser) session.get("siteUser");
	}

	/**
	 * @see UserService#saveUser(String, String, String, String, String, String, String, String)
	 */
	@Override
	public boolean saveUser(Map<String, Object> session, String username, String password, String firstname, String lastname, String email,
			String phone, String primaryUserRoleName, String regionName, String stateName, List<String> states,
			String[] secondaryUserRoleNames, boolean locked, boolean receiveEmailNotifications, Long userId)
	{

		log.debug("UserServiceImpl.saveUser");
		log.debug("\tusername = " + username);
		log.debug("\tpassword = " + password);
		log.debug("\tfirstname = " + firstname);
		log.debug("\tlastname = " + lastname);
		log.debug("\temail = " + email);
		log.debug("\tphone = " + phone);
		log.debug("\tprimaryUserRoleName = " + primaryUserRoleName);
		log.debug("\tregionName = " + regionName);
		log.debug("\tstateName = " + stateName);
		log.debug("\tsecondaryUserRoleNames = " + Arrays.toString(secondaryUserRoleNames));
		log.debug("\tlocked = " + locked);
		log.debug("\treceiveEmailNotifications = " + receiveEmailNotifications);
		// TODO: Find and remove AuthLog and FakeUser entries.
        log.info("saving username: "+username);
		Session dbSession = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("id"), userId)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = dbSession.createQuery(criteriaQuery);	
		SiteUser siteUser = null;

		try {
			if(!q.getResultList().isEmpty()) {
				siteUser = (SiteUser) q.getSingleResult();
			}			
		} catch (Exception e) {
			log.warn("Inside exception of saveUser");
			e.printStackTrace();
		}
		
		log.debug("\tsiteUser = " + siteUser);

		boolean result = false;
		if (siteUser != null)
		{
			siteUser.setUserName(username);
			if (!StringUtils.isEmpty(password))
			{
				String digestedPassword = digester.digest(password);
				siteUser.setPassword(digestedPassword);
				siteUser.setPwChangedDate(Calendar.getInstance());
			}
			siteUser.setFirstName(firstname);
			siteUser.setLastName(lastname);
			siteUser.setEmailAddress(email);
			siteUser.setPhoneNumber(phone);
			siteUser.setPrimaryUserRole(lookupPrimaryUserRole(primaryUserRoleName));
			siteUser.setRegion(lookupRegion(regionName));
			siteUser.setState(lookupState(stateName));
			updateSecondaryUserRoles(siteUser, secondaryUserRoleNames);
			if(primaryUserRoleName != null && primaryUserRoleName.equals("Regional Office User")) {
				updateRegionalStatesForSiteUser(siteUser, states);
			}			
			siteUser.setLocked(locked);
			siteUser.setReceiveEmailNotifications(receiveEmailNotifications);
			//adding audit information to user account table
			//siteUser.setCreatedDate(Calendar.getInstance());
			
			siteUser.setUpdateDate(Calendar.getInstance());
			

			ValidationResult validationResult = siteUser.validate();
			log.debug(validationResult.buildDetailMessage());
			if (validationResult.isFatal())
			{
				throw new ValidationException(validationResult, "Cannot save changes to SiteUser");
			}

			dbSession.saveOrUpdate(siteUser);
			SiteUser loggedInUser = getLoggedInUser(session);
			//siteUser.setCreatedBy(loggedInUser.getUserName());
			siteUser.setUpdateBy(loggedInUser.getUserName());
			String loggedInUserName = (loggedInUser == null) ? null : loggedInUser.getUserName();
			if (siteUser.getUserName().equals(loggedInUserName)) {
				setLoggedInUser(session, siteUser);
			}
			log.info("username: "+ username + " saved successfully" );
			result = true;
		}
		return result;
	}

	@Override
	public boolean saveLoggedInUser(Map<String, Object> session, String password, String firstname,
			String lastname, String email, String phone, boolean receiveEmailNotifications)
	{
		log.info("saving logged in user with firstname ad lastname: "+firstname+" "+ lastname);
		log.debug("UserServiceImpl.saveLoggedInUser");
		log.debug("\tpassword = " + password);
		log.debug("\tfirstname = " + firstname);
		log.debug("\tlastname = " + lastname);
		log.debug("\temail = " + email);
		log.debug("\treceiveEmailNotifications = " + receiveEmailNotifications);
		log.debug("\tphone = " + phone);

		Session dbSession = getSessionFactory().getCurrentSession();
		boolean result = false;
		SiteUser loggedInUser = getLoggedInUser(session);
		if (loggedInUser != null)
		{
			CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
			CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
			Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
			criteriaQuery.where(
					criteriaBuilder.equal(root.get("userName"), loggedInUser.getUserName())
			);
			criteriaQuery.select(root);
			TypedQuery<SiteUser> q = dbSession.createQuery(criteriaQuery);	
			SiteUser siteUser = null;

			try {
				if(!q.getResultList().isEmpty()) {
					siteUser = (SiteUser) q.getSingleResult();
				}			
			} catch (Exception e) {
				log.warn("Inside exception of saveLoggedInUser");
				e.printStackTrace();
			}
			
			log.debug("\tsiteUser = " + siteUser);

			if (siteUser != null)
			{
				if (!StringUtils.isEmpty(password))
				{
					String digestedPassword = digester.digest(password);
					siteUser.setPassword(digestedPassword);
					siteUser.setPwChangedDate(Calendar.getInstance());
				}
				siteUser.setFirstName(firstname);
				siteUser.setLastName(lastname);
				siteUser.setEmailAddress(email);
				siteUser.setPhoneNumber(phone);
				siteUser.setReceiveEmailNotifications(receiveEmailNotifications);
				siteUser.setUpdateDate(Calendar.getInstance());
				siteUser.setUpdateBy(loggedInUser.getUserName());

				ValidationResult validationResult = siteUser.validate();
				log.debug(validationResult.buildDetailMessage());
				if (validationResult.isFatal())
				{
					throw new ValidationException(validationResult, "Cannot save changes to SiteUser");
				}

				dbSession.saveOrUpdate(siteUser);
				setLoggedInUser(session, siteUser);
				log.info("username: "+ firstname+" "+lastname + " saved successfully" );
				result = true;
			}
		}
		return result;
	}

	@Override
	public boolean removeUser(String username)
	{
		log.debug("UserServiceImpl.removeUser");
		log.debug("\tusername = " + username);
		// TODO: Find and remove AuthLog and FakeUser entries.

		Session session = getSessionFactory().getCurrentSession();
		boolean result = false;
		Query query = session.createQuery("select count(*) from Transmission as tr inner join tr.siteUser as su where tr.submissionStatus in ('Active', 'Inactive') and su.userName = :userName");
		 query.setString("userName", username);
		int trnsCount =  (new Long(query.uniqueResult().toString())).intValue();
		query = session.createQuery("select count(*) from MessageRecipient as tr inner join tr.siteUser as su where su.userName = :userName");
		 query.setString("userName", username);
		int msgCount =  (new Long(query.uniqueResult().toString())).intValue();
	//	System.out.println("Received Messages Count: "+msgCount);
		
		if(trnsCount == 0 && msgCount == 0)
		{
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
			Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
			criteriaQuery.where(
					criteriaBuilder.equal(root.get("userName"), username),
					criteriaBuilder.equal(root.get("isDeleted"), false)
			);
			criteriaQuery.select(root);
			TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
			SiteUser siteUser = null;
			try {
				if(!q.getResultList().isEmpty()) {
					siteUser = (SiteUser) q.getSingleResult();
				}			
			} catch (Exception e) {
				log.warn("Inside exception of removeUser");
				e.printStackTrace();
			}
			log.debug("\tsiteUser = " + siteUser);
			
			if (siteUser != null)
			{
				session.delete(siteUser);
				emailUserAcctChange(MessageService.USER_DELETED,siteUser);
				result = true;
			}
		}
		else
		{
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
			Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
			criteriaQuery.where(
					criteriaBuilder.equal(root.get("userName"), username),
					criteriaBuilder.equal(root.get("isDeleted"), false)
			);
			criteriaQuery.select(root);
			TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
			SiteUser siteUser = null;
			try {
				if(!q.getResultList().isEmpty()) {
					siteUser = (SiteUser) q.getSingleResult();
				}			
			} catch (Exception e) {
				log.warn("Inside exception of removeUser");
				e.printStackTrace();
			}
			log.debug("\tsiteUser = " + siteUser);
	
			
			if (siteUser != null)
			{
				siteUser.setDeleted(true);
				session.update(siteUser);
				emailUserAcctChange(MessageService.USER_DELETED,siteUser);
				result = true;
			}
	
		}
		return result;
	}

	/**
	 * @see UserService#addUser(String, String, String, String, String, String, String, String)
	 */
	@Override
	public boolean addUser(String username, String password, String firstname, String lastname, String email,
			String phone, String primaryUserRoleName, String regionName, String stateName, List<String> states,
			String[] secondaryUserRoleNames, boolean locked, Map<String, Object> session)
	{
		log.debug("UserServiceImpl.addUser");
		log.debug("\tusername = " + username);

		// TODO: Find and remove AuthLog and FakeUser entries.

		Session dbSession = getSessionFactory().getCurrentSession();	
		CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("userName"), username)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = dbSession.createQuery(criteriaQuery);
		SiteUser siteUser = null;
		try {
			siteUser = (SiteUser) q.getSingleResult();
		} catch (NoResultException e) {
		    log.debug("No result forund for... ");
		}
		log.debug("\tsiteUser = " + siteUser);
		SiteUser loggedInUser = getLoggedInUser(session);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -3);
		Date threeYearsAgo = cal.getTime();
		
		boolean result = false;
		if (siteUser == null || (siteUser.getIsDeleted() && siteUser.getUpdateDate().getTime().before(threeYearsAgo)))
		{
			siteUser = new SiteUser();

			String digestedPassword = digester.digest(password);

			siteUser.setUserName(username);
			siteUser.setPassword(digestedPassword);
			siteUser.setPwChangedDate(Calendar.getInstance());
			siteUser.setPwTemporary(true);
			siteUser.setFirstName(firstname);
			siteUser.setLastName(lastname);
			siteUser.setEmailAddress(email);
			siteUser.setPhoneNumber(phone);
			siteUser.setPrimaryUserRole(lookupPrimaryUserRole(primaryUserRoleName));
			siteUser.setRegion(lookupRegion(regionName));
			siteUser.setState(lookupState(stateName));
			updateSecondaryUserRoles(siteUser, secondaryUserRoleNames);
			if(primaryUserRoleName != null && primaryUserRoleName.equals("Regional Office User")) {
				updateRegionalStatesForSiteUser(siteUser, states);
			}
			siteUser.setLocked(locked);
			siteUser.setCreatedDate(Calendar.getInstance());
			siteUser.setCreatedBy(loggedInUser.getUserName());
			siteUser.setUpdateDate(Calendar.getInstance());
			siteUser.setUpdateBy(loggedInUser.getUserName());
			ValidationResult validationResult = siteUser.validate();
			log.debug(validationResult.buildDetailMessage());
			if (validationResult.isFatal())
			{
				throw new ValidationException(validationResult, "Cannot add new SiteUser");
			}
			dbSession.saveOrUpdate(siteUser);
			emailUserAcctChange(MessageService.USER_ADDED,siteUser);
			result = true;
		}

		return result;
	}

	private void updateRegionalStatesForSiteUser(SiteUser siteUser, List<String> states) {
		Set<SiteUserStateRegionMapping> existingStateRegionMappings = new HashSet<SiteUserStateRegionMapping>(siteUser.getSiteUserStateRegionMappings());
		Set<String> updatedStates = (states == null) ? new HashSet<String>() : new HashSet<String>(states);
		for (SiteUserStateRegionMapping existingStateRegionMapping : existingStateRegionMappings)
		{
			if(existingStateRegionMapping.getState() != null)
			{
				if (updatedStates.remove(existingStateRegionMapping.getState().getStateName())) {
					// already have this state for given region, so keep it
				} else {
					siteUser.removeSiteUserStateRegionMapping(existingStateRegionMapping);
				}
			}
		}
		// these are all new states for the regional user
		if(states != null) {
			for (String state : states)
			{
				SiteUserStateRegionMapping siteUserStateRegionMapping = new SiteUserStateRegionMapping();
				siteUserStateRegionMapping.setState(lookupState(state));			
				siteUser.addSiteUserStateRegionMapping(siteUserStateRegionMapping);;
			}
		}		
	}

	private void emailUserAcctChange(String typeStr, SiteUser user)
	{
		Map<String, Object> namedParams = new HashMap<String, Object>();
		String emailIds = null;
		namedParams.put("username", user.getUserName());
		namedParams.put("firstname",user.getFirstName() != null ? user.getFirstName() : "" );
		namedParams.put("lastname", user.getLastName() != null ? user.getLastName() : "" );
		namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
		Message systemMsg = messageService.createSystemMessage(typeStr,
				namedParams);
		List<String> emailAddresses = new ArrayList<String>();
		if(user.getPrimaryUserRole().getName()!= null && (user.getPrimaryUserRole().getName().equalsIgnoreCase("System Administrator") || user.getPrimaryUserRole().getName().equalsIgnoreCase("State User")))
			emailIds = getUm_state_sysadmin_email();
		else
			emailIds = getInbox_nytd();
		if(emailIds != null)
		{
			for(String address : emailIds.split(","))
				emailAddresses.add(address);
			messageService.sendEmailNotification(systemMsg, emailAddresses);
		}
	}
	
	private void updateSecondaryUserRoles(SiteUser siteUser, String[] secondaryUserRoleNames)
	{
		Set<SiteUserSecondaryUserRole> existingRoles = new HashSet<SiteUserSecondaryUserRole>(siteUser.getSiteUserSecondaryUserRoles());
		Set<String> updatedRoleNames = (secondaryUserRoleNames == null) ? new HashSet<String>() : new HashSet<String>(Arrays.asList(secondaryUserRoleNames));
		for (SiteUserSecondaryUserRole existingRole : existingRoles)
		{
			if(existingRole.getSecondaryUserRole() != null)
			{
				if (updatedRoleNames.remove(existingRole.getSecondaryUserRole().getName())) {
					// already have this role, so keep it
				} else {
					siteUser.removeSiteUserSecondaryUserRole(existingRole);
				}
			}
		}
		// these are all new roles for the user
		for (String secondaryUserRoleName : updatedRoleNames)
		{
			SiteUserSecondaryUserRole siteUserSecondaryUserRole = new SiteUserSecondaryUserRole();
			siteUserSecondaryUserRole.setSecondaryUserRole(lookupSecondaryUserRole(secondaryUserRoleName));
			siteUser.addSiteUserSecondaryUserRole(siteUserSecondaryUserRole);
		}
	}

	@Override
	public PrimaryUserRole lookupPrimaryUserRole(String primaryUserRoleName)
	{
		// TODO: Redo this. No reason to get all objects from database and filter using a for-loop.
		if (primaryUserRoleName != null)
		{
			List<PrimaryUserRole> primaryUserRoleList = getTypeOfUserList(true);
			for (PrimaryUserRole primaryUserRole : primaryUserRoleList)
			{
				if (primaryUserRoleName.equals(primaryUserRole.getName()))
				{
					return primaryUserRole;
				}
			}
		}
		return null;
	}

	@Override
	public Region lookupRegion(String regionName)
	{
		// TODO: Redo this. No reason to get all objects from database and filter using a for-loop.
		if (regionName != null)
		{
			List<Region> regionList = getRegionList();
			for (Region region : regionList)
			{
				if (regionName.equals(region.getRegion()))
				{
					return region;
				}
			}
		}
		return null;
	}

	@Override
	public State lookupState(String stateName)
	{
		// TODO: Redo this. No reason to get all states from database and filter using a for-loop.
		if (stateName != null)
		{
			List<State> stateList = getStateList();
			for (State state : stateList)
			{
				if (stateName.equals(state.getStateName()))
				{
					return state;
				}
			}
		}
		return null;
	}

	@Override
	public SecondaryUserRole lookupSecondaryUserRole(String secondaryUserRoleName)
	{
		// TODO: Redo this. No reason to get all objects from database and filter using a for-loop.
		if (secondaryUserRoleName != null)
		{
			List<SecondaryUserRole> secondaryUserRoleList = getSecondaryUserRoleList();
			for (SecondaryUserRole secondaryUserRole : secondaryUserRoleList)
			{
				if (secondaryUserRoleName.equals(secondaryUserRole.getName()))
				{
					return secondaryUserRole;
				}
			}
		}
		return null;
	}

	/**
	 * @see UserService#saveNewPasswordForUser(SiteUser, String)
	 */
	public void saveNewPasswordForUser(SiteUser siteUser, String password)
	{
		Session session = getSessionFactory().getCurrentSession();
		String digestedPassword;
		digestedPassword = digester.digest(password);
		siteUser.setPassword(digestedPassword);
		siteUser.setPwChangedDate(Calendar.getInstance());
		siteUser.setPwChangeKey(null);
		siteUser.setPwTemporary(false);
		session.saveOrUpdate(siteUser);
	}

	/**
	 * @see UserService#getUserWithUserId(Long)
	 */
	@Override
	public final SiteUser getUserWithUserId(final Long usrId) throws HibernateException
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("id"), usrId)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
		SiteUser siteUser = null;	
		try {
			if(!q.getResultList().isEmpty()) {
				siteUser = (SiteUser) q.getSingleResult();
			}			
		} catch (Exception e) {
			log.warn("Inside exception of getUserWithUserId");
			e.printStackTrace();
		}		
		return siteUser;
	}

	/**
	 * @see UserService#getUserWithUserName(String)
	 */
	@Override
	public final SiteUser getUserWithUserName(final String userName) throws HibernateException
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("userName"), userName)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
		SiteUser siteUser = (SiteUser) q.getSingleResult();
		return siteUser;
	}
	
	/**
	 * @see UserService#getActiveUserWithUserName(String)
	 */
	@Override
	public final SiteUser getActiveUserWithUserName(final String userName) throws HibernateException
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		Predicate lcUsernameLikeSearchPattern = 
				criteriaBuilder.like(
						criteriaBuilder.lower(root.get("userName")), 
						userName.toLowerCase()
				);
		criteriaQuery.where(
				lcUsernameLikeSearchPattern,
				criteriaBuilder.equal(root.get("isDeleted"), false)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
		SiteUser siteUser = (SiteUser) q.getSingleResult();
		return siteUser;
	}

	/**
	 * @see UserService#getUserWithPasswordChangeKey(String)
	 */
	public SiteUser getUserWithPasswordChangeKey(String key)
	{
		Session session = getSessionFactory().getCurrentSession();
    	CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("pwChangeKey"), key)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
		SiteUser siteUser = q.getSingleResult();
		
		return siteUser;
	}

	/**
	 * @see UserService#processPasswordChangeKey(SiteUser, ServletContext)
	 */
	public String processPasswordChangeKey(SiteUser siteUser, final HttpServletRequest request)
			throws MalformedURLException
	{
		Session session = getSessionFactory().getCurrentSession();
	
		String path = "/changePasswordPage.action";
		/*String contextURL = new URL(request.getProtocol().replaceAll("[^a-zA-Z]", ""), request.getServerName(),
				request.getServerPort(), request.getContextPath()).toString();*/
	
		String contextURL = new URL(getRootURL()).toString().trim();
		String pwChangeKey;
		
		List<SiteUser> siteUsersWithSameKey;
		do
		{
			pwChangeKey = generateKey();
	    	CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
			Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
			criteriaQuery.where(
					criteriaBuilder.equal(root.get("pwChangeKey"), pwChangeKey)
			);
			criteriaQuery.select(root);
			TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
			siteUsersWithSameKey = q.getResultList();	
		}
		while (!siteUsersWithSameKey.isEmpty());

		siteUser.setPwChangeKey(pwChangeKey);
		session.saveOrUpdate(siteUser);

		path = contextURL + path + "?key=" + pwChangeKey;

		return path;
	}

	/**
	 * @see UserService#processPasswordChangeKey(String, ServletContext)
	 */
	public String processPasswordChangeKey(final String emailAddress, final HttpServletRequest request)
			throws HibernateException, MalformedURLException
	{
		Session session = getSessionFactory().getCurrentSession();
    	CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
		Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("emailAddress"), emailAddress)
		);
		criteriaQuery.select(root);
		TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);	
		SiteUser siteUser = q.getSingleResult();
		
		if (siteUser == null)
		{
			// TODO: Throw an error here.
			return null;
		}

		return processPasswordChangeKey(siteUser, request);
	}

	/**
	 * @see UserService#getTypeOfUserList()
	 */
	public List<PrimaryUserRole> getTypeOfUserList(boolean includeSysAdmin)
	{
		Session session = getSessionFactory().getCurrentSession();
		List<PrimaryUserRole> primaryUserRoleList;
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<PrimaryUserRole> criteriaQuery = criteriaBuilder.createQuery(PrimaryUserRole.class);
		Root<PrimaryUserRole> root = criteriaQuery.from(PrimaryUserRole.class);
		if (!includeSysAdmin) {
			criteriaQuery.where(
				criteriaBuilder.notEqual(root.get("name"), Constants.SYSTEMADMIN)
			);
		}
		criteriaQuery.select(root);
		TypedQuery<PrimaryUserRole> q = session.createQuery(criteriaQuery);	
		primaryUserRoleList = q.getResultList();

		return primaryUserRoleList;
	}

	/**
	 * @see UserService#getRegionList()
	 */
	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	public List<Region> getRegionList()
	{
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Region.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<Region> regionList;

		regionList = criteria.list();

		return regionList;
	}

	public List<State> getStateList()
	{
		Session session = getSessionFactory().getCurrentSession();	
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<State> criteriaQuery = criteriaBuilder.createQuery(State.class);
		Root<State> root = criteriaQuery.from(State.class);
		criteriaQuery.select(root);
		TypedQuery<State> q = session.createQuery(criteriaQuery);	
		List<State> stateList = q.getResultList();
		return stateList;
	}

	/**
	 * Get states for a given region	
	 * @param regionId
	 * @return
	 */
	
	public List<State> getStateListForRegion(Long regionId)
	{
		List<State> statesInRegion = new ArrayList<State>();
		for(State state:getStateList()) {
			if(regionId == Long.getLong(state.getRegion().getRegionCode())){
				statesInRegion.add(state);
			}
		}
		return statesInRegion;
	}

	/**
	 * Get states for a given region	
	 * @param regionName
	 * @return
	 */
	
	public Map<String, String> getStateMapForRegionName(String regionName)
	{
		Map<String, String> statesInRegion = new TreeMap<String, String>();
		for(State state:getStateList()) {
			if(regionName.equals(state.getRegion().getRegion())){
				statesInRegion.put(state.getStateName(), state.getStateName());
			}	
		}
		return statesInRegion;
	}
	
	/**
	 * @see UserService#getSecondaryUserRoleList()
	 */
	public List<SecondaryUserRole> getSecondaryUserRoleList()
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<SecondaryUserRole> criteriaQuery = criteriaBuilder.createQuery(SecondaryUserRole.class);
		Root<SecondaryUserRole> root = criteriaQuery.from(SecondaryUserRole.class);
		criteriaQuery.select(root);
		TypedQuery<SecondaryUserRole> q = session.createQuery(criteriaQuery);
		
		return q.getResultList();
	}

	/**
	 * @see UserService#getUserSearchResultList(String, String, String, String)
	 */
	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29		
	@SuppressWarnings("deprecation")
	public List<SiteUser> getUserSearchResultList(String username, String firstName, String lastName,
			String email, String[] selectedPrimaryRoles, String selectedRegion, String selectedState,
			String[] selectedSecondaryRoles, String columnSelected, 
			boolean orderByDescending, boolean excludeTestUsers)
	{
		log.debug("UserServiceImpl.getUserSearchResultList");
		log.debug("\tusername = " + username);
		log.debug("\tfirstName = " + firstName);
		log.debug("\tlastName = " + lastName);
		log.debug("\temail = " + email);
		log.debug("\tselectedPrimaryRoles = " + Arrays.toString(selectedPrimaryRoles));
		log.debug("\tselectedRegion = " + selectedRegion);
		log.debug("\tselectedState = " + selectedState);
		log.debug("\tselectedSecondaryRoles = " + Arrays.toString(selectedSecondaryRoles));
		log.debug("\texcludeTestUsers = " + excludeTestUsers);
		
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		Session session = getSessionFactory().getCurrentSession();
		List<SiteUser> siteUserList;
		
		Criteria criteria = session.createCriteria(SiteUser.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		int numCriteria = appendLike(criteria, username, "userName");
		if (excludeTestUsers) {
			appendNotLike(criteria, "test", "userName");
		}
		numCriteria += appendLike(criteria, firstName, "firstName");
		numCriteria += appendLike(criteria, lastName, "lastName");
		numCriteria += appendLike(criteria, email, "emailAddress");
		numCriteria += appendIn(criteria, selectedPrimaryRoles, "primaryUserRole", "name");
		if ((selectedSecondaryRoles != null) && (selectedSecondaryRoles.length > 0)) {
			Criteria susurCriteria = criteria.createCriteria("siteUserSecondaryUserRoles");
			numCriteria += appendIn(susurCriteria, selectedSecondaryRoles, "secondaryUserRole", "name");
		}
		numCriteria += appendEquals(criteria, selectedRegion, "region", "region");
		numCriteria += appendEquals(criteria, selectedState, "state", "stateName");

		log.debug("criteria = " + criteria);
		if(columnSelected != null && !columnSelected.isEmpty())
		{
			log.debug("Inside UserServiceImpl.getUserSearchResultList(), columnSelected:"+columnSelected);
			log.debug("Inside UserServiceImpl.getUserSearchResultList(), orderByDescending:"+orderByDescending);
			
			if (columnSelected.equalsIgnoreCase("userName"))
			{
				if (orderByDescending)
					criteria.addOrder(Order.desc("userName").ignoreCase());
				else criteria.addOrder(Order.asc("userName").ignoreCase());
			}
			else if (columnSelected.equalsIgnoreCase("firstName"))
			{
				if (orderByDescending)
					criteria.addOrder(Order.desc("firstName").ignoreCase());
				else criteria.addOrder(Order.asc("firstName").ignoreCase());
			}else if (columnSelected.equalsIgnoreCase("lastName"))
			{
				if (orderByDescending)
					criteria.addOrder(Order.desc("lastName").ignoreCase());
				else criteria.addOrder(Order.asc("lastName").ignoreCase());
			}else if (columnSelected.equalsIgnoreCase("emailAddress"))
			{
				if (orderByDescending)
					criteria.addOrder(Order.desc("emailAddress").ignoreCase());
				else criteria.addOrder(Order.asc("emailAddress").ignoreCase());
			}else if (columnSelected.equalsIgnoreCase("phoneNumber"))
			{
				if (orderByDescending)
					criteria.addOrder(Order.desc("phoneNumber").ignoreCase());
				else criteria.addOrder(Order.asc("phoneNumber").ignoreCase());
			}
			else if (columnSelected.equalsIgnoreCase("state"))
			{
				if (orderByDescending)
					criteria.addOrder(Order.desc("state").ignoreCase());
				else criteria.addOrder(Order.asc("state").ignoreCase());
			}else if (columnSelected.equalsIgnoreCase("role"))
			{
				Criteria detCri = criteria.createCriteria("siteUserSecondaryUserRoles");
				detCri.createAlias("secondaryUserRole", "secondaryUserRole_");
					
				if (orderByDescending)
					criteria.addOrder(Order.desc("secondaryUserRole_.name").ignoreCase());
				else criteria.addOrder(Order.asc("secondaryUserRole_.name").ignoreCase());
			}
			else if (columnSelected.equalsIgnoreCase("type"))
			{
				//criteria.createAlias("primaryUserRole", "primaryUserRole_");
				
				if (orderByDescending)
					criteria.addOrder(Order.desc("primaryUserRole").ignoreCase());
				else criteria.addOrder(Order.asc("primaryUserRole").ignoreCase());
			}
			numCriteria += 1;
		
		}
			
		criteria.add(Restrictions.eq("isDeleted", false));
		siteUserList = criteria.list();

		log.debug("siteUserList size is: " + siteUserList.size());
		log.debug("numCriteria is: " + numCriteria);

		return siteUserList;
	}

	/*
	 * if properties has one value it is a simple property name, if two, then it
	 * is a subproperty that must be aliased
	 */
	private String createPropertyAliasedIfNeeded(Criteria criteria, boolean haveValue,
												 String... properties)
	{
		String property = null;
		if (haveValue && (properties.length > 0))
		{
			if (properties.length > 1)
			{
				String alias = properties[0] + "_";
				criteria.createAlias(properties[0], alias);
				property = alias + "." + properties[1];
			}
			else
			{
				property = properties[0];
			}
		}
		return property;
	}

	private int appendLike(Criteria criteria, String value, String... properties)
	{
		int result = 0;
		boolean haveValue = !StringUtils.isEmpty(value);
		String property = createPropertyAliasedIfNeeded(criteria, haveValue, properties);
		if (haveValue && (property != null))
		{
			criteria.add(Restrictions.like(property, value + "%").ignoreCase()); // case
			// insensitive
			// search
			result += 1;
		}
		return result;
	}

	private int appendNotLike(Criteria criteria, String value, String... properties)
	{
		int result = 0;
		boolean haveValue = !StringUtils.isEmpty(value);
		String property = createPropertyAliasedIfNeeded(criteria, haveValue, properties);
		if (haveValue && (property != null))
		{
			criteria.add(Restrictions.not(Restrictions.like(property, "%" + value + "%").ignoreCase())); // case
			// insensitive
			// search
			result += 1;
		}
		return result;
	}

	private int appendEquals(Criteria criteria, String value, String... properties)
	{
		int result = 0;
		boolean haveValue = !StringUtils.isEmpty(value);
		String property = createPropertyAliasedIfNeeded(criteria, haveValue, properties);
		if (haveValue && (property != null))
		{
			criteria.add(Restrictions.eq(property, value).ignoreCase()); // case
			// insensitive
			// search
			result += 1;
		}
		return result;
	}

	private int appendIn(Criteria criteria, String[] values, String... properties)
	{
		int result = 0;
		boolean haveValue = (values != null) && (values.length > 0);
		String property = createPropertyAliasedIfNeeded(criteria, haveValue, properties);
		if (haveValue && (property != null))
		{
			criteria.add(Restrictions.in(property, values));
			result += 1;
		}
		return result;
	}

	/**
	 * @return the digester
	 */
	public StandardStringDigester getDigester()
	{
		return digester;
	}

	/**
	 * @param digester
	 *           the digester to set
	 */
	public void setDigester(StandardStringDigester digester)
	{
		this.digester = digester;
	}

	/**
	 * @see UserService#loadPrivileges(SiteUser, Map)
	 */
	public HashSet<String> loadPrivileges(SiteUser user, Map<String, Object> session)
	{
		Session dbSession = getSessionFactory().getCurrentSession();
		HashSet<String> loadedPrivileges = new HashSet<String>();
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
	 * Generate a random, 14 character, alphanumeric string.
	 * 
	 * @return the generated random string
	 */
	private String generateKey()
	{
		int mod = 268435455; // 0xFFFFFFF, which is 7 characters (half of 14)
		int int1 = randomNumberGenerator.nextInt() % mod;
		int int2 = randomNumberGenerator.nextInt() % mod;
		String result;
		result = Integer.toHexString(int1).toUpperCase();
		result += Integer.toHexString(int2).toUpperCase();
		result = result.substring(0, 14);
		return result;
	}

	/**
	 * @return the rootURL
	 */
	public String getRootURL()
	{
		return rootURL;
	}

	/**
	 * @param rootURL the rootURL to set
	 */
	public void setRootURL(String rootURL)
	{
		this.rootURL = rootURL;
	}

}
