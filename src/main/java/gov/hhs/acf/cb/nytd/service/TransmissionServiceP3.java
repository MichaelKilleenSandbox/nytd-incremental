package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.report.CohortSearch;
import gov.hhs.acf.cb.nytd.actions.report.OutcomesReportSearch;
import gov.hhs.acf.cb.nytd.actions.transmission.TransmissionSearch;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Service for transmission. 
 * User: 13873 
 * Date: Jun 1, 2010
 */
public interface TransmissionServiceP3 extends BaseService {
    
    /**
     * Delete a transmission by id.
     *
     * @param transmissionId transmission id 
     * @param siteUser user
     */
    void deleteTransmission(Long transmissionId, SiteUser siteUser);

    /**
     * Delete a transmission by Transmission object.
     *
     * @param transmission transmission
     * @param siteUser user
     */
    void deleteTransmission(Transmission transmission, SiteUser siteUser);

    /**
     * Get baseline youth count by transmission id.
     *
     * @param transmissionId transmission id
     * @return count as Integer
     */
    Integer getCountBaselineYouth(Long transmissionId);

    /**
     * Get followup youth count by transmission id.
     *
     * @param transmissionId transmission id
     * @return count as Integer
     */
    Integer getCountFollowupYouth(Long transmissionId);

    /**
     * Get served youth count by transmission id.
     *
     * @param transmissionId transmission id
     * @return count as Integer
     */
    Integer getCountServedYouth(Long transmissionId);

    /**
     * Get total record count by transmission id.
     *
     * @param transmissionId transmission id
     * @return count as Integer
     */
    Integer getCountTotalRecords(Long transmissionId);

    /**
     * Get current reporting period.
     *
     * @return ReportingPeriod object
     */
    ReportingPeriod getCurrentReportingPeriod();

    /**
     * Get current reporting period for state which may vary by 
     * extended due date.
     *
     * @param stateId Long state id
     * @param fileType String Regular or Corrected
     * @param reportingPeriodId Long reporting period id
     * @return ReportingPeriod object
     */
    ReportingPeriod getCurrentReportingPeriodForState(Long stateId, String fileType, Long reportingPeriodId);
            
    /**
     * Get dashboard transmissions.
     *
     * @param search TransmissionSearch object
     * @return TransmissionSearch object
     */
    TransmissionSearch getDashboardTransmissions(TransmissionSearch search);

    /**
     * Get datum notes.
     *
     * @param search TransmissionSearch object
     * @return TransmissionSearch object
     */
    TransmissionSearch getDatumNotes(TransmissionSearch search);

    /**
     * Get element notes.
     *
     * @param search TransmissionSearch object
     * @return TransmissionSearch object
     */
    TransmissionSearch getElementNotes(TransmissionSearch search);

    /**
     * Get record notes.
     *
     * @param search TransmissionSearch object
     * @return TransmissionSearch object
     */
    TransmissionSearch getRecordNotes(TransmissionSearch search);

    /**
     * Get submission.
     *
     * @param state State object
     * @param period ReportingPeriod object
     * @return Transmission object
     */
    Transmission getSubmission(State state, ReportingPeriod period);

    /**
     * Get submission deadline.
     *
     * @param period ReportingPeriod object
     * @return Calendar object
     */
    Calendar getSubmissionDeadline(ReportingPeriod period);

    /**
     * Get submission deadline by transmission type.
     *
     * @param period ReportingPeriod object
     * @param transmissionType type of transmission as Long
     * @return Calendar object
     */
    Calendar getSubmissionDeadline(ReportingPeriod period, Long transmissionType);

    /**
     * Get submission deadline by transmission type.
     *
     * @param transmissionId transmission id
     * @return Transmission object
     */
    Transmission getTransmission(Long transmissionId);

    /**
     * Get list of transmission id.
     *
     * @return id list as Long
     */
    List<Long> getTransmissionIds();

    /**
     * Get transmission id of inactive status.
     *
     * @param stateId state id
     * @param reportingPeriodId reporting period id
     * @return id as Long
     */
    Long getTransmissionIdOfInactiveStatus(Long stateId, Long reportingPeriodId);

    /**
     * Get state id by state name.
     *
     * @param stateId state id
     * @param stateName as String
     * @return state id as Long
     */
    Long getStateId(String stateName);

