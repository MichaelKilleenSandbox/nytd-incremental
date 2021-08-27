/**
 * Filename: ComplianceStandardReportHelper.java
 *
 *  Copyright 2009, ICF International
 *  Created: Feb 11, 2011
 *  Author: 16939
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the
 *  copyright owner.
 */



package gov.hhs.acf.cb.nytd.actions.report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ComplianceStandardReportHelper
{
  @Getter @Setter private String stateName;
  @Getter @Setter private String reportingPeriod;
  @Getter @Setter private String groupBy;
  @Getter @Setter private String timelyData;
  @Getter @Setter private String fileNumber;
  @Getter @Setter private String fileFormat;
  @Getter @Setter private String fileSubmissionErrorFree;
  @Getter @Setter private String dataStandardErrorFree;
  @Getter @Setter private String outcomeUniverse;
  @Getter @Setter private String participationInCare;
  @Getter @Setter private String participationDischarged;
  @Getter @Setter private String recordLevel;
  @Getter @Setter private String potentialPenalty;
  @Getter @Setter private String enforcedPenalty;  
  @Getter @Setter private String enforcedPenaltyReason;  
  @Getter @Setter private List<?> resultList;	
  
  @Getter @Setter private int enforcedFileFormatCount;
  @Getter @Setter private int enforcedTimelyCount;
  @Getter @Setter private int enforcedFileSubmissionErrorFreeCount;
  @Getter @Setter private int enforcedDataStandardsErrorFreeCount;
  @Getter @Setter private int enforcedOutcomeUniverseCount;
  @Getter @Setter private int enforcedParticipationInCareCount;
  @Getter @Setter private int enforcedParticipationDischargedCount;
  
 
}
