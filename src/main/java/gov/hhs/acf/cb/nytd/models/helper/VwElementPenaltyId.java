package gov.hhs.acf.cb.nytd.models.helper;

import java.math.BigDecimal;
import java.util.Calendar;

@SuppressWarnings("serial")
public class VwElementPenaltyId implements java.io.Serializable
{
	private Long transmissionId;
	private Long regionId;
	private String region;
	private String state;
	private Long stateId;
	private String stateAbbr;
	private String reportingPeriod;
	private Long reportingPeriodId;
	private Boolean status;
	private BigDecimal percentValue;
	private BigDecimal potentialPenalty;
	private BigDecimal complianceStatus;
	private Long elementId;
	private Long elementNumber;
	private String element;
	private Calendar submittedDate;

	public VwElementPenaltyId()
	{
	}

	public VwElementPenaltyId(Long transmissionId)
	{
		this.transmissionId = transmissionId;
	}

	public VwElementPenaltyId(Long transmissionId, Long regionId, String region, String state, Long stateId,
			String stateAbbr, String reportingPeriod, Long reportingPeriodId, Boolean status,
			BigDecimal percentValue, BigDecimal potentialPenalty, BigDecimal complianceStatus, Long elementId,
			Long elementNumber, String element)
	{
		this.transmissionId = transmissionId;
		this.regionId = regionId;
		this.region = region;
		this.state = state;
		this.stateId = stateId;
		this.stateAbbr = stateAbbr;
		this.reportingPeriod = reportingPeriod;
		this.reportingPeriodId = reportingPeriodId;
		this.status = status;
		this.percentValue = percentValue;
		this.potentialPenalty = potentialPenalty;
		this.complianceStatus = complianceStatus;
		this.elementId = elementId;
		this.elementNumber = elementNumber;
		this.element = element;
	}

	public Long getTransmissionId()
	{
		return this.transmissionId;
	}

	public void setTransmissionId(Long transmissionId)
	{
		this.transmissionId = transmissionId;
	}

	public Long getRegionId()
	{
		return this.regionId;
	}

	public void setRegionId(Long regionId)
	{
		this.regionId = regionId;
	}

	public String getRegion()
	{
		return this.region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public Long getStateId()
	{
		return this.stateId;
	}

	public void setStateId(Long stateId)
	{
		this.stateId = stateId;
	}

	public String getStateAbbr()
	{
		return this.stateAbbr;
	}

	public void setStateAbbr(String stateAbbr)
	{
		this.stateAbbr = stateAbbr;
	}

	public String getReportingPeriod()
	{
		return this.reportingPeriod;
	}

	public void setReportingPeriod(String reportingPeriod)
	{
		this.reportingPeriod = reportingPeriod;
	}

	public Long getReportingPeriodId()
	{
		return this.reportingPeriodId;
	}

	public void setReportingPeriodId(Long reportingPeriodId)
	{
		this.reportingPeriodId = reportingPeriodId;
	}

	public Boolean getStatus()
	{
		return this.status;
	}

	public void setStatus(Boolean status)
	{
		this.status = status;
	}

	public BigDecimal getPercentValue()
	{
		return this.percentValue;
	}

	public void setPercentValue(BigDecimal percentValue)
	{
		this.percentValue = percentValue;
	}

	public BigDecimal getPotentialPenalty()
	{
		return this.potentialPenalty;
	}

	public void setPotentialPenalty(BigDecimal potentialPenalty)
	{
		this.potentialPenalty = potentialPenalty;
	}

	public BigDecimal getComplianceStatus()
	{
		return this.complianceStatus;
	}

	public void setComplianceStatus(BigDecimal complianceStatus)
	{
		this.complianceStatus = complianceStatus;
	}

	public Long getElementId()
	{
		return this.elementId;
	}

	public void setElementId(Long elementId)
	{
		this.elementId = elementId;
	}

	public Long getElementNumber()
	{
		return this.elementNumber;
	}

	public void setElementNumber(Long elementNumber)
	{
		this.elementNumber = elementNumber;
	}

	public String getElement()
	{
		return this.element;
	}

	public void setElement(String element)
	{
		this.element = element;
	}

	/**
	 * @return the submittedDate
	 */
	public Calendar getSubmittedDate()
	{
		return submittedDate;
	}

	/**
	 * @param submittedDate
	 *           the submittedDate to set
	 */
	public void setSubmittedDate(Calendar submittedDate)
	{
		this.submittedDate = submittedDate;
	}

	public boolean equals(Object other)
	{
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof VwElementPenaltyId))
			return false;
		VwElementPenaltyId castOther = (VwElementPenaltyId) other;

