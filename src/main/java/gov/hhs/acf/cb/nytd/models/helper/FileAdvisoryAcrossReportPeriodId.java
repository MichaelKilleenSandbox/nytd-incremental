package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class FileAdvisoryAcrossReportPeriodId implements Serializable {
    @Getter @Setter private String recordNumber;
    @Getter @Setter private String reportPeriodName;
    @Getter @Setter private String transmissionId;
    @Getter @Setter private String elementName;
    @Getter @Setter private String selectedValue;
    @Getter @Setter private String targetValue;
}
