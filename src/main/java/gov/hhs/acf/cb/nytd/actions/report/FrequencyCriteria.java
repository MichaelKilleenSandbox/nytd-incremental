package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.Frequency;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.FrequencyService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Action to select criteria for frequency reports, including
 * state, report period, element, and sorting.
 * 
 * @author Adam Russell (18816)
 */
public class FrequencyCriteria extends ActionSupport implements Preparable, SessionAware,ParameterAware, ServletRequestAware, ServletResponseAware
{
	protected Logger log = Logger.getLogger(getClass());
	
	@Getter @Setter private Map<String, String> availableStates;
	@Getter @Setter private Map<String, String> availableReportPeriods;
	@Getter @Setter private Map<String, String> availableElements;
	@Getter @Setter private Map<String, String> defaultStates;
	@Getter @Setter private Map<String, String> defaultReportPeriods;
	@Getter @Setter private Map<String, String> defaultElements;
	@Getter @Setter private Boolean defaultStateSort;
	@Getter @Setter private Boolean defaultReportPeriodSort;
	@Getter @Setter private Boolean defaultElementSort;
	@Getter @Setter private Collection<String> states;
	@Getter @Setter private Collection<String> reportPeriods;
	@Getter @Setter private Collection<String> elements;
	@Getter @Setter private Boolean stateSort;
	@Getter @Setter private Boolean reportPeriodSort;
	@Getter @Setter private Boolean elementSort;
	@Getter @Setter private String formId;
	
	@Getter @Setter private FrequencyService frequencyService;
	@Getter @Setter private DataExtractionService dataExtractionService;
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
	@Setter private HttpServletRequest servletRequest;
	@Setter private HttpServletResponse servletResponse;
	@Setter private Map<String, String[]> parameters;
	@Getter @Setter List<Frequency> frequencies;
//	Map<String, Object> map;
	
	@Getter @Setter private Map<String, Object> session;

	@Override
	public void prepare() throws Exception
	{
		SiteUser siteUser = (SiteUser) getSession().get("siteUser");
		
		availableStates = populateSearchCriteriaService.getStateSelectMapForUser(siteUser);
		availableReportPeriods = populateSearchCriteriaService
		                         .getReportPeriodSelectMapForUser(siteUser);
		availableElements = frequencyService.getElementSelectMap();
		
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
		defaultStates = new HashMap<String, String>(0);
		
		if(servletRequest.getParameterValues("reportPeriods") != null)
		{
			String [] rps =  servletRequest.getParameterValues("reportPeriods");
			defaultReportPeriods = new HashMap<String, String>(0);
			for(int i=0 ; i< rps.length;i++)
			  {
				defaultReportPeriods.put(rps[i].toString(), availableReportPeriods.get(rps[i].toString()));
				  availableReportPeriods.remove(rps[i].toString());
			  }
			
		}
		else
			defaultReportPeriods = new HashMap<String, String>(0);
		
		if(servletRequest.getParameterValues("elements") != null)
		{
			String [] elems =  servletRequest.getParameterValues("elements");
			defaultElements = new HashMap<String, String>(0);
			for(int i=0 ; i< elems.length;i++)
			  {
				defaultElements.put(elems[i].toString(), availableElements.get(elems[i].toString()));
				availableElements.remove(elems[i].toString());
			  }
			
		}
		else
			defaultElements = new HashMap<String, String>(0);
		
		if(servletRequest.getParameter("stateSort") != null)
		{
			defaultStateSort = Boolean.parseBoolean(servletRequest.getParameter("stateSort"));
		}
		else
			defaultStateSort = false;
		
		if(servletRequest.getParameter("reportPeriodSort") != null)
		{
			defaultReportPeriodSort = Boolean.parseBoolean(servletRequest.getParameter("reportPeriodSort"));
		}
		else
			defaultReportPeriodSort = false;
		if(servletRequest.getParameter("elementSort") != null)
		{
			defaultElementSort = Boolean.parseBoolean(servletRequest.getParameter("elementSort"));
		}
		else
			defaultElementSort = false;
	}
	
	/**
	 * Displays form.
	 * 
	 * @return Action.SUCCESS on success
	 */
	@SkipValidation
	public final String get()
	{
		return SUCCESS;
	}
	
