package gov.hhs.acf.cb.nytd.models.helper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;


/**
 * Primary key for RecordForFileComparisonWithinReportPeriod.
 * 
 * @author Adam Russell (18816)
 * @see RecordForFileComparisonWithinReportPeriod
 */
@Embeddable
@EqualsAndHashCode
public class RecordForFileComparisonWithinReportPeriodId implements Serializable
{
	@Getter @Setter private String recordNumber;
	@Getter @Setter private Long transmissionId;
	@Getter @Setter private Boolean matched;
	@Getter @Setter private Boolean changed;
	@Getter @Setter private Boolean inError;
}
