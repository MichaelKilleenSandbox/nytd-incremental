package gov.hhs.acf.cb.nytd.actions.penalty;

import com.opensymphony.xwork2.Action;
import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.*;
import gov.hhs.acf.cb.nytd.util.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Performs actions related to penalties.
 */
public class PenaltyAction extends SearchAction<PenaltySearch> implements SessionAware, ApplicationAware,
		ApplicationContextAware, ServletRequestAware {

    // default logger
    protected transient Logger log = Logger.getLogger(getClass());

    // constants
    private static final String STATES = "states";
    private static final String INITIAL_TRANSMISSION_ID_LIST = "initialTransmissionIdList";
    private static final String FINAL_TRANSMISSION_ID_LIST = "finalTransmissionIdList";
    
    // service objects
    private DataExtractionService dataExtractionService;
    private ComplianceService complianceService;
    private TransmissionServiceP3 transmissionServiceP3;
    @Getter @Setter private PenaltyLetterService penaltyLetterService;
    @Getter @Setter private LoggingService loggingService;
    
    // class properties
    private String tempBuffer = "";
    private boolean skipElement = false;
    private boolean detailedList = false;
    private boolean showElements = false;
    @Getter @Setter private int templateNumber = 0;
    @Getter @Setter private int outcomeAge;
    @Getter @Setter private String transmissionType;
    private PenaltySearch search;
    private PenaltyLetterUtil penaltyLetterUtil;
    private PenaltyBreakdown penaltyBreakdown;
    private PenaltyBreakdown previousPenaltyBreakdown;
    private Map<String, Long> elementNumberMap;
    private Map<String, String> contactInfoMap;
    @Getter @Setter private boolean defaultPage;
    @Getter @Setter private Map<String,String> doubleStatesList = new HashMap<>();
    @Getter @Setter private String userFilename;
    @Getter @Setter private List<String> initialTransmissionIdList = new ArrayList<>();
    @Getter @Setter private List<String> finalTransmissionIdList = new ArrayList<>();
    @Getter @Setter private String exportFilename;
    @Getter @Setter private String exportLocation;
    @Getter @Setter private List<String> selectedReportPeriodsList = new ArrayList<>();
    @Getter @Setter private List<String> selectedStatesList = new ArrayList<>();
    @Setter private ApplicationContext applicationContext;
    @Getter @Setter private List<PenaltyLettersMetadata> plMetadata;
    @Getter private InputStream inputStream;
    @Getter private String contentType;
        
    @Override
    public void prepare() {
        // when using the tab navigation the search object is not created
        if (search == null) {
            search = new PenaltySearch();
        }
        if (super.lookupService == null) {
            super.setLookupService(lookupService);
        }
        super.prepare();
        // multiple penalty letters download metadata
        getGeneratePLParamsFromSession();
        if (penaltyLetterService != null) {
            plMetadata = penaltyLetterService.getPlMetadata();
        }
    }

    /**
     * Displays Penalty Breakdown AKA Compliance Details
     */
    public String transmissionPenalties() {
        showElements = true;
        buildPenaltyBreakdown();

        // display the penalty breakdown page
        return Action.SUCCESS;
    }

    public String clearAggregateComplianceSearch() {
        search.reset();
        initSearch();
        return searchAggregateCompliance();
    }

    public String clearElementComplianceSearch() {
        search.reset();
        initSearch();
        return searchElementCompliance();
    }

    public String searchAggregateCompliance() {
        initSearch();

        // DPM: make sure the getSelectedReportingPeriods contain valid reporting periods.
        checkSelectedReportingPeriodsForMalformedString();
        rebuildAvailableReportingPeriodsList();

        if (getServletRequest().getParameter("printRequest") != null
                && getServletRequest().getParameter("printRequest").equals("true")){
            Iterator<String> itr = search.getSelectedReportingPeriods().iterator();
            if (itr != null && !search.getSelectedReportingPeriods().isEmpty()) {
                String rpStr = itr.next();
                String[] reportPeriods = rpStr.split(",");
                search.getSelectedReportingPeriods().clear();
                for (String reportPeriod : reportPeriods) {
                    if (!search.getSelectedReportingPeriods().contains(reportPeriod)) {
                        search.getSelectedReportingPeriods().add(reportPeriod);
                    }
                }
            }

        }
        if (!search.isJavaScriptEnabled()) {
            Iterator<String> itr = search.getNoJSList().iterator();
            search.getSelectedReportingPeriods().clear();
            if (itr != null) {
                while (itr.hasNext()) {
                    String reprotingPeriod = itr.next();
                    if (!search.getSelectedReportingPeriods().contains(reprotingPeriod)) {
                        search.getSelectedReportingPeriods().add(reprotingPeriod);
                    }
                }
            }
        }

        complianceService.searchAggregatePenalties(search);
        return Action.SUCCESS;
    }

    public String searchElementCompliance() {
        initSearch();

        checkSelectedReportingPeriodsForMalformedString();
        rebuildAvailableReportingPeriodsList();

        if (getServletRequest().getParameter("printRequest") != null
                && getServletRequest().getParameter("printRequest").equals("true")) {
            Iterator<String> itr = search.getSelectedReportingPeriods().iterator();
            if (itr != null && !search.getSelectedReportingPeriods().isEmpty()) {
                String rpStr = itr.next();
                String[] reportPeriods = rpStr.split(",");
                search.getSelectedReportingPeriods().clear();
                for (String reportPeriod : reportPeriods) {
                    if (!search.getSelectedReportingPeriods().contains(reportPeriod)) {
                        search.getSelectedReportingPeriods().add(reportPeriod);
                    }
                }
            }
            itr = search.getSelectedElementNumbers().iterator();
            if (itr != null && !search.getSelectedElementNumbers().isEmpty()) {
                String rpStr = itr.next();
                String[] elemNums = rpStr.split(",");
                search.getSelectedElementNumbers().clear();
                search.getSelectedElementNums().clear();
                for (String elemNum : elemNums) {
                    if (!search.getSelectedElementNumbers().contains(elemNum)) {
                        search.getSelectedElementNumbers().add(elemNum);
                    }
                    search.getSelectedElementNums().add(elementNumberMap.get(elemNum));
                }
            }

        }

        if (!search.isJavaScriptEnabled()) {
            Iterator<String> itr = search.getNoJSList().iterator();
            search.getSelectedReportingPeriods().clear();
            if (itr != null) {
                while (itr.hasNext()) {
                    String reprotingPeriod = itr.next();
                    if (!search.getSelectedReportingPeriods().contains(reprotingPeriod)) {
                        search.getSelectedReportingPeriods().add(reprotingPeriod);
                    }
                }
            }
        }
        complianceService.searchElementPenalties(search);
        
        return Action.SUCCESS;
    }
    
    private void rebuildAvailableReportingPeriodsList() {
        // Application-level reporting periods
        ArrayList<ReportingPeriod> canonicalReportingPeriodList =
                        (ArrayList<ReportingPeriod>) this.getApplication().get(Constants.APPKEY_REPORTING_PERIOD_LIST);
        search.getAvailableReportingPeriods().clear();
        for(ReportingPeriod reportingPeriod :canonicalReportingPeriodList ) {
            if(!search.getSelectedReportingPeriods().contains(reportingPeriod.getName())) {
                search.getAvailableReportingPeriods().add(reportingPeriod.getName());
            }
        }
    }

    private void checkSelectedReportingPeriodsForMalformedString() {
        // Application-level reporting periods
        ArrayList<ReportingPeriod> canonicalReportingPeriodList =
                        (ArrayList<ReportingPeriod>) this.getApplication().get(Constants.APPKEY_REPORTING_PERIOD_LIST);

        // If there are incoming periods that are not in the right format, dont allow
        Iterator<String> itr = search.getSelectedReportingPeriods().iterator();
        if (itr != null && !search.getSelectedReportingPeriods().isEmpty()) {
            while(itr.hasNext()) {
                String rpStr = itr.next();
                String[] reportPeriods = rpStr.split(",");

                // For each incoming string, check against the master list.
                boolean found = false;
                for (String reportPeriod : reportPeriods) {
                    for (Iterator<ReportingPeriod> iterator = canonicalReportingPeriodList.iterator(); iterator.hasNext();) {
                        ReportingPeriod reportingPeriod = iterator.next();
                        if (reportingPeriod.getName().equals(reportPeriod)) {
                            found = true;
                        }
                    }

                    // Something's fishy ... don't render on the page
                    if (!found) {
                        search.getSelectedReportingPeriods().remove(reportPeriod);
                        log.error("Possible HTTP parameter or attribute hacking. Malformed p found.");
                    }
                }
            }
        }
    }

    public String exportAggregateCompliance() {
        // execute the search
        initSearch();
        getComplianceService().searchAggregatePenalties(getSearch());

        // create the exporter and add disclaimer
        AggregatePenaltyExport exporter = new AggregatePenaltyExport(this, dataExtractionService, user);
        exporter.setDisclaimers(Arrays.asList(getText("aggregateCompliance.disclaimer")));

        // export results
        return exporter.export(getServletResponse(), search.getPageResults(), "aggregateCompliancePenalties_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    public String exportElementCompliance() {
        // execute the search
        initSearch();
        getComplianceService().searchElementPenalties(getSearch());

        // create the exporter and add disclaimer
        ElementPenaltyExport exporter = new ElementPenaltyExport(this, dataExtractionService, user);
        exporter.setDisclaimers(Arrays.asList(getText("elementCompliance.disclaimer")));

        // export results
        return exporter.export(getServletResponse(), search.getPageResults(), "elementCompliancePenalties_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    public String exportPenaltyBreakdown() {
        // build the penalty breakdown object
        buildPenaltyBreakdown();

        // add the disclaimers
        List<String> disclaimers = new ArrayList<>();
        disclaimers.add(getText("penaltyBreakdown.disclaimer1"));
        disclaimers.add(getText("penaltyBreakdown.disclaimer2"));
        disclaimers.add(getText("penaltyBreakdown.disclaimer3"));
        penaltyBreakdown.setDisclaimers(disclaimers);

        // export the results
        penaltyBreakdown.setDataExtractionService(dataExtractionService);
        return penaltyBreakdown.export(this, getServletResponse(), "penaltyBreakdown_"
                + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
    }

    /**
     * @return the dataExtractionService
     */
    public DataExtractionService getDataExtractionService() {
        return dataExtractionService;
    }

    /**
     * @param dataExtractionService the dataExtractionService to set
     */
    public void setDataExtractionService(DataExtractionService dataExtractionService) {
        this.dataExtractionService = dataExtractionService;
    }

    /**
     * @return the complianceService
     */
    public ComplianceService getComplianceService() {
        return complianceService;
    }

    /**
     * @param complianceService the complianceService to set
     */
    public void setComplianceService(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @Override
    protected PenaltySearch getPaginatedSearch() {
        return getSearch();
    }

    public PenaltySearch getSearch() {
        return search;
    }

    public void setSearch(PenaltySearch search) {
        this.search = search;
    }

    public PenaltyBreakdown getPenaltyBreakdown() {
        return penaltyBreakdown;
    }

    public void setPenaltyBreakdown(PenaltyBreakdown penaltyBreakdown) {
        this.penaltyBreakdown = penaltyBreakdown;
    }

	/**
	 * action for generating the penalty letters
	 *
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XMLStreamException
	 * @throws FactoryConfigurationError
	 */
        //TODO: Mustuo 8/6/2021 SonarQube - it will reduce a lot of flags by using PenaltyLetterService.parseAndWritePenaltyLetter()
	public String parseAndWritePenaltyLetter() throws FileNotFoundException, IOException, SAXException,
			ParserConfigurationException, XMLStreamException, FactoryConfigurationError
	{
		final Properties properties = new Properties();
		penaltyLetterUtil = new PenaltyLetterUtil();
		Double penaltyAmt = 0.00;
		/*
		 * Load the properties file
		 */
		try
		{
			properties.load(this.getClass().getResourceAsStream("/config/systemConfig.properties"));
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			log.debug(e.getMessage(), e);
		}

		servletResponse.setContentType("application/octet-stream");
		// select the penalty letter template based on the penalty amount and the
		// transmission type
		String penaltyLttrTemplate = penaltyLetterUtil.getPenaltyLetterTemplate(penaltyAmt, properties,
				servletRequest.getParameter("transmission"), servletRequest);
		this.setTemplateNumber(penaltyLetterUtil.getPenaltyLetterTemplateNumber(penaltyAmt, properties, servletRequest.getParameter("transmission"), servletRequest));
		setTransmissionType(servletRequest.getParameter("transmission"));

		// create the name of the file
		// format <state abbreviation>_NYTD_<report
		// period>_<Initial/Final>_Compliance_<date(MMddYYYY)>
		// eg. VA_NYTD_2013A_Initial_Compliance_04042013.doc
		String fileName = penaltyLetterUtil.getFileName(servletRequest);
		servletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".doc");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		OutputStream outputStream = servletResponse.getOutputStream();
		String stateName = servletRequest.getParameter("stateName");
		if(Constants.DISTRICT_OF_COLUMBIA.equals(stateName))
		{
			stateName = servletRequest.getParameter("stateAbbr");
		}
		final XMLStreamWriter xmlStrWriter = new IndentingXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(
				new OutputStreamWriter(outputStream, "utf-8")));
		final SmartXmlTagWriter smartXmlTagWriter = new SmartXmlTagWriter(xmlStrWriter);

		contactInfoMap = penaltyLetterService.getStateRegionContactInfoMap(stateName, servletRequest.getParameter("regionName"),
				properties.getProperty("penaltyLetter.webservice.url"));
		contactInfoMap.put("region.id", penaltyLetterUtil.getRegionIDs().get(servletRequest.getParameter("regionName")));
		contactInfoMap.put("state.stateName",servletRequest.getParameter("stateName"));
		if (log.isDebugEnabled()) {
			contactInfoMap.forEach((k,v) -> log.debug("ContactInfoMap K: " + k + " V: " + v));
		}
		InputStream inputStream = getClass().getResourceAsStream(penaltyLttrTemplate);

		Reader reader = new InputStreamReader(inputStream, "UTF-8");

		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");
		DefaultHandler handler = new DefaultHandler()
		{
			/**
			 * Setting the encoding,version and processing instruction to open the
			 * docx file in word
			 */
			public void startDocument() throws SAXException
			{
				try
				{
					xmlStrWriter.writeStartDocument("UTF-8", "1.0");
					// specifies the program in which the document shall be opened.
					// In this case it will be MS Word
					xmlStrWriter.writeProcessingInstruction("mso-application progid=\"Word.Document\"");
				}
				catch (XMLStreamException e)
				{
					log.error(e.getMessage(), e);
				}
			}

			public void endDocument() throws SAXException
			{
				try
				{
					xmlStrWriter.writeEndDocument();
					xmlStrWriter.close();
				}
				catch (XMLStreamException e)
				{
					log.error(e.getMessage(), e);
				}
			}

			/**
			 * Flush out the characters in the new docx file
			 */
			public void characters(char[] buffer, int start, int length)
			{
				boolean bullets = false;
				boolean suggestions = false;
				try
				{
					tempBuffer = new String(buffer, start, length);
					StringBuilder sBldr = new StringBuilder(tempBuffer);
					String startDelim = properties.getProperty("penaltyLetter.template.placeholder.start");
					String endDelim = properties.getProperty("penaltyLetter.template.placeholder.end");
					String dateDelim = properties.getProperty("penaltyLetter.template.placeholder.date.delim");
					// start finding the placeholder
					if (StringUtils.contains(sBldr.toString(), startDelim))
					{
						// counter to check if there are more than one placeholders in
						// one line
						// for eg. {City, State, Zip}
						String placeholder = StringUtils.substring(sBldr.toString(), sBldr.toString().indexOf(
								startDelim) + 1, sBldr.toString().indexOf(endDelim));

						String updatedString = "";
						if (placeholder.contains(","))
						{
							String[] placeholderArray = StringUtils.split(placeholder, ",");

							for (String token : placeholderArray)
							{
								if (sBldr.toString().contains(token))
								{
									//updatedString = updatedString + contactInfoMap.get(token.trim()) + ", ";
									if (token.contains("city"))
										updatedString = updatedString + contactInfoMap.get(token.trim()) + ", ";
									else
										updatedString = updatedString + contactInfoMap.get(token.trim()) + " ";
								}
							}
							//updatedString = StringUtils.removeEnd(updatedString, ", ");
							sBldr = new StringBuilder(updatedString);

						}
						else
						{
							if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_PREVIOUS)
									|| StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_CURRENT)
									|| StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_SUGGESTIONS))
							{
								bullets = true;
								if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_PREVIOUS))
								{
									// build the Previous penalty breakdown object
									detailedList = false;
									suggestions = false;
									buildPreviousPenaltyBreakdown();
								}
								else if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_CURRENT))
								{
									// create the current penalty object and the detailed
									// items along with it
									detailedList = true;
									suggestions = false;
									search.setTransmissionId(Long.parseLong(servletRequest
											.getParameter("currentTransmissionId")));
									buildPenaltyBreakdown();
								}
								else if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_SUGGESTIONS))
								{
									suggestions = true;
									detailedList = false;
									buildPenaltyBreakdown();
								}
							} // end block: bullet place holders are present

							// replace the amount field with $$$$$
							else if (StringUtils.contains(placeholder, Constants.DOLLAR_AMOUNT_PLACEHOLDER))
							{
								sBldr = new StringBuilder("$$$$$");
							}
							else if (StringUtils.contains(placeholder, Constants.PERCENTAGE_FINE_PLACEHOLDER))
							{
								sBldr = new StringBuilder(servletRequest.getParameter("penaltyAmt") + "%");
							}
							else if (StringUtils.contains(placeholder, Constants.PERCENTAGE_FINE_PLACEHOLDER_REGULAR))
							{
								sBldr = new StringBuilder(transmissionServiceP3.getPenaltyAmtForInactiveRegularFile(Long.parseLong(servletRequest.getParameter("currentTransmissionId")),Long.parseLong(servletRequest.getParameter("stateId")),Long.parseLong(servletRequest.getParameter("reportingPeriodId"))) + "%");
							}
							else if (StringUtils.contains(placeholder, Constants.REPORT_PERIOD_PLACEHOLDER))
							{
								sBldr = new StringBuilder(servletRequest.getParameter("reportPeriodName"));
							}

							else
							{
								if (contactInfoMap.get(placeholder) != null && !"null".equalsIgnoreCase(contactInfoMap.get(placeholder))) {
									String finalText = sBldr
									.toString()
									.replace(placeholder,contactInfoMap.get(placeholder));
									finalText = StringUtils.remove(finalText,
											startDelim);
									finalText = StringUtils.remove(finalText,
											endDelim);
									sBldr = new StringBuilder(finalText);
								}
								else if (!placeholder.equalsIgnoreCase("Date")) {
									if (StringUtils.contains(placeholder, Constants.SALUTATION_PREFIX_PLACEHOLDER)) {
										sBldr = new StringBuilder("<<No salutation/prefix for contact found in MLS>>");
									} else {
										//no value exists for the placeholder
										sBldr = new StringBuilder();
									}
								}
							}
						}
					} // end of block: if (StringUtils.contains(sBldr.toString(), startDelim)) {
					// insert year in the date
					else if (StringUtils.contains(sBldr.toString(), dateDelim))
					{
						String updatedString = sBldr.toString();
						/*String updatedString = StringUtils.remove(sBldr.toString(), dateDelim);
						if (StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION))
							sBldr = new StringBuilder(StringUtils.replace(updatedString, "End Report Period",
									servletRequest.getParameter("correctedFileEndReportDate")));
						else
						sBldr = new StringBuilder(StringUtils.replace(updatedString, "End Report Period",
								servletRequest.getParameter("endRptDate")));*/
						if(StringUtils.contains(updatedString,"^End Report Period^"))
							sBldr = new StringBuilder(StringUtils.replace(updatedString, "^End Report Period^",
									servletRequest.getParameter("endRptDate")));
						else if(StringUtils.contains(updatedString,"^Corrected End Report Period^"))
							sBldr = new StringBuilder(StringUtils.replace(updatedString, "^Corrected End Report Period^",
									servletRequest.getParameter("correctedFileEndReportDate")));

					}
					// if the parser encounters the bullet placeholder
					if (bullets)
					{

						List<String> bulletList = new ArrayList<String>();
						List<String> bulletListSuggestions = new ArrayList<String>();
						boolean isSampled = false;
						boolean temphold = false;
						if(outcomeAge == 21)
							isSampled = true;
						if(suggestions)
						{
							// display File submission standard suggestions
							temphold = isSampled;
								isSampled = true;
							if (penaltyBreakdown.getFileSubmissionDesc() != null)
							{
								if(penaltyBreakdown.getFileSubmissionDesc().get(Constants.ERROR_FREE_INFO_LC) != null)
								{
									bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("File Error free Information"));
								}

								//START: Task#26 -Additional updates requested by Miguel- See email with subject -Letter Missing Information
								if(penaltyBreakdown.getFileSubmissionDesc().get(Constants.TIMELY_DATA) != null || penaltyBreakdown.getFileSubmissionDesc().get(Constants.FILE_FORMAT) != null)
								{
									StringBuilder timelyDataComplianceStmtStrBuilder =new StringBuilder();
									String timelyDataComplianceStatement =PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("File Submission Standard Timely Data OR File Format");

									if(StringUtils.contains(timelyDataComplianceStatement,"^Corrected End Report Period^")){
										 timelyDataComplianceStmtStrBuilder = new StringBuilder(StringUtils.replace(timelyDataComplianceStatement,
												 "^Corrected End Report Period^",servletRequest.getParameter("correctedFileEndReportDate")));
									}

									bulletListSuggestions.add(timelyDataComplianceStmtStrBuilder.toString());
								}

							}

							// display data submission standard suggestions
							if (penaltyBreakdown.getDataStandardDesc() != null)
							{
								if(penaltyBreakdown.getDataStandardDesc().get(Constants.ERROR_FREE_INFO_LC) != null)
								{
									bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Data Error free Information"));
								}
								if(penaltyBreakdown.getDataStandardDesc().get(Constants.UNIVERSE) != null)
								{
									if(!isSampled)
										bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.UNIVERSE));
									else
										bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Universe_SAMPLED"));
								}
								//Prashanth Task#26 -Removing the code to check null on outcomes universe
								if((penaltyBreakdown.getDataStandardDesc().get(Constants.FOSTER_CARE_PARTICIPATION) != null )
										&& (penaltyBreakdown.getDataStandardDesc().get(Constants.DISCHARGED_PARTICIPATION) != null ))
								{
									if(!isSampled){
										bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Foster Care Youth 2"));
										bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.DISCHARGED_PARTICIPATION));
									} else {
										bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Foster Care Youth_SAMPLED 2"));
										bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Discharged Youth_SAMPLED"));
									}
								} else {
									if(penaltyBreakdown.getDataStandardDesc().get(Constants.FOSTER_CARE_PARTICIPATION) != null )
									{
										if(!isSampled)
											bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.FOSTER_CARE_PARTICIPATION));
										else
											bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Foster Care Youth_SAMPLED"));
									}
									if(penaltyBreakdown.getDataStandardDesc().get(Constants.DISCHARGED_PARTICIPATION) != null)
									{
										if(!isSampled)
											bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.DISCHARGED_PARTICIPATION));
										else
											bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Discharged Youth_SAMPLED"));
									}
								}

							}
							if(bulletListSuggestions!=null && bulletListSuggestions.size() >0)
							{

								penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletListSuggestions);
								bulletListSuggestions.clear();
							}

							isSampled = temphold;

						}

						// show only the Compliance Category information for
						// previous penalty break down object with no detailed
						// element list
						if (!detailedList)
						{
							if (previousPenaltyBreakdown != null)
							{
								if (previousPenaltyBreakdown.getFileSubmissionDesc() != null)
								{
									if(previousPenaltyBreakdown.getFileSubmissionDesc().get(Constants.TIMELY_DATA) != null)
									{
										if(templateNumber == 5 || templateNumber == 6 )
										{

											bulletList.add(previousPenaltyBreakdown.getFileSubmissionDesc2().get(Constants.TIMELY_DATA));
										}
										if(!suggestions)
										{
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}
									}
									if(previousPenaltyBreakdown.getFileSubmissionDesc().get(Constants.ERROR_FREE_INFO_LC) != null)
									{
										if(templateNumber == 5 || templateNumber == 6 )
										{
											bulletList.add(previousPenaltyBreakdown.getFileSubmissionDesc2().get(Constants.ERROR_FREE_INFO_LC));
										}

										if(!suggestions)
										{
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}


										List<String> dataElementList = new ArrayList<String>();
										// display the detailed list of file submission
										// data elements
										if (previousPenaltyBreakdown.getFileSubmissionComplianceStdPenaltyDesc() != null)
										{
											for (String desc : previousPenaltyBreakdown
													.getFileSubmissionComplianceStdPenaltyDesc())
											{
												dataElementList.add(desc);
											}
											if(!suggestions)
											{
												penaltyLetterUtil.writeWhiteLine(xmlStrWriter,1);
												penaltyLetterUtil.createDataElementList(xmlStrWriter, dataElementList);
												penaltyLetterUtil.writeWhiteLine(xmlStrWriter,0);
											}
										}

									}
									if(previousPenaltyBreakdown.getFileSubmissionDesc().get(Constants.FILE_FORMAT) != null)
									{
										if(templateNumber == 5 || templateNumber == 6 )
										{
											bulletList.add(previousPenaltyBreakdown.getFileSubmissionDesc2().get(Constants.FILE_FORMAT));
										}

										if(!suggestions)
										{
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}
									}


								}
								if (previousPenaltyBreakdown.getDataStandardDesc() != null) {
									if (previousPenaltyBreakdown.getDataStandardDesc().get(Constants.ERROR_FREE_INFO_LC) != null) {
										if (templateNumber == 5 || templateNumber == 6) {
											bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.ERROR_FREE_INFO_LC));
										}

										if (!suggestions) {
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}


										List<String> dataElementList = new ArrayList<String>();
										// display the detailed list of data standard
										// data elements only for Error Free Information
										// non compliance
										if (previousPenaltyBreakdown.getDataStandardComplianceStdPenaltyDesc() != null) {
											for (String desc : previousPenaltyBreakdown
													.getDataStandardComplianceStdPenaltyDesc()) {
												if (!StringUtils.contains(desc, "Data Element 36") && !StringUtils.contains(desc, "Data Element 3 - Record Number (45 CFR 1356.85(a)(3))"))
													dataElementList.add(desc);
											}
											if (!suggestions) {
												penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
												penaltyLetterUtil.createDataElementList(xmlStrWriter, dataElementList);
												penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 0);
											}
										}

									}
									if (previousPenaltyBreakdown.getDataStandardDesc().get(Constants.UNIVERSE) != null) {
										if (templateNumber == 5 || templateNumber == 6) {
											bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.UNIVERSE));
										}

										//Prashanth -6/26 -Fix for Task#26 Section6
										if (!suggestions) {
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}
									}

									boolean dischargedYouthDescAdded = false;
									if (previousPenaltyBreakdown.getDataStandardDesc().get(Constants.FOSTER_CARE_PARTICIPATION) != null) {
										if (templateNumber == 5 || templateNumber == 6) {

											if (previousPenaltyBreakdown.getDataStandardDesc().get(Constants.DISCHARGED_PARTICIPATION) != null) {

												bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.FOSTER_CARE_PARTICIPATION) + Constants.PENALTY_LETTER_PUNCTUATION_COLON + Constants.PENALTY_LETTER_WORD_AND);
												bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.DISCHARGED_PARTICIPATION));

												dischargedYouthDescAdded = true;
											} else {

												bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.FOSTER_CARE_PARTICIPATION) + Constants.PENALTY_LETTER_PUNCTUATION_PERIOD);
											}


										}

										if (!suggestions) {
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}
									}
									if (previousPenaltyBreakdown.getDataStandardDesc().get(Constants.DISCHARGED_PARTICIPATION) != null) {
										if ((templateNumber == 5 || templateNumber == 6) && !dischargedYouthDescAdded) {
												bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.DISCHARGED_PARTICIPATION));
										}

										if (!suggestions) {
											penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
											penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
											bulletList.clear();
										}
									}

								}
							}
							else
							{
								if(!suggestions)
								{
									bulletList.add(Constants.NO_DATA_AVAILABLE);
									penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
									penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
								}
							}
						}
						// show the compliance category info of current penalty
						// breakdown object with detailed element list
						//Prashanth 06/12/2017: Task#26 . This block of code populates the summarized non-compliance descriptions for Final-Determination-NONCOMPLIANT-No-Corrected-File-xml.xml (Template=4)
						// and also populates detailed non-compliance descriptions for Initial-Determination-NONCOMPLIANT-With-Data-xml.xml; Final-Determination-NONCOMPLIANT-With-Corrected-File-xml.xml
						// template =2 or 5
						else
						{
							showElements = true;
							buildPenaltyBreakdown();
							// display file submission standard
							if (penaltyBreakdown.getFileSubmissionDesc() != null)
							{

								writeBulletSection(Constants.FILE_SUBMISSION_STANDARDS, Constants.TIMELY_DATA,
										templateNumber, penaltyBreakdown.getFileSubmissionErrors());
								writeBulletSection(Constants.FILE_SUBMISSION_STANDARDS, Constants.ERROR_FREE_INFO,
										templateNumber, penaltyBreakdown.getFileSubmissionErrors());
								writeBulletSection(Constants.FILE_SUBMISSION_STANDARDS, Constants.FILE_FORMAT,
										templateNumber, penaltyBreakdown.getFileSubmissionErrors());

							}
							// display data submission standard
							if (penaltyBreakdown.getDataStandardDesc() != null)
							{

								writeBulletSection(Constants.DATA_STANDARDS, Constants.ERROR_FREE_INFO,
										templateNumber, penaltyBreakdown.getDataErrors());
								writeBulletSection(Constants.DATA_STANDARDS, Constants.UNIVERSE,
										templateNumber, penaltyBreakdown.getDataErrors());
								writeBulletSection(Constants.DATA_STANDARDS, Constants.FOSTER_CARE_PARTICIPATION,
										templateNumber, penaltyBreakdown.getDataErrors());
								writeBulletSection(Constants.DATA_STANDARDS, Constants.DISCHARGED_PARTICIPATION,
										templateNumber, penaltyBreakdown.getDataErrors());
								writeBulletSection(Constants.DATA_STANDARDS, Constants.FILE_FORMAT,
										templateNumber, penaltyBreakdown.getDataErrors());

							}
						}

					} // end block: if (bullets) {
					else
					{
						if (!skipElement)
						{
							smartXmlTagWriter.write(sBldr.toString());
						}
					}
				}
				catch (XMLStreamException e)
				{
					log.error(e.getMessage(), e);
				}
			}

			/**
			 *
			 * @param complianceCategory
			 * @param templateNumber
			 * @param errors
			 */
			private void writeBulletSection(String complianceSuperCategory, String complianceCategory,
											int templateNumber, List<NytdError> errors) {
				Set<String> errorSet = new HashSet<>();
				List<String> bulletList = new ArrayList<>();

				errors.stream()
						.filter(error -> StringUtils.equalsIgnoreCase(
								complianceCategory, error.getComplianceCategory().getName()))
						.map(NytdError::formatErrorMessage)
						.filter(msg -> !StringUtils.equalsIgnoreCase(msg, Constants.NOT_APPLICABLE_ABBREV))
						.forEach(errorSet::add);

				if (!errorSet.isEmpty()) {
					penaltyLetterUtil.writeWhiteLine(xmlStrWriter,1);
					penaltyLetterUtil.createBulletedList(xmlStrWriter,
							getErrorDescription(complianceSuperCategory, complianceCategory, templateNumber));

					// Write the detailed error description for only Final Non-Compliant Letters
					if (templateNumber == 4 || templateNumber == 5) {
						bulletList.addAll(errorSet);
						penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList, "1");
					}
					errorSet.clear();
				}
			}

			/**
			 *
			 * @param complianceCategory
			 * @param templateNumber
			 * @return
			 */
			private List<String> getErrorDescription(String complianceSuperCategory,
													 String complianceCategory, int templateNumber) {
				if (StringUtils.equalsIgnoreCase(complianceCategory, Constants.TIMELY_DATA)) {
					return getTimelyDataDescription();
				}
				else if (StringUtils.equalsIgnoreCase(complianceSuperCategory, Constants.FILE_SUBMISSION_STANDARDS)) {
					return Collections.singletonList(templateNumber != 4 ?
							penaltyBreakdown.getFileSubmissionDesc().get(complianceCategory) :
							penaltyBreakdown.getFileSubmissionDesc2().get(complianceCategory));
				}
				else { // Data Standards
					return Collections.singletonList(templateNumber != 4 ?
							penaltyBreakdown.getDataStandardDesc().get(complianceCategory) :
							penaltyBreakdown.getDataStandardDesc2().get(complianceCategory));
				}
			}

			private List<String> getTimelyDataDescription() {
				String tempTimelyData = penaltyBreakdown.getFileSubmissionDesc().get(Constants.TIMELY_DATA);
				if(StringUtils.contains(servletRequest.getParameter("reportPeriodName"), 'A'))
				{
					tempTimelyData = StringUtils.replace(tempTimelyData, "{March 31 or September 30}", "March 31");
					tempTimelyData = StringUtils.replace(tempTimelyData, "{May 15 or November 14}", "May 15");
				}
				else
				{
					tempTimelyData = StringUtils.replace(tempTimelyData, "{March 31 or September 30}", "September 30");
					tempTimelyData = StringUtils.replace(tempTimelyData, "{May 15 or November 14}", "November 14");
				}
				return Collections.singletonList(tempTimelyData);
			}

			// parser encounters the beginning of the xml element
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException
			{
				skipElement = false;

				try
				{
					// skip the {bulleted list} placeholder while being inserted in
					// the new doc
					if (qName.equals("w:p") || qName.equals("w:pPr") || qName.equals("w:rPr")
							|| qName.equals("w:sz") || qName.equals("w:r") || qName.equals("w:t"))
					{
						if (StringUtils.isNotBlank(attributes.getValue("id")))
						{
							if (attributes.getValue("id").equals("list"))
							{
								skipElement = true;
							}
						}
					}
					// if the xml element is just a regular node push it in the new
					// docx file
					if (!skipElement)
					{
						smartXmlTagWriter.writeStartElement(qName);
						// iterate over the attributes associated with the element
						for (int i = 0; i < attributes.getLength(); i++)
						{
							String attrName = attributes.getLocalName(i) != null ? attributes.getLocalName(i) : "";
							String attrVal = attributes.getValue(i) != null ? attributes.getValue(i) : "";

							smartXmlTagWriter.writeAttribute(attrName, attrVal);
						}
					}

				}
				catch (XMLStreamException e)
				{
					log.error(e.getMessage(), e);
				}
			}

			/**
			 * Parser encounters the end of the element
			 */
			public void endElement(String uri, String localName, String qName) throws SAXException
			{
				try
				{
					// skip adding the end of the element for {bulleted list}
					// placeholder
					if (!skipElement)
					{
						smartXmlTagWriter.writeEndElement(qName);
					}
				}
				catch (XMLStreamException e)
				{
					log.error(e.getMessage(), e);
				}
			}
		};

		saxParser.parse(is, handler);
		inputStream.close();
		// xmlStrWriter.flush();

		// outputStream.flush();
		outputStream.close();

		return null;
	}

	public void logPenaltyLetterGeneration() {
		try {
			loggingService.logPenaltyLetterGeneration(transmissionServiceP3.getTransmission(
					Long.valueOf(servletRequest.getParameter(Constants.TRANSMISSION_ID_REQUEST_PARAM))),
					(SiteUser) session.get(Constants.SITE_USER));
		}
		catch (Exception e) {
			log.error("Error when attempting to log penalty letter generation.", e);
		}
	}

	/**
	 * Creates the Penalty Break down object of the last inactive transmission
	 * submitted
	 */
	private void buildPreviousPenaltyBreakdown()
	{
		ComplianceService complianceSvc = getComplianceService();
		TransmissionServiceP3 transmissionSvc = getTransmissionServiceP3();
		List<ComplianceCategory> fileSubmissionCategories = complianceSvc
				.getFileSubmissionStandardsCategories();
		List<ComplianceCategory> dataCategories = complianceSvc.getDataStandardsCategories();
		List<NytdError> previousFileSubmissionErrors;
		List<NytdError> previousDataErrors;

		Long previousTransmissionId = transmissionSvc.getTransmissionIdOfInactiveStatus(Long.parseLong(servletRequest.getParameter("stateId")), Long.parseLong(servletRequest.getParameter("reportingPeriodId")));

		// create hashmaps of error counts for each super category
		// the maps are used by penaltyBreakdown.jsp to calculate
		// column rowspans in the breakdown HTML tables
		if (previousTransmissionId != null)
		{
			Map<ComplianceCategory, Integer> previousFileSubmissionErrorCounts = new HashMap<ComplianceCategory, Integer>();
			for (ComplianceCategory category : fileSubmissionCategories)
			{
				previousFileSubmissionErrorCounts.put(category, complianceSvc.getErrorCountForCategory(
						previousTransmissionId, category));
			}

			Map<ComplianceCategory, Integer> previousDataErrorCounts = new HashMap<ComplianceCategory, Integer>();
			for (ComplianceCategory category : dataCategories)
			{
				previousDataErrorCounts.put(category, complianceSvc.getErrorCountForCategory(
						previousTransmissionId, category));
			}

			// calculate the data penalties
			Map<ComplianceCategory, Double> previousDataPenalties = new HashMap<ComplianceCategory, Double>();
			for (ComplianceCategory category : dataCategories)
			{
				previousDataPenalties.put(category, complianceSvc.calcDataPenalty(previousTransmissionId,
						category));
			}

/*			// calculate errors
			previousFileSubmissionErrors = complianceSvc.getErrorsForCategories(previousTransmissionId,
					complianceSvc.getFileSubmissionStandardsCategories());
			//previousDataErrors = complianceSvc.getErrorsForCategories(previousTransmissionId, complianceSvc
			//		.getDataStandardsCategories());
			previousDataErrors = new ArrayList<NytdError>();*/

			if(showElements) {
				previousFileSubmissionErrors = complianceSvc.getErrorsForCategories(previousTransmissionId,
						complianceSvc.getFileSubmissionStandardsCategories());
				previousDataErrors = complianceSvc.getErrorsForCategories(previousTransmissionId,
						complianceSvc.getDataStandardsCategories());
			} else {
				previousFileSubmissionErrors = new ArrayList<NytdError>();
				previousDataErrors = new ArrayList<NytdError>();
			}
			// initialize the penalty breakdown
			this.previousPenaltyBreakdown = new PenaltyBreakdown();
			this.previousPenaltyBreakdown.setDataPenalties(previousDataPenalties);
			this.previousPenaltyBreakdown.setFileSubmissionErrorCounts(previousFileSubmissionErrorCounts);
			this.previousPenaltyBreakdown.setDataErrorCounts(previousDataErrorCounts);
			this.previousPenaltyBreakdown.setFileSubmissionErrors(previousFileSubmissionErrors);
			this.previousPenaltyBreakdown.setDataErrors(previousDataErrors);

			//Prashanth -6/26 -Task#26 -Section 6 fix -Generate consistent bullets or non-compliance with data files in both initial and final letters
			this.previousPenaltyBreakdown.setFileSubmissionComplianceStdPenaltyDesc(createComplianceStandardPenaltyLetterDesc(previousFileSubmissionErrors));
			this.previousPenaltyBreakdown.setDataStandardComplianceStdPenaltyDesc(createComplianceStandardPenaltyLetterDesc(previousDataErrors));

			this.previousPenaltyBreakdown.setDataStandardDesc(createDataStandardErrorMapForPenalty(
					dataCategories, previousPenaltyBreakdown));
			this.previousPenaltyBreakdown.setFileSubmissionDesc(createFileSubmissionErrorMapForPenalty(
					fileSubmissionCategories, previousPenaltyBreakdown));
			this.previousPenaltyBreakdown.setDataStandardDesc2(createDataStandardErrorMapForPenalty2(
					dataCategories, previousPenaltyBreakdown));
			this.previousPenaltyBreakdown.setFileSubmissionDesc2(createFileSubmissionErrorMapForPenalty2(
					fileSubmissionCategories, previousPenaltyBreakdown));

			// add missing categories to error lists
			this.previousPenaltyBreakdown.addMissingCategories(fileSubmissionCategories, dataCategories);
		}

	}

	private void buildPenaltyBreakdown()
	{
		// load the compliance categories
		ComplianceService dao = getComplianceService();
		List<ComplianceCategory> fileSubmissionCategories = dao.getFileSubmissionStandardsCategories();
		List<ComplianceCategory> dataCategories = dao.getDataStandardsCategories();
		List<NytdError> fileSubmissionErrors;
		List<NytdError> dataErrors;

		// create hashmaps of error counts for each super category
		// the maps are used by penaltyBreakdown.jsp to calculate
		// column rowspans in the breakdown HTML tables
		Map<ComplianceCategory, Integer> fileSubmissionErrorCounts = new HashMap();
		for (ComplianceCategory category : fileSubmissionCategories)
		{
			fileSubmissionErrorCounts.put(category, dao.getErrorCountForCategory(search.getTransmissionId(),
					category));
		}

		Map<ComplianceCategory, Integer> dataErrorCounts = new HashMap();
		for (ComplianceCategory category : dataCategories)
		{
			dataErrorCounts.put(category, dao.getErrorCountForCategory(search.getTransmissionId(), category));
		}

		// calculate the data penalties
		Map<ComplianceCategory, Double> dataPenalties = new HashMap();
		for (ComplianceCategory category : dataCategories)
		{
			dataPenalties.put(category, dao.calcDataPenalty(search.getTransmissionId(), category));
		}

		if(showElements) {
			dataErrors = dao.getErrorsForCategories(search.getTransmissionId(), dao.getDataStandardsCategories());
			fileSubmissionErrors = dao.getErrorsForCategories(search.getTransmissionId(), dao
					.getFileSubmissionStandardsCategories());
		} else {
			fileSubmissionErrors = new ArrayList<NytdError>();
			dataErrors = new ArrayList<NytdError>();
		}

		// initialize the penalty breakdown
		this.penaltyBreakdown = new PenaltyBreakdown();
		this.penaltyBreakdown.setDataPenalties(dataPenalties);
		this.penaltyBreakdown.setFileSubmissionErrorCounts(fileSubmissionErrorCounts);
		this.penaltyBreakdown.setDataErrorCounts(dataErrorCounts);
		this.penaltyBreakdown.setFileSubmissionErrors(fileSubmissionErrors);
		this.penaltyBreakdown.setDataErrors(dataErrors);
		this.penaltyBreakdown
				.setFileSubmissionComplianceStdPenaltyDesc(createComplianceStandardPenaltyLetterDesc(fileSubmissionErrors));
		this.penaltyBreakdown
				.setDataStandardComplianceStdPenaltyDesc(createComplianceStandardPenaltyLetterDesc(dataErrors));
		this.penaltyBreakdown.setDataStandardDesc(createDataStandardErrorMapForPenalty(dataCategories,
				penaltyBreakdown));
		this.penaltyBreakdown.setDataStandardDesc2(createDataStandardErrorMapForPenalty2(dataCategories,
				penaltyBreakdown));
		this.penaltyBreakdown.setFileSubmissionDesc(createFileSubmissionErrorMapForPenalty(
				fileSubmissionCategories, penaltyBreakdown));
		this.penaltyBreakdown.setFileSubmissionDesc2(createFileSubmissionErrorMapForPenalty2(
				fileSubmissionCategories, penaltyBreakdown));
		// add missing categories to error lists
		this.penaltyBreakdown.addMissingCategories(fileSubmissionCategories, dataCategories);
	}

	/**
	 * Creates a map of the file submission compliance category and its penalty
	 * letter description
	 *
	 * @param fileSubmissionCategories
	 * @return
	 */
	private Map<String, String> createFileSubmissionErrorMapForPenalty(
			List<ComplianceCategory> fileSubmissionCategories, PenaltyBreakdown penaltyBreakdown)
	{
		Map<String, String> fileSubmissionDesc = new CaseInsensitiveMap<>();

		for (ComplianceCategory cc : fileSubmissionCategories)
		{
			Integer count = penaltyBreakdown.getFileSubmissionErrorCounts().get(cc);
			if (count > 0)
			{
				fileSubmissionDesc.put(cc.getName(), cc.getPenaltyLetterDesc());
			}
		}
		return fileSubmissionDesc;
	}

	private Map<String, String> createFileSubmissionErrorMapForPenalty2(
			List<ComplianceCategory> fileSubmissionCategories, PenaltyBreakdown penaltyBreakdown)
	{
		Map<String, String> fileSubmissionDesc2 = new CaseInsensitiveMap<>();

		for (ComplianceCategory cc : fileSubmissionCategories)
		{
			Integer count = penaltyBreakdown.getFileSubmissionErrorCounts().get(cc);
			if (count > 0)
			{
				fileSubmissionDesc2.put(cc.getName(), cc.getPenaltyLetterDesc2());
			}
		}
		return fileSubmissionDesc2;
	}

	/**
	 * Creates a map for Data standard errors for Penalty Letters containing the
	 * compliance category name and the penalty letter description
	 *
	 * @param dataCategories
	 * @return
	 */
	private Map<String, String> createDataStandardErrorMapForPenalty(List<ComplianceCategory> dataCategories,
			PenaltyBreakdown penaltyBreakdown)
	{
		Map<String, String> dataStandardDesc = new CaseInsensitiveMap<>();
		for (ComplianceCategory cc : dataCategories)
		{
			Integer count = penaltyBreakdown.getDataErrorCounts().get(cc);
			if (count > 0)
			{
				dataStandardDesc.put(cc.getName(), cc.getPenaltyLetterDesc());
			}
		}
		return dataStandardDesc;
	}

	private Map<String, String> createDataStandardErrorMapForPenalty2(List<ComplianceCategory> dataCategories,
			PenaltyBreakdown penaltyBreakdown)
	{
		Map<String, String> dataStandardDesc2 = new CaseInsensitiveMap<>();
		for (ComplianceCategory cc : dataCategories)
		{
			Integer count = penaltyBreakdown.getDataErrorCounts().get(cc);
			if (count > 0)
			{
				dataStandardDesc2.put(cc.getName(), cc.getPenaltyLetterDesc2());
			}
		}
		return dataStandardDesc2;
	}

	/**
	 * Creates a list of Compliance Standard Penalty Letter Description for a
	 * file submission and data standards
	 *
	 * @param errorCategories
	 * @return
	 */
	private List<String> createComplianceStandardPenaltyLetterDesc(List<NytdError> errorCategories)
	{
		Map<Long, String> csPenaltyLttrDescMap = getComplianceService()
				.getComplianceStandardPenaltyLetterDesc();
		List<String> finalCsPenaltyLttrDescList = new ArrayList<String>();
		for (NytdError ne : errorCategories)
		{
			Long elementId = null;
			if (ne.getNonCompliance() != null && ne.getNonCompliance().getDataAggregate() != null
					&& ne.getNonCompliance().getDataAggregate().getElement() != null)
			{
				elementId = ne.getNonCompliance().getDataAggregate().getElement().getId();

				if (elementId != null)
				{
					finalCsPenaltyLttrDescList.add(csPenaltyLttrDescMap.get(elementId));
				}
			}
		}

		return finalCsPenaltyLttrDescList;
	}

	private void initSearch()
	{
		// Initialize the search form based on user's role
		UserRoleEnum role = UserRoleEnum.getRole(getPrimaryUserRole().getName());
		switch (role)
		{
			case ADMIN:
			case FEDERAL:
				getSearch().setViewSubmissionsOnly(true);
				break;
			case REGIONAL:
				setStatesInRegion(lookupService.getRegionStates(user.getRegion()));
				String regionStates = new String();
				Iterator<State> statesIterator = getStatesInRegion().iterator();

				if (search.getStateName() != null && search.getStateName().equalsIgnoreCase("All"))
				{
					while (statesIterator.hasNext())
					{
						State state = statesIterator.next();
						regionStates += state.getStateName() + ";";
					}
					getSearch().setStateName(regionStates);
				}

				getSearch().setViewSubmissionsOnly(true);
				break;
			case STATE:
				getSearch().setStateName(user.getState().getStateName());
				break;
		}

		// load the available reporting periods and store in a map by reporting
		// period id
		List<ReportingPeriod> reportingPeriods = getLookupService().getReportingPeriods();

		// initialize the reporting period properties of the search
		if (search.getAvailableReportingPeriods().isEmpty() && search.getSelectedReportingPeriods().isEmpty())
		{
			for (ReportingPeriod rp : reportingPeriods)
			{
				search.getAvailableReportingPeriods().add(rp.getName());
			}
		}
		// initialize the element number properties of the search
		if (search.getAvailableElementNumbers().isEmpty())
		{
			elementNumberMap = new HashMap<String, Long>();
			for (Element element : (List<Element>) application.get(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN))
			{
				search.getAvailableElementNumbers().add(element.getName() + ' ' + element.getDescription());
				elementNumberMap.put(element.getName() + ' ' + element.getDescription(), element.getId());
			}
		}
		else if (elementNumberMap == null)
		{
			elementNumberMap = new HashMap<String, Long>();
			for (Element element : (List<Element>) application.get(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN))
			{
				elementNumberMap.put(element.getName() + ' ' + element.getDescription(), element.getId());
			}
		}

		if (search.getSelectedElementNumbers() != null && !search.getSelectedElementNumbers().isEmpty())
		{
			Collection<String> elementNumbers = search.getSelectedElementNumbers();
			if (elementNumbers != null && elementNumbers.size() > 0)
			{
				if (search.getSelectedElementNums() == null)
				{
					search.setSelectedElementNums(new ArrayList<Long>());
				}
				for (String elem : elementNumbers)
				{

					search.getSelectedElementNums().add(elementNumberMap.get(elem));

				}
			}
		}
                
                //Get the states with multiselectBox for multiple PL download
                SiteUser siteUser = (SiteUser) session.get("siteUser");
                Map<String,String> statesData = dataExtractionService.getStates(siteUser);
                if (search.getAvailableStates().isEmpty()) {
                    search.setAvailableStates(statesData);
                }	
                search.setSelectedStates(new HashMap<>(0));
                setDoubleStatesList(new HashMap<>(0));
	}

	/**
	 * @return the transmissionService
	 */
	public TransmissionServiceP3 getTransmissionServiceP3()
	{
		return transmissionServiceP3;
	}

	/**
	 * @param transmissionService
	 *           the transmissionService to set
	 */
	public void setTransmissionServiceP3(TransmissionServiceP3 transmissionService)
	{
		this.transmissionServiceP3 = transmissionService;
	}

	/**
	 * @return the previousPenaltyBreakdown
	 */
	public PenaltyBreakdown getPreviousPenaltyBreakdown()
	{
		return previousPenaltyBreakdown;
	}

	/**
	 * @param previousPenaltyBreakdown
	 *           the previousPenaltyBreakdown to set
	 */
	public void setPreviousPenaltyBreakdown(PenaltyBreakdown previousPenaltyBreakdown)
	{
		this.previousPenaltyBreakdown = previousPenaltyBreakdown;
	}

	public boolean hasAccessToGeneratePenaltyLetter() {
		return UserAccessUtil.hasAccessToGenerateLetter(transmissionServiceP3, servletRequest, session);
	}

	public boolean hasAccessToInitialExport() {
		return UserAccessUtil.hasAccessToInitialExport(transmissionServiceP3, servletRequest, session);
	}

	public boolean hasAccessToFinalExport() {
		return UserAccessUtil.hasAccessToFinalExport(transmissionServiceP3, servletRequest, session);
	}

	private interface TagWriteCommand {
		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException;
	}

	private class SmartXmlTagWriter {
		private XMLStreamWriter xmlStrWriter;
		private List<TagWriteCommand> commandCache;
		private String cachedTag;
		private boolean cacheNeedsContent;
		private boolean cacheHasContent;
		private boolean contentPending;

		public SmartXmlTagWriter(XMLStreamWriter xmlStrWriter) {
			this.xmlStrWriter = xmlStrWriter;
		}

		public void writeStartElement(String qName) throws XMLStreamException {
			if (qName.equalsIgnoreCase("w:p")) {
				//cache all writes associated with the 'w:p' tag first
				//to determine if real content exists before the actual write.
				//"real content" is defined as the presence of a 'w:t' tag
				//nested within the 'w:p' and having a non-null/non-blank value.
				//it is also possible for a w:p tag to not have a child 'w:t'
				//at all which is valid.
				cachedTag = qName;
				commandCache = new ArrayList<TagWriteCommand>();
				commandCache.add(new StartTagWriter(qName));
			} else if (cachedTag != null) {
				if (qName.equalsIgnoreCase("w:t")) {
					cacheNeedsContent = true;
					contentPending = true;
				}
				commandCache.add(new StartTagWriter(qName));
			} else {
				xmlStrWriter.writeStartElement(qName);
			}
		}

		public void writeEndElement(String qName) throws XMLStreamException {
			if (qName.equalsIgnoreCase(cachedTag)) {
				//closing the cached tag. determine if real content exists -
				//if yes, write the ENTIRE cache, otherwise delete the cache
				//and ignore the entire cached tag
				if ((cacheNeedsContent && cacheHasContent) || !cacheNeedsContent) {
					commandCache.add(new EndTagWriter());
					writeTagCache();
					clearTagCache();
				} else if (cacheNeedsContent && !cacheHasContent) {
					clearTagCache();
				}
			} else if (cachedTag != null) {
				if (qName.equalsIgnoreCase("w:t")) {
					contentPending = false;
				}
				commandCache.add(new EndTagWriter());
			} else {
				xmlStrWriter.writeEndElement();
			}
		}

		public void writeAttribute(String attrName, String attrVal) throws XMLStreamException {
			if (cachedTag != null) {
				commandCache.add(new TagAttrbWriter(attrName, attrVal));
			} else {
				xmlStrWriter.writeAttribute(attrName, attrVal);
			}
		}

		public void write(String content) throws XMLStreamException {
			if (cachedTag != null) {
				//contentPending = true infers that content is what
				//is contained in the <w:t> tag
				if (contentPending && StringUtils.isNotBlank(content)) {
					cacheHasContent = true;
				}
				commandCache.add(new TagContentWriter(content));
			} else {
				xmlStrWriter.writeCharacters(content);
			}
		}

		private void writeTagCache() throws XMLStreamException {
			for (TagWriteCommand twc : commandCache) {
				twc.write(xmlStrWriter);
			}
		}

		private void clearTagCache() {
			commandCache.clear();
			cachedTag = null;
			cacheNeedsContent = false;
			cacheHasContent = false;
		}
	}

	private class StartTagWriter implements TagWriteCommand {
		private String qName;

		public StartTagWriter(String qName) {
			this.qName = qName;
		}

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeStartElement(qName);
		}
	}

	private class EndTagWriter implements TagWriteCommand {

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeEndElement();
		}
	}

	private class TagAttrbWriter implements TagWriteCommand {
		private String attrbName;
		private String attrbValue;

		public TagAttrbWriter(String attrbName, String attrbValue) {
			this.attrbName = attrbName;
			this.attrbValue = attrbValue;
		}

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeAttribute(attrbName, attrbValue);
		}
	}

	private class TagContentWriter implements TagWriteCommand {
		private String content;

		public TagContentWriter(String content) {
			this.content = content;
		}

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeCharacters(content);
		}
	}
        
    /**
     * This method invoke a method to add necessary parameters to session 
     * for generate penalty letters action
     */
    public final String doGeneratePenaltyLetters() {
        addGeneratePLParamsToSession();

        return SUCCESS;
    }

    /**
     * This method control an action to search multiple penalty letters to generate
     */
    public String searchGeneratePenaltiesLetters() {
        log.info("inside PenaltyAction >>> searchGeneratePenaltiesLetters");
        initSearch();
        checkSelectedReportingPeriodsForMalformedString();
        rebuildAvailableReportingPeriodsList();
        if(servletRequest.getParameterValues(STATES) != null) {
            String [] stts =  servletRequest.getParameterValues(STATES);
            for(int i=0 ; i< stts.length;i++) {
                search.getSelectedStates().put(stts[i], search.getAvailableStates().get(stts[i]));
                doubleStatesList.put(stts[i], search.getAvailableStates().get(stts[i]));
                search.getAvailableStates().remove(stts[i]);
            }
        }
        // disable pagination
        search.setPageSize(0);
        if(!defaultPage) {
            // return active plus most recent inactive regular submission as a default.
            penaltyLetterService.searchGeneratePenaltyLetters(search);
        }

        return Action.SUCCESS;
    }

    /**
    * Clear the search filter for multiple penalty letters to generate
    */
    public String clearGeneratePenaltiesLettersSearch() {
        log.info("inside PenaltyAction >>> clearGeneratePenaltiesLettersSearch");
        search.reset();
        initSearch();

        return Action.SUCCESS;
    }
    
    /**
     * Stores generate penalty letter job parameters in a HashMap and puts it into the user
     * session
     * 
     * This method is necessary because an actionRedirect causes generate PL
     * parameters on the initial form submit to be lost. This method is used in
     * conjunction with getGeneratePLParamsFromSession(), which should be called in
     * the redirected action.
     */
    public void addGeneratePLParamsToSession() {
        HashMap<String, Object> map = new HashMap<>();
        String[] selectedInitialLetters = servletRequest.getParameterValues("select_il");
        if (selectedInitialLetters != null) {
            initialTransmissionIdList.addAll(Arrays.asList(selectedInitialLetters));
        }
        String[] selectedFinalLetters = servletRequest.getParameterValues("select_fl");
        if (selectedFinalLetters != null) {
            finalTransmissionIdList.addAll(Arrays.asList(selectedFinalLetters));
        }
        map.put("userFilename", getUserFilename());
        map.put(INITIAL_TRANSMISSION_ID_LIST, initialTransmissionIdList);
        map.put(FINAL_TRANSMISSION_ID_LIST, finalTransmissionIdList);
        session.put("generatePLParameters", map);
    }

    /**
     * Starts a quartz job that generates penalty letter, including only the selected
     * fields.
     * 
     * This method does not attempt to set the field selection to default.
     * 
     * @return Action.SUCCESS on success; Action.ERROR otherwise
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    public final String batchGeneratePenaltyLetters() {
        getGeneratePLParamsFromSession();
        Scheduler scheduler = (Scheduler) applicationContext.getBean("nytdScheduler");
        try {
            if (!scheduler.isStarted()) {
                    scheduler.start();
            }

            // create export file name
            String fileFormat = "zip";
            StringBuilder efn = new StringBuilder();
            SiteUser siteUser = (SiteUser) session.get("siteUser");
            Date date = new Date();
            if(userFilename != null && !userFilename.trim().isEmpty()) {
                efn.append(userFilename.trim().replace(" ", "")).append(".").append(fileFormat);
            } else {
                efn.append(siteUser.getUserName()).append("_penalty_letters_").append(date.getTime())
                        .append(".").append(fileFormat);
            }
            exportFilename = efn.toString();

            // get quartz job bean
            JobDetail generatePLJobDetail = (JobDetail) applicationContext.getBean("generatePenaltyLettersJob");
            generatePLJobDetail.isDurable();
            JobKey jobKey = generatePLJobDetail.getKey();
            log.debug("exportFilename: " + exportFilename);

            // prepare quartz job parameters
            JobDataMap dataMap = generatePLJobDetail.getJobDataMap();

            log.debug("initialTransmissionIdList: "+initialTransmissionIdList);
            log.debug("finalTransmissionIdList: "+finalTransmissionIdList);
            
            // build metadata with lists of selected reporting periods and states
            List<String> combinedList = Stream.of(initialTransmissionIdList, finalTransmissionIdList)
                                        .flatMap(x -> x.stream())
                                        .collect(Collectors.toList());
            for (String transmissionId : combinedList) {
                Transmission trans = transmissionServiceP3.getTransmission(Long.valueOf(transmissionId));
                String transReportPeriodName = trans.getReportingPeriod().getName();
                String transStateName = trans.getState().getStateName();
                if (!selectedReportPeriodsList.contains(transReportPeriodName)) {
                    selectedReportPeriodsList.add(transReportPeriodName);
                }
                if (!selectedStatesList.contains(transStateName)) {
                    selectedStatesList.add(transStateName);
                }
            }
            String selectedRportPeriods = 
                    String.join(", ", selectedReportPeriodsList.stream().sorted().collect(Collectors.toList()));
            String selectedStates = 
                    String.join(", ", selectedStatesList.stream().sorted().collect(Collectors.toList()));
            dataMap.put("reportingPeriods", selectedRportPeriods);
            dataMap.put("states", selectedStates);
            dataMap.put("fileName", exportFilename);
            dataMap.put("siteUser", session.get("siteUser"));
            dataMap.put(INITIAL_TRANSMISSION_ID_LIST, initialTransmissionIdList);
            dataMap.put(FINAL_TRANSMISSION_ID_LIST, finalTransmissionIdList);
            StringBuilder hostAndPort = new StringBuilder();
            
            // download penalty letters zip
            hostAndPort.append("http://").append(servletRequest.getServerName()).append(":").append(
                    servletRequest.getServerPort()).append(servletRequest.getContextPath()).append(
                    "/downloadPenaltyLettersZip.action?downloadFilename=");
            log.debug("Download URI : " + hostAndPort);
            dataMap.put("hostAndPort", hostAndPort.toString());

            // add job to scheduler
            scheduler.addJob(generatePLJobDetail, true);
            log.debug("trigger Generate Penalty Letter job");
            scheduler.triggerJob(jobKey, dataMap);
            log.debug("Generate Penalty Letter job triggered");

        } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                return Action.ERROR;
        } catch (Exception ex ) {
            log.error(ex.getMessage(), ex);
            return Action.ERROR;
        }

        return Action.SUCCESS;
    }

    /**
     * Retrieves generatePL job parameters from user session
     * 
     * This method is necessary because an actionRedirect causes generatePL
     * parameters on the initial form submit to be lost. This method is used in
     * conjunction with addGeneratePLParamsToSession(), which should be called before
     * actionRedirect.
     * 
     */
    @SuppressWarnings("unchecked")
    public void getGeneratePLParamsFromSession() {
        HashMap<String, Object> map = (HashMap<String, Object>) session.get("generatePLParameters");
        if (map != null) {
            setUserFilename((String)map.get("userFilename"));
            setExportFilename((String)map.get("fileName"));
            setInitialTransmissionIdList((List<String>)map.get(INITIAL_TRANSMISSION_ID_LIST));
            setFinalTransmissionIdList((List<String>)map.get(FINAL_TRANSMISSION_ID_LIST));
        }
    }
    
    /**
    * Downloads a zip file of penalty letters that exists on the file system.
    * 
    * @return Action.SUCCESS on success; Action.ERROR otherwise
    */
    @SkipValidation
    public String downloadPenaltyLettersZip() {
        String downloadFileName = getServletRequest().getParameter("downloadFilename");
            setExportFilename(downloadFileName);
            String fullFileUrl = exportLocation + downloadFileName;
            try {
                File fileToDownload = new File(new URI(fullFileUrl));
                if (!fileToDownload.exists()) {
                    log.error("Error: file does not exist - URI: " + fileToDownload );
                    return Action.ERROR;
                }
                inputStream = new FileInputStream(fileToDownload);
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return Action.ERROR;
            }

            contentType = "application/zip";

        return Action.SUCCESS;
   }

    /**
    * Check if user can generate initial penalty letter
    * @param transmissionId as String
    * @return boolean value
    */
    public boolean canGenerateInitialLetter(String transmissionId) {
        SiteUser siteUser = (SiteUser) session.get(Constants.SITE_USER);
        return PenaltyLetterUtil.canGenerateInitialLetter(transmissionId, siteUser, transmissionServiceP3);
    }

    /**
    * Check if user can generate final penalty letter
    * @param transmissionId as String
    * @return boolean value
    */
    public boolean canGenerateFinalLetter(String transmissionId) {
        SiteUser siteUser = (SiteUser) session.get(Constants.SITE_USER);
        return PenaltyLetterUtil.canGenerateFinalLetter(transmissionId, siteUser, transmissionServiceP3);
    }
    
    // Handler to read serializable objects (i.e.) session, request etc.
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        log.info("reading serialization object");
        in.defaultReadObject();
    }

    // Handler to write serializable objects (i.e.) session, request etc.
    private void writeObject(ObjectOutputStream out) throws IOException {
        log.info("writing serialization object");
        out.defaultWriteObject();
    }

}
