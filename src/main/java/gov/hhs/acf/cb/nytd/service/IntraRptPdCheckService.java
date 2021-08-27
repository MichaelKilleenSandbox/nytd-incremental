package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod;


/**
 * Handles operations related to the NYTD cross-file comparison within a report period.
 * 
 * @author Adam Russell (18816)
 */
public interface IntraRptPdCheckService extends BaseService
{
	/**
	 * Get name of report period given an id.
	 * 
	 * @param reportPeriodId id of report period
	 * @return name of report period
	 */
	String getReportPeriodName(Long reportPeriodId);
	
	/**
	 * Get name of state given an id.
	 * 
	 * @param stateId id of state
	 * @return name of state
	 */
	String getStateName(Long stateId);
	
	/**
	 * Compares two transmitted data files within a report period.
	 * 
	 * @param transmission1Id the database identifier of the first transmission to compare
	 * @param transmission2Id the database identifier of the second transmission to compare
	 * @return the comparison between the two transmissions
	 */
	FileComparisonWithinReportPeriod getFileComparisonWithinReportPeriod(
			Long transmission1Id, Long transmission2Id);
}
