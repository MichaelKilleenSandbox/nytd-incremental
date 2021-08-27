package gov.hhs.acf.cb.nytd.actions.sampling.federal;

import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.actions.report.CohortSearch;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SamplingRequest;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.helper.VwSamplingRequest;
import gov.hhs.acf.cb.nytd.models.sampling.federal.FederalSamplingContext;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.service.sampling.federal.FederalSamplingService;
import gov.hhs.acf.cb.nytd.service.sampling.state.StateSamplingService;
import gov.hhs.acf.cb.nytd.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.jfree.util.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.util.*;


public class FederalSamplingController extends SearchAction<FederalSamplingContext> implements ApplicationContextAware
{
    private FederalSamplingService federalSamplingService;
    public static final String FEDERAL_SAMPLING_HOME = "federal.sampling.home";
    public static final String  SUBJECT_COMMENTS_SAMPLING_REQUEST = "Comments on Sampling Request - ";
    //public static final String  SUBJECT_REJECTION_SAMPLING_REQUEST = "Reason for Rejection";
    public static final String  SUBJECT_REJECTION_SAMPLING_REQUEST = "Sampling request rejected - ";
    @Getter @Setter private ComplianceService complianceService;
    @Getter	@Setter	private MessageService messageServiceP3;
    @Getter	@Setter	private UserService userService;
    @Getter @Setter private FederalSamplingContext search;
    @Getter @Setter private CohortSearch cohortSearch;
    @Getter @Setter private StateSamplingService stateSamplingService;
    @Getter @Setter private String samplingFileLocation;
    @Setter private ApplicationContext applicationContext;
    protected final Logger log = Logger.getLogger(getClass());
    
    private File cohortSample;    
    
    public File getCohortSample() {
		return cohortSample;
	}


	public void setCohortSample(File cohortSample) {
		this.cohortSample = cohortSample;
	}

	@Getter @Setter private String cohortSampleContentType;
    @Getter @Setter private String cohortSampleFileName;
    
    public void prepare()
	{
		super.prepare();

		// when using the tab navigation the search object is not created
		if (search == null)
		{
			search = new FederalSamplingContext();
		}
		prepareSearchCriteria();
	}

   
	@Override
	protected FederalSamplingContext getPaginatedSearch()
	{
		return getSearch();
	}
	
	 @SkipValidation
	public String reset()
	{
		search.reset();
		return FEDERAL_SAMPLING_HOME;
	}
	
	/* public String resubmitSamplingRequest()
	 {
		 search.setHasAlternateSamplingMethod(false);
 		map = stateSamplingService.getAlternateSamplingMethod(search.getSelectedSamplingRequestId());
 		if(!"SRS".equalsIgnoreCase(map.get("SAMPLINGMETHOD")))
 		{
 			search.setHasAlternateSamplingMethod(true);
 			search.setAlternateSamplingMethod(map.get("SAMPLINGMETHODTEXT"));
 		}
 		return
	 }*/
	 @SkipValidation
	public String submitSamplingRequest()
	{
		 System.out.println("in submitSamplingRequest");
		 Message msg = sendRequestSubmittedMessages();
		 search.setMessageId(msg.getId());
		 
		 if(search.getSelectedSamplingRequestId() == null ||
	    			search.getSelectedSamplingRequestId() == 0L)
	    	{
	    	//	Message msg = sendRequestSubmittedMessages();
	    	//	federalSamplingContext.setMessageId(msg.getId());
			 federalSamplingService.saveSamplingRequest(search);
	    	}
	    	else
	    	{
	    		
	    		if(!search.isHadAlternateSamplingMethod())
	    			search.setLastAlternateSamplingMethod("SRS");
	    		federalSamplingService.reSubmitSamplingRequest(search);
	    	}
		return FEDERAL_SAMPLING_HOME;
	}
	
