/**
 * Copyright 2010, ICF International
 * Created: Jan 29, 2010
 * Author: 15178
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

import java.util.Comparator;

/**
 * @author 15178
 * 
 */
public class ComplianceErrorsComparator implements Comparator<ComplianceErrors>
{
	private String columnSelected;
	private boolean orderBy;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ComplianceErrors o1, ComplianceErrors o2)
	{
		int result = -2;
		if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.ELEMENT_NUMBER)
				&& o1 != null && o2 != null && o1.getElementNumberLong() != null
				&& o2.getElementNumberLong() != null)
		{
			result = o1.getElementNumberLong().compareTo(o2.getElementNumberLong());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.ELEMENT_NAME)
				&& o1.getElementName() != null && o2.getElementName() != null)
		{
			result = o1.getElementName().toLowerCase().compareTo(o2.getElementName().toLowerCase());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.ERROR_FREE)
				&& o1.getActualRate() != null && o2.getActualRate() != null)
		{

			Float f1 = new Float(o1.getActualRate());
			Float f2 = new Float(o2.getActualRate());
			result = f1.compareTo(f2);
			//result = o1.getActualRate().compareTo(o2.getActualRate());
		}
		else if (getColumnSelected() != null
				&& getColumnSelected().equalsIgnoreCase(Constants.REGULATED_COMPLIANCE_STANDARD)
				&& o1.getCompliance() != null && o2.getCompliance() != null)
		{
			result = o1.getCompliance().compareTo(o2.getCompliance());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.DESCRIPTION)
				&& o1.getDescription() != null && o2.getDescription() != null)
		{
			result = o1.getDescription().toLowerCase().compareTo(o2.getDescription().toLowerCase());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.RECORD_NUMBER)
				&& o1.getRecordNumber() != null && o2.getRecordNumber() != null)
		{
			result = o1.getRecordNumber().toLowerCase().compareTo(o2.getRecordNumber().toLowerCase());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.NOTE)
				&& o1.getNote() != null && o2.getNote() != null)
		{
			result = o1.getNote().toLowerCase().compareTo(o2.getNote().toLowerCase());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.DATUM_VALUE)
				&& o1.getDatumValue() != null && o2.getDatumValue() != null)
		{
			result = o1.getDatumValue().compareTo(o2.getDatumValue());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.ACTUAL_RATE)
				&& o1.getActualRate() != null && o2.getActualRate() != null)
		{
			result = o1.getActualRate().compareTo(o2.getActualRate());
		}
		else if (getColumnSelected() != null
				&& getColumnSelected().equalsIgnoreCase(Constants.DATA_QUALITY_ADV_RATE)
				&& o1.getDataQualityAdvRate() != null && o2.getDataQualityAdvRate() != null)
		{
			result = o1.getDataQualityAdvRate().compareTo(o2.getDataQualityAdvRate());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.NAME_KEY)
				&& o1.getNameKey() != null && o2.getNameKey() != null)
		{
			result = o1.getNameKey().compareTo(o2.getNameKey());
		}
		else if (getColumnSelected() != null
				&& getColumnSelected().equalsIgnoreCase(Constants.POTENTIAL_PENALTY)
				&& o1.getPotentialPenalty() != null && o2.getPotentialPenalty() != null)
		{
			result = o1.getPotentialPenalty().compareTo(o2.getPotentialPenalty());
		}
		else if (getColumnSelected() != null
				&& getColumnSelected().equalsIgnoreCase(Constants.REPORTING_PERIOD)
				&& o1.getReportingPeriod() != null && o2.getReportingPeriod() != null)
		{
			result = o1.getReportingPeriod().compareTo(o2.getReportingPeriod());
		}
		else if (getColumnSelected() != null && getColumnSelected().equalsIgnoreCase(Constants.STATE)
				&& o1.getState() != null && o2.getState() != null)
		{
			result = o1.getState().toLowerCase().compareTo(o2.getState().toLowerCase());
		}
		return result;
	}

	/**
	 * @return the columnSelected
	 */
	public String getColumnSelected()
	{
		return columnSelected;
	}

	/**
	 * @param columnSelected
	 *           the columnSelected to set
	 */
	public void setColumnSelected(String columnSelected)
	{
		this.columnSelected = columnSelected;
	}

	/**
	 * @return the orderBy
	 */
	public boolean isOrderBy()
	{
		return orderBy;
	}

	/**
	 * @param orderBy
	 *           the orderBy to set
	 */
	public void setOrderBy(boolean orderBy)
	{
		this.orderBy = orderBy;
	}

}