	/**
	 * Processes submitted form.
	 * 
	 * @return Action.SUCCESS on success,
	 *         Action.INPUT if required input was not given by user
	 */
	@Validations(
		requiredFields={
			@RequiredFieldValidator(fieldName="states", message="You must select at least one state."),
			@RequiredFieldValidator(fieldName="reportPeriods",
			                        message="You must select at least one report period."),
			@RequiredFieldValidator(fieldName="elements", message="You must select at least one element.")},
		expressions={
			@ExpressionValidator(message="You must select at least one state.",
			                     expression="!states.isEmpty()"),
			@ExpressionValidator(message="You must select at least one report period.",
			                     expression="!reportPeriods.isEmpty()"),
			@ExpressionValidator(message="You must select at least one element.",
			                     expression="!elements.isEmpty()")})
	public final String post()
	{
		boolean byState = stateSort;
		boolean byReportPeriod = reportPeriodSort;
		boolean byElement = elementSort;
		List<Frequency> frequencies = frequencyService.getFrequencies(states,
		                                                              reportPeriods,
		                                                              elements,
		                                                              stateSort,
		                                                              reportPeriodSort,
		                                                              elementSort);
		if (frequencies.isEmpty())
		{
			addActionError("There is no data to create this report from the selected criteria.");
			return "norecords";
		}
		setFrequencies(frequencies);
		//getSession().put("frequencies", frequencies);
		
		if (byState && !byReportPeriod && !byElement)
		{
			return "by state";
		}
		else if (!byState && byReportPeriod && !byElement)
		{
			return "by report period";
		}
		else if (!byState && !byReportPeriod && byElement)
		{
			return "by element";
		}
		else if (byState && byReportPeriod && !byElement)
		{
			return "by state & report period";
		}
		else if (byState && !byReportPeriod && byElement)
		{
			return "by state & element";
		}
		else if (!byState && byReportPeriod && byElement)
		{
			return "by report period & element";
		}
		else if (byState && byReportPeriod && byElement)
		{
			return "by state & report period & element";
		}
		return SUCCESS;
	}
	
	/**
	 * @return
	 */
	public String exportFrequencyReport()
	{
		// get the list of results to export
		log.debug("FrequencyCriteria.exportFrequencyReport");
		boolean byState = stateSort;
		boolean byReportPeriod = reportPeriodSort;
		boolean byElement = elementSort;
		ExportableTable<Frequency> exporter = null;
		String statesString = states !=null ? states.iterator().next() : null;
		String rpString = reportPeriods !=null ? reportPeriods.iterator().next(): null;
		String elementString = elements != null ? elements.iterator().next(): null;
		String filename = null;
		Pattern p = Pattern.compile("[^0-9]");
		states.clear();
		for (String state : p.split(statesString))
		{
			if(!state.isEmpty())
					states.add(state);
		}
		reportPeriods.clear();
		for (String rp : p.split(rpString))
		{
			if(!rp.isEmpty())
					reportPeriods.add(rp);
		}
		elements.clear();
		for (String element : p.split(elementString))
		{
			if(!element.isEmpty())
					elements.add(element);
		}
		List<Frequency> frequencies = frequencyService.getFrequencies(states,
		                                                              reportPeriods,
		                                                              elements,
		                                                              stateSort,
		                                                              reportPeriodSort,
		                                                              elementSort);
		
		setFrequencies(frequencies);
		
		if (byState && !byReportPeriod && !byElement)
		{
			exporter = new FrequencyReportByStateExport(this, dataExtractionService);
			filename = "FrequencyReportByState_";
		}
		else if (!byState && byReportPeriod && !byElement)
		{
			
			exporter = new FrequencyReportByRPExport(this,dataExtractionService);
			filename = "FrequencyReportByReportPeriod_";
		}
		else if (!byState && !byReportPeriod && byElement)
		{
			exporter = new FrequencyReportByElementExport(this, dataExtractionService);
			filename = "FrequencyReportByElement_";
		}
		else if (byState && byReportPeriod && !byElement)
		{
			exporter = new FrequencyReportByStateRPExport(this,dataExtractionService);
			filename = "FrequencyReportByState&ReportPeriod_";
		}
		else if (byState && !byReportPeriod && byElement)
		{
			exporter = new FrequencyReportByStateElementExport(this,dataExtractionService);
			filename = "FrequencyReportByState&Element_";
		}
		else if (!byState && byReportPeriod && byElement)
		{
			exporter = new FrequencyReportByRPElementExport(this,dataExtractionService);
			filename = "FrequencyReportByReportPeriod&Element_";
		}
		else if (byState && byReportPeriod && byElement)
		{
			exporter = new FrequencyReportByStateRPElementExport(this,dataExtractionService);
			filename = "FrequencyReportByStateReportPeriod&Element_";
			
		}else
		{
			// create the exporter
			exporter = new FrequencyReportExport(this,dataExtractionService);
			filename = "FrequencyReport_";
		}

		// export the data to csv
		return exporter.export(servletResponse, frequencies, filename
				+ DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}

//	@Override
//	public void setSession(Map<String, Object> map) {
//		this.map = map;
//	}
//
//	@Override
//	public void setParameters(Map<String, String[]> map) {
//
//	}
//
//	@Override
//	public void setServletRequest(HttpServletRequest httpServletRequest) {
//
//	}
//
//	@Override
//	public void setServletResponse(HttpServletResponse httpServletResponse) {
//
//	}
}
