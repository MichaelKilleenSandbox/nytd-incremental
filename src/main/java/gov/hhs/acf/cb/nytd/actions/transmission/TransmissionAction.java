/*
 * Filename: TransmissionAction.java
 *
 * Copyright 2009, ICF International
 * Created: Jun 5, 2009
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
package gov.hhs.acf.cb.nytd.actions.transmission;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.actions.message.MessageSearch;
import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwNote;
import gov.hhs.acf.cb.nytd.models.helper.VwTransmissionStatus;
import gov.hhs.acf.cb.nytd.service.*;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 15178, 18922 (Satinder Gill), 16939, 13873 (Grant Lewis)
 */
@SuppressWarnings("serial")
public class TransmissionAction extends SearchAction<TransmissionSearch> {
    // default logger
    protected final Logger log = Logger.getLogger(getClass());

    private final SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd");

    // current transmission
    @Getter
    @Setter
    Transmission transmission;

    // transmission detail object
    @Getter
    @Setter
    TransmissionDetail detail;

    // transmission search object
    @Getter
    @Setter
    TransmissionSearch search;

    @Getter
    @Setter
    String stateUserEddMessage;

    @Getter
    @Setter
    ArrayList<String> regionalUserEddMessage;

    // messages displayed to user
    @Getter
    @Setter
    private String countdownMessage;

    @Getter
    @Setter
    private String submissionMessage;

    @Getter
    @Setter
    private String deletionMessage;

    // dashboard properties (latest 5 messages and transmissions)
    @Getter
    @Setter
    private List<Message> dashboardMessages;

    @Getter
    @Setter
    private List<VwTransmissionStatus> dashboardTransmissions;

    // service objects (injected by Spring)
    @Getter
    @Setter
    private DataExtractionService dataExtractionService;

    @Getter
    @Setter
    private TransmissionServiceP3 transmissionServiceP3;

    @Getter
    @Setter
    private MessageService messageServiceP3;

    @Getter
    @Setter
    private ExtendedDueDateService extendedDueDateService;

    @Getter
    @Setter
    private boolean activeEDDs;

    @Getter
    @Setter
    private List<VwExtendedDueDate> vwExtendedDueDateList;

    @Getter
    @Setter
    private String userType;

