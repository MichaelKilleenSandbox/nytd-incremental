/**
 * Filename: FileAdvisoryAcrossReportPeriodsComparator.java
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

import java.util.Comparator;


/**
 * Compares FileAdvisoryAcrossReportPeriods objects so that they are ordered
 * in a manner suitable for display.
 * 
 * @author Adam Russell (18816)
 * @see FileAdvisoryAcrossReportPeriods
 * @see FileComparisonAcrossReportPeriods
 */
public class FileAdvisoryAcrossReportPeriodsComparator implements Comparator<FileAdvisoryAcrossReportPeriods>
{
	private Context context;
	
	public enum Priority { RECORD_NUMBER, ELEMENT, REPORT_PERIOD, FILE_NUMBER, DESCRIPTION,
	                       RECORD_NUMBER_DESC, ELEMENT_DESC, REPORT_PERIOD_DESC, FILE_NUMBER_DESC, DESCRIPTION_DESC }
	
	private class ComparatorByRecordNumber implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByElement implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByReportPeriod implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByFileNumber implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByDescription implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByRecordNumberDesc implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result * -1;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByElementDesc implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result * -1;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByReportPeriodDesc implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result * -1;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByFileNumberDesc implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result * -1;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class ComparatorByDescriptionDesc implements Comparator<FileAdvisoryAcrossReportPeriods>
	{
		@Override
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			int result;
			
			result = compareDescriptions(o1, o2);
			if (result != 0)
			{
				return result * -1;
			}
			
			result = compareRecordNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareElements(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareReportPeriods(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			result = compareFileNumbers(o1, o2);
			if (result != 0)
			{
				return result;
			}
			
			return 0;
		}
	}
	
	private class Context
	{
		private Comparator<FileAdvisoryAcrossReportPeriods> strategy;
		
		public Context(Comparator<FileAdvisoryAcrossReportPeriods> strategy)
		{
			this.strategy = strategy;
		}
		
		public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
		{
			return strategy.compare(o1, o2);
		}
	}
	
	public FileAdvisoryAcrossReportPeriodsComparator()
	{
		super();
		context = new Context(new ComparatorByRecordNumber());
	}
	
	public FileAdvisoryAcrossReportPeriodsComparator(Priority priority)
	{
		super();
		
		switch (priority)
		{
			case DESCRIPTION_DESC:
				context = new Context(new ComparatorByDescriptionDesc());
				break;
			case FILE_NUMBER_DESC:
				context = new Context(new ComparatorByFileNumberDesc());
				break;
			case REPORT_PERIOD_DESC:
				context = new Context(new ComparatorByReportPeriodDesc());
				break;
			case ELEMENT_DESC:
				context = new Context(new ComparatorByElementDesc());
				break;
			case RECORD_NUMBER_DESC:
				context = new Context(new ComparatorByRecordNumberDesc());
				break;
			case DESCRIPTION:
				context = new Context(new ComparatorByDescription());
				break;
			case FILE_NUMBER:
				context = new Context(new ComparatorByFileNumber());
				break;
			case REPORT_PERIOD:
				context = new Context(new ComparatorByReportPeriod());
				break;
			case ELEMENT:
				context = new Context(new ComparatorByElement());
				break;
			case RECORD_NUMBER:
			default:
				context = new Context(new ComparatorByRecordNumber());
		}
	}
	
	public int compareRecordNumbers(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
	{
		if (o1.getId().getRecordNumber().compareToIgnoreCase(o2.getId().getRecordNumber()) < 0)
		{
			return -1;
		}
		
		if (o1.getId().getRecordNumber().compareToIgnoreCase(o2.getId().getRecordNumber()) > 0)
		{
			return 1;
		}
		
		return 0;
	}
	
	public int compareElements(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
	{
		if (!o1.getId().getElementName().equalsIgnoreCase("N/A") && o2.getId().getElementName().equalsIgnoreCase("N/A"))
		{
			return -1;
		}
		
		if (o1.getId().getElementName().equalsIgnoreCase("N/A") && !o2.getId().getElementName().equalsIgnoreCase("N/A"))
		{
			return 1;
		}
		
		if (!o1.getId().getElementName().equalsIgnoreCase("N/A") && !o2.getId().getElementName().equalsIgnoreCase("N/A")
		 && Long.valueOf(o1.getId().getElementName()) < Long.valueOf(o2.getId().getElementName()))
		{
			return -1;
		}
		
		if (!o1.getId().getElementName().equalsIgnoreCase("N/A") && !o2.getId().getElementName().equalsIgnoreCase("N/A")
		 && Long.valueOf(o1.getId().getElementName()) > Long.valueOf(o2.getId().getElementName()))
		{
			return 1;
		}
		
		return 0;
	}
	
	public int compareReportPeriods(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
	{
		if (o1.getId().getReportPeriodName().compareToIgnoreCase(o2.getId().getReportPeriodName()) < 0)
		{
			return -1;
		}
		
		if (o1.getId().getReportPeriodName().compareToIgnoreCase(o2.getId().getReportPeriodName()) > 0)
		{
			return 1;
		}
		
		return 0;
	}
	
	public int compareFileNumbers(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
	{
		if (o1.getId().getTransmissionId().compareToIgnoreCase(o2.getId().getTransmissionId()) < 0)
		{
			return -1;
		}
		
		if (o1.getId().getTransmissionId().compareToIgnoreCase(o2.getId().getTransmissionId()) > 0)
		{
			return 1;
		}
		
		return 0;
	}
	
	public int compareDescriptions(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
	{
		if (o1.getProblemDescription().compareToIgnoreCase(o2.getProblemDescription()) < 0)
		{
			return -1;
		}
		
		if (o1.getProblemDescription().compareToIgnoreCase(o2.getProblemDescription()) > 0)
		{
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(FileAdvisoryAcrossReportPeriods o1, FileAdvisoryAcrossReportPeriods o2)
	{
		return context.compare(o1, o2);
	}
}
