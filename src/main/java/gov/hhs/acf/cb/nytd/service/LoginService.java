package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.SiteUser;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This service handles operations related to login authentication.
 */
public interface LoginService extends BaseService
{
	
	/**
	 * Get a SiteUser and places it in the session.
	 *
	 * @param username username of the SiteUser
	 * @param password password of the SiteUser
	 * @param session  current session map
	 * @return SiteUser with matching username and password, null otherwise
	 */
	SiteUser processLogin(String username, String password, Map<String, Object> session);
	
	/**
	 * Get a SiteUser with federal user role and places it in the session.
	 *
	 * @param email email of the SiteUser
	 * @param session  current session map
	 * @return SiteUser with matching email, null otherwise
	 */
	SiteUser getFederalUserByEmail(String email, Map<String, Object> session);

	/**
	 * Get the list of all active users with a specific Email address.
	 *
	 * @param email email address of the SiteUser
	 * @return List of active users with matching emailAddress
	 */
	List<SiteUser> getAllActiveUserListByEmail(String email);
        
	/**
	 * Add a FakeUser.
	 *
	 * @param username the username of the new FakeUser
	 */
	void saveFakeUser(String username);
	
	/**
	 * Removes the time lock on a site user.
	 *
	 * @param user the SiteUser from which to remove the time lock
	 */
	void removeTimeLock(SiteUser user);

	/**
	 * Add a time lock to a user.
	 *
	 * @param <T>      Type of user to audit. Should be either SiteUser or FakeUser.
	 * @param user	 the user to time lock
	 * @param lockTime the time of the time lock
	 */
	<T> void saveTimeLock(T user, Timestamp lockTime);

	/**
	 * Add a permanent lock to a user.
	 *
	 * @param <T>  Type of user to audit. Should be either SiteUser or FakeUser.
	 * @param user the user to permanently lock
	 */
	<T> void saveLock(T user);

	/**
	 * Determine how many times a user has attempted to log in, taking
	 * appropriate action.
	 *
	 * @param <T>     Type of user to audit. Should be either SiteUser or FakeUser.
	 * @param user	the user to audit
	 * @param session current session map
	 * @param success flag to indicate successful login event
	 */
	<T> void processUserAuthLog(T user, Map<String, Object> session);

	/**
	 * Remove all AuthLog entries for a particular user.
	 *
	 * @param username the username whose entries are to be removed
	 * @param description variable length array of specific log entry descriptions 
	 * 		to further filter which ones are removed  
	 * @return true if entries for username (and descriptions) were found in AuthLog, false
	 *         otherwise
	 */
	boolean removeAuthLogEntries(String username, String...description);

	/**
	 * Add an invalid login attempt to the AuthLog.
	 *
	 * @param username	the username of the SiteUser or FakeUser that attempted login
	 * @param attemptTime the time of the login attempt
	 * @param session	 current session map
	 * @param description type of log entry
	 */
	void saveAuthLogEntry(String username, Timestamp attemptTime, Map<String, Object> session, String description);

	/**
	 * Returns login errors created by the previous call to getUser
	 *
	 * @param session current session map
	 * @return the loginErrors created by the previous call to getUser
	 */
	LinkedList<String> getLoginErrors(Map<String, Object> session);

	/**
	 * Clears the actionErrors.
	 *
	 * @param session current session map
	 */
	void clearLoginErrors(Map<String, Object> session);

	/**
	 * Check for inactive user
	 *
	 * @param username	the username of the SiteUser that attempted login
	 */
	boolean inactiveUser(String username);

	/**
	 * Gets the privileges of a given user and places them into the session.
	 */
	HashSet<String> loadPrivileges(SiteUser user);
	
}