	 @SkipValidation
	public String approveSamplingRequest()
	{
		boolean isApproved = false;
		isApproved = federalSamplingService.updateSamplingRequestStatus(search.getSelectedSamplingRequestId(), 47L, 0L);
		if(isApproved)
		{
			federalSamplingService.updateCohortLock(search, true);
			sendApprovalMessages();
		}
		search.reset();
		return FEDERAL_SAMPLING_HOME;
	}
	
	 @SkipValidation
	public String rejectSamplingRequest()
	{
		 session.put("currentDate", getCurrentDate());
		return SUCCESS;
		
	}
	
	 @SkipValidation
	public String sendReasonforRejection()
	{
		 boolean isRejected = false;
		 Message msg = sendMessages(1);
		isRejected = federalSamplingService.updateSamplingRequestStatus(search.getSelectedSamplingRequestId(), 46L, msg.getId());
		if(isRejected)
		{
			federalSamplingService.updateCohortLock(search, false);
		}
		return FEDERAL_SAMPLING_HOME;
	}
	public Date getCurrentDate(){
	    return new Date();
	}
	/**
	 * action when comments are provided for the sample 
	 * @return
	 */
	 @SkipValidation
	public String commentSamplingRequest()
	{
		session.put("currentDate", getCurrentDate());
		return SUCCESS;
	}
	 
	 @SkipValidation
	public String sendSamplingRequestComments()
	{
		Message msg = sendMessages(2);
		federalSamplingService.updateSamplingRequestStatus(search.getSelectedSamplingRequestId(), 45L, msg.getId());
		return FEDERAL_SAMPLING_HOME;
	}
	/**
	 * default action for the Sampling homepage
	 */
	 @SkipValidation
    public String cohortList() {
		
    	search.setPageResults(federalSamplingService.searchSamplingRequest(search));
    	search.setMessageMap(federalSamplingService.getMessageIdMap(search));
        return SUCCESS;
    }
	/**
	 * action when the user wants to import the Sampling Request
	 * @return
	 */
	public String importSampleRequest(){
		search.setPageResults(federalSamplingService.searchSamplingRequest(search));
    	search.setMessageMap(federalSamplingService.getMessageIdMap(search));
    	return SUCCESS;
	}
	
	/**
	 * action for uploading cohort sample
	 * @return
	 */
	public String uploadFile(){
		
		/*federalSamplingService.uploadSample(cohortSample,cohortSampleFileName,cohortSampleContentType,
				Long.parseLong(getServletRequest().getParameter("samplingRequestId")),getText("systemConfig.samplingFileLocation"));*/
		String importStatus = null;
		importStatus = federalSamplingService.saveSample(cohortSample,cohortSampleFileName,cohortSampleContentType,
				Long.parseLong(getServletRequest().getParameter("samplingRequestId")),getSamplingFileLocation());
		log.debug("Ran *** sampleRequestId on upload:"+getServletRequest().getParameter("samplingRequestId"));
		
		sendImportSampleMessage(importStatus);
		
		return FEDERAL_SAMPLING_HOME;
	}
	
	/**
	 * action method for displaying the failure reason for import
	 */
	public String importFailureReason(){
		String importSummary = federalSamplingService.getFailureReason(Long.parseLong(getServletRequest().getParameter("samplingRequestId")));
		session.put("importSummary", importSummary);
		return SUCCESS;
	}
	
	
	 /**
	  * action to reset the Context object and
	  * return to the Sampling homepage
	  * @return
	  */
	public String cancelAction(){
		search.setSelectedCohort(0L);
		search.setSelectedRequestStatus(0L);
		search.setSelectedState(0L);
		search.setSelectedSamplingMethod(0L);
		return "federal.sampling.home";
	}
	 
	public String requestSampleMethod() {
		SamplingRequest sr = federalSamplingService.getSamplingRequest(search);
		if(sr!=null) {
			search.setAlternateSamplingMethod(sr.getSamplingMethod());
		}
		return SUCCESS;
	}
	
	/**
	 * action for Confirming the approval of the requested Sample
	 * @return
	 */
	public String confirmApproval() {
		
		return SUCCESS;
	}
    
