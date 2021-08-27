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
public class SDPTextChartRow {
	
	@Getter @Setter private String label;
	@Getter @Setter private String strVal1;
	@Getter @Setter private String strVal2;
	/**
	 * 
	 */
	public SDPTextChartRow() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param label
	 * @param strVal1
	 * @param strVal2
	 */
	public SDPTextChartRow(String label, String strVal1, String strVal2) {
		super();
		this.label = label;
		this.strVal1 = strVal1;
		this.strVal2 = strVal2;
	}
	/**
	 * @param label
	 * @param strVal1
	 */
	public SDPTextChartRow(String label, String strVal1) {
		super();
		this.label = label;
		this.strVal1 = strVal1;
	}
	
	

}
