package gov.hhs.acf.cb.nytd.models.helper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;


/**
 * Primary key for Frequency
 * 
 * @author Adam Russell (18816)
 * @see Frequency
 */
@Embeddable
@EqualsAndHashCode
public class FrequencyId implements Serializable
{
	@Getter @Setter private String state;
	@Getter @Setter private String reportPeriod;
	@Getter @Setter private String elementNumber;
	@Getter @Setter private String value;
}
