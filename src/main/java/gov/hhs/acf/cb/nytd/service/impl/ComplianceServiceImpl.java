/**
 * Filename: PenaltyServiceImpl.java
 * 
 * Copyright 2009, ICF International Created: Jun 25, 2009 Author: 16939
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.actions.compliance.ComplianceSearch;
import gov.hhs.acf.cb.nytd.actions.penalty.PenaltySearch;
import gov.hhs.acf.cb.nytd.actions.report.CohortSearch;
import gov.hhs.acf.cb.nytd.actions.report.ErrorTypeSearch;
import gov.hhs.acf.cb.nytd.actions.report.OutcomesReportSearch;
import gov.hhs.acf.cb.nytd.dao.impl.ExtendedDueDateDaoImpl;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.CohortResultDTO;
import gov.hhs.acf.cb.nytd.models.helper.VwElementPenalty;
import gov.hhs.acf.cb.nytd.models.helper.VwTransmissionStatus;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.hibernate.type.FloatType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 13873
 */
public class ComplianceServiceImpl extends BaseServiceImpl implements ComplianceService
{

	private static Double MAX_PENALTY = 2.5;
	private static Long FLAG_VALUE_1 = 1L;

	
	
	public Double calcFileSubmissionPenalty(Long transmissionId)
	{
		// calculated penalty value
		Double penalty = 0.0;

		/*
		 * elements included in the file submission standards must be 100% error
		 * free. If any element is not 100% a 2.5% penalty is applied
		 */
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("sumPenaltyForCategories");
		qry.setParameter("transmissionId", transmissionId);
		qry.setParameterList("categories", getFileSubmissionStandardsCategories());

		qry.setCacheable(true);
		BigDecimal result = (BigDecimal) qry.uniqueResult();

		penalty = result.doubleValue();
		if (penalty > 2.5)
		{
			penalty = 2.5;
		}

		return penalty;
	}

	public Double calcDataPenalty(Long transmissionId, ComplianceCategory category)
	{
		// calculated penalty value
		Double penalty = 0.0;

		// elements included in the data standards penalty must be 90%
		// or greater error free. If any element is less than 90% a 1.25%
		// penalty is applied and added to any outcomes penalties
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("sumPenaltyForCategory");
		qry.setParameter("transmissionId", transmissionId);
		qry.setParameter("category", category);

		qry.setCacheable(true);
		BigDecimal result = (BigDecimal) qry.uniqueResult();

		penalty = result.doubleValue();
		if (penalty > 1.25)
		{
			penalty = 1.25;
		}

		return penalty;
	}

	public Double calcTransmissionPenalty(Long transmissionId)
	{
		Double penalty = 0.0;

		// sum penalty value for all errors
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("sumPenaltyForTransmission");
		qry.setParameter("transmissionId", transmissionId);

		qry.setCacheable(true);
		BigDecimal result = (BigDecimal) qry.uniqueResult();

		penalty = result.doubleValue();
		if (penalty > 2.5)
		{
			penalty = 2.5;
		}

		return penalty;
	}

	public List<ComplianceCategory> getFileSubmissionStandardsCategories()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery(
				"getFileSubmissionStandardsCategories");

		qry.setCacheable(true);
		List<ComplianceCategory> results = qry.list();

