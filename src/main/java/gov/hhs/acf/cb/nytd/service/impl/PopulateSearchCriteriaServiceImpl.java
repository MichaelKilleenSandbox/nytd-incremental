/**
 * Filename: PopulateSearchCriteriaServiceImpl.java
 * 
 * Copyright 2009, ICF International
 * Created: Jun 18, 2009
 * Author: 15178
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

import gov.hhs.acf.cb.nytd.dao.*;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @author 15178
 * 
 */
@Transactional
public class PopulateSearchCriteriaServiceImpl extends BaseServiceImpl implements
		PopulateSearchCriteriaService
{
	@Getter @Setter private StateDAO stateDAO;
	
	@Getter @Setter private TransmissionDAO transmissionDAO;
	
	@Getter @Setter private SubmissionDAO submissionDAO;
	
	@Getter @Setter private ReportingPeriodDAO reportingPeriodDAO;
	
	@Getter @Setter private StateReportDAO stateReportDAO;
	
	@Getter @Setter private ElementDAO elementDAO;

	/*
	 * (non-Javadoc) This method returns a list of compliance status to populate
	 * drop down box from lookup table where category = "ComplianceStatus"
	 * 
	 * @author 15178
	 * 
	 * @param None
	 * 
	 * @return List<Lookup>
	 */
	@Override
	public List<Lookup> getComplianceStatus()
	{
		// TODO Auto-generated method stub
		List<Lookup> complianceStatus = findLookupByType(Constants.COMPLIANCESTATUS);
		log.debug("in populateSearchcriteria size of complianceStatusList:" + complianceStatus.size());
		return complianceStatus;
	}
	
	public List<Lookup> getSamplingRequestStatus()
	{
		List<Lookup> samplingRequestStatus = findLookupByType(Constants.SAMPLINGREQUESTSTATUS);
		log.debug("in populateSearchcriteria size of samplingRequestStatusList:" + samplingRequestStatus.size());
		return samplingRequestStatus;
	}

	/*
	 * (non-Javadoc) This method return list of reporting period to be used in a
	 * drop down box
	 * 
	 * @author 15178
	 * 
	 * @param None
	 * 
	 * @return List<ReportingPeriod> List of reporting period
	 */
	@Override
	public List<ReportingPeriod> getReportingPeriodList()
	{
		// TODO Remove this method if possible.
		Session session = getSessionFactory().getCurrentSession();
		List<ReportingPeriod> reportingPeriodList;
		String query;
		query = "select reportingPeriod " + "from ReportingPeriod as reportingPeriod "
				+ "order by reportingPeriod.endReportingDate DESC";
		reportingPeriodList = session.createQuery(query).list();
		return reportingPeriodList;
	}

	/*
	 * (non-Javadoc) This method list of possible transmission/Submission/File
	 * Type from TransmissionType table
	 * 
	 * @author 15178
	 * 
	 * @param None
	 * 
	 * @return List<TransmissionType>: List of transmission type object
	 */
	@Override
	public List<TransmissionType> getTransmissionTypeList()
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<TransmissionType> criteriaQuery = criteriaBuilder.createQuery(TransmissionType.class);
		Root<TransmissionType> root = criteriaQuery.from(TransmissionType.class);
		criteriaQuery.select(root);
		TypedQuery<TransmissionType> q = session.createQuery(criteriaQuery);
		List<TransmissionType> transmissionTypeList = q.getResultList();
		
		return transmissionTypeList;
	}

	/**
	 * This function populates ComplianceCategory drop down
	 * 
	 * @author 16939
	 * @return list
	 * @param none
	 */
	/*
	public List<ComplianceCategory> getComplianceAndQuality()
	{
		Session session = getSessionFactory().getCurrentSession();
		List<ComplianceCategory> complianceCategorylist = session.createCriteria(ComplianceCategory.class)
				.list();
		return complianceCategorylist;
	}
	*/

	/**
	 * This method populates Element Number drop down
	 * 
	 * @author 16939
	 * @return list
	 * @param none
	 */
	public List<Element> getElementNumbers()
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Element> criteriaQuery = criteriaBuilder.createQuery(Element.class);
		Root<Element> root = criteriaQuery.from(Element.class);
		criteriaQuery.select(root);
		TypedQuery<Element> q = session.createQuery(criteriaQuery);
		List<Element> elementList = q.getResultList();
		
		return elementList;
	}

	/**
	 * This method populates the static viewResults radio button choices
	 * 
	 * @author 16939
	 * @param none
	 * @return list
	 */
	public LinkedHashMap<String, String> getViewResultsList()
	{
		LinkedHashMap<String, String> viewResultsMap = new LinkedHashMap<String, String>();
		viewResultsMap.put("25", "25");
		viewResultsMap.put("50", "50");
		viewResultsMap.put("100", "100");
		viewResultsMap.put("0", "All");

		return viewResultsMap;

	}

	/**
	 * To retrieve list of state names
	 * 
	 * @return list
	 */
	public List<State> getStatesList()
	{
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<State> criteriaQuery = criteriaBuilder.createQuery(State.class);
		Root<State> root = criteriaQuery.from(State.class);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("stateName")));
		criteriaQuery.select(root);
		TypedQuery<State> q = session.createQuery(criteriaQuery);
		List<State> stateList = q.getResultList();
		
		return stateList;
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getStatesForUser(SiteUser)
	 */
	public List<State> getStatesForUser(SiteUser siteUser)
	{
		Session session = getSessionFactory().getCurrentSession();
		List<State> states;
		UserRoleEnum role = UserRoleEnum.getRole(siteUser.getPrimaryUserRole().getName());
		switch (role)
		{
			case STATE:
				states = new LinkedList<State>();
				states.add(siteUser.getState());
				break;
			case REGIONAL:
				states = new LinkedList<State>();
				states.addAll(getStateDAO().getStatesInRegion(siteUser.getRegion().getId()));
				break;
			default:
				CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
				CriteriaQuery<State> criteriaQuery = criteriaBuilder.createQuery(State.class);
				Root<State> root = criteriaQuery.from(State.class);
				criteriaQuery.select(root);
				TypedQuery<State> q = session.createQuery(criteriaQuery);	
				states = q.getResultList();
				
				if (states == null)
				{
					states = new LinkedList<State>();
				}
		}

		return states;
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getStateSelectMapForUser(SiteUser)
	 */
	public Map<String, String> getStateSelectMapForUser(SiteUser siteUser)
	{
		Map<String, String> states;
		if (siteUser.getPrimaryUserRole().getName().equalsIgnoreCase(Constants.STATEUSER))
		{
			states = new TreeMap<String, String>();
			states.put(String.valueOf(siteUser.getState().getId()), siteUser.getState().getStateName());
		}
		else if (siteUser.getPrimaryUserRole().getName().equalsIgnoreCase(Constants.REGIONALUSER))
		{
			states = new TreeMap<String, String>();
			for (State state : getStateDAO().getStatesInRegion(siteUser.getRegion().getId()))
			{
				states.put(String.valueOf(state.getId()), state.getStateName());
			}
		}
		else
		{
			states = getStateDAO().getStateSelectMap();
			if (states == null)
			{
				states = new TreeMap<String, String>();
			}
		}
		return states;
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getTransmissionSelectMap(SiteUser)
	 */
	@Override
	public Map<String, String> getTransmissionSelectMap(SiteUser siteUser)
	{
		return getTransmissionDAO().getTransmissionSelectMap(siteUser);
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getTransmissionCount(SiteUser, Long,
	 *      Long)
	 */
	@Override
	public Long getTransmissionCount(SiteUser siteUser, Long stateId, Long reportPeriodId)
	{
		if (!siteUser.getPrivileges().contains("canViewTransmissions"))
		{
			return getTransmissionDAO().getTransmissionCount(stateId, reportPeriodId, true);
		}
		return getTransmissionDAO().getTransmissionCount(stateId, reportPeriodId, false);
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getTransmissionSelectMap(SiteUser,
	 *      Long, Long)
	 */
	@Override
	public Map<String, String> getTransmissionSelectMap(SiteUser siteUser, Long stateId, Long reportPeriodId)
	{
		if (!siteUser.getPrivileges().contains("canViewTransmissions"))
		{
			return getSubmissionDAO().getSubmissionSelectMap(stateId, reportPeriodId);
		}
		return getTransmissionDAO().getTransmissionSelectMap(stateId, reportPeriodId);
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getReportPeriodSelectMap()
	 */
	@Override
	public Map<String, String> getReportPeriodSelectMap()
	{
		return getReportingPeriodDAO().getReportingPeriodSelectMap();
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getElementSelectMap()
	 */
	@Override
	public Map<String, String> getElementSelectMap()
	{
		return getElementDAO().getElementSelectMap();
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getReportPeriodSelectMapForUser(SiteUser,
	 *      Integer)
	 */
	@Override
	public Map<String, String> getReportPeriodSelectMapForUser(SiteUser siteUser, Integer numFiles)
	{
		return getReportingPeriodDAO().getReportPeriodSelectMapForUser(siteUser, numFiles);
	}

	/**
	 * @author Adam Russell (18816)
	 * @see PopulateSearchCriteriaService#getReportPeriodSelectMapForUser(SiteUser)
	 */
	@Override
	public Map<String, String> getReportPeriodSelectMapForUser(SiteUser siteUser)
	{
		return getReportPeriodSelectMapForUser(siteUser, 1);
	}
	
	/**
	 * @see PopulateSearchCriteriaService#getFiscalYearSelectMapForUser()
	 */
	@Override
	public Map<String, String> getFiscalYearSelectMapForUser(SiteUser siteUser)
	{
		Map<String, String> reportingPeriods = getReportPeriodSelectMapForUser(siteUser);
		Map<String, String> fiscalYears = new LinkedHashMap<String, String>();
		// the loop that strip off last character, adding unique FY value
		for (Map.Entry<String,String> entry:reportingPeriods.entrySet()){
			String fyValue = entry.getValue().substring(0, 4);
			if (!fiscalYears.containsValue(fyValue)){
				fiscalYears.put(entry.getKey(), fyValue);
			}
		}
		return fiscalYears;
	}
	
	/**
	 * @see PopulateSearchCriteriaService#getReportPeriodDescSelectMap()
	 */
	@Override
	public Map<String, String> getReportPeriodDescSelectMap()
	{
		Map<String, String> reportPeriodDescriptions = new LinkedHashMap<String, String>();
		reportPeriodDescriptions.put("1", "A period");
		reportPeriodDescriptions.put("2", "B period");
		reportPeriodDescriptions.put("3", "Full Year");
		reportPeriodDescriptions =  Collections.unmodifiableMap(reportPeriodDescriptions);
		
		return reportPeriodDescriptions;
	}
	
	/**
	 * @see PopulateSearchCriteriaService#getPopulationTypeSelectMap()
	 */
	@Override
	public Map<String, String> getPopulationTypeSelectMap()
	{
		Map<String, String> populationType = new LinkedHashMap<String, String>();
		populationType.put("1", "Served");
		populationType.put("2", "Outcomes");
		populationType =  Collections.unmodifiableMap(populationType);
		
		return populationType;
	}

}
