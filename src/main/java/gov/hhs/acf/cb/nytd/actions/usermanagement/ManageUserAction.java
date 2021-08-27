/**
 * Filename: ManageUserAction.java
 * 
 * Copyright 2009, ICF International
 * Created: Jun 11, 2009
 * Author: 18816
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
package gov.hhs.acf.cb.nytd.actions.usermanagement;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.LoginService;
import gov.hhs.acf.cb.nytd.service.LookupService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.ApplicationDataHelper;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.ValidationException;
import gov.hhs.acf.cb.nytd.util.ValidationResultItem;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Satinder Gill (18922)
 */
@SuppressWarnings("serial")
public class ManageUserAction extends ActionSupport implements SessionAware, ApplicationAware, ValidationAware, ServletRequestAware, ParameterAware
{

	public static final String RESPONSE_DISPLAY_AM_PAGE = "displayAccountManagementPage";
	public static final String RESPONSE_DISPLAY_ADD_USER_PAGE = "displayAddUserPage";
	public static final String RESPONSE_DISPLAY_EDIT_USER_PAGE = "displayEditUserPage";
	public static final String RESPONSE_DISPLAY_REMOVE_USER_PAGE = "displayRemoveUserPage";
	public static final String RESPONSE_MANAGE_USER_PAGE = "manageUserPage";
	public static final String RESPONSE_CONFIRMATION = "confirmation";
	public static final String RESPONSE_USER_PRIMARY_ROLE_SELECT = "selectUserPrimaryRolePage";

	// logger
	protected Logger log = Logger.getLogger(getClass());

	// services
	private LookupService lookupService;
	private UserService userService;
	private LoginService loginService;

	protected enum LockedStatus
	{
		Locked(true), Unlocked(false);

		private boolean locked;

		private LockedStatus(boolean locked)
		{
			this.locked = locked;
		}

		public boolean isLocked()
		{
			return locked;
		}

		public static LockedStatus toLockedStatus(boolean locked)
		{
			return locked ? Locked : Unlocked;
		}
	}

	// action properties
	private String userName;
	private String oldPassword;
	private String newPassword1;
	private String newPassword2;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String primaryUserRoleName;
	private String regionName;
	private String stateName;
	private String[] secondaryUserRoleNames;
	private String lockedStatus;
	private boolean receiveEmailNotifications;
	private boolean editingLoggedInUser;
	private boolean editingLoggedInUserPassword;
	private Long usrId;
	private String save;
	private Map<String, Object> session;
	private Map<String, Object> application;

	@Getter @Setter private Map<String, String> defaultStates = new HashMap<String, String>(0);
	@Getter @Setter private Map<String, String> availableStates = new HashMap<String, String>(0);
	@Getter @Setter private List<String> states;
	@Setter private HttpServletRequest servletRequest;
	@Setter private Map<String, String[]> parameters;
	
	public final String accountManagement()
	{
		String returnVal = RESPONSE_DISPLAY_AM_PAGE;
		log.debug("ManageUserAction.accountManagement");
		log.debug("1) receiveEmailNotifications = " + receiveEmailNotifications);

		SiteUser loggedInUser = getLoggedInUser();

		loadValues(loggedInUser);

		log.debug("2) receiveEmailNotifications = " + receiveEmailNotifications);

		if(!loggedInUser.getPrimaryUserRole().getName().equals("System Administrator") && !loggedInUser.getPrimaryUserRole().getName().equals("State User"))
		{
			returnVal = "editLoggedInUser";
		}
		
		return returnVal;
	}