		return results;
	}

	public List<ComplianceCategory> getDataStandardsCategories()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getDataStandardsCategories");

		qry.setCacheable(true);
		List<ComplianceCategory> results = qry.list();

		return results;
	}

	public List<ComplianceCategory> getNonPenaltyCategories()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getNonPenaltyCategories");

		qry.setCacheable(true);
		List<ComplianceCategory> results = qry.list();

		return results;
	}

	@Override
	public List<ComplianceCategory> getAggregateErrorCategories()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getAggregateErrorCategories");

		qry.setCacheable(true);
		List<ComplianceCategory> results = qry.list();

		return results;
	}

	@Override
	public List<ComplianceCategory> getRecordErrorCategories()
	{
		return getNonPenaltyCategories();
	}

	@Override
	public List<ComplianceCategory> getTransmissionErrorCategories()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getTransmissionErrorCategories");

		qry.setCacheable(true);
		List<ComplianceCategory> results = qry.list();

		return results;
	}

	@Override
	// public Integer getCountDataQualtiyAdvisories(Long transmissionId)
	public List<Integer> getCountDataQualtiyAdvisories(Long transmissionId)
	{

		List<Integer> dqaCnts = new ArrayList<Integer>();
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countDQAs");
		qry.setParameter("transmissionId", transmissionId);
		qry.setCacheable(true);
		Object[] cnts = (Object[]) qry.uniqueResult();
		dqaCnts.add((Integer) cnts[0]);
		dqaCnts.add((Integer) cnts[1]);
		return dqaCnts;

	}

	public Integer getErrorCountForCategory(Long transmissionId, ComplianceCategory category)
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countErrorsForCategory");
		qry.setParameter("transmissionId", transmissionId);
		qry.setParameter("category", category);

		qry.setCacheable(true);
		Long result = (Long) qry.uniqueResult();

		return result.intValue();
	}

	@Override
	public Integer getErrorCountForCategories(Long transmissionId, List<ComplianceCategory> categories)
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countErrorsForCategories");
		qry.setParameter("transmissionId", transmissionId);
		qry.setParameterList("categories", categories);

		qry.setCacheable(true);
		Long result = (Long) qry.uniqueResult();

		return result.intValue();

	}

	public List<NytdError> getErrorsForCategories(Long transmissionId, List<ComplianceCategory> categories)
	{
		List<NytdError> results = (List<NytdError>) getHibernateTemplate().findByNamedQueryAndNamedParam(
				"getErrorsForCategories", new String[] { "transmissionId", "categories" },
				new Object[] { transmissionId, categories });

		return results;
	}

	@Override
	public boolean isCompliant(Long transmissionId)
	{
		Double penalty = calcTransmissionPenalty(transmissionId);

		if (penalty > 0)
		{
			return false;
		}

		return true;
	}

	//TODO: there is no replacement for sqlRestriction(), rewrite with custom or/and use Named Native Query
	//https://discourse.hibernate.org/t/deprecation-of-hibernate-criteria-and-how-we-can-still-prevent-it/788/18
	//https://stackoverflow.com/questions/5182701/equivalent-of-hibernates-restrictions-sqlrestriction-in-jpa2-criteria-api
	@Override
	public ComplianceSearch searchDataAggregates(ComplianceSearch search)
	{
		log.info("TODO: No replacement of sqlRestriction() available, rewrite with custom or/and use Named Native Query");
		
		// Create a criteria object for DataAggregate
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(DataAggregate.class);

		// map of entity association criterias
		Map<String, Criteria> entityMap = new HashMap<String, Criteria>();
		entityMap.put("transmission", criteria.createCriteria("transmission"));
		entityMap.put("aggregateType", criteria.createCriteria("aggregateType"));
		entityMap.put("element", criteria.createCriteria("element"));
		entityMap.put("complianceStandard", entityMap.get("element").createCriteria("complianceStandard"));

		// restrict to elementCompliance aggregate type the transmission id is
		// passed in the search object
		entityMap.get("transmission").add(Restrictions.eq("id", search.getTransmissionId()));
		entityMap.get("aggregateType").add(
				Restrictions.sqlRestriction("lower({alias}.codeDescription) = lower(?)", "ElementCompliance",
						StringType.INSTANCE));

		// add sort
		if (search.getSortColumn() != null)
		{
			switch (search.getSortDirection())
			{
				case ASC:
					if (search.getSortAssociation() != null && !search.getSortAssociation().isEmpty())
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.asc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.asc(search.getSortColumn()));
					}
					break;
				case DESC:
					if (search.getSortAssociation() != null && !search.getSortAssociation().isEmpty())
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.desc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.desc(search.getSortColumn()));
					}
					break;
			}
		}
		else
		{
			// default sort is element id
			search.setSortAssociation("element");
			search.setSortColumn("id");
			search.setSortDirection(ComplianceSearch.SortDirection.ASC);
			entityMap.get("element").addOrder(Order.asc("id"));
		}

		// eagerly fetch element and complianceStandard associations
		criteria.setFetchMode("element", FetchMode.JOIN);
		entityMap.get("element").setFetchMode("complianceStandard", FetchMode.JOIN);

		/*
		 * ignore pagesize for aggregate compliance search. always return all
		 * element aggregates for the specified transmission id
		 */
		List<DataAggregate> results = criteria.list();
		search.setPageResults(results);

		return search;
	}
	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	public ComplianceSearch searchDataQualityAggregates(ComplianceSearch search)
	{
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		// create a criteria object for DataQualityAdvAggregate
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(
				DataQualityAdvAggregate.class);

		/*
		 * map of entity association criterias (needed to support complex sorts on
		 * association properties)
		 */
		Map<String, Criteria> entityMap = new HashMap<String, Criteria>();
		entityMap.put("dataQualityAdvStandard", criteria.createCriteria("dataQualityAdvStandard"));
		entityMap.put("allowedValue", entityMap.get("dataQualityAdvStandard").createCriteria("allowedValue"));
		entityMap.put("element", entityMap.get("allowedValue").createCriteria("element"));

		// add restriction for transmission id
		criteria.createCriteria("transmission").add(Restrictions.eq("id", search.getTransmissionId()));

		// eagerly fetch associations
		criteria.setFetchMode("dataQualityAdvStandard", FetchMode.JOIN);
		entityMap.get("dataQualityAdvStandard").setFetchMode("allowedValue", FetchMode.JOIN);
		entityMap.get("allowedValue").setFetchMode("element", FetchMode.JOIN);

		// add sort
		if (search.getSortColumn() != null)
		{
			switch (search.getSortDirection())
			{
				case ASC:
					if (search.getSortAssociation() != null)
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.asc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.asc(search.getSortColumn()));
					}
					break;
				case DESC:
					if (search.getSortAssociation() != null)
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.desc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.desc(search.getSortColumn()));
					}
					break;
			}
		}
		else
		{
			// default sort is element id
			search.setSortAssociation("element");
			search.setSortColumn("id");
			search.setSortDirection(ComplianceSearch.SortDirection.ASC);
			entityMap.get("element").addOrder(Order.asc("id"));
		}

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<DataQualityAdvAggregate> results = criteria.list();
		search.setPageResults(results);
		search.setPageSize(0);

		return search;
	}

	public ComplianceSearch searchRecordErrors(ComplianceSearch search)
	{
		// create a criteria object for RecordLevelError
		DetachedCriteria resultCriteria = DetachedCriteria.forClass(RecordLevelError.class);

		// map of entity association criterias
		Map<String, DetachedCriteria> entityMap = new HashMap<String, DetachedCriteria>();
		entityMap.put("complianceCategory", resultCriteria.createCriteria("complianceCategory"));
		entityMap.put("problemDescription", resultCriteria.createCriteria("problemDescription"));
		entityMap.put("nonCompliance", resultCriteria.createCriteria("nonCompliance"));
		entityMap.put("transmission", entityMap.get("nonCompliance").createCriteria("transmission"));
		entityMap.put("datum", entityMap.get("nonCompliance").createCriteria("datum"));
		entityMap.put("element", entityMap.get("datum").createCriteria("element"));
		entityMap.put("transmissionRecord", entityMap.get("datum").createCriteria("transmissionRecord"));

		// add restriction for transmission id
		entityMap.get("transmission").add(Restrictions.eq("id", search.getTransmissionId()));

		// add record number restriction
		Disjunction disjunction = Restrictions.disjunction();
		entityMap.get("transmissionRecord").add(disjunction);
		List<String> recordNumbers = search.getRecordNumbers();
		if (recordNumbers != null && !recordNumbers.isEmpty())
		{
			for (String recordNumber : recordNumbers)
			{
				recordNumber = new StringBuffer().append("%").append(recordNumber.trim()).append("%").toString();
				disjunction.add(Restrictions.ilike("recordNumber", recordNumber));
			}
		}

		// add element number restriction
		String elementNumber = search.getElementNumber();
		if (elementNumber != null && !elementNumber.isEmpty() && !elementNumber.equals("All"))
		{
			entityMap.get("element").add(Restrictions.eq("name", search.getElementNumber()));
		}

		// add compliance category restriction
		String complianceType = search.getComplianceType();
		if (complianceType != null && !complianceType.isEmpty() && !complianceType.equals("All"))
		{
			entityMap.get("complianceCategory").add(Restrictions.eq("name", complianceType));
		}

		// execute count query to return row count
		resultCriteria.setProjection(Projections.rowCount());
		Criteria countCriteria = resultCriteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
		Long lRowCount = (Long)countCriteria.uniqueResult();
		search.setRowCount(lRowCount.intValue());
		//search.setRowCount(((Integer) countCriteria.uniqueResult()));
		resultCriteria.setProjection(null);
		resultCriteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// add sort
		if (search.getSortAssociation() == null
				|| !search.getSortAssociation().equalsIgnoreCase("problemDescription"))
		{
			if (search.getSortColumn() != null)
			{
				switch (search.getSortDirection())
				{
					case ASC:
						if (search.getSortAssociation() != null
								&& !search.getSortAssociation().equalsIgnoreCase("problemDescription"))
						{
							entityMap.get(search.getSortAssociation()).addOrder(Order.asc(search.getSortColumn()));
						}
						else
						{
							resultCriteria.addOrder(Order.asc(search.getSortColumn()));
						}
						break;
					case DESC:
						if (search.getSortAssociation() != null
								&& !search.getSortAssociation().equalsIgnoreCase("problemDescription"))
						{
							entityMap.get(search.getSortAssociation()).addOrder(Order.desc(search.getSortColumn()));
						}
						else
						{
							resultCriteria.addOrder(Order.desc(search.getSortColumn()));
						}
						break;
				}
			}
			else
			{
				// default sort is element id
				search.setSortAssociation("transmissionRecord");
				search.setSortColumn("recordNumber");
				search.setSortDirection(ComplianceSearch.SortDirection.ASC);
				entityMap.get("transmissionRecord").addOrder(Order.asc("recordNumber"));
			}
		}

		// eagerly fetch required associations in one db transaction
		resultCriteria.setFetchMode("complianceCategory", FetchMode.JOIN);
		resultCriteria.setFetchMode("problemDescription", FetchMode.JOIN);
		resultCriteria.setFetchMode("nonCompliance", FetchMode.JOIN);
		entityMap.get("nonCompliance").setFetchMode("transmission", FetchMode.JOIN);
		entityMap.get("nonCompliance").setFetchMode("datum", FetchMode.JOIN);
		entityMap.get("datum").setFetchMode("element", FetchMode.JOIN);
		entityMap.get("datum").setFetchMode("transmissionRecord", FetchMode.JOIN);

		// create a copy of the primary criteria for the comparator sort query
		DetachedCriteria sortCriteria = copyCriteria(resultCriteria);

		// execute result query. limit results if page size > 0
		Criteria execCriteria;
		int pageSize = search.getPageSize();
		int pageNumber = search.getPage();
		int firstResult = 0;
		if (pageSize > 0 && pageNumber > 0)
		{
			firstResult = pageNumber * pageSize;
		}

		if (search.getSortAssociation() != null
				&& search.getSortAssociation().equalsIgnoreCase("problemDescription"))
		{
			execCriteria = sortCriteria.getExecutableCriteria(getHibernateSession());
			List<RecordLevelError> sortResults = execCriteria.list();
			Collections.sort(sortResults, new ProblemDescriptionComparator());
			if (search.getSortDirection() == PaginatedSearch.SortDirection.DESC)
			{
				Collections.reverse(sortResults);
			}
			if (pageSize > 0)
			{
				search.setPageResults(sortResults.subList(firstResult, (firstResult + pageSize) - 1));
			}
			else
			{
				search.setPageResults(sortResults);
			}
		}
		else
		{
			execCriteria = resultCriteria.getExecutableCriteria(getHibernateSession());
			execCriteria.setFirstResult(firstResult);
			if (pageSize > 0)
			{
				execCriteria.setMaxResults(pageSize);
			}
			List<RecordLevelError> results = execCriteria.list();
			search.setPageResults(results);
		}

		return search;
	}

	@Override
	public ComplianceSearch searchTransmissionErrors(ComplianceSearch search)
	{
		// get the list of compliance categories for transmission-level errors
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getTransmissionErrorCategories");
		List<ComplianceCategory> categories = qry.list();

		List<NytdError> results = getErrorsForCategories(search.getTransmissionId(), categories);
		search.setPageResults(results);
		search.setPageSize(0);

		return search;
	}

	/**
	 * @param search
	 * @return
	 */
	public PenaltySearch searchAggregatePenalties(PenaltySearch search)
	{
		List<VwTransmissionStatus> transmissionList = null;

		// create criteria
		DetachedCriteria criteria = DetachedCriteria.forClass(VwTransmissionStatus.class);

		// add state name restriction to criteria
		String stateName = search.getStateName();
		if (stateName != null && !stateName.isEmpty())
		{
			String[] stateNames = stateName.split(";");
			if (stateNames.length > 0 && !(stateName.trim()).equalsIgnoreCase("All"))
			{
				criteria.add(Restrictions.in("state", stateNames));
			}
		}
                
                // generatePenaltiesLetters to get the results with selected states
                Collection<String> selectedStates = search.getSelectedStates().values();
                if (!selectedStates.isEmpty()) {
                    criteria.add(Restrictions.in("state", selectedStates));
                }

		// add compliance status to criteria
		String complianceStatus = search.getComplianceStatus();
		if (complianceStatus != null
				&& (!complianceStatus.isEmpty() && (!(complianceStatus.trim()).equalsIgnoreCase("All"))))
		{
			criteria.add(Restrictions.eq("complianceStatus", complianceStatus.trim()));
		}

		// restrict to specified reporting periods
		Collection<String> names = search.getSelectedReportingPeriods();
		if (names != null && names.size() > 0)
		{
			criteria.add(Restrictions.in("reportingPeriod", names));
		}

		// restrict results to submitted transmissions
		if (search.isViewSubmissionsOnly())
		{
			criteria.add(Restrictions.isNotNull("submittedDate"));
		}
		if (search.isViewActiveSubmissionsOnly())
		{
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("submissionStatus", "Active")).add(
					Restrictions.eq("submissionStatus", "active")));
		}
		String timelyData = search.getTimelyData();
		if (timelyData != null && !timelyData.isEmpty() && !timelyData.equalsIgnoreCase("All"))
		{
			criteria.add(Restrictions.eq("timelyErrCnt", new BigDecimal(timelyData)));
		}

		String correctFormatData = search.getCorrectFormatData();
		if (correctFormatData != null && !correctFormatData.isEmpty()
				&& !correctFormatData.equalsIgnoreCase("All"))
		{
			criteria.add(Restrictions.eq("formatErrCnt", new BigDecimal(correctFormatData)));
		}

		String errorFreeData = search.getErrorFreeData();
		if (errorFreeData != null && !errorFreeData.isEmpty() && !errorFreeData.equalsIgnoreCase("All"))
		{

			if (Integer.parseInt(errorFreeData) == 0)
			{
				criteria.add(Restrictions.eq("datValueCompliantCnt", new BigDecimal(0)));
			}
			else
			{
				criteria.add(Restrictions.gt("datValueCompliantCnt", new BigDecimal(0)));
			}
		}

		// execute count query to return row count
		criteria.setProjection(Projections.rowCount());
		Criteria countCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
		Long lRowCount = (Long)countCriteria.uniqueResult();
		//search.setRowCount(((Integer) countCriteria.uniqueResult()));
		search.setRowCount(lRowCount.intValue());
		// search.setRowCount((Integer) countCriteria.uniqueResult());
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// add sort
		if (search.getSortColumn() != null)
		{
			switch (search.getSortDirection())
			{
				case ASC:
					criteria.addOrder(Order.asc(search.getSortColumn()));
					break;
				case DESC:
					criteria.addOrder(Order.desc(search.getSortColumn()));
					break;
			}
		}
		else
		{
			// default sort order
			criteria.addOrder(Order.desc("reportingPeriod"));
			search.setSortColumn("reportingPeriod");
			search.setSortDirection(PaginatedSearch.SortDirection.DESC);

		}

		// execute result query. limit results if page size > 0
		Criteria resultsCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
        ExtendedDueDateDaoImpl.getPages(resultsCriteria, search.getPageSize(), search.getPage());

        transmissionList = resultsCriteria.list();
		search.setPageResults(transmissionList);
		return search;
	}

	/**
	 * @param search
	 * @return
	 */
	public PenaltySearch searchElementPenalties(PenaltySearch search)
	{
		List<VwElementPenalty> vwElementPenaltyList = null;
		// create criteria
		DetachedCriteria criteria = DetachedCriteria.forClass(VwElementPenalty.class);

		// add state name restriction to criteria
		String stateName = search.getStateName();
		if (stateName != null && !stateName.isEmpty())
		{
			String[] stateNames = stateName.split(";");
			if (stateNames.length > 0 && !(stateName.trim()).equalsIgnoreCase("All"))
			{
				criteria.add(Restrictions.in("id.state", stateNames));
			}
		}

		// add element number restriction
		// String elementNumber = search.getElementNumber();
		String elementNumber = "";
		if (elementNumber != null && !elementNumber.isEmpty() && !elementNumber.equals("All"))
		{
			criteria.add(Restrictions.eq("id.elementNumber", Long.valueOf(elementNumber)));
		}

		// restrict to specified reporting periods
		Collection<String> names = search.getSelectedReportingPeriods();
		if (names != null && names.size() > 0)
		{
			criteria.add(Restrictions.in("id.reportingPeriod", names));
		}

		// restrict to specified element numbers
		Collection<Long> elementNumbers = search.getSelectedElementNums();
		if (elementNumbers != null && elementNumbers.size() > 0)
		{
			criteria.add(Restrictions.in("id.elementId", elementNumbers));
		}

		// restrict results to submitted transmissions
		if (search.isViewSubmissionsOnly())
		{
			criteria.add(Restrictions.isNotNull("submittedDate"));
		}
		// restrict results to active submission only
		if (search.isViewActiveSubmissionsOnly())
		{
			criteria.add(Restrictions.disjunction().add(Restrictions.eq("submissionStatus", "Active")).add(
					Restrictions.eq("submissionStatus", "active")));
		}

		// execute count query to return row count
		criteria.setProjection(Projections.rowCount());
		Criteria countCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
		Long lRowCount = (Long)countCriteria.uniqueResult();
		search.setRowCount(lRowCount.intValue());
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);

		// add sort
		if (search.getSortColumn() != null && !search.getSortColumn().isEmpty())
		{
			switch (search.getSortDirection())
			{
				case ASC:
					criteria.addOrder(Order.asc(search.getSortColumn()));
					break;
				case DESC:
					criteria.addOrder(Order.desc(search.getSortColumn()));
					break;
			}
		}
		else
		{
			// default sort order
			criteria.addOrder(Order.asc("id.elementNumber"));
			search.setSortColumn("id.elementNumber");
			search.setSortDirection(PaginatedSearch.SortDirection.ASC);
		}

		// execute result query. limit results if page size > 0
		Criteria resultsCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
        ExtendedDueDateDaoImpl.getPages(resultsCriteria, search.getPageSize(), search.getPage());

        vwElementPenaltyList = resultsCriteria.list();
		search.setPageResults(vwElementPenaltyList);
		return search;
	}

	public String getElementNameFromNumberInApplication(String number, List<Element> list)
	{
		String name = "";
		for (Element element : list)
		{
			if (element.getName().equals(number))
			{
				name = element.getDescription();
				break;
			}
		}
		return name;
	}

	private class ProblemDescriptionComparator implements Comparator<RecordLevelError>
	{
		public int compare(RecordLevelError o1, RecordLevelError o2)
		{
			String o1ProblemDesc = o1.formatErrorMessage();
			String o2ProblemDesc = o2.formatErrorMessage();
			return o1ProblemDesc.compareTo(o2ProblemDesc);
		}
	}

	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	@Override
	public ComplianceSearch searchElementLevelAdvisories(ComplianceSearch search)
	{
		System.out.println("********************Inside complianceService.searchElementlevelAdvisories()");
		
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		// create a criteria object for ElementLevelAdvisory
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(ElementLevelAdvisory.class);

		/*
		 * map of entity association criterias (needed to support complex sorts on
		 * association properties)
		 */
		Map<String, Criteria> entityMap = new HashMap<String, Criteria>();
		entityMap.put("elementLevelDQAStandard", criteria.createCriteria("elementLevelDQAStandard"));
		entityMap.put("allowedValue", entityMap.get("elementLevelDQAStandard").createCriteria("allowedValue"));
		entityMap.put("element", entityMap.get("allowedValue").createCriteria("element"));

		// add restriction for transmission id
		criteria.add(Restrictions.eq("transmissionId", search.getTransmissionId()));

		// eagerly fetch associations
		criteria.setFetchMode("elementLevelDQAStandard", FetchMode.JOIN);
		entityMap.get("elementLevelDQAStandard").setFetchMode("allowedValue", FetchMode.JOIN);
		entityMap.get("allowedValue").setFetchMode("element", FetchMode.JOIN);

		// add sort
		if (search.getSortColumn() != null)
		{
			switch (search.getSortDirection())
			{
				case ASC:
					if (search.getSortAssociation() != null)
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.asc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.asc(search.getSortColumn()));
					}
					break;
				case DESC:
					if (search.getSortAssociation() != null)
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.desc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.desc(search.getSortColumn()));
					}
					break;
			}
		}
		else
		{
			// default sort is element id
			search.setSortAssociation("element");
			search.setSortColumn("id");
			search.setSortDirection(ComplianceSearch.SortDirection.ASC);
			entityMap.get("element").addOrder(Order.asc("id"));
		}

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<ElementLevelAdvisory> results = criteria.list();
		search.setPageResults(results);
		search.setPageSize(0);

		return search;

	}

	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	@Override
	public ComplianceSearch searchRecordLevelAdvisories(ComplianceSearch search)
	{

		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		// create a criteria object for ElementLevelAdvisory
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(RecordLevelAdvisory.class);

		/*
		 * map of entity association criterias (needed to support complex sorts on
		 * association properties)
		 */
		Map<String, Criteria> entityMap = new HashMap<String, Criteria>();
		entityMap.put("problemDescription", criteria.createCriteria("problemDescription"));
		entityMap.put("datum", criteria.createCriteria("datum"));
		entityMap.put("transmissionRecord", entityMap.get("datum").createCriteria("transmissionRecord"));
		entityMap.put("element", entityMap.get("datum").createCriteria("element"));

		// add restriction for transmission id
		criteria.add(Restrictions.eq("transmissionId", search.getTransmissionId()));
		System.out.println(search.getRecordNumbers());

		if (search.getRecordNumbers() != null && !search.getRecordNumbers().isEmpty()
				&& !search.getRecordNumbers().get(0).isEmpty())
		{
			// System.out.println("*****************************"+search.getRecordNumbers().get(0).trim());
			entityMap.get("transmissionRecord").add(
					Restrictions.like("recordNumber", search.getRecordNumbers().get(0).trim()));
		}

		if (search.getElementNumber() != null && !"all".equalsIgnoreCase(search.getElementNumber()))
		{
			entityMap.get("element").add(Restrictions.eq("name", search.getElementNumber()));
		}

		// execute count query to return row count
		criteria.setProjection(Projections.rowCount());
		Long lRowCount = (Long)criteria.uniqueResult();
		search.setRowCount(lRowCount.intValue());
		//search.setRowCount((Integer) criteria.uniqueResult());
		criteria.setProjection(null);

		// add sort
		if (search.getSortColumn() != null)
		{
			switch (search.getSortDirection())
			{
				case ASC:
					if (search.getSortAssociation() != null)
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.asc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.asc(search.getSortColumn()));
					}
					break;
				case DESC:
					if (search.getSortAssociation() != null)
					{
						entityMap.get(search.getSortAssociation()).addOrder(Order.desc(search.getSortColumn()));
					}
					else
					{
						criteria.addOrder(Order.desc(search.getSortColumn()));
					}
					break;
			}
		}
		else
		{
			// default sort is element id
			search.setSortAssociation("transmissionRecord");
			search.setSortColumn("recordNumber");
			search.setSortDirection(ComplianceSearch.SortDirection.ASC);
			entityMap.get("transmissionRecord").addOrder(Order.asc("recordNumber"));

		}

        ExtendedDueDateDaoImpl.getPages(criteria, search.getPageSize(), search.getPage());

        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<RecordLevelAdvisory> results = criteria.list();
		search.setPageResults(results);
		// search.setPageSize(0);

		return null;
	}

	@Override
	public List<ErrorTypeCount> getNonComplianceCountsReport(ErrorTypeSearch search)
	{

		// create a criteria object for ElementLevelAdvisory
		/*
		 * Criteria criteria =
		 * getSessionFactory().getCurrentSession().createCriteria(
		 * ErrorTypeCount.class);
		 */

		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery
				.append("select  distinct ")
				.append(
						"cd.transmissionid transmissionid,rp.name reportingperiod, st.statename,cd.elementid, ele.description ElementName, case when pd.compliancecategoryid is not null then cc.name ||': ' || pd.name else pd.name end PROBLEMDESCRIPTION,cc.name COMPCAT, cd.count ERRORCOUNT ")
				.append("from NonComplianceCountReport cd ")
				.append("left outer join state st on cd.stateid = st.stateid ")
				.append(
						"left outer join problemdescription pd on cd.problemdescriptionid = pd.problemdescriptionid ")
				.append(
						"left outer join compliancecategory cc on pd.compliancecategoryid = cc.compliancecategoryid ")
				.append("left outer join reportingperiod rp on cd.reportingperiodid = rp.reportingperiodid ")
				.append("left outer join element ele on cd.elementid = ele.elementid ").append(
						"where rp.name in (:rps) and st.abbreviation in (:abs) ").append(
						" and cd.errortype not in ('Error Free Information','Timely Data') ");
		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			sqlQuery.append(" and ele.name in (:elemnames)");
		}
		if (search.getSelectedErrorTypes() != null && !search.getSelectedErrorTypes().isEmpty())
		{
			sqlQuery.append(" and cd.errortype in (:errortypes)");
		}

		if (search.getSortColumn() != null && !search.getSortColumn().isEmpty())
		{
			sqlQuery.append(" order by ").append(search.getSortColumn()).append(" ").append(
					search.getSortDirection());
		} else
		{			
			sqlQuery.append(" order by elementid").append(" ").append(
					search.getSortDirection());
		}

		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sqlQuery.toString());
		query.setParameterList("rps", search.getSelectedReportingPeriods());
		query.setParameterList("abs", search.getSelectedStateName());
		if (search.getSelectedElemNums() != null && !search.getSelectedElemNums().isEmpty())
		{
			query.setParameterList("elemnames", search.getSelectedElemNums());
		}
		if (search.getSelectedErrorTypes() != null && !search.getSelectedErrorTypes().isEmpty())
		{
			query.setParameterList("errortypes", search.getSelectedErrorTypes());
		}
		query.setResultTransformer(Transformers.aliasToBean(ErrorTypeCount.class));
		if (search.getPageSize() > 0)
		{
			query.setFirstResult((search.getPage() * search.getPageSize()));
			query.setMaxResults(search.getPageSize());
			query.setFetchSize(search.getPageSize());
		}
		List<ErrorTypeCount> nonComplianceCountList = (List<ErrorTypeCount>) query.list();
		return nonComplianceCountList;
	}

	@Override
	public List<ErrorTypeCount> getDQACountsReport(ErrorTypeSearch search)
	{
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery
				.append("select distinct ")
				.append(
						"cd.transmissionid transmissionid,rp.name reportingperiod, st.statename,cd.elementid, ele.description ElementName, nvl(cd.problemdescriptionid,0) \"problemDescriptionId\", case when cd.problemdescriptionid is not null then pd.name else edqas.name || ': ' || edqas.dqatext end PROBLEMDESCRIPTION, cd.count ERRORCOUNT ")
				.append("from DQACountReport cd ")
				.append("left outer join state st on cd.stateid = st.stateid ")
				.append(
						"left outer join problemdescription pd on cd.problemdescriptionid = pd.problemdescriptionid ")
				// .append("left outer join compliancecategory cc on cd.compliancecategoryid = cc.compliancecategoryid ")
				.append("left outer join reportingperiod rp on cd.reportingperiodid = rp.reportingperiodid ")
				.append("left outer join element ele on cd.elementid = ele.elementid ")
				.append(
						"left outer join elementleveldqastandard edqas on cd.elementleveldqastdid = edqas.elementleveldqastdid ")
				.append("where rp.name in (:rps) and st.abbreviation in (:abs) ");
		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			sqlQuery.append(" and ele.name in (:elemnames)");
		}
		if (search.getSelectedDQATypes() != null && !search.getSelectedDQATypes().isEmpty())
		{
			sqlQuery.append(" and cd.dqaname in (:dqas)");
		}		
		if (search.getSortColumn() != null && !search.getSortColumn().isEmpty())
		{
			sqlQuery.append(" order by ").append(search.getSortColumn()).append(" ").append(
					search.getSortDirection());
		} else
		{			
			sqlQuery.append(" order by elementid").append(" ").append(
					search.getSortDirection());
		}
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sqlQuery.toString());
		query.setParameterList("rps", search.getSelectedReportingPeriods());
		query.setParameterList("abs", search.getSelectedStateName());
		if (search.getSelectedElemNums() != null && !search.getSelectedElemNums().isEmpty())
		{
			query.setParameterList("elemnames", search.getSelectedElemNums());
		}
		if (search.getSelectedDQATypes() != null && !search.getSelectedDQATypes().isEmpty())
		{
			query.setParameterList("dqas", search.getSelectedDQATypes());
		}

		query.setResultTransformer(Transformers.aliasToBean(ErrorTypeCount.class));
		if (search.getPageSize() > 0)
		{
			query.setFirstResult((search.getPage() * search.getPageSize()));
			query.setMaxResults(search.getPageSize());
			query.setFetchSize(search.getPageSize());
		}
		List<ErrorTypeCount> dqaCountList = (List<ErrorTypeCount>) query.list();
		return dqaCountList;
	}

	public List<String> getErrorTypes()
	{
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery = new StringBuffer().append("select distinct rulename from problemdescription ").append(
				"where rulename is not null and rulename not like \'%DQA#%\' ").append("order by rulename ");
		Query query = getSessionFactory().getCurrentSession().createSQLQuery(sqlQuery.toString());
		List<String> errorTypeList = query.list();

		return errorTypeList;
	}

	@Override
	public int getNonCompliancesCount(ErrorTypeSearch search)
	{
		StringBuffer sqlQuery = new StringBuffer();
		/*sqlQuery
				.append("select ")
				.append("count(*) ")
				.append("from NonComplianceCountReport cd ")
				.append("left outer join state st on cd.stateid = st.stateid ")
				.append(
						"left outer join problemdescription pd on cd.problemdescriptionid = pd.problemdescriptionid ")
				.append(
						"left outer join compliancecategory cc on cd.compliancecategoryid = cc.compliancecategoryid ")
				.append("left outer join reportingperiod rp on cd.reportingperiodid = rp.reportingperiodid ")
				.append("left outer join element ele on cd.elementid = ele.elementid ").append(
						"where rp.name in (:rps) and st.abbreviation in (:abs) ").append(
						" and cd.errortype not in ('Error Free Information','Timely Data') ");*/
		sqlQuery
		.append("select count(*)  from (select  distinct ")
		.append(
				"cd.transmissionid transmissionid,rp.name reportingperiod, st.statename,cd.elementid, ele.description ElementName, case when pd.compliancecategoryid is not null then cc.name ||': ' || pd.name else pd.name end PROBLEMDESCRIPTION,cc.name COMPCAT, cd.count ERRORCOUNT ")
		.append("from NonComplianceCountReport cd ")
		.append("left outer join state st on cd.stateid = st.stateid ")
		.append(
				"left outer join problemdescription pd on cd.problemdescriptionid = pd.problemdescriptionid ")
		.append(
				"left outer join compliancecategory cc on pd.compliancecategoryid = cc.compliancecategoryid ")
		.append("left outer join reportingperiod rp on cd.reportingperiodid = rp.reportingperiodid ")
		.append("left outer join element ele on cd.elementid = ele.elementid ").append(
				"where rp.name in (:rps) and st.abbreviation in (:abs) ").append(
				" and cd.errortype not in ('Error Free Information','Timely Data') ");
		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			sqlQuery.append(" and ele.name in (:elemnames)");
		}
		if (search.getSelectedErrorTypes() != null && !search.getSelectedErrorTypes().isEmpty())
		{
			sqlQuery.append(" and cd.errortype in (:errortypes)");
		}
		sqlQuery.append(" )");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sqlQuery.toString());
		query.setParameterList("rps", search.getSelectedReportingPeriods());
		query.setParameterList("abs", search.getSelectedStateName());
		if (search.getSelectedElemNums() != null && !search.getSelectedElemNums().isEmpty())
		{
			query.setParameterList("elemnames", search.getSelectedElemNums());
		}
		if (search.getSelectedErrorTypes() != null && !search.getSelectedErrorTypes().isEmpty())
		{
			query.setParameterList("errortypes", search.getSelectedErrorTypes());
		}
		BigDecimal rowCount = (BigDecimal) query.uniqueResult();
		return rowCount != null ? rowCount.intValue() : 0;
	}

	@Override
	public int getDQAsCount(ErrorTypeSearch search)
	{
		StringBuffer sqlQuery = new StringBuffer();
		/*sqlQuery.append("select ").append("count(*)  ").append("from DQACountReport cd ").append(
				"left outer join state st on cd.stateid = st.stateid ").append(
				"left outer join problemdescription pd on cd.problemdescriptionid = pd.problemdescriptionid ")
				// .append("left outer join compliancecategory cc on cd.compliancecategoryid = cc.compliancecategoryid ")
				.append("left outer join reportingperiod rp on cd.reportingperiodid = rp.reportingperiodid ")
				.append("left outer join element ele on cd.elementid = ele.elementid ").append(
						"where rp.name in (:rps) and st.abbreviation in (:abs) ");*/
		sqlQuery
		.append("select count(*) from (select distinct ")
		.append(
				"cd.transmissionid transmissionid,rp.name reportingperiod, st.statename,cd.elementid, ele.description ElementName, case when cd.problemdescriptionid is not null then pd.name else edqas.name || ': ' || edqas.dqatext end PROBLEMDESCRIPTION, cd.count ERRORCOUNT ")
		.append("from DQACountReport cd ")
		.append("left outer join state st on cd.stateid = st.stateid ")
		.append(
				"left outer join problemdescription pd on cd.problemdescriptionid = pd.problemdescriptionid ")
		// .append("left outer join compliancecategory cc on cd.compliancecategoryid = cc.compliancecategoryid ")
		.append("left outer join reportingperiod rp on cd.reportingperiodid = rp.reportingperiodid ")
		.append("left outer join element ele on cd.elementid = ele.elementid ")
		.append(
				"left outer join elementleveldqastandard edqas on cd.elementleveldqastdid = edqas.elementleveldqastdid ")
		.append("where rp.name in (:rps) and st.abbreviation in (:abs) ");
		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			sqlQuery.append(" and ele.name in (:elemnames)");
		}
		if (search.getSelectedDQATypes() != null && !search.getSelectedDQATypes().isEmpty())
		{
			sqlQuery.append(" and cd.dqaname in (:dqas)");
		}
		
		sqlQuery.append(" )");

		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sqlQuery.toString());
		query.setParameterList("rps", search.getSelectedReportingPeriods());
		query.setParameterList("abs", search.getSelectedStateName());
		if (search.getSelectedElemNums() != null && !search.getSelectedElemNums().isEmpty())
		{
			query.setParameterList("elemnames", search.getSelectedElemNums());
		}
		if (search.getSelectedDQATypes() != null && !search.getSelectedDQATypes().isEmpty())
		{
			query.setParameterList("dqas", search.getSelectedDQATypes());
		}
		BigDecimal rowCount = (BigDecimal) query.uniqueResult();
		return rowCount != null ? rowCount.intValue() : 0;
	}

	@Override
	public List<Cohort> getCohortList()
	{
		List<Cohort> cohorts= null;
		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Cohort> criteriaQuery = criteriaBuilder.createQuery(Cohort.class);
		Root<Cohort> root = criteriaQuery.from(Cohort.class);
		criteriaQuery.select(root);
		TypedQuery<Cohort> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		cohorts = q.getResultList();
		
		return cohorts;
	}

	@Override
	public Map getCohortStatus(CohortSearch search)
	{
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		CohortStatus cohortStatus = null;
		int cohortPopulationType = 0;
		Long sampled = 0L;
		Long pop21 = 0L;
		cohortPopulationType = search.getSelectedPopulationType();
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = null;
		Collection<CohortRecord> cohortRecords = null;

		if (search.getShowSample())
			sampled = 1L;
		if (cohortPopulationType == 21)
			pop21 = 1L;
		
			criteria = session.createCriteria(CohortStatus.class);
			criteria.add(Restrictions.eq("cohortId", new Integer(search.getSelectedCohorts()).longValue()))
					.add(Restrictions.eq("stateId", new Integer(search.getSelectedState()).longValue()))
					.createAlias("cohortRecords", "cohortRecords");
			if(cohortPopulationType == 21)
			{
				criteria.add(Restrictions.and(Restrictions.isNotNull("cohortRecords.population21"),Restrictions.eq("cohortRecords.population21", pop21)));
				if(search.getShowReportPeriod()!= null && search.getShowReportPeriod().size() == 1)
					criteria.add(Restrictions.ilike("cohortRecords.followupRPName", search.getQueryFollowup19RP()));
			}
			if(cohortPopulationType == 19)
			{
				if(search.getShowSample())
					criteria.add(Restrictions.and(Restrictions.isNotNull("cohortRecords.sampled"),Restrictions.eq("cohortRecords.sampled", sampled)));
				if(search.getShowReportPeriod()!= null && search.getShowReportPeriod().size() == 1)
				{
					criteria.add(
							Restrictions.or(
									Restrictions.and(Restrictions.ilike("cohortRecords.reportPeriodName", search.getQueryBaselineRP()), Restrictions.ilike("cohortRecords.outcomePopulation", "Baseline")), 
									Restrictions.and(Restrictions.ilike("cohortRecords.reportPeriodName", search.getQueryPostbufferRP()), Restrictions.ilike("cohortRecords.outcomePopulation", "Post-buffer"))
								)
							);
				}
			}
			
			List<Map> results =	criteria.setFetchMode("cohortRecords", FetchMode.JOIN)
					.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
					.list();
		if(results !=null && results.size() > 0)
		{
			
				cohortRecords = new ArrayList<CohortRecord>(0);
				for (Map map : results)
				{
					cohortStatus = (CohortStatus) map.get(Criteria.ROOT_ALIAS);
					cohortRecords.add((CohortRecord) map.get("cohortRecords"));
				}
		}
		Map resultsMap = new HashMap<String, Object>();
		resultsMap.put("cohortStatus", cohortStatus);
		resultsMap.put("cohortRecords", cohortRecords);
		return resultsMap;
	
}

	//TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
	//http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
	@SuppressWarnings("deprecation")
	public List getCohortBaseline(CohortSearch search)
	{
		log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
		
		List recordNumbers = null;

		Session session = getSessionFactory().getCurrentSession();
		List baselinePeriods = session.createSQLQuery(
				"select reportyear17||'A',reportyear17||'B',(reportyear17+1)||'A' from cohorts where cohortsid = "+ search.getSelectedCohorts()).list();
		Object[] obj = (Object[]) baselinePeriods.get(0);
		baselinePeriods.clear();
		baselinePeriods.add(obj[0]);
		baselinePeriods.add(obj[1]);
		baselinePeriods.add(obj[2]);
		Criteria criteria = session.createCriteria(RecordToExport.class);
		criteria.add(Restrictions.eq("stateId", new Integer(search.getSelectedState()).longValue()));
		criteria.add(Restrictions.in("outcomePopulation", Arrays.asList("Baseline", "Post-buffer")));
		criteria.add(Restrictions.eq("inCohort", new Integer(1).longValue()));
		criteria.add(Restrictions.in("this.reportingPeriod", baselinePeriods));
		criteria.addOrder(Order.asc("e3RecordNumber"));
	//	criteria.setProjection(Projections.property("e3RecordNumber"));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("id"),"recordId")
				.add(Projections.property("transId"),"transmissionId")
				.add(Projections.property("e3RecordNumber"),"e3RecordNumber")
				.add(Projections.property("reportingPeriod"),"reportingPeriod")
			);
		criteria.setResultTransformer(Transformers.aliasToBean(CohortResultDTO.class));
		recordNumbers = criteria.list(); 
		return recordNumbers;
	}

	public Long getCohortSampleSize(CohortSearch search)
	{
		Long cohortSize = 0L;
		Session session = getSessionFactory().getCurrentSession();		
    	CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<CohortStatus> criteriaQuery = criteriaBuilder.createQuery(CohortStatus.class);
		Root<CohortStatus> root = criteriaQuery.from(CohortStatus.class);
		criteriaQuery.select(root.get("sampleSize"));
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("cohortId"), new Integer(search.getSelectedCohorts()).longValue()),
				criteriaBuilder.equal(root.get("stateId"), new Integer(search.getSelectedState()).longValue())
		);
		List list = session.createQuery(criteriaQuery).getResultList();
		
		if (list != null && list.size() > 0)
		{
			cohortSize = new Long((Long) list.get(0));
		}
		return cohortSize;
	}
	
	/*
	 * (non-Javadoc)
	 * @see gov.hhs.acf.cb.nytd.service.ComplianceService#overWriteCohortSet(gov.hhs.acf.cb.nytd.actions.report.CohortSearch)
	 * @param cohortsearch
	 * Overwrite the cohort records and flags submitted records as in cohort and with cohortid
	 */
	public void overWriteCohortSet(CohortSearch search)
	{

		// Calling overWriteCohortSet must always precede overWriteCohortinRE.
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("overWriteCohortSet");
		int overWriteFlag = 0;
		qry.setParameter(1, search.getSelectedState());
		qry.setParameter(2, search.getSelectedCohorts());
		qry.setParameter(3, java.sql.Types.NULL);
		if (search.getSelectedPopulationType() == 19)
			overWriteFlag = 1;
		else if (search.getSelectedPopulationType() == 21)
			overWriteFlag = 2;
		qry.setParameter(4, overWriteFlag);
		qry.executeUpdate();
		
		// Call spOverwriteCohortInRE must be made only after calling spInsCohortRecords
		//else  cohort and cohortid will be marked incorrect in recordtoexport table.
		Query flagQry = getSessionFactory().getCurrentSession().getNamedQuery("overWriteCohortinRE");
		flagQry.setParameter(1, search.getSelectedState());
		flagQry.setParameter(2, search.getSelectedCohorts());
		flagQry.setParameter(3, overWriteFlag);
		flagQry.executeUpdate();

	}

	public Map<Long, String> getComplianceStandardPenaltyLetterDesc()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getComplianceStandards");
		Map<Long, String> complianceStdPenaltyDesc = new HashMap<Long, String>();
		List<ComplianceStandard> result = qry.list();
		for (ComplianceStandard cs : result)
		{
			complianceStdPenaltyDesc.put(cs.getElement().getId(), cs.getPenaltyLetterDesc());
		}
		return complianceStdPenaltyDesc;
	}

	public int showUpdateCohortSetButton(int cohortsId)
	{
		int showButton = 0;
		StringBuffer queryStr = new StringBuffer();
		
		//TODO: move query to DAO
		queryStr.append("select  case when  (to_date('01-Oct-'||REPORTYEAR19) < sysdate) then 2 ").append(
				" when ( to_date('01-Oct-'||REPORTYEAR17) < sysdate) then 1 else 0 end \"showbutton\" ").append(
				" from cohorts where cohortsid = :cohortsid");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(queryStr.toString())
			.setParameter("cohortsid", cohortsId);
		Object result = query.uniqueResult();
		BigDecimal resultInt = (BigDecimal) result;
		showButton = resultInt.intValue();
				
		return showButton;
	}

	public List getOutcomesReportWithTransId(OutcomesReportSearch search)
	{
		StringBuffer queryStr = new StringBuffer();
		List<Integer> outcomeAges = null;
		List<OutcomesReport> results =  null;
		queryStr.append(" select st.statename as \"stateName\", st.stateId as \"stateId\",rp.name as \"rpName\",rp.outcomeage as \"outcomeAge\", ")
		.append(" ore.outcomereportid as \"id\", ore.reportingperiodid as \"reportingperiodId\", ")
		.append(" ore.cohortSize as \"cohortSize\", ore.sampleSize as \"sampleSize\",  ")
		.append(" ore.cohortstatusid as \"cohortStatusId\",ore.outcomeuniversecount as \"outcomeUniverseCount\",ore.notreportedyouthcount as \"notReportedYouthCount\", ")
		.append(" ore.invalide34youthcount as \"invalidE34YouthCount\",ore.outcomepercentage as \"outcomePercentage\",ore.fcpartcount as \"fcPartCount\",ore.fcexcludedcount as \"fcExcludedCount\",ore.fccount as \"fcCount\",ore.fcnotpartcount as \"fcNotPartCount\", ")
		.append(" ore.fcpartpercentage as \"fcPartPercentage\",ore.dcpartcount as \"dcPartCount\",ore.dcexcludedcount as \"dcExcludedCount\",ore.dccount as \"dcCount\",ore.dcnotpartcount as \"dcNotPartCount\", ")
		.append(" ore.dcpartpercentage as \"dcPartPercentage\",ore.baselinerpname as \"baselineRPName\",ore.postbufferrpname as \"postBufferRPName\",ore.followup19rpname as \"followup19RPName\", ")
		.append(" ore.transmissionid as \"transmissionId\" ")
		.append(" from outcomereport ore ")
		.append(" left outer join cohortstatus cs on ore.cohortstatusid = cs.cohortstatusid ")
		.append(" inner join state st on  cs.stateid = st.stateid ")
		.append(" inner join reportingperiod rp on ore.reportingperiodid = rp.reportingperiodid ")
		.append(" where ore.submissionstatus = 'Active' and ore.submissionstatus is not null and ")
	    .append(" st.abbreviation in (select abbreviation from state where stateid in( select stateid from transmission where transmissionid = :transmissionId) ) and ")
	    .append(" cs.cohortsid = (select cohortsid from cohorts ")
	    .append(" where reportyear19 like (select  substr(name,1,4) from reportingperiod where reportingperiodid = (select reportingperiodid from transmission where transmissionid = :transmissionId) ) or ")
	    .append(" reportyear21 like (select  substr(name,1,4) from reportingperiod where reportingperiodid = (select reportingperiodid from transmission where transmissionid = :transmissionId) ))  and ")
	    .append(" rp.outcomeage in (select outcomeage from reportingperiod where reportingperiodid = (select reportingperiodid from transmission where transmissionid = :transmissionId)) ")
	    .append("order by ore.reportingperiodid asc");
		
			
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(queryStr.toString())
			.addScalar("stateName", StringType.INSTANCE)
			.addScalar("stateId", LongType.INSTANCE)
			.addScalar("rpName", StringType.INSTANCE)
			.addScalar("outcomeAge", LongType.INSTANCE)
			.addScalar("cohortSize", LongType.INSTANCE)
			.addScalar("sampleSize", LongType.INSTANCE)
			.addScalar("id", LongType.INSTANCE)
			.addScalar("reportingperiodId", LongType.INSTANCE)
			.addScalar("cohortStatusId", LongType.INSTANCE)
			.addScalar("outcomeUniverseCount", LongType.INSTANCE)
			.addScalar("notReportedYouthCount", LongType.INSTANCE)
			.addScalar("invalidE34YouthCount", LongType.INSTANCE)
			.addScalar("outcomePercentage", FloatType.INSTANCE)
			.addScalar("fcPartCount", LongType.INSTANCE)
			.addScalar("fcExcludedCount", LongType.INSTANCE)
			.addScalar("fcCount", LongType.INSTANCE)
			.addScalar("fcNotPartCount", LongType.INSTANCE)
			.addScalar("fcPartPercentage", FloatType.INSTANCE)
			.addScalar("dcPartCount", LongType.INSTANCE)
			.addScalar("dcExcludedCount", LongType.INSTANCE)
			.addScalar("dcCount",LongType.INSTANCE)
			.addScalar("dcNotPartCount", LongType.INSTANCE)
			.addScalar("dcPartPercentage", FloatType.INSTANCE)
			.addScalar("baselineRPName", StringType.INSTANCE)
			.addScalar("postBufferRPName", StringType.INSTANCE)
			.addScalar("followup19RPName", StringType.INSTANCE)
			.addScalar("transmissionId", LongType.INSTANCE);
		query.setLong("transmissionId", search.getSelectedTransmissionId());
		query.setResultTransformer(Transformers.aliasToBean(OutcomesReport.class));
		
		results = (List<OutcomesReport>)query.list();
		
		return results;
	
	}
	
	@Override
	public List getOutcomesReport(OutcomesReportSearch search) {
		
		
		StringBuffer queryStr = new StringBuffer();
		List<Integer> outcomeAges = null;
		List<OutcomesReport> results =  null;
		SiteUser siteUser = search.getSiteUser();
		queryStr.append(" select st.statename as \"stateName\", st.stateId as \"stateId\",rp.name as \"rpName\",rp.outcomeage as \"outcomeAge\", ")
		.append(" ore.outcomereportid as \"id\", ore.reportingperiodid as \"reportingperiodId\", ")
		.append(" ore.cohortSize as \"cohortSize\", ore.sampleSize as \"sampleSize\",  ")
		.append(" ore.cohortstatusid as \"cohortStatusId\",ore.outcomeuniversecount as \"outcomeUniverseCount\",ore.notreportedyouthcount as \"notReportedYouthCount\", ")
		.append(" ore.invalide34youthcount as \"invalidE34YouthCount\",ore.outcomepercentage as \"outcomePercentage\",ore.fcpartcount as \"fcPartCount\",ore.fcexcludedcount as \"fcExcludedCount\",ore.fccount as \"fcCount\",ore.fcnotpartcount as \"fcNotPartCount\", ")
		.append(" ore.fcpartpercentage as \"fcPartPercentage\",ore.dcpartcount as \"dcPartCount\",ore.dcexcludedcount as \"dcExcludedCount\",ore.dccount as \"dcCount\",ore.dcnotpartcount as \"dcNotPartCount\", ")
		.append(" ore.dcpartpercentage as \"dcPartPercentage\",ore.baselinerpname as \"baselineRPName\",ore.postbufferrpname as \"postBufferRPName\",ore.followup19rpname as \"followup19RPName\", ")
		.append(" ore.transmissionid as \"transmissionId\", ore.submissionstatus as \"submissionStatus\" ")
		.append(" from outcomereport ore ")
		.append(" left outer join cohortstatus cs on ore.cohortstatusid = cs.cohortstatusid ")
		.append(" inner join state st on  cs.stateid = st.stateid ")
		.append(" inner join reportingperiod rp on ore.reportingperiodid = rp.reportingperiodid where ");
		if(siteUser.getPrimaryUserRole()!=null && (siteUser.getPrimaryUserRole().getId() == 1 || siteUser.getPrimaryUserRole().getId() == 3 ))
			queryStr.append(" ore.submissionstatus = 'Active' and ore.submissionstatus is not null and ");
		queryStr.append(" st.abbreviation in (:states) and ")
	    .append(" cs.cohortsid = :cohorts  and ")
	    .append(" rp.outcomeage in (:outcomeage) ")
	    .append("order by ore.transmissionid desc, ore.submissionstatus asc ");
		
		outcomeAges = new ArrayList<Integer>();
		if(search.getSelectedFollowup19())
			outcomeAges.add(19);
		if(search.getSelectedFollowup21())
			outcomeAges.add(21);	
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(queryStr.toString())
			.addScalar("stateName", StringType.INSTANCE)
			.addScalar("stateId", LongType.INSTANCE)
			.addScalar("rpName", StringType.INSTANCE)
			.addScalar("outcomeAge", LongType.INSTANCE)
			.addScalar("cohortSize", LongType.INSTANCE)
			.addScalar("sampleSize", LongType.INSTANCE)
			.addScalar("id", LongType.INSTANCE)
			.addScalar("reportingperiodId", LongType.INSTANCE)
			.addScalar("cohortStatusId", LongType.INSTANCE)
			.addScalar("outcomeUniverseCount", LongType.INSTANCE)
			.addScalar("notReportedYouthCount", LongType.INSTANCE)
			.addScalar("invalidE34YouthCount", LongType.INSTANCE)
			.addScalar("outcomePercentage", FloatType.INSTANCE)
			.addScalar("fcPartCount", LongType.INSTANCE)
			.addScalar("fcExcludedCount", LongType.INSTANCE)
			.addScalar("fcCount", LongType.INSTANCE)
			.addScalar("fcNotPartCount", LongType.INSTANCE)
			.addScalar("fcPartPercentage", FloatType.INSTANCE)
			.addScalar("dcPartCount", LongType.INSTANCE)
			.addScalar("dcExcludedCount", LongType.INSTANCE)
			.addScalar("dcCount", LongType.INSTANCE)
			.addScalar("dcNotPartCount", LongType.INSTANCE)
			.addScalar("dcPartPercentage", FloatType.INSTANCE)
			.addScalar("baselineRPName", StringType.INSTANCE)
			.addScalar("postBufferRPName", StringType.INSTANCE)
			.addScalar("followup19RPName", StringType.INSTANCE)
			.addScalar("transmissionId", LongType.INSTANCE)
			.addScalar("submissionStatus",StringType.INSTANCE);
		query.setParameterList("states", search.getSelectedStateName());
		query.setInteger("cohorts",search.getSelectedCohorts());
		query.setParameterList("outcomeage", outcomeAges);
		query.setResultTransformer(Transformers.aliasToBean(OutcomesReport.class));
		
		results = (List<OutcomesReport>)query.list();
		
		return results;
	}

	/* (non-Javadoc)
	 * @see gov.hhs.acf.cb.nytd.service.ComplianceService#getOutcomesUniverseRecords(gov.hhs.acf.cb.nytd.actions.report.OutcomesReportSearch)
	 */
	@Override
	public List getOutcomesUniverseRecords(OutcomesReportSearch search) {
		
		StringBuffer queryStr = new StringBuffer();
		List<String> results = null;
		queryStr.append("select a.recordnumber from cohortrecords a ") 
				.append(" left outer join outcomesreportdata b on (a.cohortstatusid = b.cohortstatusid and a.recordnumber = b.recordnumber ) where ")
				.append("b.cohortstatusid = :cohortstatusid  and b.transmissionid = :transId ");
		
		
		if(search.getOutcomeAge() == 19)
		{
			queryStr.append(" and a.reportingperiodname in (:baselineRPName,:postBufferRPName) ");
			if(search.isShowYouthNotReported())
			{
				queryStr.append(" and b.ou19notreported = 1 ");
				
			}
			else if(search.isShowInvalidE34Youth())
			{
				queryStr.append(" and b.ou19invalide34 = 1 ");
			}
		}
		else if(search.getOutcomeAge() == 21)
		{
			
			if(search.isShowYouthNotReported())
			{
				queryStr.append(" and b.ou21notreported = 1 ");
				queryStr.append(" and ( a.followupRPName = :followupRPName or a.followupRPName is null )");
				
			}
			else if(search.isShowInvalidE34Youth())
				
			{
	//			queryStr.append(" and a.followupRPName = :followupRPName ");
				queryStr.append(" and b.ou21invalide34 = 1 ");
			}
		}
		
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(queryStr.toString());
		query.setLong("cohortstatusid", search.getCohortStatusId());
		query.setLong("transId", search.getTransmissionId());
		if(search.getOutcomeAge() == 19)
		{
			query.setString("baselineRPName", search.getBaselineRPName());
			query.setString("postBufferRPName", search.getPostBufferRPName());
		}
		if(search.getOutcomeAge() == 21 && search.isShowYouthNotReported())
		{
			query.setString("followupRPName", search.getFollowup19RPName());
			
		}
		results = (List<String>) query.list();
		
		return results;
	}

	/* (non-Javadoc)
	 * @see gov.hhs.acf.cb.nytd.service.ComplianceService#getOutcomesParticipationRecords(gov.hhs.acf.cb.nytd.actions.report.OutcomesReportSearch)
	 */
	@Override
	public List getOutcomesParticipationRecords(OutcomesReportSearch search) {
		
		StringBuffer queryStr = new StringBuffer();
		List<String> results = null;
		queryStr.append("select a.recordnumber from cohortrecords a ") 
				.append(" left outer join outcomesreportdata b on (a.cohortstatusid = b.cohortstatusid and a.recordnumber = b.recordnumber ) where ")
				.append("b.cohortstatusid = :cohortstatusid  and b.transmissionid = :transId ");
		
		
		if(search.getOutcomeAge() == 19)
		{
			queryStr.append(" and a.reportingperiodname in (:baselineRPName,:postBufferRPName) ");
			if(search.isShowFCNotParticipated())
			{
				queryStr.append(" and b.fc19notParticipated = 1 ");
				
			}
			else if(search.isShowDCNotParticipated())
			{
				queryStr.append(" and b.dc19notParticipated = 1 ");
			}
		}
		else if(search.getOutcomeAge() == 21)
		{
			queryStr.append(" and a.followupRPName = :followupRPName ");
			if(search.isShowFCNotParticipated())
			{
				queryStr.append(" and b.fc21notParticipated = 1 ");
				
			}
			else if(search.isShowDCNotParticipated())
			{
				queryStr.append(" and b.dc21notParticipated = 1 ");
			}
		}
		
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(queryStr.toString());
		query.setLong("cohortstatusid", search.getCohortStatusId());
		query.setLong("transId", search.getTransmissionId());
		if(search.getOutcomeAge() == 19)
		{
			query.setString("baselineRPName", search.getBaselineRPName());
			query.setString("postBufferRPName", search.getPostBufferRPName());
		}
		if(search.getOutcomeAge() == 21)
		{
			query.setString("followupRPName", search.getFollowup19RPName());
			
		}
		results = (List<String>) query.list();
		
		return results;
	}

	//TODO: there is no replacement for sqlRestriction(), rewrite with custom or/and use Named Native Query
	//https://discourse.hibernate.org/t/deprecation-of-hibernate-criteria-and-how-we-can-still-prevent-it/788/18
	//https://stackoverflow.com/questions/5182701/equivalent-of-hibernates-restrictions-sqlrestriction-in-jpa2-criteria-api
	@Override
	public List<Cohort> getSamplingCohortList() {
		
		log.info("TODO: No replacement of sqlRestriction() available, rewrite with custom or/and use Named Native Query");
		
		List<Cohort> cohorts = null;
		Session session = getSessionFactory().getCurrentSession();
		cohorts = (List<Cohort>) session.createCriteria(Cohort.class)
					.add(Restrictions.sqlRestriction("sysdate > to_date('01-Apr-'||(reportYear17 +1),'DD-Mon-YYYY')"))
				.list();
		return cohorts;
	}

	
}
