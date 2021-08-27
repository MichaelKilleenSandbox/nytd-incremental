/**
 *  Copyright 2010, ICF International
 *  Created: Feb 22, 2010
 *  Author: 23839
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.RecordLevelErrorDAO;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * @author 23839
 *
 */
public class RecordLevelErrorDAOImpl extends HibernateDaoSupport implements RecordLevelErrorDAO
{
	protected final Logger log = Logger.getLogger(getClass());
		
	/* (non-Javadoc)
	 * @see gov.hhs.acf.cb.nytd.dao.RecordLevelErrorDAO#searchRecordLevelErrors(java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, java.util.Map)
	 */
	/*
	@Override
	public List<ComplianceErrors> searchRecordLevelErrors(Long transmissionId, String recordNumber,
			String errorDescription, String complianceQualityTypeSelected, String elementNumberSelected,
			String maxResults, int firstResult, Map<String, String> selectedColumnMap)
	{
		Session session = getSessionFactory().getCurrentSession();
		List<ComplianceErrors> list = new LinkedList<ComplianceErrors>();
				
		//Defining the detached criteria to retrieve Record Level Error records.
		//------------------Start of Detached Criteria ---------------------
		
		Criteria recordLevelErrorCrit = session.createCriteria(RecordLevelError.class);
		// DetachedCriteria transColCrit = null;
		Criteria complianceCrit = recordLevelErrorCrit.createCriteria("nonCompliance");
		Criteria datCrit = complianceCrit.createCriteria("datum","dat");
		complianceCrit.createAlias("transmission", "trms");
		complianceCrit.createAlias("complianceType", "comptype");
		complianceCrit.add(Restrictions.and(Restrictions.eq("trms.id", transmissionId), Restrictions.eq(
				"comptype.codeDescription", Constants.RECORDLEVEL)));
		
		// setting the Fetch Mode to Eager(JOIN). This helps to retrieve all the data in a single DB hit.
		complianceCrit.setFetchMode("datum.transmissionRecord", FetchMode.JOIN);
		complianceCrit.setFetchMode("datum.element", FetchMode.JOIN);
		recordLevelErrorCrit.setFetchMode("nonCompliance.datum", FetchMode.JOIN);
		recordLevelErrorCrit.setFetchMode("complianceCategory", FetchMode.JOIN);
		recordLevelErrorCrit.setFetchMode("problemDescription", FetchMode.JOIN);
		
		//Creating Alias names to facilitate search and sort
		datCrit.createAlias("transmissionRecord", "transRec");
		datCrit.createAlias("element", "elem");
		recordLevelErrorCrit.createAlias("problemDescription","probDesc");
		recordLevelErrorCrit.createAlias("complianceCategory", "compCat");
		// transColCrit = datCrit.createCriteria("transmissionColumn", "transCol");
		// transColCrit.createAlias("element","elem");
		//setting search conditions per user inputs
		if(recordNumber!=null && !recordNumber.isEmpty())
		{
			//datCrit.createAlias("transmissionRecord", "transRec");
			StringBuffer strRecordNumber = new StringBuffer("%").append(recordNumber.trim()).append("%");
			//recordLevelErrorCrit.add(Restrictions.ilike("transRec.recordNumber", recordNumber.trim()));
			recordLevelErrorCrit.add(Restrictions.ilike("transRec.recordNumber", strRecordNumber.toString()));
		}
	
		if(errorDescription != null && !errorDescription.isEmpty())
		{
			//recordLevelErrorCrit.createAlias("problemDescription","probDesc");
			recordLevelErrorCrit.add(Restrictions.ilike("probDesc.description", errorDescription.trim()));
		}
		if(complianceQualityTypeSelected != null && !complianceQualityTypeSelected.isEmpty() && !complianceQualityTypeSelected.equalsIgnoreCase("All"))
		{	
			
			//recordLevelErrorCrit.createAlias("complianceCategory", "compCat");
			recordLevelErrorCrit.add(Restrictions.eq("compCat.name", complianceQualityTypeSelected.trim()));
		}
		if(elementNumberSelected != null && !elementNumberSelected.isEmpty() && !elementNumberSelected.equalsIgnoreCase("0"))
		{
			
			//transColCrit = datCrit.createCriteria("transmissionColumn", "transCol");
			//datCrit.createAlias("element","elem");
			recordLevelErrorCrit.add(Restrictions.eq("elem.name", elementNumberSelected.trim()));	
		}
		
		
		
		//setting the sort order
		String sortOrder = selectedColumnMap.get("sortOrder");
				
		if(selectedColumnMap != null && selectedColumnMap.get("selectcolumn")!= null && selectedColumnMap.get("selectcolumn").equalsIgnoreCase(Constants.RECORD_NUMBER))
		{
			
			if(sortOrder.equalsIgnoreCase("asc"))
				recordLevelErrorCrit.addOrder(Order.asc("transRec.recordNumber"));
			else
				recordLevelErrorCrit.addOrder(Order.desc("transRec.recordNumber"));
		}
		else if(selectedColumnMap != null && selectedColumnMap.get("selectcolumn")!= null && selectedColumnMap.get("selectcolumn").equalsIgnoreCase(Constants.ELEMENT_NAME))
		{
			if(sortOrder.equalsIgnoreCase("asc"))
				recordLevelErrorCrit.addOrder(Order.asc("elem.description"));
			else
				recordLevelErrorCrit.addOrder(Order.desc("elem.description"));
		}
		else if(selectedColumnMap != null && selectedColumnMap.get("selectcolumn")!= null && selectedColumnMap.get("selectcolumn").equalsIgnoreCase(Constants.DESCRIPTION))
		{
			if(sortOrder.equalsIgnoreCase("asc"))
				recordLevelErrorCrit.addOrder(Order.asc("probDesc.description"));
			else
				recordLevelErrorCrit.addOrder(Order.desc("probDesc.description"));
			
		}
		else if(selectedColumnMap != null && selectedColumnMap.get("selectcolumn")!= null && selectedColumnMap.get("selectcolumn").equalsIgnoreCase("compliance"))
		{
			if(sortOrder.equalsIgnoreCase("asc"))
				recordLevelErrorCrit.addOrder(Order.asc("compCat.name"));
			else
				recordLevelErrorCrit.addOrder(Order.desc("compCat.name"));
		}
		else if(selectedColumnMap != null && selectedColumnMap.get("selectcolumn")!= null && selectedColumnMap.get("selectcolumn").equalsIgnoreCase(Constants.ELEMENT_NUMBER))
		{
			if(sortOrder.equalsIgnoreCase("asc"))
				recordLevelErrorCrit.addOrder(Order.asc("elem.id"));
			else
				recordLevelErrorCrit.addOrder(Order.desc("elem.id"));
		}
		else
			recordLevelErrorCrit.addOrder(Order.asc("nonCompliance.id"));
		
		//------------------End of Detached Criteria ------------------
		
		List<RecordLevelError> errorList = new LinkedList<RecordLevelError>();
		
		//Retrieving the records from DB
		recordLevelErrorCrit.setFirstResult(firstResult);
		recordLevelErrorCrit.setMaxResults(Integer.parseInt(maxResults));
		recordLevelErrorCrit.setFetchSize(Integer.parseInt(maxResults));
		errorList = recordLevelErrorCrit.list();
		
		Iterator<RecordLevelError> reit = errorList.iterator();
		boolean addToList = true;
		while (reit.hasNext())
		{
			ComplianceErrors complianceErrors = new ComplianceErrors();
			addToList = true;
			RecordLevelError re = reit.next();
			try
			{
				complianceErrors.setRecordNumber(re.getNonCompliance().getDatum().getTransmissionRecord()
						.getRecordNumber());
				

				complianceErrors.setElementNumber(re.getNonCompliance().getDatum()
						.getElement().getName());
				

				complianceErrors.setElementName(re.getNonCompliance().getDatum()
						.getElement().getDescription());
				

				complianceErrors.setCompliance(re.getComplianceCategory().getName());
				

				complianceErrors.setDescription(re.getProblemDescription().getDescription());
				
			}
			catch (Exception e)
			{
				log.error(e.getMessage() + "Compliance Id" + re.getNonCompliance().getId()
						+ "has error and Transmission ID" + re.getNonCompliance().getTransmission().getId()
						+ "is related to it");
				addToList = false;
			}
			
			
			if (addToList)
			{
				list.add(complianceErrors);
			}

		}
				
		return list;

	}
	*/

}
