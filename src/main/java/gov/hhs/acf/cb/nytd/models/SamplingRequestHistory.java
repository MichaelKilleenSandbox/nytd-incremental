/**
 * Filename: Cohort.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Jun 27, 2012
 *  Author: 23839
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;


/**
 * @author Nava Krishna Mallela (23839)
 *
 */
public class SamplingRequestHistory extends PersistentObject {
	
	@Getter @Setter private Long samplingStatusId;
	@Getter @Setter private Long samplingRequestId;
	@Getter @Setter private Long requestStatusId;
	@Getter @Setter private Long messageId;
	@Getter @Setter private Calendar date; 
	@Getter @Setter private SamplingRequest samplingRequest; 
	@Getter @Setter private String requestStatus;
	@Getter @Setter private String lastSamplingMethod;
	
	public SamplingRequestHistory(){}
	public SamplingRequestHistory(Long id)
	{
		this.id = id;
	}

	public SamplingRequestHistory(Long id,Long samplingStatusId,
			Long requestStatusId, Long messageId, Calendar date,SamplingRequest samplingRequest, String lastSamplingMethod)
	{
		this.id = id;
		this.samplingStatusId = samplingStatusId;
		this.requestStatusId = requestStatusId;
		this.lastSamplingMethod = lastSamplingMethod;
		this.messageId = messageId;
		this.date = date;
		this.samplingRequest = samplingRequest;
	}
}
