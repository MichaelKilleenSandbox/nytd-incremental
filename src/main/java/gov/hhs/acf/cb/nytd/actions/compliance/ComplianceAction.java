/**
 * Filename: RecordLevleErrorAction.java
 * 
 * Copyright 2009, ICF International Created: May 5, 2009 Author: 16939
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
package gov.hhs.acf.cb.nytd.actions.compliance;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author 13873
 *
 */
public class ComplianceAction extends SearchAction<ComplianceSearch>
{
	// Logger
	protected Logger log = Logger.getLogger(getClass());

	// Search object (only required parameter is transmissionId)
	// query string parameter for autowiring is search.transmissionId
	private ComplianceSearch search;

	// Services
	private ComplianceService complianceService;
	private DataExtractionService dataExtractionService;
	private TransmissionServiceP3 transmissionServiceP3;

	@Override
	protected ComplianceSearch getPaginatedSearch()
	{
		return getSearch();
	}

	/**
	 * Property getter for compliance search object
	 *
	 * @return search object
	 */
	public ComplianceSearch getSearch()
	{
		return search;
	}

	/**
	 * Property setter for compliance search object
	 *
	 * @param search
	 *           search object
	 */
	public void setSearch(ComplianceSearch search)
	{
		this.search = search;
	}

	// ------------------------------------------------------------------------------------------------------------------
	// SERVICE getter/setter methods (autowired by Spring)
	// ------------------------------------------------------------------------------------------------------------------

	public ComplianceService getComplianceService()
	{
		return complianceService;
	}

	public void setComplianceService(ComplianceService complianceService)
	{
		this.complianceService = complianceService;
	}

	public DataExtractionService getDataExtractionService()
	{
		return dataExtractionService;
	}

	public void setDataExtractionService(DataExtractionService dataExtractionService)
	{
		this.dataExtractionService = dataExtractionService;
	}

	public TransmissionServiceP3 getTransmissionServiceP3()
	{
		return transmissionServiceP3;
	}

	public void setTransmissionServiceP3(TransmissionServiceP3 transmissionServiceP3)
	{
		this.transmissionServiceP3 = transmissionServiceP3;
	}

	// ------------------------------------------------------------------------------------------------------------------
	// ACTION METHODS (mapped in struts.xml)
	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * Action method for aggregate-level compliance
	 *
	 * @return
	 */
	public final String aggregateErrors()
	{
		// search data aggregates
		getComplianceService().searchDataAggregates(search);

		// display the result
		return Action.SUCCESS;
	}

	public final String clearRecordSearch()
	{
		search.reset();
		search.setNonPenaltyCategories(getComplianceService().getNonPenaltyCategories());
		getComplianceService().searchRecordErrors(search);
		return Action.SUCCESS;
	}

	/**
	 * Action method for element-level data quality advisories
	 *
	 * @return
	 */
	public final String elementLeveldataQualityAdvisories()
	{
	// search element level data quality advisories
		getComplianceService().searchElementLevelAdvisories(search);

		// display the result
		return Action.SUCCESS;
	}

	public final String clearRecordLevelDQA()
	{
		search.setRecordNumbers(null);
		search.setElementNumber(null);
		search.setPageSize(25);

	// search data quality aggregates
		getComplianceService().searchRecordLevelAdvisories(search);

		return Action.SUCCESS;
	}

	public final String recordLeveldataQualityAdvisories()
	{
		// search data quality aggregates
		getComplianceService().searchRecordLevelAdvisories(search);

		// display the result
		return Action.SUCCESS;
	}

