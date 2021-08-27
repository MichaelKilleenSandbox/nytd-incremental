/**
 * Copyright 2009, ICF International Created: Dec 2, 2009 Author: 15178
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
package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * This action is used to display the reports page and cross-file across
 * reporting period selection page
 * 
 * @author Ranju Saroch (15178)
 */
@SuppressWarnings("serial")
public class ReportsPageAction extends ActionSupport  implements SessionAware, ApplicationAware, ServletRequestAware, ServletResponseAware
{
	protected Logger log = Logger.getLogger(getClass());
	@Getter @Setter private Map<String, Object> session;
	@Getter @Setter private Map<String, Object> application;	
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;	
	@Getter @Setter private String reportingPeriod;

	@Getter @Setter private DataExtractionService dataExtractionService;
	@Getter @Setter SubmissionStatisticsReportHelper submissionStatsHelper;
	@Getter @Setter String sortBy;
	@Getter @Setter String sortOrder;
	@Getter @Setter boolean printFriendly;
	@Getter @Setter String selectedReportingPeriodName;
	
	@Getter @Setter private List<String> availableReportingPeriods;
	@Getter @Setter private List<String> selectedReportingPeriods;
	@Getter @Setter private List<String> availableStates;
	@Getter @Setter private List<String> selectedStates;
	@Getter @Setter private List<String> groupBy;
	@Getter @Setter private String selectedGroupBy = null;
	@Getter @Setter private ReportingPeriod rp;
	@Getter @Setter private String exportName;
	@Getter @Setter private boolean dataFlag;
	@Getter @Setter private boolean viewActiveSubmissionsOnly = false;

	@Getter @Setter List<ComplianceStandardsReport> complianceStandardsReportResults;
  
	protected HttpServletRequest servletRequest;
   protected HttpServletResponse servletResponse;

	

	/**
	 * Executes action and take user to the reports page
	 * 
	 * @return Action.SUCCESS
	 */
	public final String reportsPage()
	{
		putInApplicationScope();
		return Action.SUCCESS;
	}
	
	
	public final String submissionStatisticsDisplay()
	{		
		return Action.SUCCESS;
	}
	public final String defaultSubmissionStatisticsReport()
	{	       			
			this.sortBy = "stateName";
			this.sortOrder = "ASC";	
			if(isPrintFriendly())
			{
			   this.sortBy = servletRequest.getParameter("sortBy");
			   this.sortOrder = servletRequest.getParameter("sortOrder");	
			   this.reportingPeriod = servletRequest.getParameter("reportingPeriod");
			   this.viewActiveSubmissionsOnly = Boolean.parseBoolean(servletRequest.getParameter("viewActiveSubmissionsOnly"));
			}
		
			if(getReportingPeriod() != null)
				submissionStatsHelper = dataExtractionService.submissionStatisticsReport(Long.parseLong(getReportingPeriod()), this.sortBy,this.sortOrder,this.viewActiveSubmissionsOnly);
		return Action.SUCCESS;
	}
	public final String submissionStatisticsReport()
	{			
		   this.sortBy = servletRequest.getParameter("sortBy");
			this.sortOrder = servletRequest.getParameter("sortOrder");	
			this.reportingPeriod = servletRequest.getParameter("reportingPeriod");
			if(sortBy == null)
			{
				sortBy = "stateName";
			}
			if(sortOrder == null)
			{
				sortOrder = "asc";
			}
			//System.out.println("the sortBy" + this.sortBy + "the sortOrder" + this.sortOrder);
			if(this.sortOrder != null && this.sortOrder.equalsIgnoreCase("ASC"))
			{
				//System.out.println("came here in sortOrder not null");
				this.sortOrder = "DESC";
				//System.out.println("came here in sortOrder not null" + this.sortOrder + "the method one" + getSortOrder());
			}
			else if(this.sortOrder != null && this.sortOrder.equalsIgnoreCase("DESC"))
			{
				this.sortOrder = "ASC";
			}
		if(getReportingPeriod() != null)
		{
			dataFlag = true;		
			submissionStatsHelper = dataExtractionService.submissionStatisticsReport(Long.parseLong(getReportingPeriod()), this.sortBy,this.sortOrder,this.isViewActiveSubmissionsOnly());
		}
		else
		{			
			if(getReportingPeriod() == null)
			{
				return Action.INPUT;
			}
		}
		return Action.SUCCESS;
	}
	
