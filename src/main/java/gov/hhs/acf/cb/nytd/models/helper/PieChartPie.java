/**
 * 
 */
package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 23839
 *
 */
public class PieChartPie {
	
	@Getter @Setter private String label;
	@Getter @Setter private double value;

	public PieChartPie()
	{
		super();
	}
	
	/**
	 * @param label
	 * @param value
	 */
	public PieChartPie(String label, double value) {
		super();
		this.label = label;
		this.value = value;
	}


	
}
