package gov.hhs.acf.cb.nytd.models;

// Generated May 20, 2009 10:16:43 AM by Hibernate Tools 3.2.4.GA

import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.ValidationResult;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.*;


/*
 * SiteUser originally generated by hbm2java
 * 
 * @author 18816
 */
public class SiteUser extends PersistentObject implements Sender, Recipient
{
    
    public static final String SESSION_KEY = "siteUser";
    
	protected transient Logger log = Logger.getLogger(getClass());

    private State state;
	private Region region;
	private PrimaryUserRole primaryUserRole;
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private boolean locked = false;
	private Timestamp timeLocked;
	private String pwChangeKey;
	private boolean receiveEmailNotifications;
	private boolean isDeleted;
	private Calendar pwChangedDate;
	private boolean pwTemporary;

    private Set<Message> messages;
    private HashSet<String> privileges;
    private Set<SiteUserSecondaryUserRole> siteUserSecondaryUserRoles = new HashSet<>(0);
    private Set<SiteUserStateRegionMapping> siteUserStateRegionMappings = new HashSet<>(0);
	private boolean privilegesSet = false;
	
	//account audit information
	private Calendar createdate;
	private Calendar updateDate;
	private String createdBy;
	private String updateBy;

	public SiteUser()
	{
	}

	public SiteUser(Long siteUserid)
	{
		this.id = siteUserid;
	}

    /**
     * @return the primaryUserRole
     */
	public PrimaryUserRole getPrimaryUserRole()
	{
		return primaryUserRole;
	}

    /**
     * @param primaryUserRole the primaryUserRole to set
     */
	public void setPrimaryUserRole(PrimaryUserRole primaryUserRole)
	{
		this.primaryUserRole = primaryUserRole;
	}

    /**
     * @return the userName
     */
	public String getUserName()
	{
		return userName;
	}

    /**
     * @param userName the userName to set
     */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

    /**
     * @return the password
     */
	public String getPassword()
	{
		return password;
	}

    /**
     * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

    /**
     * @return the firstName
     */
	public String getFirstName()
	{
		return firstName;
	}

    /**
     * @param firstName the firstName to set
     */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

    /**
     * @return the lastName
     */
	public String getLastName()
	{
		return lastName;
	}

    /**
     * @param lastName the lastName to set
     */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

    /**
     * @return the emailAddress
     */
	public String getEmailAddress()
	{
		return emailAddress;
	}

    /**
     * @param emailAddress the emailAddress to set
     */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

    /**
     * @param phoneNumber the phoneNumber to set
     */
	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

    /**
     * @return the value of locked
     */
	public boolean isLocked()
	{
		return this.locked;
	}