	private void loadValues(SiteUser siteUser)
	{
		usrId = (siteUser == null) ? -1 : siteUser.getId();
		userName = (siteUser == null) ? "" : siteUser.getUserName();
		firstName = (siteUser == null) ? "" : siteUser.getFirstName();
		lastName = (siteUser == null) ? "" : siteUser.getLastName();
		emailAddress = (siteUser == null) ? "" : siteUser.getEmailAddress();
		phoneNumber = (siteUser == null) ? "" : siteUser.getPhoneNumber();
		receiveEmailNotifications = (siteUser == null) ? false : siteUser.isReceiveEmailNotifications();
		oldPassword = "";
		newPassword1 = "";
		newPassword2 = "";
		primaryUserRoleName = (siteUser == null) || (siteUser.getPrimaryUserRole() == null) ? "" : siteUser
				.getPrimaryUserRole().getName();
		regionName = (siteUser == null) || (siteUser.getRegion() == null) ? "" : siteUser.getRegion().getRegion();
		stateName = (siteUser == null) || (siteUser.getState() == null) ? "" : siteUser.getState()
				.getStateName();
		lockedStatus = LockedStatus.toLockedStatus((siteUser == null) ? false : siteUser.isLocked()).name();
		if (siteUser == null)
		{
			secondaryUserRoleNames = new String[] {};
		}
		else
		{
			secondaryUserRoleNames = new String[siteUser.getSiteUserSecondaryUserRoles().size()];
			int idx = 0;
			for (SiteUserSecondaryUserRole siteUserSecondaryUserRole : siteUser.getSiteUserSecondaryUserRoles())
			{
				if (siteUserSecondaryUserRole.getSecondaryUserRole() != null)
					secondaryUserRoleNames[idx++] = siteUserSecondaryUserRole.getSecondaryUserRole().getName();
			}
			if(siteUser.getPrimaryUserRole() != null && 
			   siteUser.getPrimaryUserRole().getName().equals("Regional Office User")){
				  availableStates = userService.getStateMapForRegionName(regionName);
				  defaultStates = new TreeMap<String, String>();
				  if(siteUser.getSiteUserStateRegionMappings() != null && !siteUser.getSiteUserStateRegionMappings().isEmpty()) {
					  for(SiteUserStateRegionMapping siteUserStateRegionMapping:siteUser.getSiteUserStateRegionMappings()) {
						  defaultStates.put(siteUserStateRegionMapping.getState().getStateName(), siteUserStateRegionMapping.getState().getStateName());
					  }
				  } else {
					  defaultStates = userService.getStateMapForRegionName(regionName);
				  }
			}
		}
		log.debug("loadValues");
		log.debug("\tuserName = " + userName);
		log.debug("\tfirstName = " + firstName);
		log.debug("\tlastName = " + lastName);
		log.debug("\temailAddress = " + emailAddress);
		log.debug("\tphoneNumber = " + phoneNumber);
		log.debug("\tnewPassword1 = " + newPassword1);
		log.debug("\tnewPassword2 = " + newPassword2);

		log.debug("\tprimaryUserRoleName = " + primaryUserRoleName);
		log.debug("\tregionName = " + regionName);
		log.debug("\tstateName = " + stateName);
		log.debug("\tsecondaryUserRoleNames = " + Arrays.toString(secondaryUserRoleNames));
		log.debug("\tlockedStatus = " + lockedStatus);
		log.debug("\treceiveEmailNotifications = " + receiveEmailNotifications);
	}

	public final String editLoggedInUser()
	{
		editingLoggedInUser = true;
		editingLoggedInUserPassword = false;
		SiteUser siteUser = userService.getUserWithUserName(userName);
		receiveEmailNotifications = siteUser.isReceiveEmailNotifications();
		loadValues(siteUser);
		return Action.INPUT;
	}

	public final String editLoggedInUserPassword()
	{
		editingLoggedInUser = true;
		editingLoggedInUserPassword = true;
		return Action.INPUT;
	}

