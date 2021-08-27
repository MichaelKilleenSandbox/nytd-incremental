package gov.hhs.acf.cb.nytd.service.impl;
// TODO: Mutsuo 7/30/2021 SonarQube Duplicated lines here are parseAndwritePenaltyLetter() left in action class 
//       which works only with a single PL generation with using a single transmissio id from request. Use one 
//       in this service method will eliminate most (if not all) duplicates.
// TODO: Mutsuo 7/30/2021 SonarQube  re-write IndentingXMLStreamWriter without using sun package or false positive?
//       ref 1 - Rewite: https://stackoverflow.com/questions/7153221/alternative-to-indentingxmlstreamwriter-java
//       ref 2 - False Positive: https://stackoverflow.com/questions/43869567/sonarqube-rule-classes-from-com-sun-and-sun-packages-should-not-be-used

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import gov.hhs.acf.cb.nytd.actions.penalty.PenaltyBreakdown;
import gov.hhs.acf.cb.nytd.actions.penalty.PenaltySearch;
import gov.hhs.acf.cb.nytd.dao.PenaltyLetterDAO;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import gov.hhs.acf.cb.nytd.service.ContactDTO;
import gov.hhs.acf.cb.nytd.service.PenaltyLetterService;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.ContactUtil;
import gov.hhs.acf.cb.nytd.util.PenaltyLetterUtil;
import gov.hhs.acf.cb.nytd.webservice.StateRegionContactInfoResponse;
import gov.hhs.acf.cb.nytd.webservice.client.MLSServiceClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implements PenaltyService.
 * 
 * @see UserService
 */
public class PenaltyLetterServiceImpl extends BaseServiceImpl implements PenaltyLetterService {

    // constants
    private static final String STATE_START_TAG = "<state>";
    private static final String STATE_END_TAG = "</state>";
    private static final String PENALTY_LETTER_ENCODING = "UTF-8";
    private static final String CORRECTED_END_REPORT_PERIOD = "^Corrected End Report Period^";
    
    // daos
    @Getter @Setter private StateDAO stateDAO;
    @Getter @Setter private PenaltyLetterDAO penaltyLetterDAO;

    // services
    @Getter @Setter private TransmissionServiceP3 transmissionServiceP3;
    @Getter @Setter private ComplianceService complianceService;

    /*
     * variable to store the node value of the xml response
     */
    String tempBuffer = "";
    /*
     * Webservice client object
     */
    MLSServiceClient mlsServiceClient;
    /*
     * name of the placeholder which is being parsed
     */
    String placeholderName = "";
    /*
     * Map to store the state/region contact information from the webservice
     * response
     */
    Map<String, String> contactInfoMap;

    private Map<String, String> stateAbbreviationMap;

    @Getter @Setter private int templateNumber = 0;
    @Getter @Setter private int outcomeAge;
    @Getter @Setter private String transmissionType;
    @Getter @Setter private PenaltyBreakdown penaltyBreakdown;
    @Getter @Setter private PenaltyBreakdown previousPenaltyBreakdown;
    private boolean skipElement = false;
    private boolean detailedList = false;
    private boolean showElements = false;

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.hhs.acf.cb.nytd.service.PenaltyLetterService#getStateRegionContactInfoMap
     * ()
     */
    @Override
    public Map<String, String> getStateRegionContactInfoMap(String stateName, String regionName,String webServiceURL) 
            throws ParserConfigurationException, SAXException {
        mlsServiceClient = new MLSServiceClient();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        // make the call to the webservice
        StateRegionContactInfoResponse srcInfoRes = mlsServiceClient.getStateRegionContactService(
                stateName, PenaltyLetterUtil.getRegions().get(regionName), webServiceURL);
        if(log.isDebugEnabled()) {
            log.debug(srcInfoRes.toString());
        }
        contactInfoMap = createStateRegionContactInfoMap(saxParser, srcInfoRes);
        contactInfoMap.put("gmo.name", ContactUtil.getFormattedName(contactInfoMap, "gmo."));

        return contactInfoMap;
    }

