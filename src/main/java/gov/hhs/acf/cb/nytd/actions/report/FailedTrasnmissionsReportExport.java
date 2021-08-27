package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

/**
 * User: 23839
 * Date: Dec 20, 2010
 */
public class FailedTrasnmissionsReportExport extends ExportableTable<FailedTransmissionDetail> {
    public FailedTrasnmissionsReportExport(ActionSupport action,
                             DataExtractionService dataExtractionService) {

        super(action, dataExtractionService);
    }

    @Override
    protected void addColumns() {
        addColumn("State",
                new ValueProvider<FailedTransmissionDetail>() {
                    public String getValue(
                            final FailedTransmissionDetail detailItem) {
                        return detailItem.getStateAbbr();
                    }
                });
        addColumn("File Type",
                new ValueProvider<FailedTransmissionDetail>() {
                    public String getValue(
                            final FailedTransmissionDetail detailItem) {
                        return detailItem.getFileType() != null ? detailItem.getFileType() : "" ;
                    }
                });
        addColumn("File Name",
                new ValueProvider<FailedTransmissionDetail>() {
                    public String getValue(
                            final FailedTransmissionDetail detailItem) {
                        return detailItem.getFileName();
                    }
                });
        addColumn("Reason for Failure",
                new ValueProvider<FailedTransmissionDetail>() {
                    public String getValue(
                            final FailedTransmissionDetail detailItem) {
                        return detailItem.getReason();
                    }
                });
       
    }
}
