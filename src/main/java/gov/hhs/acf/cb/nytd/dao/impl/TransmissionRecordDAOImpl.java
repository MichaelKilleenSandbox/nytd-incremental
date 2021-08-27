package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.TransmissionRecordDAO;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.models.TransmissionRecord;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Transactional
public class TransmissionRecordDAOImpl extends HibernateDaoSupport implements TransmissionRecordDAO
{
	/**
	 * @author Adam Russell (18816)
	 * @see TransmissionRecordDAO#getPreviousBufferCaseRecords(ReportingPeriod, State)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TransmissionRecord> getPreviousBufferCaseRecords(ReportingPeriod previousReportPeriod, State state)
	{
		assert(previousReportPeriod != null);
		
		Session session = getSessionFactory().getCurrentSession();
		List<TransmissionRecord> records = new ArrayList<TransmissionRecord>(0);
		String query;
		
		query = "select record "
		      + "from TransmissionRecord as record "
		      + "inner join record.outcomePopulation as population "
		      + "inner join record.transmission as transmission "
		      + "inner join transmission.reportingPeriod as reportPeriod "
		      + "where reportPeriod.id = %d "
		      + "and transmission.submissionStatus = 'Active' " 
		      + "and population.name = 'Pre-buffer' ";
		
		query = String.format(query, previousReportPeriod.getId());
		
		records = session.createQuery(query).list();
		
		return records;
	}

	/**
	 * @author Adam Russell
	 * @see TransmissionRecordDAO#getPreviousRecordForServedYouth(TransmissionRecord, State)
	 */
	@Override
	public TransmissionRecord getPreviousRecordForServedYouth(TransmissionRecord transmissionRecord, State state)
	{
		assert(transmissionRecord.getServedPopulation() != null);
		
		Session session = getSessionFactory().getCurrentSession();
		List<TransmissionRecord> previousRecords;
		TransmissionRecord previousRecord;
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<TransmissionRecord> criteriaQuery = criteriaBuilder.createQuery(TransmissionRecord.class);
		Root<TransmissionRecord> root = criteriaQuery.from(TransmissionRecord.class);
		root.join("transmission", JoinType.LEFT).alias("transmission");
		root.join("reportingPeriod", JoinType.LEFT).alias("reportingPeriod");
		criteriaQuery.where(
			criteriaBuilder.and(
					criteriaBuilder.equal(root.get("recordNumber"), transmissionRecord.getRecordNumber()),
					criteriaBuilder.isNotNull(root.get("servedPopulation")),
					criteriaBuilder.equal(root.get("transmission").get("submissionStatus"), "Active"),
					criteriaBuilder.lessThan(root.get("reportingPeriod").get("endReportingDate"), transmissionRecord.getTransmission().getReportingPeriod().getEndReportingDate())
			)
		);
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("reportingPeriod").get("endReportingDate")));
		criteriaQuery.select(root);
		TypedQuery<TransmissionRecord> q = session.createQuery(criteriaQuery);	
		previousRecords = q.getResultList();
		
		if (!previousRecords.isEmpty())
		{
			previousRecord = previousRecords.get(0);
			return previousRecord;
		}
		
		return null;
	}
	
	/**
	 * @author Adam Russell
	 * @see TransmissionRecordDAO#getPostBufferRecordForBaselineYouth(TransmissionRecord, ReportingPeriod, State)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TransmissionRecord getPostBufferRecordForBaselineYouth(TransmissionRecord transmissionRecord,
			ReportingPeriod reportPeriod, State state)
	{
		assert(transmissionRecord != null);
		assert(reportPeriod != null);
		assert(transmissionRecord.getOutcomePopulation() != null);
		assert(transmissionRecord.getOutcomePopulation().getName().equalsIgnoreCase("Baseline"));
		
		Session session = getSessionFactory().getCurrentSession();
		TransmissionRecord nextRecord = null;
		String query;
		
		// Only run query if the transmission is a "subsequent" or "corrected"-type
		if (transmissionRecord.getTransmission().getTransmissionType().getName().equalsIgnoreCase("subsequent")
		    || transmissionRecord.getTransmission().getTransmissionType().getName().equalsIgnoreCase("corrected"))
		{
			List<TransmissionRecord> nextRecords;
			
			query = "select record "
			      + "from TransmissionRecord as record "
			      + "inner join record.transmission as transmission "
			      + "inner join record.outcomePopulation as population "
			      + "inner join transmission.reportingPeriod as reportPeriod "
			      + "where record.recordNumber = '%s' "
			      + "and population.name = 'Post-buffer' "
			      + "and reportPeriod.id = %d "
			      + "and transmission.submissionStatus = 'Active' ";
			query = String.format(query, transmissionRecord.getRecordNumber(), reportPeriod.getId());
			nextRecords = session.createQuery(query).list();
			if (nextRecords.isEmpty())
			{
				return null;
			}
			assert(nextRecords.size() == 1);
			nextRecord = nextRecords.get(0);
		}
		
		return nextRecord;
	}
	
	/**
	 * @author Adam Russell
	 * @see TransmissionRecordDAO#getPostBufferRecordForPreBufferYouth(TransmissionRecord, Transmission))
	 */
	@Override
	public TransmissionRecord getPostBufferRecordForPreBufferYouth(
			TransmissionRecord transmissionRecord, Transmission nextTransmission)
	{
		assert(transmissionRecord != null);
		assert(nextTransmission != null);
		assert(transmissionRecord.getOutcomePopulation() != null);
		assert(transmissionRecord.getOutcomePopulation().getName().equalsIgnoreCase("Pre-buffer"));
		
		Session session = getSessionFactory().getCurrentSession();
		String query;
		List<TransmissionRecord> nextRecords;
		TransmissionRecord nextRecord;
		
		query = "select record "
		      + "from TransmissionRecord as record "
		      + "inner join record.transmission as transmission "
		      + "inner join record.outcomePopulation as population "
		      + "where record.recordNumber = '%s' "
		      + "and population.name = 'Post-buffer' "
		      + "and transmission.id = %d ";
		query = String.format(query, transmissionRecord.getRecordNumber(), nextTransmission.getId());
		nextRecords = session.createQuery(query).list();
		if (nextRecords.isEmpty())
		{
			return null;
		}
		assert(nextRecords.size() == 1);
		nextRecord = nextRecords.get(0);
		
		return nextRecord;
	}
	
	/**
	 * @author Adam Russell
	 * @see TransmissionRecordDAO#getPreviousRecordForPostBufferYouthComparison(TransmissionRecord, ReportingPeriod, State)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TransmissionRecord getPreviousRecordForPostBufferYouthComparison(
			TransmissionRecord transmissionRecord, ReportingPeriod reportPeriod, State state)
	{
		assert(transmissionRecord != null);
		assert(reportPeriod != null);
		assert(transmissionRecord.getOutcomePopulation() != null);
		assert(transmissionRecord.getOutcomePopulation().getName().equalsIgnoreCase("Post-buffer"));
		
		Session session = getSessionFactory().getCurrentSession();
		List<TransmissionRecord> previousRecords;
		List<Long> activeSubmissionIds;
		TransmissionRecord previousRecord;
		String query;
		
		query = "select record "
		      + "from TransmissionRecord as record "
		      + "inner join record.transmission as transmission "
		      + "inner join transmission.reportingPeriod as reportPeriod "
		      + "where record.recordNumber = '%s' "
		      + "and reportPeriod.id = %d "
		      + "and transmission.submissionStatus = 'Active' ";
		query = String.format(query, transmissionRecord.getRecordNumber(), reportPeriod.getId());
		previousRecords = session.createQuery(query).list();
		if (previousRecords.isEmpty())
		{
			return null;
		}
		assert(previousRecords.size() == 1);
		previousRecord = previousRecords.get(0);
		
		return previousRecord;
	}

	/**
	 * @author Adam Russell
	 * @see TransmissionRecordDAO#getBaselineRecordForFollowup19YouthComparison(TransmissionRecord, ReportingPeriod, State)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TransmissionRecord getBaselineRecordForFollowup19YouthComparison(
			TransmissionRecord transmissionRecord, ReportingPeriod previousOutcomesReportPeriod, State state)
	{
		assert(transmissionRecord != null);
		assert(transmissionRecord.getOutcomePopulation() != null);
		assert(transmissionRecord.getOutcomePopulation().getName().equalsIgnoreCase("Follow-up 19"));
		
		if (previousOutcomesReportPeriod == null)
		{
			return null;
		}
		
		Session session = getSessionFactory().getCurrentSession();
		List<TransmissionRecord> previousRecords;
		String query;
		
		// In order to most accurately query for the period 1.5 years prior,
		// a range of plus or minus seven days will be applied.
		Calendar lowDateBoundary = (Calendar) transmissionRecord.getTransmission()
		                           .getReportingPeriod().getEndReportingDate().clone();
		lowDateBoundary.add(Calendar.DAY_OF_YEAR, -7);
		Calendar highDateBoundary = (Calendar) transmissionRecord.getTransmission()
                                  .getReportingPeriod().getEndReportingDate().clone();
		highDateBoundary.add(Calendar.DAY_OF_YEAR, 7);
		
		lowDateBoundary.add(Calendar.YEAR, -2);
		highDateBoundary.add(Calendar.YEAR, -2);
		lowDateBoundary.add(Calendar.MONTH, 6);
		highDateBoundary.add(Calendar.MONTH, 6);
		
		query = "select record "
		      + "from TransmissionRecord as record "
		      + "inner join record.transmission as transmission "
		      + "inner join record.outcomePopulation as population "
		      + "inner join transmission.reportingPeriod as reportPeriod "
		      + "where record.recordNumber = '%1$s' "
		      + "and transmission.submissionStatus = 'Active' "
		      + "and (reportPeriod.id = %2$d "
		      + "or (reportPeriod.endReportingDate > to_date('%3$td-%3$tb-%3$ty') "
		      + "and reportPeriod.endReportingDate < to_date('%4$td-%4$tb-%4$ty'))) "
		      + "and (population.name = 'Baseline' "
		      + "or population.name = 'Pre-buffer') "
		      + "order by reportPeriod.endReportingDate asc ";
		
		query = String.format(query, transmissionRecord.getRecordNumber(),
				previousOutcomesReportPeriod.getId(), lowDateBoundary, highDateBoundary);
		
		previousRecords = session.createQuery(query).list();
		
		if (!previousRecords.isEmpty())
		{
			TransmissionRecord previousRecord;
			previousRecord = previousRecords.get(0);
			return previousRecord;
		}
		
		return null;
	}

	/**
	 * @author Adam Russell
	 * @see TransmissionRecordDAO#getFollowup19RecordForFollowup21YouthComparison(TransmissionRecord, ReportingPeriod, State)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TransmissionRecord getFollowup19RecordForFollowup21YouthComparison(
			TransmissionRecord transmissionRecord, ReportingPeriod previousOutcomesReportPeriod, State state)
	{
		assert(transmissionRecord != null);
		assert(transmissionRecord.getOutcomePopulation() != null);
		assert(transmissionRecord.getOutcomePopulation().getName().equalsIgnoreCase("Follow-up 21"));
		
		if (previousOutcomesReportPeriod == null)
		{
			return null;
		}
		
		Session session = getSessionFactory().getCurrentSession();
		List<TransmissionRecord> previousRecords;
		String query;
		
		query = "select record "
		      + "from TransmissionRecord as record "
		      + "inner join record.transmission as transmission "
		      + "inner join transmission.reportingPeriod as reportPeriod "
		      + "inner join record.outcomePopulation as population "
		      + "where record.recordNumber = '%s' "
		      + "and transmission.submissionStatus = 'Active' "
		      + "and population.name = 'Follow-up 19' "
		      + "and reportPeriod.id = %d ";
	
		query = String.format(query, transmissionRecord.getRecordNumber(),
				previousOutcomesReportPeriod.getId());
		
		previousRecords = session.createQuery(query).list();
		
		if (!previousRecords.isEmpty())
		{
			assert(previousRecords.size() == 1);
			TransmissionRecord previousRecord = previousRecords.get(0);
			return previousRecord;
		}
		
		return null;
	}
}
