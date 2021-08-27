package gov.hhs.acf.cb.nytd.service;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 1, 2010
 */
public class SubmissionException extends TransmissionException
{
	public SubmissionException()
	{
		super();
	}

	public SubmissionException(String message)
	{
		super(message);
	}

	public SubmissionException(Throwable cause)
	{
		super(cause);
	}
}
