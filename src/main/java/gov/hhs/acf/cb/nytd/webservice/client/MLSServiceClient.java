/**
 * Copyright 2013, ICF International Created: Apr 10, 2013 Author: kpandya
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
package gov.hhs.acf.cb.nytd.webservice.client;

import gov.hhs.acf.cb.nytd.webservice.*;
import org.apache.log4j.Logger;

/**
 * Client Code to retrieve Response from the MLS Service
 * 
 * @author kpandya
 * @return StateRegionContactInfoResponse
 */
public class MLSServiceClient
{
	// logger
	protected final Logger log = Logger.getLogger(getClass());

	public StateRegionContactInfoResponse getStateRegionContactService(
			String stateName, String regionName, String webServiceURL)
	{

		log.info("stateName: " + stateName + " regionName: " + regionName + " MLS URL: " + webServiceURL);

		StateRegionContactInfoResponse response = new StateRegionContactInfoResponse();
		StateRegionContactInfo srcInfo;
		StateRegionContactInfoE srcInfoE;
		MLS_StateRegionContactServiceStub stub = null;
		try
		{
			// instantiate the Client Stub
			stub = new MLS_StateRegionContactServiceStub(webServiceURL);
			
		}
		catch (Exception e)
		{
			log.debug(e.getMessage(), e);
			stub = null;
		}
		
		try
		{
			srcInfoE = new StateRegionContactInfoE();
			srcInfo = new StateRegionContactInfo();
			// create the request packet
			srcInfo.setStatename(stateName);
			srcInfo.setRegionname(regionName);
			srcInfoE.setStateRegionContactInfo(srcInfo);
			// pass the request to the stub and receive the response
			if (stub != null)
			{
				StateRegionContactInfoResponseE responseE = stub.stateRegionContactInfo(srcInfoE);
				response = responseE.getStateRegionContactInfoResponse();
			}
			else
			{
				response = null;
			}

			// stub.
		}
		catch (Exception e)
		{
			log.debug(e.getMessage(), e);
		}
		if (log.isDebugEnabled()) {
			log.debug(response == null ? "response is null" : response.toString());
		}
		return response;
	}

}
