/**
 * Filename: MessageRecipient.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Sep 18, 2009
 *  Author: 15178
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models;

import java.util.Calendar;

/**
 * @author 15178
 *
 */
public class MessageRecipient extends PersistentObject
{

	private SiteUser siteUser;
	private Message message;
	/**
	 * 
	 */
	public MessageRecipient()
	{
		// TODO Auto-generated constructor stub
	}
	public MessageRecipient(Long messageRecipientId, SiteUser siteUser, Message message,
			Calendar createdDate, String createdBy, Calendar updateDate, String updateBy, String description)
	{
		this.id = messageRecipientId;
		this.siteUser = siteUser;
		this.message = message;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.updateDate = updateDate;
		this.updateBy = updateBy;
		this.description = description;
	}

	/**
	 * @return the siteUser
	 */
	public SiteUser getSiteUser()
	{
		return siteUser;
	}
	/**
	 * @param siteUser the siteUser to set
	 */
	public void setSiteUser(SiteUser siteUser)
	{
		this.siteUser = siteUser;
	}
	/**
	 * @return the message
	 */
	public Message getMessage()
	{
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(Message message)
	{
		this.message = message;
	}

}
