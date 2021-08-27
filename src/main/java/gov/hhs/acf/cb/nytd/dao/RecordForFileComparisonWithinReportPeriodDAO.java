package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.helper.RecordForFileComparisonWithinReportPeriod;

import java.util.List;


public interface RecordForFileComparisonWithinReportPeriodDAO
{
	List<RecordForFileComparisonWithinReportPeriod> getRecordsForFileComparisonWithinReportPeriod(
			Long transmission1Id, Long transmission2Id);
}
