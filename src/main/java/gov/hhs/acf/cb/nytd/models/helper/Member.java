/**
 * Filename: Member.java
 * 
 * Copyright 2009, ICF International
 * Created: Oct 1, 2009
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
public class Member
{
	private String firstAndLastName;
	private String memberId;
	private String userName;

	/**
	 * @return the memberId
	 */
	public String getMemberId()
	{
		return memberId;
	}

	/**
	 * @param memberId
	 *           the memberId to set
	 */
	public void setMemberId(String memberId)
	{
		this.memberId = memberId;
	}

	/**
	 * @return the firstAndLastName
	 */
	public String getFirstAndLastName()
	{
		return firstAndLastName;
	}

	/**
	 * @param firstAndLastName
	 *           the firstAndLastName to set
	 */
	public void setFirstAndLastName(String firstAndLastName)
	{
		this.firstAndLastName = firstAndLastName;
	}

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
}
