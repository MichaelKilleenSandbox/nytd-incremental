package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.helper.Frequency;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

public class FrequencyReportByRPExport extends ExportableTable<Frequency> {
  
	public FrequencyReportByRPExport(ActionSupport action,
         DataExtractionService dataExtractionService) {

   		super(action, dataExtractionService);
   }

	@Override
	protected void addColumns()
	{
		
		 addColumn("State",
             new ValueProvider<Frequency>() {
                 public String getValue(
                         final Frequency detailItem) {
                     return detailItem.getState();
                 }
             });
     addColumn("Element Number/Name",
             new ValueProvider<Frequency>() {
                 public String getValue(
                         final Frequency detailItem) {
                     return detailItem.getElementNumber()+" - " + detailItem.getElementName()  ;
                 }
             });
     addColumn("Value",
             new ValueProvider<Frequency>() {
                 public String getValue(
                         final Frequency detailItem) {
                     return detailItem.getValue();
                 }
             });
     addColumn("Count",
             new ValueProvider<Frequency>() {
                 public String getValue(
                         final Frequency detailItem) {
                     return detailItem.getCount();
                 }
             });
     
     addColumn("Value Rate",
           new ValueProvider<Frequency>() {
               public String getValue(
                       final Frequency detailItem) {
                   return detailItem.getFormattedPercent();
               }
           });
	}

}
