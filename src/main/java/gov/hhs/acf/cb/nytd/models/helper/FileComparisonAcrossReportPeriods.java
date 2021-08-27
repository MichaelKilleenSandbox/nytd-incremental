/**
 * Filename: FileComparisonAcrossReportPeriods.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Dec 8, 2009
 *  Author: adam
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
import java.util.*;


/**
 * A cross-file comparison regarding previous reporting periods.
 * 
 * More specifically, an collection of advisories for records in a 
 * transmitted data file being compared to previous reporting periods.
 * Contains Record Number, Report Period, Federal File ID, Element,
 * and Problem Description.
 * 
 * @author Adam Russell (18816)
 */
public class FileComparisonAcrossReportPeriods implements Serializable, SortedSet<FileAdvisoryAcrossReportPeriods>
{
	private Comparator<? super FileAdvisoryAcrossReportPeriods> comparator;
	
	private SortedSet<FileAdvisoryAcrossReportPeriods> fileComparisonAcrossReportPeriods;
	
	public FileComparisonAcrossReportPeriods()
	{
		super();
		
		comparator = new FileAdvisoryAcrossReportPeriodsComparator();
		fileComparisonAcrossReportPeriods = new TreeSet<FileAdvisoryAcrossReportPeriods>(comparator);
	}
	
	public FileComparisonAcrossReportPeriods(FileAdvisoryAcrossReportPeriodsComparator.Priority priority)
	{
		super();
		
		comparator = new FileAdvisoryAcrossReportPeriodsComparator(priority);
		fileComparisonAcrossReportPeriods = new TreeSet<FileAdvisoryAcrossReportPeriods>(comparator);
	}

	/**
	 * @see SortedSet#comparator()
	 */
	@Override
	public Comparator<? super FileAdvisoryAcrossReportPeriods> comparator()
	{
		return comparator;
	}

	/**
	 * @see SortedSet#first()
	 */
	@Override
	public FileAdvisoryAcrossReportPeriods first()
	{
		return fileComparisonAcrossReportPeriods.first();
	}

	/**
	 * @see SortedSet#headSet(Object)
	 */
	@Override
	public SortedSet<FileAdvisoryAcrossReportPeriods> headSet(FileAdvisoryAcrossReportPeriods toElement)
	{
		return fileComparisonAcrossReportPeriods.headSet(toElement);
	}

	/**
	 * @see SortedSet#last()
	 */
	@Override
	public FileAdvisoryAcrossReportPeriods last()
	{
		return fileComparisonAcrossReportPeriods.last();
	}

	/**
	 * @see SortedSet#subSet(Object, Object)
	 */
	@Override
	public SortedSet<FileAdvisoryAcrossReportPeriods> subSet(FileAdvisoryAcrossReportPeriods fromElement,
			FileAdvisoryAcrossReportPeriods toElement)
	{
		return fileComparisonAcrossReportPeriods.subSet(fromElement, toElement);
	}

	/**
	 * @see SortedSet#tailSet(Object)
	 */
	@Override
	public SortedSet<FileAdvisoryAcrossReportPeriods> tailSet(FileAdvisoryAcrossReportPeriods fromElement)
	{
		return fileComparisonAcrossReportPeriods.tailSet(fromElement);
	}

	/**
	 * @see java.util.Set#add(Object)
	 */
	@Override
	public boolean add(FileAdvisoryAcrossReportPeriods e)
	{
		return fileComparisonAcrossReportPeriods.add(e);
	}

	/**
	 * @see java.util.Set#addAll(Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends FileAdvisoryAcrossReportPeriods> c)
	{
		return fileComparisonAcrossReportPeriods.addAll(c);
	}

	/**
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear()
	{
		fileComparisonAcrossReportPeriods.clear();
	}

	/**
	 * @see java.util.Set#contains(Object)
	 */
	@Override
	public boolean contains(Object o)
	{
		return fileComparisonAcrossReportPeriods.contains(o);
	}

	/**
	 * @see java.util.Set#containsAll(Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return fileComparisonAcrossReportPeriods.containsAll(c);
	}

	/**
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return fileComparisonAcrossReportPeriods.isEmpty();
	}

	/**
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<FileAdvisoryAcrossReportPeriods> iterator()
	{
		return fileComparisonAcrossReportPeriods.iterator();
	}

	/**
	 * @see java.util.Set#remove(Object)
	 */
	@Override
	public boolean remove(Object o)
	{
		return fileComparisonAcrossReportPeriods.remove(o);
	}

	/**
	 * @see java.util.Set#removeAll(Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return fileComparisonAcrossReportPeriods.removeAll(c);
	}

	/**
	 * @see java.util.Set#retainAll(Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return fileComparisonAcrossReportPeriods.retainAll(c);
	}

	/**
	 * @see java.util.Set#size()
	 */
	@Override
	public int size()
	{
		return fileComparisonAcrossReportPeriods.size();
	}

	/**
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray()
	{
		return fileComparisonAcrossReportPeriods.toArray();
	}

	/**
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a)
	{
		return fileComparisonAcrossReportPeriods.toArray(a);
	}
}
