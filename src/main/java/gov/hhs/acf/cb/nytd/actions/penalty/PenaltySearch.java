package gov.hhs.acf.cb.nytd.actions.penalty;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* penalty search model extending PaginatedSerach.
*/
public class PenaltySearch extends PaginatedSearch {
    private List<String> availableReportingPeriods;
    private List<String> selectedReportingPeriods;
    private List<String> noJSList;
    private String stateName;
    private String complianceStatus;
    private String timelyData;
    private String correctFormatData;
    private String errorFreeData;
    private String elementName;
    private boolean viewSubmissionsOnly;
    private boolean javaScriptEnabled;
    @Getter @Setter private List<String> availableElementNumbers;
    @Getter @Setter private List<String> selectedElementNumbers;
    @Getter @Setter private List<Long> selectedElementNums;
    @Getter @Setter private boolean viewActiveSubmissionsOnly;
    @Getter @Setter private Map<String,String> availableStates = new HashMap<>();
    @Getter @Setter private Map<String,String> selectedStates = new HashMap<>();
    
    /*
    * Constructor with no argument
    */
    public PenaltySearch() {
        super();
        loadDefaults();
    }
    
    /*
    * Reset the search
    */
    @Override
    public void reset() {
        super.reset();
        loadDefaults();
    }

    /*
    *Getter/Setter
    */
    public List<String> getAvailableReportingPeriods() {
        return availableReportingPeriods;
    }

    public void setAvailableReportingPeriods(List<String> availableReportingPeriods) {
        this.availableReportingPeriods = availableReportingPeriods;
    }

    public List<String> getSelectedReportingPeriods() {
        return selectedReportingPeriods;
    }

    public void setSelectedReportingPeriods(List<String> selectedReportingPeriods) {
        this.selectedReportingPeriods = selectedReportingPeriods;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(String complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public String getTimelyData() {
        return timelyData;
    }

    public void setTimelyData(String timelyData) {
        this.timelyData = timelyData;
    }

    public String getCorrectFormatData() {
        return correctFormatData;
    }

    public void setCorrectFormatData(String correctFormatData) {
        this.correctFormatData = correctFormatData;
    }

    public String getErrorFreeData() {
        return errorFreeData;
    }

    public void setErrorFreeData(String errorFreeData) {
        this.errorFreeData = errorFreeData;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public boolean isViewSubmissionsOnly() {
        return viewSubmissionsOnly;
    }

    public void setViewSubmissionsOnly(boolean viewSubmissionsOnly) {
        this.viewSubmissionsOnly = viewSubmissionsOnly;        
    }

    public boolean isJavaScriptEnabled() {
        return javaScriptEnabled;
    }
  
    public void setJavaScriptEnabled(boolean javaScriptEnabled) {
        this.javaScriptEnabled = javaScriptEnabled;
    }
    
    public List<String> getNoJSList() {
            return noJSList;
    }

    public void setNoJSList(List<String> noJSList) {
            this.noJSList = noJSList;
    }

    /*
    * Loading default search on extend due date page
    */
    private void loadDefaults() {
        setStateName("All");
        setAvailableReportingPeriods(new ArrayList<>());
        setNoJSList(new ArrayList<>());
        setSelectedReportingPeriods(new ArrayList<>());        
        setComplianceStatus("All");
        setTimelyData("All");
        setCorrectFormatData("All");
        setErrorFreeData("All");
        setAvailableElementNumbers(new ArrayList<>());
        setSelectedElementNumbers(new ArrayList<>());  
        setElementName("");
        setViewSubmissionsOnly(false);
        setJavaScriptEnabled(false);
    	setViewActiveSubmissionsOnly(false);
    	setAvailableStates(new HashMap<>());
    	setSelectedStates(new HashMap<>());
    }

}
