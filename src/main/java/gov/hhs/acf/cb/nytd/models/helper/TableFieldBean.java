/**
 * Filename: TableFieldBean.java
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

import com.pmstation.spss.MissingValue;

import java.io.Serializable;

/**
 * An individual field/column of the DataTable.
 * 
 * A column will usually represent the data of a NYTD element
 * or the notes related to the data of a NYTD element.
 * 
 * Attributes are included that facilitate export of the DataTable
 * into the SPSS data format.
 * 
 * @author Adam Russell (18816)
 */
public class TableFieldBean implements Serializable
{
	public Integer sortKey;
	public String name;
	public String label;
	public int columns;
	public int align;
	public int measure;
	public int length;
	public int width;
	public int decimals;
	public MissingValue mv;
	
	public TableFieldBean()
	{
		super();
	}
	
	/**
	 * Constructs a column that contains string data.
	 * 
	 * @param sortKey an integer that specifies this field's sort priority
	 * @param name a short name for the column
	 * @param length maximum length of the data in the column
	 * @param label a longer, formatted name for the column
	 */
	public TableFieldBean(Integer sortKey, String name, int length, String label)
	{
		this.sortKey = sortKey;
		this.name = name;
		this.length = length;
		this.label = label;
	}
	
	/**
	 * Constructs a column that contains string data.
	 * 
	 * @param sortKey an integer that specifies this field's sort priority
	 * @param name a short name for the column
	 * @param length maximum length of the data in the column
	 * @param label a longer, formatted name for the column
	 * @param columns number of characters of the data to display
	 * @param align horizontal alignment of the column
	 * @param measure the measure of the column
	 */
	public TableFieldBean(Integer sortKey, String name, int length, String label, int columns, int align, int measure)
	{
		this.sortKey = sortKey;
		this.name = name;
		this.length = length;
		this.label = label;
		this.columns = columns;
		this.align = align;
		this.measure = measure;
	}
	
	/**
	 * Constructs a column that contains numeric data.
	 * 
	 * @param sortKey an integer that specifies this field's sort priority
	 * @param name a short name for the column
	 * @param width number of digits before the point in the column's data
	 * @param decimals number of digits after the point in the column's data
	 * @param label a longer, formatted name for the column
	 */
	public TableFieldBean(Integer sortKey, String name, int width, int decimals, String label)
	{
		this.sortKey = sortKey;
		this.name = name;
		this.width = width;
		this.decimals = decimals;
		this.label = label;
	}
	
	/**
	 * Constructs a column that contains numeric data.
	 * 
	 * @param sortKey an integer that specifies this field's sort priority
	 * @param name a short name for the column
	 * @param width number of digits before the point in the column's data
	 * @param decimals number of digits after the point in the column's data
	 * @param label a longer, formatted name for the column
	 * @param columns number of characters of the data to display
	 * @param align horizontal alignment of the column
	 * @param measure the measure of the column
	 */
	public TableFieldBean(Integer sortKey, String name, int width, int decimals, String label,
			int columns, int align, int measure)
	{
		this.sortKey = sortKey;
		this.name = name;
		this.width = width;
		this.decimals = decimals;
		this.label = label;
		this.columns = columns;
		this.align = align;
		this.measure = measure;
	}
	
	/**
	 * Constructs a column that contains numeric data.
	 * 
	 * @param sortKey an integer that specifies this field's sort priority
	 * @param name a short name for the column
	 * @param width number of digits before the point in the column's data
	 * @param decimals number of digits after the point in the column's data
	 * @param label a longer, formatted name for the column
	 * @param mv missing values
	 */
	public TableFieldBean(Integer sortKey, String name, int width, int decimals, String label, MissingValue mv)
	{
		this.sortKey = sortKey;
		this.name = name;
		this.width = width;
		this.decimals = decimals;
		this.label = label;
		this.mv = mv;
	}

	/**
	 * @return the sortKey
	 */
	public Integer getSortKey()
	{
		return sortKey;
	}

	/**
	 * @param sortKey the sortKey to set
	 */
	public void setSortKey(Integer sortKey)
	{
		this.sortKey = sortKey;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return the columns
	 */
	public int getColumns()
	{
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(int columns)
	{
		this.columns = columns;
	}

	/**
	 * @return the align
	 */
	public int getAlign()
	{
		return align;
	}

	/**
	 * @param align the align to set
	 */
	public void setAlign(int align)
	{
		this.align = align;
	}

	/**
	 * @return the measure
	 */
	public int getMeasure()
	{
		return measure;
	}

	/**
	 * @param measure the measure to set
	 */
	public void setMeasure(int measure)
	{
		this.measure = measure;
	}

	/**
	 * @return the length
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * @return the decimals
	 */
	public int getDecimals()
	{
		return decimals;
	}

	/**
	 * @param decimals the decimals to set
	 */
	public void setDecimals(int decimals)
	{
		this.decimals = decimals;
	}

	/**
	 * @return the mv
	 */
	public Object getMv()
	{
		return mv;
	}

	/**
	 * @param mv the mv to set
	 */
	public void setMv(MissingValue mv)
	{
		this.mv = mv;
	}
}
