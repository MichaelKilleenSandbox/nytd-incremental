package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.models.helper.DataTable;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;

public class HTMLWriterThread implements Runnable
{
	@Override
	public void run() {

	}
//	protected final Logger log = Logger.getLogger(getClass());
//	private volatile boolean startOfRecs;
//	private volatile boolean endOfRecs;
//	private volatile PipedOutputStream out;
//	private volatile DataTable dataTable;
//
//	public HTMLWriterThread(DataTable dataTable, PipedOutputStream out,boolean startOfRecs, boolean endOfRecs)
//	{
//		this.startOfRecs = startOfRecs;
//		this.endOfRecs = endOfRecs;
//		this.out = out;
//		this.dataTable = dataTable;
//	}
//
//	@Override
//	public void run()
//	{
//
//		BufferedWriter writer = null;
//		try {
//			writer = new BufferedWriter(new OutputStreamWriter(out,"ISO-8859-1"));
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//
//		final String FIELD_HEAD = "field";
//
//		// To facilitate writing of XHTML
//		class MyXHTMLWriter
//		{
//			BufferedWriter writer;
//
//			MyXHTMLWriter(BufferedWriter writer)
//			{
//				this.writer = writer;
//			}
//
//			void write(String str)
//			{
//				try
//				{
//					writer.write(str);
//				}
//				catch (IOException e)
//				{
//					log.error(e.getMessage(), e);
//				}
//			}
//
//			void newLine()
//			{
//				try
//				{
//					writer.newLine();
//				}
//				catch (IOException e)
//				{
//					log.error(e.getMessage(), e);
//				}
//			}
//
//			void writeStartTR()
//			{
//				write("\t\t\t<tr>");
//				newLine();
//			}
//
//			void writeEndTR()
//			{
//				write("\t\t\t</tr>");
//				newLine();
//			}
//
//			String prepVal(String value)
//			{
//				if (value == null)
//				{
//					value = "";
//				}
//
//				value = value.trim();
//
//				value = value.replaceAll("&", "&amp;");
//				value = value.replaceAll("<", "&lt;");
//				value = value.replaceAll(">", "&gt;");
//				value = value.replaceAll("\"", "&quot;");
//				value = value.replaceAll("'", "&apos;");
//
//				if (value.isEmpty())
//				{
//					value = "&#160;";
//				}
//
//				return value;
//			}
//
//			void writeTH(String value, int fieldNum)
//			{
//				write(String.format("\t\t\t\t<th id='%s%d'>", FIELD_HEAD, fieldNum));
//				write(prepVal(value));
//				write("</th>");
//				newLine();
//			}
//
//			void writeTD(String value, int fieldNum)
//			{
//				write(String.format("\t\t\t\t<td headers='%s%d'>", FIELD_HEAD, fieldNum));
//				write(prepVal(value));
//				write("</td>");
//				newLine();
//			}
//		}
//		MyXHTMLWriter myWriter = new MyXHTMLWriter(writer);
//		int fieldCount = dataTable.getFieldCount();
//		if(startOfRecs)
//		{
//			// CSS
//			final String css =
//			   "\t\t\t" + "table {" + "\n"
//			 + "\t\t\t\t" + "border-collapse: collapse;" + "\n"
//			 + "\t\t\t" + "}" + "\n" + "\n"
//			 + "\t\t\t" + "th, td {" + "\n"
//			 + "\t\t\t\t" + "border: 1px solid #27AAE1;" + "\n"
//			 + "\t\t\t\t" + "padding: 0.5em;" + "\n"
//			 + "\t\t\t\t" + "vertical-align: middle;" + "\n"
//			 + "\t\t\t" + "}" + "\n" + "\n"
//			 + "\t\t\t" + "th {" + "\n"
//			 + "\t\t\t\t" + "background-color: #9ED8FF;" + "\n"
//			 + "\t\t\t\t" + "color: #2D3691;" + "\n"
//			 + "\t\t\t\t" + "font-weight: bold;" + "\n"
//			 + "\t\t\t\t" + "text-align: center;" + "\n"
//			 + "\t\t\t" + "}" + "\n" + "\n"
//			 + "\t\t\t" + "td {" + "\n"
//			 + "\t\t\t\t" + "background-color: #EBF7FE;" + "\n"
//			 + "\t\t\t" + "}";
//
//			// Write the non-data XHTML
//			myWriter.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
//			myWriter.write("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
//			myWriter.newLine();
//			myWriter.write("<html xmlns='http://www.w3.org/1999/xhtml'>");
//			myWriter.newLine();
//			myWriter.write("\t<head>");
//			myWriter.newLine();
//			myWriter.write("\t\t<title>");
//			myWriter.write("NYTD Data");
//			myWriter.write("</title>");
//			myWriter.newLine();
//			myWriter.write("\t\t<style type='text/css'>");
//			myWriter.newLine();
//			myWriter.write(css);
//			myWriter.newLine();
//			myWriter.write("\t\t</style>");
//			myWriter.newLine();
//			myWriter.write("\t</head>");
//			myWriter.newLine();
//			myWriter.write("\t<body>");
//			myWriter.newLine();
//			myWriter.write("\t\t<table>");
//			myWriter.newLine();
//
//			// Write the fields.
//
//			Iterator<TableFieldBean> fieldIter = dataTable.getFields().iterator();
//			myWriter.writeStartTR();
//			for (int i = 0; i < fieldCount; ++i)
//			{
//				TableFieldBean field = fieldIter.next();
//				myWriter.writeTH(field.label, i);
//			}
//			myWriter.writeEndTR();
//		}
//		// Write the data values.
//		int rowCount = dataTable.getRowCount();
//		Iterator<TableDatumBean> dataIter = dataTable.iterator();
//		for (int i = 0; i < rowCount; ++i)
//		{
//			myWriter.writeStartTR();
//			for (int j = 0; j < fieldCount; ++j)
//			{
//				TableDatumBean datum = dataIter.next();
//				myWriter.writeTD(datum.value, j);
//			}
//			myWriter.writeEndTR();
//		}
//
//		// Close the XHTML
//		if(endOfRecs)
//		{
//			myWriter.write("\t\t</table>");
//			myWriter.newLine();
//			myWriter.write("\t</body>");
//			myWriter.newLine();
//			myWriter.write("</html>");
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
//
//	}


}
