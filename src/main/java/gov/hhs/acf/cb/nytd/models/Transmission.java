package gov.hhs.acf.cb.nytd.models;

// Generated May 20, 2009 10:16:43 AM by Hibernate Tools 3.2.4.GA

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/*
 * Transmission generated by hbm2java
 */
public class Transmission extends PersistentObject {
    private TransmissionType transmissionType;
    private ReportingPeriod reportingPeriod;
    private String name;
    private String fileGenerationDate;
    private String fileId;
    private String complianceStatus;
    private String processingStatus;
    private String submissionStatus;
    private Calendar submittedDate;
    private SiteUser siteUser;
    private String fileName;
    private Integer fileSize;
    private Calendar fileReceivedDate;
    private State state;
    private String dataFileReportPeriodValue;
    private String dataFileStateValue; // Transmission Category value in data file
    private String dataFileTransmissionTypeValue;
    private String lateWarningMessage;
    private BigDecimal potentialPenalty;
    private Long improperFormattedValCnt;
    private Long duplicateRecordsCnt;
    private Long formatErrCnt;
    private Long recordsCnt;
    private Set<NonCompliance> nonCompliances = new HashSet<>(0);
    private Set<TransmissionNote> transmissionNotes = new HashSet<>(0);
    private Set<ElementNote> elementNotes = new HashSet<>(0);

    private Set<TransmissionRecord> transmissionRecords = new HashSet<>(0);
    private Set<DataAggregate> dataAggregates = new HashSet<>(0);

    public Transmission() {
    }

    public Transmission(Long transmissionId) {
        this.id = transmissionId;
    }

    public Transmission(Long transmissionId, TransmissionType transmissionType, ReportingPeriod reportingPeriod,
                        String name, Calendar createdDate, String createdBy, Calendar updateDate, String updateBy, String description,
                        Set<NonCompliance> nonCompliances, String submissionStatus, Calendar submittedDate, SiteUser siteUser,
                        String fileName, Integer fileSize, Calendar fileReceivedDate, State state) {
        this.id = transmissionId;
        this.transmissionType = transmissionType;
        this.reportingPeriod = reportingPeriod;
        this.name = name;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.updateDate = updateDate;
        this.updateBy = updateBy;
        this.description = description;
        this.nonCompliances = nonCompliances;
        this.submissionStatus = submissionStatus;
        this.submittedDate = submittedDate;
        this.siteUser = siteUser;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileReceivedDate = fileReceivedDate;
        this.state = state;

    }

