package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.actions.PaginatedSearch.SortDirection;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.Element;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import gov.hhs.acf.cb.nytd.util.ComplianceErrorEnum;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.*;

/**
 * Action to select a) report period for failed transmissions.
 * 
 * @author Nava Krishna Mallela (23839)
 */
public class ErrorTypeReportAction extends SearchAction<ErrorTypeSearch>
{
	@Getter
	@Setter
	private String submitButtonName;
	@Getter
	@Setter
	private ErrorTypeSearch search;
	@Getter
	@Setter
	private Map<String, String> searchCriteria;
	@Getter
	@Setter
	private String defaultReportPeriod;
	@Getter
	@Setter
	private String reportPeriod;
	@Getter
	@Setter
	private String reportPeriodName;
	@Getter
	@Setter
	private String state;
	@Getter
	@Setter
	private String fileType;
	@Getter
	@Setter
	private String startDate;
	@Getter
	@Setter
	private String endDate;
	@Getter
	@Setter
	private String sortColumn;
	@Getter
	@Setter
	private String sortOrder;
	@Getter
	@Setter
	private String prevSortColumn;
	@Getter
	@Setter
	private String prevSortOrder;
	@Getter
	@Setter
	private String freezeSort;
	@Getter
	@Setter
	private boolean defaultPage;

	@Getter
	@Setter
	private PopulateSearchCriteriaService populateSearchCriteriaService;
	@Getter
	@Setter
	private TransmissionServiceP3 transmissionServiceP3;
	@Getter
	@Setter
	private DataExtractionService dataExtractionService;
	@Getter
	@Setter
	private ComplianceService complianceService;
	@Getter
	@Setter
	private List<FailedTransmissionDetail> failedTransmissionsList;
	
	@Getter @Setter private boolean stateSort = false;
	@Getter @Setter private boolean reportPeriodSort = false;
	@Getter @Setter private boolean elementSort = false;
	
	private static final String DEFAULTSORTCOLUMN = "1";
	private static final String DEFAULTSORTORDER_ASC = "0";
	private static final String SORTORDER_DESC = "1";

	protected Logger log = Logger.getLogger(getClass());

	public void prepare()
	{
		super.prepare();

		// when using the tab navigation the search object is not created
		if (search == null)
		{
			search = new ErrorTypeSearch();
		}
		prepareSearchCriteria();
	}

	@Override
	protected ErrorTypeSearch getPaginatedSearch()
	{
		return getSearch();
	}

	/**
	 * Displays Report Periods Selection page.
	 * 
	 * @return Action.SUCCESS on success
	 */
	@SkipValidation
	public final String getErrorTypeSearchPage()
	{
		log.debug("ErrorTypeReport.getErrorTypeSearchPage");
		setDefaultPage(true);
		search.reset();
		setStateSort(false);
		setReportPeriodSort(false);
		setElementSort(false);
		prepareSearchCriteria();
		return SUCCESS;
	}

	public final String getDQACountsReport()
	{
		log.debug("FailedTransmissionsReport.getReport");
		setDefaultPage(false);
		prepareSearchCriteria();
		prepareOrderCriteria();
		if (getServletRequest().getParameter("printRequest") != null
				&& getServletRequest().getParameter("printRequest").equals("true"))
		{
			Iterator<String> itr = search.getSelectedReportingPeriods().iterator();
			if (itr != null && !search.getSelectedReportingPeriods().isEmpty())
			{
				String rpStr = itr.next();
				String[] reportPeriods = rpStr.split(",");
				search.getSelectedReportingPeriods().clear();
				for (String reportPeriod : reportPeriods)
				{
					if (!search.getSelectedReportingPeriods().contains(reportPeriod))
						search.getSelectedReportingPeriods().add(reportPeriod);
				}
			}

			itr = search.getSelectedStateName().iterator();
			if (itr != null && !search.getSelectedStateName().isEmpty())
			{
				String stateStr = itr.next();
				String[] stateNames = stateStr.split(",");
				search.getSelectedStateName().clear();
				for (String sttName : stateNames)
				{
					search.getSelectedStateName().add(sttName);
				}
			}

			itr = search.getSelectedElementNumbers().iterator();
			if (itr != null && !search.getSelectedElementNumbers().isEmpty())
			{
				String elementNumberStr = itr.next();
				String[] elementNumbers = elementNumberStr.split(",");
				search.getSelectedElementNumbers().clear();
				for (String elemNums : elementNumbers)
				{
					search.getSelectedElementNumbers().add(elemNums);
				}
			}
			/*
			 * itr = search.getSelectedErrorTypes().iterator();
			 * getServletRequest().getParameterValues("search.selectedErrorTypes");
			 * if(itr != null && !search.getSelectedErrorTypes().isEmpty()) {
			 * String errorStr = itr.next(); String[] errorTypes =
			 * errorStr.split(","); search.getSelectedErrorTypes().clear();
			 * for(String errorType : errorTypes) {
			 * search.getSelectedErrorTypes().add(errorType); } }
			 */

		}

		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			Iterator<String> itr = search.getSelectedElementNumbers().iterator();
			search.getSelectedElemNums().clear();
			String elemNum = null;
			while (itr.hasNext())
			{
				elemNum = itr.next();
				search.getSelectedElemNums().add(search.getElementNumberMap().get(elemNum).toString());
			}
		}
		