    @SkipValidation
    public String startSamplingRequest() {
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
    
       public String confirmSamplingRequest()
    {
    	  
    	   
    	   boolean samplingRequestExists = false;
    	   
    	 
    	   
    	   Long cohortSize = federalSamplingService.getCohortSize(search.getSelectedState(), search.getSelectedCohort());
           
       	if(cohortSize == 0L)
           {
       		
       		StringBuffer message = new StringBuffer()
       	        .append("A sampling request cannot be submitted at this time because there are no youth ")
       	        .append("in the State's Follow-up-19 Cohort population. This is the population of data")
       	        .append(" from which a sample would be drawn");
           	addActionMessage(message.toString());
       		return "federal.sampling.request.error";
           }
    	  
       	samplingRequestExists = federalSamplingService.samplingRequestExists(search);
      	if(samplingRequestExists && (search.getSelectedSamplingRequestId() == null || search.getSelectedSamplingRequestId() == 0L))
      	{
      		addActionMessage("A Sampling request is already in place.");
    		return "federal.sampling.request.error";
      	}
    	if(search.isHasAlternateSamplingMethod() &&(search.getAlternateSamplingMethod() == null || search.getAlternateSamplingMethod().isEmpty()))
    	{
    		addActionMessage("Please specify an alternate sampling method.");
    		return "federal.sampling.request.error";
    	}
    	
    	if((search.getAlternateSamplingMethod() != null && !search.getAlternateSamplingMethod().isEmpty()) && !search.isHasAlternateSamplingMethod())
    	{
    		addActionMessage("Please select \"Yes\" to specify that you wish to use a sampling method other than SRS");
    		return "federal.sampling.request.error";
    	}
    	
    	if(search.isHasAlternateSamplingMethod() &&(search.getAlternateSamplingMethod() != null && search.getAlternateSamplingMethod().length() > 3999))
    	{
    		addActionMessage("The \"Alternate Sampling Method\" text should not exceed 3999 characters");
    		return "federal.sampling.request.error";
    	}
    	
    	List<State> states = (List<State>) application.get("stateList_key");
    	for(State state : states)
    	{
    		if(search.getSelectedState().intValue() ==  state.getId().intValue())
    		{
    			search.setSelectedStateName(state.getStateName());
    			break;
    		}
    	}
    	return SUCCESS;
    }
    
    @SkipValidation   
    public void setFederalSamplingService(FederalSamplingService federalSamplingService) {
        this.federalSamplingService = federalSamplingService;
    }

    private void prepareSearchCriteria()
    {
    //	search.setCohortList(complianceService.getCohortList());
    	search.setCohortList(complianceService.getSamplingCohortList());
    	
    	if (search.getAvailableStates() != null
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
    					states = (List<State>) application.get("stateList_key");
    				}
    				
    				search.getAvailableStates().clear();
    				search.getAvailableStates().addAll(states);
    				/*for (State state : states)
    				{
    					if (search.getSelectedStateName() != null
    							&& search.getSelectedStateName().contains(state.getAbbreviation()))
    					{
    						search.getAvailableStateName().remove(state.getAbbreviation());
    						continue;
    					}
    					else if (!search.getAvailableStateName().contains(state.getAbbreviation()))
    						search.getAvailableStateName().add(state.getAbbreviation());
    				}*/
    			}
    	
    }
    
