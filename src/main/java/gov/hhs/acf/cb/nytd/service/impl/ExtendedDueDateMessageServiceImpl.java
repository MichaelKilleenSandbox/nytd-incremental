package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.SiteUserDAO;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.service.ExtendedDueDateMessageService;
import gov.hhs.acf.cb.nytd.service.MessageService;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExtendedDueDateMessageServiceImpl extends BaseServiceImpl implements ExtendedDueDateMessageService {

    DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");

    private static final long ROLE_SYSADMIN = 1L;
    private static final long ROLE_FEDEREAL = 2L;
    @Getter
    @Setter
    private MessageService messageService;
    @Getter
    @Setter
    private StateDAO stateDAO;
    @Getter
    @Setter
    private SiteUserDAO siteUserDAO;

    private static final String DDE_STATE_NOTIFICATION = "Due Date Extension State Notification";
    private static final String DDE_REGIONAL_NOTIFICATION = "Due Date Extension Regional Notification";
    private static final String DDE_FEDERAL_NOTIFICATION = "Due Date Extension Federal Notification";
    private static final String DDE_SYSADMIN_NOTIFICATION = "Due Date Extension SysAdmin Notification";

    private static final String DDE_DELETED_STATE_NOTIFICATION = "Due Date Extension Deleted State Notification";
    private static final String DDE_DELETED_REGIONAL_NOTIFICATION = "Due Date Extension Deleted Regional Notification";
    private static final String DDE_DELETED_FEDERAL_NOTIFICATION = "Due Date Extension Deleted Federal Notification";
    private static final String DDE_DELETED_SYSADMIN_NOTIFICATION = "Due Date Extension Deleted SysAdmin Notification";

    private static final String DDE_EDITED_STATE_NOTIFICATION = "Due Date Extension Edited State Notification";
    private static final String DDE_EDITED_REGIONAL_NOTIFICATION = "Due Date Extension Edited Regional Notification";
    private static final String DDE_EDITED_FEDERAL_NOTIFICATION = "Due Date Extension Edited Federal Notification";
    private static final String DDE_EDITED_SYSADMIN_NOTIFICATION = "Due Date Extension Edited SysAdmin Notification";


    @Override
    public void extendedDueDateCreated(VwExtendedDueDate extendedDueDateData, List<ExtendedDueDate> extendedDueDate) {

        try {
            extendedDueDate.forEach(extendedDueDate1 -> {
                Optional<State> optionalStateBeingExtended = stateDAO.findStateById(extendedDueDate1.getStateId());
                if (optionalStateBeingExtended.isPresent()) {
                    long regionId = optionalStateBeingExtended.get().getRegion().getId();
                    sendEmailToState(extendedDueDateData, extendedDueDate1,DDE_STATE_NOTIFICATION);
                    sendEmailToRegions(extendedDueDateData,extendedDueDate1,regionId,DDE_REGIONAL_NOTIFICATION);
                    sendEmailToFederal(extendedDueDateData,extendedDueDate1,DDE_FEDERAL_NOTIFICATION);
                    sendEmailToSysAdmin(extendedDueDateData,extendedDueDate1,DDE_SYSADMIN_NOTIFICATION);
                }
            });
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    public void extendedDueDateDeleted( VwExtendedDueDate extendedDueDateData, List<ExtendedDueDate> extendedDueDate) {
        try {
            extendedDueDate.forEach(extendedDueDate1 -> {
                Optional<State> optionalStateBeingExtended = stateDAO.findStateById(extendedDueDate1.getStateId());
                if (optionalStateBeingExtended.isPresent()) {
                    long regionId = optionalStateBeingExtended.get().getRegion().getId();
                    sendEmailToState(extendedDueDateData, extendedDueDate1,DDE_DELETED_STATE_NOTIFICATION);
                    sendEmailToRegions(extendedDueDateData,extendedDueDate1,regionId,DDE_DELETED_REGIONAL_NOTIFICATION);
                    sendEmailToFederal(extendedDueDateData,extendedDueDate1,DDE_DELETED_FEDERAL_NOTIFICATION);
                    sendEmailToSysAdmin(extendedDueDateData,extendedDueDate1,DDE_DELETED_SYSADMIN_NOTIFICATION);
                }
            });
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    public void extendedDueDateEdited( VwExtendedDueDate extendedDueDateData, List<ExtendedDueDate> extendedDueDate) {
        try {
            extendedDueDate.forEach(extendedDueDate1 -> {
                Optional<State> optionalStateBeingExtended = stateDAO.findStateById(extendedDueDate1.getStateId());
                if (optionalStateBeingExtended.isPresent()) {
                    long regionId = optionalStateBeingExtended.get().getRegion().getId();
                    sendEmailToState(extendedDueDateData, extendedDueDate1,DDE_EDITED_STATE_NOTIFICATION);
                    sendEmailToRegions(extendedDueDateData,extendedDueDate1,regionId,DDE_EDITED_REGIONAL_NOTIFICATION);
                    sendEmailToFederal(extendedDueDateData,extendedDueDate1,DDE_EDITED_FEDERAL_NOTIFICATION);
                    sendEmailToSysAdmin(extendedDueDateData,extendedDueDate1,DDE_EDITED_SYSADMIN_NOTIFICATION);
                }
            });
        }
        catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    private Map<String, Object> populateMessageMap(VwExtendedDueDate extendedDueDateData, ExtendedDueDate extendedDueDate1, SiteUser siteUser, String ddeSysadminNotification) {
        Map<String, Object> msgParams = new HashMap<>();

        msgParams.put("stateName", extendedDueDateData.getEddStateName());
        msgParams.put("firstName", siteUser.getFirstName());
        msgParams.put("lastName", siteUser.getLastName());
        msgParams.put("reportingPeriod", extendedDueDateData.getReportingPeriodName());
        if(ddeSysadminNotification.contains("Due Date Extension Deleted")) {
            msgParams.put("extendedDueDate", dateFormat.format(extendedDueDateData.getExtendedDueDate().getTime()));
            msgParams.put("defaultDueDate", extendedDueDate1.getStrExtendedDueDate());
        }
        else {
            msgParams.put("defaultDueDate", dateFormat.format(extendedDueDateData.getExtendedDueDate().getTime()));
            msgParams.put("extendedDueDate", extendedDueDate1.getStrExtendedDueDate());
        }
        return msgParams;
    }

    private void processSiteUsers(VwExtendedDueDate extendedDueDateData, ExtendedDueDate extendedDueDate, List<SiteUser> siteUsers, String ddeSysadminNotification) {
        siteUsers.forEach(siteUser -> {
            try {
                Map<String, Object> msgParams = populateMessageMap(extendedDueDateData, extendedDueDate, siteUser, ddeSysadminNotification);
                Message messageState = messageService.createSystemMessage(ddeSysadminNotification, msgParams);
                messageService.sendSystemMessage(messageState, siteUser);
            }
            catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });
    }

    private void sendEmailToSysAdmin(VwExtendedDueDate extendedDueDateData, ExtendedDueDate extendedDueDate, String ddeDeletedSysadminNotification) {
        List<SiteUser> siteUsers = siteUserDAO.findAllByPrimaryRole(ROLE_SYSADMIN);
        processSiteUsers(extendedDueDateData, extendedDueDate, siteUsers, ddeDeletedSysadminNotification);
    }

    private void sendEmailToFederal(VwExtendedDueDate extendedDueDateData, ExtendedDueDate extendedDueDate, String ddeDeletedFederalNotification) {
        List<SiteUser> siteUsers = siteUserDAO.findAllByPrimaryRole(ROLE_FEDEREAL);
        processSiteUsers(extendedDueDateData, extendedDueDate, siteUsers, ddeDeletedFederalNotification);
    }

    private void sendEmailToRegions(VwExtendedDueDate extendedDueDateData, ExtendedDueDate extendedDueDate, long regionId, String ddeDeletedRegionalNotification) {

        List<SiteUser> siteUsers = siteUserDAO.findAllByRegion(regionId);
        processSiteUsers(extendedDueDateData, extendedDueDate, siteUsers, ddeDeletedRegionalNotification);
    }

    private void sendEmailToState(VwExtendedDueDate extendedDueDateData, ExtendedDueDate extendedDueDate, String ddeDeletedStateNotification) {
        List<SiteUser> siteUsers = siteUserDAO.findAllByStateId(extendedDueDate.getStateId());
        processSiteUsers(extendedDueDateData, extendedDueDate, siteUsers, ddeDeletedStateNotification);
    }


}
