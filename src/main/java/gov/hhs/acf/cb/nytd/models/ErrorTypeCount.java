package gov.hhs.acf.cb.nytd.models;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public  class ErrorTypeCount extends PersistentObject
{
/*	@Getter @Setter private ReportingPeriod reportingPeriod;
	@Getter @Setter private State state;
	@Getter @Setter private Element element;
	@Getter @Setter private ProblemDescription problemDescription;
	@Getter @Setter Long count;*/
	
	@Getter @Setter private String REPORTINGPERIOD;
	@Getter @Setter private String STATENAME;
	@Getter @Setter private BigDecimal ELEMENTID;
	@Getter @Setter private BigDecimal TRANSMISSIONID;
	@Getter @Setter private String ELEMENTNAME;
	@Getter @Setter private String PROBLEMDESCRIPTION;
	@Getter @Setter private String COMPCAT;
	@Getter @Setter private BigDecimal ERRORCOUNT;
	@Getter @Setter private BigDecimal ROWNUM_;
	@Getter @Setter private BigDecimal problemDescriptionId;
	
	public String formatErrorMessage() {
        if (PROBLEMDESCRIPTION == null) {
            return "N/A";
        }

        Map<String, Object> namedParams = new HashMap<String, Object>();
    //    Datum datum = nonCompliance.getDatum();
        namedParams.put("elementNumber",ELEMENTID);
        namedParams.put("elementDescription",ELEMENTID);
        namedParams.put("mmddyyyy","reporting period");
        namedParams.put("complianceRate","Compliance rate");
        namedParams.put("datumValue", "");
       /* namedParams.put("datumValue",
                datum.getValue() == null ? "Blank" : datum.getValue());
        namedParams.put("referenceDatumValue", referenceDatumValue);*/
        String errorMsg = formatText(
        		PROBLEMDESCRIPTION, namedParams);
        return errorMsg;
    }
}
