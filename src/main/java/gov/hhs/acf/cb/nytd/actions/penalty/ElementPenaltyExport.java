package gov.hhs.acf.cb.nytd.actions.penalty;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.VwElementPenalty;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;

import java.math.BigDecimal;


/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 7, 2010
 */
public class ElementPenaltyExport extends ExportableTable<VwElementPenalty>
{
	public ElementPenaltyExport(ActionSupport action, DataExtractionService dataExtractionService,
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
			addColumn("State", new ValueProvider<VwElementPenalty>()
			{
				public String getValue(VwElementPenalty penalty)
				{
					return (penalty == null) ? "" : penalty.getId().getState();
				}
			});
		}
		addColumn("File Number", new ValueProvider<VwElementPenalty>()
				{
					public String getValue(VwElementPenalty penalty)
					{
						return (penalty == null) ? "" : penalty.getId().getTransmissionId().toString();
					}
				});
		addColumn("Reporting Period", new ValueProvider<VwElementPenalty>()
		{
			public String getValue(VwElementPenalty penalty)
			{
				return (penalty == null) ? "" : penalty.getId().getReportingPeriod();
			}
		});
		addColumn("Element Number", new ValueProvider<VwElementPenalty>()
		{
			public String getValue(VwElementPenalty penalty)
			{
				return (penalty == null) ? "" : penalty.getId().getElementNumber().toString();
			}
		});
		addColumn("Element Name", new ValueProvider<VwElementPenalty>()
		{
			public String getValue(VwElementPenalty penalty)
			{
				return (penalty == null) ? "" : penalty.getId().getElement();
			}
		});
		addColumn("% Error Free", new ValueProvider<VwElementPenalty>()
		{
			public String getValue(VwElementPenalty penalty)
			{
				BigDecimal value = (penalty == null) ? null : penalty.getId().getPercentValue();
				return (value == null) ? "" : (value.toString() + "%");
			}
		});
		addColumn("System-generated Potential Penalty", new ValueProvider<VwElementPenalty>()
		{
			public String getValue(VwElementPenalty penalty)
			{
				BigDecimal value = (penalty == null) ? null : penalty.getId().getPotentialPenalty();
				return (value == null) ? "" : value.toString() + "%";
			}
		});
	}
}
