package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.message.MessageSearch;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SiteUser;

import java.util.List;
import java.util.Map;

/**
 * Service to build and send out message.
 * User: 13873
 * Date: Jun 16, 2010
 */
public interface MessageService extends BaseService {

    // constants
    static final String DATA_EXPORT_NOTIFICATION = "data export notification";
    static final String PL_DOWNLOAD_NOTIFICATION = "penalty letters download notification";
    static final String FORGOT_LOGIN_USERNAME = "forgot login username";
    static final String FORGOT_LOGIN_PASSWORD = "forgot login password";
    static final String SUBMISSION_NOTIFICATION = "submission notification";
    static final String SUBMISSION_RECEIPT = "submission receipt";
    static final String SUBMISSION_REMINDER = "submission reminder";
    static final String TRANSMISSION_FAILURE = "transmission failure";
    static final String TRANSMISSION_RECEIPT = "transmission receipt";
    static final String TRASMISSION_MISSING_STATE = "missing state exception";
    static final String TRANSMISSION_MISSING_REPORTING_PERIOD = "missing reporting period exception";
    static final String TRANSMISSION_PROCESSING_ERROR = "unknown exception";
    static final String TRANSMISSION_WRONG_EXTENSION = "xml extension error";
    public static final String TRANSMISSION_FILE_FORMAT = "Common message for File Format errors";
    public static final String TRANSMISSION_IMPROPER_FORMAT = "Improper Formatted Value - stops submission";
    public static final String TRANSMISSION_FILE_FMT_RP_MISSING = "File Format - Report period missing";
    public static final String UNSUCCESSFUL_TRANSMISSION_SYSADMIN = "Unsuccessful transmission, but successfully processed - sysadmin";
    public static final String UNSUCCESSFUL_TRANSMISSION_STATE = "Unsuccessful transmission, but successfully processed - State";
    public static final String UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_SYSADMIN = "Unsuccessful Transmission, processing stopped - Sysadmin";
    public static final String UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_STATE = "Unsuccessful Transmission, processing stopped - State";
    public static final String UNSUCCESSFUL_TRANSMISSION_FILE_TYPE_ERROR = "Unsuccessful Transmission, processing stopped - Incorrect file type";
    public static final String USER_ADDED = "User added";
    public static final String USER_DELETED = "User deleted";
    public static final String OVERWRITE_COHORTSET_AGE19 = "Cohort Update Age 19";
    public static final String OVERWRITE_COHORTSET_AGE21 = "Cohort Update Age 21";
    public static final String REIMPORTINFO_COHORTSET_AGE19 = "Cohort Update Reimport Age 19";
    public static final String REIMPORTINFO_COHORTSET_AGE21 = "Cohort Update Reimport Age 21";
    public static final String SAMPLING_REQUEST_APPROVED = "Sampling Request Approved";
    public static final String SAMPLING_REQUEST_SUBMITTED = "Sampling Request Submitted";
    public static final String SAMPLING_DRAWN = "Sampling Import Success";
    public static final String SAMPLING_REIMPORT_DRAWN = "Sampling Reimport Success";
    public static final String SAMPLING_FILE_FORMAT_ERROR = "Sampling file format error";
    public static final String SAMPLING_FILE_RECORDS_ERROR = "Sampling file records error";

    /**
     * Create system message.
     *
     * @param type a type of message as String
     * @param namedParams parameters in the message as Map
     * @return Message object to send out
     */
    Message createSystemMessage(String type, Map<String, Object> namedParams);

    /**
     * Prepare system message.
     *
     * @param msg as a Message object
     * @return Message object to send out
     */
    Message prepareSystemMessage(Message msg);

    /**
     * Prepare system message.
     *
     * @param msg as a Message object
     * @return Message object to send out
     */
    MessageSearch search(MessageSearch search);

    /**
     * Send out email notification to a list of email addresses
     *
     * @param systemMsg as a Message object
     * @param emailAddresses as a List of String
     */
    void sendEmailNotification(Message systemMsg, List<String> emailAddresses);

    /**
     * Send system message to a list of NYTD users
     *
     * @param msg as a Message object
     * @param recipients as a List of SiteUser
     */
    void sendSystemMessage(Message msg, SiteUser recipient);

    /**
     * Send system message to a list of NYTD users
     *
     * @param msg as a Message object
     * @param recipients as a List of SiteUser
     */
    void sendSystemMessage(Message msg, List<SiteUser> recipients);

    /**
     *
     */
    void sendRequiredSystemMessage(Message msg, List<SiteUser> recipients);
    /**
     * Send out message to a list of NYTD users
     *
     * @param msg as a Message object
     * @param recipients as a List of SiteUser
     * @return Message sent
     */
    Message sendMessage(Message msg, List<SiteUser> recipients);

    /**
     * Get message by message id
     *
     * @param messageId as Long
     * @return Message object
     */
    Message getMessage(Long messageId);

}
