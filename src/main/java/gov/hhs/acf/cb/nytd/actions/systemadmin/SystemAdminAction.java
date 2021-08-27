package gov.hhs.acf.cb.nytd.actions.systemadmin;

import com.opensymphony.xwork2.Action;
import gov.hhs.acf.cb.nytd.actions.PaginatedSearch.SortDirection;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.ExtendedDueDateMessageService;
import gov.hhs.acf.cb.nytd.service.ExtendedDueDateService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Action class for system admin module that control:
 *  - Extend Submission Due Date
 */
@SuppressWarnings("serial")
public class SystemAdminAction extends SearchAction<ExtendedDueDateSearch> {
    protected final transient Logger log = Logger.getLogger(getClass());
    @Getter @Setter ExtendedDueDateSearch search;
    @Getter @Setter private Map<String, String> searchCriteria;
    @Getter @Setter private String sortColumn;
    @Getter @Setter private String reportPeriod;
    @Getter @Setter private String state;
    @Getter @Setter private String sortOrder;
    @Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
    @Getter @Setter private ExtendedDueDateService extendedDueDateService;
    @Getter @Setter private ExtendedDueDateMessageService extendedDueDateMessageService;
    @Getter @Setter private DataExtractionService dataExtractionService;
    @Getter @Setter private boolean defaultPage;
    @Getter @Setter private Map<String, String> defaultStates;
    @Getter @Setter private Map<String, String> availableStates;
    @Getter @Setter private String selectedReportPeriod;
    @Getter @Setter private String selectedStartReportingDate;
    @Getter @Setter private String selectedEndReportingDate;
    @Getter @Setter private String selectedTransmissionTypeName;
    @Getter @Setter private String selectedOutcomeAge;
    @Getter @Setter private String selectedSubmissionDate;
    @Getter @Setter private ArrayList<Long> editedState = new ArrayList<>();
    @Getter @Setter private String reasonToUpdateId;
    @Getter @Setter private Calendar adjustedExtendedDueDate;
    @Getter @Setter private Long dueDateId;
    @Getter @Setter private Long extendedDueDateId;
    @Getter @Setter private String slectedStateName;
    @Getter @Setter private String selectedExtendedDueDate;
    @Getter @Setter private String selectedReason;
    @Getter @Setter private String actionErrorMessage;
    @Getter @Setter private String formattedAdjDueDate;
    @Getter @Setter private String reasonToDelete;
    @Getter @Setter private Long selectedStateId;
    @Getter @Setter private Calendar deleteFormattedEDD;

    private String loggedinUser = "";
    private static final String SORT_DIRECTION = "sortDirection";
    private static final String SORT_COLUMN = "sortColumn";
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    

    /**
    * Prepare actions
    */
    @Override 
    public void prepare(){
        log.debug(" >>>>>>>>>>>>>>> inside SystemAdminAction"); 
        super.prepare();
        // when using the tab navigation the search object is not created
        if (search == null){
            search = new ExtendedDueDateSearch();
        }
        searchCriteria = new HashMap<>();
        SiteUser siteUser = (SiteUser) session.get("siteUser");
        loggedinUser = siteUser.getUserName();
        availableStates = dataExtractionService.getStates(siteUser);
        if(servletRequest.getParameterValues("editedState") != null) {
            String [] stts =  servletRequest.getParameterValues("editedState");
            defaultStates = new HashMap<>(0);
            for(int i=0 ; i< stts.length;i++) {
                defaultStates.put(stts[i], availableStates.get(stts[i]));
                availableStates.remove(stts[i]);
            }
        } else {
            defaultStates = new HashMap<>(0);
        }
    }

    /**
    * Get paginated search
    * @return ExtendedDueDateSearch paginated search
    */
    @Override
    protected ExtendedDueDateSearch getPaginatedSearch(){
        return getSearch();
    }

    /**
    * A method to be called on System Admin link click on the top nav
    * @return String SUCCESS
    */
    public final String systemAdminPage() {
        log.debug("SystemAdminAction.getRsystemAdminPageeport");
        return Action.SUCCESS;
    }

