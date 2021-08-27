package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod.Changed;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod.InError;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod.Matched;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;


/**
 * Compares two transmissions in a report period.
 * 
 * @author Adam Russell (18816)
 */
public class CompareFilesWithinReportPeriod extends ActionSupport implements SessionAware,
		ServletResponseAware
{
	@Getter @Setter private String transmission1;
	@Getter @Setter private String transmission2;
	@Getter @Setter private String stateName;
	@Getter @Setter private String reportPeriodName;
	@Getter @Setter private FileComparisonWithinReportPeriod fileComparisonWithinReportPeriod;
	
	@Getter @Setter private DataExtractionService dataExtractionService;
	
	@Getter @Setter private HttpServletResponse servletResponse;
	@Getter @Setter private Map<String, Object> session;
	protected Logger log = Logger.getLogger(getClass());
	
	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS on success,
	 *         Action.INPUT if required input was not given by user
	 */
	public final String execute()
	{
		log.debug("CompareFilesWithinReportperiod.execute");
		
		initFromSession();
		
		if (getTransmission1() == null || getTransmission2() == null || getStateName() == null
		 || getReportPeriodName() == null || getFileComparisonWithinReportPeriod() == null)
		{
			return INPUT;
		}

		return SUCCESS;
	}
	
	public final String exportTable()
	{
		initFromSession();
		
		if (getTransmission1() == null || getTransmission2() == null || getStateName() == null
		 || getReportPeriodName() == null || getFileComparisonWithinReportPeriod() == null)
		{
			return INPUT;
		}
		
		final String FILE1_HEADER = String.format("%s (%s)", getTransmission1(),
				getFileComparisonWithinReportPeriod().getFile1Compliant() ? "Compliant" : "Non-Compliant");
		final String FILE2_HEADER = String.format("%s (%s)", getTransmission2(),
				getFileComparisonWithinReportPeriod().getFile2Compliant() ? "Compliant" : "Non-Compliant");
		final String DELTA = "Delta";
		
		ExportableTable<?> dataTable = new ExportableTable(this, getDataExtractionService())
		{
			protected void addColumns() {}
		};
		
		dataTable.addField(1, null, "State");
		dataTable.addField(2, null, getStateName());
		dataTable.addField(3, null, null);
		dataTable.addField(4, null, null);
		dataTable.addField(5, null, null);
		dataTable.addField(6, null, null);
		dataTable.addField(7, null, null);
		
		dataTable.add(new TableDatumBean(null, 1,"Report Period" ));
		dataTable.add(new TableDatumBean(null, 1, getReportPeriodName()));
		dataTable.add(new TableDatumBean(null, 1, null));
		dataTable.add(new TableDatumBean(null, 1, null));
		dataTable.add(new TableDatumBean(null, 1, null));
		dataTable.add(new TableDatumBean(null, 1, null));
		dataTable.add(new TableDatumBean(null, 1, null));
		
		dataTable.add(new TableDatumBean(null, 2, null ));
		dataTable.add(new TableDatumBean(null, 2, null));
		dataTable.add(new TableDatumBean(null, 2, null));
		dataTable.add(new TableDatumBean(null, 2, null));
		dataTable.add(new TableDatumBean(null, 2, FILE1_HEADER));
		dataTable.add(new TableDatumBean(null, 2, FILE2_HEADER));
		dataTable.add(new TableDatumBean(null, 2, DELTA));
		
		
		
		dataTable.add(new TableDatumBean(null, 3, "Records"));
		dataTable.add(new TableDatumBean(null, 3, "Unmatched"));
		dataTable.add(new TableDatumBean(null, 3, null));
		dataTable.add(new TableDatumBean(null, 3, "Without Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 3,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsUnmatchedWithoutError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 3,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsUnmatchedWithoutError().size())));
		dataTable.add(new TableDatumBean(DELTA, 3,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.UNMATCHED, Changed.N_A, InError.WITHOUT_ERROR))));
		dataTable.add(new TableDatumBean(null, 4, "Records"));
		dataTable.add(new TableDatumBean(null, 4, "Unmatched"));
		dataTable.add(new TableDatumBean(null, 4, null));
		dataTable.add(new TableDatumBean(null, 4, "With Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 4,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsUnmatchedWithError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 4,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsUnmatchedWithError().size())));
		dataTable.add(new TableDatumBean(DELTA, 4,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.UNMATCHED, Changed.N_A, InError.WITH_ERROR))));
		dataTable.add(new TableDatumBean(null, 5, "Records"));
		dataTable.add(new TableDatumBean(null, 5, "Unmatched"));
		dataTable.add(new TableDatumBean(null, 5, null));
		dataTable.add(new TableDatumBean(null, 5, "Total"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 5,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsUnmatchedTotal().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 5,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsUnmatchedTotal().size())));
		dataTable.add(new TableDatumBean(DELTA, 5,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.UNMATCHED, Changed.N_A, InError.TOTAL))));
		dataTable.add(new TableDatumBean(null, 6, "Records"));
		dataTable.add(new TableDatumBean(null, 6, "Matched"));
		dataTable.add(new TableDatumBean(null, 6, "Unchanged"));
		dataTable.add(new TableDatumBean(null, 6, "Without Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 6,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedUnchangedWithoutError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 6,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedUnchangedWithoutError().size())));
		dataTable.add(new TableDatumBean(DELTA, 6,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.UNCHANGED, InError.WITHOUT_ERROR))));
		dataTable.add(new TableDatumBean(null, 7, "Records"));
		dataTable.add(new TableDatumBean(null, 7, "Matched"));
		dataTable.add(new TableDatumBean(null, 7, "Unchanged"));
		dataTable.add(new TableDatumBean(null, 7, "With Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 7,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedUnchangedWithError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 7,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedUnchangedWithError().size())));
		dataTable.add(new TableDatumBean(DELTA, 7,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.UNMATCHED, Changed.UNCHANGED, InError.WITH_ERROR))));
		dataTable.add(new TableDatumBean(null, 8, "Records"));
		dataTable.add(new TableDatumBean(null, 8, "Matched"));
		dataTable.add(new TableDatumBean(null, 8, "Unchanged"));
		dataTable.add(new TableDatumBean(null, 8, "Total"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 8,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedUnchangedTotal().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 8,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedUnchangedTotal().size())));
		dataTable.add(new TableDatumBean(DELTA, 8,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.UNCHANGED, InError.TOTAL))));
		dataTable.add(new TableDatumBean(null, 9, "Records"));
		dataTable.add(new TableDatumBean(null, 9, "Matched"));
		dataTable.add(new TableDatumBean(null, 9, "Changed"));
		dataTable.add(new TableDatumBean(null, 9, "Without Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 9,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsMatchedChangedWithoutError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 9,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsMatchedChangedWithoutError().size())));
		dataTable.add(new TableDatumBean(DELTA, 9,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.CHANGED, InError.WITHOUT_ERROR))));
		dataTable.add(new TableDatumBean(null, 10, "Records"));
		dataTable.add(new TableDatumBean(null, 10, "Matched"));
		dataTable.add(new TableDatumBean(null, 10, "Changed"));
		dataTable.add(new TableDatumBean(null, 10, "With Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 10,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsMatchedChangedWithError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 10,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsMatchedChangedWithError().size())));
		dataTable.add(new TableDatumBean(DELTA, 10,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.CHANGED, InError.WITH_ERROR))));
		dataTable.add(new TableDatumBean(null, 11, "Records"));
		dataTable.add(new TableDatumBean(null, 11, "Matched"));
		dataTable.add(new TableDatumBean(null, 11, "Changed"));
		dataTable.add(new TableDatumBean(null, 11, "Total"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 11,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedChangedTotal().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 11,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedChangedTotal().size())));
		dataTable.add(new TableDatumBean(DELTA, 11,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.CHANGED, InError.TOTAL))));
		dataTable.add(new TableDatumBean(null, 12, "Records"));
		dataTable.add(new TableDatumBean(null, 12, "Matched"));
		dataTable.add(new TableDatumBean(null, 12, "Total"));
		dataTable.add(new TableDatumBean(null, 12, "Without Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 12,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsMatchedTotalWithoutError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 12,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsMatchedTotalWithoutError().size())));
		dataTable.add(new TableDatumBean(DELTA, 12,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.TOTAL, InError.WITHOUT_ERROR))));
		dataTable.add(new TableDatumBean(null, 13, "Records"));
		dataTable.add(new TableDatumBean(null, 13, "Matched"));
		dataTable.add(new TableDatumBean(null, 13, "Total"));
		dataTable.add(new TableDatumBean(null, 13, "With Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 13,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsMatchedTotalWithError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 13,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsMatchedTotalWithError().size())));
		dataTable.add(new TableDatumBean(DELTA, 13,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.TOTAL, InError.WITH_ERROR))));
		dataTable.add(new TableDatumBean(null, 14, "Records"));
		dataTable.add(new TableDatumBean(null, 14, "Matched"));
		dataTable.add(new TableDatumBean(null, 14, "Total"));
		dataTable.add(new TableDatumBean(null, 14, "Total"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 14,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedTotal().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 14,
				String.valueOf(getFileComparisonWithinReportPeriod().getRecordsMatchedTotal().size())));
		dataTable.add(new TableDatumBean(DELTA, 14,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.MATCHED, Changed.TOTAL, InError.TOTAL))));
		dataTable.add(new TableDatumBean(null, 15, "Records"));
		dataTable.add(new TableDatumBean(null, 15, "Total"));
		dataTable.add(new TableDatumBean(null, 15, null));
		dataTable.add(new TableDatumBean(null, 15, "Without Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 15,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsTotalWithoutError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 15,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsTotalWithoutError().size())));
		dataTable.add(new TableDatumBean(DELTA, 15,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.TOTAL, Changed.N_A, InError.WITHOUT_ERROR))));
		dataTable.add(new TableDatumBean(null, 16, "Records"));
		dataTable.add(new TableDatumBean(null, 16, "Total"));
		dataTable.add(new TableDatumBean(null, 16, null));
		dataTable.add(new TableDatumBean(null, 16, "With Error"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 16,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsTotalWithError().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 16,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsTotalWithError().size())));
		dataTable.add(new TableDatumBean(DELTA, 16,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.TOTAL, Changed.N_A, InError.WITH_ERROR))));
		dataTable.add(new TableDatumBean(null, 17, "Records"));
		dataTable.add(new TableDatumBean(null, 17, "Total"));
		dataTable.add(new TableDatumBean(null, 17, null));
		dataTable.add(new TableDatumBean(null, 17, "Total"));
		dataTable.add(new TableDatumBean(FILE1_HEADER, 17,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile1RecordsTotal().size())));
		dataTable.add(new TableDatumBean(FILE2_HEADER, 17,
				String.valueOf(getFileComparisonWithinReportPeriod().getFile2RecordsTotal().size())));
		dataTable.add(new TableDatumBean(DELTA, 17,
				String.valueOf(getFileComparisonWithinReportPeriod().getDelta(Matched.TOTAL, Changed.N_A, InError.TOTAL))));

		
		return dataTable.export(servletResponse,
		                        "crossFileComparisonWithinReportPeriod_"
		                      + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}
	
	private void initFromSession()
	{
		setStateName((String) getSession().get("intraRptPdCheckState"));
		setReportPeriodName((String) getSession().get("intraRptPdCheckReportPeriod"));
		setTransmission1((String) getSession().get("intraRptPdCheckFile1"));
		setTransmission2((String) getSession().get("intraRptPdCheckFile2"));
		setFileComparisonWithinReportPeriod((FileComparisonWithinReportPeriod) getSession().get(
				"intraRptPdCheck"));
	}
	
	/**
	 * Returns a link to the record-level error search action with parameters for the appropriate records.
	 * 
	 * @param transmissionId database identifier of the transmission containing the records
	 * @param recordNumbers record numbers to search for in the record-level error search
	 * @return link to record-level error search action
	 */
	public final String getLinkToRecordErrors(String transmissionId, Collection<String> recordNumbers)
	{
		String actionWithParams = "searchRecordErrors.action";
		actionWithParams = actionWithParams + "?search.transmissionId=" + transmissionId;
		actionWithParams = actionWithParams
		                 + CommonFunctions.getURLMultiValueParam(recordNumbers, "search.recordNumbers", false);
		return actionWithParams;
	}
}
