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
package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.Transmission;
import org.hibernate.HibernateException;

import javax.sql.DataSource;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;

/**
 * DAo to call stored procedure
 * 
 * @author 15178
 * 
 */

public interface StoredProcedureDAO
{
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
			java.sql.Timestamp timeStamp, Clob xmlData) throws HibernateException, SQLException;

	/**
	 * To set datasource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource);

	/**
	 * To get datasource
	 * 
	 * @return
	 */
	public DataSource getDataSource();

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
			java.sql.Timestamp timeStamp) throws HibernateException, SQLException;

	/**
	 * Call stored procedure to process rules
	 * 
	 * @param transmissionId
	 * @return transmission
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public List<Transmission> processRules(Long transmissionId) throws HibernateException, SQLException;
	
}