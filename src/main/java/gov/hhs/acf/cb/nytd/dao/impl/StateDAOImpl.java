package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Transactional
public class StateDAOImpl extends HibernateDaoSupport implements StateDAO
{
	protected final Logger log = Logger.getLogger(getClass());

	@Override
    public Optional<State> findStateByAbbreviation(String stateAbbreviation) {

    	try {
			Session session = getSessionFactory().getCurrentSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<State> criteriaQuery = cb.createQuery(State.class);
			Root<State> root = criteriaQuery.from(State.class);
			criteriaQuery.select(root).where(cb.equal(root.get("abbreviation"), stateAbbreviation));
			final State state = session.createQuery(criteriaQuery).getSingleResult();
			return Optional.of(state);
		}
    	catch (Exception e) {
			logger.warn("stateAbbreviation:" + stateAbbreviation);
		}
		return Optional.empty();
    }

	@Override
	public Optional<State> findStateById(Long stateId) {

		try  {
			Session session = getSessionFactory().getCurrentSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<State> criteriaQuery = cb.createQuery(State.class);
			Root<State> root = criteriaQuery.from(State.class);
			Predicate selectedState = cb.equal(root.get("id"), stateId);

			criteriaQuery.
					select(root).
					where(selectedState);
			State state = session.createQuery(criteriaQuery).uniqueResult();
			return Optional.ofNullable(state);
		}
		catch (Exception e) {
			log.warn(e.getMessage(),e);
			return Optional.empty();
		}
	}

	/**
	 * @see stateDAO#getStateName(Long)
	 * @author Adam Russell (18816)
	 */
	@Override
	public String getStateName(Long stateId)
	{
		Session session = getSessionFactory().getCurrentSession();
		String queryString;
		String queryResult;
		queryString = "select state.stateName "
		            + "from State as state "
		            + "where state.id = :stateId ";
		queryResult = (String) session.createQuery(queryString)
		              .setParameter("stateId", stateId).uniqueResult();
		return queryResult;
	}
	
	/**
	 * @see StateDAO#getStateSelectMap()
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getStateSelectMap()
	{
		Session session = getSessionFactory().getCurrentSession();
		String query;
		query = "select state.id, state.stateName "
		      + "from State as state "
		      + "order by state.stateName asc ";
		List<Object[]> queryResult = session.createQuery(query).list();
		Map<String, String> selectMap = CommonFunctions.getSelectMapFromQueryResult(queryResult);
		return selectMap;
	}

	/**
	 * @see StateDAO#getStateSelectMap()
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getStateAbbrevMap()
	{
		Session session = getSessionFactory().getCurrentSession();


		String query = "select state.stateName, state.abbreviation "
				+ "from State as state ";
		List<Object[]> queryResult = session.createQuery(query).list();
		Map<String, String> selectMap = CommonFunctions.getSelectMapFromQueryResult(queryResult);
		return selectMap;
	}

	/**
	 * @see StateDAO#getStatesInRegion(Long)
	 * @author Adam Russell (18816)
	 */
	@Override
	public List<State> getStatesInRegion(Long regionId)
	{
		Session session = getSessionFactory().getCurrentSession();
		List<State> queryResult;
		String queryString;
		queryString = "select state "
		            + "from State as state "
		            + "join state.region as region "
		            + "where region.id = :regionId ";
		queryResult = session.createQuery(queryString).setParameter("regionId", regionId).list();
		return queryResult;
	}
	
	/**
	 * @see stateDAO#getStateName(String)
	 */
	@Override
	public String getStateAbbr(String stateName)
	{
		Session session = getSessionFactory().getCurrentSession();
		String queryString = "select state.abbreviation "
				+ "from State as state "
				+ "where state.stateName = :stateName ";
		String queryResult = (String) session.createQuery(queryString)
				.setParameter("stateName", stateName).uniqueResult();
		return queryResult;
	}
        
	/**
	 * @see stateDAO#getStateIdByAbbr(String)
	 */
	@Override
	public Long getStateIdByAbbr(String abbreviation) {
            Session session = getSessionFactory().getCurrentSession();
		String queryString = "select state.id "
				+ "from State as state "
				+ "where state.abbreviation = :abbreviation ";
		Long queryResult = (Long) session.createQuery(queryString)
				.setParameter("abbreviation", abbreviation).uniqueResult();
            return queryResult;
	}
}