    /**
    * Search extended due date
    * @return String result SUCCESS/INPUT
    */
    public final String searchExtendedDueDate() {
        log.info("SystemAdminAction.searchSubmissionDueDate");
        if(defaultPage) {
            return SUCCESS;
        }
        if(!getActionErrors().isEmpty()) {
            return INPUT;
        }
        if (search == null) {
            search = new ExtendedDueDateSearch();
        }				
        // creates a map of search criteria
        prepareSearchCriteria();
        search.setState(state);
        search.setReportingPeriod(reportPeriod);
        if (state != null && !state.isEmpty() && !state.equals("0")) {
            search.setStateName(extendedDueDateService.getStateNameById(Long.valueOf(state)));
        }
        if (reportPeriod != null && !reportPeriod.isEmpty() && !reportPeriod.equals("0")) {
                search.setReportingPeriodName(extendedDueDateService.getReportPeriodNameById(Long.valueOf(reportPeriod)));
        }
        if(null!=StringUtils.trimToNull(sortColumn)) {
            search.setSortColumn(sortColumn); 
        } else {
            search.setSortColumn(getPaginatedSearch().getSortColumn());
        }
        sortOrder =  servletRequest.getParameter(SORT_DIRECTION);
        if(null!=sortOrder&&sortOrder.equalsIgnoreCase("ASC")) {
            search.setSortDirection(SortDirection.ASC);
            getPaginatedSearch().setSortDirection(SortDirection.ASC);
            sortOrder = "DESC";			
        } else {
            sortOrder = "ASC";
            search.setSortDirection(SortDirection.DESC);
            getPaginatedSearch().setSortDirection(SortDirection.DESC);
        }
        List<VwExtendedDueDate> vwExtendedDueDateList = extendedDueDateService.getExtendedDueDateData(search);
        List<VwExtendedDueDate> extendedDueDateList = getDisplayExtendedDueDateList(vwExtendedDueDateList);
        
        log.info("extendedDueDataList size: " + extendedDueDateList.size());

        return Action.SUCCESS;
    }
    
    /* 
    * This action method invokes when triggers the search button with/without search criteria 
    * and redirects to search page with search result data 
    */
    public final String postSubmissionDueDateSearchCriteria() {
        log.info(" inside .................... SystemAdminAction.searchSubmissionDueDate");
        return SUCCESS;
    }

