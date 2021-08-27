package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.Map;


/**
 * Action to select state (if Federal user) and report period for cross-file comparison within report period.
 * 
 * @author Adam Russell (18816)
 */
public class IntraRptPdCheckRptPdSelPg extends ActionSupport implements Preparable, SessionAware
{
	@Getter @Setter private Map<String, String> availableReportPeriods;
	@Getter @Setter private Map<String, String> availableStates;
	@Getter @Setter private String defaultReportPeriod;
	@Getter @Setter private String defaultState;
	@Getter @Setter private String reportPeriod;
	@Getter @Setter private String state;
	
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
	@Getter @Setter private Map<String, Object> session;
	protected Logger log = Logger.getLogger(getClass());

	@Override
	public void prepare() throws Exception
	{
		log.debug("IntraRptPdCheckRptPdSelPg.prepare");
		
		SiteUser siteUser = (SiteUser) session.get("siteUser");
		availableReportPeriods = populateSearchCriteriaService
		                         .getReportPeriodSelectMapForUser(siteUser, 2);
		availableStates = populateSearchCriteriaService.getStateSelectMapForUser(siteUser);
		
		if (availableReportPeriods.keySet().iterator().hasNext())
		{
			defaultReportPeriod = availableReportPeriods.keySet().iterator().next();
		}
		defaultState = "-1"; // header in JSP
	}
	
	/**
	 * Displays form.
	 * 
	 * @return Action.SUCCESS on success
	 */
	@SkipValidation
	public final String get()
	{
		log.debug("IntraRptPdCheckRptPdSelPg.get");
		
		return SUCCESS;
	}
	
	/**
	 * Processes submitted form.
	 * 
	 * @return Action.SUCCESS on success,
	 *         Action.INPUT if required input was not given by user
	 */
	@Validations(
		requiredStrings={
			@RequiredStringValidator(fieldName="state", message="You must enter a state."),
			@RequiredStringValidator(fieldName="reportPeriod", message="You must enter a report period.")},
		expressions={
			@ExpressionValidator(message="You must enter a state.", expression="!state.equals(\"-1\")")})
	public final String post()
	{
		log.debug("IntraRptPdCheckRptPdSelPg.post");
		
		SiteUser siteUser = (SiteUser) session.get("siteUser");
		if (!siteUser.getPrivileges().contains("canViewTransmissions"))
		{
			Long transmissionCount = populateSearchCriteriaService.getTransmissionCount(siteUser,
			                                                                            Long.valueOf(state),
			                                                                            Long.valueOf(reportPeriod));
			
			if (transmissionCount < 2)
			{
				addActionError("The selected state and report period do not contain enough data files.");
				defaultReportPeriod = reportPeriod;
				defaultState = state;
				return INPUT;
			}
		}
		
		return SUCCESS;
	}
}
