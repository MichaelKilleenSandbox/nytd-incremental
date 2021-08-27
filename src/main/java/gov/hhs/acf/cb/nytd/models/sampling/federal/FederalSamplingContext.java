package gov.hhs.acf.cb.nytd.models.sampling.federal;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.models.Cohort;
import gov.hhs.acf.cb.nytd.models.State;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FederalSamplingContext extends PaginatedSearch {
    
    public static final String SESSION_KEY = "federal.sampling.context";
    
    @Getter @Setter private List<Cohort> cohortList; 
    @Getter @Setter private Long selectedCohort;
    @Getter @Setter private Long selectedRequestStatus;
    @Getter @Setter private Long selectedState;
    @Getter @Setter private Long selectedSamplingMethod;
    @Getter @Setter private boolean hasAlternateSamplingMethod;
    @Getter @Setter private String alternateSamplingMethod;
    @Getter @Setter private String lastAlternateSamplingMethod;
    @Getter @Setter private boolean hadAlternateSamplingMethod;
    @Getter @Setter private Long selectedSamplingRequestId;
    @Getter @Setter private Long selectedCohortStatusId;
    @Getter @Setter private String selectedStateName;
    @Getter @Setter private String selectedSamplingMethodName;
    @Getter @Setter private String selectedCohortName;
    @Getter @Setter private String samplingRequestComment;
    @Getter @Setter private Long messageId;
    @Getter @Setter private Map<String,String> messageMap;
    @Getter @Setter private List<State> availableStates;
    @Getter @Setter private boolean hideFlag = false;
    @Getter @Setter private Long selectedRecordsCount;
    
    public FederalSamplingContext() {
	    super();
	    loadDefaults();
	}
	
	public void reset() {
	        super.reset();
	        loadDefaults();
	    }

	private void loadDefaults() {
		
		setMessageId(0L);
        setSelectedCohort(0L);
        setSelectedRequestStatus(0L);
        setSelectedState(0L);
        setSelectedSamplingMethod(0L);
        setCohortList(new ArrayList<Cohort>());
        setHasAlternateSamplingMethod(false);
        setAlternateSamplingMethod(null);
        setMessageMap(messageMap);
        setAvailableStates(new ArrayList<State>());
        setSelectedRecordsCount(0L);
       
    }
    

}
