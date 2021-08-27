package gov.hhs.acf.cb.nytd.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.DataTable;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provide a wrapper class around DataTable to simplify exporting views and
 * tables from web pages.
 * <p/>
 * User: 15670
 * Date: Jan 7, 2010
 * Time: 9:00:32 AM
 */
public abstract class ExportableTable<T> extends DataTable
{
	private ActionSupport action;
	private List<ColumnHandler<T>> columnHandlers = new ArrayList<ColumnHandler<T>>();
	private List<String> disclaimers;
	private List<String> metaData;
	private SiteUser user;
	private int sortKey = 0;
	private DataExtractionService dataExtractionService;

	protected ExportableTable()
	{
		super();
	}

	public ExportableTable(ActionSupport action, DataExtractionService exportService)
	{
		super();
		this.dataExtractionService = exportService;
		this.action = action;
	}

	public ExportableTable(ActionSupport action, DataExtractionService exportService, SiteUser user)
	{
		this(action, exportService);
		this.user = user;
	}

	/**
	 * Subclasses must implement this simply by repeatedly calling
	 * "addColumn(...)"
	 */
	protected abstract void addColumns();

	/**
	 * The intent is this is called once for each column from the subclass's
	 * addColumns() method
	 * 
	 * @param label
	 * @param valueProvider
	 */
	protected void addColumn(String label, ValueProvider<T> valueProvider)
	{
		columnHandlers.add(new ColumnHandler<T>(this, label, valueProvider));
	}

	public void setDataExtractionService(DataExtractionService dataExtractionService)
	{
		this.dataExtractionService = dataExtractionService;
	}

	public DataExtractionService getDataExtractionService()
	{
		return dataExtractionService;
	}

	public ActionSupport getAction()
	{
		return action;
	}

	public void setAction(ActionSupport action)
	{
		this.action = action;
	}

	public List<String> getDisclaimers()
	{
		return disclaimers;
	}

	public void setDisclaimers(List<String> disclaimers)
	{
		this.disclaimers = disclaimers;
	}

	public SiteUser getUser()
	{
		return user;
	}

	public void setUser(SiteUser user)
	{
		this.user = user;
	}
	public List<String> getMetaData() {
		return metaData;
	}

	public void setMetaData(List<String> metaData) {
		this.metaData = metaData;
	}

	public String export(HttpServletResponse response, String exportFileName)
	{
		try
		{
			int rowCount = this.getRowCount();
			// write CSV contents to output stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(getDataExtractionService()
					.getCSVFile(this,true,true),"ISO-8859-1"));
			response.setHeader("Content-Disposition", "attachment;filename=" + exportFileName);
			ServletOutputStream out = response.getOutputStream();
			
			
			String line = null;
			
			if(rowCount > 65536)				
         {				
         	line = "\"" + "Warning: Your table has more than 65536 rows. You might loose data in Microsoft Excel 2003 or earlier versions." + "\"";
         	out.println(line);
         }   

			while ((line = reader.readLine()) != null)
			{
				out.println(line);
			}
		
            if(metaData != null)
            {
				for (String metaDatum : metaData)
				{
					out.println( metaDatum  );
				}
            }
			
			// append any disclaimers to output stream
			if (disclaimers != null)
			{				
				out.println("Disclaimer(s):");
				for (String disclaimer : disclaimers)
				{
					out.println("\"" + disclaimer + "\"");
				}
			}

			out.flush();
			out.close();

			// do nothing since the browser should allow user to download file
			// specified as attachment
			return null;
		}
		catch (IOException e)
		{
			action.addActionError("There was an error while exporting: " + e.getMessage());
			return Action.INPUT;
		}
	}

	public String export(HttpServletResponse response, Collection<T> exportData, String exportFileName)
	{
		// prepare the data for export
		init(exportData);
		return export(response, exportFileName);
	}

	protected void init(final Collection<T> collection)
	{
		addColumns();
		int row = 0;
		for (T t : collection)
		{
			for (ColumnHandler<T> columnHandler : columnHandlers)
			{
				columnHandler.add(row, t);
			}
			row += 1;
		}
	}

	protected interface ValueProvider<T>
	{
		String getValue(final T t);
	}

	private class ColumnHandler<T>
	{
		protected ExportableTable<T> dataTable;
		protected String label;
		protected ValueProvider<T> valueProvider;

		public ColumnHandler(ExportableTable<T> dataTable, String label, ValueProvider<T> valueProvider)
		{
			this.dataTable = dataTable;
			this.label = label;
			this.valueProvider = valueProvider;
			dataTable.addField(sortKey++, label, label);
		}

		public void add(int row, T t)
		{
			dataTable.add(new TableDatumBean(label, row, valueProvider.getValue(t)));
		}
	}
}