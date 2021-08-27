package gov.hhs.acf.cb.nytd.actions.sampling.state;

import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.helper.StateCohortDTO;
import gov.hhs.acf.cb.nytd.models.sampling.federal.FederalSamplingContext;
import gov.hhs.acf.cb.nytd.models.sampling.state.StateSamplingContext;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.service.sampling.federal.FederalSamplingService;
import gov.hhs.acf.cb.nytd.service.sampling.state.StateSamplingService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.LateRequestUtil;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.*;

public class StateSamplingController extends SearchAction<StateSamplingContext>
{
    private static final long serialVersionUID = 1L;
    
    public static final String STATE_SAMPLING_HOME_RESULT = "state.sampling.home";
    public static final String LATE_REQUEST_RESULT = "late.request";
    public static final String EARLY_REQUEST_RESULT = "early.request";
    
    
    @Getter @Setter private StateSamplingService stateSamplingService;
    @Getter @Setter private FederalSamplingService federalSamplingService;
    /*@Getter @Setter private boolean useAlternateSamplingMethod;
    @Getter @Setter private String alternateSamplingDescription;*/
    @Getter @Setter private StateSamplingContext search;
    @Getter	@Setter	private MessageService messageServiceP3;
    @Getter	@Setter	private UserService userService;

    public String cohortList() {
    	
    	search.setPageResults(stateSamplingService.getCohortList(getSiteUser()));
    	search.setSelectedState(getSiteUser().getState().getId());
    	search.setSelectedStateName(getSiteUser().getState().getStateName());
    	search.setMessageMap(stateSamplingService.getMessageIdMap(search));
        return SUCCESS;
    }
    
    public String viewDetails() {
    	/*search.setPageResults(stateSamplingService.getCohortList(getSiteUser()));
    	search.setSelectedState(getSiteUser().getState().getId());
    	search.setSelectedStateName(getSiteUser().getState().getStateName());
    	search.setCohorts(stateSamplingService.getCohortList(getSiteUser()));*/
    	search.setPageResults(stateSamplingService.getSamplingRequestHistories(search.getSelectedSamplingRequestId()));
        return SUCCESS;
    }
    
    public String requestSample() {
    	Long cohortSize = federalSamplingService.getCohortSize(search.getSelectedState(), search.getSelectedCohort());
    	Iterator itr = stateSamplingService.getCohortList(getSiteUser()).iterator();
    	int rp17 = 0;
    	while(itr.hasNext())
    	{
    		StateCohortDTO dto = (StateCohortDTO) itr.next();
    		if(dto.getCOHORTSID().longValue() == search.getSelectedCohort())
    		{
    			rp17 =Integer.parseInt(dto.getREPORTYEAR17());
    			break;
    		}
    	}
    	if (LateRequestUtil.isEarlyRequest(this,rp17)) {
            return EARLY_REQUEST_RESULT;
        }
    	if(cohortSize == 0L)
        {
    		search.setPageResults(stateSamplingService.getCohortList(getSiteUser()));
        	search.setSelectedState(getSiteUser().getState().getId());
        	search.setSelectedStateName(getSiteUser().getState().getStateName());
    		
    		StringBuffer message = new StringBuffer()
    	        .append("A sampling request cannot be submitted at this time because there are no youth ")
    	        .append("in the State's Follow-up-19 Cohort population. This is the population of data")
    	        .append(" from which a sample would be drawn");
        	addActionMessage(message.toString());
    		return "state.sampling.request.error.zeroCohortSize";
        }
    	
    	if (LateRequestUtil.isLateRequest(this,rp17)) {
            return LATE_REQUEST_RESULT;
        }
        return SUCCESS;
    }
    
    public String submitSamplingRequest()
	{
    	FederalSamplingContext federalSamplingContext = new FederalSamplingContext();
    	federalSamplingContext.setSelectedCohort(search.getSelectedCohort());
    	federalSamplingContext.setSelectedState(search.getSelectedState());
    	federalSamplingContext.setHasAlternateSamplingMethod(search.isHasAlternateSamplingMethod());
    	federalSamplingContext.setAlternateSamplingMethod(search.getAlternateSamplingMethod());
    	Message msg = sendRequestSubmittedMessages();
    	federalSamplingContext.setMessageId(msg.getId());
    	if(search.getSelectedSamplingRequestId() == null ||
    			search.getSelectedSamplingRequestId() == 0L)
    	{
    	//	Message msg = sendRequestSubmittedMessages();
    	//	federalSamplingContext.setMessageId(msg.getId());
    		federalSamplingService.saveSamplingRequest(federalSamplingContext);
    	}
    	else
    	{
    		federalSamplingContext.setSelectedSamplingRequestId(search.getSelectedSamplingRequestId());
    		if(search.isHadAlternateSamplingMethod())
    			federalSamplingContext.setLastAlternateSamplingMethod(search.getLastAlternateSamplingMethod());
    		else
    			federalSamplingContext.setLastAlternateSamplingMethod("SRS");
    		federalSamplingService.reSubmitSamplingRequest(federalSamplingContext);
    	}
		return STATE_SAMPLING_HOME_RESULT;
	}
    
