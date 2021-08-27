package gov.hhs.acf.cb.nytd.actions.report;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.models.Cohort;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 23839
 * Date: NOV 30, 2011
 */
public class OutcomesReportSearch extends PaginatedSearch 
{
    


	@Getter @Setter private int selectedState;
	@Getter @Setter private List<String> availableStateName;
	@Getter @Setter private List<String> selectedStateName;
	@Getter @Setter private List<Cohort> availableCohorts;
	@Getter @Setter private int selectedCohorts;
	private boolean selectedFollowup19;
	private boolean selectedFollowup21;
	@Getter @Setter private int selectedReportCode;
	
	@Getter @Setter private List<String> noJSList;
	@Getter @Setter private boolean javaScriptEnabled;
	

	@Getter @Setter private String selectedCohortName;
	@Getter @Setter private String selectedReportPeriodName;
	@Getter @Setter private Long selectedTransmissionId;

	@Getter @Setter private Long outcomeAge;
	@Getter @Setter private Long stateId;
	@Getter @Setter private Long cohortStatusId;
	@Getter @Setter private Long transmissionId;
	@Getter @Setter private String reportingPeriodName;
	@Getter @Setter private String recordNumber;
	@Getter @Setter private String baselineRPName;
	@Getter @Setter private String postBufferRPName;
	@Getter @Setter private String followup19RPName;
	@Getter @Setter private String stateName;
	@Getter @Setter private Float PercentReported ;
	@Getter @Setter private Long notReportedYouth;
	@Getter @Setter private Long invalidE34Youth;
	@Getter @Setter private boolean showYouthNotReported;
	@Getter @Setter private boolean showInvalidE34Youth;
	@Getter @Setter private boolean showFCNotParticipated;
	@Getter @Setter private boolean showDCNotParticipated;
	@Getter @Setter Float fcPartPercentage;
	@Getter @Setter Float dcPartPercentage;
	@Getter @Setter Long fcNotPartCount;
	@Getter @Setter Long dcNotPartCount;
	@Getter @Setter SiteUser siteUser;
	
	public OutcomesReportSearch() {
	    super();
	    loadDefaults();
	}
	
	public void reset() {
	        super.reset();
	        loadDefaults();
	    }

	private void loadDefaults() {

        setNoJSList(new ArrayList<String>());
        setJavaScriptEnabled(false);
        setAvailableStateName(new ArrayList<String>());
        setAvailableCohorts(new ArrayList<Cohort>());
        setSelectedStateName(new ArrayList<String>());
    
        setSelectedFollowup19(true);
        setSelectedFollowup21(true);

    }
	
	public void setSelectedFollowup19(boolean selectedFollowup19)
	{
		this.selectedFollowup19 = selectedFollowup19;
	}
	
	public boolean getSelectedFollowup19()
	{
		return this.selectedFollowup19;
	}
	
	public void setSelectedFollowup21(boolean selectedFollowup21)
	{
		this.selectedFollowup21 = selectedFollowup21;
	}
	
	public boolean getSelectedFollowup21()
	{
		return this.selectedFollowup21;
	}

}
