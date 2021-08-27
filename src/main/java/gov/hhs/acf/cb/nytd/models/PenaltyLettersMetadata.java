package gov.hhs.acf.cb.nytd.models;

import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Builder;

import java.text.DateFormat;

/**
* A class hold meta data for generated penalty letters model
*/

@Builder(toBuilder = true)
public class PenaltyLettersMetadata extends PersistentObject {

    private String fileName;
    private String status;
    private String reportingPeriods;
    private String states;
    private String transmissionIds;
    private String userName;

    // construtor with no arguments
    public PenaltyLettersMetadata() {
    }

    // constructor with id as an argument
    public PenaltyLettersMetadata(Long penaltyLettersMetadataId) {
        this.id = penaltyLettersMetadataId;
    }
    
    // constructor with all arguments
    public PenaltyLettersMetadata(String fileName, String status, String reportingPeriods,  
            String states, String userName, String transmissionIds) {
        this.fileName = fileName;
        this.status = status;
        this.reportingPeriods = reportingPeriods;
        this.states = states;
        this.userName = userName;
        this.transmissionIds = transmissionIds;
    }

    // getter/setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreatedDateWithTimeStamp() {
        return DateUtil.formatDateAndTimezone(DateFormat.LONG, createdDate);
    }

    public String getReportingPeriods() {
        return reportingPeriods;
    }

    public void setReportingPeriods(String reportingPeriods) {
        this.reportingPeriods = reportingPeriods;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransmissionIds() {
        return transmissionIds;
    }

    public void setTransmissionIds(String transmissionIds) {
        this.transmissionIds = transmissionIds;
    }

    public String getUserName() {
            return userName;
    }

    public void setUserName(String userName) {
            this.userName = userName;
    }
}
