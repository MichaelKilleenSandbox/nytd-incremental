/**
 * Filename: CohortRecord.java
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
import org.apache.commons.lang3.StringUtils;


/**
 * @author Nava Krishna Mallela (23839)
 *
 */

public class CohortRecord extends PersistentObject {
	
	@Getter @Setter Long cohortStatusId;
	@Getter @Setter String recordNumber;
	@Getter @Setter String reportPeriodName;
	@Getter @Setter Long sampled;
	@Getter @Setter Long population21;
	@Getter @Setter Long notReportedFollowup19;
	@Getter @Setter CohortStatus cohortStatus;
	@Getter @Setter String followupRPName;
	@Getter @Setter String outcomePopulation;
	private String expectedFollowup19Period;
	private String expectedFollowup21Period;
	
	public CohortRecord(){}
	public CohortRecord(Long id)
	{
		this.id = id;
	}

	public CohortRecord(Long id,Long cohortStatusId,String recordNumber,String reportPeriodName, Long sampled, Long population21,Long notReportedFollowup19,CohortStatus cohortStatus,String followupRPName,String outcomePopulation)
	{
		this.id = id;
		this.cohortStatusId = cohortStatusId;
		this.recordNumber = recordNumber;
		this.sampled = sampled;
		this.population21 = population21;
		this.notReportedFollowup19 = notReportedFollowup19;
		this.cohortStatus = cohortStatus;
		this.reportPeriodName = reportPeriodName;
		this.followupRPName = followupRPName;
		this.outcomePopulation = outcomePopulation;
	}
	
	public String getExpectedFollowup19Period()
	{
		if(reportPeriodName != null)
		{
			String reportPeriodLetter = StringUtils.substring(reportPeriodName, (reportPeriodName.length()- 1),reportPeriodName.length());
			Long reportingPeiod = 0L;
			if(StringUtils.contains(reportPeriodName, 'A'))
			{
				reportingPeiod = Long.parseLong(StringUtils.removeEnd(reportPeriodName, "A"));
			}
			else
			{
				reportingPeiod = Long.parseLong(StringUtils.removeEnd(reportPeriodName, "B"));
			}
			
			if(StringUtils.equalsIgnoreCase("Baseline", outcomePopulation))
			{
				reportingPeiod = reportingPeiod +2;
				return  new StringBuffer(String.valueOf(reportingPeiod.longValue())).append(reportPeriodLetter).toString();
			}
			else if(StringUtils.equalsIgnoreCase("Post-buffer", outcomePopulation) && StringUtils.equalsIgnoreCase("B", reportPeriodLetter))
			{
				reportingPeiod = reportingPeiod +2;
				return  new StringBuffer(String.valueOf(reportingPeiod.longValue())).append('A').toString();
			}
			else if(StringUtils.equalsIgnoreCase("Post-buffer", outcomePopulation) && StringUtils.equalsIgnoreCase("A", reportPeriodLetter))
			{
				reportingPeiod = reportingPeiod +1;
				return  new StringBuffer(String.valueOf(reportingPeiod.longValue())).append('B').toString();
			}
		}
		return null;
	}
	
	public String getExpectedFollowup21Period()
	{
		if(reportPeriodName != null)
		{
			String reportPeriodLetter = StringUtils.substring(reportPeriodName, (reportPeriodName.length()- 1),reportPeriodName.length());
			Long reportingPeiod = 0L;
			if(StringUtils.contains(reportPeriodName, 'A'))
			{
				reportingPeiod = Long.parseLong(StringUtils.removeEnd(reportPeriodName, "A"));
			}
			else
			{
				reportingPeiod = Long.parseLong(StringUtils.removeEnd(reportPeriodName, "B"));
			}
			
			if(StringUtils.equalsIgnoreCase("Baseline", outcomePopulation))
			{
				reportingPeiod = reportingPeiod +4;
				return  new StringBuffer(String.valueOf(reportingPeiod.longValue())).append(reportPeriodLetter).toString();
			}
			else if(StringUtils.equalsIgnoreCase("Post-buffer", outcomePopulation) && StringUtils.equalsIgnoreCase("B", reportPeriodLetter))
			{
				reportingPeiod = reportingPeiod +4;
				return  new StringBuffer(String.valueOf(reportingPeiod.longValue())).append('A').toString();
			}
			else if(StringUtils.equalsIgnoreCase("Post-buffer", outcomePopulation) && StringUtils.equalsIgnoreCase("A", reportPeriodLetter))
			{
				reportingPeiod = reportingPeiod +3;
				return  new StringBuffer(String.valueOf(reportingPeiod.longValue())).append('B').toString();
			}
		}
		return null;
	}
}
