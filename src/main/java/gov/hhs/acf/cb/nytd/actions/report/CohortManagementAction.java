package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.service.*;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import java.util.*;

/**
 * Action to select a) report period for failed transmissions.
 * 
 * @author Nava Krishna Mallela (23839)
 */
public class CohortManagementAction extends SearchAction<CohortSearch>
{

	@Getter	@Setter private CohortSearch search;
	
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
			search = new CohortSearch();
		}
		prepareSearchCriteria();
	}

	@Override
	protected CohortSearch getPaginatedSearch()
	{
		return getSearch();
	}

	/**
	 * Displays Report Periods Selection page.
	 * 
	 * @return Action.SUCCESS on success
	 */
	
	public final String getCohortSearchPage()
	{
		log.debug("CohortManagementAction.getErrorTypeSearchPage");
		setDefaultPage(true);
		search.reset();
		prepareSearchCriteria();
		return SUCCESS;
	}
	
	public final String getCohortSamplingSearchPage()
	{
		log.debug("CohortManagementAction.getCohortSamplingSearchPage");
		search.setSampleSize(complianceService.getCohortSampleSize(search));
		return SUCCESS;
	}
	
	public final String getCohortRecords()
	{
		
		log.debug("CohortManagementAction.getCohortRecords");
		/* Getting the state Name using the selected State id*/
		Collection<State> states = (List<State>) application.get(Constants.APPKEY_STATE_LIST);
		String stateName = null;
		String baselineYear = null;
		String followup19Year = null;
		for (State state :states)
		{
			if (state.getId() == search.getSelectedState())
			{
				stateName = state.getStateName();
				break;
			}
			else
				continue;
		}
		search.setSelectedStateName(stateName);
		
		/*Getting the cohort name using the selected cohort*/
		List<Cohort> cohorts = (List<Cohort>) complianceService.getCohortList();
		String cohortName = null;
		Integer cohortId = new Integer(search.getSelectedCohorts());
		for(Cohort cohort : cohorts)
		{
			if(cohort.getId() == cohortId.longValue())
			{
				cohortName = cohort.getDerivedName();
				baselineYear = cohort.getReportYear17();
				followup19Year = cohort.getReportYear19();
				break;
			}
			else
				continue;
		}
		search.setSelectedCohortName(cohortName);
		
		
		/*Determining the baseline, post-buffer and follow-up 19 report periods*/
		if( search.getSelectedPopulationType()!= 0 && search.getSelectedPopulationType() != 17)
		{
			if(search.getShowReportPeriod()!= null && search.getShowReportPeriod().size() == 1)
			{
				if(search.getSelectedPopulationType() == 19)
				{
					// Determines the baseline and post-buffer report periods
					if("A".equalsIgnoreCase(search.getShowReportPeriod().get(0)))
					{
						search.setQueryBaselineRP(baselineYear+"A");
						search.setQueryPostbufferRP(baselineYear+"B");
					}
					else
					{
						search.setQueryBaselineRP(baselineYear+"B");
						Long postBufferYear = Long.parseLong(baselineYear) + 1L;
						search.setQueryPostbufferRP(postBufferYear.toString()+"A");
					}
				}
				else if(search.getSelectedPopulationType() == 21)
				{
					// Determines the follow-up 19 report period
					if("A".equalsIgnoreCase(search.getShowReportPeriod().get(0)))
					{
						search.setQueryFollowup19RP(followup19Year+"A");
					}
					else
					{
						search.setQueryFollowup19RP(followup19Year+"B");
					}
				}
			}
			/*Fetching the cohort records*/
			Map map = complianceService.getCohortStatus(search);
			CohortStatus cohortStatus = (CohortStatus)map.get("cohortStatus");
			if(cohortStatus != null)
			{
				List<CohortRecord> list = (List<CohortRecord>)map.get("cohortRecords");
				search.setPageResults(list);
				search.setCohortSize(cohortStatus.getCohortSize());
				search.setSampleSize(cohortStatus.getSampleSize());
				search.setPeriodLocked19(cohortStatus.getPeriodLocked19());
				search.setPeriodLocked21(cohortStatus.getPeriodLocked21());
			}
		}
		else if( search.getSelectedPopulationType()!= 0 && search.getSelectedPopulationType() == 17)
		{
			
			List<Arrays[]> list =(List<Arrays[]>) complianceService.getCohortBaseline(search);
			search.setPageResults(list);
		}
		showUpdateCohortSetButton();
		return SUCCESS;
	}

	

	private void prepareSearchCriteria()
	{
		log.debug("CohortManagementAction.prepareSearchCriteria");
	

		if (search.getAvailableStates() != null)
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
			
			search.getAvailableStates().addAll(states);


		}
		if(search.getAvailableCohorts()!=null)
		{
			search.getAvailableCohorts().addAll(complianceService.getCohortList());
		}
	}
	
	public String exportCohortBaseline()
	{
		log.debug("CohortManagementAction.exportCohortBaseline");
		CohortBaselineExport exporter = new CohortBaselineExport(this, dataExtractionService);

		// export the data to csv
		return exporter.export(getServletResponse(), complianceService.getCohortBaseline(search), "BaselinePopulation-Age17_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
		
	}
	
	public String exportCohortSet()
	{
		log.debug("CohortManagementAction.exportCohortSet");
		String fileName = null;
		int populationTypeId = search.getSelectedPopulationType();
		CohortSetExport exporter = new CohortSetExport(this, dataExtractionService,populationTypeId);
		if(populationTypeId == 19)
		{
			fileName = COHORT_FOLLOWUP_AGE19_FILENAME;
		}
		else if(populationTypeId == 21)
		{
			fileName = COHORT_FOLLOWUP_AGE21_FILENAME;
		}

		// export the data to csv
		return exporter.export(getServletResponse(),(List<CohortRecord>) complianceService.getCohortStatus(search).get("cohortRecords"), fileName
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
		
	}
	
	public String updateCohortSet()
	{
		log.debug("CohortManagementAction.updateCohortSet");
		complianceService.overWriteCohortSet(search);
		sendMessages();
		return SUCCESS;
	}
	
	private void sendMessages()
	{
		Message dataExportMsg = null;
		Message reimportInfoMsg = null;
		List<SiteUser> stUser = new ArrayList<SiteUser>();
		SiteUser user = null;
		Map<String, Object> namedParams = new HashMap<String, Object>();
		List<SiteUser> msgRecipients = new ArrayList<SiteUser>();
		
		namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("cohortNumber", search.getSelectedCohortName());
		namedParams.put("cohortId", search.getSelectedCohorts());
		namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
		namedParams.put("updatedDate", DateUtil.getCurrentDate("MM/dd/yyyy HH:mm:ss z"));
		SiteUser currentUser = (SiteUser)this.session.get("siteUser");
		namedParams.put("userName", currentUser.getUserName());
		State state = userService.lookupState(search.getSelectedStateName());
		//adding state users
		msgRecipients.addAll(lookupService.getStateUsers(state));
		//adding system admin
		msgRecipients.addAll(lookupService.getSystemAdminUsers());
		//adding  Federal Users - CB Central office users
		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
		//adding Regional Users - Regional users
		msgRecipients.addAll(lookupService.getFederalUsers());
		
		Iterator<SiteUser> userItr = msgRecipients.iterator();
		while(userItr.hasNext())
		{
			user = userItr.next();
			namedParams.remove("firstName");
			namedParams.remove("lastName");
			namedParams.put("userName", user.getUserName());
			boolean adminUser = false;
			// TODO: please replace below code with user.getPrimaryUserRole().getName().equals(UserRoleEnum.ADMIN)
			// once the role is populated as a part of site user
			if(user.getPrimaryUserRole()!=null && user.getPrimaryUserRole().getId() == 1)
			{
				namedParams.put("firstName", "System");
				namedParams.put("lastName", "Administrator");
				adminUser = true;
			} else {
				namedParams.put("firstName", user.getFirstName());
				namedParams.put("lastName", user.getLastName());				
			}
			if(search.getSelectedPopulationType() == 19)
			{
				dataExportMsg = messageServiceP3.createSystemMessage(
					MessageService.OVERWRITE_COHORTSET_AGE19, namedParams);
				reimportInfoMsg = messageServiceP3.createSystemMessage(
						MessageService.REIMPORTINFO_COHORTSET_AGE19, namedParams);
			}
			else if(search.getSelectedPopulationType() == 21)
			{
				dataExportMsg = messageServiceP3.createSystemMessage(
					MessageService.OVERWRITE_COHORTSET_AGE21, namedParams);
				reimportInfoMsg = messageServiceP3.createSystemMessage(
						MessageService.REIMPORTINFO_COHORTSET_AGE21, namedParams);				
			}
			stUser.clear();
			stUser.add(user);		
		
			try
			{
				messageServiceP3.sendSystemMessage(dataExportMsg, stUser);
				// Only send re-import message after cohort update for admin users			
				// remove below logic according to Task #529
				/* if(adminUser){
					messageServiceP3.sendSystemMessage(reimportInfoMsg, stUser);
				}*/
			}
			catch (Exception e)
			{	
				Log.debug("And exception occured"+e.getMessage());
				e.printStackTrace();
			}			
		}
	}
	
	public final String getYouthRecord()
	{
		
		RecordToExport record = null;
		if(search.getSelectedPopulationType() == 19 || search.getSelectedPopulationType() == 21 )
		{
			record = this.getTransmissionServiceP3().getCohortSet19Record(search);
		}
		// load the record from the database
		if(search.getSelectedPopulationType() == 17)
		{
			record = this.getTransmissionServiceP3().getBaselineCohortRecord(search);
		}

		Map<Integer, String> demographics = new TreeMap<Integer, String>();
		Map<Integer, String> servedPopulation = new TreeMap<Integer, String>();
		Map<Integer, String> outcomesPopulation = new TreeMap<Integer, String>();
		
		// split record elements into appropriate categories
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
	private void showUpdateCohortSetButton()
	{
		// if the value of showbutton is 0 then none of the two buttons will be displayed
		// if the value of showbutton is 1 then "Update Follow-up19 
		
		int showbuttons = 0; 
		showbuttons = complianceService.showUpdateCohortSetButton(search.getSelectedCohorts());
		search.setShowUpdateCohortSetButton(showbuttons);
	}
}