    private Message sendMessages(int subjectFlag)
	{
		Message msg = null;
		List<SiteUser> stUser = new ArrayList<SiteUser>();
		SiteUser user = null;
		Map<String, Object> namedParams = new HashMap<String, Object>();
		List<SiteUser> msgRecipients = new ArrayList<SiteUser>();
		
		/*namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("cohortNumber", search.getSelectedCohortName());
		namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);*/
		
		State state = userService.lookupState(search.getSelectedStateName());
		//adding state users
		msgRecipients.addAll(lookupService.getStateUsers(state));
		//adding system admin
		msgRecipients.addAll(lookupService.getSystemAdminUsers());
		//adding  Federal Users - CB Central office users
		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
		//adding Regional Users - Regional users
		msgRecipients.addAll(lookupService.getFederalUsers());
		
		msg = new Message();
		msg = messageServiceP3.prepareSystemMessage(msg);
		if(subjectFlag == 1)
		{
			msg.setSubject(SUBJECT_REJECTION_SAMPLING_REQUEST+Calendar.getInstance().getTime()+" - State: "+search.getSelectedStateName()+" - Cohort Name: "+search.getSelectedCohortName());
		//	msg = messageServiceP3.createSystemMessage(
		//	SUBJECT_REJECTION_SAMPLING_REQUEST, namedParams);
		}else if(subjectFlag == 2){
			msg.setSubject(SUBJECT_COMMENTS_SAMPLING_REQUEST+Calendar.getInstance().getTime()+" - State: "+search.getSelectedStateName()+" - Cohort Name: "+search.getSelectedCohortName());
			//msg.setMessageBody(search.getSamplingRequestComment());
		}
		msg.setMessageBody(search.getSamplingRequestComment());
		msg = messageServiceP3.sendMessage(msg, msgRecipients);
		return msg;
	}
    
    private Message sendRequestSubmittedMessages()
    {
    	System.out.println("in sendRequestSubmittedMessages");
    	Message msg = null;
    	List<SiteUser> stUser = new ArrayList<SiteUser>();
		SiteUser user = null;
		Map<String, Object> namedParams = new HashMap<String, Object>();
		List<SiteUser> msgRecipients = new ArrayList<SiteUser>();
		
		/*namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("cohortNumber", search.getSelectedCohortName());*/
		State state = userService.lookupState(search.getSelectedStateName());
		namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
		namedParams.put("cohortNumber", search.getSelectedCohort());
		System.out.println("selectedStateName:"+search.getSelectedStateName());	
		//adding state users
		msgRecipients.addAll(lookupService.getStateUsers(state));
		//adding system admin
		msgRecipients.addAll(lookupService.getSystemAdminUsers());
		//adding Regional Users - Regional users
		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
		//adding  Federal Users - CB Central office users
	//	msgRecipients.addAll(lookupService.getFederalUsers());
		System.out.println("create System msg");
		msg = messageServiceP3.createSystemMessage(
				MessageService.SAMPLING_REQUEST_SUBMITTED, namedParams);
		System.out.println("sendSystemMessage");
		messageServiceP3.sendSystemMessage(msg, msgRecipients);
    	
    	return msg;
    }
    
    private void sendApprovalMessages()
	{
		Message dataExportMsg = null;
		List<SiteUser> stUser = new ArrayList<SiteUser>();
		SiteUser user = null;
		Map<String, Object> namedParams = new HashMap<String, Object>();
		List<SiteUser> msgRecipients = new ArrayList<SiteUser>();
		
		namedParams.put("stateName", search.getSelectedStateName());
		namedParams.put("insertMethod", search.getSelectedSamplingMethodName());
		namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
		namedParams.put("cohortNumber", search.getSelectedCohortName());
		State state = userService.lookupState(search.getSelectedStateName());
		//adding state users
		msgRecipients.addAll(lookupService.getStateUsers(state));
		//adding system admin
		msgRecipients.addAll(lookupService.getSystemAdminUsers());
		
		//adding Regional Users - Regional users
		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
		//adding  Federal Users - CB Central office users		
		msgRecipients.addAll(lookupService.getFederalUsers());
		
		Iterator<SiteUser> userItr = msgRecipients.iterator();
		while(userItr.hasNext())
		{
			user = userItr.next();
			namedParams.remove("firstName");
			namedParams.remove("lastName");
			namedParams.put("firstName", user.getFirstName());
			namedParams.put("lastName", user.getLastName());
			
			
			dataExportMsg = messageServiceP3.createSystemMessage(
			MessageService.SAMPLING_REQUEST_APPROVED, namedParams);
			
			stUser.clear();
			stUser.add(user);		
		
			messageServiceP3.sendSystemMessage(dataExportMsg, stUser);
		}
	}
    
