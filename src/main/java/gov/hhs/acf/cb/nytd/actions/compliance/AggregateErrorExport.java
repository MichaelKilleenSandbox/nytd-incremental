package gov.hhs.acf.cb.nytd.actions.compliance;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.DataAggregate;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 4, 2010
 */
public class AggregateErrorExport extends ExportableTable<DataAggregate>
{
	public AggregateErrorExport(ActionSupport action, DataExtractionService dataExtractionService)
	{

		super(action, dataExtractionService);
	}

	protected void addColumns()
	{
		addColumn("Element Number/Name", new ValueProvider<DataAggregate>()
		{
			public String getValue(final DataAggregate dataAggregate)
			{
				return dataAggregate.getElement().getName() +" - "+dataAggregate.getElement().getDescription();
			}
		});

/*		addColumn("Element Name", new ExportableTable.ValueProvider<DataAggregate>()
		{
			public String getValue(final DataAggregate dataAggregate)
			{
				return dataAggregate.getElement().getDescription();
			}
		});*/

		addColumn("Compliance Status", new ValueProvider<DataAggregate>()
		{
			public String getValue(final DataAggregate dataAggregate)
			{
				return dataAggregate.getComplianceStatus();
			}
		});

		addColumn("Regulated Compliance Standard", new ValueProvider<DataAggregate>()
		{
			public String getValue(final DataAggregate dataAggregate)
			{
				String value = dataAggregate.getElement().getComplianceStandard().getComplianceStandard()
						.toString();
				return value;
			}
		});

		addColumn("% Error Free", new ValueProvider<DataAggregate>()
		{
			public String getValue(final DataAggregate dataAggregate)
			{
				return dataAggregate.getPercentValue().toString();
			}
		});
	}
}