    /**
     * Get transmission note by transmission id.
     *
     * @param transmissionId transmission id
     * @return list of TransmissionNote object
     */
    List<TransmissionNote> getTransmissionNotes(Long transmissionId);

    /**
     * Get youth record.
     *
     * @param transmissionId transmission id
     * @param recordNumber record number as String
     * @param datumId datum id
     * @return TransmissionRecord object
     */
    TransmissionRecord getYouthRecord(Long transmissionId, String recordNumber, Long datumId);

    /**
     * Check authorization by user and transmission id.
     *
     * @param user SiteUser object
     * @param transmissionId transmission id
     * @return true or false
     */
    boolean isAuthorized(SiteUser user, Long transmissionId);

    /**
     * Search Transmissions.
     *
     * @param search as TransmissionSearch object
     * @param user as SiteUser
     * @return TransmissionSearch object
     */
    TransmissionSearch searchTransmissions(TransmissionSearch search, SiteUser user);

    /**
     * Process submission reminder.
     *
     */
    void submissionReminder();

    /**
     * Submit Transmissions.
     *
     * @param transmission as Transmission
     * @param stateUser as SiteUser
     * @param ignoreWarning as Boolean object
     * @throws SubmissionException
     * @return TransmissionSearch object
     */
    Transmission submitTransmission(Transmission transmission, SiteUser stateUser, Boolean ignoreWarning) throws SubmissionException;

    /**
     * Get state by state id.
     *
     * @param stateId state id
     * @return state as State object
     */
    State getState(Long stateId);

    /**
     * Get reporting period by reporting period id.
     *
     * @param reportingPeriodId reporting period id
     * @return reporting period as ReportingPeriod object
     */
    ReportingPeriod getReportingPeriod(Long reportingPeriodId);

    /**
     * Get failed transmissions report.
     *
     * @param searchCriteria as a Map
     * @return list of FailedTransmissionDetail object
     */
    List<FailedTransmissionDetail> getFailedTransmissionsReport(Map<String, String> searchCriteria);

    /**
     * Get baseline cohort record to export.
     *
     * @param cohortSearch as a CohortSearch object
     * @return a record to export as RecordToExport object
     */
    RecordToExport getBaselineCohortRecord(CohortSearch cohortSearch);
    
    /**
     * Get cohort 19 record to export.
     *
     * @param cohortSearch as a CohortSearch object
     * @return a record to export as RecordToExport object
     */
    RecordToExport getCohortSet19Record(CohortSearch cohortSearch);
    
    /**
     * Get outcomes universe record that are not reported.
     *
     * @param search as a OutcomesReportSearch object
     * @return a record to export as RecordToExport object
     */
    RecordToExport getOutcomesUniverseNotReportedRecord(OutcomesReportSearch search);
    
    /**
     * Get due date of transmission by transmission id.
     *
     * @param transmissionid transmission id
     * @return due date as String
     */
    String getDueDateofTransmission(Long transmissionid);
    
    /**
     * Check if cohort is sampled by transmission id.
     *
     * @param transmissionid transmission id
     * @return true or false
     */
    boolean isCohortSampled(Long transmissionid);
    
    /**
     * Check if a transmission is within regular report period.
     *
     * @param transmissionid transmission id
     * @return String
     */
    String isWithinRegularReportPeriod(Long transmissionid);
    
    /**
     * Get penalty amount for inactive regular file.
     *
     * @param correctedFileTransmissionid transmission id of the corrected file
     * @param stateid state id
     * @param reportingperiodid reporting period id
     * @return penalty amount by Float object
     */
    Float getPenaltyAmtForInactiveRegularFile(Long correctedFileTransmissionid, Long stateid, Long reportingperiodid);

    /**
     * Get element name by id.
     *
     * @param elementId element id
     * @return element name as String
     */
    String getElementNameById(Integer elementId);
         
    /**
    * Update transmission in database.
    *
    * @param transmission
    */
    void updateTransmission(Transmission transmission);
    
    /**
    * Get unprocessed directory.
    *
    * @return directory as File
    */
    File getUnprocessedDir();
    
    /**
    * Get toprocess directory.
    *
    * @return directory as File
    */
    File getToprocessDir();

    boolean isTransmissionActive(String transmissionId);

    String getTransmissionType(String transmissionId);

    boolean isCompliant(String transmissionId);

    boolean stateHasActiveTransmission(State state);

    boolean stateHasActiveTransmission(State state, Long reportingPeriod);


    
    
}
