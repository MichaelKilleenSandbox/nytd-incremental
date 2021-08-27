package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;

import java.util.List;
import java.util.Map;


public interface SubmissionDAO
{
	/**
	 * Returns map containing submission ids and "submission reference numbers"
	 * corresponding to given state and report period
	 * 
	 * @param stateId id of state for which to return submission
	 * @param reportPeriodId id of report period for which to return submission
	 * @return map containing submission ids and "submission reference numbers"
	 */
	Map<String, String> getSubmissionSelectMap(Long stateId, Long reportPeriodId);
	
	/**
	 * Returns the database identifier of the active/current submision given
	 * a state and report period 
	 * 
	 * @param stateId id of state for which to return active submission
	 * @param reportPeriodId id of report period for which to return submission
	 * @return id of active/current submission
	 */
	Long getActiveSubmissionId(Long stateId, Long reportPeriodId);
	
	/**
	 * Gets database identifiers of active ("current") submissions for a given state.
	 * 
	 * @param state state whose submission ids are to be returned
	 * @return list of active submissions' database identifiers
	 */
	List<Long> getActiveSubmissionIds(State state);
	
	/**
	 * Gets active ("current") submissions for a given state.
	 * 
	 * @param state state whose submissions are to be returned
	 * @return list of active submissions
	 */
	List<Transmission> getActiveSubmissions(State state);
	
	/**
	 * Gets the active submission of a given state and report period
	 * 
	 * @param state state for which to return the active submission
	 * @param reportPeriod report period for which to find the federal file ID of the active submission
	 * @return the active submission of the given state and report period
	 */
	Transmission getSubmissionForReportPeriod(State state, ReportingPeriod reportPeriod);
	
	/**
	 * Gets the file number of the active submission of a given report period
	 * 
	 * @param state state for which to find the file number
	 * @param reportPeriod report period for which to find the file number of the active submission
	 * @return the file number of the active submission of the given report period
	 */
	String getSubmissionFileNumberOfReportPeriod(State state, ReportingPeriod reportPeriod);
}