	/**
	 * @return
	 */
	public final String saveLoggedInUser()
	{
		log.debug("ManageUserAction.saveLoggedInUser");
		log.debug("\tuserName = " + userName);
		log.debug("\tfirstName = " + firstName);
		log.debug("\tlastName = " + lastName);
		log.debug("\temailAddress = " + emailAddress);
		log.debug("\treceiveEmailNotifications = " + receiveEmailNotifications);
		log.debug("\tphoneNumber = " + phoneNumber);
		log.debug("\toldPassword = " + oldPassword);
		log.debug("\tnewPassword1 = " + newPassword1);
		log.debug("\tnewPassword2 = " + newPassword2);
		
		boolean bailout = true;

		if(oldPassword != null)
		{
			if (!oldPasswordFieldMatches())
			{
				addFieldError("oldPassword", "Invalid Current Password");
				log.debug("Invalid Current Password");
			}
			else if (!newPasswordFieldsMatch())
			{
				addFieldError("newPassword2", "New Passwords Must Match");
				log.debug("New Passwords Must Match");
			}
			else if (!userService.passwordIsStrong(newPassword1))
			{
				addFieldError("newPassword1", userService.INSUFFICIENT_PWD);
				log.debug("Insufficient password strength");
			}
			else bailout = false;
			
			if (bailout) {
				editingLoggedInUser = true;
				editingLoggedInUserPassword = true;
				return Action.INPUT;
			}
		}
		
		try
		{
			userService.saveLoggedInUser(session, newPassword1, firstName, lastName, emailAddress,
					phoneNumber, receiveEmailNotifications);
			editingLoggedInUser = false;
			editingLoggedInUserPassword = false;
		}
		catch (ValidationException e)
		{
			log.debug("validation exceptions");
			for (ValidationResultItem validationResultItem : e.getValidationResult().getMessages())
			{
				log.debug("\t" + validationResultItem.getItem() + ", " + validationResultItem.getMessage());
				addFieldError(validationResultItem.getItem(), validationResultItem.getMessage());
			}
			editingLoggedInUser = true;
			return Action.INPUT;
		}

		return RESPONSE_CONFIRMATION;
	}

	public boolean isEditingLoggedInUser()
	{
		log.debug("ManageUserAction.isEditingLoggedInUser: " + editingLoggedInUser);
		return editingLoggedInUser;
	}

	public void setEditingLoggedInUser(boolean editingLoggedInUser)
	{
		this.editingLoggedInUser = editingLoggedInUser;
	}

	public boolean isEditingLoggedInUserPassword()
	{
		log.debug("ManageUserAction.isEditingLoggedInUserPassword: " + editingLoggedInUserPassword);
		return editingLoggedInUserPassword;
	}

	public void setEditingLoggedInUserPassword(boolean editingLoggedInUserPassword)
	{
		this.editingLoggedInUserPassword = editingLoggedInUserPassword;
	}

	private SiteUser getLoggedInUser()
	{
		return (SiteUser) session.get("siteUser");
	}

