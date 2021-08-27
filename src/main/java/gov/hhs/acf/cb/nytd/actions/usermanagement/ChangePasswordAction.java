/**
 * Filename: changePasswordAction.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Aug 3, 2009
 *  Author: 18816
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.actions.usermanagement;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.LoginService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;


/**
 * Reset a user's password.
 * 
 * @author 18816
 */
public class ChangePasswordAction extends ActionSupport implements SessionAware
{
	private String password1;
	private String password2;
	private LoginService loginService;
	private UserService userService;
	private Map<String, Object> session;
	private String destination;
	
	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS if password is successfully changed
	 *         Action.ERROR if this action is accessed without a successful changePasswordPage action
	 *         Constants.BAD_INPUT if the given two passwords do not match
	 *         Action.INPUT if the user did not enter a password in one or both of the fields
	 */
	public final String execute()
	{
		SiteUser siteUser;
		
		siteUser = (SiteUser) session.get("userChangingPassword");
		
		if (siteUser == null)
		{
			// This action was accessed without going through ChangePasswordPage action first.
			return Action.ERROR;
		}
		
		if (!password1.equals(password2))
		{
			addActionError("Your passwords must match. Please try again.");
			return Constants.BAD_INPUT;
		}
		else if (!userService.passwordIsStrong(password1))
		{
			addActionError(userService.INSUFFICIENT_PWD);
			return Action.INPUT;
		}
		else if (userService.passwordNotChanged(siteUser, password1))
		{
			addActionError("Your password cannot be the same as the current password. Please try again.");
			return Constants.BAD_INPUT;
		}
		
		userService.saveNewPasswordForUser(siteUser, password1);
		session.remove("userChangingPassword");
		
		if (session.containsKey("autoLogin")) {
			loginService.processLogin(siteUser.getUserName(), password1, session);
			session.remove("autoLogin");
		}
		
		if (session.containsKey("destination")) {
			this.destination = (String)session.get("destination");
			return Action.NONE;
		}
		
		return Action.SUCCESS;
	}
	
	/**
	 * @return the password1
	 */
	public String getPassword1()
	{
		return password1;
	}

	/**
	 * @param password1 the password1 to set
	 */
	public void setPassword1(String password1)
	{
		this.password1 = password1;
	}

	/**
	 * @return the password2
	 */
	public String getPassword2()
	{
		return password2;
	}

	/**
	 * @param password2 the password2 to set
	 */
	public void setPassword2(String password2)
	{
		this.password2 = password2;
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
	
	/**
	 * @param userService the service to set
	 */
	public final void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the service
	 */
	public final UserService getUserService()
	{
		return userService;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.interceptor.SessionAware#setSession(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSession(Map session)
	{
		this.session = session;
	}
	
	public String getDestination() {
		return destination;
	}

}
