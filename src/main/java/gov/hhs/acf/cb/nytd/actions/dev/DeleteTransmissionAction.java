/**
 * Filename: DeleteTransmissionAction.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Dec 2, 2009
 *  Author: adam
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.actions.dev;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;


/**
 * Deletes transmissions with specified database identifiers.
 * 
 * @author Adam Russell (18816)
 */
public class DeleteTransmissionAction extends ActionSupport
{
	TransmissionServiceP3 transmissionServiceP3;
	Collection<Long> transmissions = new LinkedList<Long>();
	protected Map<String, Object> session;
	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS
	 */
	public final String execute()
	{
		SiteUser siteUser = (SiteUser) session.get("siteUser");
        for (Long id : getTransmissions()) {
            transmissionServiceP3.deleteTransmission(id, siteUser);
        }
        
		return SUCCESS;
	}

	/**
	 * @return the transmissionService
	 */
	public TransmissionServiceP3 getTransmissionServiceP3()
	{
		return transmissionServiceP3;
	}

	/**
	 * @param transmissionServiceP3 the transmissionService to set
	 */
	public void setTransmissionServiceP3(TransmissionServiceP3 transmissionServiceP3)
	{
		this.transmissionServiceP3 = transmissionServiceP3;
	}

	/**
	 * @return the transmissions
	 */
	public Collection<Long> getTransmissions()
	{
		return transmissions;
	}

	/**
	 * @param transmissions the transmissions to set
	 */
	public void setTransmissions(Collection<Long> transmissions)
	{
		this.transmissions = transmissions;
	}
}

