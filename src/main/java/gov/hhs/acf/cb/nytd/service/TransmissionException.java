package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.Transmission;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 15, 2010
 */
public class TransmissionException extends ImportException {
	Transmission transmission;

	public TransmissionException()
	{
		super();
	}

	public TransmissionException(String message)
	{
		super(message);
	}

	public TransmissionException(Throwable cause)
	{
		super(cause);
	}

	public Transmission getTransmission()
	{
		return transmission;
	}

	public void setTransmission(Transmission transmission)
	{
		this.transmission = transmission;
	}
}
