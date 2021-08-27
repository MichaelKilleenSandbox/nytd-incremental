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
public class TransmissionErrorExport extends ExportableTable<NytdError>
{
	public TransmissionErrorExport(ActionSupport action, DataExtractionService dataExtractionService)
	{
		super(action, dataExtractionService);
	}

	@Override
	protected void addColumns()
	{
		addColumn("Category", new ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				String category = error.getComplianceCategory().getName();
				return category;
			}
		});

		addColumn("Description", new ValueProvider<NytdError>()
		{
			public String getValue(final NytdError error)
			{
				return error.formatErrorMessage();
			}
		});
	}

}
