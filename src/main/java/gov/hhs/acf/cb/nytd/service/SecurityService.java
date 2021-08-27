package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.SiteUser;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 22, 2010
 */
public interface SecurityService
{
	public SiteUser login(String userName, String password) throws LoginException;
}
