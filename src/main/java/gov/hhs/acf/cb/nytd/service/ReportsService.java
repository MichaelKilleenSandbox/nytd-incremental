/**
 * 
 */
package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.report.StateDataSnapshotSearch;
import gov.hhs.acf.cb.nytd.models.Statereport;
import gov.hhs.acf.cb.nytd.models.helper.SDPOutcomesHeaderData;
import gov.hhs.acf.cb.nytd.models.helper.SDPServedHeaderData;


/**
 * @author 23839
 *
 */
public interface ReportsService extends BaseService {
	
	Statereport getStateReport(StateDataSnapshotSearch search);
	Statereport getStateReport(long stateid, String fiscalYear, String reportPeriod);
	Statereport getStateReport(long stateid, String fiscalYear, String reportPeriod, String populationType);
	SDPServedHeaderData getSDPServedHeaderData(long stateReportId);
	SDPOutcomesHeaderData getSDPOutcomesHeaderData(long stateReportId);
	String getAbbrByStateId(long stateId);
}
