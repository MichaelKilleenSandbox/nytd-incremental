package gov.hhs.acf.cb.nytd.service;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 23, 2010
 */
public class FileFormatException extends TransmissionException
{
	public FileFormatException()
	{
		super();
	}

	public FileFormatException(String message)
	{
		super(message);
	}

	public FileFormatException(Throwable cause)
	{
		super(cause);
	}
}
