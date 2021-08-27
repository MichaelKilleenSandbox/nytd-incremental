package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.helper.CohortResultDTO;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * User: 23839
 * Date: Dec 20, 2010
 */
public class CohortBaselineExport extends ExportableTable<CohortResultDTO> {
    public CohortBaselineExport(ActionSupport action,
                             DataExtractionService dataExtractionService) {

        super(action, dataExtractionService);
    }

    @Override
    protected void addColumns() {
        addColumn("Baseline Population - Age 17",
                new ValueProvider<CohortResultDTO>() {
                    public String getValue(
                            final CohortResultDTO recNumber) {
                        return recNumber.getE3RecordNumber();
                    }
                });
        addColumn("Baseline Report Period",
                new ValueProvider<CohortResultDTO>() {
                    public String getValue(
                            final CohortResultDTO recNumber) {
                        return recNumber.getReportingPeriod();
                    }
                });
        
       
    }
}