		return (this.getTransmissionId() == castOther.getTransmissionId())
				&& ((this.getRegionId() == castOther.getRegionId()) || (this.getRegionId() != null
						&& castOther.getRegionId() != null && this.getRegionId().equals(castOther.getRegionId())))
				&& ((this.getRegion() == castOther.getRegion()) || (this.getRegion() != null
						&& castOther.getRegion() != null && this.getRegion().equals(castOther.getRegion())))
				&& ((this.getStateId() == castOther.getStateId()) || (this.getStateId() != null
						&& castOther.getStateId() != null && this.getStateId().equals(castOther.getStateId())))
				&& ((this.getStateAbbr() == castOther.getStateAbbr()) || (this.getStateAbbr() != null
						&& castOther.getStateAbbr() != null && this.getStateAbbr().equals(castOther.getStateAbbr())))
				&& ((this.getReportingPeriod() == castOther.getReportingPeriod()) || (this.getReportingPeriod() != null
						&& castOther.getReportingPeriod() != null && this.getReportingPeriod().equals(
						castOther.getReportingPeriod())))
				&& ((this.getReportingPeriodId() == castOther.getReportingPeriodId()) || (this
						.getReportingPeriodId() != null
						&& castOther.getReportingPeriodId() != null && this.getReportingPeriodId().equals(
						castOther.getReportingPeriodId())))
				&& ((this.getStatus() == castOther.getStatus()) || (this.getStatus() != null
						&& castOther.getStatus() != null && this.getStatus().equals(castOther.getStatus())))
				&& ((this.getPercentValue() == castOther.getPercentValue()) || (this.getPercentValue() != null
						&& castOther.getPercentValue() != null && this.getPercentValue().equals(
						castOther.getPercentValue())))
				&& ((this.getPotentialPenalty() == castOther.getPotentialPenalty()) || (this
						.getPotentialPenalty() != null
						&& castOther.getPotentialPenalty() != null && this.getPotentialPenalty().equals(
						castOther.getPotentialPenalty())))
				&& ((this.getComplianceStatus() == castOther.getComplianceStatus()) || (this
						.getComplianceStatus() != null
						&& castOther.getComplianceStatus() != null && this.getComplianceStatus().equals(
						castOther.getComplianceStatus())))
				&& ((this.getElementId() == castOther.getElementId()) || (this.getElementId() != null
						&& castOther.getElementId() != null && this.getElementId().equals(castOther.getElementId())))
				&& ((this.getElementNumber() == castOther.getElementNumber()) || (this.getElementNumber() != null
						&& castOther.getElementNumber() != null && this.getElementNumber().equals(
						castOther.getElementNumber())))
				&& ((this.getElement() == castOther.getElement()) || (this.getElement() != null
						&& castOther.getElement() != null && this.getElement().equals(castOther.getElement())));
	}

	public int hashCode()
	{
		int result = 17;

		result = (int) (37 * result + this.getTransmissionId());
		result = 37 * result + (getRegionId() == null ? 0 : this.getRegionId().hashCode());
		result = 37 * result + (getRegion() == null ? 0 : this.getRegion().hashCode());
		result = 37 * result + (getStateId() == null ? 0 : this.getStateId().hashCode());
		result = 37 * result + (getStateAbbr() == null ? 0 : this.getStateAbbr().hashCode());
		result = 37 * result + (getReportingPeriod() == null ? 0 : this.getReportingPeriod().hashCode());
		result = 37 * result + (getReportingPeriodId() == null ? 0 : this.getReportingPeriodId().hashCode());
		result = 37 * result + (getStatus() == null ? 0 : this.getStatus().hashCode());
		result = 37 * result + (getPercentValue() == null ? 0 : this.getPercentValue().hashCode());
		result = 37 * result + (getPotentialPenalty() == null ? 0 : this.getPotentialPenalty().hashCode());
		result = 37 * result + (getComplianceStatus() == null ? 0 : this.getComplianceStatus().hashCode());
		result = 37 * result + (getElementId() == null ? 0 : this.getElementId().hashCode());
		result = 37 * result + (getElementNumber() == null ? 0 : this.getElementNumber().hashCode());
		result = 37 * result + (getElement() == null ? 0 : this.getElement().hashCode());
		return result;
	}
}
