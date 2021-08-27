package gov.hhs.acf.cb.nytd.actions.transmission;

import gov.hhs.acf.cb.nytd.models.Transmission;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 1, 2010
 */
public class TransmissionDetail
{
	private Transmission transmission;
	private Integer baselineYouthCount;
	private Integer servedYouthCount;
	private Integer followupYouthCount;
	private Integer totalYouthCount;
	private String complianceStatus;
	private String submittedDate;
	private String federalSystemReceivedDate;
	private String fileProcessedDate;
	private String dueDate;

	public Transmission getTransmission()
	{
		return transmission;
	}

	public void setTransmission(Transmission transmission)
	{
		this.transmission = transmission;
	}

	public Integer getBaselineYouthCount()
	{
		return baselineYouthCount;
	}

	public void setBaselineYouthCount(Integer baselineYouthCount)
	{
		this.baselineYouthCount = baselineYouthCount;
	}

	public Integer getServedYouthCount()
	{
		return servedYouthCount;
	}

	public void setServedYouthCount(Integer servedYouthCount)
	{
		this.servedYouthCount = servedYouthCount;
	}

	public Integer getFollowupYouthCount()
	{
		return followupYouthCount;
	}

	public void setFollowupYouthCount(Integer followupYouthCount)
	{
		this.followupYouthCount = followupYouthCount;
	}

	public Integer getTotalYouthCount()
	{
		return totalYouthCount;
	}

	public void setTotalYouthCount(Integer totalYouthCount)
	{
		this.totalYouthCount = totalYouthCount;
	}

	public String getComplianceStatus()
	{
		return complianceStatus;
	}

	public void setComplianceStatus(String complianceStatus)
	{
		this.complianceStatus = complianceStatus;
	}

	public String getSubmittedDate()
	{
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate)
	{
		this.submittedDate = submittedDate;
	}

	public String getFederalSystemReceivedDate()
	{
		return federalSystemReceivedDate;
	}

	public void setFederalSystemReceivedDate(String federalSystemReceivedDate)
	{
		this.federalSystemReceivedDate = federalSystemReceivedDate;
	}

	public String getFileProcessedDate()
	{
		return fileProcessedDate;
	}

	public void setFileProcessedDate(String fileProcessedDate)
	{
		this.fileProcessedDate = fileProcessedDate;
	}

	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
}
