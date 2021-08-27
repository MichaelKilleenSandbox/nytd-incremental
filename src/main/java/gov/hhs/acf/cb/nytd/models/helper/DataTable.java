/**
 * Filename: DataTable.java
 * 
 * Copyright 2009, ICF International Created: Aug 20, 2009 Author: 18816
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.util.Constants;

import java.io.Serializable;
import java.util.*;


/**
 * Representation of data ready to be exported.
 * 
 * The DataTable can be thought of as having rows and columns, where
 * the rows represent individual records in the database and the columns
 * represent the fields/elements of the record.
 * 
 * Note that the DataTable is not a list of lists, but implements a single list
 * of data (TableDatumBean). The second list, the list of fields (TableFieldBean),
 * can be accessed via the getFields() method.
 * 
 * This class was designed with both the pmStation SPSS Writer and
 * JasperReports cross-tabulations in mind.
 * 
 * In order to use the object:
 * (1) initialize it (obviously),
 * (2) populate the fields with addField(),
 * (3) populate the data via add() or addAll().
 * (4) access the data via the various get*() methods.
 * 
 * @author Adam Russell (18816)
 */
public class DataTable implements List<TableDatumBean>, Serializable
{
	private List<TableDatumBean> dataList;
	private SortedSet<TableFieldBean> fieldSet;

	public DataTable()
	{
		super();
		this.fieldSet = new TreeSet<TableFieldBean>(new TableFieldComparator());
		this.dataList = new LinkedList<TableDatumBean>();
	}

	/**
	 * Returns the fields contained in the data table.
	 * 
	 * @return set of data table's fields
	 */
	public SortedSet<TableFieldBean> getFields()
	{
		return this.fieldSet;
	}
	
	/**
	 * Returns the field at the specified position in the field set.
	 * 
	 * @param index index of the field to return
	 * @return the field at the specified position in the field set
	 */
	public TableFieldBean getField(int index)
	{
		if (index < 0 || index >= fieldSet.size())
		{
			throw new IndexOutOfBoundsException();
		}
		return this.fieldSet.toArray(new TableFieldBean[this.fieldSet.size()])[index];
	}
	
	/**
	 * Returns the number of fields in the data table.
	 * 
	 * @return the number of fields in the data table
	 */
	public int getFieldCount()
	{
		return fieldSet.size();
	}
	
	/**
	 * Returns the name of the field at the specified position in the field set.
	 * 
	 * @param index index of the field whose name is to be returned
	 * @return the name of the field at the specified position
	 */
	public String getFieldName(int index)
	{
		return this.getField(index).getLabel();
	}
	
	/**
	 * Returns a short name of the field at the specified position in the field set.
	 * 
	 * This short name should be suitable to use as an SPSS variable name.
	 * 
	 * @param index index of the field whose short name is to be returned
	 * @return the short name of the field at the specified position
	 */
	public String getShortFieldName(int index)
	{
		return this.getField(index).getName();
	}
	
	/**
	 * Adds a field to the field set.
	 * 
	 * @param sortKey an integer that specifies the field's sort priority
	 * @param shortName short name, suitable to use as an SPSS variable name
	 * @param fullName full name of the field
	 * @return
	 */
	public boolean addField(Integer sortKey, String shortName, String fullName)
	{
		TableFieldBean tableFieldBean = new TableFieldBean(sortKey, shortName,
				Constants.CHARSTRINGTYPELENGTH, fullName);
		boolean result = this.fieldSet.add(tableFieldBean);
		return result;
	}
	
	/**
	 * Returns a list containing each row in the data table.
	 * 
	 * @return list of rows in the data table
	 */
	public List<List<TableDatumBean>> getRows()
	{
		List<List<TableDatumBean>> dataTable = new ArrayList<List<TableDatumBean>>(getRowCount());
		
		for (int i = 0; i < getRowCount(); ++i)
		{
			dataTable.add(getRow(i));
		}
		
		return dataTable;
	}
	
