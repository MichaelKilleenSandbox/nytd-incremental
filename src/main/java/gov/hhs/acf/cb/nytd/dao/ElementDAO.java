package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.Element;

import java.util.Map;

public interface ElementDAO
{
	/**
	 * Returns map containing element ids and number/names.
	 * @return map containing element ids and number/names
	 */
	Map<String, String> getElementSelectMap();
	
	/**
	 * Returns map containing element ids and number/names appropriate for frequency analysis.
	 * @return map containing element ids and number/names appropriate for frequency analysis
	 */
	Map<String, String> getElementSelectMapForFrequencies();
	
	/**
	 * Returns element given an element name.
	 * @param name name of the element to get
	 * @return element
	 */
	Element getElementByName(String name);
}
