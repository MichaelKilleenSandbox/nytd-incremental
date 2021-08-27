/**
 * Filename: DataExportJob.java
 * 
 * Copyright 2009, ICF International
 * Created: May 7, 2010
 * Author: 19714
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.jobs;

import com.pmstation.spss.SPSSWriter;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.NytdDataTable;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.models.helper.TableFieldBean;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.service.impl.SPSSWriterThread;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Quartz job that creates a data export file.
 * 
 * @author Dan Bradley (19714)
 */
public class DataExportJob extends QuartzJobBean implements StatefulJob
{
	protected final Logger log = Logger.getLogger(getClass());
	private DataExtractionService dataExtractionService;
	private MessageService messageServiceP3;
	private String exportLocation;
	private String rootURL;
	private static final String downloadAction = "/downloadExportFile.action?downloadFilename=";
	/**
	 * Exports the Quartz job.
	 * 
	 * @param jobContext
	 *           the Quartz job context
	 */
	protected void executeInternal(JobExecutionContext jobContext) throws JobExecutionException
	{
		JobDataMap jobDataMap = jobContext.getJobDetail().getJobDataMap();

		// create temp file in case user clicks link before file is ready
		File placeholderFile;
		try
		{
			placeholderFile = new File(new URI(getExportLocation() + (String) jobDataMap.get("fileName") + ".tmp"));
		}
		catch (URISyntaxException e)
		{
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
		try
		{
			placeholderFile.createNewFile();
		}
		catch (IOException exception)
		{
			log.error("Error creating " + placeholderFile.getAbsolutePath() + ".", exception);
			exception.printStackTrace();
		}
		
		int pageSize = 10000;
		int totalrows = getCount(jobDataMap);
		if(pageSize > totalrows && totalrows > 0)
			pageSize = totalrows;
		int totalPages =  totalrows%pageSize == 0 ? totalrows/pageSize : (totalrows+1)/pageSize;
		NytdDataTable dataTable = null;
		SiteUser user = (SiteUser) jobDataMap.get("siteUser");
		String fileType = (String) jobDataMap.get("fileType");
		SPSSWriter outSPSS  = null;
		
		String finalFileName = getExportLocation() + (String) jobDataMap.get("fileName");
		String tempFileName = finalFileName + ".tmp";
		File tempFile = null;
		File finalFile = null;
		PipedInputStream in = null;
		PipedOutputStream out = null;
		Thread thread = null;
		int counter = 0;
		for (int pageNumber = 0 ; pageNumber <= totalPages ; pageNumber++ )
		{
			// prepare parameters for createFile()
			dataTable = createDataTable(jobDataMap,pageNumber,pageSize);
		//	user = (SiteUser) jobDataMap.get("siteUser");

			boolean endofRecs = false;
			boolean startofRecs = false;
			if (pageNumber == totalPages)
					endofRecs = true;
			if (pageNumber == 0)
				startofRecs =true;
			try
			{
				// The  logic flow to generate SPSS document is different from the rest of file types.
				// This is due to the fact that SPSS document have a specific closing section that needs to be added just before closing the file.
				// Once the closing section of the SPSS document is written to the file, no more cases can be appended to it.

				if ( getFileFormat(fileType).equalsIgnoreCase(Constants.SAV_EXT))
				{
					String value;
					tempFile = new File(new URI(tempFileName));
					finalFile = new File(new URI(finalFileName));
					try
						{
							// Adds the SPSS dictionary section and data section only once at the start of the file writing
							if(startofRecs)
							{
								in = new PipedInputStream();
								out = new PipedOutputStream(in);
								
								// Assign SPSS output to the file.
								// The windows-1252 charset is only used because that's what
								// the tutorial used. If it matters, it's possible
								// something else can be used.
								// outSPSS = new SPSSWriter(out,"windows-1252");
								outSPSS = new SPSSWriter(out,"ISO-8859-1");
	
								// Creating SPSS variable description table.
								outSPSS.addDictionarySection("NYTD Data Export","NYTD Data Export");
		
								// Describing variable names and types.
								for (TableFieldBean field : dataTable.getFields())
								{
									//outSPSS.addStringVar(field.getName(), field.getLength(), field.getLabel());
									if(Constants.string_fields.containsKey(field.getName()))
									{
										outSPSS.addStringVar(CommonFunctions.getSPSSFIELDNAME(field), field.getLength(), field.getLabel());
									}
									else
										if(Constants.FIELD_E4.equalsIgnoreCase(field.getName()) ||
												Constants.FIELD_E35.equalsIgnoreCase(field.getName()))
										{
											//outSPSS.addDateVar(field.getName(), DataConstants.DATE_CODE_03, field.getLabel());
											 outSPSS.addStringVar(CommonFunctions.getSPSSFIELDNAME(field), field.getLength(), field.getLabel());
										}
										else
										{
											outSPSS.addNumericVar(CommonFunctions.getSPSSFIELDNAME(field), 8, 0, field.getLabel());
										}
								}
		
								// Creating SPSS variable value define table.
								outSPSS.addDataSection();
							}
							
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							// Insert the actual data into the file.
							for (TableDatumBean datum : dataTable)
							{
								//value = datum.value;
								value = CommonFunctions.getSPSSCode(datum);
							/*	if (value != null)
								{
									value = value.trim();
								}*/
								String fieldName = null;
								if(datum.getColumn().contains(Constants.OPEN_PARENTHESIS))
								{
									fieldName =new StringBuffer(datum.getColumn().substring(0,
											(datum.getColumn().indexOf(Constants.OPEN_PARENTHESIS)-1)
											)).toString().trim();
								
								}
								else
									fieldName = datum.getColumn().trim();
						
							//	outSPSS.addData(value);
								try {
									if(Constants.string_fields_LABEL.containsKey(fieldName))
									{
										if(value == null || (value!=null & value.length() == 0))
											outSPSS.addData(" ");
										else
											outSPSS.addData(value);
										
									}
									else if(Constants.FIELD_E4_LABEL.equalsIgnoreCase(fieldName) ||
												Constants.FIELD_E35_LABEL.equalsIgnoreCase(fieldName))
									{
										
										if(value == null || (value!=null & value.length() == 0))
											outSPSS.addData(" ");
										else
											outSPSS.addData(value);
										
									}
									else
									{
										Long lv = new Long(value);
										outSPSS.addData(lv);
									}
								} catch (Exception e) {
								/*	System.out.println("ERROR OCCURED WHILE READING COLUMN: "+datum.getColumn());
									System.out.println("ERROR VALUE IS: "+value+" & Actual Value is: "+datum.getValue());
									e.printStackTrace();*/
								}
							}
							
							// Adds the SPSS finish section only once at the end of the file writing.							
							if(endofRecs && !startofRecs)
							{							
								try {
									outSPSS.addFinishSection();
								} catch (Exception e) {
									
									e.printStackTrace();
								}						
							}
							
							synchronized (tempFile)
							{
								thread = new Thread(new SPSSWriterThread(in,startofRecs,endofRecs,tempFile,finalFile,thread));
								thread.setName("Thread"+ ++counter);
								thread.start();
							}

							if(endofRecs)
							{
							// notify user
								Map<String, Object> namedParams = new HashMap<String, Object>();
							//	namedParams.put("dataExportLink", hostAndPort + fileName);
								namedParams.put("dataExportLink", getRootURL() + getDownloadaction() + (String) jobDataMap.get("fileName"));
								namedParams.put("firstName", user.getFirstName());
								namedParams.put("lastName", user.getLastName());
								Message dataExportMsg = messageServiceP3.createSystemMessage(
										MessageService.DATA_EXPORT_NOTIFICATION, namedParams);
								List<SiteUser> recipients = new ArrayList<SiteUser>();
								recipients.add(user);
								messageServiceP3.sendSystemMessage(dataExportMsg, recipients);
							// closing input and output streams
								in.close();
								out.close();
							}
						
					}
					catch (Exception e)
					{
						// We failed to create the SPSS file for some reason.
						log.error(e.getMessage(), e);
					}

					
				}
				else
				{
					createFile(dataTable, user, getFileFormat(fileType), (String) jobDataMap.get("fileName"), (String) jobDataMap.get("hostAndPort"),startofRecs,endofRecs);
				}
			}
			catch (URISyntaxException e)
			{
				log.error(e.getMessage(), e);
				e.printStackTrace();
				throw new JobExecutionException(e);
			
			}
		}
		
	}

	



	/**
	 * Returns count of youth records that match the data export criteria
	 * 
	 * @param dataMap
	 *           the JobDataMap containing the export parameters
	 * @return integer
	 */
	
	private int getCount(JobDataMap dataMap)
	{
		Map<String, String> fields = (Map<String, String>) dataMap.get("fields");
		
		NytdDataTable dataTable = new NytdDataTable(dataExtractionService, fields);

		Collection<String> generalFields = (Collection<String>) dataMap.get("generalFields");
		Collection<String> demographics = (Collection<String>) dataMap.get("demographics");
		Collection<String> characteristics = (Collection<String>) dataMap.get("characteristics");
		Collection<String> services = (Collection<String>) dataMap.get("services");
		Collection<String> outcomes = (Collection<String>) dataMap.get("outcomes");
		Collection<String> demographicsNotes = (Collection<String>) dataMap.get("demographicsNotes");
		Collection<String> characteristicsNotes = (Collection<String>) dataMap.get("characteristicsNotes");
		Collection<String> servicesNotes = (Collection<String>) dataMap.get("servicesNotes");
		Collection<String> outcomesNotes = (Collection<String>) dataMap.get("outcomesNotes");

		Collection<String> reportingPeriods = (Collection<String>) dataMap.get("reportingPeriods");
		Collection<String> states = (Collection<String>) dataMap.get("states");
		Collection<String> populations = (Collection<String>) dataMap.get("populations");
		Collection<String> cohorts = (Collection<String>) dataMap.get("cohorts");

		dataTable.addGeneralFields(generalFields);
		dataTable.addElementFields(demographics);
		dataTable.addElementFields(characteristics);
		dataTable.addElementFields(services);
		dataTable.addElementFields(outcomes);
		dataTable.addNoteFields(demographicsNotes);
		dataTable.addNoteFields(characteristicsNotes);
		dataTable.addNoteFields(servicesNotes);
		dataTable.addNoteFields(outcomesNotes);

		try
		{
			return  dataExtractionService.dataCount(dataTable,
					CommonFunctions.convertCollectionOfStringsToLongs(reportingPeriods),
					CommonFunctions.convertCollectionOfStringsToLongs(states),
					CommonFunctions.convertCollectionOfStringsToLongs(populations),
					CommonFunctions.convertCollectionOfStringsToLongs(cohorts));
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return 0;
		}	
		
		
	}
	
	/**
	 * Returns a DataTable based on the quartz job parameters
	 * 
	 * @param dataMap
	 *           the JobDataMap containing the export parameters
	 * @return DataTable
	 */
	private NytdDataTable createDataTable(JobDataMap dataMap, int pageNumber,int pageSize)
	{
		Map<String, String> fields = (Map<String, String>) dataMap.get("fields");
		
		NytdDataTable dataTable = new NytdDataTable(dataExtractionService, fields);

		Collection<String> generalFields = (Collection<String>) dataMap.get("generalFields");
		Collection<String> demographics = (Collection<String>) dataMap.get("demographics");
		Collection<String> characteristics = (Collection<String>) dataMap.get("characteristics");
		Collection<String> services = (Collection<String>) dataMap.get("services");
		Collection<String> outcomes = (Collection<String>) dataMap.get("outcomes");
		Collection<String> demographicsNotes = (Collection<String>) dataMap.get("demographicsNotes");
		Collection<String> characteristicsNotes = (Collection<String>) dataMap.get("characteristicsNotes");
		Collection<String> servicesNotes = (Collection<String>) dataMap.get("servicesNotes");
		Collection<String> outcomesNotes = (Collection<String>) dataMap.get("outcomesNotes");

		Collection<String> reportingPeriods = (Collection<String>) dataMap.get("reportingPeriods");
		Collection<String> cohorts = (Collection<String>) dataMap.get("cohorts");
		Collection<String> states = (Collection<String>) dataMap.get("states");
		Collection<String> populations = (Collection<String>) dataMap.get("populations");

		dataTable.addGeneralFields(generalFields);
		dataTable.addElementFields(demographics);
		dataTable.addElementFields(characteristics);
		dataTable.addElementFields(services);
		dataTable.addElementFields(outcomes);
		dataTable.addNoteFields(demographicsNotes);
		dataTable.addNoteFields(characteristicsNotes);
		dataTable.addNoteFields(servicesNotes);
		dataTable.addNoteFields(outcomesNotes);

		try
		{
			return (NytdDataTable) dataExtractionService.compileData(dataTable,
					CommonFunctions.convertCollectionOfStringsToLongs(reportingPeriods),
					CommonFunctions.convertCollectionOfStringsToLongs(states),
					CommonFunctions.convertCollectionOfStringsToLongs(populations),
					CommonFunctions.convertCollectionOfStringsToLongs(cohorts),
					pageNumber,pageSize);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a data export file on the file system
	 * 
	 * @param dataTable
	 *           the export data to write
	 * @param user
	 *           the logged in user
	 * @param format
	 *           the file format
	 * @param fileName
	 *           the file name
	 * @param hostAndPort
	 *           the host and port used to create a html link to the created file
	 */
	private void createFile(NytdDataTable dataTable, SiteUser user, String format, String fileName,
			String hostAndPort,boolean startOfRecs, boolean endOfRecs) throws URISyntaxException
	{
		InputStream inputStream = null;
		String finalFileName = getExportLocation() + fileName;
		String tempFileName = finalFileName + ".tmp";
		File tempFile = new File(new URI(tempFileName));
		File finalFile = new File(new URI(finalFileName));

		if (format.equalsIgnoreCase(Constants.SAV_EXT))
		{
			inputStream = dataExtractionService.getSPSSFile(dataTable,startOfRecs,endOfRecs);
		}
		else if (format.equalsIgnoreCase(Constants.CSV_EXT))
		{
			inputStream = dataExtractionService.getCSVFile(dataTable,startOfRecs,endOfRecs);
		}
		else if (format.equalsIgnoreCase(Constants.TXT_EXT))
		{
			inputStream = dataExtractionService.getRSTFile(dataTable,startOfRecs,endOfRecs);
		}
		else if (format.equalsIgnoreCase(Constants.HTM_EXT))
		{
			inputStream = dataExtractionService.getHTMFile(dataTable,startOfRecs,endOfRecs);
		}
		else if (format.equalsIgnoreCase(Constants.XLS_EXT))
		{
			inputStream = dataExtractionService.getXLSFile(dataTable,startOfRecs,endOfRecs);
		}
		else
		{
			assert(false);  // Unknown file type!
		}

		writeToFile(inputStream, tempFile);
		if (endOfRecs)
		{
			tempFile.renameTo(finalFile);
			// notify user
			Map<String, Object> namedParams = new HashMap<String, Object>();
			//	namedParams.put("dataExportLink", hostAndPort + fileName);
			namedParams.put("dataExportLink", getRootURL() + getDownloadaction() + fileName);
			namedParams.put("firstName", user.getFirstName());
			namedParams.put("lastName", user.getLastName());
			Message dataExportMsg = messageServiceP3.createSystemMessage(
					MessageService.DATA_EXPORT_NOTIFICATION, namedParams);
			List<SiteUser> recipients = new ArrayList<SiteUser>();
			recipients.add(user);
			messageServiceP3.sendSystemMessage(dataExportMsg, recipients);
		}
	}

	/**
	 * Returns a String file extension based on a numeric file type
	 * 
	 * @param fileType
	 *           the file type
	 * @return a file extension string
	 */
	private String getFileFormat(String fileType)
	{
		String format = null;

		if (fileType.equals(Constants.SAV_KEY))
			format = Constants.SAV_EXT;
		else if (fileType.equals(Constants.CSV_KEY))
			format = Constants.CSV_EXT;
		else if (fileType.equals(Constants.XLS_KEY))
			format = Constants.XLS_EXT;
		else if (fileType.equals(Constants.HTM_KEY))
			format = Constants.HTM_EXT;
		else if (fileType.equals(Constants.TXT_KEY))
			format = Constants.TXT_EXT;

		return format;
	}

	/**
	 * Writes data export file to file system with an InputStream and File
	 * 
	 * @param is
	 *           the InputStream to write to the file
	 * @param file
	 *           the File object to create
	 */
	private void writeToFile(InputStream is, File file)
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			OutputStream out = new FileOutputStream(file,true);
			BufferedOutputStream bos = new BufferedOutputStream(out);
			OutputStreamWriter osw = new OutputStreamWriter(bos);
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
			{
				
				 bos.write(buf, 0, len);
				
			}
				
				
				
		//	osw.flush();
		//	out.close();
			osw.close();
		//	is.close();
			isr.close();
		}
		catch (IOException e)
		{
			log.error("Error Writing/Reading Streams.", e);
		}
	}

	/**
	 * @param dataExtractionService
	 *           the dataExtractionService to set
	 */
	public void setDataExtractionService(DataExtractionService dataExtractionService)
	{
		this.dataExtractionService = dataExtractionService;
	}

	/**
	 * @param messageServiceP3
	 *           the messagingService to set
	 */
	public void setMessageServiceP3(MessageService messageServiceP3)
	{
		this.messageServiceP3 = messageServiceP3;
	}

	/**
	 * @return the exportLocation
	 */
	public String getExportLocation()
	{
		return exportLocation;
	}

	/**
	 * @param exportLocation
	 *           the exportLocation to set
	 */
	public void setExportLocation(String exportLocation)
	{
		this.exportLocation = exportLocation;
	}

	/**
	 * @return the rootURL
	 */
	public String getRootURL()
	{
		return rootURL;
	}

	/**
	 * @param rootURL the rootURL to set
	 */
	public void setRootURL(String rootURL)
	{
		this.rootURL = rootURL;
	}

	/**
	 * @return the downloadaction
	 */
	public static String getDownloadaction()
	{
		return downloadAction;
	}
}
