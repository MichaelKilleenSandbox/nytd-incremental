package gov.hhs.acf.cb.nytd.service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 22, 2010
 */
public class LoginException extends Exception
{
        //TODO: SonarLint - Exception classes should be immutable. 
        // If make this to final, unable to override loginErrors.
	private List<String> loginErrors;

	public LoginException()
	{
		super();
	}

	public LoginException(List<String> loginErrors)
	{
		this();
		this.loginErrors = loginErrors;
	}

	public List<String> getLoginErrors()
	{
		return loginErrors;
	}

	public void setLoginErrors(List<String> loginErrors)
	{
		this.loginErrors = loginErrors;
	}
}