	public List<PrimaryUserRole> getPrimaryUserRoleList()
	{
		SiteUser loggedInUser = getLoggedInUser();
		boolean includeSysAdmin = (loggedInUser != null)
				&& loggedInUser.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS);
		ApplicationDataHelper applicationDataHelper = new ApplicationDataHelper(application);
		if (includeSysAdmin)
		{
			return applicationDataHelper.getPrimaryUserRoleWithSysAdminList();
		}
		else
		{
			return applicationDataHelper.getPrimaryUserRoleNoSysAdminList();
		}
	}
	
	public List<State> getStates()
	{
		ApplicationDataHelper applicationDataHelper = new ApplicationDataHelper(application);
		return applicationDataHelper.getStateList();
	}

	
	public final String selectUserPrimaryRolePage()
	{
		SiteUser loggedInUser = getLoggedInUser();

		setSecondaryUserRoleNames(null);
		log.debug("ManageUserAction.addUserPage");
		log.debug("\tloggedInUser = " + loggedInUser);

		if (loggedInUser == null)
		{
			return Action.INPUT;
		}

		loadValues(null);
		this.secondaryUserRoleNames = null;
		if (loggedInUser.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS))
		{
			primaryUserRoleName = "";
			regionName = "";
			stateName = "";
		}
		else
		{
			primaryUserRoleName = (loggedInUser.getPrimaryUserRole() == null) ? "" : loggedInUser
					.getPrimaryUserRole().getName();
			regionName = (loggedInUser.getRegion() == null) ? "" : loggedInUser.getRegion().getRegion();
			stateName = (loggedInUser.getState() == null) ? "" : loggedInUser.getState().getStateName();
		}
		
		return RESPONSE_USER_PRIMARY_ROLE_SELECT;
	}
	
	public final String addUserPage()
	{
		SiteUser loggedInUser = getLoggedInUser();
		String tempPrimaryUserRole = getPrimaryUserRoleName();
		String tempStateName = getStateName();
		String tempRegionName = getRegionName();
		
		if(tempPrimaryUserRole == null || (tempPrimaryUserRole != null && tempPrimaryUserRole.equals("")))
		{
			
			//addFieldError("primaryUserRoleName", "PrimaryUserRole must be entered.");
			addActionError("PrimaryUserRole must be entered.");
			return Action.INPUT;
		}
		{
			boolean errorFlag = false;
			if(tempPrimaryUserRole != null && tempPrimaryUserRole.equals("Regional Office User"))
			{
  			    availableStates = userService.getStateMapForRegionName(tempRegionName);
				defaultStates = new HashMap<String, String>(0);
				if((tempRegionName == null ||(tempRegionName != null && tempRegionName.equals("")))) {
					errorFlag = true;
					addFieldError("regionName", "Region must be selected.");					
				}
			}
			
			if(tempPrimaryUserRole != null && tempPrimaryUserRole.equals("State User") &&(tempStateName == null ||(tempStateName != null && tempStateName.equals(""))))
			{
				errorFlag = true;
				addFieldError("stateName", "State must be selected.");
			}
					
			if(errorFlag)
				return Action.INPUT;
			
		}
	
		setSecondaryUserRoleNames(null);
		log.debug("ManageUserAction.addUserPage");
		log.debug("\tloggedInUser = " + loggedInUser);

		if (loggedInUser == null)
		{
			return Action.INPUT;
		}

		loadValues(null);
		
		
		this.secondaryUserRoleNames = null;
		if (loggedInUser.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS))
		{
			primaryUserRoleName = "";
			regionName = "";
			stateName = "";
			setPrimaryUserRoleName(tempPrimaryUserRole);
			setStateName(tempStateName);
			setRegionName(tempRegionName);
		}
		else
		{
			primaryUserRoleName = (loggedInUser.getPrimaryUserRole() == null) ? "" : loggedInUser
					.getPrimaryUserRole().getName();
			regionName = (loggedInUser.getRegion() == null) ? "" : loggedInUser.getRegion().getRegion();
			stateName = (loggedInUser.getState() == null) ? "" : loggedInUser.getState().getStateName();
		}

		return RESPONSE_DISPLAY_ADD_USER_PAGE;
	}

	public final String editUserPage()
	{
		log.debug("ManageUserAction.editUserPage");

		SiteUser loggedInUser = getLoggedInUser();
		if (loggedInUser == null)
		{
			return Action.INPUT;
		}

		SiteUser siteUser = getUserService().getUserWithUserId(usrId);
		if (siteUser == null)
		{
			String errMsg = "Selected user not found";
			log.debug(errMsg);
			addActionError(errMsg);
			return RESPONSE_MANAGE_USER_PAGE; // return to search page with error
		}

		loadValues(siteUser);

		return RESPONSE_DISPLAY_EDIT_USER_PAGE;
	}

	public final String removeUserPage()
	{
		log.debug("ManageUserAction.removeUserPage");

		SiteUser loggedInUser = getLoggedInUser();
		if (loggedInUser == null)
		{
			return Action.INPUT;
		}

		SiteUser siteUser = getUserService().getActiveUserWithUserName(userName);
		if (siteUser == null)
		{
			String errMsg = "User ID '" + userName + "' not found";
			log.debug(errMsg);
			addActionError(errMsg);
			return RESPONSE_MANAGE_USER_PAGE; // return to search page with error
		}

		loadValues(siteUser);

		return RESPONSE_DISPLAY_REMOVE_USER_PAGE;
	}
	
	
