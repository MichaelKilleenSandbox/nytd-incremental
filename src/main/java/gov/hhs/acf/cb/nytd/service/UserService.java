/**
 * Filename: UserService.java
 *
 *  Copyright 2009, ICF International
 *  Created: May 30, 2009
 *  Author: 18816
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.*;
import org.hibernate.HibernateException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * This service handles operations on a SiteUser.
 * <p/>
 * Currently this includes operations related to both authentication and user
 * management.
 *
 * @author Adam Russell (18816)
 */
public interface UserService extends BaseService
{
	// TODO: Separate this into security and user management services, perhaps?
	
	public static String AUTHLOG_SUCCESS = "Successful login";
	public static String AUTHLOG_FAIL = "Invalid login attempt";
	public static String AUTHLOG_FAKE = "Fake user login attempt";
	
	//public static String INSUFFICIENT_PWD = "Passwords must be a minimum of 8 characters and contain: 1 uppercase and 1 lowercase letter, 1 special character, 1 number and no spaces.";
	public static String INSUFFICIENT_PWD = "Passwords must meet following criteria:<br/>" +
			"- At least 8 characters<br/>" +
			"- Contain 1 uppercase character<br/>" +
			"- Contain 1 lowercase character<br/>" +
			"- Contain 1 special character:<br/>" +
			"&nbsp; (!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~)<br/>" +
			"- Contain 1 number<br/>" +
			"- Contain no spaces";

	/**
	 * Validate username password combination
	 *
	 * @param username
	 * @param password
	 * @return true if user exists and the password is correct
	 */
	boolean checkPassword(final String username, final String password);

	/**
	 * Add a SiteUser.
	 *
	 * @param username			   User name of the SiteUser
	 * @param password			   Password of the SiteUser, pass in null value or empty string to leave password alone
	 * @param firstname			  First name of the SiteUser
	 * @param lastname			   Last name of the SiteUser
	 * @param email				  Email address of the SiteUser
	 * @param phone				  Phone number of the SiteUser
	 * @param primaryUserRoleName	type or primary role of user
	 * @param regionName			 region of user
	 * @param stateName			  state of user
	 * @param states              list of states for regional user
	 * @param secondaryUserRoleNames SecondaryUserRole of the SiteUser
	 * @param locked				 create user in locked state
	 * @return true if user was added
	 */
	boolean addUser(String username, String password, String firstname, String lastname, String email,
					String phone, String primaryUserRoleName, String regionName, String stateName, List<String> states,
					String[] secondaryUserRoleNames, boolean locked, Map<String, Object> session);

	/**
	 * Update a SiteUser.
	 *
	 * @param session   where the logged in user is stored
	 * @param username				  User name of the SiteUser
	 * @param password				  Password of the SiteUser, pass in null value or empty string to leave password alone
	 * @param firstname				 First name of the SiteUser
	 * @param lastname				  Last name of the SiteUser
	 * @param email					 Email address of the SiteUser
	 * @param phone					 Phone number of the SiteUser
	 * @param primaryUserRoleName	   type or primary role of user
	 * @param regionName				region of user
	 * @param stateName				 state of user
	 * @param secondaryUserRoleNames	SecondaryUserRole of the SiteUser
	 * @param locked					create user in locked state
	 * @param receiveEmailNotifications user requests to receive email notifications
	 * @return true if username already exists and changes were successfully made
	 */
	boolean saveUser(Map<String, Object> session, String username, String password,
					 String firstname, String lastname,
					 String email, String phone,
					 String primaryUserRoleName, String regionName, String stateName, List<String> states,
					 String[] secondaryUserRoleNames,
					 boolean locked,
					 boolean receiveEmailNotifications,
					 Long userId);

	/**
	 * Update a SiteUser.
	 *
	 * @param session   where the logged in user is stored
	 * @param password  Password of the SiteUser, pass in null value or empty string to
	 *                  leave password alone
	 * @param firstname First name of the SiteUser
	 * @param lastname  Last name of the SiteUser
	 * @param email	 Email address of the SiteUser
	 * @param phone	 Phone number of the SiteUser
	 * @param receiveEmailNotifications user requests to receive email notifications
	 * @return true if logged in user exists and changes were successfully made
	 */
	boolean saveLoggedInUser(Map<String, Object> session, String password,
							 String firstname, String lastname,
							 String email, String phone,
							 boolean receiveEmailNotifications);

	/**
	 * Remove a SiteUser from database
	 *
	 * @param username User name of the SiteUser
	 * @return true if username exists and it was successfully removed
	 */
	boolean removeUser(String username);

	/**
	 * Change a user's password.
	 *
	 * @param siteUser user whose password will be changed
	 * @param password the new password
	 */
	void saveNewPasswordForUser(SiteUser siteUser, String password);

