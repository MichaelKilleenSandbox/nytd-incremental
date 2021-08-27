/**
 * Filename: DataExportAction.java
 * 
 * Copyright 2009, ICF International Created: Aug 7, 2009 Author: 18816
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
package gov.hhs.acf.cb.nytd.actions.dataextraction;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.validator.annotations.*;
import gov.hhs.acf.cb.nytd.models.Element;
import gov.hhs.acf.cb.nytd.models.ExportMetadata;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.NytdDataTable;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.*;


/**
 * Exports data from the database into the desired format.
 * 
 * @author Adam Russell (18816)
 */
public class DataExportAction extends ActionSupport implements SessionAware, ApplicationAware,
		ApplicationContextAware, ParameterAware, ServletRequestAware, ValidationAware, Preparable
{
	@Getter @Setter private DataExtractionService dataExtractionService;
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
//	@Getter @Setter private ComplianceService complianceService;
	
	@Getter private NytdDataTable dataTable;
	@Getter private InputStream inputStream;
	@Getter private String filename;
	@Getter @Setter private String userFilename;
	@Getter private String format = null;
	@Getter private String contentType;
	@Getter private Map<String, Object> reportParameters = new HashMap<String, Object>();
	@Getter @Setter private Map<String, String> availableFileTypes;
	@Getter @Setter private Map<String, String> availableReportingPeriods;
	@Getter @Setter private Map<String, String> availableStates;
	@Getter @Setter private Map<String, String> availablePopulations;
	@Getter @Setter private Map<String, String> availableFields;
	@Getter @Setter private Map<String, String> availableGeneralFields;
	@Getter @Setter private Map<String, String> availableDemographics;
	@Getter @Setter private Map<String, String> availableCharacteristics;
	@Getter @Setter private Map<String, String> availableServices;
	@Getter @Setter private Map<String, String> availableOutcomes;
	@Getter @Setter private Map<String, String> availableDemographicsNotes;
	@Getter @Setter private Map<String, String> availableCohorts;
	@Getter @Setter private String defaultFileType;
	@Getter @Setter private Map<String, String> defaultCohorts;
	@Getter @Setter private Map<String, String> defaultReportingPeriods;
	@Getter @Setter private Map<String, String> defaultStates;
	@Getter @Setter private Collection<String> defaultPopulations;
	@Getter @Setter private Collection<String> defaultFields;
	@Getter @Setter private Collection<String> defaultGeneralFields;
	@Getter @Setter private Collection<String> defaultDemographics;
	@Getter @Setter private Collection<String> defaultCharacteristics;
	@Getter @Setter private Collection<String> defaultServices;
	@Getter @Setter private Collection<String> defaultOutcomes;
	@Getter @Setter private Collection<String> defaultDemographicsNotes;
	@Getter @Setter private Collection<String> defaultCharacteristicsNotes;
	@Getter @Setter private Collection<String> defaultServicesNotes;
	@Getter @Setter private Collection<String> defaultOutcomesNotes;
	@Getter @Setter private String selectedSubmission;
	@Getter @Setter private String fileType;
	@Getter @Setter private Collection<String> cohorts;
	@Getter @Setter private Collection<String> reportingPeriods;
	@Getter @Setter private Collection<String> states;
	@Getter @Setter private Collection<String> populations;
	@Getter @Setter private Collection<String> generalFields;
	@Getter @Setter private Collection<String> demographics;
	@Getter @Setter private Collection<String> characteristics;
	@Getter @Setter private Collection<String> services;
	@Getter @Setter private Collection<String> outcomes;
	@Getter @Setter private Collection<String> demographicsNotes;
	@Getter @Setter private Collection<String> characteristicsNotes;
	@Getter @Setter private Collection<String> servicesNotes;
	@Getter @Setter private Collection<String> outcomesNotes;
	@Getter @Setter private String exportFilename;
	@Getter @Setter private String exportLocation;
	@Getter @Setter private List<ExportMetadata> previousExports;
	@Getter @Setter private String anchor;
	@Getter @Setter private boolean javaScriptEnabled;
	@Setter private Map<String, Object> session;
	@Setter private Map<String, Object> application;
	protected Logger log = Logger.getLogger(getClass());
	@Setter private ApplicationContext applicationContext;
	@Setter private HttpServletRequest servletRequest;
	@Setter private Map<String, String[]> parameters;
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepare() throws Exception
	{
		SiteUser siteUser = (SiteUser) session.get("siteUser");
		this.setJavaScriptEnabled(false);
		availableFileTypes = dataExtractionService.getFileTypes();
		availableReportingPeriods = dataExtractionService.getReportingPeriods(siteUser);
		Map<String,String> FYRP = new LinkedHashMap<String, String>();
		String key = null;
		String value = null;
		//Following for loop gets a map of Reporting years out of reporting periods map.
		for(Map.Entry<String,String> entry : availableReportingPeriods.entrySet())
		{
			if(value != null && value.equals(entry.getValue().substring(0, 4)))
			{
				StringBuffer strKey = new StringBuffer(key).append('-').append(entry.getKey());
				FYRP.put(strKey.toString(), entry.getValue().substring(0, 4));
			}
				value = entry.getValue().substring(0, 4);
				key = entry.getKey();
		}
		availableReportingPeriods.putAll(FYRP);
		if(servletRequest.getParameterValues("reportingPeriods") != null)
		{
			String [] rps =  servletRequest.getParameterValues("reportingPeriods");
			defaultReportingPeriods = new HashMap<String, String>(0);
			for(int i=0 ; i< rps.length;i++)
			  {
				  defaultReportingPeriods.put(rps[i].toString(), availableReportingPeriods.get(rps[i].toString()));
				  availableReportingPeriods.remove(rps[i].toString());
			  }
			
		}
		else
		{
			defaultReportingPeriods = new HashMap<String, String>(0);
		}
		availableCohorts = dataExtractionService.getCohorts();
		if(servletRequest.getParameterValues("cohorts") != null)
		{
			String [] chts =  servletRequest.getParameterValues("cohorts");
			defaultCohorts = new HashMap<String, String>(0);
			for(int i=0 ; i< chts.length;i++)
			  {
				defaultCohorts.put(chts[i].toString(), availableCohorts.get(chts[i].toString()));
				availableCohorts.remove(chts[i].toString());
			  }
		}
		else
		{
			defaultCohorts = new HashMap<String, String>(0);
		}
		availableStates = dataExtractionService.getStates(siteUser);
		if(servletRequest.getParameterValues("states") != null)
		{
			String [] stts =  servletRequest.getParameterValues("states");
			defaultStates = new HashMap<String, String>(0);
			for(int i=0 ; i< stts.length;i++)
			  {
				defaultStates.put(stts[i].toString(), availableStates.get(stts[i].toString()));
				availableStates.remove(stts[i].toString());
			  }
			
		}
		else
		{
			defaultStates = new HashMap<String, String>(0);
		}
		availablePopulations = dataExtractionService.getPopulations();
		previousExports = dataExtractionService.getPreviousDataExports();

		if (application.containsKey("dataFields"))
		{
			availableFields = (Map<String, String>) application.get("dataFields");
		}
		else
		{
			dataExtractionService.setElementsList((ArrayList<Element>)application.get(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN));
			availableFields = dataExtractionService.getFields();
			application.put("dataFields", availableFields);
		}
		if (application.containsKey("dataGeneralFields"))
		{
			availableGeneralFields = (Map<String, String>) application.get("dataGeneralFields");
		}
		else
		{
			availableGeneralFields = dataExtractionService.getGeneralFields();
			application.put("dataGeneralFields", availableGeneralFields);
		}
		if (application.containsKey("dataDemographicFields"))
		{
			availableDemographics = (Map<String, String>) application.get("dataDemographicFields");
		}
		else
		{
		//	availableDemographics = dataExtractionService.getDemographics(availableFields);
			availableDemographics = dataExtractionService.getDemographics();
			application.put("dataDemographicFields", availableDemographics);
		}
		if (application.containsKey("dataCharacteristicFields"))
		{
			availableCharacteristics = (Map<String, String>) application.get("dataCharacteristicFields");
		}
		else
		{
			//availableCharacteristics = dataExtractionService.getCharacteristics(availableFields);
			availableCharacteristics = dataExtractionService.getCharacteristics();
			application.put("dataCharacteristicFields", availableCharacteristics);
		}
		if (application.containsKey("dataServiceFields"))
		{
			availableServices = (Map<String, String>) application.get("dataServiceFields");
		}
		else
		{
		//	availableServices = dataExtractionService.getServices(availableFields);
			availableServices = dataExtractionService.getServices();
			application.put("dataServiceFields", availableServices);
		}
		if (application.containsKey("dataOutcomeFields"))
		{
			availableOutcomes = (Map<String, String>) application.get("dataOutcomeFields");
		}
		else
		{
	//		availableOutcomes = dataExtractionService.getOutcomes(availableFields);
			availableOutcomes = dataExtractionService.getOutcomes();
			application.put("dataOutcomeFields", availableOutcomes);
		}
		if (application.containsKey("dataDemographicFieldNotes"))
		{
			availableDemographicsNotes = (Map<String, String>) application.get("dataDemographicFieldNotes");
		}
		else
		{
		//	availableDemographicsNotes = new LinkedHashMap<String, String>(dataExtractionService.getDemographics(availableFields));
			availableDemographicsNotes = new LinkedHashMap<String, String>(dataExtractionService.getDemographics());
			availableDemographicsNotes.remove("1");
			availableDemographicsNotes.remove("2");
			application.put("dataDemographicFieldNotes", availableDemographicsNotes);
		}
		
		if(servletRequest.getParameter("fileType") != null)
			defaultFileType = servletRequest.getParameter("fileType");
		else
			defaultFileType = availableFileTypes.keySet().iterator().next();
		if(servletRequest.getParameterValues("populations") != null)
		{
			String[] pops = servletRequest.getParameterValues("populations");
			defaultPopulations  = new ArrayList<String>();
			for (int i= 0; i < pops.length; i++)
				defaultPopulations.add(pops[i]);
		}
		else
		defaultPopulations = availablePopulations.keySet();
		defaultGeneralFields = availableGeneralFields.keySet();
		if(servletRequest.getParameterValues("demographics") != null)
		{
			String[] defaultDems = servletRequest.getParameterValues("demographics");
			defaultDemographics  = new ArrayList<String>();
			for (int i= 0; i < defaultDems.length; i++)
			{
				
				defaultDemographics.add(defaultDems[i]);
			}
				
		}
		else
		defaultDemographics = availableDemographics.keySet();
		if(servletRequest.getParameterValues("characteristics") != null)
		{
			String[] character = servletRequest.getParameterValues("characteristics");
			defaultCharacteristics  = new ArrayList<String>();
			for (int i= 0; i < character.length; i++)
			{
				
				defaultCharacteristics.add(character[i]);
			}
				
		}
		else
		defaultCharacteristics = availableCharacteristics.keySet();
		if(servletRequest.getParameterValues("services") != null)
		{
			String[] serv = servletRequest.getParameterValues("services");
			defaultServices  = new ArrayList<String>();
			for (int i= 0; i < serv.length; i++)
			{
				
				defaultServices.add(serv[i]);
			}
				
		}
		else
		defaultServices = availableServices.keySet();
		if(servletRequest.getParameterValues("outcomes") != null)
		{
			String[] outcomes = servletRequest.getParameterValues("outcomes");
			defaultOutcomes  = new ArrayList<String>();
			for (int i= 0; i < outcomes.length; i++)
			{
				
				defaultOutcomes.add(outcomes[i]);
			}
				
		}
		else
		defaultOutcomes = availableOutcomes.keySet();
		if(servletRequest.getParameterValues("demographicsNotes") != null)
		{
			String[] demoNotes = servletRequest.getParameterValues("demographicsNotes");
			defaultDemographicsNotes = new LinkedList<String>();
			for (int i= 0; i < demoNotes.length; i++)
			{
				
				defaultDemographicsNotes.add(demoNotes[i]);
			}
				
		}
		else
			defaultDemographicsNotes = new LinkedList<String>();
		if(servletRequest.getParameterValues("characteristicsNotes") != null)
		{
			String[] charNotes = servletRequest.getParameterValues("characteristicsNotes");
			defaultCharacteristicsNotes = new LinkedList<String>();
			for (int i= 0; i < charNotes.length; i++)
			{
				
				defaultCharacteristicsNotes.add(charNotes[i]);
			}
				
		}
		else
			defaultCharacteristicsNotes = new LinkedList<String>();
		if(servletRequest.getParameterValues("servicesNotes") != null)
		{
			String[] serNotes = servletRequest.getParameterValues("servicesNotes");
			defaultServicesNotes = new LinkedList<String>();
			for (int i= 0; i < serNotes.length; i++)
			{
				
				defaultServicesNotes.add(serNotes[i]);
			}
				
		}
		else
			defaultServicesNotes = new LinkedList<String>();
		if(servletRequest.getParameterValues("outcomesNotes") != null)
		{
			String[] outcomeNotes = servletRequest.getParameterValues("outcomesNotes");
			defaultOutcomesNotes = new LinkedList<String>();
			for (int i= 0; i < outcomeNotes.length; i++)
			{
				
				defaultOutcomesNotes.add(outcomeNotes[i]);
			}
				
		}
		else
		defaultOutcomesNotes = new LinkedList<String>();
	}

	@SkipValidation
	public final String getCriteria()
	{	
		return SUCCESS;
	}
	
	@SkipValidation
	public final String resetCriteria()
	{
		return SUCCESS;
	}

	/**
	 * Exports the data, including only the selected fields.
	 * 
	 * This method does not attempt to set the field selection to default.
	 * Constants.BATCH results in redirectAction to batchExportData()
	 * Constants.SERIAL results in redirectAction to exportData()
	 * 
	 * @return Constants.BATCH if data export kicks off a batch quartz job
	 *         Constants.SERIAL user waits for data export
	 */
	@Validations(
		requiredFields={
			@RequiredFieldValidator(fieldName="states", message="You must select at least one state."),
			
			@RequiredFieldValidator(fieldName="reportingPeriods",
			                        message="You must select at least one report period."),
			@RequiredFieldValidator(fieldName="populations", message="You must select at least one population.")},
		requiredStrings={
			@RequiredStringValidator(fieldName="fileType", message="You must enter a file type.")},
		expressions={
			@ExpressionValidator(message="You must select at least one state.",
			                     expression="!states.isEmpty()"),
			@ExpressionValidator(message="You must select at least one report period.",
			                     expression="!reportingPeriods.isEmpty()"),
			@ExpressionValidator(message="You must select at least one population.",
			                     expression="!populations.isEmpty()")},
		regexFields={
				@RegexFieldValidator(message = "File Name is allowed to contain any combination of characters \"Aa-Zz\",\"0-9\",\'_\' and \'-\' only",fieldName="userFilename", shortCircuit = true,regex = "[a-zA-Z0-9\\-\\_]*")}
		
	)
			                     
	public final String doExport()
	{
		addExportParamsToSession();

		if (fileType.equalsIgnoreCase(Constants.XLS_KEY))
		{
			boolean overLimit = false;
			if (this.getReportingPeriods().size() > 2)
			{
				overLimit = true;
				addActionError("HTM and XLS exports can have a maximum of two reporting periods.");
			}
			if (this.getStates().size() > 1)
			{
				overLimit = true;
				addActionError("HTM and XLS exports can have a maximum of one state.");
			}
			if (overLimit)
			{
				return Action.INPUT;
			}
		}
		
		return SUCCESS;
	}

	/**
	 * Starts a quartz job that exports the data, including only the selected
	 * fields.
	 * 
	 * This method does not attempt to set the field selection to default.
	 * 
	 * @return Action.SUCCESS on success; Action.ERROR otherwise
	 */
	@SuppressWarnings("unchecked")
	@SkipValidation
	public final String batchExportData()
	{
		getExportParamsFromSession();
		Scheduler scheduler = (Scheduler) applicationContext.getBean("nytdScheduler");
		try
		{
			if (!scheduler.isStarted())
			{
				scheduler.start();
			}
			
			// get available data export fields
			Map<String, String> fields;
			if (application.containsKey("dataFields"))
			{
				fields = (Map<String, String>) application.get("dataFields");
			}
			else
			{
				fields = dataExtractionService.getFields();
				application.put("dataFields", fields);
			}

			// determine file format
			String fileFormat = "";
			if (getFileType().equals(Constants.SAV_KEY))
				fileFormat = Constants.SAV_EXT;
			else if (getFileType().equals(Constants.CSV_KEY))
				fileFormat = Constants.CSV_EXT;
			else if (getFileType().equals(Constants.XLS_KEY))
				fileFormat = Constants.XLS_EXT;
			else if (getFileType().equals(Constants.HTM_KEY))
				fileFormat = Constants.HTM_EXT;
			else if (getFileType().equals(Constants.TXT_KEY))
				fileFormat = Constants.TXT_EXT;

			// create export file name
			StringBuffer efn = new StringBuffer();
			SiteUser siteUser = (SiteUser) session.get("siteUser");
			Date date = new Date();
			if(userFilename!=null && !userFilename.trim().isEmpty())
			{
				efn.append(userFilename.trim().replaceAll(" ", "")).append(".").append(
						fileFormat);
			}
			else
			{
				efn.append(siteUser.getUserName()).append("_export_").append(date.getTime()).append(".").append(
					fileFormat);
			}
			exportFilename = efn.toString();
			
			//Handling Reporting Years - converting selected reporting years into corresponding reporting periods
			Collection<String> rpFixed = new ArrayList<String>();
			for(String val : getReportingPeriods())
			{
				if(val.contains("-"))
				{
					String[] tkns = val.split("-");
					for (String tkn : tkns)
					{
					//	rpFixed.addAll(java.util.Arrays.asList(tkns));
						if(!rpFixed.contains(tkn))
								rpFixed.add(tkn);
					}
				}
				else if(!rpFixed.contains(val))
					rpFixed.add(val);
			}

			// get quartz job bean
			JobDetail dataExportJobDetail = (JobDetail) applicationContext.getBean("dataExportJob");
			//dataExportJobDetail.setDurability(true);
			dataExportJobDetail.isDurable();
			//String exportJobName = dataExportJobDetail.getName();
			//String exportJobGroup = dataExportJobDetail.getGroup();
			JobKey jobKey = dataExportJobDetail.getKey();

			// prepare quartz job parameters
			JobDataMap dataMap = dataExportJobDetail.getJobDataMap();
			dataMap.put("generalFields", getGeneralFields());
			dataMap.put("demographics", getDemographics());
			dataMap.put("characteristics", getCharacteristics());
			dataMap.put("services", getServices());
			dataMap.put("outcomes", getOutcomes());
			dataMap.put("demographicsNotes", getDemographicsNotes());
			dataMap.put("characteristicsNotes", getCharacteristicsNotes());
			dataMap.put("servicesNotes", getServicesNotes());
			dataMap.put("outcomesNotes", getOutcomesNotes());
			//dataMap.put("reportingPeriods", getReportingPeriods());
			dataMap.put("reportingPeriods", rpFixed);
			dataMap.put("cohorts", getCohorts());
			dataMap.put("states", getStates());
			dataMap.put("populations", getPopulations());
			// dataMap.put("populationSelector", getPopulationSelector());
			dataMap.put("fileType", getFileType());
			dataMap.put("fileName", exportFilename);
			dataMap.put("siteUser", (SiteUser) session.get("siteUser"));
			StringBuffer hostAndPort = new StringBuffer();
			hostAndPort.append("http://").append(servletRequest.getServerName()).append(":").append(
					servletRequest.getServerPort()).append(servletRequest.getContextPath()).append(
					"/downloadExportFile.action?downloadFilename=");
			dataMap.put("hostAndPort", hostAndPort.toString());
			dataMap.put("fields", fields);

			// add job to scheduler
			scheduler.addJob(dataExportJobDetail, true);

			log.debug("trigger export job");
			//scheduler.triggerJob(exportJobName, exportJobGroup, dataMap);
			scheduler.triggerJob(jobKey, dataMap);
		//	dataExtractionService.writeExportMetadata(fields, dataMap);
			dataExtractionService.writeExportMetadata(dataMap);
			log.debug("export job triggered");
		}
		catch (SchedulerException e)
		{
			log.error(e.getMessage(), e);
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	/**
	 * Stores export job parameters in a HashMap and puts it into the user
	 * session
	 * 
	 * This method is necessary because an actionRedirect causes export
	 * parameters on the initial form submit to be lost. This method is used in
	 * conjunction with getExportParamsFromSession(), which should be called in
	 * the redirected action.
	 * 
	 */
	public void addExportParamsToSession()
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("populations", getPopulations());
		map.put("generalFields", getGeneralFields());
		map.put("demographics", getDemographics());
		map.put("characteristics", getCharacteristics());
		map.put("services", getServices());
		map.put("outcomes", getOutcomes());
		map.put("demographicsNotes", getDemographicsNotes());
		map.put("characteristicsNotes", getCharacteristicsNotes());
		map.put("servicesNotes", getServicesNotes());
		map.put("outcomesNotes", getOutcomesNotes());
		map.put("reportingPeriods", getReportingPeriods());
		map.put("cohorts", getCohorts());
		map.put("states", getStates());
		map.put("fileType", fileType);
		map.put("userFileName", getUserFilename());
		session.put("dataExportParameters", map);
	}

	/**
	 * Retrieves export job parameters from user session
	 * 
	 * This method is necessary because an actionRedirect causes export
	 * parameters on the initial form submit to be lost. This method is used in
	 * conjunction with addExportParamsToSession(), which should be called before
	 * actionRedirect.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void getExportParamsFromSession()
	{
		HashMap<String, Object> map = (HashMap<String, Object>) session.get("dataExportParameters");
		if (map != null)
		{
			setPopulations((Collection<String>) map.get("populations"));
			setGeneralFields((Collection<String>) map.get("generalFields"));
			setDemographics((Collection<String>) map.get("demographics"));
			setCharacteristics((Collection<String>) map.get("characteristics"));
			setServices((Collection<String>) map.get("services"));
			setOutcomes((Collection<String>) map.get("outcomes"));
			setDemographicsNotes((Collection<String>) map.get("demographicsNotes"));
			setCharacteristicsNotes((Collection<String>) map.get("characteristicsNotes"));
			setServicesNotes((Collection<String>) map.get("servicesNotes"));
			setOutcomesNotes((Collection<String>) map.get("outcomesNotes"));
			setReportingPeriods((Collection<String>) map.get("reportingPeriods"));
			setCohorts((Collection<String>) map.get("cohorts"));
			setStates((Collection<String>) map.get("states"));
			setFileType((String) map.get("fileType"));
			setUserFilename((String)map.get("userFileName"));
		}
	}

	/**
	 * Downloads a data export file that exists on the file system.
	 * 
	 * @return Action.SUCCESS on success; Action.ERROR otherwise
	 */
	@SkipValidation
	public String downloadExportFile()
	{
		String result = Action.ERROR;
		String[] filenameVals = parameters.get("downloadFilename");
		if (filenameVals != null && filenameVals.length > 0)
		{
			String downloadFileName = filenameVals[0];
			setExportFilename(downloadFileName);
			String format = downloadFileName.substring(downloadFileName.indexOf('.'));
			String fullFileUrl = exportLocation + downloadFileName;

			try
			{
				File fileToDownload = new File(new URI(fullFileUrl));
				if (!fileToDownload.exists())
				{
					File tempFile = new File(new URI(fullFileUrl + ".tmp"));
					if (tempFile.exists())
					{
						return Constants.CREATINGFILE;
					}
					else
					{
						return Action.ERROR;
					}
				}
				inputStream = new FileInputStream(fileToDownload);
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
				result = Action.ERROR;
			}

			if (format.equalsIgnoreCase(Constants.SAV_EXT))
			{
				contentType = "application/x-spss";
			}
			else if (format.equalsIgnoreCase(Constants.CSV_EXT))
			{
				contentType = "text/csv";
			}
			else if (format.equalsIgnoreCase(Constants.TXT_EXT))
			{
				contentType = "text/plain";
			}
			else if (format.equalsIgnoreCase(Constants.HTM_EXT))
			{
				contentType = "text/html";
			}
			else if (format.equalsIgnoreCase(Constants.XLS_EXT))
			{
				contentType = "application/vnd.ms-excel";
			}
			result = Action.SUCCESS;
		}

		return result;
	}
}