		search.setRowCount(complianceService.getDQAsCount(search));
		search.setPageResults(complianceService.getDQACountsReport(search));
		return SUCCESS;

	}

	/**
	 * Displays Failed Transmission Report page
	 * 
	 * @return Action.SUCCESS on success
	 */

	public final String getNonComplianceCountsReport()
	{

		log.debug("ErrorTypeReportAction.getNonComplianceCountsReport");
		setDefaultPage(true);
		prepareSearchCriteria();
		prepareOrderCriteria();
		if (getServletRequest().getParameter("printRequest") != null
				&& getServletRequest().getParameter("printRequest").equals("true"))
		{
			Iterator<String> itr = search.getSelectedReportingPeriods().iterator();
			if (itr != null && !search.getSelectedReportingPeriods().isEmpty())
			{
				String rpStr = itr.next();
				String[] reportPeriods = rpStr.split(",");
				search.getSelectedReportingPeriods().clear();
				for (String reportPeriod : reportPeriods)
				{
					if (!search.getSelectedReportingPeriods().contains(reportPeriod))
						search.getSelectedReportingPeriods().add(reportPeriod);
				}
			}

			itr = search.getSelectedStateName().iterator();
			if (itr != null && !search.getSelectedStateName().isEmpty())
			{
				String stateStr = itr.next();
				String[] stateNames = stateStr.split(",");
				search.getSelectedStateName().clear();
				for (String sttName : stateNames)
				{
					search.getSelectedStateName().add(sttName);
				}
			}

			itr = search.getSelectedElementNumbers().iterator();
			if (itr != null && !search.getSelectedElementNumbers().isEmpty())
			{
				String elementNumberStr = itr.next();
				String[] elementNumbers = elementNumberStr.split(",");
				search.getSelectedElementNumbers().clear();
				for (String elemNums : elementNumbers)
				{
					search.getSelectedElementNumbers().add(elemNums);
				}
			}
			/*
			 * itr = search.getSelectedErrorTypes().iterator();
			 * getServletRequest().getParameterValues("search.selectedErrorTypes");
			 * if(itr != null && !search.getSelectedErrorTypes().isEmpty()) {
			 * String errorStr = itr.next(); String[] errorTypes =
			 * errorStr.split(","); search.getSelectedErrorTypes().clear();
			 * for(String errorType : errorTypes) {
			 * search.getSelectedErrorTypes().add(errorType); } }
			 */

		}

		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			Iterator<String> itr = search.getSelectedElementNumbers().iterator();
			search.getSelectedElemNums().clear();
			String elemNum = null;
			while (itr.hasNext())
			{
				elemNum = itr.next();
				search.getSelectedElemNums().add(search.getElementNumberMap().get(elemNum).toString());

			}
		}
		search.setRowCount(complianceService.getNonCompliancesCount(search));
		search.setPageResults(complianceService.getNonComplianceCountsReport(search));		
		return SUCCESS;
	}

	private void prepareOrderCriteria() {
		List<String> orderCriteriaList = new ArrayList<String>();
		if(servletRequest.getParameter("stateSort") != null)
		{
			this.stateSort = Boolean.parseBoolean(servletRequest.getParameter("stateSort"));
		}
		
		if(servletRequest.getParameter("reportPeriodSort") != null)
		{
			this.reportPeriodSort = Boolean.parseBoolean(servletRequest.getParameter("reportPeriodSort"));
		}

		if(servletRequest.getParameter("elementSort") != null)
		{
			this.elementSort = Boolean.parseBoolean(servletRequest.getParameter("elementSort"));			
		}
		
		if(this.stateSort) {
			orderCriteriaList.add("statename");			
		}
		if(this.reportPeriodSort) {
			orderCriteriaList.add("reportingperiod");
		}
		if(this.elementSort) {
			orderCriteriaList.add("elementname");
		}
		if(!orderCriteriaList.isEmpty()) {
			search.setSortColumn(String.join(",", orderCriteriaList));
			search.setSortDirection(SortDirection.ASC);
		}
	}

	/**
	 * @return
	 */
	public String exportDQACountsReport()
	{
		// get the list of results to export
		log.debug("ErrorTypeReportAction.exportDQACountsReport");
		prepareSearchCriteria();
		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			Iterator<String> itr = search.getSelectedElementNumbers().iterator();
			search.getSelectedElemNums().clear();
			String elemNum = null;
			while (itr.hasNext())
			{
				elemNum = itr.next();
				search.getSelectedElemNums().add(search.getElementNumberMap().get(elemNum).toString());

			}
		}
		// create the exporter
		ErrorTypeCountsExport exporter = new ErrorTypeCountsExport(this, dataExtractionService, true);

		// export the data to csv
		return exporter.export(servletResponse, complianceService.getDQACountsReport(search),
				"DQACountsReport_" + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	/**
	 * @return
	 */

	@Validations(requiredFields = {
			@RequiredFieldValidator(fieldName = "search.selectedStateName", message = "You must select at least one state."),

			@RequiredFieldValidator(fieldName = "search.selectedReportingPeriods", message = "You must select at least one report period.") }, expressions = {
			@ExpressionValidator(message = "You must select at least one state.", expression = "!search.selectedStateName.isEmpty()"),
			@ExpressionValidator(message = "You must select at least one report period.", expression = "!search.selectedReportingPeriods.isEmpty()") })
	public String exportNonComplianceCountsReport()
	{
		// get the list of results to export
		log.debug("ErrorTypeReportAction.exportNonComplianceCountsReport");
		prepareSearchCriteria();
		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			Iterator<String> itr = search.getSelectedElementNumbers().iterator();
			search.getSelectedElemNums().clear();
			String elemNum = null;
			while (itr.hasNext())
			{
				elemNum = itr.next();
				search.getSelectedElemNums().add(search.getElementNumberMap().get(elemNum).toString());

			}
		}
		// create the exporter
		ErrorTypeCountsExport exporter = new ErrorTypeCountsExport(this, dataExtractionService, false);

		// export the data to csv
		return exporter.export(servletResponse, complianceService.getNonComplianceCountsReport(search),
				"NonComplianceCountsReport_" + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

	public String printFailedTransmissionsReport()
	{
		prepareSearchCriteria();
		failedTransmissionsList = transmissionServiceP3.getFailedTransmissionsReport(searchCriteria);
		return SUCCESS;

	}

	public String resetFailedTransmissionsReport()
	{
		state = null;
		fileType = null;
		reportPeriod = null;
		startDate = null;
		endDate = null;
		failedTransmissionsList = null;
		sortColumn = null;
		sortOrder = null;
		defaultPage = true;
		return SUCCESS;

	}

	private void prepareSearchCriteria()
	{
		log.debug("ErrorTypeReport.prepareSearchCriteria");				
		if (search.getElementNumberMap() != null && search.getElementNumberMap().isEmpty())
		{
			for (Element element : (List<Element>) application.get(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN))
			{
				search.getElementNumberMap().put(element.getName() + ' ' + element.getDescription(),
						element.getId());
			}
		}

		if (search.getAvailableElementNumbers() != null
		// && search.getAvailableElementNumbers().isEmpty()
		)
		{
			for (Element element : (List<Element>) application.get(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN))
			{
				if (search.getSelectedElementNumbers() != null
						&& search.getSelectedElementNumbers().contains(
								element.getName() + ' ' + element.getDescription()))
				{
					search.getAvailableElementNumbers().remove(element.getName() + ' ' + element.getDescription());
					continue;
				}
				else if (!search.getAvailableElementNumbers().contains(
						element.getName() + ' ' + element.getDescription()))
					search.getAvailableElementNumbers().add(element.getName() + ' ' + element.getDescription());

			}
		}

		if (search.getAvailableReportingPeriods() != null
		// && search.getAvailableReportingPeriods().isEmpty()
		)
		{
			for (ReportingPeriod reportPeriod : (List<ReportingPeriod>) application
					.get(Constants.APPKEY_REPORTING_PERIOD_LIST))
			{
				if (search.getSelectedReportingPeriods() != null
						&& search.getSelectedReportingPeriods().contains(reportPeriod.getName()))
				{
					search.getAvailableReportingPeriods().remove(reportPeriod.getName());
					continue;
				}
				else if (!search.getAvailableReportingPeriods().contains(reportPeriod.getName()))
					search.getAvailableReportingPeriods().add(reportPeriod.getName());
			}
		}

		if (search.getAvailableStateName() != null
		// && search.getAvailableStateName().isEmpty()
		)
		{

			/*
			 * Bugs-15526
			 * 
			 * Only the user's state should be available for State Users.
			 * 
			 * Only region states should be available to Regional Users, so here we
			 * set the "states" collection from the region for these users,
			 * otherwise we set it with the full list of states.
			 * 
			 * The requirement states to simply disable the selection in the UI for
			 * State Users, however we are also limiting the state list here for
			 * logical consistency, even though it will no longer be referenced for
			 * State Users.
			 */
			Collection<State> states = null;
			if (this.user.isStateUser())
			{
				states = Collections.singleton(this.user.getState());
			}
			else if (this.user.isRegionalUser())
			{
				states = this.user.getRegion().getStates();
			}
			else
			{
				states = (List<State>) application.get(Constants.APPKEY_STATE_LIST);
			}

			for (State state : states)
			{
				if (search.getSelectedStateName() != null
						&& search.getSelectedStateName().contains(state.getAbbreviation()))
				{
					search.getAvailableStateName().remove(state.getAbbreviation());
					continue;
				}
				else if (!search.getAvailableStateName().contains(state.getAbbreviation()))
					search.getAvailableStateName().add(state.getAbbreviation());
			}
		}

		if (search.getAvailableErrorTypes() != null
		// && search.getAvailableErrorTypes().isEmpty()
		)
		{

			for (String errorType : complianceService.getErrorTypes())
			{
				if (search.getSelectedErrorTypes() != null && search.getSelectedErrorTypes().contains(errorType))
				{
					search.getAvailableErrorTypes().remove(errorType);
					continue;
				}
				else if (!search.getAvailableErrorTypes().contains(errorType))
					search.getAvailableErrorTypes().add(errorType);
			}

			/*
			 * Bug 15527 - ensuring that the Timely Data and Error Free Information
			 * error types are not included in the available error type list
			 */
			List<String> availableErrorTypes = this.search.getAvailableErrorTypes();
			availableErrorTypes.remove(ComplianceErrorEnum.TIMELY_DATA.getErrorName());
			availableErrorTypes.remove(ComplianceErrorEnum.ERROR_FREE_INFORMATION.getErrorName());

		}

		if (search.getAvailableDQATypes() != null
		// && search.getAvailableDQATypes().isEmpty()
		)
		{
			for (int i = 1; i <= 26; i++)
			{
				if (search.getSelectedDQATypes() != null && search.getSelectedDQATypes().contains("DQA#" + i))
				{
					search.getAvailableDQATypes().remove("DQA#" + i);
					continue;
				}
				else if (!search.getAvailableDQATypes().contains("DQA#" + i))
					search.getAvailableDQATypes().add("DQA#" + i);
			}

		}

	}

}