	public final String exportAggregateErrors()
	{
		// get the list of results to export
		getComplianceService().searchDataAggregates(search);

		// create the exporter
		AggregateErrorExport exporter = new AggregateErrorExport(this, dataExtractionService);

		// export the data to csv
		return exporter.export(getServletResponse(), search.getPageResults(), "aggregateLevelCompliance_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	public String exportDataQualityAdvisories()
	{
		// get the list of results to export
		getComplianceService().searchElementLevelAdvisories(search);
		// create the exporter
		ElementLevelDQAExport exporter = new ElementLevelDQAExport(this, dataExtractionService);

		// export the csv
		return exporter.export(getServletResponse(), search.getPageResults(), "ElementLevelDataQualityAdvisories_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	public String exportRecordLevelDataQualityAdvisories()
	{
		// get the list of results to export
			getComplianceService().searchRecordLevelAdvisories(search);
		// create the exporter
			RecordLevelDQAExport exporter = new RecordLevelDQAExport(this, dataExtractionService);

		// export the csv
		return exporter.export(getServletResponse(), search.getPageResults(), "RecordLevelDataQualityAdvisories_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	public final String exportRecordErrors()
	{
		// get the list of results to export
		getComplianceService().searchRecordErrors(search);

		// create the exporter
		RecordErrorExport exporter = new RecordErrorExport(this, dataExtractionService);

		// export the data to csv
		return exporter.export(getServletResponse(), search.getPageResults(), "recordLevelCompliance_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	public String exportTransmissionErrors()
	{
		// get the list of results to export
		getComplianceService().searchTransmissionErrors(search);

		// create the exporter
		TransmissionErrorExport exporter = new TransmissionErrorExport(this, dataExtractionService);

		// export the data to csv
		return exporter.export(getServletResponse(), search.getPageResults(), "transmissionLevelCompliance_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	/**
	 * Invoked prior to action execution by Struts2 interceptor. Call compliance
	 * service methods to calculate error counts displayed by
	 * complianceOverview.jsp
	 *
	 * @throws Exception
	 */
	public void prepare()
	{
		super.prepare();

		// need to read the transmission id from the request
		// in the prepare method. this method is called before
		// the action properties are set by the framework
		Long transmissionId = Long.parseLong(getServletRequest().getParameter("search.transmissionId"));

		// push a map of error counts on the value stack
		Map<String, Integer> errorCounts = new HashMap<>();
		List<ComplianceCategory> categories = null;

		// add count of aggregate errors
		categories = getComplianceService().getAggregateErrorCategories();
		errorCounts.put("aggregate", getComplianceService().getErrorCountForCategories(transmissionId,
				categories));

		// add count of record errors
		categories = getComplianceService().getRecordErrorCategories();
		errorCounts
				.put("record", getComplianceService().getErrorCountForCategories(transmissionId, categories));

		// add count of transmission errors
		categories = getComplianceService().getTransmissionErrorCategories();
		errorCounts.put("transmission", getComplianceService().getErrorCountForCategories(transmissionId,
				categories));

		// add count of data quality advisories
		List<Integer> dqaCnts = getComplianceService().getCountDataQualtiyAdvisories(transmissionId);
		// add count of element level dqa count
		errorCounts.put("elementLevelDQA", dqaCnts.get(0));

		// add count of record level dqa count
		errorCounts.put("recordLevelDQA", dqaCnts.get(1));

		// add to value stack for use in complianceOverview.jsp & dqaOverview.jsp
		ActionContext.getContext().getActionInvocation().getStack().set("errorCounts", errorCounts);
	}

	/**
	 * Action method for record-level compliance
	 *
	 * @return
	 */
	public final String recordErrors()
	{
		// load the non-penalty categories
		search.setNonPenaltyCategories(getComplianceService().getNonPenaltyCategories());
		search.setNonCompliancePageName("RECORD-LEVEL");
		search.setElementName(complianceService.getElementNameFromNumberInApplication(
				search.getElementNumber(),
				(List<Element>) application.get(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN)));

		int psize = getServletRequest().getParameter("search.pageSize") == null
			? 25
			: Integer.parseInt(getServletRequest().getParameter("search.pageSize"));
		List<String> l1 = null;
		if (getServletRequest().getParameterValues("search.recordNumbers") != null)
		{
			l1 = new ArrayList<>();
			l1.addAll(Arrays.asList(getServletRequest().getParameterValues("search.recordNumbers")));
		}
		search.setRecordNumbers(l1);
		search.setElementNumber(getServletRequest().getParameter("search.elementNumber"));
		search.setComplianceType(getServletRequest().getParameter("search.complianceType"));
		search.setPageSize(psize);

		// search record errors
		getComplianceService().searchRecordErrors(search);

		// display the result
		return Action.SUCCESS;
	}

	/**
	 * Action method for transmission-level compliance
	 *
	 * @return
	 */
	public final String transmissionErrors()
	{
		// search transmission errors
		getComplianceService().searchTransmissionErrors(search);

		// store errors in map keyed on compliance category name
		// and push the map on the value stack (simplifies jsp)
		Map<String, NytdError> errorMap = new HashMap<>();
		List<NytdError> transmissionErrors = search.getPageResults();
	     List<NytdError> FileFormatErrors = new ArrayList<>();
	     List<NytdError> timelyErrors = new ArrayList<>();
        for (NytdError error : transmissionErrors) {
      	  	if("File Format".equals(error.getComplianceCategory().getName()))
      	  	{
      	  		FileFormatErrors.add(error);
      	  	}
      	  	else if("Timely Data".equals(error.getComplianceCategory().getName()))
      	  	{
      	  		timelyErrors.add(error);
      	  	}

      	  		errorMap.put(error.getComplianceCategory().getName(), error);
          }

        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        stack.set("transmissionErrors", errorMap);
        stack.set("fileformatErrors", FileFormatErrors);
        stack.set("timelyErrors", timelyErrors);

        // display the result
		return Action.SUCCESS;
	}

	public final String youthRecord()
	{
		// load the record from the database
		TransmissionRecord record = this.getTransmissionServiceP3().getYouthRecord(search.getTransmissionId(),
				search.getRecordNumbers().get(0),search.getDatumId());

		Map<Integer, Datum> demographics = new TreeMap<>();
		Map<Integer, Datum> servedPopulation = new TreeMap<>();
		Map<Integer, Datum> outcomesPopulation = new TreeMap<>();

		// split record elements into appropriate categories
		for (Datum datum : record.getDatums()) {
			int elementNum = Integer.parseInt(datum.getElement().getName());
			// demographic
			if (elementNum <= 13)
				demographics.put(elementNum, datum);
			// served population
			else if (elementNum >= 14 && elementNum <= 33)
				servedPopulation.put(elementNum - 13, datum);
			// outcomes population
			else if (elementNum >= 34)
				outcomesPopulation.put(elementNum - 33, datum);
		}

		// list used to create a 3 column table of record elements
		// column 1 contains elements E1 - E13
		// column 2 contains elements E14 - E33
		// column 3 contains elements E34 - E58
		ArrayList<Datum> datums = new ArrayList<>();
		for (int i = 1; i <= 25; i++)
		{
			Integer key = i;

			Datum dd = demographics.get(key);
			if (dd != null)
				datums.add(dd);
			else
				datums.add(new Datum());

			Datum sp = servedPopulation.get(key);
			if (sp != null)
				datums.add(sp);
			else
				datums.add(new Datum());

			Datum op = outcomesPopulation.get(key);
			if (op != null)
				datums.add(op);
			else
				datums.add(new Datum());
		}

		// push the list on the value stack for use in youthRecord.jsp
		ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
		stack.set("youthRecordElements", datums);

		return Action.SUCCESS;
	}
}
