package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Action to select 
 * 	a) report period for failed transmissions.
 * 
 * @author Nava Krishna Mallela (23839)
 */
public class FailedTransmissionsReport extends ActionSupport implements Preparable, SessionAware, ParameterAware, ServletRequestAware, ServletResponseAware
{
	@Getter @Setter private Map<String, String> availableReportPeriods;
	@Getter @Setter private Map<String, String> searchCriteria;
	@Getter @Setter private String defaultReportPeriod;
	@Getter @Setter private String reportPeriod;
	@Getter @Setter private String reportPeriodName;
	@Getter @Setter private String state;
	@Getter @Setter private String fileType;
	@Getter @Setter private String startDate;
	@Getter @Setter private String endDate;
	@Getter @Setter private String sortColumn;
	@Getter @Setter private String sortOrder;
	@Getter @Setter private String prevSortColumn;
	@Getter @Setter private String prevSortOrder;
	@Getter @Setter private String freezeSort;
	@Getter @Setter private boolean defaultPage;
	
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
	@Getter @Setter private TransmissionServiceP3 transmissionServiceP3;
	@Getter @Setter private DataExtractionService dataExtractionService;
	@Getter @Setter private	List<FailedTransmissionDetail>  failedTransmissionsList;
	@Getter @Setter private Map<String, Object> session;
	@Setter private Map<String, String[]> parameters;
	@Setter private HttpServletRequest servletRequest;
	@Setter private HttpServletResponse servletResponse;
	private static final String DEFAULTSORTCOLUMN = "1";
	private static final String DEFAULTSORTORDER_ASC = "0";
	private static final String SORTORDER_DESC = "1";
	
	protected Logger log = Logger.getLogger(getClass());

	@Override
	public void prepare() throws Exception
	{
		log.debug("FailedTransmissionsReport");

		searchCriteria = new HashMap<String, String>();
		
	}

	/**
	 * Displays Report Periods Selection page.
	 * 
	 * @return Action.SUCCESS on success
	 */
	@SkipValidation
	public final String getReportPeriods()
	{
		log.debug("FailedTransmissionsReport.getReportPeriods");
		return SUCCESS;
	}

	/**
	 * Displays Failed Transmission Report page
	 * 
	 * @return Action.SUCCESS on success
	 */

	public final String getReport()
	{

		log.debug("FailedTransmissionsReport.getReport");
		if(defaultPage) 
		{
			
			return SUCCESS;
		}
		
		if(freezeSort != null && Integer.parseInt(freezeSort) == 1)
		{
			sortColumn = null;
			sortOrder = null;
		}
		
		Date sttDate = null;
		Date edDate = null;
		if (startDate != null && !startDate.isEmpty())
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				formatter.setLenient(false);
				sttDate = (Date) formatter.parse(startDate);
				long sTime = sttDate.getTime();
			}
			catch (ParseException e)
			{
				addActionError("Start Date is not a valid date.");
		
			}
		}
		if (endDate != null && !endDate.isEmpty())
		{
			try
			{
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				formatter.setLenient(false);
				edDate = (Date) formatter.parse(endDate);
				long eTime = edDate.getTime();
			}
			catch (ParseException e)
			{
				addActionError("End Date is not a valid date.");
		
			}
		}

		if (sttDate != null && edDate != null && sttDate.after(edDate))
		{
			addActionError("Start Date must not be after End Date");
		}
		
		if(!getActionErrors().isEmpty())
			return INPUT;
		
		
		
		if(sortColumn == null)
		{
			sortColumn = DEFAULTSORTCOLUMN;
		}
		
		if(sortOrder == null )
		{
			sortOrder = DEFAULTSORTORDER_ASC;
		}else if(DEFAULTSORTORDER_ASC.equals(sortOrder) && freezeSort == null)
		{
			sortOrder = SORTORDER_DESC;
		}
		else if(SORTORDER_DESC.equals(sortOrder) && freezeSort == null)
		{
			sortOrder = DEFAULTSORTORDER_ASC;
		}
		
		// creates a map of search criteria
		prepareSearchCriteria();
		if (transmissionServiceP3 != null)
		{
			long l1 = System.currentTimeMillis();
			failedTransmissionsList = transmissionServiceP3.getFailedTransmissionsReport(searchCriteria);
			long l2 = System.currentTimeMillis();
			System.out.println((l2 - l1) + " millisecs");
		}

		return SUCCESS;
	}

	/**
	 * @return
	 */
	public String exportFailedTransmissionsReport()
	{
		// get the list of results to export
		log.debug("FailedTransmissionsReport.exportFailedTransmissionsReport");
		prepareSearchCriteria();
		List<FailedTransmissionDetail> failedTransList = transmissionServiceP3
				.getFailedTransmissionsReport(searchCriteria);

		// create the exporter
		FailedTrasnmissionsReportExport exporter = new FailedTrasnmissionsReportExport(this,
				dataExtractionService);

		// export the data to csv
		return exporter.export(servletResponse, failedTransList, "FailedTransmissionsReport_"
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}
	
	public String printFailedTransmissionsReport()
	{
		prepareSearchCriteria();
		failedTransmissionsList = transmissionServiceP3.getFailedTransmissionsReport(searchCriteria);
		return SUCCESS;
		
	}
	
	public String resetFailedTransmissionsReport()
	{
		state= null;
		fileType = null;
		reportPeriod = null;
		startDate = null;
		endDate = null;
		failedTransmissionsList = null;
		sortColumn = null;
		sortOrder = null;	
		defaultPage = true;
		return SUCCESS;
		
	}

	private void prepareSearchCriteria()
	{
		if (sortColumn == null)
		{
			sortColumn = DEFAULTSORTCOLUMN;
			sortOrder = DEFAULTSORTORDER_ASC;
		}
			searchCriteria.put("SORTCOLUMN", sortColumn);
			searchCriteria.put("SORTORDER", sortOrder);
		if (reportPeriod != null && !reportPeriod.equals("0") && !reportPeriod.isEmpty())
		{
			searchCriteria.put("REPORTPERIOD", reportPeriod);
		}
		if (state != null && !state.equals("0"))
		{
			searchCriteria.put("STATE", state);
		}
		if (startDate != null && !startDate.isEmpty())
		{
			searchCriteria.put("STARTDATE", startDate);
		}
		if (endDate != null && !endDate.isEmpty())
		{
			searchCriteria.put("ENDDATE", endDate);
		}
		if (fileType != null && !fileType.equals("0"))
		{
			searchCriteria.put("FILETYPE", fileType);
		}
	}

}
