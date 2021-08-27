package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.ReportingPeriodDAO;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Transactional
public class ReportingPeriodDAOImpl extends HibernateDaoSupport implements ReportingPeriodDAO
{
	/**
	 * @see ReportingPeriodDAO#getReportPeriodName(Long)
	 * @author Adam Russell (18816)
	 */
	@Override
	public String getReportPeriodName(Long reportPeriodId)
	{
		Session session = getSessionFactory().getCurrentSession();
		String queryString;
		String queryResult;
		queryString = "select reportPeriod.name "
		            + "from ReportingPeriod as reportPeriod "
		            + "where reportPeriod.id = :reportPeriodId ";
		queryResult = (String) session.createQuery(queryString)
		              .setParameter("reportPeriodId", reportPeriodId).uniqueResult();
		return queryResult;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see ReportingPeriodDAO#getPreviousOutcomesReportingPeriod(ReportingPeriod)
	 */
	@Override
	public ReportingPeriod getPreviousOutcomesReportingPeriod(ReportingPeriod reportPeriod)
	{
		assert(reportPeriod != null);
		
		if (reportPeriod.getOutcomeAge() == null)
		{
			return null;
		}
		
		Session session = getSessionFactory().getCurrentSession();
		List<ReportingPeriod> previousReportPeriods;
		ReportingPeriod previousReportPeriod;
		String query;
		
		// In order to most accurately query for the period 2 years prior,
		// a range of plus or minus seven days will be applied.
		Calendar lowDateBoundary = (Calendar) reportPeriod.getEndReportingDate().clone();
		lowDateBoundary.add(Calendar.DAY_OF_YEAR, -7);
		Calendar highDateBoundary = (Calendar) reportPeriod.getEndReportingDate().clone();
		highDateBoundary.add(Calendar.DAY_OF_YEAR, 7);
		
		lowDateBoundary.add(Calendar.YEAR, -2);
		highDateBoundary.add(Calendar.YEAR, -2);
		
		// Get the previous outcomes reporting period.
		query = "select reportPeriod "
		      + "from ReportingPeriod as reportPeriod "
		      + "where reportPeriod.endReportingDate > to_date('%1$td-%1$tb-%1$ty') "
		      + "and reportPeriod.endReportingDate < to_date('%2$td-%2$tb-%2$ty') ";
		query = String.format(query, lowDateBoundary, highDateBoundary);
		previousReportPeriods = session.createQuery(query).list();
		if (previousReportPeriods.isEmpty())
		{
			return null;
		}
		assert(previousReportPeriods.size() == 1);
		previousReportPeriod = previousReportPeriods.get(0);
		
		return previousReportPeriod;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see ReportingPeriodDAO#getEnsuingReportPeriod(ReportingPeriod)
	 */
	@Override
	public ReportingPeriod getEnsuingReportPeriod(ReportingPeriod reportPeriod)
	{
		assert(reportPeriod != null);
		
		Session session = getSessionFactory().getCurrentSession();		
		List<ReportingPeriod> ensuingReportPeriods;
		ReportingPeriod ensuingReportPeriod;
		String query;
		
		// Get the report period directly following the given report period.
		query = "from ReportingPeriod as reportPeriod "
		      + "where reportPeriod.endReportingDate > to_date('%1$td-%1$tb-%1$ty') "
		      + "order by reportPeriod.endReportingDate asc";
		query = String.format(query, reportPeriod.getEndReportingDate());
		ensuingReportPeriods = session.createQuery(query).list();
		if (ensuingReportPeriods.isEmpty())
		{
			return null;
		}
		ensuingReportPeriod = ensuingReportPeriods.get(0);
		
		return ensuingReportPeriod;
	}

	/**
	 * @author Adam Russell (18816)
	 * @see ReportingPeriodDAO#getPrecedingReportPeriod(ReportingPeriod)
	 */
	@Override
	public ReportingPeriod getPrecedingReportPeriod(ReportingPeriod reportPeriod)
	{
		assert(reportPeriod != null);
		
		Session session = getSessionFactory().getCurrentSession();		
		List<ReportingPeriod> previousReportPeriods;
		ReportingPeriod previousReportPeriod;
		String query;
		
		// Get the report period directly preceding the given report period.
		query = "from ReportingPeriod as reportPeriod "
		      + "where reportPeriod.endReportingDate < to_date('%1$td-%1$tb-%1$ty') "
		      + "order by reportPeriod.endReportingDate desc";
		query = String.format(query, reportPeriod.getEndReportingDate());
		previousReportPeriods = session.createQuery(query).list();
		if (previousReportPeriods.isEmpty())
		{
			return null;
		}
		previousReportPeriod = previousReportPeriods.get(0);
		
		return previousReportPeriod;
	}
	
	/**
	 * @see ReportingPeriodDAO#getReportingPeriodSelectMap()
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getReportingPeriodSelectMap()
	{
		Session session = getSessionFactory().getCurrentSession();
		Calendar now = new GregorianCalendar();
		now.setTime(new Date());
		now.set(2019, 10, 1); // TODO: Remove this line. We are developing for the future currently.
		Map<String, String> selectMap;
		List<Object[]> queryResult;
		String query;
		query = "select reportPeriod.id, reportPeriod.name "
		      + "from ReportingPeriod as reportPeriod "
		      + "where reportPeriod.startReportingDate <= :startDate "
		      + "order by reportPeriod.endReportingDate desc ";
		queryResult = session.createQuery(query).setParameter("startDate", now).list();
		selectMap = CommonFunctions.getSelectMapFromQueryResult(queryResult);
		return selectMap;
	}
	
	/**
	 * @see ReportingPeriodDAO#getReportPeriodSelectMapForUser(SiteUser, Integer)
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getReportPeriodSelectMapForUser(
			SiteUser siteUser, Integer minimumTransmissions)
	{
		Session session = getSessionFactory().getCurrentSession();
		Map<String, String> selectMap;
		List<Object[]> queryResult;
		Query query;
		String queryString;
		String userRole = siteUser.getPrimaryUserRole().getDescription();
		String userSpecificClause = "";
		
		if (userRole.equals("State"))
		{
			userSpecificClause = "where state.id = :stateId";
		}
		else if (userRole.equals("Regional"))
		{
			userSpecificClause = "where state.id in (%s) and ("
			                   + "transmission.submissionStatus = 'Active' or "
			                   + "transmission.submissionStatus = 'Inactive') ";
			String statesInRegion = "select state.id "
			                      + "from State as state "
			                      + "join state.region as region "
			                      + "where region.id = %d ";
			statesInRegion = String.format(statesInRegion, siteUser.getRegion().getId());
			userSpecificClause = String.format(userSpecificClause, statesInRegion);
		}
		else
		{
			userSpecificClause = "where transmission.submissionStatus = 'Active' or "
			                   + "transmission.submissionStatus = 'Inactive' ";
		}
		
		queryString = "select reportPeriod.id, reportPeriod.name "
		            + "from ReportingPeriod as reportPeriod "
		            + "join reportPeriod.transmissions as transmission "
		            + "join transmission.state as state "
		            + "%s "
		            + "group by reportPeriod.id, reportPeriod.name, reportPeriod.endReportingDate "
		            + "having count(transmission) >= :minimumTransmissions "
		            + "order by reportPeriod.endReportingDate desc ";
		queryString = String.format(queryString, userSpecificClause);
		
		query = session.createQuery(queryString)
		        .setParameter("minimumTransmissions", minimumTransmissions.longValue());
		if (userRole.equals("State"))
		{
			query = query.setParameter("stateId", siteUser.getState().getId());
		}
		queryResult = query.list();
		selectMap = CommonFunctions.getSelectMapFromQueryResult(queryResult);
		return selectMap;
	}

	/**
	 * @see ReportingPeriodDAO#getReportPeriodSelectMapForUser(SiteUser)
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getReportPeriodSelectMapForUser(SiteUser siteUser) {
		return getReportPeriodSelectMapForUser(siteUser, 1);
	}
        
	/**
	 * @see ReportingPeriodDAO#getReportingPeriodIdByName(String)
	 */
	@Override
	public Long getReportingPeriodIdByName(String reportingPeriodName) {
            Session session = getSessionFactory().getCurrentSession();
            String queryString;
            Long queryResult;
            queryString = "select reportPeriod.id "
                        + "from ReportingPeriod as reportPeriod "
                        + "where reportPeriod.name = :reportingPeriodName ";
            queryResult = (Long) session.createQuery(queryString)
                          .setParameter("reportingPeriodName", reportingPeriodName).uniqueResult();
            return queryResult;
	}

}