    public TransmissionType getTransmissionType() {
        return this.transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public ReportingPeriod getReportingPeriod() {
        return this.reportingPeriod;
    }

    public void setReportingPeriod(ReportingPeriod reportingPeriod) {
        this.reportingPeriod = reportingPeriod;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<NonCompliance> getNonCompliances() {
        return this.nonCompliances;
    }

    public void setNonCompliances(Set<NonCompliance> nonCompliances) {
        this.nonCompliances = nonCompliances;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public Calendar getFileReceivedDate() {
        return fileReceivedDate;
    }

    public void setFileReceivedDate(Calendar fileReceivedDate) {
        this.fileReceivedDate = fileReceivedDate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    /**
     * @return the notes
     */
    public Set<ElementNote> getElementNotes() {
        return elementNotes;
    }

    /**
     * @param notes the notes to set
     */
    public void setElementNotes(Set<ElementNote> notes) {
        this.elementNotes = notes;
    }

    /**
     * @return the notes
     */
    public Set<TransmissionNote> getTransmissionNotes() {
        return transmissionNotes;
    }

    /**
     * @param notes the notes to set
     */
    public void setTransmissionNotes(Set<TransmissionNote> notes) {
        this.transmissionNotes = notes;
    }

    /**
     * @return the fileGenerationDate
     */
    public String getFileGenerationDate() {
        return fileGenerationDate;
    }

    /**
     * @param fileGenerationDate the fileGenerationDate to set
     */
    public void setFileGenerationDate(String fileGenerationDate) {
        this.fileGenerationDate = fileGenerationDate;
    }

    /**
     * @return the fileId
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFederalFileId() {
        String federalFileId = "";
        if (getFileName() != null) {
            federalFileId = getFileName().replace(".xml", "_");
        }
        federalFileId += "TID.";
        if (fileId != null) {
            federalFileId += fileId;
            federalFileId += "_";
        }
        federalFileId += id.toString();

        return federalFileId;
    }

    /**
     * @return the transmissionRecords
     */
    public Set<TransmissionRecord> getTransmissionRecords() {
        return transmissionRecords;
    }

    /**
     * @param transmissionRecords the transmissionRecords to set
     */
    public void setTransmissionRecords(Set<TransmissionRecord> transmissionRecords) {
        this.transmissionRecords = transmissionRecords;
    }

    /**
     * @return the dataAggregates
     */
    public Set<DataAggregate> getDataAggregates() {
        return dataAggregates;
    }

    /**
     * @param dataAggregates the dataAggregates to set
     */
    public void setDataAggregates(Set<DataAggregate> dataAggregates) {
        this.dataAggregates = dataAggregates;
    }

    /**
     * @return the submissionStatus
     */
    public String getSubmissionStatus() {
        return submissionStatus;
    }

    /**
     * @param submissionStatus the submissionStatus to set
     */
    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    /**
     * @return the submittedDate
     */
    public Calendar getSubmittedDate() {
        return submittedDate;
    }

    /**
     * @param submittedDate the submittedDate to set
     */
    public void setSubmittedDate(Calendar submittedDate) {
        this.submittedDate = submittedDate;
    }

    /**
     * @return the siteUser
     */
    public SiteUser getSiteUser() {
        return this.siteUser;
    }

    /**
     * @param siteUser the siteUser to set
     */
    public void setSiteUser(SiteUser siteUser) {
        this.siteUser = siteUser;
    }

    public String getLateWarningMessage() {
        return lateWarningMessage;
    }

    public void setLateWarningMessage(String lateWarningMessage) {
        this.lateWarningMessage = lateWarningMessage;
    }

    public BigDecimal getPotentialPenalty() {
        return potentialPenalty;
    }

    public void setPotentialPenalty(BigDecimal potentialPenalty) {
        this.potentialPenalty = potentialPenalty;
    }

    /**
     * @return the dataFileReportPeriodValue
     */
    public String getDataFileReportPeriodValue() {
        return dataFileReportPeriodValue;
    }

    /**
     * @param dataFileReportPeriodValue the dataFileReportPeriodValue to set
     */
    public void setDataFileReportPeriodValue(String dataFileReportPeriodValue) {
        this.dataFileReportPeriodValue = dataFileReportPeriodValue;
    }

    /**
     * @return the dataFileStateValue
     */
    public String getDataFileStateValue() {
        return dataFileStateValue;
    }

    /**
     * @param dataFileStateValue the dataFileStateValue to set
     */
    public void setDataFileStateValue(String dataFileStateValue) {
        this.dataFileStateValue = dataFileStateValue;
    }

    /**
     * @return the improperFormattedValCnt
     */
    public Long getImproperFormattedValCnt() {
        return improperFormattedValCnt;
    }

    /**
     * @param improperFormattedValCnt the improperFormattedValCnt to set
     */
    public void setImproperFormattedValCnt(Long improperFormattedValCnt) {
        this.improperFormattedValCnt = improperFormattedValCnt;
    }

    /**
     * @return the formatErrCnt
     */
    public Long getFormatErrCnt() {
        return formatErrCnt;
    }

    /**
     * @param formatErrCnt the formatErrCnt to set
     */
    public void setFormatErrCnt(Long formatErrCnt) {
        this.formatErrCnt = formatErrCnt;
    }

    /**
     * @return the dataFileTransmissionTypeValue
     */
    public String getDataFileTransmissionTypeValue() {
        return dataFileTransmissionTypeValue;
    }

    /**
     * @param dataFileTransmissionTypeValue the dataFileTransmissionTypeValue to set
     */
    public void setDataFileTransmissionTypeValue(String dataFileTransmissionTypeValue) {
        this.dataFileTransmissionTypeValue = dataFileTransmissionTypeValue;
    }

    /**
     * @return the duplicateRecordsCnt
     */
    public Long getDuplicateRecordsCnt() {
        return duplicateRecordsCnt;
    }

    /**
     * @param duplicateRecordsCnt the duplicateRecordsCnt to set
     */
    public void setDuplicateRecordsCnt(Long duplicateRecordsCnt) {
        this.duplicateRecordsCnt = duplicateRecordsCnt;
    }

    public Long getRecordsCnt() {
        return recordsCnt;
    }

    public void setRecordsCnt(Long recordsCnt) {
        this.recordsCnt = recordsCnt;
    }


    /**
     * Informs caller of any transmission errors in the current transmission.
     * @return
     */
    public boolean hasNoErrorsInTransmission() {

        return (getImproperFormattedValCnt() == null || (getImproperFormattedValCnt() != null
                && getImproperFormattedValCnt() == 0))
                && (getFormatErrCnt() == null || (getFormatErrCnt() != null
                && getFormatErrCnt() == 0));
    }

    /**
     * Checks duplicate records have been counted.
     * @return
     */
    public boolean hasDuplicateRecords() {
        return (getDuplicateRecordsCnt() != null && getDuplicateRecordsCnt() > 0)
                && (getImproperFormattedValCnt() == null || (getImproperFormattedValCnt() != null
                && getImproperFormattedValCnt() == 0));
    }

    /**
     * Checks if any file format errors have been counted.
     * @return
     */
    public boolean hasFileFormatErrors() {
        return (getDuplicateRecordsCnt() != null && getDuplicateRecordsCnt() > 0)
                && (getImproperFormattedValCnt() != null && getImproperFormattedValCnt() > 0);
    }
}
