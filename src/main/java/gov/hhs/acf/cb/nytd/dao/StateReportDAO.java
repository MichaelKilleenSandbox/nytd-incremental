package gov.hhs.acf.cb.nytd.dao;

import java.util.Map;


public interface StateReportDAO
{
	
	/**
	 * Returns map containing fiscal years of those in state report table.
	 * 
	 * @return map containing fiscal years
	 */
	Map<String, String> getReportYearSelectMap();
	
	/**
	 * Returns map containing Population Type of those in state report table.
	 * 
	 * @return map containing Population Type
	 */
	Map<String, String> getPopulationTypeSelectMap();
}
