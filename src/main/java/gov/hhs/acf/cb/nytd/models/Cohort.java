/**
 * Filename: Cohort.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Jun 27, 2012
 *  Author: 23839
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models;

import lombok.Getter;
import lombok.Setter;


/**
 * @author Nava Krishna Mallela (23839)
 *
 */
public class Cohort extends PersistentObject {
	
	@Getter @Setter String name;
	@Getter @Setter String reportYear17;
	@Getter @Setter String reportYear19;
	@Getter @Setter String reportYear21;
	@Getter @Setter String derivedName;
	
	public Cohort(){}
	public Cohort(Long id)
	{
		this.id = id;
	}

	public Cohort(Long id,String reportYear17,String reportYear19, String reportYear21,String derivedName)
	{
		this.id = id;
		this.reportYear17 = reportYear17;
		this.reportYear19 = reportYear19;
		this.reportYear21 = reportYear21;
		this.derivedName = derivedName;
	}
}
