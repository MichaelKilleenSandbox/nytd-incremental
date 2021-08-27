package gov.hhs.acf.cb.nytd.models.sampling.state;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.sampling.Cohort;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class StateSamplingContext extends PaginatedSearch {

       
    public static final String SESSION_KEY = "state.sampling.context";
    
    @Getter @Setter private List<Cohort> cohorts;
    @Getter @Setter private State state;
    @Getter @Setter private Long selectedCohort;
    @Getter @Setter private Long selectedRequestStatus;
    @Getter @Setter private Long selectedState;
    @Getter @Setter private Long selectedSamplingMethod;
    @Getter @Setter private boolean hasAlternateSamplingMethod;
    @Getter @Setter private String alternateSamplingMethod;
    @Getter @Setter private String lastAlternateSamplingMethod;
    @Getter @Setter private boolean hadAlternateSamplingMethod;
    @Getter @Setter private Long selectedSamplingRequestId;
    @Getter @Setter private String selectedStateName;
    @Getter @Setter private String selectedCohortName;
    @Getter @Setter private String samplingRequestComment;
    @Getter @Setter private Map<String,String> messageMap;
    
    
    public StateSamplingContext() {
	    super();
	    loadDefaults();
	}
	
	public void reset() {
	        super.reset();
	        loadDefaults();
	    }

	private void loadDefaults() {

        setSelectedCohort(0L);
        setSelectedRequestStatus(0L);
        setSelectedState(0L);
        setSelectedSamplingMethod(0L);
        setHasAlternateSamplingMethod(false);
        setAlternateSamplingMethod(null);
        setMessageMap(messageMap);
       
    }
    

}
