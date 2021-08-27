/**
 * Filename: PopulateSearchCriteriaService.java
 * 
 * Copyright 2009, ICF International
 * Created: Jun 18, 2009
 * Author: 15178
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 15178
 */
public interface PopulateSearchCriteriaService extends BaseService
{
	List<Lookup> getComplianceStatus();
	
	List<Lookup> getSamplingRequestStatus();

	List<ReportingPeriod> getReportingPeriodList();

	List<TransmissionType> getTransmissionTypeList();

	//List<ComplianceCategory> getComplianceAndQuality();

	List<Element> getElementNumbers();

	LinkedHashMap<String, String> getViewResultsList();

	List<State> getStatesList();

	/**
	 * Gets the states available to a given site user.
	 * 
	 * @param siteUser
	 *           site user for which to get available states
	 * @return states available to the given site user
	 */
	List<State> getStatesForUser(SiteUser siteUser);

	/**
	 * Gets a map of state ids and names available to a given site user.
	 * 
	 * @param siteUser
	 *           site user for which to get available states
	 * @return map of state ids and names available to the given site
	 *         user
	 */
	Map<String, String> getStateSelectMapForUser(SiteUser siteUser);

	/**
	 * Gets a map of transmission ids and federal file ids available to a given
	 * site user
	 * 
	 * @param siteUser
	 *           site user for which to get available transmissions
	 * @return a map of transmission ids and federal file ids available to the
	 *         given site user
	 */
	Map<String, String> getTransmissionSelectMap(SiteUser siteUser);

	/**
	 * Gets a map of transmission ids and federal file ids available to a given
	 * site user
	 * 
	 * @param siteUser
	 *           site user for which to get available transmissions
	 * @param stateId
	 *           id of state for which to return submission
	 * @param reportPeriodId
	 *           id of report period for which to return submission
	 * @return a map of transmission ids and federal file ids available to the
	 *         given site user
	 */
	Map<String, String> getTransmissionSelectMap(SiteUser siteUser, Long stateId, Long reportPeriodId);

	/**
	 * Returns number of transmissions/submissions in a given report period for a
	 * given state.
	 * 
	 * @param siteUser
	 *           current user
	 * @param stateId
	 *           id of state
	 * @param reportPeriodId
	 *           id of report period
	 * @return number of transmissions/submissions
	 */
	Long getTransmissionCount(SiteUser siteUser, Long stateId, Long reportPeriodId);

	/**
	 * Gets a map of past reporting period ids and names
	 * 
	 * @return a map of past reporting period ids and names
	 */
	Map<String, String> getReportPeriodSelectMap();

	/**
	 * Returns map containing element ids and number/names.
	 * 
	 * @return map containing element ids and number/names
	 */
	Map<String, String> getElementSelectMap();

	/**
	 * Gets a map of reporting period ids and names of those containing data
	 * 
	 * @return a map of past reporting period ids and names
	 */
	Map<String, String> getReportPeriodSelectMapForUser(SiteUser siteUser, Integer numFiles);

	/**
	 * Gets a map of reporting period ids and names of those containing data
	 * 
	 * @return a map of past reporting period ids and names
	 */
	Map<String, String> getReportPeriodSelectMapForUser(SiteUser siteUser);
	
	/**
	 * Gets a map of Fiscal years from report period striping A or B
	 * 
	 * @return a map of Fiscal years
	 */
	Map<String, String> getFiscalYearSelectMapForUser(SiteUser siteUser);
	
	/**
	 * Gets a map of reporting period descriptions 
	 * 
	 * @return a map of reporting period descriptions 
	 */
	Map<String, String> getReportPeriodDescSelectMap();
	
	/**
	 * Gets a map of population type 
	 * 
	 * @return a map of population type
	 */
	Map<String, String> getPopulationTypeSelectMap();
	
}
