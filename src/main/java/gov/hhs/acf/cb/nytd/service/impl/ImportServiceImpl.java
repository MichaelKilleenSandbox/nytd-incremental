package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.ReportingPeriodDAO;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.*;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import oracle.jdbc.OracleTypes;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Import service implementation class.
 * User: 13873
 * Date: Jun 15, 2010
 *
 * @see ImportService
 */
//TODO (throughout the code): SonarQube - Define a constant instead of duplicating literal. 
// SonarQube - Duplicated blocks of code must be removed.
public class ImportServiceImpl extends BaseServiceImpl implements ImportService {

    // processing statuses for error conditions
    private static final String FILE_FORMAT_ERROR = "Exited";
    private static final String FILE_TYPE_ERROR = "Exited Not Expected File Category";
    private static final String DATA_IMPORT_ERROR = "Uncaught error during table population";
    private static final String RULES_ENGINE_ERROR = "Uncaught error during rules engine";
    private static final Pattern paramPattern = Pattern.compile("(\\$\\{(\\w+)\\})");

    // transmission page URL used for links in system generated messages
    private String transmissionPageURL;

    // services
    @Getter @Setter
    private LookupService lookupService;
    @Getter @Setter
    private MessageService messageService;
    @Getter @Setter
    private ComplianceService complianceService;
    @Getter @Setter
    private TransmissionServiceP3 transmissionServiceP3;
    @Getter @Setter
    private ImportValidationService importValidationService;

    // DAOs
    @Getter @Setter
    private StateDAO stateDAO;
    @Getter @Setter
    private ReportingPeriodDAO reportingPeriodDAO;

    /**
     * @see ImportService#processFile(File)
     */
    @Override
    public List<Object> processFile(File xmlFile) throws TransmissionException {


        log.info(String.format("Detected file %s. Beginning to process...", xmlFile.getName()));
        List<Object> msgPayload = new ArrayList<>();

        try (Connection conn = getDataSource().getConnection()) {
            // create a statement to call the stored procedure

            Optional<Transmission> optionalTransmission = processXMLData(xmlFile, conn);

            if (!optionalTransmission.isPresent()) {
                // unexpected exception in pl/sql function return ImportException in message payload to signal error
                TransmissionException ex = new TransmissionException("fnProcessNytdXmlData returned null transmission");
                ex.setXmlFile(xmlFile);
                msgPayload.add(ex);
            } else {
                Transmission transmission = optionalTransmission.get();
                // check for processing errors
                transmissionDetailsForProcessFile(xmlFile, msgPayload, transmission);
            } //else
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            TransmissionException importEx = new TransmissionException(ex);
            importEx.setXmlFile(xmlFile);
            msgPayload.add(importEx);
        }

        log.info(String.format("Data for file %s has been loaded into the system.", xmlFile.getName()));

        return msgPayload;
    }

