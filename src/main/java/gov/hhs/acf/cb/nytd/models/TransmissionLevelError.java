package gov.hhs.acf.cb.nytd.models;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: May 10, 2010
 */
public class TransmissionLevelError extends NytdError {
	private String[] splitedFileName = null;
    public String formatErrorMessage() {
   	 	if (problemDescription == null) {
            return "N/A";
        }
   	 	String datafileValue = null;	
        Map<String, Object> namedParams = new HashMap<String, Object>();
        if(this.getComplianceCategory().getId() != 2)
        {
	       if(getNonCompliance().getDataAggregate()!= null)
	       {
	    	   BigDecimal complianceRate = getNonCompliance().getDataAggregate().getPercentValue();
	    	   //       	complianceRate = BigDecimal.valueOf(100).subtract(complianceRate);
	    	   namedParams.put("complianceRate", complianceRate);
	       }
	       Set<DueDate> dueDates =	this.getNonCompliance().getTransmission().getReportingPeriod().getDueDates();
	       if(dueDates != null)
	       {
		       int transmissionTypeId = this.getNonCompliance().getTransmission().getTransmissionType().getId().intValue();
		       Iterator<DueDate> itr = dueDates.iterator();
		       while(itr.hasNext())
		       {
		    	   DueDate due = itr.next();
		    	   int transTypeId = due.getTransmissionType().getId().intValue();
		    	   if(transTypeId == transmissionTypeId)
		    	   {
		    		  Calendar submissionDate = due.getSubmissionDate();
		    		  DateFormat format = new SimpleDateFormat("dd-MMM-yyyy"); 
		    		  namedParams.put("mmddyyyy", format.format(submissionDate.getTime()));
		    		  break;
		    		
		    	   }
		       }
	       }
        }
        else
        {
      	  if(this.getNonCompliance().getTransmission().getFileName() != null) 
      	  {
      		  namedParams.put("fileName",this.getNonCompliance().getTransmission().getFileName());      		 
      	  }
      	else
			  namedParams.put("fileName","");
      	  if(this.getNonCompliance().getTransmission().getDataFileReportPeriodValue() != null )
      		  namedParams.put("reportdate",this.getNonCompliance().getTransmission().getDataFileReportPeriodValue());
      	else
			  namedParams.put("reportdate","");
      	  if(this.getNonCompliance().getTransmission().getDataFileStateValue()!= null)
      		  namedParams.put("state",this.getNonCompliance().getTransmission().getDataFileStateValue());
      	else
			  namedParams.put("state","");
      	  if(this.getNonCompliance().getTransmission().getDataFileTransmissionTypeValue()!= null)
      		  namedParams.put("fileCategory",this.getNonCompliance().getTransmission().getDataFileTransmissionTypeValue());
      	  else
			  namedParams.put("fileCategory","");
    
/*        		if(problemDescription.getId() == 61)
        		{
        			datafileValue = this.getNonCompliance().getTransmission().getDataFileReportPeriodValue();
        			datafileValue = datafileValue == null ? "BLANK" : datafileValue;
        			namedParams.put("reportdate",datafileValue);
        		}
        		if(problemDescription.getId() == 60)
        		{
        			datafileValue = this.getNonCompliance().getTransmission().getDataFileStateValue();
        			datafileValue = datafileValue == null ? " " : datafileValue;
        			namedParams.put("state",datafileValue);
        		}   
        		if(problemDescription.getId() == 74)
        		{
        			datafileValue = this.getNonCompliance().getTransmission().getDataFileReportPeriodValue();
        			datafileValue = datafileValue == null ? "BLANK" : datafileValue;
        			namedParams.put("reportdate",datafileValue);        			
        		} 
        		if(problemDescription.getId() == 17)
        		{
        			datafileValue = this.getNonCompliance().getTransmission().getDataFileReportPeriodValue();
        			datafileValue = datafileValue == null ? "BLANK" : datafileValue;
        			namedParams.put("reportdate",datafileValue);        			
        		} 
     		if(problemDescription.getId() == 70)
        		{      			
        			datafileValue = this.getNonCompliance().getTransmission().getDataFileTransmissionTypeValue();        			
        			datafileValue = datafileValue == null ? "BLANK" : datafileValue;
        			namedParams.put("fileCategory",datafileValue);        			
        		}
*/  	
        }
        String errorMsg = problemDescription.formatText(
                problemDescription.getName(), namedParams);
        return errorMsg;
    }
}
