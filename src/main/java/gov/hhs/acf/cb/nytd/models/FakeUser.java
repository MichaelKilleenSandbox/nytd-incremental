/**
 * Filename: FakeUser.java
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
import java.util.Date;


/**
 * @author Adam Russell (18816)
 *
 */
public class FakeUser extends PersistentObject
{
	/**
	 * Default constructor
	 */
	public FakeUser()	{}
	
	/**
	 * Constructor
	 * 
	 * @param id the id to set
	 */
	public FakeUser(Long id)
	{
		this.id = id;
	}
	
	/**
	 * Constructor 
	 * 
	 * @param id the id to set
	 * @param userName the userName to set
	 * @param locked the value of locked to set
	 * @param timeLocked the value of timeLocked to set
	 * @param createdDate the createdDate to set
	 * @param createdBy the createdBy to set
	 * @param updateDate the updateDate to set
	 * @param updateBy the updateBy to set
	 * @param description the description to set
	 */
	public FakeUser(Long id, String userName, boolean locked, Timestamp timeLocked,
	                Calendar createdDate, String createdBy, Calendar updateDate, String updateBy, String description)
	{
		this.id = id;
		this.userName = userName;
		this.locked = locked;
		this.timeLocked = timeLocked;
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
	 * @return the locked
	 */
	public boolean isLocked()
	{
		return locked;
	}

	/**
	 * @param locked the locked to set
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
	 * Determine whether the FakeUser is time locked.
	 * 
	 * The FakeUser is time locked if the value of timeLocked is a time within the past 30 minutes.
	 * 
	 * @return true if FakeUser is time locked, false otherwise
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

	private String userName;
	private boolean locked = false;
	private Timestamp timeLocked;
}
