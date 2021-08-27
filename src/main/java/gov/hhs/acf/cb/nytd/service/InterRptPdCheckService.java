package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonAcrossReportPeriods;

import java.util.Collection;
import java.util.List;


/**
 * Handles operations related to the NYTD cross-file comparison across report periods.
 * 
 * @author Adam Russell (18816)
 */
public interface InterRptPdCheckService extends BaseService
{
	/**
	 * Gets a transmission given its database identifier.
	 * 
	 * @param id database identifier of the transmission to get
	 * @return transmission with given database identifier
	 */
	Transmission getTransmissionWithId(Long id);
	
	/**
	 * Checks NYTD elements 4, 5-11, and 13 between the two given records for inconsistencies.
	 * 
	 * @param currentRecord record selected for comparison
	 * @param previousRecord record in the appropraite previous report period to compare against
	 * @return set of advisories resulting from the comparison
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCommonCrossReportPeriodAdvisoriesForYouth(
			RecordToExport currentRecord,
			RecordToExport previousRecord);
	
	/**
	 * Checks a served youth's record against the most recent report period in previousReportPeriod
	 * which his information appears.
	 * 
	 * @param record record selected for comparison
	 * @param state state whose file is being compared
	 * @return set of advisories resulting from the comparison
	 * @see InterRptPdCheckService#getPreviousRecordForServedYouth(TransmissionRecord, State)
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForServedYouth(
			RecordToExport record, State state);
	
	/**
	 * Checks a non-buffer-case-baseline youth's record in a "subsequent" or
	 * "corrected"-type file against the active submission in the directly
	 * ensuing report period to ensure that no matching post-buffer-case
	 * baseline information exists.
	 * 
	 * @param record record selected for comparison
	 * @param reportPeriod the report period ensuing that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return set of advisories resulting from the comparison
	 * @see InterRptPdCheckService#getPostBufferRecordForBaselineYouth(TransmissionRecord)
	 * @see InterRptPdCheckService#getEnsuingReportPeriod(ReportingPeriod)
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForBaselineYouth(
			RecordToExport record, ReportingPeriod reportPeriod, State state);
	
	/**
	 * Checks a first-period buffer-case baseline youth's record against a
	 * transmission in the directly ensuing report period, provided by the caller,
	 * to make sure that there is matching post-buffer information.
	 * 
	 * @param transmissionRecord pre-buffer baseline youth record selected for comparison
	 *        in the earlier report period
	 * @param nextTransmission transmission in which to find the youth's next record
	 * @return set of advisories resulting from the comparison
	 * @see InterRptPdCheckService#getPostBufferRecordForPreBufferYouth(TransmissionRecord, Transmission)
	 * @see InterRptPdCheckService#getPreviousBufferCaseRecords(ReportingPeriod, State)
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForPreBufferBaselineYouth(
			TransmissionRecord transmissionRecord, Transmission nextTransmission);
	
	/**
	 * Checks a second-period buffer-case baseline youth's record against the
	 * directly previous reporting period to make sure that baseline information
	 * does not exist there. A pre-buffer record with no information is allowed.
	 * Also ensures that the previous report period is the one in which the youth
	 * actually turned 17.
	 * 
	 * @param record record selected for comparison
	 * @param reportPeriod the report period preceding that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return set of advisories resulting from the comparison
	 * @see InterRptPdCheckService#getPreviousRecordForPostBufferYouthComparison(TransmissionRecord)
	 * @see InterRptPdCheckService#getPrecedingReportPeriod(ReportingPeriod)
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForPostBufferBaselineYouth(
			RecordToExport record, ReportingPeriod reportPeriod, State state);
	
	/**
	 * Checks a 19-year-old follow-up youth's record against either the reporting
	 * period 2 years prior or 1.5 years prior---whichever has the baseline information.
	 * 
	 * @param record record selected for comparison
	 * @param previousOutcomesReportPeriod the outcomes collection reporting period 2 years
	 *        prior to that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return set of advisories resulting from the comparison
	 * @see InterRptPdCheckService#getBaselineRecordForFollowup19YouthComparison(TransmissionRecord)
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForFollowup19Youth(
			RecordToExport record, ReportingPeriod previousOutcomesReportPeriod, State state);
	
	/**
	 * Checks a 21-year-old follow-up youth's record against the report period 2 years prior.
	 * 
	 * @param record record selected for comparison
	 * @param previousOutcomesReportPeriod the outcomes collection reporting period 2 years
	 *        prior to that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return set of advisories resulting from the comparison
	 * @see InterRptPdCheckService#getFollowup19RecordForFollowup21YouthComparison(TransmissionRecord)
	 */
	Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForFollowup21Youth(
			RecordToExport record, ReportingPeriod previousOutcomesReportPeriod, State state);

	/**
	 * Compares a transmitted data file to submissions in previous report
	 * periods.
	 * 
	 * Youth in the served population are checked against the most recent
	 * reporting period in which their information appear.
	 * 
	 * Follow-up-19 youth is checked against either the reporting
	 * period 2 years prior or 1.5 years prior---whichever has the baseline
	 * information.
	 * 
	 * Follow-up-21 youth, buffer-case or not, are checked against the
	 * reporting period 2 years prior.
	 * 
	 * Post-buffer baseline youth information is checked against the previous
	 * report period, which should be the report period in which the youth 
	 * had their 17th birthdays for redundant baseline information.
	 * 
	 * The buffer-case baseline youth records with missing information of the
	 * preceding report period are checked against the target file for the 
	 * expected baseline information.
	 * 
	 * If the target file is of the "subsequent" or "corrected"-type,
	 * baseline youth records are checked to make sure that post-buffer
	 * baseline information is not already present in the directly ensuing
	 * report period.
	 * 
	 * @param targetFile transmitted data file on which to run the comparisons
	 * @return the resulting FileComparisonAcrossReportPeriods object
	 * @see FileComparisonAcrossReportPeriods
	 */
	FileComparisonAcrossReportPeriods getFileComparisonAcrossReportPeriods(Transmission targetFile);
	
	/**
	 * @param Id of the transmission selected for comparision across report periods
	 * @return set of advisories resulting from the comparison
	 */
	List<FileAdvisoryAcrossReportPeriods> getFileAdvisoriesAcrossReportPeriods(String TransmissionId,long siteUserId);
}