    public String confirmSamplingRequest()
    {
    	
    	if(search.isHasAlternateSamplingMethod() &&(search.getAlternateSamplingMethod() == null || search.getAlternateSamplingMethod().isEmpty()))
    	{
    		addActionMessage("Please specify an alternate sampling method.");
    		return "state.sampling.request.error";
    	}
    	
    	if((search.getAlternateSamplingMethod() != null && !search.getAlternateSamplingMethod().isEmpty()) && !search.isHasAlternateSamplingMethod())
    	{
    		addActionMessage("Please select \"Yes\" to specify that you wish to use a sampling method other than SRS");
    		return "state.sampling.request.error";
    	}
    	

    	if(search.isHasAlternateSamplingMethod() &&(search.getAlternateSamplingMethod() != null && search.getAlternateSamplingMethod().length() > 3999))
    	{
    		addActionMessage("The \"Alternate Sampling Method\" text should not exceed 3999 characters");
    		return "state.sampling.request.error";
    	}
    	
    	return SUCCESS;
    }
    
    public String continueRequestSample() {
    	
    	Map<String,String> map = null;
    	if(search.getSelectedSamplingRequestId() != null && search.getSelectedSamplingRequestId() > 0L)
    	{
    		search.setHasAlternateSamplingMethod(false);
    		map = stateSamplingService.getAlternateSamplingMethod(search.getSelectedSamplingRequestId());
    		if(!"SRS".equalsIgnoreCase(map.get("SAMPLINGMETHOD")))
    		{
    			search.setHasAlternateSamplingMethod(true);
    			search.setAlternateSamplingMethod(map.get("SAMPLINGMETHODTEXT"));
    		}
    	}
        return SUCCESS;
    }
    
    public String samplingSubmitted(){
    	search.setSelectedStateName(getSiteUser().getState().getStateName());
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
    	
    	session.put("currDate", sdf.format(cal.getTime()));
    	session.put("today", cal.getTime());
 		return SUCCESS;
    }
    
  /*  public String submitRequestSample() {
     //   this.stateSamplingService.requestSample(this.getModel(), this.useAlternateSamplingMethod, this.alternateSamplingDescription);
        return STATE_SAMPLING_HOME_RESULT;
    }
  */  
    public void prepare()
	{
		super.prepare();

		// when using the tab navigation the search object is not created
		if (search == null)
		{
			search = new StateSamplingContext();
		}
		prepareSearchCriteria();
	}

   
	@Override
	protected StateSamplingContext getPaginatedSearch()
	{
		return getSearch();
	}    
    private SiteUser getSiteUser() {
        return (SiteUser) this.getSession().get(SiteUser.SESSION_KEY);
    }
    
   /* public void setUseAlternateSamplingMethod(boolean useAlternateSamplingMethod) {
        this.useAlternateSamplingMethod = useAlternateSamplingMethod;
    }
    
    public void setAlternateSamplingDescription(String alternateSamplingDescription) {
        this.alternateSamplingDescription = alternateSamplingDescription;
    }
*/
    private void prepareSearchCriteria()
    {
    	//search.setCohortList(complianceService.getCohortList());
    }
    
    private Message sendRequestSubmittedMessages()
    {
    	Message msg = null;
    	List<SiteUser> stUser = new ArrayList<SiteUser>();
		SiteUser user = null;
		Map<String, Object> namedParams = new HashMap<String, Object>();
		List<SiteUser> msgRecipients = new ArrayList<SiteUser>();
		
		/*namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("cohortNumber", search.getSelectedCohortName());*/
		State state = userService.lookupState(search.getSelectedStateName());
		namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("cohortNumber", search.getSelectedCohort());
		namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
		//adding state users
		msgRecipients.addAll(lookupService.getStateUsers(state));
		//adding system admin
		msgRecipients.addAll(lookupService.getSystemAdminUsers());
		//adding Regional Users - Regional users
		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
		//adding  Federal Users - CB Central office users
	//	msgRecipients.addAll(lookupService.getFederalUsers());
		
		msg = messageServiceP3.createSystemMessage(
				MessageService.SAMPLING_REQUEST_SUBMITTED, namedParams);
		
		messageServiceP3.sendSystemMessage(msg, msgRecipients);
    	
    	return msg;
    }
}
