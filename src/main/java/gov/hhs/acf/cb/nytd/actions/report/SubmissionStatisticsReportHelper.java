/**
 * Filename: SubmissionStatisticsReportHelper.java
 *
 *  Copyright 2009, ICF International
 *  Created: Dec 16, 2010
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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class SubmissionStatisticsReportHelper
{
	@Getter @Setter private String stateName;
	@Getter @Setter private BigDecimal numberOfTransmissions;
	@Getter @Setter private String submittedTheFile;
	@Getter @Setter private String submittedDate;
	@Getter @Setter private String onTime;
	@Getter @Setter private String fileNumber;
	@Getter @Setter private int totalNumberOfTransmissions;
	@Getter @Setter private int totalNumberOfSubmissions;
	@Getter @Setter private SubmissionStatisticsMetaData submissionStatisticsMetaData;
	@Getter @Setter private List<SubmissionStatisticsReportHelper> resultList;	
	
}

class sortByState implements Comparator<SubmissionStatisticsReportHelper>
{

	public int compare(SubmissionStatisticsReportHelper s1, SubmissionStatisticsReportHelper s2)
	{
		return s1.getStateName().compareTo(s2.getStateName());
	}
}

class sortByNumOfTransmissions implements Comparator<SubmissionStatisticsReportHelper>
{
	public int compare(SubmissionStatisticsReportHelper s1, SubmissionStatisticsReportHelper s2)
	{
		return s1.getNumberOfTransmissions().intValue() - (s2.getNumberOfTransmissions().intValue());
	}
}

class sortBysubmittedTheFile implements Comparator<SubmissionStatisticsReportHelper>
{
	public int compare(SubmissionStatisticsReportHelper s1, SubmissionStatisticsReportHelper s2)
	{
		return s1.getSubmittedTheFile().compareTo(s2.getSubmittedTheFile());
	}
}

class sortBysubmittionDateAndTime implements Comparator<SubmissionStatisticsReportHelper>
{
	public int compare(SubmissionStatisticsReportHelper s1, SubmissionStatisticsReportHelper s2)
	{
		return s1.getSubmittedDate().compareTo(s2.getSubmittedDate());
	}
}

class sortByOnTime implements Comparator<SubmissionStatisticsReportHelper>
{
	public int compare(SubmissionStatisticsReportHelper s1, SubmissionStatisticsReportHelper s2)
	{
		return s1.getOnTime().compareTo(s2.getOnTime());
	}
}

class sortByFileNumber implements Comparator<SubmissionStatisticsReportHelper>
{
	public int compare(SubmissionStatisticsReportHelper s1, SubmissionStatisticsReportHelper s2)
	{
		return Integer.parseInt(s1.getFileNumber()) - (Integer.parseInt(s2.getFileNumber()));
	}
}


