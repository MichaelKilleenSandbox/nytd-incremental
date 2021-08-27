/**
 * Filename: changePasswordPageAction.java
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
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;


/**
 * Prepare a page that allows a user to change his/her password.
 * 
 * @author 18816
 */
public class ChangePasswordPageAction extends ActionSupport implements SessionAware, ParameterAware
{
	private String key;
	private UserService userService;
	private Map<String, Object> session;
	private Map<String, String[]> parameters;
	
	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS if key is valid for a SiteUser,
	 *         Constants.BAD_PARAM if key is invalid or doesn't exist.
	 */
	public final String execute()
	{
		//NYTD-9: If user logged in with an existing password that
		//no longer passes strength test, userChangingPassword property
		//will have already been set in UserServiceImpl
		if (!session.containsKey("userChangingPassword")) {
			String[] keyVals = parameters.get("key");
			SiteUser siteUser;
			
			if (keyVals == null || keyVals.length == 0)
			{
				return Constants.BAD_PARAM;
			}
			
			key = keyVals[0];		
			siteUser = userService.getUserWithPasswordChangeKey(key);
			
			if (siteUser == null)
			{
				return Constants.BAD_PARAM;
			}
			
			session.put("userChangingPassword", siteUser);
		} 
		else {
			//tell the user why they were redirected to change
			//password page
			addActionError(userService.INSUFFICIENT_PWD);
			session.put("destination", "dashboard.action");
			session.put("autoLogin", true);
		}
		
		return Action.SUCCESS;
	}
	
	/**
	 * @return the key
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key)
	{
		this.key = key;
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

	/* (non-Javadoc)
	 * @see org.apache.struts2.interceptor.ParameterAware#setParameters(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setParameters(Map parameters)
	{
		this.parameters = parameters;
	}
}
