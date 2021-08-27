package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.CohortRecord;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * User: 23839
 * Date: Dec 20, 2010
 */
public class CohortSetExport extends ExportableTable<CohortRecord> {
    
	private String title;
	
	private boolean isFollowup19 = false;
	public CohortSetExport(ActionSupport action,
                             DataExtractionService dataExtractionService) {

        super(action, dataExtractionService);
    }
    
    public CohortSetExport(ActionSupport action,
                             DataExtractionService dataExtractionService,int populationTypeId) {

    	
    	this(action, dataExtractionService);
    	
    	if(populationTypeId == 19)
    	{
    		title = "Follow-up Population - Age 19";
    		setFollowup19(true);
    	}
    	else if(populationTypeId == 21)
    	{
    		title = "Follow-up Population - Age 21";
    	}
    }

    @Override
    protected void addColumns() {
        addColumn(title,
                new ValueProvider<CohortRecord>() {
                    public String getValue(
                            final CohortRecord cohortRecord) {
                        return cohortRecord.getRecordNumber();
                    }
                });
        addColumn("Baseline Report Period - Age 17",
                new ValueProvider<CohortRecord>() {
                    public String getValue(
                            final CohortRecord cohortRecord) {
                    	//if(isFollowup19())
                    		return cohortRecord.getReportPeriodName();
                       // return cohortRecord.getFollowupRPName();
                    }
                });
        addColumn("Follow-up Report Period - Age 19",
                new ValueProvider<CohortRecord>() {
                    public String getValue(
                            final CohortRecord cohortRecord) {
                    	if(cohortRecord.getNotReportedFollowup19() == 0)
                    		return cohortRecord.getExpectedFollowup19Period();
                    	else 
                    		return "Not Reported";
                       
                    }
                });
        if(!isFollowup19)
        {
        	addColumn("Follow-up Report Period - Age 21",
                    new ValueProvider<CohortRecord>() {
                        public String getValue(
                                final CohortRecord cohortRecord) {
                        	return cohortRecord.getExpectedFollowup21Period();
                           
                        }
                    });
        }
            }

	/**
	 * @return the isFollowup19
	 */
	public boolean isFollowup19() {
		return isFollowup19;
	}

	/**
	 * @param isFollowup19 the isFollowup19 to set
	 */
	public void setFollowup19(boolean isFollowup19) {
		this.isFollowup19 = isFollowup19;
	}
}
