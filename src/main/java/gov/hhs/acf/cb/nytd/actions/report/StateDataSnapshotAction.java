package gov.hhs.acf.cb.nytd.actions.report;

//import gov.hhs.acf.cb.nytd.actions.ExportableTable;

import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.service.ReportsService;
import gov.hhs.acf.cb.nytd.util.Constants;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateDataSnapshotAction extends SearchAction<StateDataSnapshotSearch>
{

	@Getter	@Setter private StateDataSnapshotSearch search;
	@Getter @Setter private Map<String, String> searchCriteria;
	@Getter @Setter private Map<String, String> availableStates;
	@Getter @Setter private Map<String, String> availableFiscalYears;
	@Getter @Setter private Map<String, String> availableReportPeriods;
	@Getter @Setter private Map<String, String> availablePopulationTypes;
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
	@Getter @Setter private ReportsService reportsService;
	@Getter @Setter private StringBuffer reportFileName;
	@Getter @Setter private boolean isReportAvailable = true; 

	protected Logger log = Logger.getLogger(getClass());

	public void prepare()
	{
		super.prepare();

		// when using the tab navigation the search object is not created
		if (search == null)
		{
			search = new StateDataSnapshotSearch();
		}
		SiteUser siteUser = (SiteUser) session.get("siteUser");
		availableStates = populateSearchCriteriaService.getStateSelectMapForUser(siteUser);
		availableFiscalYears = populateSearchCriteriaService.getFiscalYearSelectMapForUser(siteUser);
		availableReportPeriods = populateSearchCriteriaService.getReportPeriodDescSelectMap();
		availablePopulationTypes = populateSearchCriteriaService.getPopulationTypeSelectMap();
	}

	@Override
	protected StateDataSnapshotSearch getPaginatedSearch()
	{
		return getSearch();
	}
	
	/**
	 * Displays State Data Snapshot Selection page.
	 * 
	 * @return Action.SUCCESS on success
	 */
	public final String getStateDataSnapshotSearchPage()
	{
		log.debug("StateDataSnapshotAction.getStateDataSnapshotSearchPage");
		
		SiteUser siteUser = (SiteUser)session.get("siteUser");
		search.setSiteUser(siteUser);
		
//		setDefaultPage(true);
//		search.reset();
		return SUCCESS;
	}
	
	/**
	 * Displays State Data Snapshot.
	 * 
	 * @return Action.SUCCESS on success
	 */
	public final String getStateDataSnapshot()
	{
		log.debug("StateDataSnapshotAction.getStateDataSnapshot");
		Statereport stateReport = null;
		if(search.getSelectedState() == -1 || search.getSelectedFiscalYear() == -1
				|| search.getSelectedReportPeriod() == -1 || search.getSelectedPopulationType() == -1)
		{
			addActionError("One or more fields missing data. All fields below are required and need to be selected.");
			return "input";
		}
		
		log.debug("Selected State is: "+ search.getSelectedState());
		log.debug("Selected FiscalYear is: "+ search.getSelectedFiscalYear());
		log.debug("Selected ReportPeriod is: "+ search.getSelectedReportPeriod());
		log.debug("Selected PopulationType is: "+ search.getSelectedPopulationType());
		log.debug("Selected ReportFormat is: "+ search.getSelectedReportFormat());
		
		stateReport = fetchServedReportObject();
		
		if(stateReport == null)
		{
			addActionError("Requested report is not available at this time. Please try later.");
			return "input";
		}
		
		// Jasper Reports
		switch(search.getSelectedPopulationType())
		{
			case 1:{
				generateServedReport();
				break;
			}
			case 2:{
				generateOutcomesReport(stateReport);
				break;
			}
		}	

		if(!isReportAvailable())
		{
			addActionError("Requested report is not available at this time. Please try later.");
			return "input";
		}
		
		return null;
	}
	
	private Statereport fetchServedReportObject()
	{
		Statereport stateReport = null;
		switch(search.getSelectedPopulationType())
		{
			case 1:{
				stateReport = getReportsService().getStateReport
				(search.getSelectedState(),getSelectedFiscalYear(),getSelectedRpLetter(search.getSelectedReportPeriod()),"Served");
				break;
			}
			case 2:{
				stateReport = getReportsService().getStateReport
				(search.getSelectedState(),getSelectedFiscalYear(),getSelectedRpLetter(search.getSelectedReportPeriod()),getOutcomesPopulationTypeOfSelectedFiscalYear());
				break;
			}
		}	
		return stateReport;
	}
	
	public void generateServedReport()
	{
		Statereport statereport = null;
		statereport = getReportsService().getStateReport
		(search.getSelectedState(),getSelectedFiscalYear(),getSelectedRpLetter(search.getSelectedReportPeriod()),"Served");
		generateServedReport(statereport);
	}
	
	public void generateServedReport(Statereport statereport)
	{
	//	Statereport statereport = null;
		Map<String,Object> reportParams = new HashMap<String, Object>();
		JRBeanCollectionDataSource beanCollectionDataSource = null;
		InputStream inputStream = null;
		reportFileName = new StringBuffer();
		ArrayList<Object> results = new ArrayList<Object>();
		/*if(statereport == null)
		statereport = getReportsService().getStateReport
				(search.getSelectedState(),getSelectedFiscalYear(),getSelectedRpLetter(search.getSelectedReportPeriod()),"Served");*/
		reportFileName.append(reportsService.getAbbrByStateId(search.getSelectedState()))
		.append("_")
		.append(getSelectedFiscalYear())
		.append(getSelectedRpLetter(search.getSelectedReportPeriod()))
		.append("_")
		.append("Served");
		if(statereport == null)
		{
			isReportAvailable = false;
		}
		
		if(statereport != null) {
			statereport.setServedHeaderData(getReportsService().getSDPServedHeaderData(statereport.getStatereportid()));

			if(statereport.getStatereportservicesreceived() == null) {
				statereport.setStatereportservicesreceived(new Statereportservicesreceived());
			} else {
				statereport.getStatereportservicesreceived().populatePieChart();
			}
			if(statereport.getStatereporteduleveldetail() == null) {
				statereport.setStatereporteduleveldetail(new Statereporteduleveldetail());
			} else {
				statereport.getStatereporteduleveldetail().populateBarChart();
			}
			if(statereport.getStatereportservicetypes() == null) {
				statereport.setStatereportservicetypes(new Statereportservicetypes());
			} else {
				statereport.getStatereportservicetypes().populateBarChart();
			}
				
			inputStream = getClass().getResourceAsStream("/jasperreports/SDPServed.jasper");
			results.add(statereport);
			beanCollectionDataSource = new JRBeanCollectionDataSource(results);
			reportParams.put("HEADERNYTDIMAGE", "/jasperreports/");
			reportParams.put("REPORTFILENAME", reportFileName.toString());
		
			if (search.getSelectedReportFormat() == 1){
				exportPdfReport(inputStream,reportParams, beanCollectionDataSource );
			} else {
				exportDocxReport(inputStream,reportParams, beanCollectionDataSource );
			}				
		}
		
	}
	
	private void generateOutcomesReport(Statereport statereport)
	{
	//	Statereport statereport = null;
		Map<String,Object> reportParams = new HashMap<String, Object>();
		JRBeanCollectionDataSource beanCollectionDataSource = null;
		InputStream inputStream = null;
		reportFileName = new StringBuffer();

		ArrayList<Object> results = new ArrayList<Object>();
		/*statereport = getReportsService().getStateReport
				(search.getSelectedState(),getSelectedFiscalYear(),getSelectedRpLetter(search.getSelectedReportPeriod()),getOutcomesPopulationTypeOfSelectedFiscalYear());*/
		reportFileName.append(reportsService.getAbbrByStateId(search.getSelectedState()))
					.append("_")
					.append(getSelectedFiscalYear())
					.append(getSelectedRpLetter(search.getSelectedReportPeriod()))
					.append("_")
					.append(getOutcomesPopulationTypeOfSelectedFiscalYear());
		if(statereport == null)
		{
			isReportAvailable = false;
		}
		
		if(statereport != null)
			{
				statereport.setOutcomesHeaderData(getReportsService().getSDPOutcomesHeaderData(statereport.getStatereportid()));
				if(statereport.getStatereportservicesreceived() == null)
					statereport.setStatereportservicesreceived(new Statereportservicesreceived());
				else
					statereport.getStatereportservicesreceived().populatePieChart();
				
				
				if(statereport.getPopulationtype()!=null && statereport.getPopulationtype().equalsIgnoreCase("Baseline"))
				{
					if(statereport.getStatereportdemographics() !=null)
						statereport.getStatereportdemographics().generateSDPDemographicChart();
					else
						statereport.setStatereportdemographics(new Statereportdemographics());
					
					if(statereport.getStatereportreasonnonparti() !=null)
						statereport.getStatereportreasonnonparti().generateNonParticipationReasonsChart();
					else
						statereport.setStatereportreasonnonparti(new Statereportreasonnonparti());
					
					if(statereport.getStatereportoutcomes()!=null)
						statereport.getStatereportoutcomes().generateOutcomesChart();
					else
						statereport.setStatereportoutcomes(new Statereportoutcomes());
					
					inputStream = getClass().getResourceAsStream("/jasperreports/SDPFBaseline.jasper");
				}
				else
				{
				
					if(statereport.getVwsdpfollowupdemographics()!=null)
						statereport.getVwsdpfollowupdemographics().generateSDPDemographicChart();
					else
						statereport.setVwsdpfollowupdemographics(new Vwsdpfollowupdemographics());
					
					if(statereport.getVwsdpfollowupnonpartreasons()!=null)
						statereport.getVwsdpfollowupnonpartreasons().generateNonParticipationReasonsChart();
					else
						statereport.setVwsdpfollowupnonpartreasons(new Vwsdpfollowupnonpartreasons());
					
					if(statereport.getVwsdpoutcomes()!=null)
						statereport.getVwsdpoutcomes().generateOutcomesChart();
					else
						statereport.setVwsdpoutcomes(new Vwsdpoutcomes());
					
					
					inputStream = getClass().getResourceAsStream("/jasperreports/SDPFollowup.jasper");
				}
				
				results.add(statereport);
				beanCollectionDataSource = new JRBeanCollectionDataSource(results);
				reportParams.put("HEADERNYTDIMAGE", "/jasperreports/");
				reportParams.put("REPORTFILENAME", reportFileName.toString());
				
				if (search.getSelectedReportFormat() == 1){
					exportPdfReport(inputStream,reportParams, beanCollectionDataSource );
				} else {
					exportDocxReport(inputStream,reportParams, beanCollectionDataSource );
				}
			}
	}
	
	private void exportPdfReport(InputStream inputStream, Map<String,Object>reportParams,JRBeanCollectionDataSource beanCollectionDataSource )
	{
		StringBuffer tmp = new StringBuffer();
		try {
			JRPdfExporter exporter = new JRPdfExporter();
			JasperPrint jasperPrint = null;
			String fileName = (String)reportParams.get("REPORTFILENAME");
			reportParams.remove("REPORTFILENAME");
			jasperPrint = JasperFillManager.fillReport(inputStream, reportParams, beanCollectionDataSource);
			exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, getServletResponse().getOutputStream());
			if(fileName!= null)
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_FILE_NAME, fileName);
			else
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_FILE_NAME, "SDPExport");
			getServletResponse().setHeader("Content-disposition","attachment; filename=" +fileName+".pdf");
			getServletResponse().setContentType("application/pdf");
			exporter.exportReport();
			/*JasperRunManager.runReportToPdfStream(inputStream,getServletResponse().getOutputStream(),reportParams, beanCollectionDataSource);
			tmp.append("; filename=SDP_");
			if(reportFileName!=null)	
			{
				tmp.append(reportFileName);
			}
			tmp.append(System.currentTimeMillis())
				.append(".pdf");
			getServletResponse().setHeader("Content-disposition","attachment; filename=" +tmp.toString());
			getServletResponse().setContentType("application/pdf");
			getServletResponse().getOutputStream().flush();
			getServletResponse().getOutputStream().close();*/
		} catch (JRException e) {	
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void exportDocxReport(InputStream inputStream, Map<String,Object>reportParams,JRBeanCollectionDataSource beanCollectionDataSource )
	{
		byte[] output;
        JasperPrint jasperPrint = null;
        StringBuffer tmp = new StringBuffer();
        JRExporter exporter;
		
		try {
			
			JRDocxExporter docxExporter = new JRDocxExporter();
	//		JasperPrint jasperPrint = null;
			String fileName = (String)reportParams.get("REPORTFILENAME");
			reportParams.remove("REPORTFILENAME");
			jasperPrint = JasperFillManager.fillReport(inputStream, reportParams, beanCollectionDataSource);
			docxExporter.setParameter(JRDocxExporterParameter.CHARACTER_ENCODING, "UTF-8");
			docxExporter.setParameter(JRDocxExporterParameter.JASPER_PRINT, jasperPrint);
			docxExporter.setParameter(JRDocxExporterParameter.OUTPUT_STREAM, getServletResponse().getOutputStream());
			if(fileName!= null)
				docxExporter.setParameter(JRDocxExporterParameter.OUTPUT_FILE_NAME, fileName);
			else
				docxExporter.setParameter(JRDocxExporterParameter.OUTPUT_FILE_NAME, "SDPExport");
			getServletResponse().setHeader("Content-disposition","attachment; filename=" +fileName+".docx");
			getServletResponse().setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			docxExporter.exportReport();
									
		/*	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			jasperPrint = JasperFillManager.fillReport(inputStream, reportParams, beanCollectionDataSource);
			
			tmp.append("; filename=SDP_");
			if(reportFileName!=null)	
			{
				tmp.append(reportFileName);
			}
			tmp.append(System.currentTimeMillis())
				.append(".docx");
			getServletResponse().setHeader("Content-disposition","attachment; filename=" +tmp.toString());
			getServletResponse().setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			exporter = new JRDocxExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			exporter.exportReport();
	        output = baos.toByteArray();
	        getServletResponse().setContentLength(output.length);
	        getServletResponse().getOutputStream().write(output);
	        getServletResponse().getOutputStream().flush();*/
			
			
		} catch (JRException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		/*finally
		{
			try {
				if(getServletResponse().getOutputStream() !=null)
					getServletResponse().getOutputStream().close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}*/
		
	}
	
	private String getSelectedFiscalYear(){
		String fiscalYear = "";
		for (ReportingPeriod reportPeriod : (List<ReportingPeriod>) application
				.get(Constants.APPKEY_REPORTING_PERIOD_LIST))
		{
			if(reportPeriod.getId() == search.getSelectedFiscalYear())
			{
				fiscalYear = reportPeriod.getName().substring(0,4);
				break;
			}
		}
		return fiscalYear;
	}
	
	private String getSelectedRpLetter(int selectedReportPeriod){
		String rpLetter = "";
		switch(selectedReportPeriod){
			case 1:{
				rpLetter = "A";
				break;
			}
			case 2:{
				rpLetter = "B";
				break;
			}
			case 3:{
				rpLetter = "FULL";
				break;
			}
		}
		return rpLetter;
	}
	private String getOutcomesPopulationTypeOfSelectedFiscalYear()
	{
		String populationType = null;
		for (ReportingPeriod reportPeriod : (List<ReportingPeriod>) application
				.get(Constants.APPKEY_REPORTING_PERIOD_LIST))
		{
			if(reportPeriod.getId() == search.getSelectedFiscalYear())
			{
				if(reportPeriod.getOutcomeAge() == null){
					populationType = "Baseline";
				} else {
					switch(reportPeriod.getOutcomeAge().intValue())
					{
				
						case 19:
						{
							populationType = "Follow-up 19";
							break;
						}
						case 21:
						{
							populationType = "Follow-up 21";
							break;
						}
					
						default:{
							populationType = "Baseline";
							break;
						}
				
					}
				//break;
				}
			}
		}
		
		return populationType;
	}

}
