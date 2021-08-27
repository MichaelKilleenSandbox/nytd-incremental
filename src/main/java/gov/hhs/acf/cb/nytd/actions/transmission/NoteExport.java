package gov.hhs.acf.cb.nytd.actions.transmission;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.ElementNote;
import gov.hhs.acf.cb.nytd.models.TransmissionNote;
import gov.hhs.acf.cb.nytd.models.helper.VwNote;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 9, 2010
 */
public class NoteExport
{
	public static ExportableTable<VwNote> forDatum(ActionSupport action, DataExtractionService exportService)
	{
		return new ExportableTable<VwNote>(action, exportService)
		{
			@Override
			protected void addColumns()
			{
				addColumn("Record Number", new ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getRecordNumber();
					}
				});
				addColumn("Element Number/Name", new ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getElementName()+" - "+note.getElementDescription();
					}
				});
/*				addColumn("Element Name", new ExportableTable.ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getElementDescription();
					}
				});
*/				addColumn("Datum Value", new ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getDatumValue();
					}
				});
				addColumn("Notes", new ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getNoteText();
					}
				});
			}
		};
	}

	public static ExportableTable<ElementNote> forElement(ActionSupport action,
			DataExtractionService exportService)
	{
		return new ExportableTable<ElementNote>(action, exportService)
		{
			@Override
			protected void addColumns()
			{
				addColumn("Element Number", new ValueProvider<ElementNote>()
				{
					public String getValue(final ElementNote note)
					{
						return note.getElement().getName();
					}
				});
				addColumn("Element Name", new ValueProvider<ElementNote>()
				{
					public String getValue(final ElementNote note)
					{
						return note.getElement().getDescription();
					}
				});
				addColumn("Notes", new ValueProvider<ElementNote>()
				{
					public String getValue(final ElementNote note)
					{
						return note.getNote();
					}
				});
			}
		};
	}

	public static ExportableTable<VwNote> forRecord(ActionSupport action, DataExtractionService exportService)
	{
		return new ExportableTable<VwNote>(action, exportService)
		{
			@Override
			protected void addColumns()
			{
				addColumn("Record Number", new ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getRecordNumber();
					}
				});
				addColumn("Notes", new ValueProvider<VwNote>()
				{
					public String getValue(final VwNote note)
					{
						return note.getNoteText();
					}
				});
			}
		};
	}

	public static ExportableTable<TransmissionNote> forTransmission(ActionSupport action,
			DataExtractionService exportService)
	{
		return new ExportableTable<TransmissionNote>(action, exportService)
		{
			@Override
			protected void addColumns()
			{
				addColumn("Notes", new ValueProvider<TransmissionNote>()
				{
					public String getValue(final TransmissionNote note)
					{
						return note.getNote();
					}
				});
			}
		};
	}
}
