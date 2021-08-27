package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.models.helper.DataTable;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.models.helper.TableFieldBean;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.FillPatternType;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class XLSWriterThread implements Runnable
{
	protected final Logger log = Logger.getLogger(getClass());
	private volatile boolean startOfRecs;
	private volatile boolean endOfRecs;
	private volatile PipedOutputStream out;
	private volatile DataTable dataTable;
	
	public XLSWriterThread(DataTable dataTable, PipedOutputStream out,boolean startOfRecs, boolean endOfRecs)
	{
		this.startOfRecs = startOfRecs;
		this.endOfRecs = endOfRecs;
		this.out = out;
		this.dataTable = dataTable;
	}
	
	@Override
	public void run()
	{
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		
		HSSFRow row = sheet.createRow(0);  // the header row, for now
		
		// Specify the header row style.
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		HSSFFont headerFont = wb.createFont();
		headerFont.setColor(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		
		int fieldCount = dataTable.getFieldCount();
		// Write the fields.
		if(startOfRecs)
		{
			
			Iterator<TableFieldBean> fieldIter = dataTable.getFields().iterator();
			for (int i = 0; i < fieldCount; ++i)
			{
				TableFieldBean field = fieldIter.next();
				String value = field.label;
				if (value != null)
				{
					value = value.trim();
				}
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(value);
			}
		}
		// Write the data.
		int rowCount = dataTable.getRowCount();
		Iterator<TableDatumBean> dataIter = dataTable.iterator();
		for (int i = 1; i < rowCount + 1; ++i)
		{
			row = sheet.createRow(i);
			
			for (int j = 0; j < fieldCount; ++j)
			{
				TableDatumBean datum = dataIter.next();
				String value = datum.value;
				if (value != null)
				{
					value = value.trim();
					try {

						value =  new String(value.getBytes("ISO-8859-1"));
					} catch (UnsupportedEncodingException e) {
						
						e.printStackTrace();
					}
				}
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(value);
			}
		}
		
		try
		{
			wb.write(out);
			out.close();
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
		}

		
	}

	

}