	public final String defaultComplianceStandardsReport()
	{	

		groupBy = new ArrayList<String>();		
		groupBy.add("By State");
		groupBy.add("By Report Period");
	    selectedGroupBy = "By State";	
	    dataFlag = false;
	

			availableReportingPeriods = new ArrayList<String>();
			selectedReportingPeriods = new ArrayList<String>();
			List<ReportingPeriod> reportingPeriods = (List<ReportingPeriod>) application.get("reportingPeriodList_key");
			for (ReportingPeriod rp : reportingPeriods)
			{				
				availableReportingPeriods.add(rp.getName());	
			}

			List<State> states = (List<State>) application.get("stateList_key");
			availableStates = new ArrayList<String>();
		    selectedStates = new ArrayList<String>();
			
			for (State st : states)
			{				
				availableStates.add(st.getStateName());	
			}
		

		return Action.SUCCESS;
	}
	public final String complianceStandardsReport()
	{  
		groupBy = new ArrayList<String>();		
		groupBy.add("By State");
		groupBy.add("By Report Period");		
		if(selectedStates != null && selectedStates.size() > 0 
				&& selectedReportingPeriods != null && selectedReportingPeriods.size() > 0
				&& selectedGroupBy != null )
		{
		 complianceStandardsReportResults = new ArrayList<ComplianceStandardsReport>();
		 complianceStandardsReportResults = dataExtractionService.complianceStandardsReport(selectedStates, selectedReportingPeriods, selectedGroupBy);
		 dataFlag = true;  
		}
		else
		{
			if(selectedStates != null && selectedStates.size() == 0 
					&& selectedReportingPeriods != null && selectedReportingPeriods.size() == 0
					&& selectedGroupBy != null )
			{
				addActionError("Please select Report period and State.");
				return Action.INPUT;
			}
		}
		//System.out.println("got compliance standard" + complianceStandardsReportResults.size());
		return Action.SUCCESS;
	}	