    /**
     * Creates State and Region information map from the webservice response. For
     * state information "state." prefix is added to the name of element node of
     * the xml. For eg. state.firstname For region information "region." prefix
     * is added to the name of element node of the xml. For eg. region.firstname.
     * The Map will then contain
     * "state/region.(element name),value of the xml node" as key value pairs.
     * 
     * @param saxParser
     * @param srcInfoRes
     * @return Map of state/region contact info
     */
    // TODO: Mutsuo 8/6/2021 SonarQube - Refactor this method to reduce its Cognitive Complexity from 49 to the 15 allowed.
    private Map<String, String> createStateRegionContactInfoMap(
            SAXParser saxParser, StateRegionContactInfoResponse srcInfoRes) {
            contactInfoMap = new HashMap<>();

            DefaultHandler handler = new DefaultHandler()
            {
                    // indicator flags which are turned on when the parser sees any
                    // state/region information in the xml
                    boolean stateInfo = false;
                    boolean regionInfo = false;
                    boolean gmoInfo = false;
                    boolean nytdInfo = false;
                    ContactDTO tempContact;

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
                     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
                     */
                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes)
                                    throws SAXException
                    {
                            if (StringUtils.equals(qName, "statecontact"))
                            {
                                    stateInfo = true;
                            }
                            else if (StringUtils.equals(qName, "regionalprogrammanagercontact"))	
                            {
                                    regionInfo = true;
                            }
                            else if(StringUtils.equals(qName, "regionalgrantofficercontact"))
                            {
                                    gmoInfo = true;
                            }
                            else if(StringUtils.equals(qName, Constants.MLS_QNAME_NYTD_CONTACT))
                            {
                                    nytdInfo = true;
                            }
                            placeholderName = qName;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
                     * java.lang.String, java.lang.String)
                     */
                    @Override
                    public void endElement(String uri, String localName, String qName) throws SAXException
                    {
                            if (StringUtils.equals(qName, "statecontact"))
                            {
                                    stateInfo = false;
                            }
                            else if (StringUtils.equals(qName, "regionalprogrammanagercontact"))	
                            {

                                    regionInfo = false;
                            }
                            else if(StringUtils.equals(qName, "regionalgrantofficercontact"))
                            {
                                    gmoInfo = false;
                            }
                            else if (StringUtils.equals(qName, Constants.MLS_QNAME_NYTD_CONTACT)) {
                                    nytdInfo = false;
                            }
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
                     */
                    @Override
                    public void characters(char[] ch, int start, int length) throws SAXException
                    {
                            tempBuffer = StringUtils.trim(new String(ch, start, length));
                            tempBuffer = StringUtils.replace(tempBuffer, Constants.STR_NULL, StringUtils.EMPTY);
                            super.characters(ch, start, length);
                            // if the parser encounters state information in the xml
                            if (stateInfo && !regionInfo && !gmoInfo)
                            {
                                    if (StringUtils.isNotBlank(tempBuffer))
                                    {
                                            contactInfoMap.put("state." + placeholderName, contactInfoMap.getOrDefault(
                                                            "state." + placeholderName, StringUtils.EMPTY) + tempBuffer);
                                    }
                            }
                            // if the parser encounters region information in the xml
                            else if (!stateInfo && regionInfo  && !gmoInfo)
                            {
                                    if (StringUtils.isNotBlank(tempBuffer))
                                    {
                                            contactInfoMap.put("region." + placeholderName, contactInfoMap.getOrDefault(
                                                            "region." + placeholderName, StringUtils.EMPTY) + tempBuffer);
                                    }
                            }
                            // if the parser encounters regional grant officer information in the xml
                            else if (!stateInfo && !regionInfo  && gmoInfo)
                            {
                                    if (StringUtils.isNotBlank(tempBuffer))
                                    {
                                            contactInfoMap.put("gmo." + placeholderName, contactInfoMap.getOrDefault(
                                                            "gmo." + placeholderName, StringUtils.EMPTY) + tempBuffer);
                                    }
                            }
                            else if (nytdInfo) {
                                    parseContactTag(placeholderName, StringUtils.stripToEmpty(tempBuffer));
                            }
                    }

                    /**
                     * Loads tag data into target field of temp contact object
                     * @param qName - Qualified Tag Name
                     * @param value - String value
                     */
                    private void parseContactTag(String qName, String value) {
                            switch (StringUtils.lowerCase(qName)) {
                                    case Constants.MLS_QNAME_ID:
                                            tempContact = new ContactDTO();
                                            tempContact.setId(value);
                                            break;
                                    case Constants.MLS_QNAME_FIRSTNAME:
                                            tempContact.setFirstname(value);
                                            break;
                                    case Constants.MLS_QNAME_MIDDLEINITIAL:
                                            tempContact.setMiddleinitial(value);
                                            break;
                                    case Constants.MLS_QNAME_LASTNAME:
                                            tempContact.setLastname(value);
                                            break;
                                    case Constants.MLS_QNAME_PREFIX:
                                            tempContact.setPrefix(value);
                                            break;
                                    case Constants.MLS_QNAME_SUFFIX:
                                            tempContact.setSuffix(value);
                                            break;
                                    case Constants.MLS_QNAME_AGENCY:
                                            tempContact.setAgency(value);
                                            break;
                                    case Constants.MLS_QNAME_DIVISION:
                                            tempContact.setDivision(value);
                                            break;
                                    case Constants.MLS_QNAME_TITLE:
                                            tempContact.setTitle(value);
                                            break;
                                    case Constants.MLS_QNAME_PHONE:
                                            tempContact.setPhone(value);
                                            break;
                                    case Constants.MLS_QNAME_FAX:
                                            tempContact.setFax(value);
                                            break;
                                    case Constants.MLS_QNAME_EMAIL:
                                            tempContact.setEmail(value);
                                            break;
                                    case Constants.MLS_QNAME_ADDRESS1:
                                            tempContact.setAddress1(value);
                                            break;
                                    case Constants.MLS_QNAME_ADDRESS2:
                                            tempContact.setAddress2(value);
                                            break;
                                    case Constants.MLS_QNAME_CITY:
                                            tempContact.setCity(value);
                                            break;
                                    case Constants.MLS_QNAME_STATE:
                                            tempContact.setState(getTwoLetterStateAbbreviation(value));
                                            break;
                                    case Constants.MLS_QNAME_ZIPCODE:
                                            tempContact.setZipcode(value);
                                            break;
                                    case Constants.MLS_QNAME_COUNTRY:
                                            tempContact.setCountry(value);
                                            addContactToMap(tempContact);
                                            break;
                                    default:
                                            log.warn("Unknown tag: " + qName + " Value: " + value);
                            }
                    }

                    public String getTwoLetterStateAbbreviation(@NonNull String state) {
                            if (stateAbbreviationMap == null) {
                                    stateAbbreviationMap = stateDAO.getStateAbbrevMap();
                            }
                            if (state.length() == 2) {
                                    return state;
                            }
                            else {
                                    return stateAbbreviationMap.get(state);
                            }
                    }

                    /**
                     * Adds contact data to target fields in contact map
                     * @param contact - contact data object
                     */
                    private void addContactToMap(ContactDTO contact) {
                            String tagPrefix = getContactTagPrefix(contact.getTitle());
                            if (StringUtils.isNotBlank(tagPrefix)) {
                                    addFieldToContactMap(Constants.MLS_QNAME_ID, tagPrefix, contact.getId());
                                    addFieldToContactMap(Constants.MLS_QNAME_NAME, tagPrefix, contact.getFormattedName());
                                    addFieldToContactMap(Constants.MLS_QNAME_SUFFIX, tagPrefix, contact.getSuffix());
                                    addFieldToContactMap(Constants.MLS_QNAME_PREFIX, tagPrefix, contact.getPrefix());
                                    addFieldToContactMap(Constants.MLS_QNAME_AGENCY, tagPrefix, contact.getAgency());
                                    addFieldToContactMap(Constants.MLS_QNAME_DIVISION, tagPrefix, contact.getDivision());
                                    addFieldToContactMap(Constants.MLS_QNAME_TITLE, tagPrefix, contact.getTitle());
                                    addFieldToContactMap(Constants.MLS_QNAME_PHONE, tagPrefix, contact.getPhone());
                                    addFieldToContactMap(Constants.MLS_QNAME_FAX, tagPrefix, contact.getFax());
                                    addFieldToContactMap(Constants.MLS_QNAME_EMAIL, tagPrefix, contact.getEmail());
                                    addFieldToContactMap(Constants.MLS_QNAME_ADDRESS1, tagPrefix, contact.getAddress1());
                                    addFieldToContactMap(Constants.MLS_QNAME_ADDRESS2, tagPrefix, contact.getAddress2());
                                    addFieldToContactMap(Constants.MLS_QNAME_CITY, tagPrefix, contact.getCity());
                                    addFieldToContactMap(Constants.MLS_QNAME_STATE, tagPrefix, contact.getState());
                                    addFieldToContactMap(Constants.MLS_QNAME_ZIPCODE, tagPrefix, contact.getZipcode());
                                    addFieldToContactMap(Constants.MLS_QNAME_COUNTRY, tagPrefix, contact.getCountry());
                            }
                            else {
                                    log.warn("Title not recognized. Contact will not be added. " + contact.toString());
                            }
                    }

                    /**
                     * Adds target field to contact map with
                     * @param field - field name
                     * @param prefix - key prefix for each field
                     * @param value - field value
                     */
                    private void addFieldToContactMap(String field, String prefix, String value) {
                            if (StringUtils.isNotBlank(value)) {
                                    contactInfoMap.put(prefix + field, value);
                            }
                    }

                    /**
                     * Returns tag prefix based on title
                     * @param title - contact title
                     * @return - tag prefix
                     */
                    private String getContactTagPrefix(String title) {
                            String tagPrefix = "";
                            switch (title) {
                                    case Constants.MLS_COMMISSIONER_TITLE:
                                            tagPrefix = Constants.COMMISSIONER_TAG_PREFIX;
                                            break;
                                    case Constants.MLS_FINANCIAL_SPECIALIST_TITLE:
                                            tagPrefix = Constants.FINANCIAL_MANAGER_TAG_PREFIX;
                                            break;
                                    case Constants.MLS_GRANT_SPECIALIST_TITLE:
                                            tagPrefix = Constants.GRANT_MANAGER_TAG_PREFIX;
                                            break;
                                    default:
                                            log.warn("Unknown contact title: " + title);
                            }
                            return tagPrefix + ".";
                    }
            };  // end - DefaultHandler


            try
            {
                    String correctedXml = Constants.MLS_WEBSERVICE_RESPONSE_DEFAULT;

                    if (srcInfoRes.get_return() != null)
                    {
                            // escape the ampersand
                            correctedXml = StringUtils.replace(srcInfoRes.get_return(), "&", "&amp;");

                            // NYTD-45 replace abbreviation for state name (TODO: better to have MLS webservice returns abbreviation...tweaking for now)
                            String stateContactInfo = correctedXml.substring(0, correctedXml.indexOf("</statecontact>")+15);
                            String stateNameWithTagOriginal = stateContactInfo.substring(stateContactInfo.indexOf(STATE_START_TAG), stateContactInfo.indexOf(STATE_END_TAG));
                            String stateName = stateContactInfo.substring(stateContactInfo.indexOf(STATE_START_TAG)+7, stateContactInfo.indexOf(STATE_END_TAG));
                            if (StringUtils.isNotEmpty(stateName) && stateDAO!=null 
                                            && StringUtils.isNotEmpty(stateDAO.getStateAbbr(stateName))){
                                    String stateNameWithTagModified = StringUtils.replace(stateNameWithTagOriginal, stateName, stateDAO.getStateAbbr(stateName));					
                                    stateContactInfo = StringUtils.replace(stateContactInfo, stateNameWithTagOriginal, stateNameWithTagModified);
                            }

                            // NYTD-45 replace abbreviation for state name in regional contact info as well.
                            String regionalContactInfo = correctedXml.substring(correctedXml.indexOf("<regionalprogrammanagercontact>"), correctedXml.length());
                            String regionStateName = regionalContactInfo.substring(regionalContactInfo.indexOf(STATE_START_TAG)+7, regionalContactInfo.indexOf(STATE_END_TAG));

                            if (StringUtils.isNotEmpty(regionStateName) && stateDAO!=null 
                                            && StringUtils.isNotEmpty(stateDAO.getStateAbbr(regionStateName)) ){
                                    regionalContactInfo = StringUtils.replace(regionalContactInfo, regionStateName, stateDAO.getStateAbbr(regionStateName));
                            }

                            correctedXml = stateContactInfo + regionalContactInfo;

                    }

                    log.info("*************************************");
                    log.info(correctedXml);
                    log.info("*************************************");
                    saxParser.parse(new InputSource(new StringReader(correctedXml)), handler);

            }
            catch (IOException | SAXException e) {
                    log.error(e.getMessage(), e);
            }
            return contactInfoMap;
    }
        