    private void sendImportSampleMessage(String importStatus)
    {
    	Message dataExportMsg = null;
		List<SiteUser> stUser = new ArrayList<SiteUser>();
		SiteUser user = null;
		Map<String, Object> namedParams = new HashMap<String, Object>();
		List<SiteUser> msgRecipients = new ArrayList<SiteUser>();
		String messageTemp = null;
		VwSamplingRequest vwSamplingRequest = federalSamplingService.getSamplingRquest(new Long(getServletRequest().getParameter("samplingRequestId")));
    	if(importStatus.equalsIgnoreCase("Success") && vwSamplingRequest.getRequestStatus().equalsIgnoreCase("Sample Drawn")){
    		messageTemp = MessageService.SAMPLING_DRAWN;
    		State state = userService.lookupState(vwSamplingRequest.getStateName());
    		//adding state users
    		msgRecipients.addAll(lookupService.getStateUsers(state));
    		
    		//adding Regional Users - Regional users
    		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
    	}else if (importStatus.equalsIgnoreCase("Success") && vwSamplingRequest.getRequestStatus().equalsIgnoreCase("Sample Reimported")){
    		messageTemp = MessageService.SAMPLING_REIMPORT_DRAWN;
    		State state = userService.lookupState(vwSamplingRequest.getStateName());
    		//adding state users
    		msgRecipients.addAll(lookupService.getStateUsers(state));
    		
    		//adding Regional Users - Regional users
    		msgRecipients.addAll(lookupService.getRegionUsers(state.getRegion(),state));
    	}
    	else if(importStatus.equalsIgnoreCase(Constants.INCORRECT_FILE_FORMAT)){
    		messageTemp = MessageService.SAMPLING_FILE_FORMAT_ERROR;
    	}
    	else{
    		messageTemp = MessageService.SAMPLING_FILE_RECORDS_ERROR;
    		namedParams.put("errorRecords", vwSamplingRequest.getImportSummary() == null ? "" :vwSamplingRequest.getImportSummary());
    	}
    	namedParams.put("stateName", vwSamplingRequest.getStateName());
    	namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
    	namedParams.put("cohortNumber", vwSamplingRequest.getCohortName());
    	msgRecipients.addAll(lookupService.getSystemAdminUsers());
    	msgRecipients.addAll(lookupService.getFederalUsers());
    	Iterator<SiteUser> userItr = msgRecipients.iterator();
		while(userItr.hasNext())
		{
			user = userItr.next();
			namedParams.remove("firstName");
			namedParams.remove("lastName");
			/*if(user.getPrimaryUserRole()!=null && user.getPrimaryUserRole().getId() == 1)
			{
				namedParams.put("firstName", "System");
				namedParams.put("lastName", "Administrator");				
			} else {
				namedParams.put("firstName", user.getFirstName());
				namedParams.put("lastName", user.getLastName());				
			}*/			
			namedParams.put("firstName", user.getFirstName());
			namedParams.put("lastName", user.getLastName());
			dataExportMsg = messageServiceP3.createSystemMessage(
					messageTemp, namedParams);
			
			stUser.clear();
			stUser.add(user);		
		
			try
			{
				messageServiceP3.sendSystemMessage(dataExportMsg, stUser);
			}
			catch (Exception e)
			{	
				Log.debug("And exception occured"+e.getMessage());
				e.printStackTrace();
			}
		}
    	
    }
    public String exportCohortSet()
    {
    	/*if(cohortSearch == null)
    		cohortSearch = new CohortSearch();
    	cohortSearch.setSelectedPopulationType(19);g
    	cohortSearch.setSelectedState(search.getSelectedState().intValue());
    	cohortSearch.setSelectedCohorts(search.getSelectedCohort().intValue());*/
    	return "federal.sampling.exportCohortSet";
    }

}
