package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.helper.Frequency;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Handles operations related to the NYTD frequency reports.
 * 
 * @author Adam Russell (18816)
 */
public interface FrequencyService extends BaseService
{
	/**
	 * Gets a frequency table. Note that no parameters are aggregated.
	 * 
	 * @param states ids of states on which to run the frequency analysis
	 * @param reportPeriods ids of report periods on which to run the frequency analysis
	 * @param elements ids of elements on which to run the frequency analysis
	 * @param byState sort primarily by state
	 * @param byReportPeriod sort primarily by report period (defers to byState)
	 * @param byElement sort primarily by element (defers to byReportPeriod)
	 * @return frequency table
	 */
	List<Frequency> getFrequencies(Collection<String> states,
	                               Collection<String> reportPeriods,
	                               Collection<String> elements,
	                               Boolean byState,
	                               Boolean byReportPeriod,
	                               Boolean byElement);
	
	/**
	 * Returns map containing element ids and number/names.
	 * 
	 * @return map containing element ids and number/names
	 */
	Map<String, String> getElementSelectMap();
}
