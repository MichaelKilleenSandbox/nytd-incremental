package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

public class ComplianceStandardsExport extends ExportableTable<ComplianceStandardReportHelper>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplianceStandardsExport(ActionSupport action, DataExtractionService dataExtractionService)
	{

		super(action, dataExtractionService);
	}

	
	@Override
	protected void addColumns()
	{
		
		addColumn("State", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getStateName();
					}
				});
		addColumn("Report Period", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getReportingPeriod();
					}
				});
		addColumn("File Number", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getFileNumber();
					}
				});
		addColumn("FSS Error free", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getFileSubmissionErrorFree();
					}
				});
		addColumn("Timely data", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getTimelyData();
					}
				});
		addColumn("File format", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getFileFormat();
					}
				});
		addColumn("DS Error free", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getDataStandardErrorFree();
					}
				});
		addColumn("Outcomes Universe", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getOutcomeUniverse();
					}
				});
		addColumn("FCY-Outcomes Participation", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getParticipationInCare();
					}
		});
		addColumn("DY-Outcomes Participation", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getParticipationDischarged();
					}
		});
		addColumn("Enforced Penalty", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getEnforcedPenalty();
					}
		});
		addColumn("Reason", new ValueProvider<ComplianceStandardReportHelper>()
				{
					public String getValue(final ComplianceStandardReportHelper report)
					{
						return report.getEnforcedPenaltyReason();
					}
		});
	}


}
