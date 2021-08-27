package gov.hhs.acf.cb.nytd.actions.compliance;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.RecordLevelAdvisory;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: 23839
 * Date: Jan 20, 2011
 */
public class RecordLevelDQAExport extends ExportableTable<RecordLevelAdvisory>
{
	public RecordLevelDQAExport(ActionSupport action, DataExtractionService dataExtractionService)
	{
		super(action, dataExtractionService);
	}

	protected void addColumns()
	{
		addColumn("Record Number", new ValueProvider<RecordLevelAdvisory>()
				{
					public String getValue(final RecordLevelAdvisory recordLevelAdvisory)
					{
						return recordLevelAdvisory.getDatum().getTransmissionRecord().getRecordNumber();
					}
				});
		
		addColumn("Element Number/Name", new ValueProvider<RecordLevelAdvisory>()
		{
			public String getValue(final RecordLevelAdvisory recordLevelAdvisory)
			{
				return recordLevelAdvisory.getDatum().getElement().getName() +" - "+recordLevelAdvisory.getDatum().getElement().getDescription() ;
			}
		});
/*		addColumn("Element Name", new ExportableTable.ValueProvider<RecordLevelAdvisory>()
		{
			public String getValue(final RecordLevelAdvisory recordLevelAdvisory)
			{
				return recordLevelAdvisory.getDatum().getElement().getDescription();
			}
		});
*/		
		addColumn("Error Description", new ValueProvider<RecordLevelAdvisory>()
				{
					public String getValue(final RecordLevelAdvisory recordLevelAdvisory)
					{
						Map<String, Object> namedParams = new HashMap<String, Object>();
							namedParams.put("elementNumber", recordLevelAdvisory.getDatum().getElement().getName());
						return 	recordLevelAdvisory.formatText(recordLevelAdvisory.getProblemDescription().getName(),namedParams);
					}
				});
		
	}
}
