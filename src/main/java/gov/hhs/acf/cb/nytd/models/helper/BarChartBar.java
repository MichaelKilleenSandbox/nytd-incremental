/**
 * 
 */
package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;

public class BarChartBar {
	
	@Getter @Setter private String label;
	@Getter @Setter private double value;

	public BarChartBar()
	{
		super();
	}
	
	/**
	 * @param label
	 * @param value
	 */
	public BarChartBar(String label, double value) {
		super();
		this.label = label;
		this.value = value;
	}


	
}
