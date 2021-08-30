package gov.hhs.acf.cb.nytd.service.impl;

//import gov.hhs.acf.cb.nytd.models.helper.DataTable;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;

public class CVSWriterThread implements Runnable
{
//	protected final Logger log = Logger.getLogger(getClass());
//	private volatile boolean startOfRecs;
//	private volatile boolean endOfRecs;
//	private volatile PipedOutputStream out;
//	private volatile DataTable dataTable;
	
//	public CVSWriterThread(DataTable dataTable, PipedOutputStream out,boolean startOfRecs, boolean endOfRecs)
//	{
//		this.startOfRecs = startOfRecs;
//		this.endOfRecs = endOfRecs;
//		this.out = out;
//		this.dataTable = dataTable;
//	}

	@Override
	public void run() {

	}

//	@Override
//	public void run()
//	{
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,Charset.forName("ISO8859_1")));
//		int rowCount = 0, fieldCount = 0;
//		int i, j;
//		String value;
//
//		// For converting the values to be suitable for CSV.
//		class MyCSVWriter
//		{
//			void write(String value, Boolean isLast, Writer writer)
//			{
//				if (value == null)
//				{
//					value = "";
//				}
//				else
//				{
//					value = value.replace("\"", "\"\"");
//					if (value.contains(",") ||
//					    value.contains("\r") ||
//					    value.contains("\n") ||
//					    value.startsWith(" ") ||
//					    value.startsWith("\t") ||
//					    value.startsWith("\u000B") ||
//					    value.startsWith("\f") ||
//					    value.endsWith(" ") ||
//					    value.endsWith("\t") ||
//					    value.endsWith("\u000B") ||
//					    value.endsWith("\f") ||
//					    value.isEmpty())
//					{
//						value = "\"" + value + "\"";
//					}
//				}
//				if (!isLast.booleanValue())
//				{
//					value += ",";
//				}
//				try
//				{
//					writer.write(value);
//				}
//				catch (IOException e)
//				{
//					log.error(e.getMessage(), e);
//				}
//			}
//		}
//		MyCSVWriter myWriter = new MyCSVWriter();
//
//		// Write the fields.
//		fieldCount = dataTable.getFields().size();
//		if(startOfRecs)
//		{
//
//			i = 0;
//			for (TableFieldBean field : dataTable.getFields())
//			{
//				Boolean isLast = Boolean.valueOf(false);
//				if (i == (fieldCount - 1))
//				{
//					isLast = Boolean.valueOf(true);
//				}
//
//				myWriter.write(field.label, isLast, writer);
//
//				++i;
//			}
//			try
//			{
//				writer.newLine();
//			}
//			catch (IOException e)
//			{
//				log.error(e.getMessage(), e);
//			}
//			}
//
//			// Write the data values.
//			rowCount = dataTable.getRowCount();
//			for (i = 0; i < rowCount; ++i)
//			{
//				Iterable<TableDatumBean> row = dataTable.getRow(i);
//
//				j = 0;
//				for (TableDatumBean datum : row)
//				{
//					value = datum.value;
//					if (value != null)
//					{
//						value = value.trim();
//					}
//					myWriter.write(value, Boolean.valueOf(j == (fieldCount - 1)), writer);
//					++j;
//				}
//
//				try
//				{
//					writer.newLine();
//				}
//				catch (IOException e)
//				{
//					log.error(e.getMessage(), e);
//			}
//		}
//
//		// Clean up the writer.
//		if (writer != null)
//		{
//			try
//			{
//				writer.flush();
//				writer.close();
//			}
//			catch (IOException e)
//			{
//				log.error(e.getMessage(), e);
//			}
//		}
//	}
//

	

}
