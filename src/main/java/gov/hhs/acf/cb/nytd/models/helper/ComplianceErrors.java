/**
 * Filename: ComplianceErrors.java
 * 
 * Copyright 2009, ICF International
 * Created: May 15, 2009
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

import org.apache.log4j.Logger;

/**
 * @author 16939
 * 
 */
public class ComplianceErrors
{
	protected Logger log = Logger.getLogger(getClass());
	private String recordNumber;
	private String elementName;
	private String elementNumber;
	private String compliance;
	private String description;
	private String note;
	private String datumValue;
	private String actualRate;
	private String dataQualityAdvRate;
	private String nameKey;
	private String potentialPenalty;
	private String reportingPeriod;
	private String state;

	// TO DO remove this in future
	private Long elementNumberLong;

	/**
	 * @return the recordNumber
	 */
	public String getRecordNumber()
	{
		return recordNumber;
	}

	/**
	 * @param recordNumber
	 *           the recordNumber to set
	 */
	public void setRecordNumber(String recordNumber)
	{
		this.recordNumber = recordNumber;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName()
	{
		return elementName;
	}

	/**
	 * @param elementName
	 *           the elementName to set
	 */
	public void setElementName(String elementName)
	{
		this.elementName = elementName;
	}

	/**
	 * @return the elementNumber
	 */
	public String getElementNumber()
	{
		return elementNumber;
	}

	/**
	 * @param elementNumber
	 *           the elementNumber to set
	 */
	public void setElementNumber(String elementNumber)
	{
		this.elementNumber = elementNumber;
		if (this.elementNumber != null)
			setElementNumberLong(new Long(this.elementNumber));
	}

	/**
	 * @return the compliance
	 */
	public String getCompliance()
	{
		return compliance;
	}

	/**
	 * @param compliance
	 *           the compliance to set
	 */
	public void setCompliance(String compliance)
	{
		this.compliance = compliance;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the note
	 */
	public String getNote()
	{
		return note;
	}

	/**
	 * @param note
	 *           the note to set
	 */
	public void setNote(String note)
	{
		this.note = note;
	}

	/**
	 * @return the datumValue
	 */
	public String getDatumValue()
	{
		return datumValue;
	}

	/**
	 * @param datumValue
	 *           the datumValue to set
	 */
	public void setDatumValue(String datumValue)
	{
		this.datumValue = datumValue;
	}

	/**
	 * Commenting the function. It was just used for the prototype release
	 * 
	 * @param actualRate
	 *           the actualRate to set
	 */
	// Changing while refactoring, remove the commented lines after testing
	/*
	public void setActualRate(String actualRate, String complianceType)
	{
		if (complianceType != null && complianceType.equalsIgnoreCase(Constants.COMPLIANCE))
		{
			double newActualRate = 0;
			double percent = 100.00;
			NumberFormat formatter = new DecimalFormat("##0.00");
			log.debug(actualRate);
			if (actualRate != null && !actualRate.equalsIgnoreCase(""))
			{
				int indexPercent = actualRate.indexOf("%");
				if (indexPercent > 0)
					actualRate = actualRate.substring(0, indexPercent);
				newActualRate = (percent - Double.parseDouble(actualRate));
				actualRate = formatter.format(newActualRate);
				log.debug("actualRate:" + actualRate);
			}
		}
		this.actualRate = actualRate;
	}
	*/
	
	/**
	 * @return the dataQualityAdvRate
	 */
	public String getDataQualityAdvRate()
	{
		return dataQualityAdvRate;
	}

	/**
	 * @param dataQualityAdvRate
	 *           the dataQualityAdvRate to set
	 */
	public void setDataQualityAdvRate(String dataQualityAdvRate)
	{
		this.dataQualityAdvRate = dataQualityAdvRate;
	}

	/**
	 * @return the nameKey
	 */
	public String getNameKey()
	{
		return nameKey;
	}

	/**
	 * @param nameKey
	 *           the nameKey to set
	 */
	public void setNameKey(String nameKey)
	{
		this.nameKey = nameKey;
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
	 * @return the state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state
	 *           the state to set
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * @return the elementNumberLong
	 */
	public Long getElementNumberLong()
	{
		return elementNumberLong;
	}

	/**
	 * @param elementNumberLong
	 *           the elementNumberLong to set
	 */
	public void setElementNumberLong(Long elementNumberLong)
	{
		this.elementNumberLong = elementNumberLong;
		if ((this.elementNumberLong.intValue() < 5.1) || (this.elementNumberLong.intValue() == 14)
				|| (this.elementNumberLong.intValue() == 36))
		{
			this.setPotentialPenalty("2.5%");
		}
		else
			this.setPotentialPenalty("1.25%");
	}

	/**
	 * @return the actualRate
	 */
	public String getActualRate()
	{
		return actualRate;
	}

	/**
	 * @param actualRate
	 *           the actualRate to set
	 */
	public void setActualRate(String actualRate)
	{
		this.actualRate = actualRate;
	}

}
