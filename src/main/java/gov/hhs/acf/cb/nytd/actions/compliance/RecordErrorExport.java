package gov.hhs.acf.cb.nytd.actions.compliance;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.NytdError;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 4, 2010
 */
public class RecordErrorExport extends ExportableTable<NytdError>
{
	public RecordErrorExport(ActionSupport action, DataExtractionService dataExtractionService)
	{

		super(action, dataExtractionService);
	}

	@Override
	protected void addColumns()
	{
		addColumn("Record Number", new ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				return error.getNonCompliance().getDatum().getTransmissionRecord().getRecordNumber();
			}
		});
		addColumn("Element Number/Name", new ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				return error.getNonCompliance().getDatum().getElement().getName()
						+" - "+error.getNonCompliance().getDatum().getElement().getDescription();
			}
		});
/*		addColumn("Element Name", new ExportableTable.ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				return error.getNonCompliance().getDatum().getElement().getDescription();
			}
		});*/
		addColumn("Compliance Type", new ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				return error.getComplianceCategory().getName();
			}
		});
		addColumn("Error Description", new ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				return error.formatErrorMessage();
			}
		});
	}
}
