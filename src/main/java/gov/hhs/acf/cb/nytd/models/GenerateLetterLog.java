package gov.hhs.acf.cb.nytd.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@Builder(toBuilder = true)
public class GenerateLetterLog {

  private Long generateLetterLogId;
  private Transmission transmission;
  private TransmissionType transmissionType;
  private ReportingPeriod reportingPeriod;
  private State state;
  private SiteUser siteUser;
  private String complianceStatus;
  private LocalDateTime dateTimestamp;

}