/*	@Validations(
			
			expressions={
				@ExpressionValidator(message="Password must be entered.",
				                     expression="primaryUserRoleName.equals(\"System Administrator\") && newPassword1.equals(\"\")")
				
				                     }
			
		)*/

	public final String addUser()
	{
		log.debug("ManageUserAction.addUser");
		log.debug("\tuserName = " + userName);
		log.debug("\tfirstName = " + firstName);
		log.debug("\tlastName = " + lastName);
		log.debug("\temailAddress = " + emailAddress);
		log.debug("\tphoneNumber = " + phoneNumber);
		log.debug("\tnewPassword1 = " + newPassword1);
		log.debug("\tnewPassword2 = " + newPassword2);

		log.debug("\tprimaryUserRoleName = " + primaryUserRoleName);
		log.debug("\tregionName = " + regionName);
		log.debug("\tstateName = " + stateName);
		log.debug("\tsecondaryUserRoleNames = " + Arrays.toString(secondaryUserRoleNames));
		log.debug("\tlockedStatus = " + lockedStatus);

		
		if((primaryUserRoleName!=null &&(primaryUserRoleName.equalsIgnoreCase("System Administrator") ||primaryUserRoleName.equalsIgnoreCase("State User") )))
		{
			boolean errorFlag = false;
			
			if(newPassword1 == null  ||(newPassword1 !=null && newPassword1.equals("")))
			{
				addFieldError("newPassword1", "Passwords must be entered.");
				errorFlag = true;
			}
			if(newPassword2 == null  ||(newPassword2 !=null && newPassword2.equals("")))
			{
				addFieldError("newPassword2", "Passwords must be confirmed.");
				errorFlag = true;
			}
			
			if(errorFlag)
				return Action.INPUT;
		}
		
		// Initialization and validations for regional office user
		if(primaryUserRoleName != null && primaryUserRoleName.equals("Regional Office User")) {
			availableStates = userService.getStateMapForRegionName(regionName);
			defaultStates = new HashMap<String, String>(0);
			if((states == null || states.isEmpty())) {
				addFieldError("states", "Please select atleast one state from the list");
				return Action.INPUT;
			}
		}
		
		try
		{
			if ((primaryUserRoleName.equalsIgnoreCase("Site User") || primaryUserRoleName.equalsIgnoreCase("System Administrator")) && !newPasswordFieldsMatch())
			{
				addFieldError("newPassword2", "Passwords must match");
				log.debug("Passwords must match");
				return Action.INPUT;
			} 
			else if (userService.addUser(userName, newPassword1, firstName, lastName, emailAddress, phoneNumber,
					primaryUserRoleName, regionName, stateName, states, secondaryUserRoleNames, LockedStatus.valueOf(
							lockedStatus).isLocked(), session))
			{
				return RESPONSE_CONFIRMATION;
			}
			else
			{
				addFieldError("userName", "User ID already exists: " + userName);
				log.debug("userName already exists: " + userName);
				return Action.INPUT;
			}
		}
		catch (ValidationException e)
		{
			for (ValidationResultItem validationResultItem : e.getValidationResult().getMessages())
			{
				addFieldError(validationResultItem.getItem(), validationResultItem.getMessage());
			}
			return Action.INPUT;
		}
	}

	// either both password fields must be empty, or they must match

	private boolean newPasswordFieldsMatch()
	{
		return (StringUtils.isEmpty(newPassword1) && StringUtils.isEmpty(newPassword2))
				|| newPassword1.equals(newPassword2);
	}

	private boolean oldPasswordFieldMatches()
	{
		SiteUser loggedInUser = getLoggedInUser();
		log.debug("ManageUserAction.oldPasswordFieldMatches");
		log.debug("loggedInUser = " + loggedInUser);
		log.debug("oldPassword = " + oldPassword);
		if (StringUtils.isEmpty(oldPassword))
		{
			return true;
		}
		else if (loggedInUser == null)
		{
			return false;
		}
		else
		{
			return getUserService().checkPassword(loggedInUser.getUserName(), oldPassword);
		}
	}

	public final String editUser()
	{
		log.debug("ManageUserAction.editUser");
		log.debug("\tuserName = " + userName);
		log.debug("\tfirstName = " + firstName);
		log.debug("\tlastName = " + lastName);
		log.debug("\temailAddress = " + emailAddress);
		log.debug("\tphoneNumber = " + phoneNumber);
		log.debug("\tnewPassword1 = " + newPassword1);
		log.debug("\tnewPassword2 = " + newPassword2);

		log.debug("\tprimaryUserRoleName = " + primaryUserRoleName);
		log.debug("\tregionName = " + regionName);
		log.debug("\tstateName = " + stateName);
		log.debug("\tsecondaryUserRoleNames = " + Arrays.toString(secondaryUserRoleNames));
		log.debug("\tlockedStatus = " + lockedStatus);
		log.debug("\treceiveEmailNotifications = " + receiveEmailNotifications);
		log.debug("\tuserID = " + usrId);

		try
		{
			if (!newPasswordFieldsMatch())
			{
				addFieldError("newPassword2", "Passwords must match");
				log.debug("Passwords must match");
				return Action.INPUT;
			}
			//if a new password has not been entered, bypass strength test
			else if (!StringUtils.isEmpty(newPassword1)) {
				if (!userService.passwordIsStrong(newPassword1))
				{
					addFieldError("newPassword1", userService.INSUFFICIENT_PWD);
					log.debug("Insufficient password strength");
					return Action.INPUT;
				}
			}
			

			
			SiteUser editedUser = userService.getUserWithUserId(usrId);			
			// Check if the user role has changed from regional office user
			if("Regional Office User".equals(editedUser.getPrimaryUserRole().getName())) {
				// if user role has changed from regional user to other primary role
				// delete all states associated with this user
				if(!primaryUserRoleName.equals(editedUser.getPrimaryUserRole().getName())) {
					states = new ArrayList<String>();
				} else if(regionName!=null && !regionName.equals(editedUser.getRegion().getRegion())) {
					// check if the region has changed for this regional user
					// if yes... then update the states in the state list and allow new state selections
					// go back to input page		
					  if(states == null || states.isEmpty()) {
						  availableStates = userService.getStateMapForRegionName(regionName);
						  defaultStates = new HashMap<String, String>(0);
						  addFieldError("states", "Please select at least one state from the list");
							return Action.INPUT;					  
					  }	else if(!checkStatesRegion(states,regionName))
					  {
						  availableStates = userService.getStateMapForRegionName(regionName);
						  defaultStates = new HashMap<String, String>(0);					
						  return Action.INPUT;
					  }						  
				} else if(save == null && regionName.equals(editedUser.getRegion().getRegion())) {
					  if(states == null || states.isEmpty()) {
						  availableStates = userService.getStateMapForRegionName(regionName);
						  defaultStates = new TreeMap<String, String>();
						  if(editedUser.getSiteUserStateRegionMappings() != null && !editedUser.getSiteUserStateRegionMappings().isEmpty()) {
							  for(SiteUserStateRegionMapping siteUserStateRegionMapping:editedUser.getSiteUserStateRegionMappings()) {
								  defaultStates.put(siteUserStateRegionMapping.getState().getStateName(), siteUserStateRegionMapping.getState().getStateName());
							  }
						  } else {
							  defaultStates = userService.getStateMapForRegionName(regionName);
						  }
							return Action.INPUT;					  
					  }					
				}
			} 
			boolean wasLocked = editedUser.isLocked();
			
			if (userService.saveUser(session, userName, newPassword1, firstName, lastName, emailAddress,
					phoneNumber, primaryUserRoleName, regionName, stateName, states, secondaryUserRoleNames, LockedStatus
							.valueOf(lockedStatus).isLocked(), receiveEmailNotifications, usrId))
			{
				if (wasLocked && !LockedStatus.valueOf(lockedStatus).isLocked()) {
					//delete last login authlog entry to prevent automatic locking of account at next login
					loginService.removeAuthLogEntries(userName, UserService.AUTHLOG_SUCCESS);
				}
				return RESPONSE_CONFIRMATION;
			}
			else
			{
				String errMsg = "User ID '" + userName + "' not found";
				log.debug(errMsg);
				addActionError(errMsg);
				return RESPONSE_MANAGE_USER_PAGE; // return to search page with
				// error
			}
		}
		catch (ValidationException e)
		{
			for (ValidationResultItem validationResultItem : e.getValidationResult().getMessages())
			{
				addFieldError(validationResultItem.getItem(), validationResultItem.getMessage());
			}
			return Action.INPUT;
		}
	}

	private boolean checkStatesRegion(List<String> states, String regionName) {
		// Check if selected states are in the region
		boolean isStateRegion = true;
		for(String stateStr:states) {
			State state = userService.lookupState(stateStr);
			if(!regionName.equals(state.getRegion().getRegion())) {
				isStateRegion = false;
				break;
			}
		}
		return isStateRegion;
	}

	public final String removeUser()
	{
		log.debug("ManageUserAction.removeUser");

		if (userService.removeUser(userName))
		{
			return RESPONSE_CONFIRMATION;
		}
		else
		{
			String errMsg = "User ID '" + userName + "' not found";
			errMsg = "The User ID '" + userName + "' can not be deleted at this point of time. This user may be associated with either active or inactive submissions in the NYTD system.";
			log.debug(errMsg);
			addActionError(errMsg);
			return RESPONSE_MANAGE_USER_PAGE; // return to search page with error
		}
	}

	public final void setUserName(final String userName)
	{
		this.userName = userName;
	}

	public final String getUserName()
	{
		return this.userName;
	}

	public final void setSuid(final String suid)
	{
		this.usrId = Long.parseLong(suid);
	}

	public final String getSuid()
	{
		return this.usrId.toString();
	}

	public final void setOldPassword(final String oldPassword)
	{
		this.oldPassword = oldPassword;
	}

	public final String getOldPassword()
	{
		return this.oldPassword;
	}

	public String getNewPassword1()
	{
		return newPassword1;
	}

	public void setNewPassword1(String newPassword1)
	{
		this.newPassword1 = newPassword1;
	}

	public String getNewPassword2()
	{
		return newPassword2;
	}

	public void setNewPassword2(String newPassword2)
	{
		this.newPassword2 = newPassword2;
	}

	public final void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	public final String getFirstName()
	{
		return this.firstName;
	}

	public final void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	public final String getLastName()
	{
		return this.lastName;
	}

	public final void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public final String getEmailAddress()
	{
		return this.emailAddress;
	}

	public final void setPhoneNumber(final String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public final String getPhoneNumber()
	{
		return this.phoneNumber;
	}

	public void setPrimaryUserRoleName(String primaryUserRoleName)
	{
		this.primaryUserRoleName = primaryUserRoleName;
	}

	public String getPrimaryUserRoleName()
	{
		return primaryUserRoleName;
	}

	public String getRegionName()
	{
		return regionName;
	}

	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}

	public String getStateName()
	{
		return stateName;
	}

	public void setStateName(String stateName)
	{
		this.stateName = stateName;
	}

	public String[] getSecondaryUserRoleNames()
	{
		return secondaryUserRoleNames;
	}

	public void setSecondaryUserRoleNames(String[] secondaryUserRoleNames)
	{
		this.secondaryUserRoleNames = secondaryUserRoleNames;
	}

	public boolean isHaveSecondaryUserRoleName()
	{
		return (secondaryUserRoleNames != null)
				&& ((secondaryUserRoleNames.length > 1) || !secondaryUserRoleNames[0].trim().equals(""));
	}

	public String getLockedStatus()
	{
		return lockedStatus;
	}

	public void setLockedStatus(String lockedStatus)
	{
		this.lockedStatus = lockedStatus;
	}

	public boolean isReceiveEmailNotifications()
	{
		return receiveEmailNotifications;
	}

	public void setReceiveEmailNotifications(boolean receiveEmailNotifications)
	{
		this.receiveEmailNotifications = receiveEmailNotifications;
	}

	public LookupService getLookupService()
	{
		return lookupService;
	}

	public void setLookupService(LookupService lookupService)
	{
		this.lookupService = lookupService;
	}

	public final void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public final UserService getUserService()
	{
		return this.userService;
	}
	
	/**
	 * @param loginService the service to set
	 */
	public final void setLoginService(final LoginService loginService)
	{
		this.loginService = loginService;
	}

	/**
	 * @return the loginService
	 */
	public final LoginService getLoginService()
	{
		return loginService;
	}

	public final Map<String, Object> getSession()
	{
		return session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSession(Map session)
	{
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setApplication(Map application)
	{
		this.application = application;

	}

	public final Map<String, Object> getApplication()
	{
		return application;
	}

	public Long getUsrId() {
		return usrId;
	}

	public void setUsrId(Long usrId) {
		this.usrId = usrId;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}
}