	public final String exportComplianceStandardsReport() {
		groupBy = new ArrayList<String>();
		groupBy.add("By State");
		groupBy.add("By Report Period");

		if (exportName != null) {

			// = servletRequest.getParameterValues("availableReportingPeriods");
			if (selectedGroupBy.equalsIgnoreCase("By State")) {
				selectedStates.clear();
				selectedStates.add(exportName);
			} else {
				String[] temp = servletRequest
						.getParameterValues("selectedStates");
				String[] tempVal = temp[0].split(",");
				selectedStates.clear();
				for (int i = 0; i < tempVal.length; i++) {
					if (i == 0 || i == (tempVal.length - 1)) {
						String newStr = tempVal[i].substring(1,
								(tempVal[i]).length());
						selectedStates.add(newStr.trim());
					} else {
						selectedStates.add(tempVal[i].trim());
					}
				}
			}
			if (selectedGroupBy.equalsIgnoreCase("By Report Period")) {
				selectedReportingPeriods.clear();
				selectedReportingPeriods.add(exportName);
			} else {
				String[] temp = servletRequest
						.getParameterValues("selectedReportingPeriods");
				String[] tempVal = temp[0].split(",");
				selectedReportingPeriods.clear();
				for (int i = 0; i < tempVal.length; i++) {
					if (i == 0 || i == (tempVal.length - 1)) {
						String newStr = tempVal[i].substring(1,
								(tempVal[i]).length());
						selectedReportingPeriods.add(newStr.trim());
					} else {
						selectedReportingPeriods.add(tempVal[i].trim());
					}
				}

			}

		}

		complianceStandardsReportResults = new ArrayList<ComplianceStandardsReport>();
		complianceStandardsReportResults = dataExtractionService
				.complianceStandardsReport(selectedStates,
						selectedReportingPeriods, selectedGroupBy);
		System.out.println("got compliance standard"
				+ complianceStandardsReportResults.size());

		ComplianceStandardsReport csr = complianceStandardsReportResults.get(0);
		// create the exporter
		ComplianceStandardsExport exporter = new ComplianceStandardsExport(
				this, dataExtractionService);
		String metaData_0 = "Counts,FSS Error free, Timely data,File format,DS Error free, Outcomes Universe, FCY-Outcomes Participation, DY-Outcomes Participation";
		String metaData_1 = "EP Count,"
				+ csr.getEnforcedFileSubmissionErrorFreeCount() + ","
				+ csr.getEnforcedTimelyCount() + ","
				+ csr.getEnforcedFileFormatCount() + ","
				+ csr.getEnforcedDataStandardsErrorFreeCount() + ","
				+ csr.getEnforcedOutcomeUniverseCount() + ","
				+ csr.getEnforcedParticipationInCareCount() + ","
				+ csr.getEnforcedParticipationDischargedCount();

		String metaData_2 = "PP Count," + csr.getFileSubmissionErrorFreeCount()
				+ "," + csr.getTimelyCount() + "," + csr.getFileFormatCount()
				+ "," + csr.getDataStandardsErrorFreeCount() + ","
				+ csr.getOutcomeUniverseCount() + ","
				+ csr.getParticipationInCareCount() + ","
				+ csr.getParticipationDischargedCount();

		String[] tableData = new String[3];
		tableData[0] = metaData_0;
		tableData[1] = metaData_1;
		tableData[2] = metaData_2;

		exporter.setMetaData(Arrays.asList(tableData));
		// export the data to csv
		return exporter.export(
				getServletResponse(),
				complianceStandardsReportResults.get(0).getResultList(),
				"ComplianceStandardsReport_"
						+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}
	
   public final String exportSubmissionStatisticsReport() {
      // get the list of results to export
    System.out.println("ng in the export" + this.viewActiveSubmissionsOnly);
   	submissionStatsHelper = dataExtractionService.submissionStatisticsReport(Long.parseLong(getReportingPeriod()), servletRequest.getParameter("sortBy"),servletRequest.getParameter("sortOrder"),this.viewActiveSubmissionsOnly);

      // create the exporter
   	SubmissionStatisticsExport exporter =
              new SubmissionStatisticsExport(this, dataExtractionService);

      // export the data to csv
      return exporter.export(
              getServletResponse(),
              submissionStatsHelper.getResultList(),
              "SubmissionStatisticsReport_"
                      + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm")
                      + ".csv");
  }
   
   public HttpServletRequest getServletRequest() {
      return servletRequest;
  }

  public void setServletRequest(HttpServletRequest httpServletRequest) {
      this.servletRequest = httpServletRequest;
  }

  public HttpServletResponse getServletResponse() {
      return servletResponse;
  }

  public void setServletResponse(HttpServletResponse httpServletResponse) {
      this.servletResponse = httpServletResponse;
  }	
	
	
	// putting the reporting period dropdown lists in application scope
	public final void putInApplicationScope()
	{
		// log.debug("SATINDER 1: in putInApplicationScope");
		if (!application.containsKey("reportingPeriodList_key"))
		{
			// log.debug("SATINDER 2: in reportingPeriodList_key");
			List<ReportingPeriod> reportingPeriodList = populateSearchCriteriaService.getReportingPeriodList();
			application.put("reportingPeriodList_key", (Serializable) reportingPeriodList);
		}

	} // end putInApplicationScope
	
}
