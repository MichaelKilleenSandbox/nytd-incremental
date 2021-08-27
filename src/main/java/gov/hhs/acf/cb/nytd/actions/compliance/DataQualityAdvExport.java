package gov.hhs.acf.cb.nytd.actions.compliance;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.DataQualityAdvAggregate;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 4, 2010
 */
public class DataQualityAdvExport extends ExportableTable<DataQualityAdvAggregate>
{
	public DataQualityAdvExport(ActionSupport action, DataExtractionService dataExtractionService)
	{
		super(action, dataExtractionService);
	}

	protected void addColumns()
	{
		addColumn("Element Number/Name", new ValueProvider<DataQualityAdvAggregate>()
		{
			public String getValue(final DataQualityAdvAggregate dataQualityAggregate)
			{
				return dataQualityAggregate.getDataQualityAdvStandard().getAllowedValue().getElement().getName()
						+" - "+dataQualityAggregate.getDataQualityAdvStandard().getAllowedValue().getElement()
						.getDescription();
			}
		});
/*		addColumn("Element Name", new ExportableTable.ValueProvider<DataQualityAdvAggregate>()
		{
			public String getValue(final DataQualityAdvAggregate dataQualityAggregate)
			{
				return dataQualityAggregate.getDataQualityAdvStandard().getAllowedValue().getElement()
						.getDescription();
			}
		});*/
		addColumn("Value (Subject Population)", new ValueProvider<DataQualityAdvAggregate>()
		{
			public String getValue(final DataQualityAdvAggregate dataQualityAggregate)
			{
				Map<String, Object> namedParams = new HashMap<String, Object>();
				namedParams.put("datumValue", dataQualityAggregate.getDatumValue());
				return dataQualityAggregate.formatText(dataQualityAggregate.getDataQualityAdvStandard()
						.getDescription(), namedParams);
			}
		});
		addColumn("Value Rate", new ValueProvider<DataQualityAdvAggregate>()
		{
			public String getValue(final DataQualityAdvAggregate dataQualityAggregate)
			{
				return dataQualityAggregate.getPercentValue().toString();
			}
		});
		addColumn("Advisory Threshold", new ValueProvider<DataQualityAdvAggregate>()
		{
			public String getValue(final DataQualityAdvAggregate dataQualityAggregate)
			{
				return dataQualityAggregate.getDataQualityAdvStandard().getDataQualityAdvStandard().toString();
			}
		});
	}
}