	/**
	 * Gets the privileges of a given user and places them into the session.
	 * TODO: (dsjoquist) consider *not* storing the privileges in the
	 * session (let something else do that)
	 */
	HashSet<String> loadPrivileges(SiteUser user, Map<String, Object> session);

	/**
	 * Get the SiteUser with the user id (siteuserid PK)
	 *
	 * @param usrId of the SiteUser
	 * @return SiteUser with matching id, null otherwise
	 */
	SiteUser getUserWithUserId(Long usrId) throws HibernateException;

	/**
	 * Get the SiteUser with a specific userName
	 *
	 * @param userName userName of the SiteUser
	 * @return SiteUser with matching userName, null otherwise
	 */
	SiteUser getUserWithUserName(String userName) throws HibernateException;

	/**
	 * Get the Active SiteUser with a specific userName
	 *
	 * @param userName userName of the SiteUser
	 * @return Active SiteUser with matching userName, null otherwise
	 */
	SiteUser getActiveUserWithUserName(String userName) throws HibernateException;
	
	/**
	 * Get the SiteUser with a specific Email address.
	 *
	 * @param emailAddress emailAddress of the SiteUser
	 * @return SiteUser with matching emailAddress, null otherwise
	 */
	/*
	SiteUser getUserWithEmailAddress(String emailAddress) throws HibernateException;
	*/
	
	/**
	 * Get a SiteUser given a password change key.
	 *
	 * @param key password change key
	 * @return SiteUser with given key or null if none exists
	 */
	SiteUser getUserWithPasswordChangeKey(String key);

	/**
	 * Generate a URL for a user to change his/her password.
	 *
	 * @param siteUser	   user that needs to change password
	 * @param servletContext a servlet context---get one by implementing ServletContextAware
	 * @return the change password URL
	 * @throws MalformedURLException
	 */
	String processPasswordChangeKey(SiteUser siteUser, final HttpServletRequest request)
			throws MalformedURLException;

	/**
	 * Validate password strength
	 *
	 * @param password	   candidate password
	 * @return true if password passes strength, otherwise false
	 */
	boolean passwordIsStrong (String password);

	/**
	 * Validate password is changed
	 *
	 * @param siteUser	   the user logged in
	 * @param password	   new password
	 * @return true if password is the same as current password
	 */
	boolean passwordNotChanged (SiteUser siteUser, String password);
	
	/**
	 * Generate a URL for a user to change his/her password.
	 *
	 * @param emailAddress   the email address of the user that needs to change password
	 * @param servletContext a servlet context---get one by implementing ServletContextAware
	 * @return the change password URL, or null if no user exists with the given
	 *         email
	 * @throws HibernateException
	 * @throws MalformedURLException
	 */
	String processPasswordChangeKey(final String emailAddress, final HttpServletRequest request)
			throws HibernateException, MalformedURLException;

	/**
	 * Populates the static 'Type' radio button choices
	 *
	 * @return list
	 * @author 18922
	 */
	List<PrimaryUserRole> getTypeOfUserList(boolean includeSysAdmin);

	/**
	 * Searches for users with specific criteria.
	 *
	 * @param username			   the username to search for
	 * @param firstName			  the first name to search for
	 * @param lastName			   the last name to search for
	 * @param email				  the email address to search for
	 * @param selectedPrimaryRoles   if any roles specified, the user must match at least one of them
	 * @param selectedRegion		 the region of the user
	 * @param selectedState		  the state of the user
	 * @param selectedSecondaryRoles if any roles specified, the user must match at least one of them
	 * @return list of matching users
	 */
	List<SiteUser> getUserSearchResultList(String username, String firstName, String lastName, String email,
										   String[] selectedPrimaryRoles, String selectedRegion, String selectedState,
										   String[] selectedSecondaryRoles,String columnSelected,
										   boolean orderByDescending, boolean excludeTestUsers);

	/**
	 * Returns list of regions.
	 *
	 * @return list of regions.
	 * @author 18922
	 */
	List<Region> getRegionList();

	List<State> getStateList();

	/**
	 * Get state list for a given region 	
	 * @param regionId
	 * @return List<State>
	 */
	List<State> getStateListForRegion(Long regionId);

	/**
	 * Get state list for a given region name	
	 * @param regionName
	 * @return List<State>
	 */	
	Map<String, String> getStateMapForRegionName(String regionName);
	
	/**
	 * Gets list of secondary user roles.
	 *
	 * @return list of secondary user roles.
	 */
	List<SecondaryUserRole> getSecondaryUserRoleList();

	PrimaryUserRole lookupPrimaryUserRole(String primaryUserRoleName);

	Region lookupRegion(String regionName);

	State lookupState(String stateName);

	SecondaryUserRole lookupSecondaryUserRole(String secondaryUserRoleName);

}
