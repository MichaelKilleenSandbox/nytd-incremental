/**
 * Filename: OutcomesReport.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Feb 15, 2013
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


/**
 * @author Nava Krishna Mallela (23839)
 *
 */
public class OutcomesReport extends PersistentObject {
	
	@Getter @Setter String stateName;
	@Getter @Setter private Long stateId;
	@Getter @Setter String rpName;
	@Getter @Setter String baselineRPName;
	@Getter @Setter String postBufferRPName;
	@Getter @Setter String followup19RPName;
	@Getter @Setter Long outcomeAge;
	@Getter @Setter Long reportingperiodId;
	@Getter @Setter Long cohortStatusId;
	@Getter @Setter Long cohortSize;
	@Getter @Setter Long SampleSize;
	@Getter @Setter Long outcomeUniverseCount;
	@Getter @Setter Long notReportedYouthCount;
	@Getter @Setter Long invalidE34YouthCount;
	@Getter @Setter Float outcomePercentage;
	@Getter @Setter Long fcPartCount;
	@Getter @Setter Long fcExcludedCount;
	@Getter @Setter Long fcCount;
	@Getter @Setter Long fcNotPartCount;
	@Getter @Setter Float fcPartPercentage;
	@Getter @Setter Long dcPartCount;
	@Getter @Setter Long dcExcludedCount;
	@Getter @Setter Long dcCount;
	@Getter @Setter Long dcNotPartCount;
	@Getter @Setter Float dcPartPercentage;
	@Getter @Setter Long transmissionId;
	@Getter @Setter String submissionStatus;
	
		
	public OutcomesReport(){}
	public OutcomesReport(Long id)
	{
		this.id = id;
	}


	/**
	 * @param stateName
	 * @param rpName
	 * @param baselineRPName
	 * @param postBufferRPName
	 * @param followup19rpName
	 * @param outcomeAge
	 * @param reportingperiodId
	 * @param cohortStatusId
	 * @param cohortSize
	 * @param sampleSize
	 * @param outcomeUniverseCount
	 * @param notReportedYouthCount
	 * @param invalidE34YouthCount
	 * @param outcomePercentage
	 * @param fcPartCount
	 * @param fcExcludedCount
	 * @param fcCount
	 * @param fcNotPartCount
	 * @param fcPartPercentage
	 * @param dcPartCount
	 * @param dcExcludedCount
	 * @param dcCount
	 * @param dcNotPartCount
	 * @param dcPartPercentage
	 */
	public OutcomesReport(Long id,String stateName, String rpName,
			String baselineRPName, String postBufferRPName,
			String followup19rpName, Long outcomeAge, Long reportingperiodId,
			Long cohortStatusId, Long cohortSize, Long sampleSize,
			Long outcomeUniverseCount, Long notReportedYouthCount,
			Long invalidE34YouthCount, Float outcomePercentage,
			Long fcPartCount, Long fcExcludedCount, Long fcCount,
			Long fcNotPartCount, Float fcPartPercentage, Long dcPartCount,
			Long dcExcludedCount, Long dcCount, Long dcNotPartCount,
			Float dcPartPercentage,String submissionStatus,Long transmissionId) {

		this.id = id;
		this.stateName = stateName;
		this.rpName = rpName;
		this.baselineRPName = baselineRPName;
		this.postBufferRPName = postBufferRPName;
		this.followup19RPName = followup19rpName;
		this.outcomeAge = outcomeAge;
		this.reportingperiodId = reportingperiodId;
		this.cohortStatusId = cohortStatusId;
		this.cohortSize = cohortSize;
		this.SampleSize = sampleSize;
		this.outcomeUniverseCount = outcomeUniverseCount;
		this.notReportedYouthCount = notReportedYouthCount;
		this.invalidE34YouthCount = invalidE34YouthCount;
		this.outcomePercentage = outcomePercentage;
		this.fcPartCount = fcPartCount;
		this.fcExcludedCount = fcExcludedCount;
		this.fcCount = fcCount;
		this.fcNotPartCount = fcNotPartCount;
		this.fcPartPercentage = fcPartPercentage;
		this.dcPartCount = dcPartCount;
		this.dcExcludedCount = dcExcludedCount;
		this.dcCount = dcCount;
		this.dcNotPartCount = dcNotPartCount;
		this.dcPartPercentage = dcPartPercentage;
		this.submissionStatus = submissionStatus;
		this.transmissionId = transmissionId;
	}
	
/*	public void setOutcomeAge(String sOutcomeAge)
	{
		if( sOutcomeAge != null )
			this.outcomeAge = new Long(sOutcomeAge);
		else
			this.outcomeAge = 0L;
		
	}
*/
	}
