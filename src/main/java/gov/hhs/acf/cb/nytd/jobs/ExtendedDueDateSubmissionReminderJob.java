package gov.hhs.acf.cb.nytd.jobs;

import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.dao.SiteUserDAO;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.service.ExtendedDueDateService;
import gov.hhs.acf.cb.nytd.service.MessageService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Job sends reminder emails four days before extended submission due date.
 */
public class ExtendedDueDateSubmissionReminderJob extends QuartzJobBean {

    private static final String SUBMISSION_DUE_DATE_REMINDER = "Submission Due Date Reminder";
    public static final int DAYS_OFFSET = 4;
    public static final long STATE_USER_ROLE = 4L;

    private final Logger log = Logger.getLogger(getClass());

    @Getter @Setter
    private MessageService messageServiceP3;
    @Getter @Setter
    private SiteUserDAO siteUserDAO;
    @Getter @Setter
    private ExtendedDueDateService extendedDueDateService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, DAYS_OFFSET);
        try {

            ExtendedDueDateSearch search = ExtendedDueDateSearch.from(Calendar.getInstance());
            List<VwExtendedDueDate> upcomingExtendedDueDateData = extendedDueDateService.getExtendedDueDateData(search)
                    .stream().filter(extendedDueDate ->
                            ChronoUnit.DAYS.between(today.toInstant(), extendedDueDate.getExtendedDueDate().toInstant()) == 0)
                    .collect(Collectors.toList());

            upcomingExtendedDueDateData.forEach(extendedDueDate -> {
                List<SiteUser> siteUsers = siteUserDAO.findByStateAndPrimaryRole(extendedDueDate.getEddStateId(), STATE_USER_ROLE);
                siteUsers.forEach(siteUser -> {
                    Map<String, Object> msgParams = new HashMap<>();
                    msgParams.put("stateName", extendedDueDate.getEddStateName());
                    msgParams.put("firstName", siteUser.getFirstName());
                    msgParams.put("lastName", siteUser.getLastName());
                    msgParams.put("reportingPeriod", extendedDueDate.getReportingPeriodName());
                    Message messageState = messageServiceP3.createSystemMessage(SUBMISSION_DUE_DATE_REMINDER, msgParams);
                    messageServiceP3.sendSystemMessage(messageState, siteUser);
                });
            });
        }
        catch (Exception exception) {
            log.warn("Reminder Emails may not have been run.");
        }
    }
}