    /**
     * @param locked the value of locked to set
     */
	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}

    /**
     * @param timeLocked the timeLocked to set
     */
	public void setTimeLocked(Timestamp timeLocked)
	{
		this.timeLocked = timeLocked;
	}

    /**
     * @return the timeLocked
     */
	public Timestamp getTimeLocked()
	{
		return timeLocked;
	}

    /**
     * Remove time lock.
     */
	public void removeTimeLock()
	{
		this.timeLocked = null;
	}

    /**
     * Clear any permanent or time locks.
     */
	public void unlock()
	{
		this.setLocked(false);
		this.removeTimeLock();
	}

    /**
     * Determine whether the SiteUser is time locked.
     * <p/>
     * The SiteUser is time locked if the value of timeLocked is a time within the past 30 minutes.
     *
     * @return true if SiteUser is time locked, false otherwise
     */
	public boolean isTemporarilyLocked()
	{
		boolean isTimeLocked = false;
		Timestamp currentTime = new Timestamp((new Date()).getTime());
		int window = 1800000; // 30 minutes

		if (this.getTimeLocked() == null)
		{
			return false;
		}

		if (currentTime.getTime() - this.getTimeLocked().getTime() <= window)
		{
			isTimeLocked = true;
		}

		return isTimeLocked;
	}

    /**
     * @return the pwChangeKey
     */
	public String getPwChangeKey()
	{
		return pwChangeKey;
	}

    /**
     * @param pwChangeKey the pwChangeKey to set
     */
	public void setPwChangeKey(String pwChangeKey)
	{
		this.pwChangeKey = pwChangeKey;
	}

	public String getSecondaryUserRoleNames()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (SiteUserSecondaryUserRole siteUserSecondaryUserRole : getSiteUserSecondaryUserRoles())
		{
			sb.append(sep);
			sb.append(siteUserSecondaryUserRole.getSecondaryUserRole().getName());
			sep = ", ";
		}
		return sb.toString();
	}
	
	public boolean isSecondaryStateRoleManager() {
		return isSecondaryRole(Constants.SUR_MANAGER);
	}

	public boolean isSecondaryStateRoleStateAuthorized() {
		return isSecondaryRole(Constants.SUR_STATE_AUTH);
	}

	public boolean isSecondaryRole(String role) {
		return getSiteUserSecondaryUserRoles()
				.stream()
				.map(SiteUserSecondaryUserRole::getSecondaryUserRole)
				.anyMatch(r -> r.getName().equals(role));
	}

	/**
	 * Convenience method to return roles directly
	 *
	 * @return
	 */
	public Set<SecondaryUserRole> getSecondaryUserRoles()
	{
		Set<SecondaryUserRole> result = new LinkedHashSet<SecondaryUserRole>();
		for (SiteUserSecondaryUserRole siteUserSecondaryUserRole : getSiteUserSecondaryUserRoles())
		{
			result.add(siteUserSecondaryUserRole.getSecondaryUserRole());
		}
		return result;
	}

	public Set<SiteUserSecondaryUserRole> getSiteUserSecondaryUserRoles()
	{
		return siteUserSecondaryUserRoles;
	}

	private void setSiteUserSecondaryUserRoles(Set<SiteUserSecondaryUserRole> siteUserSecondaryUserRoles)
	{
		this.siteUserSecondaryUserRoles = siteUserSecondaryUserRoles;
	}

	public void addSiteUserSecondaryUserRole(SiteUserSecondaryUserRole siteUserSecondaryUserRole)
	{
		siteUserSecondaryUserRoles.add(siteUserSecondaryUserRole);
		siteUserSecondaryUserRole.setSiteUser(this);
	}

	public void removeSiteUserSecondaryUserRole(SiteUserSecondaryUserRole siteUserSecondaryUserRole)
	{
		siteUserSecondaryUserRoles.remove(siteUserSecondaryUserRole);
		siteUserSecondaryUserRole.setSiteUser(null);
	}

	public Set<SiteUserStateRegionMapping> getSiteUserStateRegionMappings()
	{
		return siteUserStateRegionMappings;
	}

	private void setSiteUserStateRegionMappings(Set<SiteUserStateRegionMapping> siteUserStateRegionMappings)
	{
		this.siteUserStateRegionMappings = siteUserStateRegionMappings;
	}

	public void addSiteUserStateRegionMapping(SiteUserStateRegionMapping siteUserStateRegionMapping)
	{
		siteUserStateRegionMappings.add(siteUserStateRegionMapping);
		siteUserStateRegionMapping.setSiteUser(this);
	}

	public void removeSiteUserStateRegionMapping(SiteUserStateRegionMapping siteUserStateRegionMapping)
	{
		siteUserStateRegionMappings.remove(siteUserStateRegionMapping);
		siteUserStateRegionMapping.setSiteUser(null);
	}	
	/**
	 * @return the state
	 */
	public State getState()
	{
		return state;
	}

    /**
     * @param state the state to set
     */
	public void setState(State state)
	{
		this.state = state;
	}

    /**
     * @return the region
     */
	public Region getRegion()
	{
		return region;
	}

    /**
     * @param region the region to set
     */
	public void setRegion(Region region)
	{
		this.region = region;
	}

    /**
     * @return the receiveEmailNotifications
     */
	public boolean isReceiveEmailNotifications()
	{
		return receiveEmailNotifications;
	}

    /**
     * @param receiveEmailNotifications the receiveEmailNotifications to set
     */
	public void setReceiveEmailNotifications(boolean receiveEmailNotifications)
	{
		this.receiveEmailNotifications = receiveEmailNotifications;
	}

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public HashSet<String> getPrivileges()
	{
		if (!privilegesSet)
		{
			throw new IllegalStateException("Privileges must be set before being accessed");
		}
		// do not let actual privileges get modified
		return new HashSet<String>(privileges);
	}

	public void setPrivileges(HashSet<String> privileges)
	{
		this.privileges = privileges;
		privilegesSet = true;
	}

	public boolean hasPrivilege(String p)
	{
		return getPrivileges().contains(p);
	}

	/**
	 * @return true if this user has a secondary role of administrator
	 */
	private boolean hasAdministratorRights()
	{
		return hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS)
				|| hasPrivilege(Constants.PRIV_CAN_ADMIN_OFFICE_USERS);
	}

	private boolean isPrimaryRole(String roleName)
	{
		return (primaryUserRole != null) && roleName.equals(primaryUserRole.getName());
	}

	/**
	 * @return true if this user's primary role is SysOp
	 */
	public boolean isSystemAdminstratorUser()
	{
		return isPrimaryRole(Constants.SYSTEMADMIN);
	}

	/**
	 * @return true if this user's primary role is CB
	 */
	public boolean isCBUser()
	{
		return isPrimaryRole(Constants.CBUSER);
	}

	/**
	 * @return true if this user's primary role is Regional
	 */
	public boolean isRegionalUser()
	{
		return isPrimaryRole(Constants.REGIONALUSER);
	}

	/**
	 * @return true if this user's primary role is State
	 */
	public boolean isStateUser()
	{
		return isPrimaryRole(Constants.STATEUSER);
	}

	/**
	 * @param otherUser
	 *           to check
	 * @return true if this instance of a site user can see the data for the user
	 *         represented by otherUser
	 */
	public boolean canView(SiteUser otherUser)
	{
		boolean result = false;
		if (hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS))
		{
			result = true;
		}
		else if (isCBUser())
		{
			result = otherUser.isCBUser();
		}
		else if (isRegionalUser())
		{
			result = otherUser.isRegionalUser() && isSameRegion(region, otherUser.getRegion());
		}
		else if (isStateUser())
		{
			result = otherUser.isStateUser() && isSameState(state, otherUser.getState());
		}
		log.debug("SiteUser[" + this + "].canView(" + otherUser + ") = " + result);
		return result;
	}

	/**
	 * @param otherUser
	 *           to check
	 * @return true if this instance of a site user can edit the data for the
	 *         user represented by otherUser
	 */
	public boolean canEdit(SiteUser otherUser)
	{
		boolean result = canView(otherUser) && hasAdministratorRights();
		log.debug("SiteUser[" + this + "].canEdit(" + otherUser + ") = " + result);
		return result;
	}

	private boolean isSameRegion(Region r1, Region r2)
	{
		if (r1 == r2)
		{
			return true;
		}
		else if ((r1 == null) || (r2 == null))
		{
			return false;
		}
		else
		{
			return r1.getRegion().equals(r2.getRegion());
		}
	}

	private boolean isSameState(State s1, State s2)
	{
		if (s1 == s2)
		{
			return true;
		}
		else if ((s1 == null) || (s2 == null))
		{
			return false;
		}
		else
		{
			return s1.getStateName().equals(s2.getStateName());
		}
	}

	public ValidationResult validate()
	{
		ValidationResult result = new ValidationResult();
		String category = "SiteUser";
		result.checkForRequiredField(category, "userName", "User Id is required", userName);
		result.checkForRequiredField(category, "firstName", "First name is required", firstName);
		result.checkForRequiredField(category, "lastName", "Last name is required", lastName);
		if (isReceiveEmailNotifications()) {
			String regexEmailPattern = "^([0-9a-zA-Z]+[-._+&amp;])*[0-9a-zA-Z]+@([-0-9a-zA-Z]+[.])+[a-zA-Z]{2,6}$";
			result.checkForRegexMatch(category, "emailAddress", "A valid Email address is required when 'Receive Email Notifications' is set", emailAddress, regexEmailPattern);
		}
		result.checkForRequiredField(category, "primaryUserRole", "Primary user role is required",
				primaryUserRole);
		if (isRegionalUser())
		{
			result.checkForRequiredField(category, "region", "Region is required for regional users", region);
		}
		if (isStateUser())
		{
			result.checkForRequiredField(category, "state", "State is required for state users", state);
		}

		return result;
	}

	public String toString()
	{
		return "SiteUser: " + userName;
	}

	public boolean isDeleted() {
		return this.isDeleted;
	}
	
	public boolean getIsDeleted() {
		return this.isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public void setIsDeleted(boolean isDeleted)
	{
		this.setDeleted(isDeleted);
	}

	public Calendar getPwChangedDate() {
		return pwChangedDate;
	}

	public void setPwChangedDate(Calendar pwChangedDate) {
		this.pwChangedDate = pwChangedDate;
	}
	
	public boolean getPwTemporary() {
		return this.pwTemporary;
	}

	public void setPwTemporary(boolean pwTemporary) {
		this.pwTemporary = pwTemporary;
	}
	
	public Calendar getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Calendar createdate) {
		this.createdate = createdate;
	}

	public Calendar getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Calendar updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

}
