/**
 * Filename: DataExtractionService.java
 *
 *  Copyright 2009, ICF International
 *  Created: Aug 12, 2009
 *  Author: 18816
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.report.ComplianceStandardsReport;
import gov.hhs.acf.cb.nytd.actions.report.SubmissionStatisticsReportHelper;
import gov.hhs.acf.cb.nytd.models.Element;
import gov.hhs.acf.cb.nytd.models.ExportMetadata;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.DataTable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Provides functionality needed for extracting data from the database.
 *
 * The data is able to be exported into multiple file formats.
 *
 * @author Adam Russell (18816)
 */
public interface DataExtractionService extends BaseService
{
	/**
	 * Compiles a set of data into a data table given certain criteria.
	 *
	 * @param dataTable empty DataTable object to be filled; must already contain the desired fields
	 * @param reportingPeriods IDs of reporting periods of the data to be included
	 * @param states IDs of states whose data is to be included
	 * @param populations IDs of populations whose data is to be included
	 * @return
	 */
	DataTable compileData(DataTable dataTable,
	                      Collection<Long> reportingPeriods,
	                      Collection<Long> states,
	                      Collection<Long> populations,
	                      Collection<Long> cohorts,
	                      int pageNumber,
	                      int pageSize);
	
	int dataCount(DataTable dataTable,
         Collection<Long> reportingPeriods,
         Collection<Long> states,
         Collection<Long> populations,
         Collection<Long> cohorts);
        
        /**
	 * Data count for Penalty Letters.
	 *
	 * @param dataTable DataTable object populated with fields and data to be exported
         * @param reportingPeriods Collection of report period ids
         * @param states Collection of state ids
	 * @return int Data count
	 */
        int dataCountForPenaltyLetters(DataTable dataTable, Collection<Long> reportingPeriods, Collection<Long> states);

	/**
	 * Generates an SPSS data file given a data table.
	 *
	 * @param dataTable DataTable object populated with fields and data to be exported
	 * @return input stream containing the SAV
	 */
	InputStream getSPSSFile(final DataTable dataTable,boolean startofRecs, boolean endofRecs);

	/**
	 * Generates a ReStructuredText file given a data table.
	 *
	 * For more information on ReStructuredText, see:
	 * http://docutils.sourceforge.net/rst.html
	 *
	 * @param dataTable DataTable object populated with fields and data to be exported
	 * @return input stream containing the RST
	 */
	InputStream getRSTFile(final DataTable dataTable,boolean startofRecs, boolean endofRecs);

	/**
	 * Generates a CSV file given a data table.
	 *
	 * @param dataTable DataTable object populated with fields and data to be exported
	 * @return input stream containing the CSV
	 */
	InputStream getCSVFile(final DataTable dataTable,boolean startofRecs, boolean endofRecs);
	
	
	/**
	 * Generates an XHTML file given a data table.
	 *
	 * @param dataTable DataTable object populated with fields and data to be exported
	 * @return input stream containing the XHTML
	 */
	InputStream getHTMFile(final DataTable dataTable,boolean startofRecs, boolean endofRecs);

	/**
	 * Generates an Excel file given a data table.
	 *
	 * @param dataTable DataTable object populated with fields and data to be exported
	 * @return input stream containing the XLS
	 */
	InputStream getXLSFile(final DataTable dataTable,boolean startofRecs, boolean endofRecs);

	/**
	 * Gets file types able to be generated.
	 *
	 * @return map of formatted file extensions and numeric keys
	 */
	Map<String, String> getFileTypes();

	/**
	 * Gets reporting periods in the system.
	 *
	 * @param siteUser site user for which to return report periods
	 * @return map of reporting periods and their IDs
	 */
	Map<String, String> getReportingPeriods(SiteUser siteUser);

	/**
	 * Gets states in the system.
	 *
	 * @param siteUser site user for which to return states
	 * @return map of states and their IDs
	 */
	Map<String, String> getStates(SiteUser siteUser);

