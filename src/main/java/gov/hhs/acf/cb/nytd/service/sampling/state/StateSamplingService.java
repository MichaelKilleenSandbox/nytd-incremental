package gov.hhs.acf.cb.nytd.service.sampling.state;

import gov.hhs.acf.cb.nytd.models.SamplingRequestHistory;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.sampling.state.StateSamplingContext;
import gov.hhs.acf.cb.nytd.service.BaseService;

import java.util.List;
import java.util.Map;

public interface StateSamplingService extends BaseService
{
    
    public StateSamplingContext getCurrentContext(SiteUser siteUser);
    public void requestSample(StateSamplingContext context, boolean useAlternateSamplingMethod, String alternateSamplingDescription);
    List getCohortList(SiteUser siteUser);
    List<SamplingRequestHistory> getSamplingRequestHistories(Long samplingRequestId);
    Map<String,String> getMessageIdMap(StateSamplingContext search);
    Map<String,String> getAlternateSamplingMethod(Long samplingRequestId);
}
