/**
 * 
 */
package gov.hhs.acf.cb.nytd.service.impl;


import gov.hhs.acf.cb.nytd.actions.report.StateDataSnapshotSearch;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.Statereport;
import gov.hhs.acf.cb.nytd.models.Vwsdpfollowupdemographics;
import gov.hhs.acf.cb.nytd.models.Vwsdpfollowupnonpartreasons;
import gov.hhs.acf.cb.nytd.models.Vwsdpoutcomes;
import gov.hhs.acf.cb.nytd.models.helper.SDPOutcomesHeaderData;
import gov.hhs.acf.cb.nytd.models.helper.SDPServedHeaderData;
import gov.hhs.acf.cb.nytd.service.ReportsService;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author 23839
 *
 */
public class ReportsServiceImpl extends BaseServiceImpl implements ReportsService {
	
	@Getter @Setter private StateDAO stateDAO;

	@Override
	public Statereport getStateReport(StateDataSnapshotSearch search) {
		
		long stateReportId = 6L;
		
		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Statereport> criteriaQuery = criteriaBuilder.createQuery(Statereport.class);
		Root<Statereport> root = criteriaQuery.from(Statereport.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("id"), stateReportId)
		);
		criteriaQuery.select(root);
		TypedQuery<Statereport> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
		
		//TODO: why keeping this method while always returning null?
		return null;
	}

	@Override
	public SDPServedHeaderData getSDPServedHeaderData(long stateReportId) {
		
		SDPServedHeaderData data = null;
		
		Query query = getSessionFactory().getCurrentSession().getNamedQuery("getSDPServedHeaderData");
		query.setLong("STATEREPORTID", stateReportId);
		query.setResultTransformer(Transformers.aliasToBean(SDPServedHeaderData.class));
		data = (SDPServedHeaderData)query.uniqueResult();
		
		return data;
	}
	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	@Override
	public Statereport getStateReport(long stateid, String fiscalYear,
			String reportPeriod, String populationType) {
		
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Statereport.class);
		criteria.add(Restrictions.eq("stateid", stateid));
		criteria.add(Restrictions.eq("rpyear", fiscalYear));
		criteria.add(Restrictions.eq("reportingperiod", reportPeriod));
		criteria.add(Restrictions.eq("populationtype", populationType));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Statereport statereport = (Statereport) criteria.uniqueResult();
		if(statereport != null)
		{
			
			if(statereport.getPopulationtype() != null && (statereport.getPopulationtype().contains("Follow-up 19") || statereport.getPopulationtype().contains("Follow-up 21")))
			{
				try {
					statereport = generateFollowupSDPReport(statereport);
				} catch (Exception e){
				    log.error(e.getMessage(), e);
					return null;
				}
			}
		}
		
		
		return statereport;
	}
	
	@Override
	public SDPOutcomesHeaderData getSDPOutcomesHeaderData(long stateReportId) {
		
		SDPOutcomesHeaderData data = null;
		
		Query query = getSessionFactory().getCurrentSession().getNamedQuery("getSDPOutcomesHeaderData");
		query.setLong("STATEREPORTID", stateReportId);
		query.setResultTransformer(Transformers.aliasToBean(SDPOutcomesHeaderData.class));
		data = (SDPOutcomesHeaderData)query.uniqueResult();
		
		return data;
	}

	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	@Override
	public Statereport getStateReport(long stateid, String fiscalYear,
			String reportPeriod) {
		
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Statereport.class);
		criteria.add(Restrictions.eq("stateid", stateid));
		criteria.add(Restrictions.eq("rpyear", fiscalYear));
		criteria.add(Restrictions.eq("reportingperiod", reportPeriod));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		Statereport statereport = (Statereport) criteria.uniqueResult();
		if(statereport != null)
		{
			
			if(statereport.getPopulationtype() != null && (statereport.getPopulationtype().contains("Follow-up 19") || statereport.getPopulationtype().contains("Follow-up 21")))
			{
				statereport = generateFollowupSDPReport(statereport);
			}
		}
		
		
		return statereport;
	}
	
	private Statereport generateFollowupSDPReport(Statereport statereport)
	{
		if(statereport != null )
		{
			long stateReportId = statereport.getStatereportid();
			Criteria criteria = null;
			if(statereport.getVwsdpfollowupdemographics() == null)
			{
				Vwsdpfollowupdemographics demos = null;
				
				CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
				CriteriaQuery<Vwsdpfollowupdemographics> criteriaQuery = criteriaBuilder.createQuery(Vwsdpfollowupdemographics.class);
				Root<Vwsdpfollowupdemographics> root = criteriaQuery.from(Vwsdpfollowupdemographics.class);
				criteriaQuery.where(
						criteriaBuilder.equal(root.get("statereportid"), stateReportId)
				);
				criteriaQuery.select(root);
				TypedQuery<Vwsdpfollowupdemographics> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
				// Check if the record exist
				try {
					demos = q.setMaxResults(1).getSingleResult();
					statereport.setVwsdpfollowupdemographics(demos);
				} catch (NoResultException e) {
					log.error("No result found for Vwsdpfollowupdemographics with statereportid: " + stateReportId);
				}
			}
			if(statereport.getVwsdpfollowupnonpartreasons() == null)
			{
				Vwsdpfollowupnonpartreasons reasons = null;
				
				CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
				CriteriaQuery<Vwsdpfollowupnonpartreasons> criteriaQuery = criteriaBuilder.createQuery(Vwsdpfollowupnonpartreasons.class);
				Root<Vwsdpfollowupnonpartreasons> root = criteriaQuery.from(Vwsdpfollowupnonpartreasons.class);
				criteriaQuery.where(
						criteriaBuilder.equal(root.get("fstatereportid"), stateReportId)
				);
				criteriaQuery.select(root);
				TypedQuery<Vwsdpfollowupnonpartreasons> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
				reasons = q.setMaxResults(1).getSingleResult();
				
				statereport.setVwsdpfollowupnonpartreasons(reasons);
			}
			if(statereport.getVwsdpoutcomes() ==  null)
			{
				Vwsdpoutcomes outcomes = null;
				
				CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
				CriteriaQuery<Vwsdpoutcomes> criteriaQuery = criteriaBuilder.createQuery(Vwsdpoutcomes.class);
				Root<Vwsdpoutcomes> root = criteriaQuery.from(Vwsdpoutcomes.class);
				criteriaQuery.where(
						criteriaBuilder.equal(root.get("fstatereportid"), stateReportId)
				);
				criteriaQuery.select(root);
				TypedQuery<Vwsdpoutcomes> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
				outcomes = q.setMaxResults(1).getSingleResult();
				
				statereport.setVwsdpoutcomes(outcomes);
				
			}
		}
		
		return statereport;
	}
	
	@Override
	public String getAbbrByStateId(long stateId)
	{
		String stateAbbreviation = "";
		try {
			String stateName = getStateDAO().getStateName(stateId);
			stateAbbreviation = getStateDAO().getStateAbbr(stateName);
		} catch (Exception e) {
			log.error("Error getting state abbreviation with state id: " + stateId);
			log.error(e.getMessage(), e);
		}

		return stateAbbreviation;
	}

	
	

}