    private void transmissionDetailsForProcessFile(File xmlFile, List<Object> msgPayload, Transmission transmission) {
        try {
            checkForProcessingError(transmission, xmlFile.getName());
            msgPayload.add(xmlFile);
            Long transmissionId = transmission.getId();
            // ensure persistent object, trans from proc returns inconsistent values (i.e.) state null
            Transmission trans = transmissionServiceP3.getTransmission(transmissionId);

            // create link to transmission details
            StringBuilder linkBuilder = new StringBuilder();
            linkBuilder.append("<a href=\"");
            linkBuilder.append(getTransmissionPageURL());
            linkBuilder.append("?search.transmissionId=");
            linkBuilder.append(trans.getId());
            linkBuilder.append("\">Transmission Details</a>");
            // message parameters
            Map<String, Object> msgParams = new HashMap<>();
            msgParams.put("fileName", xmlFile.getName());
            msgParams.put("transmissionId", trans.getId());
            if (trans.getState() != null) {
                msgParams.put("stateName", trans.getState().getStateName());
            } else {
                msgParams.put("stateName", "State not Valid or missing");
            }
            msgParams.put("complianceStatus", trans.getComplianceStatus());
            if (trans.getReportingPeriod() != null) {
                msgParams.put("reportingPeriod", trans.getReportingPeriod().getName());
            } else {
                msgParams.put("reportingPeriod", "Reporting Period not valid or missing");
            }
            msgParams.put("fileType", trans.getTransmissionType() != null ? trans.getTransmissionType().getName() : "Not valid or missing");
            msgParams.put("fileCategory", trans.getTransmissionType() != null ? trans.getTransmissionType().getName() : "Not valid or missing");
            msgParams.put("detailPageLink", linkBuilder.toString());
            msgParams.put("dateTime", DateUtil.getHourMintueSecondWithTimeZone(new GregorianCalendar()));
            msgParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
            msgParams.put("processingStatus", trans.getProcessingStatus());


            if (trans.hasNoErrorsInTransmission()) {
                // create transmission receipt and notify state users
                notifyStateUsers(trans.getState(), MessageService.TRANSMISSION_RECEIPT, msgParams);
            } else {
                msgParams.put("listFileFormatError", formatText(getFileFormatErrorsForEmail(trans), msgParams));
                if (trans.getTransmissionType() == null) {
                    msgParams.put("briefFileFormatError", "Invalid File Category");
                } else if (trans.hasDuplicateRecords()) {
                    msgParams.put("briefFileFormatError", "Duplicate Record Numbers");
                } else if (trans.hasFileFormatErrors()) {
                    msgParams.put("briefFileFormatError", "File Format Errors");
                } else {
                    msgParams.put("briefFileFormatError", "Improperly Formatted Values");
                }
                // notify both admin and state users
                notifySystemAdministrators(MessageService.UNSUCCESSFUL_TRANSMISSION_SYSADMIN, msgParams);
                notifyStateUsers(trans.getState(), MessageService.UNSUCCESSFUL_TRANSMISSION_STATE, msgParams);
            }
        }
        catch (FileTypeException fte) { // catch and handle file type error
            msgPayload.add(fte);
            fte.setTransmission(transmission);
            fte.setXmlFile(xmlFile);
            File file = handleFileTypeProcessingError(fte);
            log.error("FileTypeException in file name: " + file.getName());
        }
        catch (TransmissionException processingEx) { // catch and handle other errors
            processingEx.setTransmission(transmission);
            processingEx.setXmlFile(xmlFile);
            msgPayload.add(processingEx);
        }
    }

