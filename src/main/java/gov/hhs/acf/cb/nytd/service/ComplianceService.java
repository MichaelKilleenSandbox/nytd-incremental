/**
 * Filename: PenaltyService.java
 * 
 * Copyright 2009, ICF International Created: Jun 25, 2009 Author: 16939
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.compliance.ComplianceSearch;
import gov.hhs.acf.cb.nytd.actions.penalty.PenaltySearch;
import gov.hhs.acf.cb.nytd.actions.report.CohortSearch;
import gov.hhs.acf.cb.nytd.actions.report.ErrorTypeSearch;
import gov.hhs.acf.cb.nytd.actions.report.OutcomesReportSearch;
import gov.hhs.acf.cb.nytd.models.*;

import java.util.List;
import java.util.Map;

/**
 * @author 13873
 * 
 */
public interface ComplianceService extends BaseService
{
	Double calcDataPenalty(Long transmissionId, ComplianceCategory category);

	Double calcFileSubmissionPenalty(Long transmissionId);

	Double calcTransmissionPenalty(Long transmissionId);

	// Integer getCountDataQualtiyAdvisories(Long transmissionId);
	List<Integer> getCountDataQualtiyAdvisories(Long transmissionId);

	Integer getErrorCountForCategory(Long transmissionId, ComplianceCategory category);

	Integer getErrorCountForCategories(Long transmissionId, List<ComplianceCategory> categories);

	List<ComplianceCategory> getFileSubmissionStandardsCategories();

	List<ComplianceCategory> getDataStandardsCategories();

	List<ComplianceCategory> getNonPenaltyCategories();

	List<ComplianceCategory> getAggregateErrorCategories();

	List<ComplianceCategory> getRecordErrorCategories();

	List<ComplianceCategory> getTransmissionErrorCategories();

	List<NytdError> getErrorsForCategories(Long transmissionId, List<ComplianceCategory> categories);

	boolean isCompliant(Long transmissionId);

	ComplianceSearch searchDataAggregates(ComplianceSearch search);

	ComplianceSearch searchElementLevelAdvisories(ComplianceSearch search);

	ComplianceSearch searchRecordLevelAdvisories(ComplianceSearch search);

	ComplianceSearch searchDataQualityAggregates(ComplianceSearch search);

	ComplianceSearch searchRecordErrors(ComplianceSearch search);

	ComplianceSearch searchTransmissionErrors(ComplianceSearch search);

	PenaltySearch searchAggregatePenalties(PenaltySearch search);

	PenaltySearch searchElementPenalties(PenaltySearch search);

	String getElementNameFromNumberInApplication(String number, List<Element> list);

	List<ErrorTypeCount> getNonComplianceCountsReport(ErrorTypeSearch search);

	List<ErrorTypeCount> getDQACountsReport(ErrorTypeSearch search);

	List<String> getErrorTypes();

	int getNonCompliancesCount(ErrorTypeSearch search);

	int getDQAsCount(ErrorTypeSearch search);

	List<Cohort> getCohortList();

	Map getCohortStatus(CohortSearch search);

	List getCohortBaseline(CohortSearch search);

	Long getCohortSampleSize(CohortSearch search);

	void overWriteCohortSet(CohortSearch search);

	int showUpdateCohortSetButton(int cohortsId);
	Map<Long, String> getComplianceStandardPenaltyLetterDesc();
	List getOutcomesReport(OutcomesReportSearch search);
	List getOutcomesReportWithTransId(OutcomesReportSearch search);
	List getOutcomesUniverseRecords(OutcomesReportSearch search);
	List getOutcomesParticipationRecords(OutcomesReportSearch search);
	List<Cohort> getSamplingCohortList();
}
