package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.StateReportDAO;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Transactional
public class StateReportDAOImpl extends HibernateDaoSupport implements StateReportDAO
{
	
	/**
	 * @see StateReportDAO#getReportYearSelectMap()
	 */
	@Override
	public Map<String, String> getReportYearSelectMap()
	{		
		Session session = getSessionFactory().getCurrentSession();
		Map<String, String> selectMap;
		List<Object[]> queryResult;
		String query;
		query = "select stateReport.rpyear, stateReport.rpyear "
		      + "from Statereport as stateReport "
		      + "group by stateReport.rpyear order by stateReport.rpyear desc ";
		queryResult = session.createQuery(query).list();
		selectMap = CommonFunctions.getSelectMapFromQueryResult(queryResult);
		return selectMap;
	}
	
	/**
	 * @see StateReportDAO#getPopulationTypeSelectMap()
	 */
	@Override
	public Map<String, String> getPopulationTypeSelectMap()
	{		
		Session session = getSessionFactory().getCurrentSession();
		Map<String, String> selectMap;
		List<Object[]> queryResult;
		String query;
		query = "select stateReport.populationtype, stateReport.populationtype "
		      + "from Statereport as stateReport "
		      + "group by stateReport.populationtype order by stateReport.populationtype asc ";
		queryResult = session.createQuery(query).list();
		selectMap = CommonFunctions.getSelectMapFromQueryResult(queryResult);
		return selectMap;
	}
	
}
