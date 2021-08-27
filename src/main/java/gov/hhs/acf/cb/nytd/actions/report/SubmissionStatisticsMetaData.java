package gov.hhs.acf.cb.nytd.actions.report;

import lombok.Getter;
import lombok.Setter;

public class SubmissionStatisticsMetaData
{   
	@Getter @Setter private int totalStatesSubmitted;
	@Getter @Setter private	int totalStatesTransmitted;
	@Getter @Setter private	int totalTransmissionAcrossStates;
	@Getter @Setter private	int totalSubmissionAcrossStates;	
	@Getter @Setter private	String selectedReportingPeriodName;	
 
}
