package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.*;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriodId;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.InterRptPdCheckService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.InterRptPdCheckText;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Implements InterRptPdCheckService
 * @author Adam Russell (18816)
 * @see InterRptPdCheckService
 */
@Transactional
public class InterRptPdCheckServiceImpl extends BaseServiceImpl implements InterRptPdCheckService
{
	private final RecordForFileComparisonAcrossReportPeriodDAO recordForFileComparisonAcrossReportPeriodDAO;
	private final TransmissionDAO transmissionDAO;
	private final SubmissionDAO submissionDAO;
	private final TransmissionRecordDAO transmissionRecordDAO;
	private final FullRecordDAO fullRecordDAO;
	private final ReportingPeriodDAO reportingPeriodDAO;
	private final ElementDAO elementDAO;
	private final DataExtractionService dataExtractionService;
	private final String genericProblemDescriptionTemplate;

	public InterRptPdCheckServiceImpl(RecordForFileComparisonAcrossReportPeriodDAO recordForFileComparisonAcrossReportPeriodDAO, TransmissionDAO transmissionDAO,
	                                  SubmissionDAO submissionDAO,
	                                  TransmissionRecordDAO transmissionRecordDAO,
	                                  FullRecordDAO fullRecordDAO,
	                                  ReportingPeriodDAO reportingPeriodDAO,
	                                  ElementDAO elementDAO,
	                                  DataExtractionService dataExtractionService)
	{
		super();
		this.recordForFileComparisonAcrossReportPeriodDAO = recordForFileComparisonAcrossReportPeriodDAO;
		this.transmissionDAO = transmissionDAO;
		this.submissionDAO = submissionDAO;
		this.transmissionRecordDAO = transmissionRecordDAO;
		this.fullRecordDAO = fullRecordDAO;
		this.reportingPeriodDAO = reportingPeriodDAO;
		this.elementDAO = elementDAO;
		this.dataExtractionService = dataExtractionService;
		
		this.genericProblemDescriptionTemplate = InterRptPdCheckText.GENERIC;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getTransmissionWithId(Long)
	 */
	@Override
	public Transmission getTransmissionWithId(Long id)
	{
		return transmissionDAO.getTransmissionWithId(id);
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCommonCrossReportPeriodAdvisoriesForYouth(RecordToExport, RecordToExport)
	 */
	@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCommonCrossReportPeriodAdvisoriesForYouth(
			RecordToExport currentRecord, RecordToExport previousRecord)
	{
		assert(currentRecord != null);
		assert(previousRecord != null);
		
		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();
		
		// For data of birth (element 4), note any change.
		if (currentRecord.getE4() == null
		 || (currentRecord.getE4() != null && !currentRecord.getE4().equals(previousRecord.getE4())))
		{
			addAdvisoryToSet(resultSet, previousRecord, "4", currentRecord.getE4(), previousRecord.getE4());
		}
		
		// For sex (element 5), note a change from male to female or female to male.
		if (previousRecord.getE5() != null && currentRecord.getE5() != null
		 && ((previousRecord.getE5().equals("male") && currentRecord.getE5().equals("female"))
		 || (previousRecord.getE5().equals("female") && currentRecord.getE5().equals("male"))))
		{
			addAdvisoryToSet(resultSet, previousRecord, "5", currentRecord.getE5(), previousRecord.getE5());
		}
		
		// For race (elements 6-11), note a change from no to yes or yes to no.
		// Changes to and from blank are always allowed for race, since it is acceptable to decline at any point.
		String[] elements = {"6", "7", "8", "9", "10", "11"};
		for (String element : Arrays.asList(elements))
		{
			if (previousRecord.getElementValue(element) != null
			 && currentRecord.getElementValue(element) != null
			 && ((previousRecord.getElementValue(element).equals("yes")
			 && currentRecord.getElementValue(element).equals("no"))
			 || (previousRecord.getElementValue(element).equals("no")
			 && currentRecord.getElementValue(element).equals("yes"))))
			{
				addAdvisoryToSet(resultSet, previousRecord, element,
						currentRecord.getElementValue(element),
						previousRecord.getElementValue(element));
			}
		}
		
		// For Hispanic or Latino Ethnicity (element 13), note these changes:
		//
		// + No -> Yes
		// + No -> Unknown
		// + Yes -> No
		// + Yes -> Unknown
		if (previousRecord.getE13() != null && currentRecord.getE13() != null
		 && ((previousRecord.getE13().equals("no") && currentRecord.getE13().equals("yes"))
		 || (previousRecord.getE13().equals("no") && currentRecord.getE13().equals("unknown"))
		 || (previousRecord.getE13().equals("yes") && currentRecord.getE13().equals("no"))
		 || (previousRecord.getE13().equals("yes") && currentRecord.getE13().equals("unknown"))))
		{
			addAdvisoryToSet(resultSet, previousRecord, "13", currentRecord.getE13(), previousRecord.getE13());
		}
		
		return resultSet;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCrossReportPeriodAdvisoriesForServedYouth(RecordToExport, State)
	 */
	@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForServedYouth(
			RecordToExport record, State state)
	{
		/*assert(record != null);
		assert(record.getTransmissionRecord().getServedPopulation() != null);
		
		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();
		TransmissionRecord previousTransmissionRecord = transmissionRecordDAO.getPreviousRecordForServedYouth(
				record.getTransmissionRecord(), state);
		RecordToExport previousRecord = fullRecordDAO.getFullRecord(previousTransmissionRecord);
		
		if (previousRecord == null)
		{
			return resultSet;
		}
		
		// Get advisories related to common elements.
		resultSet.addAll(getCommonCrossReportPeriodAdvisoriesForYouth(record, previousRecord));
		
		// For Federally-Recognized Tribe (element 16), note any change.
		if ((record.getE16() == null && previousRecord.getE16() != null)
		 || (record.getE16() != null && !record.getE16().equals(previousRecord.getE16())))
		{
			addAdvisoryToSet(resultSet, previousRecord, "16", record.getE16(), previousRecord.getE16());
		}
		
		// For Adjudicated Delinquent (element 17), note any change.
		if ((record.getE17() == null && previousRecord.getE17() != null)
		 || (record.getE17() != null && !record.getE17().equals(previousRecord.getE17())))
		{
			addAdvisoryToSet(resultSet, previousRecord, "17", record.getE17(), previousRecord.getE17());
		}
		
		// For Education Level (element 18), note any decrease.
		Map<String, Integer> educationLevelValues = new HashMap<String, Integer>(10);
		Integer currentEducationLevel;
		Integer previousEducationLevel;
		educationLevelValues.put("under 6", 1);
		educationLevelValues.put("6", 2);
		educationLevelValues.put("7", 3);
		educationLevelValues.put("8", 4);
		educationLevelValues.put("9", 5);
		educationLevelValues.put("10", 6);
		educationLevelValues.put("11", 7);
		educationLevelValues.put("12", 8);
		educationLevelValues.put("post secondary", 9);
		educationLevelValues.put("college", 10);
		currentEducationLevel = educationLevelValues.get(record.getE18());
		previousEducationLevel = educationLevelValues.get(previousRecord.getE18());
		if (!(currentEducationLevel == null && previousEducationLevel == null)
		 && ((currentEducationLevel == null && previousEducationLevel != null)
		 || (currentEducationLevel != null && previousEducationLevel == null)
		 || (currentEducationLevel.compareTo(previousEducationLevel) < 0)))
		{
			addAdvisoryToSet(resultSet, previousRecord, "18", record.getE18(), previousRecord.getE18());
		}
		
		return resultSet;*/
		return null;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCrossReportPeriodAdvisoriesForBaselineYouth(RecordToExport, ReportingPeriod, State)
	 */
	@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForBaselineYouth(
			RecordToExport record, ReportingPeriod reportPeriod, State state)
	{
	/*	assert(record != null);
		assert(reportPeriod != null);
		assert(record.getTransmissionRecord().getOutcomePopulation() != null);
		assert(record.getTransmissionRecord().getOutcomePopulation().getName().equalsIgnoreCase("Baseline"));
		
		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();
		RecordToExport nextRecord;
		boolean birthdayInBaselineBuffer = false;
		
		// If birthday categorizes youth as potential pre buffer,
		// check the ensuing report period for a post buffer record.
		try
		{
			birthdayInBaselineBuffer = isBirthdayInBaselineBuffer(record,
					record.getTransmission().getReportingPeriod());
		}
		catch (ParseException e)
		{
			birthdayInBaselineBuffer = true;
		}
		if (birthdayInBaselineBuffer)
		{
			nextRecord = fullRecordDAO.getFullRecord(transmissionRecordDAO.getPostBufferRecordForBaselineYouth(
					record.getTransmissionRecord(), reportPeriod, state));

			if (nextRecord != null)
			{
				String recordNumber = nextRecord.getE3RecordNumber();
				
				String problemDescription = InterRptPdCheckText.PRE_TO_POSTBUFFER_BASELINE_DUPLICATE;
				
				problemDescription = String.format(problemDescription,
						recordNumber, nextRecord.getReportingPeriod());
				
				FileAdvisoryAcrossReportPeriods advisory = new FileAdvisoryAcrossReportPeriods();
				advisory.setRecordNumber(recordNumber);
				advisory.setReportPeriodName(nextRecord.getReportingPeriod());
				advisory.setTransmissionId(nextRecord.getTransmission().getId().toString());
				advisory.setElementName("N/A");
				advisory.setProblemDescription(problemDescription);
				
				resultSet.add(advisory);
			}
		}
		
		return resultSet;	*/
		return null;
	}

	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCrossReportPeriodAdvisoriesForPreBufferBaselineYouth(TransmissionRecord, Transmission)
	 */
@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForPreBufferBaselineYouth(
			TransmissionRecord transmissionRecord, Transmission nextTransmission)
	{
		/*	assert(transmissionRecord != null);
		assert(nextTransmission != null);
		assert(transmissionRecord.getOutcomePopulation() != null);
		assert(transmissionRecord.getOutcomePopulation().getName().equalsIgnoreCase("Pre-buffer"));
		
		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();
		RecordToExport preBufferRecord = fullRecordDAO.getFullRecord(transmissionRecord);
		TransmissionRecord postBufferTransmissionRecord = transmissionRecordDAO
				.getPostBufferRecordForPreBufferYouth(transmissionRecord, nextTransmission);

		if (postBufferTransmissionRecord == null)
		{
			String recordNumber = preBufferRecord.getE3RecordNumber();
			
			String problemDescription = InterRptPdCheckText.MISSING_POSTBUFFER;
			
			problemDescription = String.format(problemDescription,
					recordNumber, preBufferRecord.getReportingPeriod());
			
			FileAdvisoryAcrossReportPeriods advisory = new FileAdvisoryAcrossReportPeriods();
			advisory.setRecordNumber(recordNumber);
			advisory.setReportPeriodName(preBufferRecord.getReportingPeriod());
			advisory.setTransmissionId(preBufferRecord.getTransmission().getId().toString());
			advisory.setElementName("N/A");
			advisory.setProblemDescription(problemDescription);
			
			resultSet.add(advisory);
		}
		
		return resultSet;*/
	return null;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCrossReportPeriodAdvisoriesForPostBufferBaselineYouth(RecordToExport, ReportingPeriod, State)
	 */
@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForPostBufferBaselineYouth(
			RecordToExport record, ReportingPeriod reportPeriod, State state)
	{
			/*assert(record != null);
		assert(reportPeriod != null);
		assert(record.getTransmissionRecord().getOutcomePopulation() != null);
		assert(record.getTransmissionRecord().getOutcomePopulation().getName().equalsIgnoreCase("Post-buffer"));

		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();
		TransmissionRecord previousTransmissionRecord = transmissionRecordDAO
				.getPreviousRecordForPostBufferYouthComparison(record.getTransmissionRecord(), reportPeriod,
						state);
		RecordToExport previousRecord = fullRecordDAO.getFullRecord(previousTransmissionRecord);

		try
		{
			if (!isBirthdayInReportPeriod(record, reportPeriod))
			{
				String recordNumber = record.getE3RecordNumber();
				
				String problemDescription = InterRptPdCheckText.UNTIMELY_BUFFER;
				
				problemDescription = String.format(problemDescription,
						recordNumber, reportPeriod.getName());
				
				FileAdvisoryAcrossReportPeriods advisory = new FileAdvisoryAcrossReportPeriods();
				advisory.setRecordNumber(recordNumber);
				advisory.setReportPeriodName(reportPeriod.getName());
				if (previousRecord != null)
				{
					advisory.setTransmissionId(previousRecord.getTransmission().getId().toString());
				}
				else
				{
					String fileNumber = submissionDAO.getSubmissionFileNumberOfReportPeriod(state, reportPeriod);
					advisory.setTransmissionId(fileNumber);
				}
				advisory.setElementName("N/A");
				advisory.setProblemDescription(problemDescription);
				
				resultSet.add(advisory);
			}
		}
		catch (ParseException e)
		{
			log.error(e.getMessage());
		}
		
		return resultSet;*/
	return null;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCrossReportPeriodAdvisoriesForFollowup19Youth(RecordToExport, ReportingPeriod, State)
	 */
	@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForFollowup19Youth(
			RecordToExport record, ReportingPeriod previousOutcomesReportPeriod, State state)
	{
		/*assert(record != null);
		assert(previousOutcomesReportPeriod != null);
		assert(record.getTransmissionRecord().getOutcomePopulation() != null);
		assert(record.getTransmissionRecord().getOutcomePopulation().getName().equalsIgnoreCase("Follow-up 19"));
		
		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();

		TransmissionRecord previousRecord = transmissionRecordDAO
				.getBaselineRecordForFollowup19YouthComparison(record.getTransmissionRecord(),
						previousOutcomesReportPeriod, state);

		getCrossReportPeriodAdvisoriesForFollowupYouth(record, previousRecord, previousOutcomesReportPeriod, state, resultSet);
		
		return resultSet;*/
		return null;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getCrossReportPeriodAdvisoriesForFollowup21Youth(RecordToExport, ReportingPeriod, State)
	 */
	@Override
	public Collection<FileAdvisoryAcrossReportPeriods> getCrossReportPeriodAdvisoriesForFollowup21Youth(
			RecordToExport record, ReportingPeriod previousOutcomesReportPeriod, State state)
	{
		/*assert (record != null);
		assert (previousOutcomesReportPeriod != null);
		assert (record.getTransmissionRecord().getOutcomePopulation() != null);
		assert (record.getTransmissionRecord().getOutcomePopulation().getName()
		        .equalsIgnoreCase("Follow-up 21"));

		Set<FileAdvisoryAcrossReportPeriods> resultSet = new HashSet<FileAdvisoryAcrossReportPeriods>();
		TransmissionRecord previousRecord = transmissionRecordDAO
				.getFollowup19RecordForFollowup21YouthComparison(record.getTransmissionRecord(),
						previousOutcomesReportPeriod, state);

		getCrossReportPeriodAdvisoriesForFollowupYouth(record, previousRecord, previousOutcomesReportPeriod,
				state, resultSet);

		return resultSet;*/
		return null;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see InterRptPdCheckService#getFileComparisonAcrossReportPeriods(Transmission)
	 */
	@Override
	public FileComparisonAcrossReportPeriods getFileComparisonAcrossReportPeriods(Transmission targetFile)
	{
		/*assert(targetFile != null);
		
		List<RecordToExport> targetRecords = fullRecordDAO.getFullRecordsForTransmission(targetFile);
		FileComparisonAcrossReportPeriods fileComparison = new FileComparisonAcrossReportPeriods();
		ReportingPeriod currentReportPeriod = targetFile.getReportingPeriod();
		ReportingPeriod previousOutcomesReportingPeriod = null;
		State state = targetFile.getState();
		if (currentReportPeriod.getOutcomeAge() != null)
		{
			previousOutcomesReportingPeriod = reportingPeriodDAO
					.getPreviousOutcomesReportingPeriod(currentReportPeriod);
		}
		ReportingPeriod precedingReportPeriod = reportingPeriodDAO
				.getPrecedingReportPeriod(currentReportPeriod);
		ReportingPeriod ensuingReportPeriod = reportingPeriodDAO.getEnsuingReportPeriod(currentReportPeriod);

		for (RecordToExport currentRecord : targetRecords)
		{
			TransmissionRecord currentTransmissionRecord = currentRecord.getTransmissionRecord();
			
			if (currentTransmissionRecord.getServedPopulation() != null)
			{
				fileComparison.addAll(getCrossReportPeriodAdvisoriesForServedYouth(currentRecord, state));
			}
			
			if (currentTransmissionRecord.getOutcomePopulation() != null)
			{
				String populationType = currentTransmissionRecord.getOutcomePopulation().getName();
				
				if (populationType.equalsIgnoreCase("Baseline") && ensuingReportPeriod != null
				 && (targetFile.getTransmissionType().getName().equalsIgnoreCase("Subsequent")
				  || targetFile.getTransmissionType().getName().equalsIgnoreCase("Corrected")))
				{
					fileComparison.addAll(getCrossReportPeriodAdvisoriesForBaselineYouth(
							currentRecord, ensuingReportPeriod, state));
				}
				else if (populationType.equalsIgnoreCase("Post-buffer") && precedingReportPeriod != null)
				{
					fileComparison.addAll(getCrossReportPeriodAdvisoriesForPostBufferBaselineYouth(
							currentRecord, precedingReportPeriod, state));
				}
				else if (populationType.equalsIgnoreCase("Follow-up 19") && previousOutcomesReportingPeriod != null)
				{
					fileComparison.addAll(getCrossReportPeriodAdvisoriesForFollowup19Youth(
							currentRecord, previousOutcomesReportingPeriod, state));
				}
				else if (populationType.equalsIgnoreCase("Follow-up 21") && previousOutcomesReportingPeriod != null)
				{
					fileComparison.addAll(getCrossReportPeriodAdvisoriesForFollowup21Youth(
							currentRecord, previousOutcomesReportingPeriod, state));
				}
			}
		}

		if (precedingReportPeriod != null)
		{
			List<TransmissionRecord> previousPreBufferRecords;
			previousPreBufferRecords = transmissionRecordDAO.getPreviousBufferCaseRecords(precedingReportPeriod,
					state);
			
			for (TransmissionRecord previousPreBufferRecord : previousPreBufferRecords)
			{
				fileComparison.addAll(getCrossReportPeriodAdvisoriesForPreBufferBaselineYouth(
						previousPreBufferRecord, targetFile));
			}
		}
		
		return fileComparison;*/
		return null;
	}
	
	private boolean isBirthdayInBaselineBuffer(RecordToExport fullRecord,
			ReportingPeriod reportPeriod) throws ParseException
	{
		assert(fullRecord != null);
		assert(reportPeriod != null);
		
		// Determine start and end of buffer period.
		Calendar bufferEndDate = (Calendar) reportPeriod.getEndReportingDate().clone();
		Calendar bufferStartDate = (Calendar) bufferEndDate.clone();
		// bufferStartDate is 45 days before bufferEndDate
		bufferStartDate.add(Calendar.DAY_OF_YEAR, -1 * Constants.BUFFER_PERIOD_LENGTH);
		
		// Convert birthday specified in fullRecord
		DateFormat e4Format = new SimpleDateFormat("yyyy-MM-dd");
		assert(fullRecord.getE4() != null);  // Baseline youth must have valid birthday!
		Date birthDate = e4Format.parse(fullRecord.getE4());
		Calendar birthday = new GregorianCalendar();
		birthday.setTime(birthDate);
		
		// Advance age of youth in record 17 years.
		birthday.add(Calendar.YEAR, 17);
		
		// Determine if birthday falls in buffer range.
		return (birthday.after(bufferStartDate) && birthday.before(bufferEndDate));
	}
	
	private boolean isBirthdayInReportPeriod(RecordToExport fullRecord,
			ReportingPeriod reportPeriod) throws ParseException
	{
		assert(fullRecord != null);
		assert(reportPeriod != null);
		
		// Determine start and end of report period.
		Calendar endDate = (Calendar) reportPeriod.getEndReportingDate().clone();
		Calendar startDate = (Calendar) reportPeriod.getStartReportingDate().clone();
		
		// Convert birthday specified in fullRecord
		DateFormat e4Format = new SimpleDateFormat("yyyy-MM-dd");
		assert(fullRecord.getE4() != null);  // Baseline youth must have valid birthday!
		Date birthDate = e4Format.parse(fullRecord.getE4());
		Calendar birthday = new GregorianCalendar();
		birthday.setTime(birthDate);
		
		// Advance age of youth in record 17 years.
		birthday.add(Calendar.YEAR, 17);
		
		// Determine if birthday falls in buffer range.
		return (birthday.after(startDate) && birthday.before(endDate));
	}
	
	private void getCrossReportPeriodAdvisoriesForFollowupYouth(
			RecordToExport currentRecord,
			TransmissionRecord previousTransmissionRecord,
			ReportingPeriod previousOutcomesReportPeriod,
			State state,
			Set<FileAdvisoryAcrossReportPeriods> advisorySet)
	{
		assert(currentRecord != null);
		assert(previousOutcomesReportPeriod != null);
		
		RecordToExport previousRecord = fullRecordDAO.getFullRecord(previousTransmissionRecord);
		
		if (previousRecord == null)
		{
			return;
		}
		
		// Get advisories related to common elements.
		advisorySet.addAll(getCommonCrossReportPeriodAdvisoriesForYouth(
				currentRecord, previousRecord));
		
		// For Highest educational certification received (element 46), note any decrease.
		Map<String, Integer> educationLevelValues = new HashMap<String, Integer>(6);
		Integer currentEducationLevel;
		Integer previousEducationLevel;
		educationLevelValues.put("high school", 1);
		educationLevelValues.put("vocational certificate", 2);
		educationLevelValues.put("vocational license", 2);
		educationLevelValues.put("associate", 4);
		educationLevelValues.put("bachelor", 5);
		educationLevelValues.put("higher degree", 6);
		currentEducationLevel = educationLevelValues.get(currentRecord.getE46());
		previousEducationLevel = educationLevelValues.get(previousRecord.getE46());
		if (!(currentEducationLevel == null && previousEducationLevel == null)
		 && ((currentEducationLevel == null && previousEducationLevel != null)
		 || (currentEducationLevel != null && previousEducationLevel == null)
		 || (currentEducationLevel.compareTo(previousEducationLevel) < 0)))
		{
			addAdvisoryToSet(advisorySet, previousRecord, "46", currentRecord.getE46(), previousRecord.getE46());
		}
	}
	
	private void addAdvisoryToSet(Collection<FileAdvisoryAcrossReportPeriods> set,
			RecordToExport previousRecord, String elementNumber, String currentValue, String previousValue)
	{
		assert(previousRecord != null);
		
		String recordNumber = previousRecord.getE3RecordNumber();
		
		if (currentValue == null)
		{
			currentValue = "blank";
		}
		if (previousValue == null)
		{
			previousValue = "blank";
		}
		
		Element element = elementDAO.getElementByName(elementNumber);
		String elementDescription = element.getDescription();
		
		String problemDescription = String.format(genericProblemDescriptionTemplate,
				recordNumber, currentValue, getElementLabel(elementNumber),
				previousRecord.getReportingPeriod(), previousValue);
		
		FileAdvisoryAcrossReportPeriods advisory2 = new FileAdvisoryAcrossReportPeriods();
		FileAdvisoryAcrossReportPeriodId advisory = new FileAdvisoryAcrossReportPeriodId();
		advisory.setRecordNumber(recordNumber);
		advisory.setReportPeriodName(previousRecord.getReportingPeriod());
	//	advisory.setTransmissionId(previousRecord.getTransmission().getId().toString());
		advisory.setTransmissionId(previousRecord.getTransId().toString());
		advisory.setElementName(elementNumber);
		advisory2.setId(advisory);
		advisory2.setElementDescription(elementDescription);
		advisory2.setProblemDescription(problemDescription);
		advisory2.setRecordNumber(recordNumber);
		advisory2.setTransmissionId(previousRecord.getTransId().toString());
		
		//set.add(advisory);
		set.add(advisory2);
	}
	
	/**
	 * @author Adam Russell (18816)
 	 * @see DataExtractionService#getElementLabel(Integer)
 	 */
	private String getElementLabel(String elementNumber)
	{
	//	Map<String, String> fields = dataExtractionService.getFields();
	//	return dataExtractionService.getElementLabel(fields, elementNumber);
		return dataExtractionService.getElementLabel(elementNumber);
	}

	/**
	 * @see InterRptPdCheckService#getFileAdvisoriesAcrossReportPeriods(String)
	 */
	@Override
	public List<FileAdvisoryAcrossReportPeriods> getFileAdvisoriesAcrossReportPeriods(
			String transmissionId,long siteUserId)
	{
		List<FileAdvisoryAcrossReportPeriods> fileAdvisoryList = null;
		Map<String,String> elementDescriptionMap = new HashMap<String, String>();
		FileAdvisoryAcrossReportPeriods fileAdvisory = null;
		String elementDesc = null;
		String elementId = null;
		String probDesc = null;
		fileAdvisoryList = recordForFileComparisonAcrossReportPeriodDAO.getRecordsForFileComparisonAcrossReportPeriod(Long.valueOf(transmissionId), siteUserId);
		log.debug("Received records for file comparison. Categorizing them now...");
	// get session associated with current thread
		//Session dbSession = getSessionFactory().getCurrentSession();
		//EntityManager em = dbSession.getEntityManagerFactory().createEntityManager();
		//StoredProcedureQuery qry = em.createNamedStoredProcedureQuery("spCrossReportPdCompare");
		//Query qry = session.getNamedQuery("crossReportPdCompare");
		//qry.setParameter(2, Long.parseLong(TransmissionId));
		//qry.setParameter(3, siteUserId);
		System.out.println("Transmission ID: "+transmissionId+"siteUserId: "+siteUserId);
		long l1 = System.currentTimeMillis();
		//fileAdvisoryList = qry.getResultList();
		long l2 = System.currentTimeMillis();
		System.out.println("*****Time taken for Cross file check across report periods: "+(l2-l1)+" millisecs *****");
		Iterator<FileAdvisoryAcrossReportPeriods> fileAdvisoryItr = fileAdvisoryList.iterator();
		while(fileAdvisoryItr.hasNext())
		{
			fileAdvisory = fileAdvisoryItr.next();
			//elementId = fileAdvisory.getElementName();
			elementId = fileAdvisory.getId().getElementName();
			System.out.println("elementID: "+elementId);
			elementDescriptionMap.put("N/A", "N/A");
			if(!elementDescriptionMap.containsKey(elementId))
			{
				System.out.println("getting element description for element"+elementId);
				Element element = elementDAO.getElementByName(elementId);
				elementDesc = element.getDescription();
				elementDescriptionMap.put(elementId, elementDesc);
			}
			else
			{
				elementDesc = elementDescriptionMap.get(elementId);
			}
			fileAdvisory.setElementDescription(elementDesc);
			probDesc = fileAdvisory.getProblemDescription();
			probDesc = String.format(probDesc,
					fileAdvisory.getId().getRecordNumber(), fileAdvisory.getId().getSelectedValue(), getElementLabel(elementId),
					fileAdvisory.getId().getReportPeriodName(), fileAdvisory.getId().getTargetValue());
			fileAdvisory.setProblemDescription(probDesc);
		}
		return  fileAdvisoryList;
	}
}