    private Optional<Transmission> processXMLData(File xmlFile, Connection conn) throws SQLException, FileNotFoundException {
        try (FileInputStream fis = new FileInputStream(xmlFile)) {

            try (CallableStatement importProc = conn.prepareCall("{? = call fnProcessNytdXmlData(?, ?, ?, ?)}")) {
                importProc.registerOutParameter(1, OracleTypes.CURSOR);
                // prepare the stored procedure
                importProc.setObject(2, xmlFile.getName());
                importProc.setObject(3, xmlFile.length());
                importProc.setObject(4, new Timestamp(xmlFile.lastModified()));
                importProc.setAsciiStream(5, fis, (int) xmlFile.length());
                // execute procedure
                importProc.execute();
                Object result = importProc.getObject(1);
                if (result != null) {
                    Transmission transmission = getTransmissionFromImportProc(importProc);
                    return Optional.of(transmission);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * @see ImportService#processRules(ImportDTO)
     */
    public List<Object> processRules(ImportDTO dto) throws TransmissionException {
        List<Object> msgPayload = new ArrayList<>();
        File xmlFile = dto.getTransmissionFile();
        Transmission transmission = dto.getTransmission();
        log.debug(String.format("Commencing compliance checks on %s (file number %d).", xmlFile.getName(), transmission.getId()));
        try {
            // get session associated with current thread
            Session session = null;
            SessionFactory sessionFactory = getSessionFactory();
            if (sessionFactory != null) {
                session = sessionFactory.getCurrentSession();
            } else {
                throw new IllegalStateException("session factory is null in processRules()");
            }
            // update processing status before calling procedure
            transmission.setProcessingStatus(Constants.RULES_ENGINE_INITIATED);
            // prepare the rule engine procedure
            //TODO: SonarQube - the parametrized type for this generic.
            Query qry = session.getNamedQuery("processRules");
            qry.setParameter("transmissionId", transmission.getId());

            // run rules engine
            long startTime = System.currentTimeMillis();
            Object result = qry.uniqueResult();
            long finishTime = System.currentTimeMillis();
            long timeInSeconds = (finishTime - startTime) / 1000;
            log.debug(String.format("fnRulesEngine completed in %d seconds.", timeInSeconds));

            if (result == null) {
                // unexpected exception in pl/sql function return ImportException in message payload to signal error
                TransmissionException ex = new TransmissionException("fnRulesEngine returned null transmission");
                ex.setXmlFile(xmlFile);
                msgPayload.add(ex);
            } else {
                transmission = (Transmission) result;
                transmissionDetailsForProcessRules(msgPayload, xmlFile, transmission);
            }
        }
        catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            TransmissionException rulesEx = new TransmissionException(ex);
            rulesEx.setXmlFile(xmlFile);
            msgPayload.add(rulesEx);
        }

        log.info(String.format("Finished compliance checks on %s (file number %d).", xmlFile.getName(), transmission.getId()));

        return msgPayload;
    }

    private void transmissionDetailsForProcessRules(List<Object> msgPayload, File xmlFile, Transmission transmission) {
        try {
            checkForProcessingError(transmission, xmlFile.getName());
            msgPayload.add(xmlFile);
            // refresh transmission to load associations
            getHibernateSession().refresh(transmission);
            // create link to transmission details
            StringBuilder linkBuilder = new StringBuilder();
            linkBuilder.append("<a href=\"");
            linkBuilder.append(getTransmissionPageURL());
            linkBuilder.append("?search.transmissionId=");
            linkBuilder.append(transmission.getId());
            linkBuilder.append("\">Transmission Details</a>");
            // message parameters
            Map<String, Object> msgParams = new HashMap<>();
            msgParams.put("fileName", xmlFile.getName());
            msgParams.put("transmissionId", transmission.getId());
            msgParams.put("complianceStatus", transmission.getComplianceStatus());
            msgParams.put("detailPageLink", linkBuilder.toString());
            notifyStateUsers(transmission.getState(), MessageService.TRANSMISSION_RECEIPT, msgParams);
        }
        catch (TransmissionException processingEx) {
            processingEx.setTransmission(transmission);
            processingEx.setXmlFile(xmlFile);
            msgPayload.add(processingEx);
        }
    }

    /**
     * @see ImportService#handleFileFormatError(FileFormatException)
     */
    @Override
    public File handleFileFormatError(FileFormatException ex) {

        boolean isNotWellFormedXML = false;
        // Load the transmission from the database
        // Transmission in the exception only includes id and processing status
        Transmission trans = ex.getTransmission();
        getHibernateSession().refresh(trans);
        // create link to transmission details
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append("<a href=\"");
        linkBuilder.append(getTransmissionPageURL());
        linkBuilder.append("?search.transmissionId=");
        linkBuilder.append(trans.getId());
        linkBuilder.append("\">Transmission Details</a>");
        // shared message parameters
        Map<String, Object> msgParams = new HashMap<>();
        msgParams.put("fileName", ex.getXmlFile().getName());
        msgParams.put("dateTime", DateUtil.getHourMintueSecondWithTimeZone(new GregorianCalendar()));
        msgParams.put("transmissionId", trans.getId());
        msgParams.put("detailPageLink", linkBuilder.toString());
        msgParams.put("stateName", trans.getState() != null ? trans.getState().getStateName() : "Missing State Name");
        msgParams.put("state", trans.getDataFileStateValue());
        msgParams.put("reportingPeriod", trans.getReportingPeriod() != null ? trans.getReportingPeriod().getName() : "Missing Report Period");
        msgParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
        msgParams.put("reportdate", "Report Date");
        msgParams.put("fileType", trans.getTransmissionType() != null ? trans.getTransmissionType().getName() : "Not valid or missing");
        msgParams.put("fileCategory", trans.getTransmissionType() != null ? trans.getTransmissionType().getName() : "Not valid or missing");
        //ranju JIRA-28
        msgParams.put("fileNameReportDateValue", ex.getXmlFile().getName().substring(15, 20));
        msgParams.put("listFileFormatError", formatText(getFileFormatErrorsForEmail(trans), msgParams));
        msgParams.put("processingStatus", trans.getProcessingStatus());
        isNotWellFormedXML = importValidationService.isNotWellFormedXML(trans);
        if (!ex.getXmlFile().getAbsolutePath().matches("^.*\\.[Xx][Mm][Ll]$")) {
            msgParams.put("briefFileFormatError", "Invalid Data file Extension");
            // notify system administrators file doesn't end with '.xml'
            notifySystemAdministrators(MessageService.UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_SYSADMIN, msgParams);
        } else if (ex.getTransmission().getState() == null) {
            if (!isNotWellFormedXML) {
                msgParams.put("briefFileFormatError", "Invalid State");
            } else {
                msgParams.put("stateName", "");
                msgParams.put("briefFileFormatError", "Not a well-formed XML file");
            }
            // notify system administrators state element missing from transmission
            notifySystemAdministrators(MessageService.UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_SYSADMIN, msgParams);
        } else if (ex.getTransmission().getReportingPeriod() == null) {
            msgParams.put("briefFileFormatError", "Invalid Report Date");
            // notify state users reporting period element missing from transmission
            notifyStateUsers(trans.getState(), MessageService.UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_STATE, msgParams);
            // notify system administrators state element missing from transmission
            notifySystemAdministrators(MessageService.UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_SYSADMIN, msgParams);
        }
        // return file so it's moved to unprocessed folder
        return ex.getXmlFile();
    }

    /**
     * Format text.
     *
     * @param text   as String
     * @param params as Map
     * @return formatted string
     */
    private String formatText(String text, Map<String, Object> params) {
        Matcher paramMatcher = paramPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (paramMatcher.find()) {
            String namedParameter = paramMatcher.group(2);
            if (params.containsKey(namedParameter)) {
                paramMatcher.appendReplacement(sb, params.get(namedParameter).toString());
            } else {
                try {
                    throw new ImportException("parameter map missing named parmater " + namedParameter);
                }
                catch (ImportException ex) {
                    Logger.getLogger(ImportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        paramMatcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Get file format errors for email.
     *
     * @param trans as Transmission object
     * @return true or false as String
     */
    private String getFileFormatErrorsForEmail(Transmission trans) {
        List<NytdError> fileSubmissionErrors =
                complianceService.getErrorsForCategories(trans.getId(),
                        complianceService.getFileSubmissionStandardsCategories());
        StringBuffer fileFormatErrorList = new StringBuffer("<ul>");
        Iterator<NytdError> errorItr = fileSubmissionErrors.iterator();
        NytdError error = null;
        while (errorItr.hasNext()) {
            error = errorItr.next();
            if (error.getComplianceCategory().getName().equalsIgnoreCase("File Format")) {
                fileFormatErrorList.append("<li>");
                fileFormatErrorList.append(error.getProblemDescription().getName());
                fileFormatErrorList.append("</li>");
            }
        }
        fileFormatErrorList.append("</ul>");
        return fileFormatErrorList.toString();
    }

    /**
     * Handle processing error.
     *
     * @param ex as TransmissionException object
     * @return xml file as File
     */
    @Override
    public File handleProcessingError(TransmissionException ex) {
        log.info("in handleProcessingError");
        Transmission trans = ex.getTransmission();
        if (trans != null) {
            // Load the transmission from the database
            // Transmission in the exception only includes id and processing status
            getHibernateSession().refresh(trans);
            // message parameters
            Map<String, Object> msgParams = new HashMap<>();
            msgParams.put("stateName", trans.getState().getStateName());
            msgParams.put("reportingPeriod", trans.getReportingPeriod().getName());
            msgParams.put("fileName", ex.getXmlFile().getName());
            msgParams.put("dateTime", DateUtil.getHourMintueSecondWithTimeZone(new GregorianCalendar()));
            msgParams.put("processingStatus", trans.getProcessingStatus());
            msgParams.put("listFileFormatError", formatText(getFileFormatErrorsForEmail(trans), msgParams));
            msgParams.put("briefFileFormatError", "Invalid Data File");
            msgParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
            // notify system administrators of unexpected exception during processing of the transmission
            notifySystemAdministrators(MessageService.UNSUCCESSFUL_TRANSMISSION_STOP_PROCESSING_SYSADMIN, msgParams);
        }
        // return file so it's moved to unprocessed folder
        return ex.getXmlFile();
    }

    /**
     * Send out email notification to state users.
     *
     * @param state     as State object
     * @param msg       as String
     * @param msgParams as Map
     */
    private void notifyStateUsers(State state, String msg, Map<String, Object> msgParams) {
        lookupService.getStateUsers(state)
                .forEach(u -> sendRequiredMessage(u, msg, msgParams));
    }

    private void sendRequiredMessage(SiteUser user, String msg, Map<String, Object> msgParams) {
        if (log.isDebugEnabled()) {
            log.debug("Sending System Notification to State User: " + user.toString());
        }
        msgParams.put("firstName", user.getFirstName());
        msgParams.put("lastName", user.getLastName());
        messageService.sendRequiredSystemMessage(messageService.createSystemMessage(msg, msgParams),
                Collections.singletonList(user));
    }

    /**
     * Send out email notification to system administrators.
     *
     * @param msg       as String
     * @param msgParams as Map
     */
    private void notifySystemAdministrators(String msg, Map<String, Object> msgParams) {
        // get list of administrators
        List<SiteUser> adminUsers = lookupService.getSystemAdminUsers();
        sendMessage(adminUsers, msg, msgParams);
    }

    /**
     * Send out email message by a list of users.
     *
     * @param users     as list of SiteUser object
     * @param msg       as String
     * @param msgParams as Map
     */
    private void sendMessage(List<SiteUser> users, String msg, Map<String, Object> msgParams) {
        List<SiteUser> stateUser = new ArrayList<>();
        SiteUser user = null;
        Message systemMsg = null;
        for (SiteUser siteUser : users) {
            user = siteUser;
            msgParams.remove("firstName");
            msgParams.remove("lastName");
            msgParams.put("firstName", user.getFirstName());
            msgParams.put("lastName", user.getLastName());
            systemMsg = messageService.createSystemMessage(msg, msgParams);
            stateUser.clear();
            stateUser.add(user);
            // add users as recipients to message and save message to database
            sendMessage(systemMsg, stateUser);
        }
    }

    /**
     * Send out email message by a list of users without message parameters.
     *
     * @param msg        as String
     * @param recipients as list of SiteUser object
     */
    private void sendMessage(Message msg, List<SiteUser> recipients) {
        messageService.sendSystemMessage(msg, recipients);
    }

    /**
     * Get transmission from import stored procedure.
     *
     * @param importProc as CallableStatement object
     * @param recipients as list of SiteUser object
     * @return Transmission object
     * @throws SQLException
     */
    protected Transmission getTransmissionFromImportProc(CallableStatement importProc) throws SQLException {
        Object result = null;
        result = importProc.getObject(1);
        if (result != null) {
            Transmission trans = null;
            ResultSet rs = (ResultSet) result;
            if (rs.next()) {
                trans = new Transmission();
                trans.setId(rs.getLong("transmissionId"));
                trans.setProcessingStatus(rs.getString("processingStatus"));
                trans.setImproperFormattedValCnt(rs.getLong("IMPROPERFORMATTEDVALCNT"));
                trans.setFormatErrCnt(rs.getLong("FORMATERRCNT"));
            }
            rs.close();
            return trans;
        }
        return null;
    }

    /**
     * Check file processing error and throws an appropriate transmission exception
     *
     * @param Transmission trans
     * @param String       fileName
     * @throws TransmissionException
     */
    private void checkForProcessingError(Transmission transmission, String fileName) throws TransmissionException {
        String processingStatus = transmission.getProcessingStatus();
        Long transmissionId = transmission.getId();
        // ensure persistent object as transmmission having only id and process status
        Transmission trans = transmissionServiceP3.getTransmission(transmissionId);

        if (processingStatus.equalsIgnoreCase(FILE_FORMAT_ERROR)) {
            throw new FileFormatException();
        }
        if (processingStatus.equalsIgnoreCase(DATA_IMPORT_ERROR)) {
            throw new TransmissionException();
        }
        if (processingStatus.equalsIgnoreCase(RULES_ENGINE_ERROR)) {
            throw new TransmissionException();
        }

        if (!importValidationService.isValidFileType(fileName)) {
            // update process status to "Exited Not Expected File Category"
            trans.setProcessingStatus(FILE_TYPE_ERROR);
            transmissionServiceP3.updateTransmission(trans);
            throw new FileTypeException();
        }
    }

    /**
     * Get file type error for email
     *
     * @param Transmission trans
     * @return String error in string
     */
    private String getFileTypeErrorForEmail(Transmission trans) {

        return String.format("<ul><li>Invalid file name. The transmission type specified in the file name ('%s') is not allowed during the current regular reporting period.</li></ul>", trans.getFileName());
    }

    /**
     * Handle file type error and send out email to sysadmin and state users
     *
     * @param FileTypeException ex
     * @return String error in string
     */
    private File handleFileTypeProcessingError(FileTypeException ex) {
        Transmission transmission = ex.getTransmission();
        if (transmission != null) {
            // persist object, the ransmission in the exception only includes id and processing status
            log.info("transmissin id to handle file type error: " + transmission.getId());
            Transmission trans = transmissionServiceP3.getTransmission(transmission.getId());
            // message parameters
            Map<String, Object> msgParams = new HashMap<>();
            msgParams.put("firstName", ""); // will be replaced in sendMessage()
            msgParams.put("lastName", ""); // will be replaced in sendMessage()
            msgParams.put("transmissionId", trans.getId());
            msgParams.put("stateName", trans.getState().getStateName());
            msgParams.put("reportingPeriod", trans.getReportingPeriod().getName());
            msgParams.put("fileName", ex.getXmlFile().getName());
            msgParams.put("fileType", trans.getTransmissionType().getName());
            msgParams.put("dateTime", DateUtil.getHourMintueSecondWithTimeZone(new GregorianCalendar()));
            msgParams.put("processingStatus", trans.getProcessingStatus());
            msgParams.put("listFileFormatError", formatText(getFileTypeErrorForEmail(trans), msgParams));
            msgParams.put("briefFileFormatError", "Invalid File Type");
            msgParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
            log.info(" message prameters for file type error: " + msgParams);
            Message systemMsg = messageService.createSystemMessage(MessageService.UNSUCCESSFUL_TRANSMISSION_FILE_TYPE_ERROR, msgParams);
            if (systemMsg != null) {
                log.info("message subject: " + systemMsg.getSubject());
                log.info("message body: " + systemMsg.getMessageBody());
                log.info("message signature: " + systemMsg.getSignature());
            }
            // notify state users of file type error
            notifyStateUsers(trans.getState(), MessageService.UNSUCCESSFUL_TRANSMISSION_FILE_TYPE_ERROR, msgParams);
            // notify system administrators of file type error
            notifySystemAdministrators(MessageService.UNSUCCESSFUL_TRANSMISSION_FILE_TYPE_ERROR, msgParams);
            log.info("Completed sending emails");
        }
        // moved the file to unprocessed folder
        File unprocessedDir = transmissionServiceP3.getUnprocessedDir();
        File toprocesssDir = transmissionServiceP3.getToprocessDir();
        File toprocessFile = new File(toprocesssDir, ex.getXmlFile().getName());
        try {
            FileUtils.moveFileToDirectory(toprocessFile, unprocessedDir, false);
        }
        catch (IOException ex1) {
            log.error("Error in moving file to unprocessed folder: " + ex1.getMessage());
        }

        return ex.getXmlFile();
    }

    /**
     * Get get previous report period by current report period
     *
     * @param String currentReportPeriod
     * @return String previousReportPeriod
     */
    private String getPreviousReportingPeriod(String currentReportPeriod) {
        String perviousReportPeriod = "";
        String currentyear = "";
        String previousyear = "";
        int currentyearInt = 0;
        int previousyearInt = 0;
        String currentperiod = "";
        String previousperiod = "";

        if (currentReportPeriod == null || currentReportPeriod.isEmpty()) {
            return perviousReportPeriod;
        } else if (currentReportPeriod.length() == 5) { // valid 5-digit lenght of year + period
            currentyear = currentReportPeriod.substring(0, 4);
            if (!currentyear.matches("-?\\d+(\\.\\d+)?")) { // verify year part is number
                return perviousReportPeriod;
            } else {
                try {
                    currentyearInt = Integer.parseInt(currentyear);
                    currentperiod = currentReportPeriod.substring(4, 5);
                    // build previous reporting period based on curretn A or B period
                    if (currentperiod.equals("A")) {
                        previousperiod = "B";
                        previousyearInt = currentyearInt - 1;
                        previousyear = String.valueOf(previousyearInt);
                    } else if (currentperiod.equals("B")) {
                        previousperiod = "A";
                        previousyear = String.valueOf(currentyearInt);
                    }
                }
                catch (NumberFormatException e) {
                    log.error(e.getMessage());
                    return perviousReportPeriod;
                }
                perviousReportPeriod = previousyear + previousperiod;
            }
        }

        return perviousReportPeriod;
    }


    /**
     * @return the transmissionPageURL
     */
    public String getTransmissionPageURL() {
        return transmissionPageURL;
    }

    /**
     * @param transmissionPageURL the transmissionPageURL to set
     */
    public void setTransmissionPageURL(String transmissionPageURL) {
        this.transmissionPageURL = transmissionPageURL;
    }


}
