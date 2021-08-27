package gov.hhs.acf.cb.nytd.models.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FailedTransmissionDetail
{
	 private String trasmissionId;
	 private String stateAbbr;
	 private String fileReceivedDate;
	 private String fileType;
	 private String fileName;
	 private String reason;
	 private String dataFileReportDateValue;
	 private String dataFileStateValue;
	 private String dataFileTransmissionTypeValue;
	 private String processingException;
	/**
	 * @return the trasmissionId
	 */
	public String getTrasmissionId()
	{
		return trasmissionId;
	}
	/**
	 * @param trasmissionId the trasmissionId to set
	 */
	public void setTrasmissionId(String trasmissionId)
	{
		this.trasmissionId = trasmissionId;
	}
	/**
	 * @return the stateAbbr
	 */
	public String getStateAbbr()
	{
		return stateAbbr;
	}
	/**
	 * @param stateAbbr the stateAbbr to set
	 */
	public void setStateAbbr(String stateAbbr)
	{
		this.stateAbbr = stateAbbr;
	}
	/**
	 * @return the fileReceivedDate
	 */
	public String getFileReceivedDate()
	{
		return fileReceivedDate;
	}
	/**
	 * @param fileReceivedDate the fileReceivedDate to set
	 */
	public void setFileReceivedDate(String fileReceivedDate)
	{
		this.fileReceivedDate = fileReceivedDate;
	}
	/**
	 * @return the fileType
	 */
	public String getFileType()
	{
		return fileType;
	}
	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName()
	{
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	/**
	 * @return the reason
	 */
	public String getReason()
	{
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason)
	{
		this.reason = reason;
	}
	
   public void formatErrorMessage() {
	 	
   	if (reason != null)
      {
	   	String datafileValue = null;	
	      Map<String, Object> namedParams = new HashMap<String, Object>();
	      Pattern paramPattern = Pattern.compile("(\\$\\{(\\w+)\\})");
	 	  	Matcher paramMatcher = paramPattern.matcher(this.reason);
	 	  	StringBuffer sb = new StringBuffer();
	    
		  if(this.fileName != null) 
		  	  namedParams.put("fileName",this.fileName); 
		  else
			  namedParams.put("fileName","");
		  if(this.dataFileReportDateValue != null )
			  namedParams.put("reportdate",this.dataFileReportDateValue);
		  else
			  namedParams.put("reportdate","");
		  if(this.dataFileStateValue != null)
			  namedParams.put("state",this.dataFileStateValue);
		  else
			  namedParams.put("state","");
		  if(this.dataFileTransmissionTypeValue!= null)
			  namedParams.put("fileCategory",this.dataFileTransmissionTypeValue);
		  else
			  namedParams.put("fileCategory","");
	
		  while (paramMatcher.find()) {
	         String namedParameter = paramMatcher.group(2);
	
	         if (namedParams.containsKey(namedParameter)) {
	             paramMatcher.appendReplacement(
	                     sb, namedParams.get(namedParameter).toString());
	         } 
	     }
	        paramMatcher.appendTail(sb);
	  
	     this.reason = sb.toString();
    }
 }
   
	/**
	 * @return the dataFileReportDateValue
	 */
	public String getDataFileReportDateValue()
	{
		return dataFileReportDateValue;
	}
	/**
	 * @param dataFileReportDateValue the dataFileReportDateValue to set
	 */
	public void setDataFileReportDateValue(String dataFileReportDateValue)
	{
		this.dataFileReportDateValue = dataFileReportDateValue;
	}
	/**
	 * @return the dataFileStateValue
	 */
	public String getDataFileStateValue()
	{
		return dataFileStateValue;
	}
	/**
	 * @param dataFileStateValue the dataFileStateValue to set
	 */
	public void setDataFileStateValue(String dataFileStateValue)
	{
		this.dataFileStateValue = dataFileStateValue;
	}
	/**
	 * @return the dtaFileTransmissionTypeValue
	 */
	public String getDataFileTransmissionTypeValue()
	{
		return dataFileTransmissionTypeValue;
	}
	/**
	 * @param dtaFileTransmissionTypeValue the dtaFileTransmissionTypeValue to set
	 */
	public void setDataFileTransmissionTypeValue(String dataFileTransmissionTypeValue)
	{
		this.dataFileTransmissionTypeValue = dataFileTransmissionTypeValue;
	}
	/**
	 * @return the processingException
	 */
	public String getProcessingException()
	{
		return processingException;
	}
	/**
	 * @param processingException the processingException to set
	 */
	public void setProcessingException(String processingException)
	{
		this.processingException = processingException;
	}
}
