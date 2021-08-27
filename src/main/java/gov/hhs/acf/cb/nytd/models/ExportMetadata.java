package gov.hhs.acf.cb.nytd.models;

import gov.hhs.acf.cb.nytd.util.DateUtil;

import java.text.DateFormat;

public class ExportMetadata extends PersistentObject {

    private String fileName;
    private String fileType;
    private String status;
    private String reportingPeriods;
    private String states;
    private String populations;
    private String demographics;
    private String characteristics;
    private String independentLivingServices;
    private String youthOutcomeSurveys;
    private String demographicNotes;
    private String characteristicNotes;
    private String independentLivingServiceNotes;
    private String youthOutcomeSurveyNotes;
    private String transmissionIds;
    private String userName;

    public ExportMetadata() {
    }

    public ExportMetadata(Long exportMetadataId) {
        this.id = exportMetadataId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public String getPopulations() {
        return populations;
    }

    public void setPopulations(String populations) {
        this.populations = populations;
    }

    public String getDemographics() {
        return demographics;
    }

    public void setDemographics(String demographics) {
        this.demographics = demographics;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getIndependentLivingServices() {
        return independentLivingServices;
    }

    public void setIndependentLivingServices(String independentLivingServices) {
        this.independentLivingServices = independentLivingServices;
    }

    public String getYouthOutcomeSurveys() {
        return youthOutcomeSurveys;
    }

    public void setYouthOutcomeSurveys(String youthOutcomeSurveys) {
        this.youthOutcomeSurveys = youthOutcomeSurveys;
    }

    public String getDemographicNotes() {
        return demographicNotes;
    }

    public void setDemographicNotes(String demographicNotes) {
        this.demographicNotes = demographicNotes;
    }

    public String getCharacteristicNotes() {
        return characteristicNotes;
    }

    public void setCharacteristicNotes(String characteristicNotes) {
        this.characteristicNotes = characteristicNotes;
    }

    public String getIndependentLivingServiceNotes() {
        return independentLivingServiceNotes;
    }

    public void setIndependentLivingServiceNotes(
            String independentLivingServiceNotes) {
        this.independentLivingServiceNotes = independentLivingServiceNotes;
    }

    public String getYouthOutcomeSurveyNotes() {
        return youthOutcomeSurveyNotes;
    }

    public void setYouthOutcomeSurveyNotes(String youthOutcomeSurveyNotes) {
        this.youthOutcomeSurveyNotes = youthOutcomeSurveyNotes;
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
