package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.service.InterRptPdCheckService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.List;
import java.util.Map;


/**
 * Action to select transmission for cross-file comparison across report periods.
 * 
 * @author Adam Russell (18816)
 */
public class InterRptPdCheckFileSelPg extends ActionSupport implements Preparable, SessionAware
{
	@Getter @Setter private Map<String, String> availableTransmissions;
	@Getter @Setter private String defaultTransmission;
	@Getter @Setter private String transmission;
	@Getter private String reportPeriodName;
	@Getter private String stateName;

	@Getter @Setter private InterRptPdCheckService interRptPdCheckService;
	@Getter @Setter private PopulateSearchCriteriaService populateSearchCriteriaService;

	@Getter @Setter private Map<String, Object> session;
	
	@Override
	public void prepare() throws Exception
	{
		SiteUser siteUser = (SiteUser) getSession().get("siteUser");
		setAvailableTransmissions(getPopulateSearchCriteriaService().getTransmissionSelectMap(siteUser));
		if (getAvailableTransmissions().keySet().iterator().hasNext())
		{
			setDefaultTransmission(getAvailableTransmissions().keySet().iterator().next());
		}
	}
	
	@SkipValidation
	public final String execute()
	{
		return SUCCESS;
	}
	
	@Validations(
			requiredFields={
				@RequiredFieldValidator(fieldName="transmission", message="You must select a data file.")})
		public final String crossFileRptTransmission()
		{
		
			Transmission targetTransmission = getInterRptPdCheckService().getTransmissionWithId(Long.valueOf(transmission));
			reportPeriodName = targetTransmission.getReportingPeriod().getName();
			stateName = targetTransmission.getState().getStateName();
			FileComparisonAcrossReportPeriods fileComparisonAcrossReportPeriods = null;
			SiteUser siteUser = (SiteUser) getSession().get("siteUser");
			List<FileAdvisoryAcrossReportPeriods> fileAdvisories = getInterRptPdCheckService().getFileAdvisoriesAcrossReportPeriods(transmission,siteUser.getId());
			if(fileAdvisories != null)
			{
				fileComparisonAcrossReportPeriods = new FileComparisonAcrossReportPeriods();
				fileComparisonAcrossReportPeriods.addAll(fileAdvisories);
			}

			getSession().put("interRptPdCheck", fileComparisonAcrossReportPeriods);
						
			return SUCCESS;
		}
}
