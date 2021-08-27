/**
 * Filename: FileAdvisoryAcrossReportPeriods.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Dec 8, 2009
 *  Author: adam
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;


/**
 * A cross-file advisory regarding previous reporting periods.
 * 
 * More specifically, an advisory related to a record in a transmitted data file
 * being compared to previous reporting periods.  Contains Record Number,
 * Report Period, Federal File ID, Element, and Problem Description.
 * 
 * @author Adam Russell (18816)
 */
@Entity
@Immutable
@SqlResultSetMapping(name="FileAdvisoryAcrossReportPeriodsMapping",
		entities=@EntityResult(entityClass=FileAdvisoryAcrossReportPeriods.class))
@NamedStoredProcedureQuery(name="spCrossReportPdCompare",
		procedureName="spCrossReportPdCompare",
		resultClasses=FileAdvisoryAcrossReportPeriods.class,
		parameters={
				@StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class),

				@StoredProcedureParameter(mode = ParameterMode.IN, type = Long.class),
				@StoredProcedureParameter(mode = ParameterMode.IN, type = Long.class),

		}
)
public class FileAdvisoryAcrossReportPeriods implements Serializable
{
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name="recordNumber", column=@Column(name="RECORDNUMBER")),
			@AttributeOverride(name="reportPeriodName", column=@Column(name="reportperiod")),
			@AttributeOverride(name="transmissionId", column=@Column(name="TRANSMISSIONID")),
			@AttributeOverride(name="elementName", column=@Column(name="elementNumber")),
			@AttributeOverride(name="selectedValue", column=@Column(name="selectedvalue")),
			@AttributeOverride(name="targetValue", column=@Column(name="targetvalue"))
	})
	@Getter
	@Setter
	private FileAdvisoryAcrossReportPeriodId id;
	@Column(name="elementdescription") @Getter @Setter private String elementDescription;
	@Column(name="problemdescription") @Getter @Setter private String problemDescription;
	@Column(name="selectedtransmissionid") @Getter @Setter private String selectedTransmissionId;
	@Column(name="problemdescriptionid") @Getter @Setter private String problemDescriptionId;
	@Column(name="recordNumber") @Getter @Setter private String recordNumber;
	@Column(name="transmissionId") @Getter @Setter private String transmissionId;

	// private String recordNumber;
	// private String reportPeriodName;
	 //private String transmissionId;
	// private String elementName;
	 //private String elementDescription;
	 //private String problemDescription;
	 //private String selectedValue;
	 //private String targetValue;
	// private long selectedTransmissionId;
	 //private long problemDescriptionId;
	/*public String getRecordNumber()
	{
		return recordNumber;
	}
	public void setRecordNumber(String recordNumber)
	{
		this.recordNumber = recordNumber;
	}
	public String getReportPeriodName()
	{
		return reportPeriodName;
	}
	public void setReportPeriodName(String reportPeriodName)
	{
		this.reportPeriodName = reportPeriodName;
	}
	public String getTransmissionId()
	{
		return transmissionId;
	}
	public void setTransmissionId(String transmissionId)
	{
		this.transmissionId = transmissionId;
	}
	public String getElementName()
	{
		return elementName;
	}
	public void setElementName(String elementName)
	{
		this.elementName = elementName;
	}
	public String getElementDescription()
	{
		return elementDescription;
	}
	public void setElementDescription(String elementDescription)
	{
		this.elementDescription = elementDescription;
	}
	public String getProblemDescription()
	{
		return problemDescription;
	}
	public void setProblemDescription(String problemDescription)
	{
		this.problemDescription = problemDescription;
	}
	public String getSelectedValue()
	{
		return selectedValue;
	}
	public void setSelectedValue(String selectedValue)
	{
		this.selectedValue = selectedValue;
	}
	public String getTargetValue()
	{
		return targetValue;
	}
	public void setTargetValue(String targetValue)
	{
		this.targetValue = targetValue;
	}
	public long getSelectedTransmissionId()
	{
		return selectedTransmissionId;
	}
	public void setSelectedTransmissionId(long selectedTransmissionId)
	{
		this.selectedTransmissionId = selectedTransmissionId;
	}
	public long getProblemDescriptionId()
	{
		return problemDescriptionId;
	}
	public void setProblemDescriptionId(long problemDescriptionId)
	{
		this.problemDescriptionId = problemDescriptionId;
	}*/
}
