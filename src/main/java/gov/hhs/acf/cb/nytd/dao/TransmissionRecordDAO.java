package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.models.TransmissionRecord;

import java.util.List;


public interface TransmissionRecordDAO
{
	/**
	 * Gets the buffer-case baseline youth records with missing information
	 * (pre-buffer baseline youth records) of the preceding report period
	 * 
	 * @param state state of submissions to consider
	 * @param prevoiusReportPeriod report period directly preceding that of the file selected for comparison
	 * @return buffer-case baseline youth records with missing information
	 *         (pre-buffer baseline youth records) of the preceding report period
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForPreBufferBaselineYouth(
	 *              TransmissionRecord, Transmission)
	 * @see TransmissionRecordDAO#getPostBufferRecordForPreBufferYouth(
	 *              TransmissionRecord, Transmission)
	 * @see TransmissionRecordDAO#getPrecedingReportPeriod(ReportingPeriod)
	 */
	List<TransmissionRecord> getPreviousBufferCaseRecords(ReportingPeriod previousReportPeriod, State state);
	
	/**
	 * In the set of the report periods prior to the one to which the given
	 * record belongs, get the most recent record in which a served youth's 
	 * information appears.
	 * 
	 * @param transmissionRecord record selected for comparison
	 * @param state state of submissions to consider
	 * @return record in previous report period with which to compare
	 *         or null if one wasn't found
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForServedYouth(MVwRecordToExport, State)
	 */
	TransmissionRecord getPreviousRecordForServedYouth(
			TransmissionRecord transmissionRecord, State state);
	
	/**
	 * Given a baseline youth's record in a "subsequent" or "corrected"-type 
	 * file, get that youth's post-buffer baseline records from the active
	 * submission in the directly ensuing report period.
	 * 
	 * Note that in the correct case, this should return null.
	 * 
	 * @param transmissionRecord record selected for comparison
	 * @param reportPeriod the report period ensuing that of the file selected for comparison
	 * @param state state of submissions to consider
	 * @return record in ensuing report period with which to compare, 
	 *         null if there is no matching record or if the given
	 *         transmissionRecord belongs to a "regular"-type file.
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForBaselineYouth(TransmissionRecord)
	 * @see TransmissionRecordDAO#getEnsuingReportPeriod(ReportingPeriod)
	 */
	TransmissionRecord getPostBufferRecordForBaselineYouth(
			TransmissionRecord transmissionRecord, ReportingPeriod reportPeriod, State state);
	
	/**
	 * Given a first-period buffer-case baseline youth's record, get
	 * that youth's post-buffer record from the given transmission.
	 * 
	 * @param transmissionRecord record selected for comparison
	 * @param nextTransmission transmission in which to find the youth's next record
	 * @return matching record of the youth with which to compare
	 *         or null if one wasn't found
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForPreBufferBaselineYouth(
	 *              TransmissionRecord, Transmission)
	 * @see TransmissionRecordDAO#getPreviousBufferCaseRecords(ReportingPeriod, State)
	 */
	TransmissionRecord getPostBufferRecordForPreBufferYouth(
			TransmissionRecord transmissionRecord, Transmission nextTransmission);
	
	/**
	 * Given a second-period buffer-case baseline youth's record, get that 
	 * youth's record from the directly previous reporting period.
	 * 
	 * No type checking is done on the returned record.
	 * 
	 * @param transmissionRecord record selected for comparison
	 * @param reportPeriod the report period preceding that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return record in previous report period with which to compare
	 *         or null if one wasn't found
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForPostBufferBaselineYouth(
	 *              TransmissionRecord)
	 * @see TransmissionRecordDAO#getPrecedingReportPeriod(ReportingPeriod)
	 */
	TransmissionRecord getPreviousRecordForPostBufferYouthComparison(
			TransmissionRecord transmissionRecord, ReportingPeriod reportPeriod, State state);
	
	/**
	 * Given a 19-year-old follow-up youth's record, get that youth's record from
	 * either the reporting period 2 years prior or 1.5 years prior---whichever
	 * has the baseline information.
	 * 
	 * @param transmissionRecord record selected for comparison
	 * @param previousOutcomesReportPeriod the outcomes collection reporting period 2 years
	 *        prior to that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return record in previous report period with which to compare
	 *         or null if one wasn't found
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForFollowup19Youth(TransmissionRecord)
	 */
	TransmissionRecord getBaselineRecordForFollowup19YouthComparison(
			TransmissionRecord transmissionRecord, ReportingPeriod previousOutcomesReportPeriod, State state);
	
	/**
	 * Given a 21-year-old follow-up youth's record, get that youth's
	 * follow-up-19 record from the report period 2 years prior.
	 * 
	 * @param transmissionRecord record selected for comparison
	 * @param previousOutcomesReportPeriod the outcomes collection reporting period 2 years
	 *        prior to that of the file selected for comparison
	 * @param state state whose file is being compared
	 * @return record in previous report period with which to compare
	 *         or null if one wasn't found
	 * @see TransmissionRecordDAO#getCrossReportPeriodAdvisoriesForFollowup21Youth(TransmissionRecord)
	 */
	TransmissionRecord getFollowup19RecordForFollowup21YouthComparison(
			TransmissionRecord transmissionRecord, ReportingPeriod previousOutcomesReportPeriod, State state);
}
