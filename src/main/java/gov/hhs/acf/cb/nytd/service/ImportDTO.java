package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.Transmission;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 24, 2010
 */
public class ImportDTO
{
	private Transmission transmission;
	private File transmissionFile;

	public Transmission getTransmission()
	{
		return transmission;
	}

	public void setTransmission(Transmission transmission)
	{
		this.transmission = transmission;
	}

	public File getTransmissionFile()
	{
		return transmissionFile;
	}

	public void setTransmissionFile(File transmissionFile)
	{
		this.transmissionFile = transmissionFile;
	}
}
