package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.Cohort;
import gov.hhs.acf.cb.nytd.models.RecordToExport;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.service.*;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Action to select a) report period for failed transmissions.
 * 
 * @author Nava Krishna Mallela (23839)
 */
public class OutcomesReportAction extends SearchAction<OutcomesReportSearch>
{

	@Getter	@Setter private OutcomesReportSearch search;
	
	@Getter @Setter private Map<String, String> searchCriteria;
	
	@Getter @Setter	private String defaultReportPeriod;
	
	@Getter	@Setter	private String reportPeriod;
	
	@Getter	@Setter private String reportPeriodName;
	
	@Getter @Setter private String state;
	
	@Getter @Setter private String fileType;
	
	@Getter	@Setter private String startDate;
	
	@Getter @Setter private String endDate;
	
	@Getter	@Setter private String sortColumn;
	
	@Getter @Setter private String sortOrder;
	
	@Getter @Setter private String prevSortColumn;
	
	@Getter	@Setter private String prevSortOrder;
	
	@Getter	@Setter private String freezeSort;
	
	@Getter @Setter private boolean defaultPage;

	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
	
	@Getter	@Setter private TransmissionServiceP3 transmissionServiceP3;
	
	@Getter @Setter private DataExtractionService dataExtractionService;
	
	@Getter	@Setter	private ComplianceService complianceService;
	
	@Getter	@Setter	private MessageService messageServiceP3;
	
	@Getter	@Setter	private UserService userService;
	
	@Getter	@Setter	private List<FailedTransmissionDetail> failedTransmissionsList;
	
	public static final String COHORT_FOLLOWUP_AGE19_FILENAME = "FollowupPopulation-Age19_";
	
