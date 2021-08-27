/**
 * Filename: MessageGroupListHelper.java
 * 
 * Copyright 2009, ICF International
 * Created: Sep 21, 2009
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

import java.math.BigDecimal;


/**
 * @author 16939
 * 
 */
public class MessageGroupListHelper
{
	private BigDecimal regionId;
	private BigDecimal siteUserId;
	private BigDecimal stateId;
	private BigDecimal messageGroupId;
	private String stateName;
	private String firstName;
	private String lastName;
	private String messageGroupName;
	private String username;

	/**
	 * @return the STATENAME
	 */
	public String getSTATENAME()
	{
		return stateName;
	}

	/**
	 * @param stateName
	 *           the STATENAME to set
	 */
	public void setSTATENAME(String stateName)
	{
		this.stateName = stateName;
	}

	/**
	 * @return the REGIONID
	 */
	public BigDecimal getREGIONID()
	{
		return regionId;
	}

	/**
	 * @param regionId
	 *           the REGIONID to set
	 */
	public void setREGIONID(BigDecimal regionId)
	{
		this.regionId = regionId;
	}

	/**
	 * @return the STATEID
	 */
	public BigDecimal getSTATEID()
	{
		return stateId;
	}

	/**
	 * @param stateId
	 *           the STATEID to set
	 */
	public void setSTATEID(BigDecimal stateId)
	{
		this.stateId = stateId;
	}

	/**
	 * @return the MESSAGEGROUPID
	 */
	public BigDecimal getMESSAGEGROUPID()
	{
		return messageGroupId;
	}

	/**
	 * @param messageGroupId
	 *           the MESSAGEGROUPID to set
	 */
	public void setMESSAGEGROUPID(BigDecimal messageGroupId)
	{
		this.messageGroupId = messageGroupId;
	}

	/**
	 * @return the FIRSTNAME
	 */
	public String getFIRSTNAME()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *           the FIRSTNAME to set
	 */
	public void setFIRSTNAME(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the LASTNAME
	 */
	public String getLASTNAME()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *           the LASTNAME to set
	 */
	public void setLASTNAME(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the MESSAGEGROUPNAME
	 */
	public String getMESSAGEGROUPNAME()
	{
		return messageGroupName;
	}

	/**
	 * @param messageGroupName
	 *           the MESSAGEGROUPNAME to set
	 */
	public void setMESSAGEGROUPNAME(String messageGroupName)
	{
		this.messageGroupName = messageGroupName;
	}

	/**
	 * @return the SITEUSERID
	 */
	public BigDecimal getSITEUSERID()
	{
		return siteUserId;
	}

	/**
	 * @param siteUserId
	 *           the SITEUSERID to set
	 */
	public void setSITEUSERID(BigDecimal siteUserId)
	{
		this.siteUserId = siteUserId;
	}

	/**
	 * @return the USERNAME
	 */
	public String getUSERNAME()
	{
		return username;
	}

	/**
	 * @param username
	 *           the USERNAME to set
	 */
	public void setUSERNAME(String username)
	{
		this.username = username;
	}
}
