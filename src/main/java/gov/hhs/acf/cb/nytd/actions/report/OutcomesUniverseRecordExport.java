package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * User: 23839
 * Date: Dec 20, 2010
 */
public class OutcomesUniverseRecordExport extends ExportableTable<String> {
    
	private String title;
	public OutcomesUniverseRecordExport(ActionSupport action,
                             DataExtractionService dataExtractionService) {

        super(action, dataExtractionService);
    }
    
    public OutcomesUniverseRecordExport(ActionSupport action,
                             DataExtractionService dataExtractionService,String title) {

    	this(action, dataExtractionService);
    	this.title = title;
    }

    @Override
    protected void addColumns() {
        addColumn(title,
                new ValueProvider<String>() {
                    public String getValue(
                            final String recordNumber) {
                        return recordNumber;
                    }
                });
            }
}