	public static final String COHORT_FOLLOWUP_AGE21_FILENAME = "FollowupPopulation-Age21_";
	
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
			search = new OutcomesReportSearch();
		}
		prepareSearchCriteria();
	}

	@Override
	protected OutcomesReportSearch getPaginatedSearch()
	{
		return getSearch();
	}

	/**
	 * Displays Report Periods Selection page.
	 * 
	 * @return Action.SUCCESS on success
	 */
	
	public final String getOutcomesReportSearchPage()
	{
		log.debug("OutcomesReportAction.getOutcomesReportSearchPage");
		setDefaultPage(true);
		search.reset();
		prepareSearchCriteria();
		return SUCCESS;
	}
	
	private String getDerivedCohortName(int cohortsId)
	{
		//	Getting the cohort name using the selected cohort
		List<Cohort> cohorts = (List<Cohort>) complianceService.getCohortList();
		String cohortName = null;
		Integer cohortId = new Integer(cohortsId);
		for(Cohort cohort : cohorts)
		{
			if(cohort.getId() == cohortId.longValue())
			{
				cohortName = cohort.getDerivedName();
				break;
			}
			else
				continue;
		}
		return cohortName;
	}
	
	public final String getOutcomesReportWithTransId()
	{
		StringBuffer returnString = null;
		List results = null;
//		if(search.getSelectedCohorts()==0)
//		{
//			search.setSelectedCohorts(1);
//		}
//		search.setSelectedCohortName(getDerivedCohortName(search.getSelectedCohorts()));
	
		/*if(search.getSelectedStateName() == null || (search.getSelectedStateName() != null && search.getSelectedStateName().size() == 0))
		{
			addActionError("At least one state needs to be selected.");
			prepareSearchCriteria();
			return "input";
		}
		if(!search.getSelectedFollowup19() && !search.getSelectedFollowup21())
		{
			addActionError("At least one of the populations needs to be selected.");
			prepareSearchCriteria();
			return "input";
		}*/
		
		
		results = complianceService.getOutcomesReportWithTransId(search);
		switch(search.getSelectedReportCode())
		{
			case 1: {	
						returnString = new StringBuffer("outcomesUniverse"); 
						break;
					}	
			case 2: {
						returnString = new StringBuffer("outcomesParticipation");
						break;
					}
		}
		
		search.setPageResults(results);
		return returnString.toString();
	}
	
	
	public final String getOutcomesReport()
	{
		StringBuffer returnString = null;
		List results = null;
//		if(search.getSelectedCohorts()==0)
//		{
//			search.setSelectedCohorts(1);
//		}
		search.setSiteUser((SiteUser)session.get("siteUser"));
		search.setSelectedCohortName(getDerivedCohortName(search.getSelectedCohorts()));
	
		if(search.getSelectedStateName() == null || (search.getSelectedStateName() != null && search.getSelectedStateName().size() == 0))
		{
			addActionError("At least one state needs to be selected.");
			//prepareSearchCriteria();
			return "input";
		}
		if(!search.getSelectedFollowup19() && !search.getSelectedFollowup21())
		{
			addActionError("At least one of the populations needs to be selected.");
			//prepareSearchCriteria();
			return "input";
		}
		
		
		results = complianceService.getOutcomesReport(search);
		switch(search.getSelectedReportCode())
		{
			case 1: {	
						returnString = new StringBuffer("outcomesUniverse"); 
						break;
					}	
			case 2: {
						returnString = new StringBuffer("outcomesParticipation");
						break;
					}
		}
		
		search.setPageResults(results);
		return returnString.toString();
	}
	
	public final String showOutcomeUniverseExcludedYouth()
	{
		search.setSelectedCohortName(getDerivedCohortName(search.getSelectedCohorts()));
		List<String> results = complianceService.getOutcomesUniverseRecords(search);
		search.setPageResults(results);
		return "success";
	}
	
	public final String showOutcomeParticipationExcludedYouth()
	{
		search.setSelectedCohortName(getDerivedCohortName(search.getSelectedCohorts()));
		List<String> results = complianceService.getOutcomesParticipationRecords(search);
		search.setPageResults(results);
		return "success";
	}
	
		

	private void prepareSearchCriteria()
	{
		log.debug("OutcomesReportAction.prepareSearchCriteria");
	
		
		if (search.getAvailableStateName() != null
				// && search.getAvailableStateName().isEmpty()
				)
				{
				
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


		if(search.getAvailableCohorts()!=null)
		{
			search.getAvailableCohorts().addAll(complianceService.getCohortList());
		}
	}
	

	
	public String exportOutcomesUniverseRecords()
	{
		log.debug("OutcomesReportAction.exportOutcomesUniverseRecords");
		String fileName = null;
		String title = null;
		title = "Not Reported or Invalid";
		fileName = "Outcome Universe";
				
		OutcomesUniverseRecordExport exporter = new OutcomesUniverseRecordExport(this, dataExtractionService,title);
		

		// export the data to csv
		return exporter.export(getServletResponse(),(List<String>) complianceService.getOutcomesUniverseRecords(search), fileName
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
		
	}
	
	public String exportOutcomesParticipationRecords()
	{
		log.debug("OutcomesReportAction.exportOutcomesParticipationRecords");
		String fileName = null;
		String title = null;
		title = "Not Participation";
		fileName = "Outcome Participation";
				
		OutcomesUniverseRecordExport exporter = new OutcomesUniverseRecordExport(this, dataExtractionService,title);
		

		// export the data to csv
		return exporter.export(getServletResponse(),(List<String>) complianceService.getOutcomesParticipationRecords(search), fileName
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
		
	}
	
	
	public final String getYouthRecord()
	{
		log.debug("OutcomesReportAction.getYouthRecord");
		RecordToExport record = null;
		CohortSearch cohortSearch = null;
		if(search.isShowYouthNotReported())
			record = this.getTransmissionServiceP3().getOutcomesUniverseNotReportedRecord(search);
		else
		{	
			cohortSearch = new CohortSearch();
			cohortSearch.setSelectedCohorts(search.getSelectedCohorts());
			cohortSearch.setSelectedState(search.getStateId().intValue());
			cohortSearch.setRecordNumber(search.getRecordNumber());
			cohortSearch.setSelectedPopulationType(search.getOutcomeAge().intValue());
			cohortSearch.setSelectedReportPeriodName(search.getReportingPeriodName());
			
			
			if(cohortSearch.getSelectedPopulationType() == 19 || cohortSearch.getSelectedPopulationType() == 21 )
			{
				record = this.getTransmissionServiceP3().getCohortSet19Record(cohortSearch);
			}
		}

		Map<Integer, String> demographics = new TreeMap<Integer, String>();
		Map<Integer, String> servedPopulation = new TreeMap<Integer, String>();
		Map<Integer, String> outcomesPopulation = new TreeMap<Integer, String>();
		
		// split record elements into appropriate categories
		try {
			demographics.put(1, record.getE1State());
			demographics.put(2, record.getE2());
			demographics.put(3, record.getE3RecordNumber());
			demographics.put(4, record.getE4());
			demographics.put(5, record.getE5());
			demographics.put(6, record.getE6());
			demographics.put(7, record.getE7());
			demographics.put(8, record.getE8());
			demographics.put(9, record.getE9());
			demographics.put(10, record.getE10());
			demographics.put(11, record.getE11());
			demographics.put(12, record.getE12());
			demographics.put(13, record.getE13());
			
			servedPopulation.put(1, record.getE14());
			servedPopulation.put(2, record.getE15());
			servedPopulation.put(3, record.getE16());
			servedPopulation.put(4, record.getE17());
			servedPopulation.put(5, record.getE18());
			servedPopulation.put(6, record.getE19());
			servedPopulation.put(7, record.getE20());
			servedPopulation.put(8, record.getE21());
			servedPopulation.put(9, record.getE22());
			servedPopulation.put(10, record.getE23());
			servedPopulation.put(11, record.getE24());
			servedPopulation.put(12, record.getE25());
			servedPopulation.put(13, record.getE26());
			servedPopulation.put(14, record.getE27());
			servedPopulation.put(15, record.getE28());
			servedPopulation.put(16, record.getE29());
			servedPopulation.put(17, record.getE30());
			servedPopulation.put(18, record.getE31());
			servedPopulation.put(19, record.getE32());
			servedPopulation.put(20, record.getE33());
			
			outcomesPopulation.put(1, record.getE34());
			outcomesPopulation.put(2, record.getE35());
			outcomesPopulation.put(3, record.getE36());
			outcomesPopulation.put(4, record.getE37());
			outcomesPopulation.put(5, record.getE38());
			outcomesPopulation.put(6, record.getE39());
			outcomesPopulation.put(7, record.getE40());
			outcomesPopulation.put(8, record.getE41());
			outcomesPopulation.put(9, record.getE42());
			outcomesPopulation.put(10, record.getE43());
			outcomesPopulation.put(11, record.getE44());
			outcomesPopulation.put(12, record.getE45());
			outcomesPopulation.put(13, record.getE46());
			outcomesPopulation.put(14, record.getE47());
			outcomesPopulation.put(15, record.getE48());
			outcomesPopulation.put(16, record.getE49());
			outcomesPopulation.put(17, record.getE50());
			outcomesPopulation.put(18, record.getE51());
			outcomesPopulation.put(19, record.getE52());
			outcomesPopulation.put(20, record.getE53());
			outcomesPopulation.put(21, record.getE54());
			outcomesPopulation.put(22, record.getE55());
			outcomesPopulation.put(23, record.getE56());
			outcomesPopulation.put(24, record.getE57());
			outcomesPopulation.put(25, record.getE58());
		} catch (Exception e) {
			
			log.debug("OutcomesReportAction.getYouthRecord - no matching youth record found");
			e.printStackTrace();
		}

		
	
		

		// list used to create a 3 column table of record elements
		// column 1 contains elements E1 - E13
		// column 2 contains elements E14 - E33
		// column 3 contains elements E34 - E58
		ArrayList datums = new ArrayList();
		TableDatumBean ed = null;
		for (int i = 1; i <= 25; i++)
		{
			Integer key = new Integer(i);

			String dd = demographics.get(key);
			ed	= new TableDatumBean();
			if(i <= 13)
			{
				ed.setRowNumber(i);
				ed.setValue("");
				ed.setColumn(this.getTransmissionServiceP3().getElementNameById(i));
				if (dd != null)
					ed.setValue(dd);
			}
			datums.add(ed);

			String sp = servedPopulation.get(key);
			ed	= new TableDatumBean();
			if( i+13 <=33)
			{
				ed.setRowNumber(i+13);
				ed.setValue("");
				ed.setColumn(this.getTransmissionServiceP3().getElementNameById(i+13));
				if (sp != null)
					ed.setValue(sp);
			}
			datums.add(ed);

			String op = outcomesPopulation.get(key);
			ed	= new TableDatumBean();
			if(i+33 <=58)
			{
				ed.setRowNumber(i+33);
				ed.setValue("");
				ed.setColumn(this.getTransmissionServiceP3().getElementNameById(i+33));
				if (op != null)
					ed.setValue(op);
			}
			datums.add(ed);
		}

		// push the list on the value stack for use in youthRecord.jsp
		ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
		stack.set("youthRecordElements", datums);

		return Action.SUCCESS;
	}

}
