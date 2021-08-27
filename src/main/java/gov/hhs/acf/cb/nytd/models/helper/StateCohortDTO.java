package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class StateCohortDTO {
	@Getter @Setter private BigDecimal COHORTSID;
	@Getter @Setter private String NAME;
	@Getter @Setter private BigDecimal  SAMPLINGREQUESTID;
	@Getter @Setter private BigDecimal  REQUESTSTATUSID;
	@Getter @Setter private String  REQUESTSTATUS;
	@Getter @Setter private String REPORTYEAR17;
}
