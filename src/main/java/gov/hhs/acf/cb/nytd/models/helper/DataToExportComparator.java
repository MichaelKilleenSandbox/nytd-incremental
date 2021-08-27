/**
 * Filename: DataToExportComparator.java
 * 
 *  Copyright 2010, ICF International
 *  Created: Jan 21, 2010
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

import gov.hhs.acf.cb.nytd.models.Datum;

import java.util.Comparator;


/**
 * Compares two Datum objects.
 * 
 * The first sort key is the datum's transmission record database identifier
 * in ascending order, and the second sort key is the element number in 
 * ascending order.
 * 
 * This comparator was written so that a collection of MVwRecordToExport
 * objects could be created given all of the Datum objects in a
 * Transmission.
 * 
 * @author Adam Russell (18816)
 */
public class DataToExportComparator implements Comparator<Datum>
{
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(Datum o1, Datum o2)
	{
		int result = 0;
		result = o1.getTransmissionRecord().getId().compareTo(o2.getTransmissionRecord().getId());
		if (result == 0)
		{
			result = o1.getElement().getSort().compareTo(
					o2.getElement().getSort());
		}
		return result;
	}
}