    /* 
    * Export the data to Excel 
    */
    public String exportSubmissionDueDateData() {
        log.info("search.state: " + state + ", reportPeriod: " + reportPeriod + ", sortOrder: "
                + servletRequest.getParameter(SORT_DIRECTION) + ", sortColumn: " 
                + servletRequest.getParameter(SORT_COLUMN));
        if(null == StringUtils.trimToNull(servletRequest.getParameter(SORT_DIRECTION))) {
            search.setSortDirection(SortDirection.DESC);
        } else {
            search.setSortDirection(servletRequest.getParameter(SORT_DIRECTION).equalsIgnoreCase("DESC")? 
                SortDirection.DESC:SortDirection.ASC);
        }
        search.setSortColumn(null != StringUtils.trimToNull(servletRequest.getParameter(SORT_COLUMN))? 
            servletRequest.getParameter(SORT_COLUMN): "reportingPeriodName");
        search.setState(state);
        search.setReportingPeriod(reportPeriod);
        List<VwExtendedDueDate> vwExtendedDueDateList = getExtendedDueDateService().getExtendedDueDateData(search);
        List<VwExtendedDueDate> extendedDueDateList = getDisplayExtendedDueDateList(vwExtendedDueDateList);
        ExtendedDueDataExport exporter = new ExtendedDueDataExport(this, dataExtractionService);
        // export the data to csv
        return exporter.export(getServletResponse(), extendedDueDateList, "extendedduedate_"
                    + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    /* 
    * To clear the search data and reset the search page 
    */
    public String resetSubmissionDueDateData(){
        log.info("inside *********** resetSubmissionDueDateData state: " + state + ", reportPeriod: "
                + reportPeriod + ", sortColumn: " + sortColumn + ", sortOrder: " + sortOrder);
        state= "0";
        reportPeriod = "0";
        sortColumn = "reportPeriodName";
        sortOrder = "DESC";
        defaultPage = false;
        
        return SUCCESS;
    }

    /* 
    * This action method  is to redirect to view and edit pages
    */
    public String displayData() {
        return SUCCESS;
    }
    
    /* 
    * To display the edit EXTENDEDDUEDATE DATA page with selected data from search page to edit
    */
    public String editData() {
    	log.info("inside *********** editData extendedDueDateId: " + extendedDueDateId + ", state: " + state 
                + ", dueDateId: " + dueDateId + ", actionErrorMessage: " + actionErrorMessage 
                + ", reasonToUpdateId: " + reasonToUpdateId);
        VwExtendedDueDate vwExtndueDateData = getExtendedDueDateService().getExtendedDueDateByDueDateID(dueDateId,extendedDueDateId);
    	if(!defaultPage&&null == StringUtils.trimToNull(actionErrorMessage)) {
            return Action.INPUT;
    	}
    	/* handle the data in edit page with action error messages if any */
    	if (null != vwExtndueDateData) {
            VwExtendedDueDate extndueDateData = getDisplayExtendedDueDate(vwExtndueDateData);
            setSelectedValuesForEdit(extndueDateData);
            if(null != StringUtils.trimToNull(actionErrorMessage)) {
                addActionError(actionErrorMessage);
                return Constants.BAD_INPUT;
            }	
        }
    	
        log.info("selected state: " + defaultStates.values());
        return SUCCESS;
    }

    /* 
    * Display the EXTENDEDDUEDATE DATA on view page
    */
    public String viewEDDInfo() {
    	log.info("inside *********** viewEDDInfo state: " + state + ", dueDateId: " + dueDateId 
                + ",extendedDueDateId: "+extendedDueDateId);
        VwExtendedDueDate vwExtndueDateData = getExtendedDueDateService().getExtendedDueDateByDueDateID(dueDateId, extendedDueDateId);
    	if (null != vwExtndueDateData) {
            VwExtendedDueDate extndueDateData = getDisplayExtendedDueDate(vwExtndueDateData);
            setSelectedValuesForView(extndueDateData);
    	}
        
        return SUCCESS;
    }
    
    /* 
    * This action method validate the action errors before saving data. 
    * If no Action error mesages submit the data to save method in DAOImpl 
    */
    public String submitEDD() {
    	log.info("inside *********** submitEDD adjustedExtendedDueDate: " + adjustedExtendedDueDate
                +", reasonToUpdateId: " + reasonToUpdateId + ", extendedDueDateId: " + extendedDueDateId);
        VwExtendedDueDate extndueDateData = getExtendedDueDateService().getExtendedDueDateByDueDateID(dueDateId,extendedDueDateId);
        boolean initialUpdate = (extndueDateData.getReason() == null || extndueDateData.getReason().trim().isEmpty());
        setSelectedValuesForSubmit(extndueDateData);
    	/////////////////////////  Validation
        if(null == adjustedExtendedDueDate) {
            this.actionErrorMessage = "Please provide Extended Due Date";
            defaultPage = false;
            return Constants.BAD_INPUT;
    	} else if(null != extndueDateData.getSubmissionDate()&&
                adjustedExtendedDueDate.getTime().compareTo(extndueDateData.getSubmissionDate().getTime()) <= 0) {
            this.actionErrorMessage = "Extended Due Date should be greater than Submission Due Date";
            defaultPage = false;
            return Constants.BAD_INPUT;
    	} else if(null == StringUtils.trimToNull(reasonToUpdateId)) {
            this.actionErrorMessage = "Please provide Reason for Update or Delete";
            defaultPage = false;
            return Constants.BAD_INPUT;
    	}

        //////////////////////// Process
        // all state id list
        List<Long> allStateIdList = getExtendedDueDateService().getAllStateIds();
    	String status = "";
        List<ExtendedDueDate> eddList = new ArrayList<>();
        if(extndueDateData.getExtendedDueDateId() != null) {
            // update the selected state to edit - it should be one state even though building a list 
            eddList = buildUpdateEDDList(extndueDateData);
        } else {
            // save by building new records list for one or more states selected
            for (Long saveStateId : allStateIdList) {
                eddList.add(buildSaveEDD(saveStateId, extndueDateData));
            }
        }
        // Savinng/updating data to EXTENDEDDUEDATE TABLE
    	if(!eddList.isEmpty()) {
            log.info("eddList.size() is: " + eddList.size());
            status = getExtendedDueDateService().saveExtendedDueDate(eddList);
        }
    	log.info("after save *****");
    	// If Saving or Update Success then action will be redirect to Search Page or remains 
        // in edit page and display the action error message
    	if(null != StringUtils.trimToNull(status) && status.equalsIgnoreCase("SUCCESS")) {
            status = Action.SUCCESS;
            if(initialUpdate) {
                extendedDueDateMessageService.extendedDueDateCreated(extndueDateData, eddList);
            }
            else {
                extendedDueDateMessageService.extendedDueDateEdited(extndueDateData, eddList);
            }
    	} else {
            log.info("inside else *****");
            setSelectedValuesForValidation(extndueDateData);
            /* This condition is to display message on page when data saving for EXTENDEDUEDATE failed 
             * due to either duplicate records or failed with some other reason
             */
            if(status.equalsIgnoreCase("DUPLICATE")) {
                addActionError("DATA ALREADY EXISTS");
                status = Constants.BAD_INPUT;
            } else {
                addActionError("DATA NOT SAVED");
                status = Constants.BAD_INPUT;
            }
    	}
    	
    	return status;
    }
    
    /* 
    * This action method delete extended due date 
    */
    public String deleteEDD() {
    	log.info("inside *********** deleteEDD dueDateId: " + dueDateId + ", extendedDueDateId: " 
                + extendedDueDateId + ", reasonToDelete: " + reasonToDelete);
    	String status = "";
    	SimpleDateFormat formateDate = new SimpleDateFormat(DATE_FORMAT);
    	VwExtendedDueDate extndueDateData = getExtendedDueDateService().getExtendedDueDateByDueDateID(dueDateId,extendedDueDateId);
    	reasonToUpdateId = (null != StringUtils.trimToNull(reasonToUpdateId))? reasonToUpdateId: "";
    	formattedAdjDueDate = (null != deleteFormattedEDD)?formateDate.format(deleteFormattedEDD.getTime()): "";
    	log.info(" inside *********** deleteEDD formattedAdjDueDate: "+formattedAdjDueDate);
    	if(null != StringUtils.trimToNull(reasonToDelete)) {
            this.actionErrorMessage = "Please provide Reason for Update or Delete";
            defaultPage = false;
            return Constants.BAD_INPUT;
    	}
    	if (null != extndueDateData) {
            ExtendedDueDate deleteEDD = buildDeleteEDD(extndueDateData);
    	    status = getExtendedDueDateService().deleteExtendedDueDateData(deleteEDD);
            log.info("Deleting extended due date - stateid: " + extndueDateData.getEddStateId() + ", deleteEDD: " + status);

            // now insert a record with a default duedate for the state so that being able to edit again.
            List<ExtendedDueDate> defaultEDDList = buildDefaultEDDList(extndueDateData);
            if(!defaultEDDList.isEmpty()) {
    		status = getExtendedDueDateService().saveExtendedDueDate(defaultEDDList);
                log.info("Inserting default due date on DELETE - stateid: " 
                        + extndueDateData.getEddStateId() + ", defaultEDD: " + status);
            }
            extendedDueDateMessageService.extendedDueDateDeleted(extndueDateData,defaultEDDList);
    	}
    	
    	return SUCCESS;
    }

    /* 
    * This action prepare search criteria 
    */
    private void prepareSearchCriteria() {
        searchCriteria.put(SORT_COLUMN, sortColumn);
        searchCriteria.put("SORTORDER", sortOrder);
        if (null!= reportPeriod && !reportPeriod.equals("0") && !reportPeriod.isEmpty()) {
                searchCriteria.put("REPORTPERIOD", reportPeriod);
        }
        if (null!=state && !state.equals("0")) {
                searchCriteria.put("STATE", state);
        }
    }
    
    /* 
    * Set selected values for Edit page.
    */
    private void setSelectedValuesForEdit(VwExtendedDueDate extndueDateData) {
        SimpleDateFormat formateDate = new SimpleDateFormat(DATE_FORMAT);
        selectedEndReportingDate = (null != extndueDateData.getEndReportingDate())? 
                formateDate.format(extndueDateData.getEndReportingDate().getTime()): "";
        selectedStartReportingDate = (null != extndueDateData.getStartReportingDate())?
                formateDate.format(extndueDateData.getStartReportingDate().getTime()): "";
        selectedReportPeriod = (null != StringUtils.trimToNull(extndueDateData.getReportingPeriodName()))? 
                extndueDateData.getReportingPeriodName(): "";
        selectedTransmissionTypeName = (null != StringUtils.trimToNull(extndueDateData.getTransmissionTypeName()))? 
                extndueDateData.getTransmissionTypeName(): "";
        selectedOutcomeAge = (null != extndueDateData.getOutComeAge())?
                extndueDateData.getOutComeAge().toString(): "";
        selectedSubmissionDate = (null != extndueDateData.getSubmissionDate())? 
                formateDate.format(extndueDateData.getSubmissionDate().getTime()): "";
        String stateId = (null != extndueDateData.getEddStateId())?extndueDateData.getEddStateId().toString(): "";
        selectedStateId = (null != StringUtils.trimToNull(stateId))?
                Long.parseLong(stateId):0;
        slectedStateName = (null != extndueDateData.getEddStateName())?
                extndueDateData.getEddStateName(): "";
        if(null == formattedAdjDueDate) {
            formattedAdjDueDate = (null != extndueDateData.getExtendedDueDate())?
                    formateDate.format(extndueDateData.getExtendedDueDate().getTime()): "";   
        }
        reasonToUpdateId = (null != extndueDateData.getReason())?extndueDateData.getReason(): "";
        if(null != extndueDateData.getEddStateId()) {
            defaultStates.put(stateId, availableStates.get(stateId));
        } else {
            defaultStates.put("0", availableStates.get("0"));
        }
    }
    
    /* 
    * Set selected values for View page.
    */
    private void setSelectedValuesForView(VwExtendedDueDate extndueDateData) {
        SimpleDateFormat formateDate = new SimpleDateFormat(DATE_FORMAT);
        selectedEndReportingDate = (null != extndueDateData.getEndReportingDate())? 
                formateDate.format(extndueDateData.getEndReportingDate().getTime()): "";
        selectedStartReportingDate = (null != extndueDateData.getStartReportingDate())?
                formateDate.format(extndueDateData.getStartReportingDate().getTime()): "";
        selectedReportPeriod = (null != StringUtils.trimToNull(extndueDateData.getReportingPeriodName()))? 
                extndueDateData.getReportingPeriodName(): "";
        selectedTransmissionTypeName = (null != StringUtils.trimToNull(extndueDateData.getTransmissionTypeName()))? 
                extndueDateData.getTransmissionTypeName(): "";
        selectedOutcomeAge = (null != extndueDateData.getOutComeAge())?
                extndueDateData.getOutComeAge().toString(): "";
        selectedSubmissionDate = (null != extndueDateData.getSubmissionDate())? 
                formateDate.format(extndueDateData.getSubmissionDate().getTime()): "";
        selectedExtendedDueDate = (null != extndueDateData.getExtendedDueDate())?
                formateDate.format(extndueDateData.getExtendedDueDate().getTime()):"";
        slectedStateName = (null != StringUtils.trimToNull(extndueDateData.getEddStateName()))?
                extndueDateData.getEddStateName():"All";
        selectedReason = (null != StringUtils.trimToNull(extndueDateData.getReason()))?
                extndueDateData.getReason():"";
    }
    
    /* 
    * Set selected values for Submit action from conformation page.
    */
    private void setSelectedValuesForSubmit(VwExtendedDueDate extndueDateData) {
        SimpleDateFormat formateDate = new SimpleDateFormat(DATE_FORMAT);
        selectedSubmissionDate = (null != extndueDateData.getSubmissionDate())? 
                formateDate.format(extndueDateData.getSubmissionDate().getTime()): "";
        formattedAdjDueDate = (null != adjustedExtendedDueDate)?
                formateDate.format(adjustedExtendedDueDate.getTime()):null;
        reasonToUpdateId = (null != StringUtils.trimToNull(reasonToUpdateId))?
                reasonToUpdateId:"";
    }
    
    /* 
    * Set selected values for validation from update page.
    */
    private void setSelectedValuesForValidation(VwExtendedDueDate extndueDateData) {
        SimpleDateFormat formateDate = new SimpleDateFormat(DATE_FORMAT);
        selectedEndReportingDate = (null != extndueDateData.getEndReportingDate())? 
                formateDate.format(extndueDateData.getEndReportingDate().getTime()): "";
        selectedStartReportingDate = (null != extndueDateData.getStartReportingDate())?
                formateDate.format(extndueDateData.getStartReportingDate().getTime()): "";
        selectedReportPeriod = (null != StringUtils.trimToNull(extndueDateData.getReportingPeriodName()))? 
                extndueDateData.getReportingPeriodName(): "";
        selectedTransmissionTypeName = (null != StringUtils.trimToNull(extndueDateData.getTransmissionTypeName()))? 
                extndueDateData.getTransmissionTypeName(): "";
        selectedOutcomeAge = (null != extndueDateData.getOutComeAge())?
                extndueDateData.getOutComeAge().toString(): "";
        selectedSubmissionDate = (null != extndueDateData.getSubmissionDate())? 
                formateDate.format(extndueDateData.getSubmissionDate().getTime()): "";
        formattedAdjDueDate = (null != adjustedExtendedDueDate)?
                formateDate.format(adjustedExtendedDueDate.getTime()): "";   
        reasonToUpdateId = (null != StringUtils.trimToNull(reasonToUpdateId))? 
                reasonToUpdateId: "";
    }
    
    /* 
    * Build extended due date list to update.
    */
    private List<ExtendedDueDate> buildUpdateEDDList(VwExtendedDueDate extndueDateData) {
        List<ExtendedDueDate> updateEddList = new ArrayList<>();
        ExtendedDueDate updateEDD = new ExtendedDueDate();
        updateEDD.setId(extndueDateData.getExtendedDueDateId());
        updateEDD.setCreatedBy(null != StringUtils.trimToNull(extndueDateData.getCreatedBy())?extndueDateData.getCreatedBy():loggedinUser);
        updateEDD.setCreatedDate(null != extndueDateData.getCreatedDate() ? extndueDateData.getCreatedDate() : Calendar.getInstance());
        updateEDD.setDeleteFlag(extndueDateData.getDeleteFlag());
        updateEDD.setStateId(extndueDateData.getEddStateId());
        updateEDD.setDueDateId(extndueDateData.getDueDateId());
        updateEDD.setUpdateBy(null != StringUtils.trimToNull(loggedinUser)?loggedinUser:getUserName());
        updateEDD.setUpdateDate(Calendar.getInstance());
        updateEDD.setExtendedDueDateCal(adjustedExtendedDueDate);
        updateEDD.setReason(reasonToUpdateId);
        updateEddList.add(updateEDD);

        return updateEddList;
    }
    
    /* 
    * Build extended due date to save.
    */
    private ExtendedDueDate buildSaveEDD(Long saveStateId, VwExtendedDueDate extndueDateData) {
        ExtendedDueDate saveEDD = new ExtendedDueDate();
        saveEDD.setDeleteFlag('0');
        saveEDD.setStateId(saveStateId);
        saveEDD.setDueDateId(extndueDateData.getDueDateId());
        // If All or multi-state selected, set the extended due date to input value for the state(s)
        if(editedState.contains(Long.valueOf("0")) || editedState.contains(saveStateId)) { // extend due date
            saveEDD.setCreatedBy(null != StringUtils.trimToNull(loggedinUser)?loggedinUser:getUserName());
            saveEDD.setUpdateBy(null != StringUtils.trimToNull(loggedinUser)?loggedinUser:getUserName());
            saveEDD.setCreatedDate(Calendar.getInstance());
            saveEDD.setUpdateDate(Calendar.getInstance());
            saveEDD.setExtendedDueDateCal(adjustedExtendedDueDate);
            saveEDD.setReason(reasonToUpdateId);
        } else { // Default
            saveEDD.setCreatedBy("system");
            saveEDD.setUpdateBy("system");
            Calendar cal = new GregorianCalendar(1900, 0, 1, 0, 0, 0); // default set to 1/1/1900
            saveEDD.setCreatedDate(cal);
            saveEDD.setUpdateDate(cal);
            saveEDD.setExtendedDueDateCal(extndueDateData.getSubmissionDate());
        }
        return saveEDD;
    }
    
    /* 
    * Build extended due date list to delete.
    */
    private ExtendedDueDate buildDeleteEDD(VwExtendedDueDate extndueDateData) {
        ExtendedDueDate eddToDelete = new ExtendedDueDate();
        eddToDelete.setId(extndueDateData.getExtendedDueDateId());
        eddToDelete.setCreatedBy(null != StringUtils.trimToNull(extndueDateData.getCreatedBy())?
                extndueDateData.getCreatedBy():loggedinUser);
        eddToDelete.setCreatedDate(null != extndueDateData.getCreatedDate()?
                extndueDateData.getCreatedDate():Calendar.getInstance());
        eddToDelete.setDeleteFlag(extndueDateData.getDeleteFlag());
        eddToDelete.setStateId(extndueDateData.getEddStateId());
        eddToDelete.setDueDateId(extndueDateData.getDueDateId());
        eddToDelete.setUpdateBy(null != StringUtils.trimToNull(loggedinUser)?loggedinUser:getUserName());
        eddToDelete.setUpdateDate(Calendar.getInstance());
        eddToDelete.setExtendedDueDateCal(null != extndueDateData.getExtendedDueDate()?
                extndueDateData.getExtendedDueDate():Calendar.getInstance());
        eddToDelete.setReason(reasonToDelete);
        eddToDelete.setDeleteFlag('1');
        
        return eddToDelete;
    }
    
    /* 
    * Build extended due date list to insert default extended due date upon delete.
    */
    private List<ExtendedDueDate> buildDefaultEDDList(VwExtendedDueDate extndueDateData) {
        List<ExtendedDueDate> defaultEDDList = new ArrayList<>();
        ExtendedDueDate defaultEDD = new ExtendedDueDate();
        defaultEDD.setCreatedBy(null != StringUtils.trimToNull(loggedinUser)? loggedinUser:getUserName());
        defaultEDD.setCreatedDate(Calendar.getInstance());
        defaultEDD.setDeleteFlag('0');
        defaultEDD.setStateId(extndueDateData.getEddStateId());
        defaultEDD.setDueDateId(extndueDateData.getDueDateId());
        defaultEDD.setUpdateBy(null != StringUtils.trimToNull(loggedinUser)? loggedinUser:getUserName());
        defaultEDD.setUpdateDate(Calendar.getInstance());
        defaultEDD.setExtendedDueDateCal(extndueDateData.getSubmissionDate());
        defaultEDDList.add(defaultEDD);
        
        return defaultEDDList;
    }
    
    /* 
    * This method converts VwExtendedDueDate list to return the list with empty values
    * for updateBy, updatedDate, extendedDueDate if the due date is not extended.
    */
    private List<VwExtendedDueDate> getDisplayExtendedDueDateList(List<VwExtendedDueDate> vwExtendedDueDateList) {
        List<VwExtendedDueDate> displayExtendedDueDateList = new ArrayList<>();
        for (VwExtendedDueDate vwExtendedDueDate : vwExtendedDueDateList) {
            if(vwExtendedDueDate.getExtendedDueDate() != null && vwExtendedDueDate.getSubmissionDate() != null) {
                boolean isSameDay = 
                        DateUtils.isSameDay(vwExtendedDueDate.getExtendedDueDate(), vwExtendedDueDate.getSubmissionDate());
                if(isSameDay) {
                    vwExtendedDueDate.setExtendedUpdateDate(null);
                    vwExtendedDueDate.setExtendedUpdateBy("");
                    vwExtendedDueDate.setExtendedDueDate(null);
                }
            }
            displayExtendedDueDateList.add(vwExtendedDueDate);
        }
        
        return displayExtendedDueDateList;
    }
    
    /* 
    * This method converts VwExtendedDueDate to return empty values
    * for extendedDueDate and reason.
    */
    private VwExtendedDueDate getDisplayExtendedDueDate(VwExtendedDueDate vwExtendedDueDate) {
        if(vwExtendedDueDate.getExtendedDueDate() != null && vwExtendedDueDate.getSubmissionDate() != null) {
            boolean isSameDay = 
                    DateUtils.isSameDay(vwExtendedDueDate.getExtendedDueDate(), vwExtendedDueDate.getSubmissionDate());
            if(isSameDay) {
                vwExtendedDueDate.setExtendedDueDate(null);
                vwExtendedDueDate.setReason("");
            }
        }
        
        return vwExtendedDueDate;
    }
    /* 
    * Handler to read serializable objects (i.e.) session, request etc. 
    */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        log.info("reading serialization object");
        in.defaultReadObject();
    }

    /* 
    * Handler to write serializable objects (i.e.) session, request etc. 
    */
    private void writeObject(ObjectOutputStream out) throws IOException {
        log.info("writing serialization object");
        out.defaultWriteObject();
    }

}