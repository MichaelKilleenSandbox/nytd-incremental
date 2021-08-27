package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.SubmissionDAO;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Transactional
public class SubmissionDAOImpl extends HibernateDaoSupport implements SubmissionDAO
{
	/**
	 * @see SubmissionDAO#getSubmissionSelectMap(Long, Long)
	 */
	@Override
	public Map<String, String> getSubmissionSelectMap(Long stateId, Long reportPeriodId)
	{
		Session session = getSessionFactory().getCurrentSession();
		Map<String, String> selectMap = new LinkedHashMap<String, String>();
		List<Transmission> queryResult;
		String query;
		query = "select transmission "
		      + "from Transmission as transmission "
		      + "join transmission.reportingPeriod as reportPeriod "
		      + "join transmission.state as state "
		      + "where state.id = :stateId "
		      + "and reportPeriod.id = :reportPeriodId "
		      + "and (transmission.submissionStatus = 'Active' "
		      + "or transmission.submissionStatus = 'Inactive') "
		      + "order by transmission.id desc ";
		queryResult = session.createQuery(query).setParameter("stateId", stateId).setParameter("reportPeriodId", reportPeriodId).list();
		for (Transmission submission : queryResult)
		{
			selectMap.put(String.valueOf(submission.getId()), String.valueOf(submission.getId()));
		}
		return selectMap;
	}
	
	/**
	 * @see SubmissionDAO#getActiveSubmissionId(Long, Long)
	 * @author Adam Russell (18816)
	 */
	@Override
	public Long getActiveSubmissionId(Long stateId, Long reportPeriodId)
	{
		Session session = getSessionFactory().getCurrentSession();
		Long transmissionId;
		String queryString;
		
		queryString = "select transmission.id "
		            + "from Transmission as transmission "
		            + "join transmission.reportingPeriod as reportPeriod "
		            + "join transmission.state as state "
		            + "where transmission.submissionStatus = 'Active' "
		            + "and state.id = :stateId "
		            + "and reportPeriod.id = :reportPeriodId ";
		transmissionId = (Long) session.createQuery(queryString)
		                 .setParameter("stateId", stateId)
		                 .setParameter("reportPeriodId", reportPeriodId).uniqueResult();
		
		return transmissionId;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see SubmissionDAO#getActiveSubmissionIds(State)
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getActiveSubmissionIds(State state)
	{
		assert(state != null);
		
		Session session = getSessionFactory().getCurrentSession();
		String query;
		List<Long> activeSubmissionIds = new LinkedList<Long>();
		
		query = "select transmission.id "
		      + "from Transmission as transmission "
		      + "join transmission.state as state "
		      + "where state.id = :stateId "
		      + "and transmission.submissionStatus = 'Active' ";
		
		activeSubmissionIds = session.createQuery(query).setParameter("stateId", state.getId()).list();
		
		return activeSubmissionIds;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see SubmissionDAO#getActiveSubmissions(State)
	 */
	@SuppressWarnings("unchecked")
	public List<Transmission> getActiveSubmissions(State state)
	{
		assert(state != null);
		
		Session session = getSessionFactory().getCurrentSession();
		String queryString;
		List<Transmission> activeSubmissions = new LinkedList<Transmission>();
		
		queryString = "select transmission "
		            + "from Transmission as transmission "
		            + "join transmission.state as state "
		            + "where state.id = :stateId "
		            + "and submission.submissionStatus = 'Active' ";

		activeSubmissions = session.createQuery(queryString).setParameter("stateId", state.getId()).list();
		
		return activeSubmissions;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see SubmissionDAO#getSubmissionForReportPeriod(State, ReportingPeriod)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Transmission getSubmissionForReportPeriod(State state, ReportingPeriod reportPeriod)
	{
		assert(reportPeriod != null);
		
		Session session = getSessionFactory().getCurrentSession();
		String query;
		Transmission activeSubmission;
		
		query = "select transmission "
		      + "from Transmission as transmission "
		      + "inner join transmission.reportingPeriod as reportPeriod "
		      + "inner join transmission.state as state "
		      + "where reportPeriod.id = :reportPeriodId "
		      + "and state.id = :stateId "
		      + "and transmission.submissionStatus = :active";
		activeSubmission = (Transmission) session.createQuery(query)
		                   .setParameter("stateId", state.getId())
		                   .setParameter("reportPeriodId", reportPeriod.getId())
		                   .setParameter("active",Constants.TRANSMISSION_ACTIVE_SUBMISSION).uniqueResult();
		
		return activeSubmission;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see SubmissionDAO#getSubmissionFileNumberOfReportPeriod(State, ReportingPeriod)
	 */
	@Override
	public String getSubmissionFileNumberOfReportPeriod(State state, ReportingPeriod reportPeriod)
	{
		assert(reportPeriod != null);
		
		String fileNumber = "N/A";
		Transmission submission;
		submission = getSubmissionForReportPeriod(state, reportPeriod);
		if (submission != null)
		{
			fileNumber = submission.getId().toString();
		}
		
		return fileNumber;
	}
}
