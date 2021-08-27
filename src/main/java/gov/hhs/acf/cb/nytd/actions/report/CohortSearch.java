package gov.hhs.acf.cb.nytd.actions.report;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.models.Cohort;
import gov.hhs.acf.cb.nytd.models.State;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 23839
 * Date: NOV 30, 2011
 */
public class CohortSearch extends PaginatedSearch 
{
    

	@Getter @Setter private List<State> availableStates;
	@Getter @Setter private int selectedState;
	@Getter @Setter private List availableCohorts;
	@Getter @Setter private int selectedCohorts;
	@Getter @Setter private int selectedPopulationType;
	@Getter @Setter private boolean showSample;
	
	@Getter @Setter private List<String> noJSList;
	@Getter @Setter private boolean javaScriptEnabled;
	
	@Getter @Setter private String selectedStateName;
	@Getter @Setter private String selectedCohortName;
	@Getter @Setter private String selectedReportPeriodName;
	@Getter @Setter private Long cohortSize;
	@Getter @Setter private Long sampleSize;
	@Getter @Setter private Long periodLocked19;
	@Getter @Setter private Long periodLocked21;
	@Getter @Setter private String recordNumber;
	@Getter @Setter private Long transmissionId;
	@Getter @Setter private Long R2EId;
	@Getter	@Setter private List<String> showReportPeriod;
	@Getter @Setter private String queryBaselineRP;
	@Getter @Setter private String queryPostbufferRP;
	@Getter @Setter private String queryFollowup19RP;
	@Getter @Setter private int showUpdateCohortSetButton;
	
	public CohortSearch() {
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
        setAvailableStates(new ArrayList<State>());
        setAvailableCohorts(new ArrayList<Cohort>());
        setShowSample(false);

    }
	
	public void setShowSample(boolean showSample)
	{
		this.showSample = showSample;
	}
	
	public boolean getShowSample()
	{
		return this.showSample;
	}

}
