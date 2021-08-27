/**
 * Filename: TableDatumBean.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Aug 27, 2009
 *  Author: 18816
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models.helper;

import java.io.Serializable;


/**
 * An individual datum to be stored in the DataTable.
 * 
 * Unlike the current Datum class, this bean doesn't contain both the value
 * and the note.  It will contain one or the other, just as it would be
 * represented as a cell in a spreadsheet-style table.
 * 
 * The attributes of this bean are chosen to expedite the export of
 * the DataTable into a JasperReport cross-tabulation.
 * 
 * @author Adam Russell (18816)
 */
public class TableDatumBean implements Serializable
{
	/**
	 * the name of the column
	 */
	public String column;
	
	/**
	 * row number, to determine order
	 */
	public Integer rowNumber;
	
	/**
	 * value of the cell/datum
	 */
	public String value;
	
	public TableDatumBean()
	{
		super();
	}
	
	/**
	 * @param column the column to set
	 * @param rowNumber the rowNumber to set
	 * @param value the value to set
	 */
	public TableDatumBean(String column, Integer rowNumber, String value)
	{
		super();
		this.column = column;
		this.rowNumber = rowNumber;
		this.value = value;
	}

	/**
	 * @return the column
	 */
	public String getColumn()
	{
		return column;
	}
	
	/**
	 * @param column the column to set
	 */
	public void setColumn(String column)
	{
		this.column = column;
	}
	
	/**
	 * @return the rowNumber
	 */
	public Integer getRowNumber()
	{
		return rowNumber;
	}
	
	/**
	 * @param rowNumber the rowNumber to set
	 */
	public void setRowNumber(Integer rowNumber)
	{
		this.rowNumber = rowNumber;
	}
	
	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
}
