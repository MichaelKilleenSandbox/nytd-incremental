package gov.hhs.acf.cb.nytd.jobs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jul 27, 2010
 */
public class FilesReceivedJob extends QuartzJobBean implements StatefulJob
{
	// job data map keys
	public static final String FILE_PROCESSING_DIR = "fileProcessingDir";
	public static final String FILE_UPLOAD_DIR = "fileUploadDir";
	public static final String FILES_RECEIVED = "filesReceived";
	public static final String FILES_PROCESSED = "fileProcessedDir";
	public static final String FILES_INACTIVE = "fileInActiveDir";
	public static final String FILES_ACTIVE = "fileActiveDir";
	public static final String FILES_NOTTOPROCESS = "fileNotToProcessDir";
	public static final String FILES_UNPROCESSED = "fileUnProcessedDir";

	private final Logger log = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		String fileProcessingDir = dataMap.getString(FILE_PROCESSING_DIR);
		String fileUploadDir = dataMap.getString(FILE_UPLOAD_DIR);
		Map<String, Long> filesReceived = (Map<String, Long>) dataMap.get(FILES_RECEIVED);

		if (fileProcessingDir == null)
		{
			log.error("missing required parameter " + FILE_PROCESSING_DIR + " in job data map");
			throw new JobExecutionException();
		}
		if (fileUploadDir == null)
		{
			log.error("missing required parameter " + FILE_UPLOAD_DIR + " in job data map");
			throw new JobExecutionException();
		}
		if (filesReceived == null)
		{
			log.error("missing required parameter " + FILES_RECEIVED + " in job data map");
			throw new JobExecutionException();
		}

		// make sure directories exists
		File processingDir;
		File uploadDir;
		try
		{
			processingDir = new File(new URI(fileProcessingDir));
			uploadDir = new File(new URI(fileUploadDir));
		}
		catch (URISyntaxException e1)
		{
			log.error(e1.getMessage(), e1);
			throw new JobExecutionException();
		}
		if (!processingDir.exists())
		{
			log.error("directory " + fileProcessingDir + " invalid");
			throw new JobExecutionException();
		}
		if (!uploadDir.exists() && !uploadDir.mkdir())
		{
			log.error("directory " + fileUploadDir + " invalid");
			throw new JobExecutionException();
		}

		// process xml files in upload directory
		File[] uploadedFiles = uploadDir.listFiles();
		Arrays.sort(uploadedFiles, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
		List<File> uploadedFileList = Arrays.stream(uploadedFiles).filter(file -> !file.getName().equals(".DS_Store")).collect(Collectors.toList());

		for (File xmlFile : uploadedFileList)
		{

			if (filesReceived.containsKey(xmlFile.getName()) && !isPrevTransmittedFile(dataMap,xmlFile.getName()))
			{
				long savedModifiedTime = filesReceived.get(xmlFile.getName());

				if (savedModifiedTime == xmlFile.lastModified())
				{
					// copy file to processing directory, delete original
					// and remove reference from filesReceived
					try
					{
						FileUtils.moveFileToDirectory(xmlFile, processingDir, false);
					}
					catch (IOException e)
					{
						log.error(e.getMessage(), e);
						throw new JobExecutionException();
					}
					filesReceived.remove(xmlFile.getName());
				}
				else
				{
					// update the last modified time in the filesReceived map
					filesReceived.put(xmlFile.getName(), xmlFile.lastModified());
				}
			}
			else
			{
				// add new file to filesReceived
				filesReceived.put(xmlFile.getName(), xmlFile.lastModified());
			}
		}
	}


	/**
	 * @param dataMap
	 * @param filename
	 * @return
	 * @throws JobExecutionException
	 */
	private boolean isPrevTransmittedFile(JobDataMap dataMap,String filename)
			throws JobExecutionException
	{
		boolean isProcessed = false;
		String fileDir = null;
		File processedDir = null;

		// Verify the filename against files in processed folder
		isProcessed = isFileProcessed(dataMap, filename, FILES_PROCESSED);

		//Verify the filename against files in unprocessed folder
		if(!isProcessed)
		{
			isProcessed = isFileProcessed(dataMap, filename, FILES_UNPROCESSED);
		}

		//Verify the filename against files in not-to-process folder
		if(!isProcessed)
		{
			isProcessed = isFileProcessed(dataMap, filename, FILES_NOTTOPROCESS);
		}


		//Verify the filename against files in active folder
		if(!isProcessed)
		{
			isProcessed = isFileProcessed(dataMap, filename, FILES_ACTIVE);
		}

		//Verify the filename against files in inactive folder
		if(!isProcessed)
		{
			isProcessed = isFileProcessed(dataMap, filename, FILES_INACTIVE);
		}

		return isProcessed;
	}

	private boolean isFileProcessed(JobDataMap dataMap, String filename, String filesProcessed) throws JobExecutionException {
		String fileDir;
		File processedDir;
		boolean isProcessed;
		fileDir = dataMap.getString(filesProcessed);
		try {
			processedDir = new File(new URI(fileDir));
		} catch (URISyntaxException e1) {
			log.error(e1.getMessage(), e1);
			throw new JobExecutionException();
		}
		isProcessed = checkThoughFileList(processedDir.listFiles(),filename);
		return isProcessed;
	}

	/**
	 * @param filesArray
	 * @param filename
	 * @return
	 */
	private boolean checkThoughFileList(File[] filesArray, String filename)
	{
		boolean isPresent = false;
		if(Objects.isNull(filesArray)) {
			return false;
		}

		for (int j = 0; j < filesArray.length ; j++)
		{
			if(filesArray[j].getName().equals(filename))
			{
				isPresent = true;
				break;
			}

		}
		return isPresent;
	}
}
