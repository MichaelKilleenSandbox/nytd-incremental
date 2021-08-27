package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod;
import gov.hhs.acf.cb.nytd.service.IntraRptPdCheckService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.Iterator;
import java.util.Map;


/**
 * Action to select two transmissions/submissions given a state and report period.
 *
 * @author Adam Russell (18816)
 */
public class IntraRptPdCheckFileSelPg extends ActionSupport implements Preparable, SessionAware, ParameterAware {
	protected Logger log = Logger.getLogger(getClass());

	@Getter @Setter private Map<String, String> availableTransmissions;
	@Getter @Setter private String defaultTransmission1;
	@Getter @Setter private String defaultTransmission2;
	@Getter @Setter private String transmission1;
	@Getter @Setter private String transmission2;
	@Getter @Setter private String reportPeriod;
	@Getter @Setter private String state;
	@Getter @Setter private String stateName;
	@Getter @Setter private String reportPeriodName;

	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;
	@Getter @Setter private TransmissionServiceP3 transmissionServiceP3;
	@Getter @Setter private IntraRptPdCheckService intraRptPdCheckService;

	@Getter @Setter private Map<String, Object> session;
	@Getter @Setter private Map<String, String[]> parameters;

	@Override
	public void prepare() throws Exception {
		setState(getParameters().containsKey("state") ? getParameters().get("state")[0] : null);
		setReportPeriod(getParameters().containsKey("reportPeriod") ? getParameters().get("reportPeriod")[0] : null);

		if (getState() == null
		 || getState().isEmpty()
		 || getReportPeriod() == null
		 || getReportPeriod().isEmpty()) {
			return;
		}

		SiteUser siteUser = (SiteUser) getSession().get("siteUser");
		Long stateId = Long.valueOf(getState());
		Long reportPeriodId = Long.valueOf(getReportPeriod());

		setAvailableTransmissions(getPopulateSearchCriteriaService().getTransmissionSelectMap(
				siteUser, stateId, reportPeriodId));

		Transmission submission = null;
		if (siteUser.getPrivileges().contains("canViewTransmissions")) {
			// don't want to find active submission for federal users
			submission = transmissionServiceP3.getSubmission(
					transmissionServiceP3.getState(stateId),
					transmissionServiceP3.getReportingPeriod(reportPeriodId));
		}
		if (submission != null) {
			setDefaultTransmission1(submission.getId().toString());
		} else {
			// get second entry in the drop-down
			Iterator<String> it = getAvailableTransmissions().keySet().iterator();
			assert (it.hasNext());
			setDefaultTransmission1(it.next());
			assert (it.hasNext());
			setDefaultTransmission1(it.next());
		}

		assert (getAvailableTransmissions().keySet().iterator().hasNext());
		setDefaultTransmission2(getAvailableTransmissions().keySet().iterator().next());

		setStateName(getIntraRptPdCheckService().getStateName(Long.valueOf(getState())));
		setReportPeriodName(getIntraRptPdCheckService().getReportPeriodName(Long.valueOf(getReportPeriod())));
	}

	/**
	 * Displays form.
	 *
	 * @return Action.SUCCESS on success,
	 *         Action.INPUT if required input was not given by user
	 */
	@SkipValidation
	public final String get() {
		log.debug("IntraRptPdCheckFileSelPg.get");

		if (getState() == null || getState().isEmpty() || getReportPeriod() == null || getReportPeriod().isEmpty()) {
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * Processes submitted form.
	 *
	 * @return Action.SUCCESS on success,
	 *         Action.INPUT if required input was not given by user
	 */
	@Validations(
		requiredStrings = {
			@RequiredStringValidator(fieldName="transmission1", message="You must enter a value for transmission 1."),
			@RequiredStringValidator(fieldName="transmission2", message="You must enter a value for transmission 2."),
			@RequiredStringValidator(fieldName="state", message="You must enter a state."),
			@RequiredStringValidator(fieldName="reportPeriod",
			                         message="You must enter a report period.")},
		expressions = {
			@ExpressionValidator(message="Transmission 1 and 2 should be different.",
			                     expression="!transmission1.equals(transmission2)")})
	public final String post() {
		log.debug("IntraRptPdCheckFileSelPg.post");

		FileComparisonWithinReportPeriod fileComparisonWithinReportPeriod = getIntraRptPdCheckService()
				.getFileComparisonWithinReportPeriod(Long.valueOf(getTransmission1()), Long.valueOf(getTransmission2()));

		setStateName(getIntraRptPdCheckService().getStateName(Long.valueOf(getState())));
		setReportPeriodName(getIntraRptPdCheckService().getReportPeriodName(Long.valueOf(getReportPeriod())));

		getSession().put("intraRptPdCheckState", getStateName());
		getSession().put("intraRptPdCheckReportPeriod", getReportPeriodName());
		getSession().put("intraRptPdCheckFile1", getTransmission1());
		getSession().put("intraRptPdCheckFile2", getTransmission2());
		getSession().put("intraRptPdCheck", fileComparisonWithinReportPeriod);
		
		return SUCCESS;
	}
}
