package gov.hhs.acf.cb.nytd.models.helper;

//TODO: A lot of unused imports due to trying various approaches

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents an entry in a NYTD frequency table.
 * 
 * @author Adam Russell (18816)
 */
@Entity
@Immutable
@SqlResultSetMapping(name="frequencyMapping",
                     entities=@EntityResult(entityClass=Frequency.class))
@NamedStoredProcedureQuery(
	    name = "spFrequencyReport",
		procedureName = "spFrequencyReport",
	    resultClasses = Frequency.class,
		parameters = {
				@StoredProcedureParameter(mode=ParameterMode.REF_CURSOR, type=void.class),
				@StoredProcedureParameter(mode=ParameterMode.IN, type=String.class),
				@StoredProcedureParameter(mode=ParameterMode.IN, type=String.class),
				@StoredProcedureParameter(mode=ParameterMode.IN, type=String.class),
				@StoredProcedureParameter(mode=ParameterMode.IN, type=Integer.class),
				@StoredProcedureParameter(mode=ParameterMode.IN, type=Integer.class),
				@StoredProcedureParameter(mode=ParameterMode.IN, type=Integer.class)
		}
)

@EqualsAndHashCode
public class Frequency implements Serializable
{
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="state", column=@Column(name="STATENAME")),
		@AttributeOverride(name="reportPeriod", column=@Column(name="REPORTINGPERIOD")),
		@AttributeOverride(name="elementNumber", column=@Column(name="ELEMENTNUMBER")),
		@AttributeOverride(name="value", column=@Column(name="VALUE"))
	})
	@Getter
	@Setter
	private FrequencyId id;
	@Column(name="REPORTINGPERIODID") @Getter @Setter private String reportingPeriodId;
	@Column(name="STATEID") @Getter @Setter private String stateId;
	@Column(name="ELEMENTNAME") @Getter @Setter private String elementName;
	@Column(name="VALUECOUNT") @Getter @Setter private String count;
	@Column(name="PERCENTTOTAL") @Getter @Setter private String percent;
	
	public String getFormattedPercent()
	{
		return String.format("%.2f%%", Float.valueOf(getPercent()));
	}

	public String getState()
	{
		return getId().getState();
	}
	
	public void setState(String state)
	{
		getId().setState(state);
	}
	
	public String getReportPeriod()
	{
		return getId().getReportPeriod();
	}
	
	public void setReportPeriod(String reportPeriod)
	{
		getId().setReportPeriod(reportPeriod);
	}
	
	public String getElementNumber()
	{
		return getId().getElementNumber();
	}
	
	public void setElementNumber(String elementNumber)
	{
		getId().setElementNumber(elementNumber);
	}
	
	public String getValue()
	{
		return getId().getValue();
	}
	
	public void setValue(String value)
	{
		getId().setValue(value);
	}
}
