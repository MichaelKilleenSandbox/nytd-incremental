package gov.hhs.acf.cb.nytd.service.sampling.federal;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch.SortDirection;
import gov.hhs.acf.cb.nytd.models.CohortStatus;
import gov.hhs.acf.cb.nytd.models.SamplingRequest;
import gov.hhs.acf.cb.nytd.models.SamplingRequestHistory;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.helper.TempSamplingRecord;
import gov.hhs.acf.cb.nytd.models.helper.VwSamplingRequest;
import gov.hhs.acf.cb.nytd.models.sampling.federal.FederalSamplingContext;
import gov.hhs.acf.cb.nytd.service.impl.BaseServiceImpl;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class FederalSamplingServiceImpl extends BaseServiceImpl implements FederalSamplingService {
	
	public Long getCohortStatusId(Long cohortId, Long stateId)
	{	
		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<CohortStatus> criteriaQuery = criteriaBuilder.createQuery(CohortStatus.class);
		Root<CohortStatus> root = criteriaQuery.from(CohortStatus.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("cohortId"), cohortId),
				criteriaBuilder.equal(root.get("stateId"), stateId)
		);
		criteriaQuery.select(root);
		TypedQuery<CohortStatus> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		CohortStatus cohortStatus = null;
		Long cohortStatusId = 0L;
		try {
			if(!q.getResultList().isEmpty()) {
				cohortStatus = (CohortStatus) q.getSingleResult();
				if(cohortStatus != null)
				{
					cohortStatusId = cohortStatus.getId();
				}
			}			
		} catch (Exception e) {
			log.warn("Inside exception of getCohortSize");
			e.printStackTrace();
		}		
		return cohortStatusId;
	}
	
	
	public Long reSubmitSamplingRequest(FederalSamplingContext search)
	{
		int updateCnt = 0;
		log.debug("FederalSamplingServiceImpl.reSubmitSamplingRequest()");
		Long samplingRequestId = search.getSelectedSamplingRequestId();
		Query query = getSessionFactory().getCurrentSession().createSQLQuery("update samplingrequests set samplingMethod = :samplingMethod, samplingrequeststatusid = 51, lastsamplingstatusdate  = :lastsamplingstatusdate where samplingrequestid = :samplingrequestid ");
		if(search.isHasAlternateSamplingMethod())
			query.setParameter("samplingMethod", search.getAlternateSamplingMethod());
		else
			query.setParameter("samplingMethod", "SRS");
		query.setParameter("lastsamplingstatusdate", Calendar.getInstance());
		query.setParameter("samplingrequestid", samplingRequestId);
		updateCnt = query.executeUpdate();
		saveSamplingRequestHistory(samplingRequestId, 51L, search.getMessageId(), search.getLastAlternateSamplingMethod());
		if(updateCnt == 0)
			return 0L;
		else
			return 1L;
	}
	
	public Long saveSamplingRequest(FederalSamplingContext search)
	{
		log.debug("FederalSamplingServiceImpl.saveSamplingRequest()");
		Long samplingRequestId = 0L;
		Session session = getSessionFactory().getCurrentSession();
		SamplingRequest samplingRequest = new SamplingRequest();
		samplingRequest.setCohortStatusId(getCohortStatusId(search.getSelectedCohort(), search.getSelectedState()));
		if(search.isHasAlternateSamplingMethod())
			samplingRequest.setSamplingMethod(search.getAlternateSamplingMethod());
		else
			samplingRequest.setSamplingMethod("SRS");
		samplingRequest.setSamplingRequestStatusId(44L);
		samplingRequest.setSamplingStatusDate(Calendar.getInstance());
		
		try {
			session.saveOrUpdate(samplingRequest);
			samplingRequestId = samplingRequest.getId();
			saveSamplingRequestHistory(samplingRequest.getId(), 44L,search.getMessageId());
			session.flush();
		} catch (Exception e) {
			log.debug("Exception occured while saving Sampling Request");
			e.printStackTrace();
		}
		return samplingRequestId;
	}
	
	public List<VwSamplingRequest> searchSamplingRequest(FederalSamplingContext search)
	{
		List<VwSamplingRequest> samplingRequests = null;
		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<VwSamplingRequest> criteriaQuery = criteriaBuilder.createQuery(VwSamplingRequest.class);
		Root<VwSamplingRequest> root = criteriaQuery.from(VwSamplingRequest.class);
		Predicate predicateCohort = null;
		
		if( search!=null && search.getSelectedCohort() != null && search.getSelectedCohort()>0)
		{
			predicateCohort = criteriaBuilder.equal(root.get("cohortId"), search.getSelectedCohort());
		}
		Predicate predicateState = null;
		if(search!=null && search.getSelectedState()!= null && search.getSelectedState() > 0)
		{
			predicateState = criteriaBuilder.equal(root.get("stateId"), search.getSelectedState());
		}
		else
		{
			List<Long> stateIdList = new ArrayList<Long>();
			for(State state : search.getAvailableStates())
			{
				stateIdList.add(state.getId());
			}
			predicateState = root.get("stateId").in(stateIdList);
		}
		Predicate predicateRequestStatus = null;
		if(search!=null && search.getSelectedRequestStatus()!= null && search.getSelectedRequestStatus() > 0)
		{
			predicateRequestStatus = criteriaBuilder.equal(root.get("requestStatusId"), search.getSelectedRequestStatus());
		}
		Predicate predicateSamplingMethod = null;
		if(search!=null && search.getSelectedSamplingMethod()!= null && search.getSelectedSamplingMethod() > 0)
		{
			if(search.getSelectedSamplingMethod() == 1) 
			{
				predicateSamplingMethod = criteriaBuilder.and(criteriaBuilder.like(criteriaBuilder.upper(root.get("samplingMethod")),"SRS"));
			} 
			else if(search.getSelectedSamplingMethod() == 2) 
			{
				predicateSamplingMethod = criteriaBuilder.notEqual(root.get("samplingMethod"), "SRS");
			}
		}

		if (predicateCohort != null){
			if (predicateState != null){
				if (predicateRequestStatus != null){
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateState, predicateRequestStatus, predicateSamplingMethod));
					} else {
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateState, predicateRequestStatus));
					}
				} else {
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateState, predicateSamplingMethod));
					} else {
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateState));
					}
				}
			} else {
				if (predicateRequestStatus != null){
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateRequestStatus, predicateSamplingMethod));
					} else {
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateRequestStatus));
					}
				} else {
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateCohort, predicateSamplingMethod));
					} else {
						criteriaQuery.where(predicateCohort);
					}
				}
			}
		} else {	
			if (predicateState != null){
				if (predicateRequestStatus != null){
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateState, predicateRequestStatus, predicateSamplingMethod));
					} else {
						criteriaQuery.where(criteriaBuilder.and(predicateState, predicateRequestStatus));
					}
				} else {
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateState, predicateSamplingMethod));
					} else {
						criteriaQuery.where(predicateState);
					}
				}
			} else {
				if (predicateRequestStatus != null){
					if (predicateSamplingMethod != null){
						criteriaQuery.where(criteriaBuilder.and(predicateRequestStatus, predicateSamplingMethod));
					} else {
						criteriaQuery.where(predicateRequestStatus);
					}
				} else {
					if (predicateSamplingMethod != null){
						criteriaQuery.where(predicateSamplingMethod);
					}
				}
			}
		}

		// add user sort (default sort is message created date)
		if (search.getSortColumn() != null)
		{
			switch (search.getSortDirection())
			{
				case ASC:
					criteriaQuery.orderBy(criteriaBuilder.asc(root.get(search.getSortColumn())));
					break;
				case DESC:
					criteriaQuery.orderBy(criteriaBuilder.desc(root.get(search.getSortColumn())));
					break;
			}
		}
		else
		{
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));
			search.setSortColumn("createdDate");
			search.setSortDirection(SortDirection.DESC);
		}
		criteriaQuery.select(root);
		TypedQuery<VwSamplingRequest> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		samplingRequests = q.getResultList();
		
		return samplingRequests;
	}
	
	public SamplingRequest getSamplingRequest(FederalSamplingContext search)
	{
		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<SamplingRequest> criteriaQuery = criteriaBuilder.createQuery(SamplingRequest.class);
		Root<SamplingRequest> root = criteriaQuery.from(SamplingRequest.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("id"), search.getSelectedSamplingRequestId())
		);
		criteriaQuery.select(root);
		TypedQuery<SamplingRequest> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		
		return (SamplingRequest)q.getSingleResult();
	}
	/**
	 * Uploads the file to the specified filepath if its a CSV format
	 * If not stores the reason for the failure in the samplerequest table
	 */
	public String saveSample(File cohortSample,String cohortSampleFileName, String cohortSampleContentType,Long samplingRequestId,String filePath){
		String importStatus = "Failure";
		File destinationDir = new File(filePath+File.separator+cohortSampleFileName); 
	//	File destinationDir = new File("file:\\C:\\"+cohortSampleFileName);
		List<String> recNumbers  = null;
 		if(cohortSampleContentType.contains("application/vnd.ms-excel") || cohortSampleContentType.contains("text/csv"))
 		{
 			log.debug("file is in correct format");
			try {
				
					recNumbers = (List<String>)FileUtils.readLines(cohortSample,"ISO-8859-1");
					if(recNumbers != null && recNumbers.size() > 0)
					{
						int recsCnt = recNumbers.size();
						int cnt = 0;
						TempSamplingRecord tempSamplingRecord = null;
						Session session = getSessionFactory().getCurrentSession();
						for(String recNumber : recNumbers)
						{
							tempSamplingRecord = new TempSamplingRecord();
							tempSamplingRecord.setSamplingRequestId(samplingRequestId);
							tempSamplingRecord.setRecordNumber(recNumber);
							cnt++;
							session.save(tempSamplingRecord);
							if((cnt % 20 == 0) || cnt+1 == recsCnt)
							{
								session.flush();
								session.clear();
							}
						}
							session.flush();
							session.clear();
						
							
						Query query = session.getNamedQuery("applySampling");
						query.setParameter("samplingRequestId", samplingRequestId);
					//	query.setLong("samplingRequestId", samplingRequestId);
						query.list();
						
						/*Criteria criteria = session.createCriteria(VwSamplingRequest.class);
						criteria.add(Restrictions.eq("samplingRequestId", samplingRequestId));
						List list = criteria.list();
							
						if(list != null)
						{
							VwSamplingRequest vwSamplingRequest = (VwSamplingRequest) list.get(0);
							if(vwSamplingRequest.getImportStatusId() == 50L)
								importStatus = "Success";
						}*/
						if(getSamplingRquest(samplingRequestId).getImportStatusId() == 49L)
								importStatus = "Success";
					}
					log.debug("import Status on file upload:"+importStatus);
				/*FileUtils.touch(destinationDir);
				FileUtils.copyFile(cohortSample, destinationDir);*/
			} catch (IOException e) {
				log.debug("exception in saving sample");
				e.printStackTrace();
			}
			//logic to store the file in the database and execute a stored procedure
		} else {
			log.debug("incorrect file format");
			int updateRec = 0;

			Query query = getSessionFactory().getCurrentSession().createSQLQuery("update samplingrequests set importstatusid='50', " +
					"importsummary='" +Constants.INCORRECT_FILE_FORMAT+"' where samplingrequestId=:samplingRequestId");
			query.setParameter("samplingRequestId", samplingRequestId);
			updateRec = query.executeUpdate();
			if(updateRec>0) {
				importStatus = Constants.INCORRECT_FILE_FORMAT;
			}
		}
		
		return importStatus;
	}
	
	public VwSamplingRequest getSamplingRquest(Long samplingRequestId)
	{
		VwSamplingRequest vwSamplingRequest = null;
		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<VwSamplingRequest> criteriaQuery = criteriaBuilder.createQuery(VwSamplingRequest.class);
		Root<VwSamplingRequest> root = criteriaQuery.from(VwSamplingRequest.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("samplingRequestId"), samplingRequestId)
		);
		criteriaQuery.select(root);
		TypedQuery<VwSamplingRequest> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		List list = q.getResultList();
		if(list != null) {
			vwSamplingRequest = (VwSamplingRequest) list.get(0);
		}
		
		return  vwSamplingRequest;
	}

	/**
	 * retrieves the import summary from the samplingrequests table
	 * when the failure link is clicked
	 * @param samplingRequestId
	 * @return import summary
	 */
	public String getFailureReason(Long samplingRequestId){
		FederalSamplingContext samplingContext = new FederalSamplingContext();
		samplingContext.setSelectedSamplingRequestId(samplingRequestId);
		SamplingRequest samplingRequest = getSamplingRequest(samplingContext);		
		return samplingRequest.getImportSummary();
	}
	
	/**
	 * Retrieves the message id associated with the sample
	 * when the Sampling request has been provided with Comments or is Rejected
	 */
	public Map<String,String> getMessageIdMap(FederalSamplingContext search)
	{
		Query query = getSessionFactory().getCurrentSession().createSQLQuery("select sr.samplingrequestid, srh.messageid from samplingrequests sr,samplingrequesthistory " +
				"srh where sr.lastsamplingstatusdate=srh.createddate and requeststatusid between 45 and 46");
		List list = query.list();
		Map<String,String> messageMap = new HashMap<String,String>();
		if(list!=null) {
			messageMap = CommonFunctions.getSelectMapFromQueryResult(list);
		}
		
		return messageMap;
	}
	
	public boolean updateSamplingRequestStatus(Long samplingRequestId,Long requestStatusId, Long messageId)
	{
			boolean isUpdated = false;  
			int updateRecs = 0;
			Query query = getSessionFactory().getCurrentSession().createSQLQuery("update samplingrequests set samplingrequeststatusid = :samplingrequeststatusid, lastsamplingstatusdate = :lastsamplingstatusdate where samplingrequestId = :samplingrequestId");
			query.setParameter("samplingrequeststatusid", requestStatusId);
			query.setParameter("lastsamplingstatusdate", Calendar.getInstance());
			query.setParameter("samplingrequestId", samplingRequestId);
			updateRecs = query.executeUpdate();
			if(updateRecs > 0)
				isUpdated = true;
			saveSamplingRequestHistory(samplingRequestId, requestStatusId, messageId);
			return isUpdated;
	}
	
	private void saveSamplingRequestHistory(Long samplingRequestId, Long requestStatusId, Long messageId)
	{
		
		Session session = getSessionFactory().getCurrentSession();
		SamplingRequestHistory samplingRequestHistory = new SamplingRequestHistory();
		samplingRequestHistory.setRequestStatusId(requestStatusId);
		samplingRequestHistory.setSamplingRequestId(samplingRequestId);
		samplingRequestHistory.setDate(Calendar.getInstance());
		if(messageId > 0)
		{
			samplingRequestHistory.setMessageId(messageId);
		}
		session.saveOrUpdate(samplingRequestHistory);
		
	}
	
	private void saveSamplingRequestHistory(Long samplingRequestId, Long requestStatusId, Long messageId, String lastSamplingMethod)
	{
		
		Session session = getSessionFactory().getCurrentSession();
		SamplingRequestHistory samplingRequestHistory = new SamplingRequestHistory();
		samplingRequestHistory.setRequestStatusId(requestStatusId);
		samplingRequestHistory.setSamplingRequestId(samplingRequestId);
		samplingRequestHistory.setDate(Calendar.getInstance());
		if(lastSamplingMethod != null)
		{
			samplingRequestHistory.setLastSamplingMethod(lastSamplingMethod);
		}
		if(messageId > 0)
		{
			samplingRequestHistory.setMessageId(messageId);
		}
		session.saveOrUpdate(samplingRequestHistory);
		
	}
	
	public boolean updateCohortLock(FederalSamplingContext search,boolean applyLock)
	{
		boolean isUpdated = false;
		int updateCnt = 0;

		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<CohortStatus> criteriaQuery = criteriaBuilder.createQuery(CohortStatus.class);
		Root<CohortStatus> root = criteriaQuery.from(CohortStatus.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("id"), search.getSelectedCohortStatusId())
		);
		criteriaQuery.select(root);
		TypedQuery<CohortStatus> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
		CohortStatus cohortStatus =(CohortStatus) q.getSingleResult();
		
		if(applyLock)
		{
			cohortStatus.setPeriodLocked19(1L);
		}
		else
		{
			cohortStatus.setPeriodLocked19(0L);
		}
			try {
				getSessionFactory().getCurrentSession().update(cohortStatus);
				updateCnt = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		/*Query query = null;
		query = getSessionFactory().getCurrentSession().createQuery("update CohortStatus cs set  cs.periodLocked19 = :value where cs.id = :id");
		query.setLong("id",search.getSelectedCohortStatusId() );
		if(applyLock)
		{
			query = getSessionFactory().getCurrentSession().createSQLQuery("update cohortStatus set periodLocked19 = 1 where cohortStatusId = ?");
			query.setParameter(0, search.getSelectedCohortStatusId());
			query.setLong("value", 1L);
		}
		else
		{
			query = getSessionFactory().getCurrentSession().createSQLQuery("update cohortStatus set periodLocked19 = ? where cohortStatusId = ?");
			query.setLong(0, 0L);
			query.setParameter(1, search.getSelectedCohortStatusId());
			
			query.setLong("value", 0L);
		}
		updateCnt = query.executeUpdate();*/
		if(updateCnt > 0)
			isUpdated = true;
		return isUpdated;
	}
	
	public Long getCohortSize(Long stateId, Long cohortId)
	{
		Long cohortSize = 0L;
		Session dbSession = getSessionFactory().getCurrentSession();


		CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<CohortStatus> root = criteriaQuery.from(CohortStatus.class);

		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("stateId"), stateId), criteriaBuilder.equal(root.get("cohortId"), cohortId)));
	    criteriaQuery.select(root.get("cohortSize"));
		TypedQuery<Long> q = dbSession.createQuery(criteriaQuery);
		
		try {
			if(!q.getResultList().isEmpty()) {
				cohortSize = q.getSingleResult();
			}			
		} catch (Exception e) {
			log.warn("Inside exception of getCohortSize");
			e.printStackTrace();
		}
				
		return cohortSize;

		// old code to be removed
		/*Criteria criteria;
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(CohortStatus.class);
			criteria.add(Restrictions.and(Restrictions.eq("stateId", stateId), Restrictions.eq("cohortId", cohortId)));
		criteria.setProjection(Projections.property("cohortSize"));
		Object sizeObj = criteria.uniqueResult();
		if(sizeObj != null)
		{
			size =(Long) sizeObj;
		}
				
		return size;*/
	}
	
	public boolean  samplingRequestExists(FederalSamplingContext search)
	{
		boolean samplingRequestExists = false;
		Long cohortStatusId = getCohortStatusId(search.getSelectedCohort(), search.getSelectedState());

		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<SamplingRequest> root = criteriaQuery.from(SamplingRequest.class);
		criteriaQuery.select(criteriaBuilder.count(root));
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("cohortStatusId"), cohortStatusId)
		);
		Object count = getSessionFactory().getCurrentSession().createQuery(criteriaQuery).getSingleResult();
		
		if ((count != null) && (((Long)count).intValue() > 0))
			samplingRequestExists = true;
		return samplingRequestExists;
	}
}