package gov.hhs.acf.cb.nytd.service.sampling.state;

import gov.hhs.acf.cb.nytd.models.SamplingRequestHistory;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.helper.StateCohortDTO;
import gov.hhs.acf.cb.nytd.models.sampling.Cohort;
import gov.hhs.acf.cb.nytd.models.sampling.CohortSamplingStatus;
import gov.hhs.acf.cb.nytd.models.sampling.state.StateSamplingContext;
import gov.hhs.acf.cb.nytd.service.impl.BaseServiceImpl;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class StateSamplingServiceImpl extends BaseServiceImpl implements StateSamplingService {

    //TODO currently mocked
    @Override
    public StateSamplingContext getCurrentContext(SiteUser siteUser) {
        StateSamplingContext stateSamplingContext = new StateSamplingContext();
        List<Cohort> cohorts = new ArrayList<Cohort>();
        int name = 0;
        for (CohortSamplingStatus status : CohortSamplingStatus.values()) {
            Cohort cohort = new Cohort();
            cohort.setName(name++ + "");
            cohort.setSamplingStatus(status);
            cohort.setDate(new Date());
            List<String> comments = new ArrayList<String>();
            comments.add("comment1");
            comments.add("comment2");
            cohort.setComments(comments);
            cohorts.add(cohort);
        }
        stateSamplingContext.setCohorts(cohorts);
        stateSamplingContext.setState(siteUser.getState());
        return stateSamplingContext;
    }

    @Override
    public void requestSample(StateSamplingContext context, boolean useAlternateSamplingMethod, String alternateSamplingDescription) {
        System.out.println(useAlternateSamplingMethod);
        System.out.println(alternateSamplingDescription);
    }
    
    public List getCohortList(SiteUser siteUser)
    {
    	List cohortList = null;
    	StringBuffer queryStr = new StringBuffer();
    	queryStr.append("select cs.cohortsId,cs.name, vw.samplingrequestid, vw.requeststatusid ,vw.requeststatus, cs.reportyear17 from cohorts cs ") 
    			.append(" left outer join( select * from vwsamplingrequests where stateid = :stateId) vw on cs.cohortsid = vw.cohortsid where sysdate > to_date('01-Apr-'||(cs.reportYear17 +1),'DD-Mon-YYYY')");
    	Query query = getSessionFactory().getCurrentSession().createSQLQuery(queryStr.toString());
    	query.setParameter("stateId", siteUser.getState().getId());
    	
    	query.setResultTransformer(Transformers.aliasToBean(StateCohortDTO.class));
    	try {
			cohortList = (List) query.list();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return cohortList;
    }
    public Map<String,String> getMessageIdMap(StateSamplingContext search)
	{
		Query query = getSessionFactory().getCurrentSession().createSQLQuery("select sr.samplingrequestid, srh.messageid from samplingrequests sr,samplingrequesthistory " +
				"srh where sr.lastsamplingstatusdate=srh.createddate and requeststatusid between 45 and 46");
		List list = query.list();
		Map<String,String> messageMap = new HashMap();
		if(list!=null) {
			messageMap = CommonFunctions.getSelectMapFromQueryResult(list);
		}
		/*for(int i=0;i<list.size();i++){
			Object[] o = (Object[])list.get(i);
			for(int j=0;j<o.length;j++){
				messageMap.put((o[0].toString()), (o[1].toString()));
			}
		}*/
		return messageMap;
	}

    public List<SamplingRequestHistory> getSamplingRequestHistories(Long samplingRequestId)
    {
    	List<SamplingRequestHistory> samplingRequestHistories = null;
    	CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<SamplingRequestHistory> criteriaQuery = criteriaBuilder.createQuery(SamplingRequestHistory.class);
		Root<SamplingRequestHistory> root = criteriaQuery.from(SamplingRequestHistory.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("samplingRequestId"), samplingRequestId)
		);
		criteriaQuery.select(root);
		TypedQuery<SamplingRequestHistory> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		samplingRequestHistories = q.getResultList();

		return samplingRequestHistories;
    }
    
    public Map<String,String> getAlternateSamplingMethod(Long samplingRequestId)
    {
    	
    	Query query = getSessionFactory().getCurrentSession().createSQLQuery("select samplingmethod, samplingmethodtext from vwsamplingrequests where samplingrequestid = :samplingrequestid");
    	query.setParameter("samplingrequestid", samplingRequestId);
    	query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	List list = query.list();
    	Map map = (Map)list.get(0);
    	
    	return map;
    }
    
}
