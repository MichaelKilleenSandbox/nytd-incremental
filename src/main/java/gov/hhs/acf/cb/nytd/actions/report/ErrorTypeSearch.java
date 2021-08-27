package gov.hhs.acf.cb.nytd.actions.report;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: 23839
 * Date: NOV 30, 2011
 */
public class ErrorTypeSearch extends PaginatedSearch 
{
    
	@Getter @Setter private List<String> availableReportingPeriods;
	@Getter @Setter private List<String> selectedReportingPeriods;
	@Getter @Setter private List<String> availableStateName;
	@Getter @Setter private List<String> selectedStateName;
	@Getter @Setter private List<String> availableElementNumbers;
	@Getter @Setter private List<String> selectedElementNumbers;
	@Getter @Setter private List<String> availableErrorTypes;
	@Getter @Setter private List<String> selectedErrorTypes;
	@Getter @Setter private List<String> availableDQATypes;
	@Getter @Setter private List<String> selectedDQATypes;
	@Getter @Setter private List<String> noJSList;
	@Getter @Setter private boolean javaScriptEnabled;
	@Getter @Setter private Map<String, Long> elementNumberMap;
	@Getter @Setter private List<String> selectedElemNums;
	
	public ErrorTypeSearch() {
	    super();
	    loadDefaults();
	}
	
	public void reset() {
	        super.reset();
	        loadDefaults();
	    }

	private void loadDefaults() {

        setAvailableReportingPeriods(new ArrayList<String>());
        setNoJSList(new ArrayList<String>());
        setSelectedReportingPeriods(new ArrayList<String>());        
        setAvailableElementNumbers(new ArrayList<String>());
        setSelectedElementNumbers(new ArrayList<String>());
        setAvailableStateName(new ArrayList<String>());
        setSelectedStateName(new ArrayList<String>());
        setSelectedElemNums(new ArrayList<String>());
        setAvailableErrorTypes(new ArrayList<String>());
        setSelectedErrorTypes(new ArrayList<String>());
        setAvailableDQATypes(new ArrayList<String>());
        setSelectedDQATypes(new ArrayList<String>());
        setJavaScriptEnabled(false);
        setElementNumberMap(new HashMap<String, Long>());

    }

}
