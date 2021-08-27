package gov.hhs.acf.cb.nytd.jobs;

import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

public class DeleteOldExportsJob extends QuartzJobBean
{
	protected final Logger log = Logger.getLogger(getClass());
	
	public DeleteOldExportsJob()
	{
	}

	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
	{
		log.debug("Starting old export delete");
		File exportDir;
		try
		{
			exportDir = new File(new URI(getExportLocation()));
		}
		catch (URISyntaxException e)
		{
			throw new JobExecutionException(e);
		}
		log.debug("exportDir = " + exportDir.getAbsolutePath());
		Calendar now = Calendar.getInstance();
		long month = 86400000L * 30;

		if (exportDir.exists() && exportDir.isDirectory())
		{
			File[] exportFiles = exportDir.listFiles();
			for (int i = 0; i < exportFiles.length; i++)
			{
				if ((now.getTimeInMillis() - exportFiles[i].lastModified()) > month)
				{
					log.debug("deleting " + exportFiles[i].getName());
					dataExtractionService.deleteExportMetadata(exportFiles[i].getName());
					exportFiles[i].delete();
				}
			}
		}

		log.debug("finished old export delete");
	}

	private DataExtractionService dataExtractionService;

	public void setDataExtractionService(DataExtractionService dataExtractionService)
	{
		this.dataExtractionService = dataExtractionService;
	}

	private String exportLocation;

	public String getExportLocation()
	{
		return exportLocation;
	}

	public void setExportLocation(String exportLocation)
	{
		this.exportLocation = exportLocation;
	}
}