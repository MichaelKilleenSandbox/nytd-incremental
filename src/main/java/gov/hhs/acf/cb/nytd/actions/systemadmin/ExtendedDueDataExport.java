
package gov.hhs.acf.cb.nytd.actions.systemadmin;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

/**
* Extended due date export model extending ExportableTable.
*/
public class ExtendedDueDataExport extends ExportableTable<VwExtendedDueDate>{

    public ExtendedDueDataExport(ActionSupport systemAdminAction, DataExtractionService dataExtractionService) {
            super(systemAdminAction, dataExtractionService);
    }

    /**
    * Add columns for Extended due date export table.
    */
    @Override
    protected void addColumns() {
        SimpleDateFormat formateDate = new SimpleDateFormat("MM/dd/yyyy");
        addColumn("Reporting Period/Name", VwExtendedDueDate::getReportingPeriodName);
        addColumn("Start Reporting Period Date", (final VwExtendedDueDate extendedDueDate) -> 
            (null != extendedDueDate.getStartReportingDate())? 
                formateDate.format(extendedDueDate.getStartReportingDate().getTime()): "");
        addColumn("End Reporting Period Date", (final VwExtendedDueDate extendedDueDate) -> 
            (null != extendedDueDate.getEndReportingDate())? 
                formateDate.format(extendedDueDate.getEndReportingDate().getTime()): "");
        addColumn("Outcome Age", (final VwExtendedDueDate extendedDueDate) -> 
            (null != extendedDueDate.getOutComeAge())? 
                extendedDueDate.getOutComeAge().toString(): "");
        addColumn("Transmission Type Name", (final VwExtendedDueDate extendedDueDate) -> 
            (null != StringUtils.trimToNull(extendedDueDate.getTransmissionTypeName()))? 
                extendedDueDate.getTransmissionTypeName(): "");
        addColumn("Submission Date", (final VwExtendedDueDate extendedDueDate) -> 
            (null != extendedDueDate.getSubmissionDate())? 
                formateDate.format(extendedDueDate.getSubmissionDate().getTime()): "");
        addColumn("State Name", (final VwExtendedDueDate extendedDueDate) -> 
            (null != StringUtils.trimToNull(extendedDueDate.getEddStateName()))? 
                extendedDueDate.getEddStateName():"");
        addColumn("Updated By", (final VwExtendedDueDate extendedDueDate) -> 
            (null != StringUtils.trimToNull(extendedDueDate.getExtendedUpdateBy()))? 
                extendedDueDate.getExtendedUpdateBy():"");
        addColumn("Updated Date", (final VwExtendedDueDate extendedDueDate) -> 
            (null != extendedDueDate.getExtendedUpdateDate())? 
                formateDate.format(extendedDueDate.getExtendedUpdateDate().getTime()): "");
        addColumn("Extended Due Date", (final VwExtendedDueDate extendedDueDate) -> 
            (null != extendedDueDate.getExtendedDueDate())? 
                formateDate.format(extendedDueDate.getExtendedDueDate().getTime()): "");
    }

}