	/**
	 * Gets possible populations of a record.
	 *
	 * The method will return the populations included in the database,
	 * appended with an "Other" entry because the NYTD system may not be able
	 * to determine the population of all NYTD records. The key of this
	 * "Other" record will be one higher than the highest identifier in the
	 * Population table.
	 *
	 * @return map of populations and their IDs
	 */
	Map<String, String> getPopulations();

	/**
	 * Gets the general record metadata/information fields not contained in the NYTD elements.
	 *
	 * @return Appropriate map of general record metadata and numerical keys
	 */
	Map<String, String> getGeneralFields();

	/**
	 * Gets the elements classified as demographics.
	 *
	 * @return Appropriate map of formatted field descriptions and their element numbers
	 */
	Map<String, String> getDemographics();

	/**
	 * Gets the elements classified as characteristics.
	 *
	 * @return Appropriate map of formatted field descriptions and their element numbers
	 */
	Map<String, String> getCharacteristics();

	/**
	 * Gets the elements classified as independent living services.
	 *
	 * @return Appropriate map of formatted field descriptions and their element numbers
	 */
	Map<String, String> getServices();

	/**
	 * Gets the elements classified as youth outcome survey.
	 *
	 * @return Appropriate map of formatted field descriptions and their element numbers
	 */
	Map<String, String> getOutcomes();

	/**
	 * Gets a short name for an element suitable for use as an SPSS variable name.
	 *
	 * @param elementNumber element number
	 * @return short field name for element
	 */
	String getShortElementName(String elementNumber);

	/**
	 * Gets a formatted description for an element suitable for use as an SPSS variable label.
	 *
	 * @param elementNumber element number
	 * @return short field name for element
	 */
	String getElementLabel(String elementNumber);

	/**
	 * Gets a short name for an element's data-level notes suitable for use as an SPSS variable name.
	 *
	 * @param elementNumber element number
	 * @return short field name for notes of element data
	 */
	String getShortNoteName(String elementNumber);

	/**
	 * Gets a formatted description for an element's data-level notes suitable for use as an SPSS variable label.
	 *
	 * @param elementNumber element number
	 * @return short field name for notes of element data
	 */
	String getNoteLabel(String elementNumber);

	/**
	 * Gets a short name for a general/non-element field suitable for use as an SPSS variable name.
	 *
	 * @param generalFieldKey key of the general field from the general field map
	 * @return short field name
	 * @see DataExtractionService#getGeneralFields()
	 */
	String getShortGeneralFieldName(String generalFieldKey);

	/**
	 * Gets a formatted description for a general/non-element field suitable for use as an SPSS variable label.
	 *
	 * @param generalFieldKey key of the generic field in the generic field map
	 * @return full field name
	 * @see DataExtractionService#getGeneralFields()
	 */
	String getFullGeneralFieldName(String generalFieldKey);

	/**
	 * Saves export metadata to the database.
	 *
	 * @param exportCriteria map containing export criteria
	 */
	void writeExportMetadata(Map exportCriteria);

	public List<ExportMetadata> getPreviousDataExports();

	public void deleteExportMetadata(String fileName);
	
	/**
	 * @param reportPeriod
	 * @param sortBy
	 * @param sortOrder
	 * @return
	 */
	public SubmissionStatisticsReportHelper submissionStatisticsReport(Long reportPeriod, String sortBy, String sortOrder, boolean activeSubmissionFlag);

   /**
    * @param selectedStates
    * @param selectedReportingPeriods
    * @param groupBy
    */
   public List<ComplianceStandardsReport> complianceStandardsReport(List<String> selectedStates, List<String> selectedReportingPeriods, String groupBy);
	/**
	 * Gets a map of all the element number and descriptions.
	 *
	 * @return map of element numbers (keys) and descriptions (values)
	 */
	Map<String, String> getFields();
	
	public void setElementsList(ArrayList<Element> elementsList);
	
	public Map<String, String> getCohorts();
	
}
