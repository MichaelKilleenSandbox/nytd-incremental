package gov.hhs.acf.cb.nytd.actions.transmission;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.VwTransmissionStatus;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;

import java.math.BigDecimal;
import java.text.DateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 5, 2010
 */
public class TransmissionExport extends ExportableTable<VwTransmissionStatus>
{
	public TransmissionExport(ActionSupport action, DataExtractionService dataExtractionService, SiteUser user)
	{

		super(action, dataExtractionService, user);
	}

	protected void addColumns()
	{
		final UserRoleEnum role = UserRoleEnum.getRole(getUser().getPrimaryUserRole().getName());

		if (role == UserRoleEnum.STATE)
		{
			// submission status is the first column for state users
			addColumn("Submission Status", new ValueProvider<VwTransmissionStatus>()
			{
				public String getValue(VwTransmissionStatus status)
				{
					return status.getSubmissionStatus();
				}
			});
		}
		else
		{
			// state is the first column for regional and federal users
			addColumn("State", new ValueProvider<VwTransmissionStatus>()
			{
				public String getValue(final VwTransmissionStatus status)
				{
					return status.getState();
				}
			});
		}
		addColumn(role == UserRoleEnum.STATE ? "Transmission Date & Time" : "Submission Date & Time",
				new ValueProvider<VwTransmissionStatus>()
				{
					public String getValue(VwTransmissionStatus status)
					{
						if (role == UserRoleEnum.STATE)
						{
							return status.getFileReceivedDate() == null ? "" : DateUtil.formatDateAndTimezone(
									DateFormat.LONG, status.getFileReceivedDate());
						}
						else
						{
							return status.getSubmittedDate() == null ? "" : DateUtil.formatDateAndTimezone(
									DateFormat.LONG, status.getSubmittedDate());
						}
					}
				});
		if (!(role == UserRoleEnum.STATE))
		{
			// submission status is the third column for regional and federal users
			addColumn("Submission Status", new ValueProvider<VwTransmissionStatus>()
			{
				public String getValue(VwTransmissionStatus status)
				{
					return status.getSubmissionStatus();
				}
			});
		}
		addColumn("File Number", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				return status.getTransmissionId().toString();
			}
		});
		addColumn("Report Period", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				return status.getReportingPeriod();
			}
		});
		addColumn("File Type", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				return status.getTransmissionType();
			}
		});
		addColumn("Records in File", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal numberOfRecordsInFile = status.getNumberOfRecordsInFile();
				return numberOfRecordsInFile == null ? "" : numberOfRecordsInFile.toString();
			}
		});
		addColumn("Compliance Status", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				return status.getComplianceStatus();
			}
		});
		addColumn("Compliance Details: Timely?", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getTimelyErrCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? "No" : "Yes";
			}
		});
		addColumn("Compliance Details: Correct Format?", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getFormatErrCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? "No" : "Yes";
			}
		});
		addColumn("Compliance Details: Error Free?", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getDatValueCompliantCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? "No" : "Yes";
			}
		});
		addColumn("# of Data Quality Advisories", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getDatValueDataQualtyAdvisCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? count.toString() : "None";
			}
		});
		addColumn("System-generated Potential Penalty", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal penalty = status.getPotentialPenalty();
				return (penalty == null) ? "" : penalty.toString();
			}
		});
	}
}
