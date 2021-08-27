package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;

public class SDPOutcomesHeaderData {
	
	@Getter @Setter private String rpNameLong;
	@Getter @Setter private String rpName;
	@Getter @Setter private String stateName;
	@Getter @Setter private long stateReportId;
	@Getter @Setter private long sampleRecCount;
	@Getter @Setter private long outcomesAge;
	/**
	 * 
	 */
	public SDPOutcomesHeaderData() {
		super();
		
	}
	/**
	 * @param rpNameLong
	 * @param rpName
	 * @param stateName
	 * @param stateReportId
	 * @param sampleRecCount
	 */
	public SDPOutcomesHeaderData(String rpNameLong, String rpName,
			String stateName, long stateReportId, long sampleRecCount,long outcomesAge) {
		super();
		this.rpNameLong = rpNameLong;
		this.rpName = rpName;
		this.stateName = stateName;
		this.stateReportId = stateReportId;
		this.sampleRecCount = sampleRecCount;
		this.outcomesAge = outcomesAge;
	}
	
	

}
