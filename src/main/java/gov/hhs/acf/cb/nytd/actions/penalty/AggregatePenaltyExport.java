package gov.hhs.acf.cb.nytd.actions.penalty;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.VwTransmissionStatus;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;

import java.math.BigDecimal;


/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 7, 2010
 */
public class AggregatePenaltyExport extends ExportableTable<VwTransmissionStatus>
{
	public AggregatePenaltyExport(ActionSupport action, DataExtractionService dataExtractionService,
			SiteUser user)
	{

		super(action, dataExtractionService, user);
	}

	@Override
	protected void addColumns()
	{
		final UserRoleEnum role = UserRoleEnum.getRole(getUser().getPrimaryUserRole().getName());

		if (role == UserRoleEnum.ADMIN || role == UserRoleEnum.FEDERAL || role == UserRoleEnum.REGIONAL)
		{
			addColumn("State", new ValueProvider<VwTransmissionStatus>()
			{
				public String getValue(VwTransmissionStatus status)
				{
					return status.getState();
				}
			});
			//Commenting out the below as per bug no. 15282
			/*addColumn("File Processed Date", new ValueProvider<VwTransmissionStatus>()
			{
				public String getValue(VwTransmissionStatus status)
				{
					GregorianCalendar cal = (GregorianCalendar) status.getFileReceivedDate();
					return DateUtil.formatDateAndTimezone(DateFormat.LONG, cal);
				}
			});*/
		}
		addColumn("File Number", new ValueProvider<VwTransmissionStatus>()
				{
					public String getValue(VwTransmissionStatus status)
					{
						return status.getTransmissionId().toString();
					}
				});
		// Nilima: end of bug no. 13691 fix step no. 2
		addColumn("Reporting Period", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				return status.getReportingPeriod();
			}
		});
		addColumn("Compliance Status", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				return status.getComplianceStatus();
			}
		});
		addColumn("Compliance On Time", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getTimelyErrCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? "No" : "Yes";
			}
		});
		addColumn("Compliance Correct Format", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getFormatErrCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? "No" : "Yes";
			}
		});
		addColumn("Compliance Error Free", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal count = status.getDatValueCompliantCnt();
				return (count == null) ? "" : (count.longValue() > 0) ? "No" : "Yes";
			}
		});
		addColumn("System-generated Potential Penalty", new ValueProvider<VwTransmissionStatus>()
		{
			public String getValue(VwTransmissionStatus status)
			{
				BigDecimal penalty = status.getPotentialPenalty();
				return (penalty == null) ? "" : penalty.toString() + "%";
			}
		});
	}
}
