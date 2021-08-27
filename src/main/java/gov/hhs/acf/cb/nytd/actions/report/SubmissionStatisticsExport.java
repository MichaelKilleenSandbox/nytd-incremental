package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

public class SubmissionStatisticsExport extends ExportableTable<SubmissionStatisticsReportHelper>
{
	public SubmissionStatisticsExport(ActionSupport action, DataExtractionService dataExtractionService)
	{

		super(action, dataExtractionService);
	}

	@Override
	protected void addColumns()
	{
		addColumn("State", new ValueProvider<SubmissionStatisticsReportHelper>()
		{
			public String getValue(final SubmissionStatisticsReportHelper report)
			{
				return report.getStateName();
			}
		});
		addColumn("# of Transmissions", new ValueProvider<SubmissionStatisticsReportHelper>()
		{
			public String getValue(final SubmissionStatisticsReportHelper report)
			{
				return (report.getNumberOfTransmissions()).toString();
			}
		});
		addColumn("Submitted?", new ValueProvider<SubmissionStatisticsReportHelper>()
		{
			public String getValue(final SubmissionStatisticsReportHelper report)
			{
				return report.getSubmittedTheFile();
			}
		});
		addColumn("Submitted Date/Time", new ValueProvider<SubmissionStatisticsReportHelper>()
		{
			public String getValue(final SubmissionStatisticsReportHelper report)
			{
				return report.getSubmittedDate();
			}
		});
		addColumn("File Number", new ValueProvider<SubmissionStatisticsReportHelper>()
		{
			public String getValue(final SubmissionStatisticsReportHelper report)
			{
				return report.getOnTime();
			}
		});
	}
}
