/**
 * Filename: TableFieldComparator.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Sep 1, 2009
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

import java.util.Comparator;


/**
 * Compares two TableFieldBean objects.
 * 
 * TableFieldBeans are sorted first by their sortKey attributes
 * and second by their name attributes.
 * 
 * @author Adam Russell (18816)
 */
public class TableFieldComparator implements Comparator<TableFieldBean>
{	
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(TableFieldBean tableFieldBean1, TableFieldBean tableFieldBean2)
	{

		int comparison = tableFieldBean1.getSortKey().compareTo(tableFieldBean2.getSortKey());
		
		if (comparison == 0 && tableFieldBean1.getName() != null && tableFieldBean2.getName() != null)
		{
			comparison = tableFieldBean1.getName().compareTo(tableFieldBean2.getName());
		}
		
		return comparison;
	}
}
