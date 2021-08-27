/**
 * Filename: checkPermission.java
 * 
 * Copyright 2009, ICF International
 * Created: Oct 14, 2009
 * Author: 16939
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
package gov.hhs.acf.cb.nytd.models.helper;

/**
 * @author 16939
 * 
 */
public class CheckPermission
{
	private String userName;
	private String permissions;

	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * @param userName
	 *           the userName to set
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * @return the permissions
	 */
	public String getPermissions()
	{
		return permissions;
	}

	/**
	 * @param permissions
	 *           the permissions to set
	 */
	public void setPermissions(String permissions)
	{
		this.permissions = permissions;
	}
}
