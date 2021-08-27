package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriods;

import java.util.List;

public interface RecordForFileComparisonAcrossReportPeriodDAO {
        List<FileAdvisoryAcrossReportPeriods> getRecordsForFileComparisonAcrossReportPeriod(
            Long transmissionId, Long siteUserId);
}

