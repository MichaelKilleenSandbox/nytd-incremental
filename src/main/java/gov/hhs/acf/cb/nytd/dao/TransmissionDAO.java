package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;

import java.util.List;
import java.util.Map;


/**
 * Interface for Transmission data access object
 * @author 19714
 */
public interface TransmissionDAO {
    /**
     * Gets list of Transmission objects that are active ("current") submissions for a given state
     * 
     * @param state state whose submissions are to be returned
     * @return list of Transmission objects that are active submissions
     */
    List<Transmission> getActiveSubmittedTransmissions(State state);

    /**
     * Gets list of Transmission objects that are active ("current") submissions for a given state and reporting period.
    * @param state state whose submissions are to be returned
    * @param reportingPeriod
    * @return list of Transmission objects that are active submissions
     */
    List<Transmission> getActiveSubmittedTransmissions(State state, Long reportingPeriod);
	
    /**
     * Gets a transmission given its database identifier.
     * 
     * @param id database identifier of the transmission to get
     * @return transmission with given database identifier
     */
    Transmission getTransmissionWithId(Long id);
	
	
    /**
     * Returns map containing transmission ids and "file numbers"
     * corresponding to given state and report period
     * 
     * @param stateId id of state for which to return transmissions
     * @param reportPeriodId id of report period for which to return transmissions
     * @return map containing transmission ids and "file numbers"
     */
    Map<String, String> getTransmissionSelectMap(Long stateId, Long reportPeriodId);
	
    /**
     * Returns map containing transmission ids and "file numbers"
     * corresponding to given site user
     * 
     * @param siteUser site user for which to return transmissions
     * @return map containing transmission ids and "file numbers"
     */
    Map<String, String> getTransmissionSelectMap(SiteUser siteUser);
	
    /**
     * Returns number of transmissions in a given report period for a given state.
     * 
     * @param stateId id of state
     * @param reportPeriodId id of report period
     * @param onlySubmissions whether or not to count only submissions
     * @return number of transmissions
     */
    Long getTransmissionCount(Long stateId, Long reportPeriodId, Boolean onlySubmissions);

    /**
     * Delete all transmissions.
     */
    void deleteAll();

    /**
     * Return transmission with highest id.
     * @return transmission with highest id
     */
    Transmission withHighestId();
	
    /**
     * To update transmission's processingStatus
     * before invoking rules engine
     * Return Transmission
     * @param transmisssion
     * @return
     */
    Transmission updateTransmissionProcessingStatus(Transmission transmisssion);

    /**
    * To update transmission
    * @param transmission as Transmission object
    */
    void updateTransmission(Transmission transmission) ;

    /**
     * Get submission status (Active/Inactive) for a single transmission
     * @param transmissionId
     * @return
     */
    public String getSubmissionStatus(Long transmissionId);

    /**
     * Get transmission type (Regular/Corrected/Subsequent/Test) for a single transmission
     * @param transmissionId
     * @return
     */
    public String getTransmissionType(Long transmissionId);

    /**
     * Get compliance status (Compliant/Non-Compliant) for a single transmission
     * @return Compliant or non-compliant
     */
    public String getComplianceStatus(Long transmissionId);


}