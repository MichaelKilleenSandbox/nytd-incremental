package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.ErrorTypeCount;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 4, 2010
 */
public class ErrorTypeCountsExport extends ExportableTable<ErrorTypeCount>
{
	boolean isDQAExport;
	public ErrorTypeCountsExport(ActionSupport action, DataExtractionService dataExtractionService,boolean isDQAExport)
	{
		super(action, dataExtractionService);
		this.isDQAExport = isDQAExport;
	}

	@Override
	protected void addColumns()
	{
		addColumn("Reporting Period", new ValueProvider<ErrorTypeCount>()
		{
			public String getValue(final ErrorTypeCount error)
			{
				String reportingPeriod = error.getREPORTINGPERIOD();
				return reportingPeriod;
			}
		});
		addColumn("State", new ValueProvider<ErrorTypeCount>()
				{
					public String getValue(final ErrorTypeCount error)
					{
						String state = error.getSTATENAME();
						return state;
					}
				});
		addColumn("Element Number/Name", new ValueProvider<ErrorTypeCount>()
				{
					public String getValue(final ErrorTypeCount error)
					{
						String elementNumber = null;
						String elementName = null;
						if(error!=null && error.getELEMENTID()!= null)
							elementNumber = error.getELEMENTID().toString();
						if(error!=null && error.getELEMENTNAME()!= null)
							elementName = error.getELEMENTNAME();
						return elementNumber +" - "+ elementName;
					}
				});
		/*addColumn("Element Name", new ExportableTable.ValueProvider<ErrorTypeCount>()
				{
					public String getValue(final ErrorTypeCount error)
					{
						String elementName = null;
						if(error!=null && error.getELEMENTNAME()!= null)
							elementName = error.getELEMENTNAME();
						return elementName;
					}
				});*/
		
		StringBuffer descriptionCOlumnName = null;
		if(isDQAExport)
		{
			descriptionCOlumnName = new StringBuffer("DQA");
		}
		else
			{
				descriptionCOlumnName = new StringBuffer("Non-Compliance");
			}
		addColumn(descriptionCOlumnName.toString(), new ValueProvider<ErrorTypeCount>()
		{
			public String getValue(final ErrorTypeCount error)
			{
				return error.formatErrorMessage();
			}
		});
		addColumn("Count", new ValueProvider<ErrorTypeCount>()
				{
					public String getValue(final ErrorTypeCount error)
					{
						String count = error.getERRORCOUNT().toString();
						return count;
					}
				});
	}

}