	/**
	 * Returns a specific row from the data table.
	 * 
	 * @param index the index of the row to return;
	 *              not to be confused with the index of an individual element
	 * @return a specific row from the data table
	 */
	public List<TableDatumBean> getRow(int index)
	{
		if (index < 0 || index >= getRowCount())
		{
			throw new IndexOutOfBoundsException();
		}
		int fromIndex = fieldSet.size() * index;
		int toIndex = fromIndex + fieldSet.size();
		return dataList.subList(fromIndex, toIndex);
	}
	
	/**
	 * Returns the number of rows in the data table.
	 * 
	 * @return the number of rows in the data table
	 */
	public int getRowCount()
	{
		return this.size() / this.fieldSet.size();
	}
	
	/**
	 * @see List#add(Object)
	 */
	@Override
	public boolean add(TableDatumBean e)
	{
		return this.dataList.add(e);
	}

	/**
	 * @see List#add(int, Object)
	 */
	@Override
	public void add(int index, TableDatumBean element)
	{
		this.dataList.add(index, element);
	}

	/**
	 * @see List#addAll(Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends TableDatumBean> c)
	{
		return this.dataList.addAll(c);
	}

	/**
	 * @see List#addAll(int, Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends TableDatumBean> c)
	{
		return this.dataList.addAll(index, c);
	}

	/**
	 * @see List#clear()
	 */
	@Override
	public void clear()
	{
		this.dataList.clear();
	}

	/**
	 * @see List#contains(Object)
	 */
	@Override
	public boolean contains(Object o)
	{
		return this.dataList.contains(o);
	}

	/**
	 * @see List#containsAll(Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return this.dataList.containsAll(c);
	}

	/**
	 * @see List#get(int)
	 */
	@Override
	public TableDatumBean get(int index)
	{
		return this.dataList.get(index);
	}

	/**
	 * @see List#indexOf(Object)
	 */
	@Override
	public int indexOf(Object o)
	{
		return this.dataList.indexOf(o);
	}

	/**
	 * @see List#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return this.dataList.isEmpty();
	}

	/**
	 * @see List#iterator()
	 */
	@Override
	public Iterator<TableDatumBean> iterator()
	{
		return this.dataList.iterator();
	}

	/**
	 * @see List#lastIndexOf(Object)
	 */
	@Override
	public int lastIndexOf(Object o)
	{
		return this.dataList.lastIndexOf(o);
	}

	/**
	 * @see List#listIterator()
	 */
	@Override
	public ListIterator<TableDatumBean> listIterator()
	{
		return this.dataList.listIterator();
	}

	/**
	 * @see List#listIterator(int)
	 */
	@Override
	public ListIterator<TableDatumBean> listIterator(int index)
	{
		return this.dataList.listIterator(index);
	}

	/**
	 * @see List#remove(Object)
	 */
	@Override
	public boolean remove(Object o)
	{
		return this.dataList.remove(o);
	}

	/**
	 * @see List#remove(int)
	 */
	@Override
	public TableDatumBean remove(int index)
	{
		return this.dataList.remove(index);
	}

	/**
	 * @see List#removeAll(Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return this.dataList.removeAll(c);
	}

	/**
	 * @see List#retainAll(Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return this.dataList.retainAll(c);
	}

	/**
	 * @see List#set(int, Object)
	 */
	@Override
	public TableDatumBean set(int index, TableDatumBean element)
	{
		return this.dataList.set(index, element);
	}

	/**
	 * @see List#size()
	 */
	@Override
	public int size()
	{
		return this.dataList.size();
	}

	/**
	 * @see List#subList(int, int)
	 */
	@Override
	public List<TableDatumBean> subList(int fromIndex, int toIndex)
	{
		return this.dataList.subList(fromIndex, toIndex);
	}

	/**
	 * @see List#toArray()
	 */
	@Override
	public Object[] toArray()
	{
		return this.dataList.toArray();
	}

	/**
	 * @see List#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a)
	{
		return this.dataList.toArray(a);
	}
}
