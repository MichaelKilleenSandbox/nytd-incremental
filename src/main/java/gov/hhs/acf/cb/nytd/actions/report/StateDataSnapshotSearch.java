package gov.hhs.acf.cb.nytd.actions.report;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class StateDataSnapshotSearch extends PaginatedSearch 
{
	
	@Getter @Setter private int selectedState;
	@Getter @Setter private int selectedFiscalYear;
	@Getter @Setter private int selectedReportPeriod;
	@Getter @Setter private int selectedPopulationType;
	@Getter @Setter private int selectedReportFormat;
	@Getter @Setter private List<String> noJSList;
	@Getter @Setter private boolean javaScriptEnabled;
	@Getter @Setter SiteUser siteUser;
	
	public StateDataSnapshotSearch() {
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
    }

}
