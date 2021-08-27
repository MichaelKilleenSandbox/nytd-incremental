package gov.hhs.acf.cb.nytd.jobs;

import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.PenaltyLettersMetadata;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.service.PenaltyLetterService;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.PenaltyLetterUtil;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Quartz job that generates penalty letters for download.
 */
public class GeneratePenaltyLettersJob extends QuartzJobBean
{
    protected Logger log = Logger.getLogger(getClass());
    private MessageService messageServiceP3;
    private PenaltyLetterService penaltyLetterService;
    private String exportLocation;
    private String rootURL;
    private static final String DOWNLOAD_ACTION = "/downloadPenaltyLettersZip.action?downloadFilename=";
    private static final String FILE_NAME = "fileName";
    
    /**
     * Executes the quartz job.
     * @param jobContext the Quartz job context
     * @throws JobExecutionException
     */
    @Override
    protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException {

        // ensure the directory for penalty letters exists or create on disk
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/config/systemConfig.properties"));
            String penaltyLettersDir = properties.getProperty("systemConfig.penaltyLettersLocation");
            log.info("penalty letters location: " + penaltyLettersDir);
            CommonFunctions.mkdir(new File(new URI(penaltyLettersDir)));
        } catch (IOException | URISyntaxException e) {
                log.error(e.getMessage(), e);
        }
        
        // get variables from job data map
        JobDataMap jobDataMap = jobContext.getJobDetail().getJobDataMap();
        SiteUser user = (SiteUser) jobDataMap.get("siteUser");
        String finalFileName = getExportLocation() + (String) jobDataMap.get(FILE_NAME);
        File finalFile = null;
        List<String> initialTransmissionIdList = (List<String>) jobDataMap.get("initialTransmissionIdList");
        List<String> finalTransmissionIdList = (List<String>) jobDataMap.get("finalTransmissionIdList");
        
        // save records to metadata table for available zip download
        PenaltyLettersMetadata plMetadata = new PenaltyLettersMetadata();
        plMetadata.setUserName(user.getUserName());
        plMetadata.setStatus(Constants.EXPORT_IN_PROCESS);
        plMetadata.setFileName((String)jobDataMap.get(FILE_NAME));
        plMetadata.setReportingPeriods((String)jobDataMap.get("reportingPeriods"));
        plMetadata.setStates((String)jobDataMap.get("states"));
        plMetadata.setCreatedDate(Calendar.getInstance());
        penaltyLetterService.savePenaltyLettersMetadata(plMetadata, user);
        
        // generate selected initial penalty letters
        for (String initialTransmissionId : initialTransmissionIdList) {
            try {
                penaltyLetterService.parseAndWritePenaltyLetter(Long.valueOf(initialTransmissionId), "Initial");
            } catch (Exception e) {
                log.error("Error in generating initial letter for transmission id: " + initialTransmissionId 
                        + " - " + e.getMessage());
            }
        }
        // generate selected final penalty letters
        for (String finalTransmissionId : finalTransmissionIdList) {
            try {
                penaltyLetterService.parseAndWritePenaltyLetter(Long.valueOf(finalTransmissionId), "Final");
            } catch (Exception e) {
                log.error("Error in generating final letter for transmission id: " + finalTransmissionId 
                        + " - " + e.getMessage());
            }
        }

        // create zip file
        try {
            finalFile = new File(new URI(finalFileName));
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new JobExecutionException(e);
        }

        // build array of letters to zip
        List<String> srcFiles = new ArrayList<>();
        PenaltyLetterUtil penaltyLetterUtil = new PenaltyLetterUtil();
        File[] files = penaltyLetterUtil.findDocFilesInDir(getExportLocation());
        for (File file : files) {
            if (file.isFile()) {
                try {
                    srcFiles.add(new URI(getExportLocation()).getPath()+file.getName());
                } catch (URISyntaxException e){
                    log.error(e.getMessage());
                }
            }
        }
        
        // zip penalty letters
        try {
            penaltyLetterService.zipPenaltyLetters(srcFiles, finalFile);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }

        // update status
        plMetadata.setStatus(Constants.EXPORT_COMPLETE);
        penaltyLetterService.savePenaltyLettersMetadata(plMetadata, user);
        
        // notify user
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put("penaltyLettersDownloadLink", getRootURL() + DOWNLOAD_ACTION + (String) jobDataMap.get("fileName"));
        namedParams.put("firstName", user.getFirstName());
        namedParams.put("lastName", user.getLastName());
        Message plDownloadMsg = messageServiceP3.createSystemMessage(MessageService.PL_DOWNLOAD_NOTIFICATION, namedParams);
        log.debug("From: "+ plDownloadMsg.getMessageFrom());
        log.debug("Subject: "+ plDownloadMsg.getSubject());
        log.debug("Body: "+ plDownloadMsg.getMessageBody());
        List<SiteUser> recipients = new ArrayList<>();
        recipients.add(user);
        messageServiceP3.sendSystemMessage(plDownloadMsg, recipients);

    }

    /**
     * @param messageServiceP3 the messagingService to set
     */
    public void setMessageServiceP3(MessageService messageServiceP3) {
        this.messageServiceP3 = messageServiceP3;
    }
    
    /**
     * @param penaltyLetterService the penaltyLetterService to set
     */
    public void setPenaltyLetterService(PenaltyLetterService penaltyLetterService) {
        this.penaltyLetterService = penaltyLetterService;
    }

    /**
     * @return the exportLocation
     */
    public String getExportLocation() {
        return exportLocation;
    }

    /**
     * @param exportLocation the exportLocation to set
     */
    public void setExportLocation(String exportLocation) {
        this.exportLocation = exportLocation;
    }

    /**
     * @return the rootURL
     */
    public String getRootURL() {
        return rootURL;
    }

    /**
     * @param rootURL the rootURL to set
     */
    public void setRootURL(String rootURL) {
        this.rootURL = rootURL;
    }
    
}
