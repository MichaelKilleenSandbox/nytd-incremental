/**
 * Filename: MetaDataUtil.java
 * 
 * Copyright 2009, ICF International
 * Created: Jun 19, 2009
 * Author: 16939
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.util.DateUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author 16939
 * 
 */
public class MetaDataUtil
{
	private String complianceStatus;
	private String fileName;
	private String fileGenerationDate;
	private String dateTransmissionReceived;
	private String submissionDate;
	private String federalSystemReceivedDate;
	private String reportingPeriod;
	private String transmissionType;

	private String potentialPenalty;
	private Long transmissionId;
	private String federalFileId;
	private String fileType;
	private String stateAbbr;
	private int servedPopulationCount;
	private int baselinePopulationCount;
	private int followupPopulationCount;
	private BigDecimal totalRecordsinFile;
	private String submitter;

	/**
	 * @return the submitter
	 */
	public String getSubmitter()
	{
		return submitter;
	}

	/**
	 * @param submitter
	 *           the submitter to set
	 */
	public void setSubmitter(String submitter)
	{
		this.submitter = submitter;
	}

	/**
	 * @return the totalRecordsinFile
	 */
	public BigDecimal getTotalRecordsinFile()
	{
		return totalRecordsinFile;
	}

	/**
	 * @param totalRecordsinFile
	 *           the totalRecordsinFile to set
	 */
	public void setTotalRecordsinFile(BigDecimal totalRecordsinFile)
	{
		this.totalRecordsinFile = totalRecordsinFile;
	}

	/**
	 * @return the servedPopulationCount
	 */
	public int getServedPopulationCount()
	{
		return servedPopulationCount;
	}

	/**
	 * @param servedPopulationCount
	 *           the servedPopulationCount to set
	 */
	public void setServedPopulationCount(int servedPopulationCount)
	{
		this.servedPopulationCount = servedPopulationCount;
	}

	/**
	 * @return the baselinePopulationCount
	 */
	public int getBaselinePopulationCount()
	{
		return baselinePopulationCount;
	}

	/**
	 * @param baselinePopulationCount
	 *           the baselinePopulationCount to set
	 */
	public void setBaselinePopulationCount(int baselinePopulationCount)
	{
		this.baselinePopulationCount = baselinePopulationCount;
	}

	/**
	 * @return the followupPopulationCount
	 */
	public int getFollowupPopulationCount()
	{
		return followupPopulationCount;
	}

	/**
	 * @param followupPopulationCount
	 *           the followupPopulationCount to set
	 */
	public void setFollowupPopulationCount(int followupPopulationCount)
	{
		this.followupPopulationCount = followupPopulationCount;
	}

	/**
	 * @return the complianceStatus
	 */
	public String getComplianceStatus()
	{
		return complianceStatus;
	}

	/**
	 * @param complianceStatus
	 *           the complianceStatus to set
	 */
	public void setComplianceStatus(String complianceStatus)
	{
		this.complianceStatus = complianceStatus;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * @param fileName
	 *           the fileName to set
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/**
	 * @return the fileGenerationDate
	 */
	public String getFileGenerationDate()
	{
		return fileGenerationDate;
	}

	/**
	 * @param fileGenerationDate
	 *           the fileGenerationDate to set
	 */
	public void setFileGenerationDate(String fileGenerationDate)
	{
		this.fileGenerationDate = fileGenerationDate;
	}

	/**
	 * @return the reportingPeriod
	 */
	public String getReportingPeriod()
	{
		return reportingPeriod;
	}

	/**
	 * @param reportingPeriod
	 *           the reportingPeriod to set
	 */
	public void setReportingPeriod(String reportingPeriod)
	{
		this.reportingPeriod = reportingPeriod;
	}

	/**
	 * @return the transmissionType
	 */
	public String getTransmissionType()
	{
		return transmissionType;
	}

	/**
	 * @param transmissionType
	 *           the transmissionType to set
	 */
	public void setTransmissionType(String transmissionType)
	{
		this.transmissionType = transmissionType;
	}

	/**
	 * @return the potentialPenalty
	 */
	public String getPotentialPenalty()
	{
		return potentialPenalty;
	}

	/**
	 * @param potentialPenalty
	 *           the potentialPenalty to set
	 */
	public void setPotentialPenalty(String potentialPenalty)
	{
		this.potentialPenalty = potentialPenalty;
	}

	/**
	 * @return the transmissionId
	 */
	public Long getTransmissionId()
	{
		return transmissionId;
	}

	/**
	 * @param transmissionId
	 *           the transmissionId to set
	 */
	public void setTransmissionId(Long transmissionId)
	{
		this.transmissionId = transmissionId;
	}

	/**
	 * @return the federalFileId
	 */
	public String getFederalFileId()
	{
		return federalFileId;
	}

	/**
	 * @param federalFileId
	 *           the federalFileId to set
	 */
	public void setFederalFileId(String federalFileId)
	{
		this.federalFileId = federalFileId;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType()
	{
		return fileType;
	}

	/**
	 * @param fileType
	 *           the fileType to set
	 */
	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	/**
	 * @return the stateAbbr
	 */
	public String getStateAbbr()
	{
		return stateAbbr;
	}

	/**
	 * @param stateAbbr
	 *           the stateAbbr to set
	 */
	public void setStateAbbr(String stateAbbr)
	{
		this.stateAbbr = stateAbbr;
	}

	/**
	 * @return the submissionDate
	 */
	public String getSubmissionDate()
	{
		if (submissionDate != null)
			return getFormattedDate(submissionDate);
		else
			return submissionDate;
	}

	/**
	 * @param submissionDate
	 *           the submissionDate to set
	 */
	public void setSubmissionDate(String submissionDate)
	{
		this.submissionDate = submissionDate;
	}

	/**
	 * @return the federalSystemReceivedDate
	 */
	public String getFederalSystemReceivedDate()
	{
		return getFormattedDate(federalSystemReceivedDate);
	}

	/**
	 * @param federalSystemReceivedDate
	 *           the federalSystemReceivedDate to set
	 */
	public void setFederalSystemReceivedDate(String federalSystemReceivedDate)
	{
		this.federalSystemReceivedDate = federalSystemReceivedDate;
	}

	/**
	 * @return the dateTransmissionReceived
	 */
	public String getDateTransmissionReceived()
	{
		return getFormattedDate(dateTransmissionReceived);
	}

	/**
	 * @param dateTransmissionReceived
	 *           the dateTransmissionReceived to set
	 */
	public void setDateTransmissionReceived(String dateTransmissionReceived)
	{
		this.dateTransmissionReceived = dateTransmissionReceived;
	}

	private String getFormattedDate(String strDate)
	{
		Calendar tempDate = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
		String tempStrDate = null;
		try
		{
			tempDate.setTime(format.parse(strDate));
			tempDate.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			tempStrDate = DateUtil.getHourMintueSecondWithTimeZone(tempDate);

		}
		catch (Exception e)
		{
			e.printStackTrace();

		}

		return tempStrDate;

	}
}
