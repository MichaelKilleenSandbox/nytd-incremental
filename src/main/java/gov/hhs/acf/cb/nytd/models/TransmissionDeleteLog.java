/**
 * 
 */
package gov.hhs.acf.cb.nytd.models;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author 15178
 *
 */
public class TransmissionDeleteLog implements Serializable {
	private Long transmissionId;
	private TransmissionType transmissionType;
	private ReportingPeriod reportingPeriod;
    private String complianceStatus;
	private SiteUser siteUser;
	private String fileName;
	private Integer fileSize;
	private Calendar fileReceivedDate;
	private State state;
	private Calendar deletedDate;
	private String deletedBy;
	private Long recordCount;
	private String description;
	


	public TransmissionDeleteLog()
	{
	}

	public TransmissionDeleteLog(Long transmissionId)
	{
		this.transmissionId = transmissionId;
	}

	public TransmissionDeleteLog(Long transmissionId, TransmissionType transmissionType, ReportingPeriod reportingPeriod,
			Calendar deletedDate, String deletedBy, String complianceStatus, Calendar fileReceivedDate, SiteUser siteUser,
			String fileName, Integer fileSize, State state, Long recordCount)
	{
		this.transmissionId = transmissionId;
		this.transmissionType = transmissionType;
		this.reportingPeriod = reportingPeriod;
		this.deletedDate = deletedDate;
		this.deletedBy = deletedBy;
		this.siteUser = siteUser;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileReceivedDate = fileReceivedDate;
		this.state = state;
		this.recordCount = recordCount;
		this.complianceStatus = complianceStatus;

	}

	public Long getTransmissionId() {
		return transmissionId;
	}

	public void setTransmissionId(Long transmissionId) {
		this.transmissionId = transmissionId;
	}

	public TransmissionType getTransmissionType() {
		return transmissionType;
	}

	public void setTransmissionType(TransmissionType transmissionType) {
		this.transmissionType = transmissionType;
	}

	public ReportingPeriod getReportingPeriod() {
		return reportingPeriod;
	}

	public void setReportingPeriod(ReportingPeriod reportingPeriod) {
		this.reportingPeriod = reportingPeriod;
	}

	public String getComplianceStatus() {
		return complianceStatus;
	}

	public void setComplianceStatus(String complianceStatus) {
		this.complianceStatus = complianceStatus;
	}

	public SiteUser getSiteUser() {
		return siteUser;
	}

	public void setSiteUser(SiteUser siteUser) {
		this.siteUser = siteUser;
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

	public Calendar getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Calendar deletedDate) {
		this.deletedDate = deletedDate;
	}

	public String getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}
