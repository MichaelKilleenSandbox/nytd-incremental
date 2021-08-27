package gov.hhs.acf.cb.nytd.service.sampling.federal;

import gov.hhs.acf.cb.nytd.models.SamplingRequest;
import gov.hhs.acf.cb.nytd.models.helper.VwSamplingRequest;
import gov.hhs.acf.cb.nytd.models.sampling.federal.FederalSamplingContext;
import gov.hhs.acf.cb.nytd.service.BaseService;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FederalSamplingService extends BaseService
{
	Long saveSamplingRequest(FederalSamplingContext federalSamplingContext);
	Long getCohortStatusId(Long cohortId, Long stateId);
	List<VwSamplingRequest> searchSamplingRequest(FederalSamplingContext federalSamplingContext);
	boolean updateSamplingRequestStatus(Long SamplingRequestId,Long requestStatusId, Long messageId);
	boolean updateCohortLock(FederalSamplingContext federalSamplingContext, boolean applyLock);
	SamplingRequest getSamplingRequest(FederalSamplingContext federalSamplingContext);
	VwSamplingRequest getSamplingRquest(Long samplingRequestId);
	Long getCohortSize(Long stateId, Long cohortId);
	boolean  samplingRequestExists(FederalSamplingContext search);
	Map<String,String> getMessageIdMap(FederalSamplingContext search);
	String saveSample(File cohortSample,String cohortSampleFileName, String cohortSampleContentType,Long samplingRequestId,String filePath);
	String getFailureReason(Long samplingRequestId);
	Long reSubmitSamplingRequest(FederalSamplingContext federalSamplingContext);
}
