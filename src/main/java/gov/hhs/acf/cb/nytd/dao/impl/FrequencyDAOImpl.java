package gov.hhs.acf.cb.nytd.dao.impl;

//TODO: A lot of unused imports due to trying various approaches

import gov.hhs.acf.cb.nytd.dao.FrequencyDAO;
import gov.hhs.acf.cb.nytd.models.helper.Frequency;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.Collection;
import java.util.List;

@Transactional
public class FrequencyDAOImpl extends HibernateDaoSupport implements FrequencyDAO
{
	protected final Logger log = Logger.getLogger(getClass());

	/**
	 * @author Adam Russell
	 * @see FrequencyDAO#getFrequencies(Collection, Collection, Collection, Boolean, Boolean, Boolean)
	 */
	@Override
	public List<Frequency> getFrequencies(Collection<String> states, Collection<String> reportPeriods,
			Collection<String> elements, Boolean byState, Boolean byReportPeriod, Boolean byElement)
	{
		assert(states != null && reportPeriods != null && elements != null
		    && byState != null && byReportPeriod != null && byElement != null);
		
		Session dbSession = getSessionFactory().getCurrentSession();
		EntityManager em = dbSession.getEntityManagerFactory().createEntityManager();
		StoredProcedureQuery query = em.createNamedStoredProcedureQuery("spFrequencyReport");
		query.setParameter(2, CommonFunctions.join("~", elements));
		query.setParameter(3, CommonFunctions.join("~", states));
		query.setParameter(4, CommonFunctions.join("~", reportPeriods));
		query.setParameter(5, CommonFunctions.booleanToInt(byState));
		query.setParameter(6, CommonFunctions.booleanToInt(byReportPeriod));
		query.setParameter(7, CommonFunctions.booleanToInt(byElement));
		List<Frequency> queryResult = query.getResultList();
		return queryResult;
	}
}
