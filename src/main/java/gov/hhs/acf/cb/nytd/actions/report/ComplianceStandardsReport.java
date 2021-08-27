package gov.hhs.acf.cb.nytd.actions.report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ComplianceStandardsReport
{
      @Getter @Setter private String name;
	  @Getter @Setter private int fileFormatCount;
	  @Getter @Setter private int timelyCount;
	  @Getter @Setter private int fileSubmissionErrorFreeCount;
	  @Getter @Setter private int dataStandardsErrorFreeCount;
	  @Getter @Setter private int outcomeUniverseCount;
	  @Getter @Setter private int participationInCareCount;
	  @Getter @Setter private int participationDischargedCount;
	  @Getter @Setter private List<ComplianceStandardReportHelper> resultList;	
	
	  @Getter @Setter private int enforcedFileFormatCount;
	  @Getter @Setter private int enforcedTimelyCount;
	  @Getter @Setter private int enforcedFileSubmissionErrorFreeCount;
	  @Getter @Setter private int enforcedDataStandardsErrorFreeCount;
	  @Getter @Setter private int enforcedOutcomeUniverseCount;
	  @Getter @Setter private int enforcedParticipationInCareCount;
	  @Getter @Setter private int enforcedParticipationDischargedCount;


}