    protected TransmissionSearch getPaginatedSearch() {
        return getSearch();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // ACTION METHODS
    // ------------------------------------------------------------------------------------------------------------------


    /**
     * Called if javascript is disabled. Takes user to submission confirmation
     * page
     */
    @SkipValidation
    public final String confirmSubmission() {
        setTransmission(getTransmissionServiceP3().getTransmission(this.transmission.getId()));

        return Action.SUCCESS;
    }

    /**
     * Called if javascript is disabled. Takes user to deletion confirmation
     * page
     */
    @SkipValidation
    public final String confirmDeletion() {
        log.debug("in Confirm Deletion");
        setTransmission(getTransmissionServiceP3().getTransmission(this.transmission.getId()));

        return Action.SUCCESS;
    }

    /**
     * Displays the dashboard
     *
     * @return
     */
    @SkipValidation
    public final String dashboard() {
        // calculate the days, hours and minutes remaining for submission
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        ReportingPeriod reportPeriod = transmissionServiceP3.getCurrentReportingPeriod();
        if (reportPeriod != null) {
            Calendar deadline = transmissionServiceP3.getSubmissionDeadline(reportPeriod);

            long delta = deadline.getTimeInMillis() - today.getTimeInMillis();
            long millisPerDay = 1000 * 60 * 60 * 24;
            long millisPerHour = 1000 * 60 * 60;
            long millisPerMin = 1000 * 60;
            long days = delta / millisPerDay;
            long hours = (delta - (days * millisPerDay)) / millisPerHour;
            long mins = (delta - (days * millisPerDay) - (hours * millisPerHour)) / millisPerMin;

            // set the countdown message
            List messageArgs = Arrays.asList(days, hours, mins, reportPeriod.getName());
            countdownMessage = getText("submission.countdownMessage", messageArgs);
        }

        //check for current extended due dates
        Calendar date = transmissionServiceP3.getSubmissionDeadline(reportPeriod);

        ExtendedDueDateSearch eddSearch = new ExtendedDueDateSearch(date);

        eddSearch.setPageSize(0);

        vwExtendedDueDateList = extendedDueDateService.getExtendedDueDateData(eddSearch);


        UserRoleEnum role = UserRoleEnum.getRole(getPrimaryUserRole().getName());
        switch (role) {
            case ADMIN:
                eddsForSysAdmin();
                break;
            case FEDERAL:
               eddForFederal();
                break;
            case REGIONAL:
                eddsForRegions();
                break;
            case STATE:
                eddForState();
                break;
        }

        // load the dashboard transmissions for current user
        initSearch();
        getTransmissionServiceP3().getDashboardTransmissions(search);
        this.dashboardTransmissions = search.getPageResults();

        // load the dashboard messages
        MessageSearch messageSearch = new MessageSearch();
        messageSearch.setPageSize(5);
        messageSearch.setUser(getUser());
        this.dashboardMessages = messageServiceP3.search(messageSearch).getPageResults();

        return Action.SUCCESS;
    }

    /**
     *
     *
     */
    private void eddForFederal() {
        userType = "federal";
        if(vwExtendedDueDateList.isEmpty()) {
            activeEDDs = false;
            return;
        }
        regionalUserEddMessage = new ArrayList<>();

        for (VwExtendedDueDate s : vwExtendedDueDateList) {

            StringBuilder res = new StringBuilder();
            Calendar eddDate = s.getExtendedDueDate();

            activeEDDs = true;
            res.append(s.getEddStateName())
                    .append("'s submission due date has been extended to ")
                    .append(formattedDate.format(eddDate.getTime()))
                    .append(".")
                    .append("\n");
            regionalUserEddMessage.add(res.toString());
            session.put("hasAlreadyViewedPopup", "yes");
        }
    }

    /**
     *
     */
    private void eddsForSysAdmin() {
        userType = "sysadmin";
        if(vwExtendedDueDateList.isEmpty()) {
            activeEDDs = false;
            return;
        }
        activeEDDs = true;
        regionalUserEddMessage = new ArrayList<>();
        regionalUserEddMessage.add("The default submission due date has been extended for select states");
        regionalUserEddMessage.add("Go to System Admin > Extended Submission Due Date page to see which");
        regionalUserEddMessage.add("states have an extended due date.");

    }

    /**
     *
     */
    private void eddsForRegions() {
        userType = "regional";
        if(vwExtendedDueDateList.isEmpty()) {
            activeEDDs = false;
            return;
        }
        regionalUserEddMessage = new ArrayList<>();

        State[] regUserStates = user.getRegion().getStates().toArray(new State[0]);

        HashMap<String, Boolean> regionalStates = new HashMap<>();

        for (State x : regUserStates) {
            regionalStates.put(x.getStateName(), true);
        }

        for (VwExtendedDueDate s : vwExtendedDueDateList) {
            if (regionalStates.containsKey(s.getEddStateName())) {
                StringBuilder res = new StringBuilder();
                Calendar eddDate = s.getExtendedDueDate();
                activeEDDs = true;
                res.append(s.getEddStateName())
                        .append("'s submission due date has been extended to ")
                        .append(formattedDate.format(eddDate.getTime()))
                        .append(".")
                        .append("\n");
                regionalUserEddMessage.add(res.toString());
            }
        }
    }

    /**
     *
     */
    private void eddForState() {
        userType = "state";
        if(vwExtendedDueDateList.isEmpty()) {
            activeEDDs = false;
            return;
        }
        regionalUserEddMessage = new ArrayList<>();
        String usersState = user.getState().getStateName();
        // check if state users state has extended due date to display
        vwExtendedDueDateList.stream().filter(a -> a.getEddStateName().equalsIgnoreCase(usersState))
                .forEach(s -> {
                    activeEDDs = true;
                    Calendar eddDate = s.getExtendedDueDate();
                    regionalUserEddMessage.add(usersState + "'s submission due date has been extended to " +
                            formattedDate.format(eddDate.getTime()) + ".");
                });
    }

    @SkipValidation
    public final String datumNotes() {
        // load the list from the database
        getTransmissionServiceP3().getDatumNotes(search);

        // push the list on the value stack
        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        stack.set("datumNotes", search.getPageResults());

        return Action.SUCCESS;
    }

    @SkipValidation
    public final String elementNotes() {


        // load the list from the database
        getTransmissionServiceP3().getElementNotes(search);

        // push the list on the value stack
        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        stack.set("elementNotes", search.getPageResults());

        return Action.SUCCESS;
    }

    @SkipValidation
    public final String exportDatumNotes() {
        // load the list from the database
        getTransmissionServiceP3().getDatumNotes(search);

        // create the exporter
        ExportableTable<VwNote> exporter = NoteExport.forDatum(this, dataExtractionService);

        // export the results
        return exporter.export(getServletResponse(), search.getPageResults(), "datumNotes_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    @SkipValidation
    public final String exportElementNotes() {
        // load the list from the database
        getTransmissionServiceP3().getElementNotes(search);

        // create the exporter
        ExportableTable<ElementNote> exporter = NoteExport.forElement(this, dataExtractionService);

        // export the results
        return exporter.export(getServletResponse(), search.getPageResults(), "elementNotes_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    @SkipValidation
    public final String exportRecordNotes() {
        // load the list from the database
        getTransmissionServiceP3().getRecordNotes(search);

        // create the exporter
        ExportableTable<VwNote> exporter = NoteExport.forRecord(this, dataExtractionService);

        // export the results
        return exporter.export(getServletResponse(), search.getPageResults(), "recordNotes_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    @SkipValidation
    public final String exportSearchResults() {
        // execute the current search
        initSearch();
        getTransmissionServiceP3().searchTransmissions(search, user);

        // create the exporter
        TransmissionExport exporter = new TransmissionExport(this, dataExtractionService, user);

        // export the results to CSV
        return exporter.export(getServletResponse(), search.getPageResults(), "transmissionSearchResults_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    @SkipValidation
    public final String exportTransmissionNotes() {
        // load the list from the database
        List<TransmissionNote> notes = getTransmissionServiceP3().getTransmissionNotes(
                search.getTransmissionId());

        // create the exporter
        ExportableTable<TransmissionNote> exporter = NoteExport.forTransmission(this, dataExtractionService);

        // export the results
        return exporter.export(getServletResponse(), notes, "transmissionNotes_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    /**
     * Called prior to the execute method from the prepare interceptor. We use it
     * to retrieve the user object from the session and set the current date and
     * time
     */
    @Override
    public void prepare() {
        super.prepare();

        // when using the tab navigation the search object is not created
        if (search == null) {
            search = new TransmissionSearch();
        }
    }

    @SkipValidation
    public final String recordNotes() {
        // load the list from the database
        getTransmissionServiceP3().getRecordNotes(search);

        // push the list on the value stack
        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        stack.set("recordNotes", search.getPageResults());

        return Action.SUCCESS;
    }

    @SkipValidation
    public final String resetSearch() {
        return SUCCESS;
    }

    @SkipValidation
    public final String searchTransmissions() {
        initSearch();

        // Workaround for an intermittent bug where dates will not already be decoded by Struts
        if (search.getTransmissionStartDate() != null) {
            search.setTransmissionStartDate(urlDecode(search.getTransmissionStartDate()));
        }
        if (search.getTransmissionEndDate() != null) {
            search.setTransmissionEndDate(urlDecode(search.getTransmissionEndDate()));
        }

        transmissionServiceP3.searchTransmissions(search, user);
        return SUCCESS;
    }

    @Validations(
            regexFields = {
                    @RegexFieldValidator(fieldName = "search.transmissionStartDate", regex = "\\d{1,2}\\/\\d{1,2}/\\d{4}",
                            message = "The Start Date must be in the MM/DD/YYYY format"),
                    @RegexFieldValidator(fieldName = "search.transmissionEndDate", regex = "\\d{1,2}\\/\\d{1,2}/\\d{4}",
                            message = "The End Date must be in the MM/DD/YYYY format")})
    public final String postTransmissionSearchCriteria() {
        if (getSearch().getTransmissionStartDate() != null && getSearch().getTransmissionEndDate() != null
                && !getSearch().getTransmissionStartDate().isEmpty() && !getSearch().getTransmissionEndDate().isEmpty()) {
            Date startDate, endDate;
            try {
                startDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(getSearch().getTransmissionStartDate());
                endDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(getSearch().getTransmissionEndDate());
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
                return ERROR;
            }
            if (startDate.after(endDate)) {
                addActionError("Start Date must not be after End Date");
                return INPUT;
            }
        }
        return SUCCESS;
    }

    /**
     * Submits a transmission.
     *
     * @return the user is returned to the dashboard or search page with respect
     * to the page from where transmission was selected for submission
     */
    @SkipValidation
    public final String submitTransmission() {
        SiteUser siteUser = (SiteUser) session.get("siteUser");
        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        Boolean ignoreWarning = Boolean.parseBoolean(getServletRequest()
                .getParameter("ignoreSubmissionWarning"));
        String isWithinRegularRPStr = transmissionServiceP3.isWithinRegularReportPeriod(this.transmission.getId());
        if (isWithinRegularRPStr != null) {
            submissionMessage = getText("submission.fileNotSubmittable");
            String cancelDestination = getServletRequest().getParameter("cancelDestination");
            stack.set("cancelDestination", cancelDestination);
            return "submissionDenied";
        }

        // Log the submission.
        PrimaryUserRole primaryUserRole = siteUser.getPrimaryUserRole();
        String userType = "User";
        if (primaryUserRole != null && primaryUserRole.getName().equalsIgnoreCase("State User")) {
            userType = siteUser.getState().getAbbreviation() + " user";
        }
        log.info(String.format("%s %s has submitted file number %d.",
                userType,
                siteUser.getUserName(),
                this.transmission.getId()));

        try {
            // load all the transmission properties
            this.transmission = transmissionServiceP3.getTransmission(this.transmission.getId());

            // submit the transmission
            Transmission submission = transmissionServiceP3.submitTransmission(this.transmission, siteUser,
                    ignoreWarning);
            this.transmission = submission;

            // create submission confirmation message
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"), Locale.US);
            String dateString = DateUtil.formatDateAndTimezone(DateFormat.LONG, calendar);
            String[] dateParams = dateString.split(" ");
            List messageArgs = Arrays.asList(transmission.getId(), dateParams[1] + " " + dateParams[2],
                    dateParams[0]);
            submissionMessage = getText("submission.confirmationMessage", messageArgs);
            stack.set(Constants.SUBMISSION_STATUS, getSubmissionStatus(submission));
        } catch (SubmissionException se) {
            this.transmission = se.getTransmission();
            String lateWarning = "You have selected a %s file for the %s NYTD report, but the regular file submission deadline has passed. This file will be marked \"late\".";

            lateWarning = String.format(lateWarning,
                    transmission.getTransmissionType().getName(),
                    transmission.getReportingPeriod().getName());

            stack.set("lateTransmissionWarning", lateWarning);
            String cancelDestination = getServletRequest().getParameter("cancelDestination");
            stack.set("cancelDestination", cancelDestination);
        }

        // redirect to dashboard
        return Action.SUCCESS;
    }

    private String getSubmissionStatus(Transmission submission) {
        String submissionStatus = submission.getComplianceStatus();
        if (StringUtils.equalsIgnoreCase(Constants.COMPLIANT, submissionStatus) &&
                StringUtils.equalsIgnoreCase(
                        Constants.SUBSEQUENT_TRANSMISSION, submission.getTransmissionType().getName())) {
            submissionStatus = Constants.SUBSEQUENT_TRANSMISSION;
        }
        log.info("Submission Status: " + submissionStatus);
        return submissionStatus;
    }


    /**
     * Deletes a transmission.
     *
     * @return the user is returned to the dashboard or search page with respect
     * to the page from where transmission was selected for deletion
     */
    @SkipValidation
    public final String deleteTransmission() {
        SiteUser siteUser = (SiteUser) session.get("siteUser");
        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        Boolean ignoreWarning = Boolean.parseBoolean(getServletRequest()
                .getParameter("ignoreDeletionWarning"));


        // Log the submission.
        PrimaryUserRole primaryUserRole = siteUser.getPrimaryUserRole();
        String userType = "User";
        if (primaryUserRole != null && primaryUserRole.getName().equalsIgnoreCase("State User")) {
            userType = siteUser.getState().getAbbreviation() + " user";
        }
        log.info(String.format("%s %s has deleted file number %d.",
                userType,
                siteUser.getUserName(),
                this.transmission.getId()));


        // load all the transmission properties
        this.transmission = transmissionServiceP3.getTransmission(this.transmission.getId());

        // delete the transmission
        transmissionServiceP3.deleteTransmission(transmission, siteUser);

        // create deletion confirmation message
        // TO DO send message on deletion
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"), Locale.US);
        String dateString = DateUtil.formatDateAndTimezone(DateFormat.LONG, calendar);
        String[] dateParams = dateString.split(" ");
        List messageArgs = Arrays.asList(transmission.getId(), dateParams[1] + " " + dateParams[2],
                dateParams[0]);
        deletionMessage = getText("deletion.confirmationMessage", messageArgs);

        // redirect to dashboard
        return Action.SUCCESS;
    }


    @SkipValidation
    public final String transmissionDetail() {
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        transmission = getTransmissionServiceP3().getTransmission(transmission.getId());
        String dueDateStr = getTransmissionServiceP3().getDueDateofTransmission(transmission.getId());

        detail = new TransmissionDetail();
        detail.setDueDate(dueDateStr);
        detail.setTransmission(transmission);
        detail.setBaselineYouthCount(getTransmissionServiceP3().getCountBaselineYouth(transmission.getId()));
        detail.setFollowupYouthCount(getTransmissionServiceP3().getCountFollowupYouth(transmission.getId()));
        detail.setServedYouthCount(getTransmissionServiceP3().getCountServedYouth(transmission.getId()));
        detail.setTotalYouthCount(getTransmissionServiceP3().getCountTotalRecords(transmission.getId()));

        if (transmission.getSubmittedDate() != null) {
            Calendar cal = transmission.getSubmittedDate();
            detail.setSubmittedDate(formatDateAndTimezone(DateFormat.LONG, cal));
        } else {
            detail.setSubmittedDate("N/A");
        }

        if (transmission.getFileReceivedDate() != null) {
            Calendar cal = transmission.getFileReceivedDate();
            detail.setFederalSystemReceivedDate(formatDateAndTimezone(DateFormat.LONG, cal));
        } else {
            detail.setFederalSystemReceivedDate("N/A");
        }

        if (transmission.getCreatedDate() != null) {
            Calendar cal = transmission.getCreatedDate();
            detail.setFileProcessedDate(formatDateAndTimezone(DateFormat.LONG, cal));
        } else {
            detail.setFileProcessedDate("N/A");
        }

        if (transmission.getPotentialPenalty().doubleValue() > 0) {
            detail.setComplianceStatus("Non-Compliant");
        } else {
            detail.setComplianceStatus("Compliant");
        }

        return Action.SUCCESS;
    }

    @SkipValidation
    public final String transmissionNotes() {
        // load the list from the database
        List<TransmissionNote> notes = getTransmissionServiceP3().getTransmissionNotes(
                search.getTransmissionId());

        // push the list on the value stack
        ValueStack stack = ActionContext.getContext().getActionInvocation().getStack();
        stack.set("transmissionNotes", notes);

        return Action.SUCCESS;
    }

    private void initSearch() {
        // associate current user with search
        search.setUser(getUser());

        // Initialize the search form based on user's role
        UserRoleEnum role = UserRoleEnum.getRole(getPrimaryUserRole().getName());
        switch (role) {
            case ADMIN:
            case FEDERAL:
                getSearch().setViewSubmissionsOnly(true);
                break;
            case REGIONAL:
                setStatesInRegion(lookupService.getRegionStates(user.getRegion()));
                if (search.getStateName() != null && search.getStateName().equalsIgnoreCase("All")) {
                    StringBuilder regionStates = new StringBuilder();
                    for (State state : getStatesInRegion()) {
                        regionStates.append(state.getStateName()).append(";");
                    }
                    getSearch().setStateName(regionStates.toString());
                }
                getSearch().setViewSubmissionsOnly(true);
                break;
            case STATE:
                getSearch().setStateName(user.getState().getStateName());
                break;
        }
    }
}
