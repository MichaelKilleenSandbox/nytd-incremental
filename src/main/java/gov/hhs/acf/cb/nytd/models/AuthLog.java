/**
 * Filename: AuthLog.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Jun 17, 2009
 *  Author: 18816
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models;

import java.sql.Timestamp;
import java.util.Calendar;


/**
 * @author Adam Russell (18816)
 *
 */
public class AuthLog extends PersistentObject
{
	/**
	 * Default constructor
	 */
	public AuthLog() {}
	
	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public AuthLog(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Constructor
	 * 
	 * @param id the id to set
	 * @param userName the userName to set
	 * @param attemptTime the attemptTime to set
	 * @param createdDate the createdDate to set
	 * @param createdBy the createdBy to set
	 * @param updateDate the updateDate to set
	 * @param updateBy the updateBy to set
	 * @param description the description to set
	 */
	public AuthLog(Long id, String userName, Timestamp attemptTime,
	               Calendar createdDate, String createdBy, Calendar updateDate, String updateBy, String description)
	{
		this.id = id;
		this.userName = userName;
		this.attemptTime = attemptTime;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.updateDate = updateDate;
		this.updateBy = updateBy;
		this.description = description;
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
	 * @return the attemptTime
	 */
	public Timestamp getAttemptTime()
	{
		return attemptTime;
	}

	/**
	 * @param attemptTime the attemptTime to set
	 */
	public void setAttemptTime(Timestamp attemptTime)
	{
		this.attemptTime = attemptTime;
	}
	
	private String userName;
	private Timestamp attemptTime;
}
