package gov.hhs.acf.cb.nytd.service;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 15, 2010
 */
public class ImportException extends Exception
{
	File xmlFile;

	public ImportException()
	{
		super();
	}

	public ImportException(String message)
	{
		super(message);
	}

	public ImportException(Throwable cause)
	{
		super(cause);
	}

	public File getXmlFile()
	{
		return xmlFile;
	}

	public void setXmlFile(File xmlFile)
	{
		this.xmlFile = xmlFile;
	}
}
