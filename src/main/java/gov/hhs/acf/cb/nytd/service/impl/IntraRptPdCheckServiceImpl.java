package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.RecordForFileComparisonWithinReportPeriodDAO;
import gov.hhs.acf.cb.nytd.dao.ReportingPeriodDAO;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonWithinReportPeriod;
import gov.hhs.acf.cb.nytd.models.helper.RecordForFileComparisonWithinReportPeriod;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import gov.hhs.acf.cb.nytd.service.IntraRptPdCheckService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Implements IntraRptPdCheckService
 * @author Adam Russell (18816)
 * @see IntraRptPdCheckService
 */
@Transactional
public class IntraRptPdCheckServiceImpl extends BaseServiceImpl implements IntraRptPdCheckService
{
	private final RecordForFileComparisonWithinReportPeriodDAO recordForFileComparisonWithinReportPeriodDAO;
	private final StateDAO stateDAO;
	private final ReportingPeriodDAO reportingPeriodDAO;
	private final ComplianceService complianceService;
	
	public IntraRptPdCheckServiceImpl(RecordForFileComparisonWithinReportPeriodDAO recordForFileComparisonWithinReportPeriodDAO,
	                                  StateDAO stateDAO,
	                                  ReportingPeriodDAO reportingPeriodDAO,
	                                  ComplianceService complianceService)
	{
		super();
		
		this.recordForFileComparisonWithinReportPeriodDAO = recordForFileComparisonWithinReportPeriodDAO;
		this.stateDAO = stateDAO;
		this.reportingPeriodDAO = reportingPeriodDAO;
		this.complianceService = complianceService;
	}
	
	/**
	 * @see IntraRptPdCheckService#getReportPeriodName(Long)
	 * @author Adam Russell (18816)
	 */
	public String getReportPeriodName(Long reportPeriodId)
	{
		return reportingPeriodDAO.getReportPeriodName(reportPeriodId);
	}
	
	/**
	 * @see IntraRptPdCheckService#getStateName(Long)
	 * @author Adam Russell (18816)
	 */
	public String getStateName(Long stateId)
	{
		return stateDAO.getStateName(stateId);
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see IntraRptPdCheckService#getFileComparisonWithinReportPeriod(Long, Long)
	 */
	@Override
	public FileComparisonWithinReportPeriod getFileComparisonWithinReportPeriod(Long transmission1Id,
			Long transmission2Id)
	{
		log.debug("Entering getFileComparisonWithinReportPeriod.");
		
		FileComparisonWithinReportPeriod result = new FileComparisonWithinReportPeriod();
		
		result.setFile1Id(transmission1Id);
		result.setFile2Id(transmission2Id);
		
		Boolean file1Compliant = complianceService.isCompliant(transmission1Id);
		Boolean file2Compliant = complianceService.isCompliant(transmission2Id);
		result.setFile1Compliant(file1Compliant);
		result.setFile2Compliant(file2Compliant);
		
		List<RecordForFileComparisonWithinReportPeriod> comparison = recordForFileComparisonWithinReportPeriodDAO
				.getRecordsForFileComparisonWithinReportPeriod(transmission1Id, transmission2Id);
		log.debug("Received records for file comparison. Categorizing them now...");

		for (RecordForFileComparisonWithinReportPeriod categorizedRecord : comparison)
		{
			Boolean isFirstFile = null;
			Boolean isMatched = false;
			Boolean isChanged = null;
			Boolean isInError = false;
			
			if (categorizedRecord.getId().getTransmissionId() > 0)
			{
				isFirstFile = Boolean.valueOf(true);
				if (categorizedRecord.getId().getTransmissionId().equals(transmission2Id))
				{
					isFirstFile = Boolean.valueOf(false);
				}
			}
			
			isMatched = categorizedRecord.getId().getMatched();
			
			if (isMatched)
			{
				isChanged = categorizedRecord.getId().getChanged();
			}
			
			isInError = categorizedRecord.getId().getInError();
			
			// Add the record number to the FileComparisonWithinReportPeriod instance.
			result.addRecordNumber(categorizedRecord.getId().getRecordNumber(), isFirstFile, isMatched, isChanged, isInError);
		}
		
		log.debug("Exiting getFileComparisonWithinReportPeriod.");
		return result;
	}
	
}
