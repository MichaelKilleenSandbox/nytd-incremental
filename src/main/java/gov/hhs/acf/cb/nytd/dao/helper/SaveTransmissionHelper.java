/**
 * Filename: saveTransmissionHelper.java
 * 
 * Copyright 2009, ICF International Created: Jul 20, 2009 Author: 15178
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
package gov.hhs.acf.cb.nytd.dao.helper;

import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.sql.Clob;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 15178
 * 
 */
public class SaveTransmissionHelper extends StoredProcedure
{
	protected Logger log = Logger.getLogger(getClass());

	TransmissionMapper transmissionMap = new TransmissionMapper();
	SubmissionMapper submissionMap = new SubmissionMapper();
	DataSource dataSource;

	public SaveTransmissionHelper(DataSource dataSource, String storedProcedure)
	{
		super(dataSource, storedProcedure);
		this.dataSource = dataSource;
		log.debug("storedProcedure called:" + storedProcedure);
		if (storedProcedure.equalsIgnoreCase(Constants.STORED_PROC_INSERT_TRANSMISSION))
		{
			declareParameter(new SqlReturnResultSet("CUR_RESULT", transmissionMap));
			// declareParameter(new SqlParameter("p_string", Types.VARCHAR));
			declareParameter(new SqlParameter(Constants.P_FILE_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(Constants.P_FILE_SIZE, Types.BIGINT));
			declareParameter(new SqlParameter(Constants.P_FILE_TIMESTAMP, Types.TIMESTAMP));
			declareParameter(new SqlParameter(Constants.P_FILE_DATA, Types.CLOB));
		}
		else if (storedProcedure.equalsIgnoreCase(Constants.STORED_PROC_SUBMIT_SUBMISSION))
		{
			log.debug("declare parameters for submit transmission");
            setFunction(true);
			declareParameter(new SqlReturnResultSet("CUR_RESULT", submissionMap));
			declareParameter(new SqlParameter(Constants.P_TRANSMISSION_ID, Types.BIGINT));
			declareParameter(new SqlParameter(Constants.P_SITE_USER_ID, Types.BIGINT));
			declareParameter(new SqlParameter(Constants.P_CREATED_DATE, Types.TIMESTAMP));
		}
		compile();
		log.debug("successfully compiled");
	}

	@SuppressWarnings( { "unchecked", "static-access" })
	public Map execute(String fileName, Long fileSize, Timestamp fileTimeStamp, Clob xmlData)
	{
		Map inParams = new HashMap(1);
		log.debug("P_FILE_NAME:" + fileName);
		inParams.put(Constants.P_FILE_NAME, fileName);
		log.debug("P_FILE_SIZE:" + fileSize);
		// inParams.put(Constants.P_FILE_SIZE, fileSize);
		inParams.put(Constants.P_FILE_SIZE, 21504);
		//Timestamp tstamp = new Timestamp(00 - 00 - 00);
		//log.debug("P_FILE_TIMESTAMP:" + tstamp.valueOf("2009-07-10 12:50:45.01"));
		//inParams.put(Constants.P_FILE_TIMESTAMP, tstamp.valueOf("2009-07-10 12:50:45.01"));
		log.debug("fileTimeStamp:"+fileTimeStamp);
		inParams.put(Constants.P_FILE_TIMESTAMP, fileTimeStamp);
		log.debug("P_FILE_DATA:" + xmlData);
		inParams.put(Constants.P_FILE_DATA, xmlData);
		log.debug("before execute stored procedure:");
		Map outParams = super.execute(inParams);
		if (outParams.size() > 0)
		{
			log.debug("size of outParams:" + outParams.size() + outParams.toString());
			return outParams;
		}
		else
		{
			log.debug("size of outParams is null:");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Map execute()
	{
		Map inParams = new HashMap(1);
		inParams.put("keyword", "test");
		// log.debug("before execute stored procedure:");
		// again, this sproc has no input parameters, so an empty Map is
		// supplied...
		return super.execute(inParams);
	}

	@SuppressWarnings( { "unchecked" })
	public Map execute(Long transmissionId, Long siteUserId, Timestamp createdDate)
	{
		Map inParams = new HashMap(1);
		log.debug("transmissionId:" + transmissionId);
		inParams.put(Constants.P_TRANSMISSION_ID, transmissionId);
		log.debug("siteUserId:" + siteUserId);
		// inParams.put(Constants.P_FILE_SIZE, fileSize);
		inParams.put(Constants.P_SITE_USER_ID, siteUserId);
		log.debug("createdDate:" + createdDate);
		inParams.put(Constants.P_CREATED_DATE, createdDate);
		log.debug("before execute stored procedure submit Transmission:");
		Map outParams = super.execute(inParams);
		if (outParams.size() > 0)
		{
			log.debug("size of outParams:" + outParams.size() + outParams.toString());
			return outParams;
		}
		else
		{
			log.debug("size of outParams is null:");
			return null;
		}
	}

	/**
	 * @return List
	 */
	public List<Transmission> getTransmissionList()
	{
		return transmissionMap.getTransmissionList();
	}

	/**
	 * @return List
	 */
	public List<Transmission> getSubmissionList()
	{
		return submissionMap.getSubmissionList();
	}

}
