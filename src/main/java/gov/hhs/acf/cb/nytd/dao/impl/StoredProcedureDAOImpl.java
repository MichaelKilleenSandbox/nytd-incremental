/**
 * Filename: BaseDAO.java
 * 
 * Copyright 2009, ICF International Created: May 21, 2009 Author: 15178
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
package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.StoredProcedureDAO;
import gov.hhs.acf.cb.nytd.dao.helper.ProcessTransmissionRulesHelper;
import gov.hhs.acf.cb.nytd.dao.helper.SaveTransmissionHelper;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import javax.sql.DataSource;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Implementation of stored procedure DAO
 * 
 * @author 15178
 * 
 */
public class StoredProcedureDAOImpl implements StoredProcedureDAO
{
	protected Logger log = Logger.getLogger(getClass());

	private DataSource dataSource;

	/**
	 * To set datasource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	/**
	 * To get datasource
	 * 
	 * @return
	 */
	public DataSource getDataSource()
	{
		return this.dataSource;
	}

	/**
	 * Stored procedure to save transmitted XML file data
	 * 
	 * @param fileName
	 * @param fileSize
	 * @param timeStamp
	 * @param xmlData
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public List<Transmission> saveTransmissionXML(String fileName, Long fileSize,
			java.sql.Timestamp timeStamp, Clob xmlData) throws HibernateException, SQLException
	{
		// TODO Auto-generated method stub
		log.debug("in saveTransmission");
		SaveTransmissionHelper proc = new SaveTransmissionHelper(getDataSource(),
				Constants.STORED_PROC_INSERT_TRANSMISSION);
		log.debug("calling proc execute");
		@SuppressWarnings("unused")
		Map<?, ?> transmissionMap = proc.execute(fileName, fileSize, timeStamp, xmlData);
		return proc.getTransmissionList();
	}

	/**
	 * Call stored procedure to submit transmission
	 * 
	 * @param transmissionId
	 * @param siteUserId
	 * @param timeStamp
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public List<Transmission> submitTransmission(Long transmissionId, Long siteUserId,
			java.sql.Timestamp createdDate) throws HibernateException, SQLException
	{
		// TODO Auto-generated method stub
		log.debug("in saveTransmission");
		SaveTransmissionHelper proc = new SaveTransmissionHelper(getDataSource(),
				Constants.STORED_PROC_SUBMIT_SUBMISSION);
		log.debug("calling proc execute");
		@SuppressWarnings("unused")
		Map<?, ?> submissionMap = proc.execute(transmissionId, siteUserId, createdDate);
		return proc.getSubmissionList();
	}
	
	/**
	 * Call stored procedure to process rules
	 * 
	 * @param transmissionId
	 * @return transmission
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public List<Transmission> processRules(Long transmissionId) throws HibernateException, SQLException
	{
		// TODO Auto-generated method stub
		log.debug("In Process Rules");	
		ProcessTransmissionRulesHelper proc = new ProcessTransmissionRulesHelper(getDataSource(),
				Constants.STORED_PROC_PROCESS_RULES);
		log.debug("calling proc execute");
		@SuppressWarnings("unused")
		Map<?, ?> transmissionMap = proc.execute(transmissionId);
		log.debug("size of transmission List:"+transmissionMap.size());
		return proc.getTransmissionList();
	}
	
}
