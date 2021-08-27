package gov.hhs.acf.cb.nytd.models.helper;

//TODO: A lot of unused imports due to trying various approaches

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Represents a categorized record for a cross-file comparison within a report period.
 * 
 * @author Adam Russell (18816)
 */
@Entity
@Immutable
@SqlResultSetMapping(name="recordForFileComparisonWithinReportPeriodMapping",
                     entities=@EntityResult(entityClass=RecordForFileComparisonWithinReportPeriod.class))
@NamedStoredProcedureQuery(name="spCrossFileCompWithinReportPd",
					procedureName="spCrossFileCompWithinReportPd",
                 resultClasses=RecordForFileComparisonWithinReportPeriod.class,
                 parameters={
						@StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class),

						@StoredProcedureParameter(mode = ParameterMode.IN, type = Long.class),
						@StoredProcedureParameter(mode = ParameterMode.IN, type = Long.class),

				 }
)

public class RecordForFileComparisonWithinReportPeriod implements Serializable
{	
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="recordNumber", column=@Column(name="RECORDNUMBER")),
		@AttributeOverride(name="transmissionId", column=@Column(name="TRANSMISSIONID")),
		@AttributeOverride(name="matched", column=@Column(name="ISMATCHED")),
		@AttributeOverride(name="changed", column=@Column(name="ISCHANGED")),
		@AttributeOverride(name="inError", column=@Column(name="INERROR"))
	})
	@Getter
	@Setter
	private RecordForFileComparisonWithinReportPeriodId id;
}
