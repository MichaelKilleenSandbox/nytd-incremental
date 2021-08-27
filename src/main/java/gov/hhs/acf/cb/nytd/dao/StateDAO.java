package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.State;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface StateDAO
{
	/**
	 * Find state by its abbreviation.
	 * @param stateAbbreviation
	 * @return State object.
	 */
	Optional<State> findStateByAbbreviation(String stateAbbreviation);

	/**
	 * Find State by its ID.
	 * @param stateId numeric id.
	 * @return State object.
	 */
	Optional<State> findStateById(Long stateId);

	/**
	 * Get name of state given an id.
	 * 
	 * @param stateId id of state
	 * @return name of state
	 */
	String getStateName(Long stateId);
	
	/**
	 * Returns map containing state ids and names.
	 * @return map containing state ids and names
	 */
	Map<String, String> getStateSelectMap();

	/**
	 * Returns map with state names and abbreviations
	 * @return map with name key and abbreviation value
	 */
	Map<String, String> getStateAbbrevMap();

	/**
	 * Returns states in a given region.
	 * @param region region for which to return states
	 * @return states in given region
	 */
	List<State> getStatesInRegion(Long regionId);
	
	/**
	 * Get name of state abbreviation given state name.
	 * 
	 * @param stateName of state
	 * @return abbreviation of state name
	 */
	String getStateAbbr(String stateName);
        
	/**
	 * Get state id by state abbreviation.
	 * 
	 * @param abbreviation state abbreviation
	 * @return id of state
	 */
	Long getStateIdByAbbr(String abbreviation);
}
