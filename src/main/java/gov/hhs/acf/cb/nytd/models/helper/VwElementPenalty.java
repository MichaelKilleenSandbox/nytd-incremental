package gov.hhs.acf.cb.nytd.models.helper;

import java.util.Calendar;

public class VwElementPenalty implements java.io.Serializable
{

	private VwElementPenaltyId id;
	private Calendar submittedDate;
	private String submissionStatus;

	public VwElementPenalty()
	{
	}

	public VwElementPenalty(VwElementPenaltyId id)
	{
		this.id = id;
	}

	public VwElementPenaltyId getId()
	{ 
		return this.id;
	}

	public void setId(VwElementPenaltyId id)
	{
		this.id = id;
	}
	
	public boolean equals(VwElementPenaltyId vwElementPenaltyId)
	{
		boolean isEqual = false;
		if( 
				this.id.getTransmissionId() == vwElementPenaltyId.getTransmissionId() &&
				this.id.getRegionId() == vwElementPenaltyId.getRegionId() &&
				this.id.getRegion().equals(vwElementPenaltyId.getRegion())&&
				this.id.getStateId()  == vwElementPenaltyId.getStateId()	&&
				this.id.getStateAbbr().equals(vwElementPenaltyId.getStateAbbr()) &&
				this.id.getReportingPeriod().equals(vwElementPenaltyId.getReportingPeriod())&&
				this.id.getRegionId() == vwElementPenaltyId.getRegionId() &&
				this.id.getStatus() == vwElementPenaltyId.getStatus() &&
				this.id.getPercentValue() == vwElementPenaltyId.getPercentValue() &&
				this.id.getPotentialPenalty() == vwElementPenaltyId.getPotentialPenalty() &&
				this.id.getComplianceStatus() == vwElementPenaltyId.getComplianceStatus() &&
				this.id.getElementId().equals(vwElementPenaltyId.getElementId()) &&
				this.id.getElementNumber().equals(vwElementPenaltyId.getElementNumber()) &&
				this.id.getElement().equals(vwElementPenaltyId.getElement())
				
			)
			
		{
			if (this.id.getSubmittedDate()!= null && vwElementPenaltyId.getSubmittedDate() != null)
			{
				if (this.id.getSubmittedDate().equals(vwElementPenaltyId.getSubmittedDate()))
				{
					 isEqual = true;
				}
			} else if (this.id.getSubmittedDate()== null && vwElementPenaltyId.getSubmittedDate() == null)
			{
					isEqual = true;
			}
		}
	
		return isEqual;
	}
	
	/**
	 * @return the submittedDate
	 */
	public Calendar getSubmittedDate()
	{
		return submittedDate;
	}

	/**
	 * @param submittedDate the submittedDate to set
	 */
	public void setSubmittedDate(Calendar submittedDate)
	{
		this.submittedDate = submittedDate;
	}

	public String getSubmissionStatus() {
		return submissionStatus;
	}

	public void setSubmissionStatus(String submissionStatus) {
		this.submissionStatus = submissionStatus;
	}
}
