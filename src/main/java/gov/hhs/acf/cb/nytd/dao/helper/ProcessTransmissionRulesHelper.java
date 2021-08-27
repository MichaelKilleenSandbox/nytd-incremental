/**
 *  Copyright 2010, ICF International
 *  Created: May 28, 2010
 *  Author: 15178
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.dao.helper;

import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 15178
 *
 */
public class ProcessTransmissionRulesHelper extends StoredProcedure
{
	protected Logger log = Logger.getLogger(getClass());

	TransmissionMapper transmissionMap = new TransmissionMapper();
	DataSource dataSource;

	public ProcessTransmissionRulesHelper(DataSource dataSource, String storedProcedure)
	{
		super(dataSource, storedProcedure);
		this.dataSource = dataSource;
		log.debug("storedProcedure called:" + storedProcedure);
		if (storedProcedure.equalsIgnoreCase(Constants.STORED_PROC_PROCESS_RULES))
		{
			declareParameter(new SqlReturnResultSet("CUR_RESULT", transmissionMap));
			declareParameter(new SqlParameter(Constants.P_TRANSMISSION_ID, Types.BIGINT));
		}
		compile();
		log.debug("successfully compiled");
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
	public Map execute(Long transmissionId)
	{
		Map inParams = new HashMap(1);
		log.debug("transmissionId:" + transmissionId);
		inParams.put(Constants.P_TRANSMISSION_ID, transmissionId);
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

}