    /*
     * @see
     * gov.hhs.acf.cb.nytd.service.PenaltyLetterService#parseAndWritePenaltyLetter(Long, String)
     */
    //TODO: 7/15/2021 - cognitive complexity: separate this as a class?!(e.g.)PenaltyLetterSaxHandler extends DefaultHandler
    @Override
    public String parseAndWritePenaltyLetter(Long transmissionId, String letterType) throws IOException, SAXException,
                    ParserConfigurationException, XMLStreamException, FactoryConfigurationError {
        final Properties properties = new Properties();
        PenaltyLetterUtil penaltyLetterUtil = new PenaltyLetterUtil();
        Double penaltyAmt = 0.00;
        // Load the properties file
        try {
            properties.load(this.getClass().getResourceAsStream("/config/systemConfig.properties"));
        } catch (IOException e) {
                log.error(e.getMessage(), e);
        }
        log.info("transmissionId: " + transmissionId);
        Transmission trans = transmissionServiceP3.getTransmission(transmissionId);
        String transTypeName = trans.getTransmissionType().getName();
        String transReportPeriodName = trans.getReportingPeriod().getName();
        // select the penalty letter template based on the penalty amount and the transmission type
        String penaltyLttrTemplate = penaltyLetterUtil.getPenaltyLetterTemplate(penaltyAmt, properties, transTypeName, letterType);
        this.setTemplateNumber(penaltyLetterUtil.getPenaltyLetterTemplateNumber(penaltyAmt, transTypeName, letterType));
        this.setTransmissionType(transTypeName);

        // create the name of the file format <state abbreviation>_NYTD_<report period>_<Initial/Final>_Compliance_<date(MMddYYYY)>
        // eg. VA_NYTD_2013A_Initial_Compliance_04042013.doc
        String fileName = trans.getState().getAbbreviation()+"_NYTD_"
                +transReportPeriodName+"_"+letterType+"_Compliance_"+trans.getId()+"_"+penaltyLetterUtil.getDate();
        log.info("fileName: " + fileName);
           
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        // SonarQube security fix: below 2 lines prevent XML parsers being vulnerable to XXE attacks
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        String penaltyLettersLocation = properties.getProperty("systemConfig.penaltyLettersLocation");
        OutputStream outputStream = null;        
        try {
             outputStream  = new FileOutputStream(new File(new URI(penaltyLettersLocation + fileName + ".doc")));
        
        String stateName = trans.getState().getStateName();
        String regionName = trans.getState().getRegion().getRegion();
        if(Constants.DISTRICT_OF_COLUMBIA.equals(stateName)) {
            stateName = "DC";
        }
        
        final XMLStreamWriter xmlStrWriter = new IndentingXMLStreamWriter(
                XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));   
        final PenaltyLetterUtil.SmartXmlTagWriter smartXmlTagWriter = penaltyLetterUtil.new SmartXmlTagWriter(xmlStrWriter);
        contactInfoMap = this.getStateRegionContactInfoMap(stateName, regionName, properties.getProperty("penaltyLetter.webservice.url"));
        contactInfoMap.put("region.id", PenaltyLetterUtil.getRegionIDs().get(regionName));
        contactInfoMap.put("state.stateName",stateName);
        if (log.isDebugEnabled()) {
                contactInfoMap.forEach((k,v) -> log.debug("ContactInfoMap K: " + k + " V: " + v));
        }

        InputStream inputStream = getClass().getResourceAsStream(penaltyLttrTemplate);
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        InputSource is = new InputSource(reader);
        is.setEncoding(PENALTY_LETTER_ENCODING);

        DefaultHandler handler = new DefaultHandler() {

            // Setting the encoding,version and processing instruction to open the docx file in word
            @Override
            public void startDocument() throws SAXException {
                try {
                    xmlStrWriter.writeStartDocument(PENALTY_LETTER_ENCODING, "1.0");
                    xmlStrWriter.writeProcessingInstruction("mso-application progid=\"Word.Document\"");
                } catch (XMLStreamException e) {
                        log.error(e.getMessage(), e);
                }
            }

            @Override
            public void endDocument() throws SAXException {
                try {
                    xmlStrWriter.writeEndDocument();
                    xmlStrWriter.close();
                } catch (XMLStreamException e) {
                    log.error(e.getMessage(), e);
                }
            }

            // Flush out the characters in the new docx file
            @Override
            public void characters(char[] buffer, int start, int length) {
                boolean bullets = false;
                boolean suggestions = false;
                try {
                    tempBuffer = new String(buffer, start, length);
                    StringBuilder sBldr = new StringBuilder(tempBuffer);
                    String startDelim = properties.getProperty("penaltyLetter.template.placeholder.start");
                    String endDelim = properties.getProperty("penaltyLetter.template.placeholder.end");
                    String dateDelim = properties.getProperty("penaltyLetter.template.placeholder.date.delim");
                    // start finding the placeholder
                    if (StringUtils.contains(sBldr.toString(), startDelim)) {
                        // counter to check if there are more than one placeholders in one line for eg. City, State, Zip
                        String placeholder = StringUtils.substring(
                                sBldr.toString(), sBldr.toString().indexOf(startDelim) + 1, sBldr.toString().indexOf(endDelim));
                        StringBuilder updatedString = new StringBuilder();
                        if (placeholder.contains(",")) {
                            String[] placeholderArray = StringUtils.split(placeholder, ",");
                            for (String token : placeholderArray) {
                                if (sBldr.toString().contains(token)) {
                                    if (token.contains("city")) {
                                            updatedString.append(contactInfoMap.get(token.trim())).append(", ");
                                    } else {
                                            updatedString.append(contactInfoMap.get(token.trim())).append(" ");
                                    }
                                }
                            }
                            sBldr = new StringBuilder(updatedString.toString());
                        }  else {
                            if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_PREVIOUS)
                                    || StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_CURRENT)
                                    || StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_SUGGESTIONS)) {
                                bullets = true;
                                if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_PREVIOUS)) {
                                    // build the Previous penalty breakdown object
                                    detailedList = false;
                                    suggestions = false;
                                    buildPreviousPenaltyBreakdown(trans.getState().getId(), trans.getReportingPeriod().getId());
                                } else if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_CURRENT)) {
                                    // create the current penalty object and the detailed
                                    // items along with it
                                    detailedList = true;
                                    suggestions = false;
                                    buildPenaltyBreakdown(transmissionId);
                                } else if (StringUtils.contains(placeholder, Constants.BULLET_PLACEHOLDER_SUGGESTIONS)) {
                                    suggestions = true;
                                    detailedList = false;
                                    buildPenaltyBreakdown(transmissionId);
                                }
                            } else if (StringUtils.contains(placeholder, Constants.DOLLAR_AMOUNT_PLACEHOLDER)) {
                                // replace the amount field with $$$$$
                                sBldr = new StringBuilder("$$$$$");
                            } else if (StringUtils.contains(placeholder, Constants.PERCENTAGE_FINE_PLACEHOLDER)) {
                                sBldr = new StringBuilder(trans.getPotentialPenalty() + "%");
                            } else if (StringUtils.contains(placeholder, Constants.PERCENTAGE_FINE_PLACEHOLDER_REGULAR)) {
                                sBldr = new StringBuilder(transmissionServiceP3.getPenaltyAmtForInactiveRegularFile(
                                        trans.getId(),trans.getState().getId(),trans.getReportingPeriod().getId()) + "%");
                            } else if (StringUtils.contains(placeholder, Constants.REPORT_PERIOD_PLACEHOLDER)) {
                                sBldr = new StringBuilder(transReportPeriodName);
                            } else {
                                if (contactInfoMap.get(placeholder) != null && !"null".equalsIgnoreCase(contactInfoMap.get(placeholder))) {
                                    String finalText = sBldr
                                    .toString()
                                    .replace(placeholder,contactInfoMap.get(placeholder));
                                    finalText = StringUtils.remove(finalText,
                                                    startDelim);
                                    finalText = StringUtils.remove(finalText,
                                                    endDelim);
                                    sBldr = new StringBuilder(finalText);
                                } else if (!placeholder.equalsIgnoreCase("Date")) {
                                        if (StringUtils.contains(placeholder, Constants.SALUTATION_PREFIX_PLACEHOLDER)) {
                                                sBldr = new StringBuilder("<<No salutation/prefix for contact found in MLS>>");
                                        } else {
                                                //no value exists for the placeholder
                                                sBldr = new StringBuilder();
                                        }
                                }
                            }
                        }
                    } else if (StringUtils.contains(sBldr.toString(), dateDelim)) {
                        // insert year in the date
                        String updatedString = sBldr.toString();
                        if(StringUtils.contains(updatedString,"^End Report Period^")) {
                            sBldr = new StringBuilder(StringUtils.replace(
                                    updatedString, "^End Report Period^", trans.getReportingPeriod().getEndRptDateStr()));
                        } else if(StringUtils.contains(updatedString,CORRECTED_END_REPORT_PERIOD)) {
                            sBldr = new StringBuilder(StringUtils.replace(
                                    updatedString, CORRECTED_END_REPORT_PERIOD, trans.getReportingPeriod().getCorrectedFileEndRptDateStr()));
                        }

                    }
                    // if the parser encounters the bullet placeholder
                    if (bullets) {
                        List<String> bulletList = new ArrayList<>();
                        List<String> bulletListSuggestions = new ArrayList<>();
                        boolean isSampled = false;
                        if(outcomeAge == 21) {
                            isSampled = true;
                        }
                        if(suggestions) {
                            // display File submission standard suggestions
                            isSampled = true;
                            if (penaltyBreakdown.getFileSubmissionDesc() != null) {
                                if(penaltyBreakdown.getFileSubmissionDesc().get(Constants.ERROR_FREE_INFO_LC) != null) {
                                    bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("File Error free Information"));
                                }
                                //START: Task#26 -Additional updates requested by Miguel- See email with subject -Letter Missing Information
                                if(penaltyBreakdown.getFileSubmissionDesc().get(Constants.TIMELY_DATA) != null 
                                        || penaltyBreakdown.getFileSubmissionDesc().get(Constants.FILE_FORMAT) != null) {
                                    StringBuilder timelyDataComplianceStmtStrBuilder =new StringBuilder();
                                    String timelyDataComplianceStatement =PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(
                                            "File Submission Standard Timely Data OR File Format");
                                    if(StringUtils.contains(timelyDataComplianceStatement,CORRECTED_END_REPORT_PERIOD)){
                                        timelyDataComplianceStmtStrBuilder = new StringBuilder(StringUtils.replace(
                                                timelyDataComplianceStatement, CORRECTED_END_REPORT_PERIOD, trans.getReportingPeriod().getCorrectedFileEndRptDateStr()));
                                    }
                                    bulletListSuggestions.add(timelyDataComplianceStmtStrBuilder.toString());
                                }
                            }

                            // display data submission standard suggestions
                            if (penaltyBreakdown.getDataStandardDesc() != null) {
                                if(penaltyBreakdown.getDataStandardDesc().get(Constants.ERROR_FREE_INFO_LC) != null) {
                                    bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(
                                            "Data Error free Information"));
                                }
                                if(penaltyBreakdown.getDataStandardDesc().get(Constants.UNIVERSE) != null) {
                                    if(!isSampled) {
                                        bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.UNIVERSE));
                                    } else {
                                        bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Universe_SAMPLED"));
                                    }
                                }
                                //Prashanth Task#26 -Removing the code to check null on outcomes universe
                                if((penaltyBreakdown.getDataStandardDesc().get(Constants.FOSTER_CARE_PARTICIPATION) != null )
                                                && (penaltyBreakdown.getDataStandardDesc().get(Constants.DISCHARGED_PARTICIPATION) != null )) {
                                    if(!isSampled) {
                                        bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Foster Care Youth 2"));
                                        bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.DISCHARGED_PARTICIPATION));
                                    } else {
                                        bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Foster Care Youth_SAMPLED 2"));
                                        bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Discharged Youth_SAMPLED"));
                                    }
                                } else {
                                    if(penaltyBreakdown.getDataStandardDesc().get(Constants.FOSTER_CARE_PARTICIPATION) != null ) {
                                        if(!isSampled) {
                                            bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.FOSTER_CARE_PARTICIPATION));
                                        } else {
                                            bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Foster Care Youth_SAMPLED"));
                                        }
                                    }
                                    if(penaltyBreakdown.getDataStandardDesc().get(Constants.DISCHARGED_PARTICIPATION) != null) {
                                        if(!isSampled) {
                                            bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get(Constants.DISCHARGED_PARTICIPATION));
                                        } else {
                                            bulletListSuggestions.add(PenaltyLetterUtil.getNonComplianceSuggestionsMap().get("Outcomes Participation - Discharged Youth_SAMPLED"));
                                        }
                                    }
                                }
                            }
                            if(bulletListSuggestions != null && !bulletListSuggestions.isEmpty()) {
                                penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletListSuggestions);
                                bulletListSuggestions.clear();
                            }
                        }
                        // show only the Compliance Category information for previous penalty break down object with no detailed element list
                        if (!detailedList) {
                            if (previousPenaltyBreakdown != null) {
                                if (previousPenaltyBreakdown.getFileSubmissionDesc() != null) {
                                    if(previousPenaltyBreakdown.getFileSubmissionDesc().get(Constants.TIMELY_DATA) != null) {
                                        if(templateNumber == 5 || templateNumber == 6 ) {
                                            bulletList.add(previousPenaltyBreakdown.getFileSubmissionDesc2().get(Constants.TIMELY_DATA));
                                        }
                                        if(!suggestions) {
                                            penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
                                            penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
                                            bulletList.clear();
                                        }
                                    }
                                    if(previousPenaltyBreakdown.getFileSubmissionDesc().get(Constants.ERROR_FREE_INFO_LC) != null) {
                                        if(templateNumber == 5 || templateNumber == 6 ) {
                                            bulletList.add(previousPenaltyBreakdown.getFileSubmissionDesc2().get(Constants.ERROR_FREE_INFO_LC));
                                        }
                                        if(!suggestions) {
                                            penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
                                            penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
                                            bulletList.clear();
                                        }
                                        List<String> dataElementList = new ArrayList<>();
                                        // display the detailed list of file submission data elements
                                        if (previousPenaltyBreakdown.getFileSubmissionComplianceStdPenaltyDesc() != null) {
                                            for (String desc : previousPenaltyBreakdown.getFileSubmissionComplianceStdPenaltyDesc()) {
                                                dataElementList.add(desc);
                                            }
                                            if(!suggestions) {
                                                penaltyLetterUtil.writeWhiteLine(xmlStrWriter,1);
                                                penaltyLetterUtil.createDataElementList(xmlStrWriter, dataElementList);
                                                penaltyLetterUtil.writeWhiteLine(xmlStrWriter,0);
                                            }
                                        }
                                    }
                                    if(previousPenaltyBreakdown.getFileSubmissionDesc().get(Constants.FILE_FORMAT) != null) {
                                        if(templateNumber == 5 || templateNumber == 6 ) {
                                            bulletList.add(previousPenaltyBreakdown.getFileSubmissionDesc2().get(Constants.FILE_FORMAT));
                                        }
                                        if(!suggestions) {
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
                                        List<String> dataElementList = new ArrayList<>();
                                        // display the detailed list of data standard data elements only for Error Free Information non compliance
                                        if (previousPenaltyBreakdown.getDataStandardComplianceStdPenaltyDesc() != null) {
                                            for (String desc : previousPenaltyBreakdown.getDataStandardComplianceStdPenaltyDesc()) {
                                                if (!StringUtils.contains(desc, "Data Element 36") && !StringUtils.contains(
                                                        desc, "Data Element 3 - Record Number (45 CFR 1356.85(a)(3))")) {
                                                    dataElementList.add(desc);
                                                }
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
                                                bulletList.add(previousPenaltyBreakdown.getDataStandardDesc2().get(Constants.FOSTER_CARE_PARTICIPATION) 
                                                        + Constants.PENALTY_LETTER_PUNCTUATION_COLON + Constants.PENALTY_LETTER_WORD_AND);
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
                            } else {
                                if(!suggestions) {
                                    bulletList.add(Constants.NO_DATA_AVAILABLE);
                                    penaltyLetterUtil.writeWhiteLine(xmlStrWriter, 1);
                                    penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList);
                                }
                            }
                        } else {
                            // show the compliance category info of current penalty breakdown object with detailed element list
                            // Prashanth 06/12/2017: Task#26 . This block of code populates the summarized non-compliance descriptions for Final-Determination-NONCOMPLIANT-No-Corrected-File-xml.xml (Template=4)
                            // and also populates detailed non-compliance descriptions for Initial-Determination-NONCOMPLIANT-With-Data-xml.xml; Final-Determination-NONCOMPLIANT-With-Corrected-File-xml.xml template =2 or 5
                            showElements = true;
                            buildPenaltyBreakdown(transmissionId);
                            // display file submission standard
                            if (penaltyBreakdown.getFileSubmissionDesc() != null) {
                                writeBulletSection(Constants.FILE_SUBMISSION_STANDARDS, Constants.TIMELY_DATA,
                                                templateNumber, penaltyBreakdown.getFileSubmissionErrors());
                                writeBulletSection(Constants.FILE_SUBMISSION_STANDARDS, Constants.ERROR_FREE_INFO,
                                                templateNumber, penaltyBreakdown.getFileSubmissionErrors());
                                writeBulletSection(Constants.FILE_SUBMISSION_STANDARDS, Constants.FILE_FORMAT,
                                                templateNumber, penaltyBreakdown.getFileSubmissionErrors());
                            }
                            // display data submission standard
                            if (penaltyBreakdown.getDataStandardDesc() != null) {
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
                    } else {
                        if (!skipElement) {
                            smartXmlTagWriter.write(sBldr.toString());
                        }
                    }
                } catch (XMLStreamException e) {
                    log.error(e.getMessage(), e);
                }
            }

            /**
             * @param complianceCategory
             * @param templateNumber
             * @param errors
             */
            private void writeBulletSection(String complianceSuperCategory, String complianceCategory, int templateNumber, List<NytdError> errors) {
                Set<String> errorSet = new HashSet<>();
                List<String> bulletList = new ArrayList<>();
                errors.stream().filter(error -> 
                        StringUtils.equalsIgnoreCase(complianceCategory, error.getComplianceCategory().getName()))
                        .map(NytdError::formatErrorMessage)
                        .filter(msg -> !StringUtils.equalsIgnoreCase(msg, Constants.NOT_APPLICABLE_ABBREV))
                        .forEach(errorSet::add);
                if (!errorSet.isEmpty()) {
                    penaltyLetterUtil.writeWhiteLine(xmlStrWriter,1);
                    penaltyLetterUtil.createBulletedList(
                            xmlStrWriter, getErrorDescription(complianceSuperCategory, complianceCategory, templateNumber));
                    // Write the detailed error description for only Final Non-Compliant Letters
                    if (templateNumber == 4 || templateNumber == 5) {
                        bulletList.addAll(errorSet);
                        penaltyLetterUtil.createBulletedList(xmlStrWriter, bulletList, "1");
                    }
                    errorSet.clear();
                }
            }

            /**
             * @param complianceCategory
             * @param templateNumber
             * @return
             */
            private List<String> getErrorDescription(String complianceSuperCategory, String complianceCategory, int templateNumber) {
                if (StringUtils.equalsIgnoreCase(complianceCategory, Constants.TIMELY_DATA)) {
                    return getTimelyDataDescription();
                } else if (StringUtils.equalsIgnoreCase(complianceSuperCategory, Constants.FILE_SUBMISSION_STANDARDS)) {
                    return Collections.singletonList(templateNumber != 4 ?
                            penaltyBreakdown.getFileSubmissionDesc().get(complianceCategory) :
                            penaltyBreakdown.getFileSubmissionDesc2().get(complianceCategory));
                } else { // Data Standards
                    return Collections.singletonList(templateNumber != 4 ?
                            penaltyBreakdown.getDataStandardDesc().get(complianceCategory) :
                            penaltyBreakdown.getDataStandardDesc2().get(complianceCategory));
                }
            }

            private List<String> getTimelyDataDescription() {
                String tempTimelyData = penaltyBreakdown.getFileSubmissionDesc().get(Constants.TIMELY_DATA);
                if(StringUtils.contains(trans.getReportingPeriod().getName(), 'A')) {
                    tempTimelyData = StringUtils.replace(tempTimelyData, "{March 31 or September 30}", "March 31");
                    tempTimelyData = StringUtils.replace(tempTimelyData, "{May 15 or November 14}", "May 15");
                } else {
                    tempTimelyData = StringUtils.replace(tempTimelyData, "{March 31 or September 30}", "September 30");
                    tempTimelyData = StringUtils.replace(tempTimelyData, "{May 15 or November 14}", "November 14");
                }
                return Collections.singletonList(tempTimelyData);
            }

            // parser encounters the beginning of the xml element
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                skipElement = false;
                try {
                    // skip the {bulleted list} placeholder while being inserted in the new doc
                    if ((qName.equals("w:p") || qName.equals("w:pPr") || qName.equals("w:rPr")
                            || qName.equals("w:sz") || qName.equals("w:r") || qName.equals("w:t"))
                            && (StringUtils.isNotBlank(attributes.getValue("id")) && attributes.getValue("id").equals("list"))) {
                        skipElement = true;
                    }
                    // if the xml element is just a regular node push it in the new docx file
                    if (!skipElement) {
                        smartXmlTagWriter.writeStartElement(qName);
                        // iterate over the attributes associated with the element
                        for (int i = 0; i < attributes.getLength(); i++) {
                            String attrName = attributes.getLocalName(i) != null ? attributes.getLocalName(i) : "";
                            String attrVal = attributes.getValue(i) != null ? attributes.getValue(i) : "";
                            smartXmlTagWriter.writeAttribute(attrName, attrVal);
                        }
                    }
                } catch (XMLStreamException e) {
                        log.error(e.getMessage(), e);
                }
            }

            /**
             * Parser encounters the end of the element
             */
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                try {
                    // skip adding the end of the element for {bulleted list} placeholder
                    if (!skipElement) {
                        smartXmlTagWriter.writeEndElement(qName);
                    }
                } catch (XMLStreamException e) {
                    log.error(e.getMessage(), e);
                }
            }
            
        };

        saxParser.parse(is, handler); 
        
        if (inputStream != null) {
            inputStream.close();
        }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        
    
        return null;
    }
    
    /**
    * Creates the Penalty Break down object based on transmission id
    * @param transmissionId as Long
    */
    private void buildPenaltyBreakdown(Long transmissionId) {
        // load the compliance categories
        ComplianceService dao = getComplianceService();
        List<ComplianceCategory> fileSubmissionCategories = dao.getFileSubmissionStandardsCategories();
        List<ComplianceCategory> dataCategories = dao.getDataStandardsCategories();
        List<NytdError> fileSubmissionErrors;
        List<NytdError> dataErrors;
        // create hashmaps of error counts for each super category the maps are used by 
        // penaltyBreakdown.jsp to calculate column rowspans in the breakdown HTML tables
        Map<ComplianceCategory, Integer> fileSubmissionErrorCounts = new HashMap<>();
        for (ComplianceCategory category : fileSubmissionCategories) {
            fileSubmissionErrorCounts.put(category, dao.getErrorCountForCategory(transmissionId, category));
        }
        Map<ComplianceCategory, Integer> dataErrorCounts = new HashMap<>();
        for (ComplianceCategory category : dataCategories) {
            dataErrorCounts.put(category, dao.getErrorCountForCategory(transmissionId, category));
        }
        // calculate the data penalties
        Map<ComplianceCategory, Double> dataPenalties = new HashMap<>();
        for (ComplianceCategory category : dataCategories) {
            dataPenalties.put(category, dao.calcDataPenalty(transmissionId, category));
        }
        if(showElements) {
            dataErrors = dao.getErrorsForCategories(transmissionId, dao.getDataStandardsCategories());
            fileSubmissionErrors = dao.getErrorsForCategories(transmissionId, dao.getFileSubmissionStandardsCategories());
        } else {
            fileSubmissionErrors = new ArrayList<>();
            dataErrors = new ArrayList<>();
        }
        // initialize the penalty breakdown
        this.penaltyBreakdown = new PenaltyBreakdown();
        this.penaltyBreakdown.setDataPenalties(dataPenalties);
        this.penaltyBreakdown.setFileSubmissionErrorCounts(fileSubmissionErrorCounts);
        this.penaltyBreakdown.setDataErrorCounts(dataErrorCounts);
        this.penaltyBreakdown.setFileSubmissionErrors(fileSubmissionErrors);
        this.penaltyBreakdown.setDataErrors(dataErrors);
        this.penaltyBreakdown.setFileSubmissionComplianceStdPenaltyDesc(
                createComplianceStandardPenaltyLetterDesc(fileSubmissionErrors));
        this.penaltyBreakdown.setDataStandardComplianceStdPenaltyDesc(
                createComplianceStandardPenaltyLetterDesc(dataErrors));
        this.penaltyBreakdown.setDataStandardDesc(createDataStandardErrorMapForPenalty(dataCategories, penaltyBreakdown));
        this.penaltyBreakdown.setDataStandardDesc2(createDataStandardErrorMapForPenalty2(dataCategories, penaltyBreakdown));
        this.penaltyBreakdown.setFileSubmissionDesc(
                createFileSubmissionErrorMapForPenalty(fileSubmissionCategories, penaltyBreakdown));
        this.penaltyBreakdown.setFileSubmissionDesc2(
                createFileSubmissionErrorMapForPenalty2(fileSubmissionCategories, penaltyBreakdown));
        // add missing categories to error lists
        this.penaltyBreakdown.addMissingCategories(fileSubmissionCategories, dataCategories);
    }
    
    /**
    * Creates the Penalty Break down object of the last inactive transmission submitted
    * @param stateId as Long
    * @param reportingPeriodId as Long
    */
    private void buildPreviousPenaltyBreakdown(Long stateId, Long reportingPeriodId ) {
        ComplianceService complianceSvc = getComplianceService();
        TransmissionServiceP3 transmissionSvc = getTransmissionServiceP3();
        List<ComplianceCategory> fileSubmissionCategories = complianceSvc.getFileSubmissionStandardsCategories();
        List<ComplianceCategory> dataCategories = complianceSvc.getDataStandardsCategories();
        List<NytdError> previousFileSubmissionErrors;
        List<NytdError> previousDataErrors;
        Long previousTransmissionId = transmissionSvc.getTransmissionIdOfInactiveStatus(stateId, reportingPeriodId);
        // create hashmaps of error counts for each super category the maps are used by penaltyBreakdown.jsp to calculate
        // column rowspans in the breakdown HTML tables
        if (previousTransmissionId != null) {
            Map<ComplianceCategory, Integer> previousFileSubmissionErrorCounts = new HashMap<>();
            for (ComplianceCategory category : fileSubmissionCategories) {
                previousFileSubmissionErrorCounts.put(
                        category, complianceSvc.getErrorCountForCategory(previousTransmissionId, category));
            }
            Map<ComplianceCategory, Integer> previousDataErrorCounts = new HashMap<>();
            for (ComplianceCategory category : dataCategories) {
                previousDataErrorCounts.put(
                        category, complianceSvc.getErrorCountForCategory(previousTransmissionId, category));
            }
            // calculate the data penalties
            Map<ComplianceCategory, Double> previousDataPenalties = new HashMap<>();
            for (ComplianceCategory category : dataCategories) {
                previousDataPenalties.put(category, complianceSvc.calcDataPenalty(previousTransmissionId, category));
            }

            if(showElements) {
                    previousFileSubmissionErrors = complianceSvc.getErrorsForCategories(previousTransmissionId,
                            complianceSvc.getFileSubmissionStandardsCategories());
                    previousDataErrors = complianceSvc.getErrorsForCategories(previousTransmissionId,
                            complianceSvc.getDataStandardsCategories());
            } else {
                    previousFileSubmissionErrors = new ArrayList<>();
                    previousDataErrors = new ArrayList<>();
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
            this.previousPenaltyBreakdown.setDataStandardDesc(createDataStandardErrorMapForPenalty(dataCategories, previousPenaltyBreakdown));
            this.previousPenaltyBreakdown.setFileSubmissionDesc(createFileSubmissionErrorMapForPenalty(fileSubmissionCategories, previousPenaltyBreakdown));
            this.previousPenaltyBreakdown.setDataStandardDesc2(createDataStandardErrorMapForPenalty2(dataCategories, previousPenaltyBreakdown));
            this.previousPenaltyBreakdown.setFileSubmissionDesc2(createFileSubmissionErrorMapForPenalty2(fileSubmissionCategories, previousPenaltyBreakdown));

            // add missing categories to error lists
            this.previousPenaltyBreakdown.addMissingCategories(fileSubmissionCategories, dataCategories);
        }
    }
    
    /**
     * Creates a map of the file submission compliance category and its penalty letter description
     * @param fileSubmissionCategories as List<ComplianceCategory>
     * @param penaltyBreakdown as PenaltyBreakdown
     * @return fileSubmissionDesc as Map<String, String> 
     */
    private Map<String, String> createFileSubmissionErrorMapForPenalty(
            List<ComplianceCategory> fileSubmissionCategories, PenaltyBreakdown penaltyBreakdown) {
        
        Map<String, String> fileSubmissionDesc = new CaseInsensitiveMap<>();
        for (ComplianceCategory cc : fileSubmissionCategories) {
            Integer count = penaltyBreakdown.getFileSubmissionErrorCounts().get(cc);
            if (count > 0) {
                    fileSubmissionDesc.put(cc.getName(), cc.getPenaltyLetterDesc());
            }
        }
        return fileSubmissionDesc;
    }

    /**
     * Creates a map of the file submission compliance category and its penalty letter description
     * @param fileSubmissionCategories as List<ComplianceCategory>
     * @param penaltyBreakdown as PenaltyBreakdown
     * @return fileSubmissionDesc as Map<String, String> 
     */
    private Map<String, String> createFileSubmissionErrorMapForPenalty2(
            List<ComplianceCategory> fileSubmissionCategories, PenaltyBreakdown penaltyBreakdown) {
            
        Map<String, String> fileSubmissionDesc2 = new CaseInsensitiveMap<>();
        for (ComplianceCategory cc : fileSubmissionCategories) {
            Integer count = penaltyBreakdown.getFileSubmissionErrorCounts().get(cc);
            if (count > 0) {
                fileSubmissionDesc2.put(cc.getName(), cc.getPenaltyLetterDesc2());
            }
        }
        return fileSubmissionDesc2;
    }

    /**
     * Creates a map for Data standard errors for Penalty Letters containing the
     * compliance category name and the penalty letter description
     * @param dataCategories as List<ComplianceCategory>
     * @param penaltyBreakdown as PenaltyBreakdown
     * @return dataStandardDesc as Map<String, String>
     */
    private Map<String, String> createDataStandardErrorMapForPenalty(List<ComplianceCategory> dataCategories,
            PenaltyBreakdown penaltyBreakdown) {
            
        Map<String, String> dataStandardDesc = new CaseInsensitiveMap<>();
        for (ComplianceCategory cc : dataCategories) {
            Integer count = penaltyBreakdown.getDataErrorCounts().get(cc);
            if (count > 0) {
                    dataStandardDesc.put(cc.getName(), cc.getPenaltyLetterDesc());
            }
        }
        return dataStandardDesc;
    }

    /**
     * Creates a map for Data standard errors for Penalty Letters containing the
     * compliance category name and the penalty letter description
     * @param dataCategories as List<ComplianceCategory>
     * @param penaltyBreakdown as PenaltyBreakdown
     * @return dataStandardDesc as Map<String, String>
     */
    private Map<String, String> createDataStandardErrorMapForPenalty2(List<ComplianceCategory> dataCategories,
            PenaltyBreakdown penaltyBreakdown) {
        
        Map<String, String> dataStandardDesc2 = new CaseInsensitiveMap<>();
        for (ComplianceCategory cc : dataCategories) {
            Integer count = penaltyBreakdown.getDataErrorCounts().get(cc);
            if (count > 0) {
                    dataStandardDesc2.put(cc.getName(), cc.getPenaltyLetterDesc2());
            }
        }
        return dataStandardDesc2;
    }

    /**
     * Creates a list of Compliance Standard Penalty Letter Description for a
     * file submission and data standards
     *
     * @param errorCategories as List<NytdError>
     * @return finalCsPenaltyLttrDescList as List<String>
     */
    private List<String> createComplianceStandardPenaltyLetterDesc(List<NytdError> errorCategories) {
        Map<Long, String> csPenaltyLttrDescMap = getComplianceService().getComplianceStandardPenaltyLetterDesc();
        List<String> finalCsPenaltyLttrDescList = new ArrayList<>();
        for (NytdError ne : errorCategories) {
            Long elementId = null;
            if (ne.getNonCompliance() != null && ne.getNonCompliance().getDataAggregate() != null
                    && ne.getNonCompliance().getDataAggregate().getElement() != null) {
                elementId = ne.getNonCompliance().getDataAggregate().getElement().getId();
                if (elementId != null) {
                        finalCsPenaltyLttrDescList.add(csPenaltyLttrDescMap.get(elementId));
                }
            }
        }
        return finalCsPenaltyLttrDescList;
    }
    
    /**
     * @see PenaltyLetterService#zipPenaltyLetters(List<String>, File)
     */
    @Override
    public void zipPenaltyLetters(List<String> srcFiles, File zipFile) throws IOException {
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        // TODO: Mutsuo 8/6/2021 SonarQube added code to close in finaly block, consider "try-with-resources" pattern.
        try {
            fos = new FileOutputStream(zipFile);
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
            for(String filePath : srcFiles){
                File input = new File(filePath);
                fis = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                log.info("Zipping the file: "+input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fis.read(tmp)) != -1){
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();
                fis.close();
                
                // now delete the raw files already zipped
                // need gc and thread sleep to deal with the notorious File Open in Another Program on Windows.
                System.gc();
                Thread.sleep(2000);
                FileDeleteStrategy.FORCE.delete(input);
                log.info(input.getName() + " is deleted");
            }
            zipOut.close();
            log.info("Completed Zipping the files.");
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            try {
                if(fos != null) fos.close();
                if(zipOut != null) zipOut.close();
                if(fis != null) fis.close();
            } catch(Exception ex){
                log.error(ex.getMessage());
            }
        }
    }
    
    /**
     * @see PenaltyLetterService#getPlMetadata()
     */
    @Override
    public List<PenaltyLettersMetadata> getPlMetadata() {
        List<PenaltyLettersMetadata> exportList = new LinkedList<>();
        try {
            List<PenaltyLettersMetadata> plMetadataList = penaltyLetterDAO.getPenaltyLetterMetadata();
            (plMetadataList.stream()).forEach(m -> exportList.add(m));
        } catch (Exception e) {
                log.error(" Error in getting Penalty Letters metadata" + e);
        }

        return exportList;
    }
    
    /**
     * @see PenaltyLetterService#savePenaltyLettersMetadata(PenaltyLettersMetadata,SiteUser)
     */
    @Override
    public void savePenaltyLettersMetadata(PenaltyLettersMetadata metadata, SiteUser user) {
        penaltyLetterDAO.savePenaltyLettersMetadata(metadata).builder()
                .fileName(metadata.getFileName())
                .reportingPeriods(metadata.getReportingPeriods())
                .states(metadata.getStates())
                .userName(user.getUserName())
                .status(metadata.getStatus())
                .transmissionIds(metadata.getTransmissionIds())
                .build();
    }
    
    /**
     * @see PenaltyLetterService#searchGeneratePenaltyLetters(PenaltySearch)
     */
    public PenaltySearch searchGeneratePenaltyLetters(PenaltySearch search) {
        return penaltyLetterDAO.searchGeneratePenaltyLetters(search);
    }

}
