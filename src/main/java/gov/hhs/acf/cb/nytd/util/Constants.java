/**
 * Filename: Constants.java
 * 
 * Copyright 2009, ICF International Created: Jun 8, 2009 Author: 16939
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.util;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

/**
 * @author 16939, 18816
 * 
 */
public class Constants
{
	public static final String COMPLIANCE = "COMPLIANCE";
	public static final String PERCENTVALUE = "PERCENT";
	public static final String COUNTVALUE = "COUNT";
	public static final String DATAQUALITYADV = "DATAQUALITYADVISORIES";
	public static final String AGGCOMPLIANCE = "AGGCOMPLIANCE";
	public static final String COMPLIANCESTATUS = "ComplianceStatus";
	public static final String COMPLIANCEDETAILS = "ComplianceDetails";
	public static final String COMPLIANT = "Compliant";
	public static final String NON_COMPLIANT = "Non-Compliant";
	public final static String STATE_HOME = "stateHome";
	public final static String REGION_HOME = "regionHome";
	public final static String FEDERAL_HOME = "federalHome";
	public final static String AGGREGATE_COMPLIANCE__SEARCH_RESULTS = "aggregateComplainceSearchResults";
	public static final String AGGREGATE_DATA_ERRORS = "Aggregate Data Errors";
	public static final String SAMPLINGREQUESTSTATUS = "SamplingRequestStatus";

	public static final int CHARSTRINGTYPELENGTH = 256;

	public static final String TRANSMISSIONTYPE = "transmissionLevel";
	public static final String AGGREGATETYPE = "aggregateType";
	public static final String RECORDLEVEL = "recordLevel";

	public static final String FILE_SUBMISSION_STANDARDS = "File Submission Standards";
	public static final String DATA_STANDARDS = "Data Standards";
	public static final String ERROR_TYPES = "Errors";
	public static final String SUBMISSION_STATUS = "submissionStatus";

	public static final String EXPORT_ALL = "All";
	public static final String EXPORT_COMPLETE = "Complete";
	public static final String EXPORT_DNE = "Does not exist";
	public static final String EXPORT_IN_PROCESS = "In process";
	public static final String EXPORT_NONE = "None";

	public static final String TIMELY_DATA = "Timely Data";
	public static final String FILE_FORMAT = "File Format";
	public static final String ERROR_FREE_INFO = "Error Free Information";
	public static final String ERROR_FREE_INFO_LC = "Error free Information";
	public static final String DATA_ERROR_TYPES = "Missing, Internally Inconsistent, Out-of-Range";
	public static final String MISSING = "Missing Data";
	public static final String INTERNALLY_INCONSISTENT = "Internally Inconsistent Data";
	public static final String OUT_OF_RANGE = "Out of Range Data";
	public static final String UNIVERSE = "Outcomes Universe";
	public static final String FOSTER_CARE_PARTICIPATION = "Outcomes Participation - Foster Care Youth";
	public static final String DISCHARGED_PARTICIPATION = "Outcomes Participation - Discharged Youth";

	public static final int[] DEMOGRAPHIC_FIELDS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
	public static final int[] CHARACTERISTIC_FIELDS = { 14, 15, 16, 17, 18, 19, 34, 35, 36 };
	public static final int[] SERVICE_FIELDS = { 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
	public static final int[] OUTCOME_FIELDS = { 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
			52, 53, 54, 55, 56, 57, 58 };

	public static final int NUM_DEMOGRAPHIC_FIELDS = DEMOGRAPHIC_FIELDS.length;
	public static final int NUM_CHARACTERISTIC_FIELDS = CHARACTERISTIC_FIELDS.length;
	public static final int NUM_SERVICE_FIELDS = SERVICE_FIELDS.length;
	public static final int NUM_OUTCOME_FIELDS = OUTCOME_FIELDS.length;
	public static final int NUM_ELEM_FIELDS = 58;
	public static final int NUM_GENERAL_FIELDS = 1;

	// BR-08-02
	public static final BigDecimal MAX_PENALTY = new BigDecimal(2.5, new MathContext(2));
	public static final BigDecimal MIN_PENALTY = new BigDecimal(0.5, new MathContext(2));

	// NYTD-07-01-02-03
	public static final String SAV_LABEL = "SPSS (.sav)";
	public static final String CSV_LABEL = "Comma Delimited (.csv)";
	public static final String XLS_LABEL = "Excel (.xls)";
	public static final String HTM_LABEL = "HTML (.htm)"; // TODO: Implement
	// XHTML instead of
	// HTML.
	public static final String TXT_LABEL = "ASCII (.txt))";

	// Stored procedure to save transmission and submit transmission
	public static final String STORED_PROC_INSERT_TRANSMISSION = "fnProcessNYTDXmlData";
	public static final String STORED_PROC_SUBMIT_SUBMISSION = "fnInsRecordToExport";
	public static final String STORED_PROC_PROCESS_RULES = "FNRulesEngine";
	// Related stored procedure Data Upload
	public static final String P_FILE_NAME = "fileName";
	public static final String P_FILE_SIZE = "fileSize";
	public static final String P_FILE_TIMESTAMP = "fileTimeStamp";
	public static final String P_FILE_DATA = "fileData";
	// Related to stored procedure to submit transmission
	public static final String P_TRANSMISSION_ID = "transmissionId";
	public static final String P_SITE_USER_ID = "siteUserId";
	public static final String P_CREATED_DATE = "createdDate";
	// Data Export
	public static final String SAV_KEY = "1";
	public static final String CSV_KEY = "2";
	public static final String XLS_KEY = "3";
	public static final String HTM_KEY = "4";
	public static final String TXT_KEY = "5";
	public static final String SAV_EXT = "sav";
	public static final String CSV_EXT = "csv";
	public static final String XLS_EXT = "xls";
	public static final String HTM_EXT = "htm";
	public static final String TXT_EXT = "txt";

	// Rules Engine
	public static final int LOOKUP_COMP_STD_AGGTYPE_ID = 5;
	public static final int LOOKUP_DATA_QTY_AGGTYPE_ID = 6;
	public static final int LOOKUP_AGG_COMP_AGGTYPE_ID = 41;
	public static final int LOOKUP_TRANS_LEVEL_ID = 12;
	public static final int LOOKUP_AGG_LEVEL_ID = 13;
	public static final int LOOKUP_RECORD_LEVEL_ID = 14;

	public static final int COMP_CAT_MOORIID_ID = 3;
	public static final int COMP_CAT_OUTCOMES_UNI_ID = 4;
	public static final int COMP_CAT_OUTCOMES_FCY_ID = 5;
	public static final int COMP_CAT_OUTCOMES_DY_ID = 6;
	public static final int COMP_CAT_MD_ID = 7;
	public static final int COMP_CAT_OORD_ID = 8;
	public static final int COMP_CAT_IID_ID = 9;
	public static final int COMP_CAT_EFI_ID = 10;

	public static final int PROB_DESC_MD_ID = 1;
	public static final int PROB_DESC_OORD_ID = 26;
	public static final int PROB_DESC_IID_ID = 22;

	public static final int PROB_DESC_EFI_ID = 6;
	public static final int PROB_DESC_MOORIID_ID = 7;
	public static final int PROB_DESC_OUTCOMES_UNI_ID = 8;
	public static final int PROB_DESC_OUTCOMES_FCY_ID = 9;
	public static final int PROB_DESC_OUTCOMES_DY_ID = 10;

	public static final int PROB_DESC_POP_INC_ID = 14;
	public static final int PROB_DESC_OCD_INC_ID = 16;
	public static final int PROB_DESC_RACE_INC_ID = 23;
	public static final int PROB_DESC_SERV_INC_ID = 24;
	public static final int PROB_DESC_PART_INC_ID = 25;

	public static final int NOT_SERVED_ID = 7;
	public static final int SERVED_ID = 1;
	public static final int OTHER_ID = 0;
	public static final int BL_PRE_ID = 2;
	public static final int BL_POST_ID = 3;
	public static final int BL_ID = 4;
	public static final int FU_19_ID = 5;
	public static final int FU_21_ID = 6;

	public static final int ELEM_BIRTH_DATE_ID = 4;
	public static final int ELEM_RACE_ID = 6;
	public static final int ELEM_RACE_DECLINED_ID = 12;
	public static final int ELEM_SERVED_CHECK_ID = 14;
	public static final int ELEM_ILS_ID = 20;
	public static final int ELEM_OUTCOME_CHECK_ID = 34;
	public static final int ELEM_OUTCOMES_UNI_ID = 34;
	public static final int ELEM_SURVEY_DATE_ID = 35;
	public static final int ELEM_FOSTER_CARE_ID = 36;

	public static final int ELEM_TYPE_1 = 3;
	public static final int ELEM_TYPE_2 = 14;
	public static final int ELEM_TYPE_3 = 34;
	public static final int ELEM_TYPE_4 = 37;

	public static final int AGE_BL = 17;
	public static final int AGE_FU_19 = 19;
	public static final int AGE_FU_21 = 21;
	public static final double REP_PERIOD_LENGTH = .5;
	public static final int BUFFER_PERIOD_LENGTH = 45;

	public static final double DAYS_PER_YEAR = 365.2422;
	public static final double MS_PER_YEAR = 1000.0 * 60 * 60 * 24 * 365.2422;
	public static final double HALF_YEAR_IN_MONTHS = 6;

	public static final String VALUE_OUTCOMES_FCY = "yes";
	public static final String VALUE_OUTCOMES_DY = "no";
	public static final double STD_OUTCOMES_FCY = 80;
	public static final double STD_OUTCOMES_DY = 60;
	public static final double STD_BIRTH_DATE_13_25 = 10;
	public static final double STD_SURVEY_DATE = 75;

	public static final String VALUE_BIRTH_DATE_13 = "Age under 13";
	public static final String VALUE_BIRTH_DATE_25 = "Age over 25";

	public static final Object BLANK = null;

	public static final String ALL_VALUES = "All";
	public static final String TRUE_STR = "true";
	public static final String FALSE_STR = "false";

	// Action results
	public static final String BAD_PARAM = "badparam";
	public static final String BAD_INPUT = "badinput";
	public static final String ALTERNATE = "alternate";
	public static final String CANCEL = "cancel";
	public static final String CREATINGFILE = "creatingfile";

	// privileges
	public static final String PRIV_CAN_EXPORT_DATA = "canExportData";
	public static final String PRIV_CAN_ADMIN_OFFICE_USERS = "canAdminOfficeUsers";
	public static final String PRIV_CAN_ADMIN_ALL_USERS = "canAdminAllUsers";
	public static final String PRIV_CAN_VIEW_TRANSMISSIONS = "canViewTransmissions";
	public static final String PRIV_CAN_DELETE_ANY_TRANSMISSION = "canDeleteAnyTransmission";

	// Specific secondary roles
	public static final String SUR_STATE_AUTH_OFF = "State Designated Authorized Official";
	public static final String SUR_STATE_AUTH = "State Authorized Official";
	public static final String SUR_DATA_TEAM = "CB Data Team Member";
	public static final String SUR_MANAGER = "Manager";
	public static final String SUR_DEVELOPER = "ICF NYTD Developer";

	// Messages
	public static final String CBUSER = "CB Central Office Staff Member";
	public static final String REGIONALUSER = "Regional Office User";
	public static final String STATEUSER = "State User";
	public static final String SYSTEMADMIN = "System Administrator";
	public static final String DROPDOWNFORSTATE = "state";
	public static final String DROPDOWNFORONESTATE = "onlyOneState";
	public static final String DROPDOWNFORREGION = "region";
	public static final String DROPDOWNFORONEREGION = "onlyOneRegion";
	public static final String DROPDOWNFORCB = "centralOffice";
	public static final String CENTRALOFFICEUSERSBUTTON = "Add Central Office Users";
	public static final String REGIONALUSERSBUTTON = "Add Regional Users";
	public static final String STATEUSERSBUTTON = "Add State Users";
	public static final String POSTMESSAGEBUTTON = "Post";
	public static final String CANCELMESSAGEBUTTON = "Cancel";
	public static final String OKMESSAGEBUTTON = "OK";

	public static final String DROP_DOWN_DEFAULT_VALUE = "All";

	// SystemGeneratedMessages
	public static final String DATAEXPORTMESSAGE = "Data Export Receipt";
	public static final String TRANSMISSIONRECEIPTEMESSAGE = "Transmission Receipt";
	public static final String SUBMISSIONRECEIPTEMESSAGE = "Submission Receipt";
	public static final String SUBMISSIONREMINDERMESSAGE = "${reportingPeriood} Reporting Period Submission Reminder";
	public static final String UNSUCCESSFULTRANSMISSIONRECEIPT = "Unsuccessfull Transmission Receipt";
	public static final String FEDERALSUBMISSIONRECEIPTMESSAGE = "Federal Submission Receipt";
	public static final long PRIMARYUSERROLEIDFORFEDERALS = 2;
	public static final String CBEMAILADDRESS = "NYTDhelp@acf.hhs.gov"; // "nytd@icfi.com";
	// NYTD Year. Setting Year to four years in advance to test the NYTD system
	// as discussed with the team
	// TODO The NYTDTESTYEAR needs to be removed after the testing
	public static final String NYTDTESTYEAR = "2013";
	public static final int REPORTINGPERIODGRACEPERIOD = 45;
	public static final int CURRENTDAY = 1;
	public static final int TOTALHOURSINADAY = 23;
	public static final int TOTALMINUTESINANHOUR = 59;

	// Constants for transmission Type value
	public static final String TRANSMISSION_TYPE = "transmission";
	public static final String TRANSMISSION_TYPE_VALUE_TEST = "Test";
	public static final String TRANSMISSION_SUBMISSION_STATUS = "submission.status";
	public static final String TRANSMISSION_ACTIVE_SUBMISSION = "Active";
	public static final String TRANSMISSION_INACTIVE_SUBMISSION = "Inactive";
	public static final String TRANSMISSION_QUERY_PARAM = "search.transmissionId";
	public static final String TRANSMISSION_ID_REQUEST_PARAM = "currentTransmissionId";

	public static final String tempDate1 = "12/17/2009";
	public static final String tempDate2 = "09/30/2009";

	// email address for the nytdSystem
	public static final String nytdSystemEmail = "ngandhi@icfi.com";

	// Constants for sorting columns on Transmission Details page
	public static final String ORDER_ASC = "asc";
	public static final String ORDER_DESC = "desc";
	public static final String ELEMENT_NAME = "elementName";
	public static final String ELEMENT_NUMBER = "elementNumber";
	public static final String REGULATED_COMPLIANCE_STANDARD = "reqComplianceStd";
	// corresponds to actualRate in ComplinceErrors
	public static final String ERROR_FREE = "errorFree";
	public static final String RECORD_NUMBER = "recordNumber";
	public static final String DESCRIPTION = "description";
	public static final String NOTE = "note";
	public static final String DATUM_VALUE = "datumValue";
	public static final String DATA_QUALITY_ADV_RATE = "dataQualityAdvRate";
	public static final String ACTUAL_RATE = "actualRate";
	public static final String NAME_KEY = "nameKey";
	public static final String POTENTIAL_PENALTY = "potentialPenalty";
	public static final String REPORTING_PERIOD = "reportingPeriod";
	public static final String STATE = "state";

	// application shared data
	public static final String APPKEY_PRIMARY_USER_ROLE_WITH_SYS_ADMIN_LIST = "primaryUserRoleWithSysAdminList_key";
	public static final String APPKEY_PRIMARY_USER_ROLE_NO_SYS_ADMIN_LIST = "primaryUserRoleNoSysAdminList_key";
	public static final String APPKEY_COMPLIANCE_STATUS_LIST = "complianceStatusList_key";
	public static final String APPKEY_ELEMENT_NUMBER_DROP_DOWN = "elementNumberDropDown_key";
	public static final String APPKEY_FILE_TYPE_LIST = "fileTypeList_key";
	public static final String APPKEY_FILE_TYPE_LIST_FED = "fileTypeListFed_key";
	public static final String APPKEY_REPORTING_PERIOD_LIST = "reportingPeriodList_key";
	public static final String APPKEY_STATE_LIST = "stateList_key";
	public static final String APPKEY_REGION_LIST = "viewRegionList_key";
	public static final String APPKEY_VIEW_RESULTS_LIST = "viewResultsList_key";
	public static final String APPKEY_USER_SECONDARY_ROLE_LIST = "viewUserSecondaryRole_key";
	public static final String APPKEY_EXT_USER_SECONDARY_ROLE_LIST = "viewExtUserSecondaryRole_key";
	public static final String APPKEY_SAMPLE_REQUEST_STATUS_LIST = "sampleRequestStatus_key";
	public static final String SITE_USER = "siteUser";

	// Directory Polling
	public static final int POLL_WAIT = 1000;
	public static final int MAX_TRIES = 100;

	// constants for processing status
	public static final String TABLE_POPULATION_COMPLETED = "Table population completed";
	public static final String TABLE_POPULATION_EXITED = "Exited";
	public static final String RULES_ENGINE_INITIATED = "Rules engine initiated";
	public static final String RULES_ENGINE_COMPLETED = "Rules engine completed";
	public static final String UNCAUGHT_ERROR_DURING_TABLE_POPULATION = "Uncaught error during table population";
	public static final String UNCAUGHT_ERROR_DURING_RULES_ENGINE = "Uncaught error during rules engine";
	public static final String UPDATED_BY_WEB_APP = "updated by Web App";

	public static final String INCORRECT_FILE_FORMAT = "Incorrect File Format";

	// penalty letters
	public static final String REGULAR_TRANSMISSION = "Regular";
	public static final String CORRECTED_TRANSMISSION = "Corrected";
	public static final String SUBSEQUENT_TRANSMISSION = "Subsequent";
	public static final String PENALTY_LETTER_TYPE_INITIAL = "Initial";
	public static final String PENALTY_LETTER_TYPE_FINAL = "Final";
	public static final String BULLET_PLACEHOLDER_CURRENT = "bulleted list-current";
	public static final String BULLET_PLACEHOLDER_PREVIOUS = "bulleted list-previous";
	public static final String BULLET_PLACEHOLDER_SUGGESTIONS = "bulleted list-suggestions";
	public static final String DOLLAR_AMOUNT_PLACEHOLDER = "$ amount";
	public static final String PERCENTAGE_FINE_PLACEHOLDER = "% amount";
	public static final String PERCENTAGE_FINE_PLACEHOLDER_REGULAR = "% regular amount";
	public static final String NO_DATA_AVAILABLE = "No Data Available";
	public static final String REPORT_PERIOD_PLACEHOLDER = "Report Period";
	public static final String DATE_PLACEHOLDER = "Date";
	public static final String SALUTATION_PREFIX_PLACEHOLDER = "state.prefix";
	
	public static final String FILE_ERROR_FREE_INFO = "Contain 100 percent error-free information for the elements listed above that contained an unallowable percentage of errors.";
	public static final String FILE_SUBMISSION_STANDARD_TIMELY_DATA_OR_FILE_FORMAT ="Be submitted no later than ^Corrected End Report Period^.";
	//public static final String FILE_SUBMISSION_STANDARD_FILE_FORMAT ="Be submitted no later than ^Corrected End Report Period^ (45 CFR 1356.85(a)(2)).";
	public static final String DATA_ERROR_FREE_INFO = "Contain at least 90 percent error-free information for the elements listed above that contained an unallowable percentage of errors.";
	public static final String OUTCOMES_UNIVERSE_MSG = "Contain outcomes information or a reason explaining why there are no outcomes data for each youth in the follow-up population as described at 45 CFR 1356.85(b)(2).";
	public static final String OUTCOMES_PARTICIPATION_FOSTERCARE = "Contain outcomes information on at least 80 percent of youth in the follow-up population who were in foster care on the date of outcomes data collection (45 CFR 1356.85(b)(3)(i)).";
	public static final String OUTCOMES_PARTICIPATION_FOSTERCARE_AND = "Contain outcomes information on at least 80 percent of youth in the follow-up population who were in foster care on the date of outcomes data collection (45 CFR 1356.85(b)(3)(i)); and";
	public static final String OUTCOMES_PARTICIPATION_DISCHARGED = "Contain outcomes information on at least 60 percent of youth in the follow-up population who were no longer in foster care on the date of outcomes data collection (45 CFR 1356.85(b)(3)(ii)).";
	public static final String OUTCOMES_COMMON_SUGGESTION ="Otherwise meets the standards at 45 CFR 1356.85(a) and (b) and all other NYTD requirements (45 CFR 1356.80 through 1356.86).";
//	public static final String FILE_ERROR_FREE_INFO_SAMPLED = "Contain 100 percent error-free information for the elements listed above that contained an unallowable percentage of errors.";
//	public static final String DATA_ERROR_FREE_INFO_SAMPLED = "Contain at least 90 percent error-free information for the elements listed above that contained an unallowable percentage of errors.";
	public static final String OUTCOMES_UNIVERSE_MSG_SAMPLED = "Contain outcome information or a reason explaining why there are no outcomes data for each youth in the follow-up population or sample as described at 45 CFR 1356.85(b)(2).";
	public static final String OUTCOMES_PARTICIPATION_FOSTERCARE_SAMPLED = "Contain outcomes information on at least 80 percent of youth in the follow-up population or sample who were in foster care on the date of outcomes data collection (45 CFR 1356.85(b)(3)(i)).";
	public static final String OUTCOMES_PARTICIPATION_FOSTERCARE_SAMPLED_AND = "Contain outcomes information on at least 80 percent of youth in the follow-up population or sample who were in foster care on the date of outcomes data collection (45 CFR 1356.85(b)(3)(i)); and";
	public static final String OUTCOMES_PARTICIPATION_DISCHARGED_SAMPLED = "Contain outcomes information on at least 60 percent of youth in the follow-up population or sample who were no longer in foster care on the date of outcomes data collection (45 CFR 1356.85(b)(3)(ii)).";
	public static final String OUTCOMES_COMMON_SUGGESTION_SAMPLED ="Otherwise meets the standards at 45 CFR 1356.85(a) and (b) and all other NYTD requirements (45 CFR 1356.80 through 1356.86).";
	public static final String OUTCOMES_UNIVERSE_MSG_SAMPLED_DATASTANDARD = "The state submitted a data file that did not contain at least outcomes data for the outcomes status element at 45 CFR 1356.83(g)(34) for each youth in the follow-up population or sample (45 CFR 1356.85(b)(2).";
	public static final String DISTRICT_OF_COLUMBIA = "District of Columbia";
	//Prashanth Task#26 -Constants to add punctuation and append words to compliance statements in penalty letters  
	public static final String PENALTY_LETTER_PUNCTUATION_PERIOD = ".";
	public static final String PENALTY_LETTER_PUNCTUATION_COLON = ";";
	public static final String PENALTY_LETTER_WORD_AND = "and";
	public static final String PENALTY_LETTER_WORD_OR = "or";
	public static final String MLS_QNAME_ID = "id";
	public static final String MLS_QNAME_FIRSTNAME = "firstname";
	public static final String MLS_QNAME_MIDDLEINITIAL = "middleinitial";
	public static final String MLS_QNAME_LASTNAME = "lastname";
	public static final String MLS_QNAME_PREFIX = "prefix";
	public static final String MLS_QNAME_SUFFIX = "suffix";
	public static final String MLS_QNAME_AGENCY = "agency";
	public static final String MLS_QNAME_DIVISION = "division";
	public static final String MLS_QNAME_TITLE = "title";
	public static final String MLS_QNAME_PHONE = "phone";
	public static final String MLS_QNAME_FAX = "fax";
	public static final String MLS_QNAME_EMAIL = "email";
	public static final String MLS_QNAME_ADDRESS1 = "address1";
	public static final String MLS_QNAME_ADDRESS2 = "address2";
	public static final String MLS_QNAME_CITY = "city";
	public static final String MLS_QNAME_STATE = "state";
	public static final String MLS_QNAME_ZIPCODE = "zipcode";
	public static final String MLS_QNAME_COUNTRY = "country";
	public static final String MLS_QNAME_NAME = "name";
	public static final String MLS_QNAME_NYTD_CONTACT = "nytdcontact";
	public static final String MLS_COMMISSIONER_TITLE = "Associate Commissioner";
	public static final String COMMISSIONER_TAG_PREFIX = "commissioner";
	public static final String MLS_FINANCIAL_SPECIALIST_TITLE = "Financial Management Specialist";
	public static final String FINANCIAL_MANAGER_TAG_PREFIX = "financial-manager";
	public static final String MLS_GRANT_SPECIALIST_TITLE = "Supervisory Grants Management Specialist";
	public static final String GRANT_MANAGER_TAG_PREFIX = "grant-manager";
	public static final String TOKEN_HEADER_NAME = "token";
	public static final String MLS_TOKEN = "penaltyLetter.webservice.token";
	public static final String PROPERTIES_FILE_PATH = "/config/systemConfig.properties";
	
	public static final String MLS_WEBSERVICE_RESPONSE_DEFAULT= "<contacts><statecontact><contact><id>0</id><firstname>[NAME]</firstname><middleinitial> </middleinitial><lastname> </lastname><agency>[AGENCY]</agency><division>[DIVISION]</division><title>[TITLE]</title><phone>[PHONE]</phone><fax>[FAX]</fax><email>[EMAIL]</email><address1>[ADDRESS1]</address1><address2>[ADDRESS2]</address2><city>[CITY]</city><state>[STATE]</state><zipcode>[ZIP]</zipcode><country>[COUNTRY]</country></contact></statecontact><regionalprogrammanagercontact><contact><id>0</id><firstname>[NAME]</firstname><middleinitial> </middleinitial><lastname> </lastname><agency>[AGENCY]</agency><division>[DIVISION]</division><title>[TITLE]</title><phone>[PHONE]</phone><fax>[FAX]</fax><email>[EMAIL]</email><address1>[ADDRESS1]</address1><address2>[ADDRESS2]</address2><city>[CITY]</city><state>[STATE]</state><zipcode>[ZIP]</zipcode><country>[COUNTRY]</country></contact></regionalprogrammanagercontact><regionalgrantofficercontact><contact><gmo_name>[GMO_NAME]</gmo_name><gmo_phone>[GMO_PHONE]</gmo_phone></contact></regionalgrantofficercontact></contacts>";
	
	// SPSS CODES -- Allowed Element value equivalents 
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	public static final String  YES = "yes";
	public static final String NO = "no";
	public static final String DECLINED ="declined";
	public static final String UNKNOWN = "unknown";
	public static final String NOT_APPLICABLE = "not applicable";
	public static final String DO_NOT_KNOW = "do not know";
	public static final String STR_77 = "77";
	public static final String STR_OUT_OF_RANGE = "78";
	public static final String STR_MISSING = "79";
	public static final String STR_0 = "0";
	public static final String STR_1 = "1";
	public static final String DEATH=  "death";
	public static final String INCAPACITATED=  "incapacitated";
	public static final String INCARCERATED=  "incarcerated";
	public static final String NOT_IN_SAMPLE=  "not in sample";
	public static final String PARENT_DECLINED=  "parent declined";
	public static final String PARTICIPATED=  "participated";
	public static final String RUNAWAY_MISSING=  "runaway missing";
	public static final String UNABLE_TO_LOCATE=  "unable to locate";
	public static final String ASSOCIATE=  "associate";
	public static final String BACHELOR=  "bachelor";
	public static final String HIGH_SCHOOL_GED=  "high school ged";
	public static final String HIGHER_DEGREE=  "higher degree";
	public static final String NONE_OF_THE_ABOVE=  "none of the above";
	public static final String VOCATIONAL_CERTIFICATE=  "vocational certificate";
	public static final String VOCATIONAL_LICENSE=  "vocational license";
	public static final String CENTRALIZED_UNIT =  "centralized unit";
	public static final String EDU_10=  "10";
	public static final String EDU_11=  "11";
	public static final String EDU_12=  "12";
	public static final String EDU_6=  "6";
	public static final String EDU_7=  "7";
	public static final String EDU_8=  "8";
	public static final String EDU_9=  "9";
	public static final String COLLEGE=  "college";
	public static final String POST_SECONDARY=  "post secondary";
	public static final String UNDER_6=  "under 6";
	public static final String OPEN_PARENTHESIS = "(";
	public static final String CLOSE_PARANTHESIS = ")";
	public static final String SPSS_NUMERIC ="numeric";
	public static final String SPSS_DATE ="date";
	public static final String SPSS_STRING ="string";
	public static final String FIELD_FILE_NUMBER ="File_Number";
	public static final String FIELD_SERVED ="Served";
	public static final String FIELD_BASELINE ="Baseline";
	public static final String FIELD_FOLLOWUP19 ="Follow-up 19";
	public static final String FIELD_FOLLOWUP21 ="Follow-up 21";
	public static final String FIELD_PREBUFFER ="PreBuffer";
	public static final String FIELD_POSTBUFFER ="PostBuffer";
	public static final String FIELD_COHORTFY ="Cohort_FY";
	public static final String FIELD_INSAMPLE ="In_Sample";
	public static final String FIELD_E2_LABEL ="Report Date";
	public static final String FIELD_E2 ="E2";
	public static final String FIELD_E3_LABEL ="Record Number";
	public static final String FIELD_E3 ="E3";
	public static final String FIELD_E4_LABEL ="Date of Birth";
	public static final String FIELD_E4 ="E4";
	public static final String FIELD_E35_LABEL ="Date of Outcome Data Collection";
	public static final String FIELD_E35 ="E35";
	public static final String STR_NULL = "null";
	public static final String NOT_APPLICABLE_ABBREV = "N/A";
	public static final String VAL_TAG_QNAME = "w:val";
	

	
	public static final  Map<String,Integer> e5_sex = ImmutableMap.<String,Integer>builder().put(Constants.MALE,1).put(Constants.FEMALE,2).build();
	public static final  Map<String,Integer> element_YES_NO = ImmutableMap.<String,Integer>builder().put(Constants.YES,1).put(Constants.NO,0).build();
	public static final  Map<String,Integer> element_YES_NO_UNKNOWN_DECLINED = ImmutableMap.<String,Integer>builder().put(Constants.YES,1).put(Constants.NO,0).put(Constants.DECLINED,2).put(Constants.UNKNOWN,3).build();
	public static final  Map<String,Integer> element_YES_NO_DECLINED = ImmutableMap.<String,Integer>builder().put(Constants.YES,1).put(Constants.NO,0).put(Constants.DECLINED,2).build();
	public static final  Map<String,Integer> element_YES_NO_NA_DECLINED = ImmutableMap.<String,Integer>builder().put(Constants.YES,1).put(Constants.NO,0).put(Constants.DECLINED,2).put(Constants.NOT_APPLICABLE,88).build();
	public static final  Map<String,Integer> element_YES_NO_NA_DNK_DECLINED = ImmutableMap.<String,Integer>builder().put(Constants.YES,1).put(Constants.NO,0).put(Constants.DECLINED,2).put(Constants.DO_NOT_KNOW,3).put(Constants.NOT_APPLICABLE,88).build();
	public static final  Map<String,Integer> element_YES_NO_DNK_DECLINED = ImmutableMap.<String,Integer>builder().put(Constants.YES,1).put(Constants.NO,0).put(Constants.DECLINED,2).put(Constants.DO_NOT_KNOW,3).build();
	public static final  Map<String,Integer> e34_Outcomes = ImmutableMap.<String,Integer>builder().put(Constants.PARTICIPATED,1).put(Constants.DECLINED,2).put(Constants.PARENT_DECLINED,3).put(Constants.INCAPACITATED,4)
			.put(Constants.INCARCERATED,5).put(Constants.RUNAWAY_MISSING,6).put(Constants.UNABLE_TO_LOCATE,7).put(Constants.DEATH,8).put(Constants.NOT_IN_SAMPLE,9).build();
	public static final  Map<String,Integer> e46_HigherEdu = ImmutableMap.<String,Integer>builder().put(Constants.HIGH_SCHOOL_GED,1).put(Constants.VOCATIONAL_CERTIFICATE,2).put(Constants.VOCATIONAL_LICENSE,3).put(Constants.ASSOCIATE,4)
			.put(Constants.BACHELOR,5).put(Constants.HIGHER_DEGREE,6).put(Constants.NONE_OF_THE_ABOVE,7).put(Constants.DECLINED,8).build();
	public static final  Map<String,Integer> e18_Edu = ImmutableMap.<String,Integer>builder().put(Constants.UNDER_6,0).put(Constants.EDU_6,6).put(Constants.EDU_7,7).put(Constants.EDU_8,8)
			.put(Constants.EDU_9,9).put(Constants.EDU_10,10).put(Constants.EDU_11,11).put(Constants.EDU_12,12).put(Constants.POST_SECONDARY,13).put(Constants.COLLEGE,14).build();
	/*public static final Map<String,String> string_fields = ImmutableMap.<String,String>builder().put(Constants.FIELD_FILE_NUMBER,Constants.SPSS_STRING)
			.put(Constants.FIELD_SERVED,Constants.SPSS_STRING).put(Constants.FIELD_BASELINE,Constants.SPSS_STRING).put(Constants.FIELD_FOLLOWUP19,Constants.SPSS_STRING)
			.put(Constants.FIELD_FOLLOWUP21,Constants.SPSS_STRING).put(Constants.FIELD_PREBUFFER,Constants.SPSS_STRING).put(Constants.FIELD_POSTBUFFER,Constants.SPSS_STRING)
			.put(Constants.FIELD_COHORTFY,Constants.SPSS_STRING).put(Constants.FIELD_INSAMPLE,Constants.SPSS_STRING).put(Constants.FIELD_E2,Constants.SPSS_STRING)
			.put(Constants.FIELD_E3,Constants.SPSS_STRING)
			.build();*/
	public static final Map<String,String> string_fields_LABEL = ImmutableMap.<String,String>builder().put(Constants.FIELD_FILE_NUMBER,Constants.SPSS_STRING)
			.put(Constants.FIELD_E2_LABEL,Constants.SPSS_STRING)
			.put(Constants.FIELD_E3_LABEL,Constants.SPSS_STRING)
			.build();
	public static final Map<String,String> string_fields = ImmutableMap.<String,String>builder().put(Constants.FIELD_FILE_NUMBER,Constants.SPSS_STRING)
			.put(Constants.FIELD_E2,Constants.SPSS_STRING)
			.put(Constants.FIELD_E3,Constants.SPSS_STRING)
			.build();
	public static final  Map<String,Integer> e15_FIPS = ImmutableMap.<String,Integer>builder().put(Constants.CENTRALIZED_UNIT,99999).build();
	public static final  Map<String,String> SPSS_FIELD_NAMES = ImmutableMap.<String,String>builder().put(Constants.FIELD_FILE_NUMBER,"FILENO").put("E1","STATE").put("E2","REPDATE").put("E3","RECNUMBR").put("E4","DOB").put("E5","SEX").put("E6","AMIAKN").put("E7","ASIAN").put("E8","BLKAFRAM").put("E9","HAWAIIPI").put("E10","WHITE").put("E11","RACEUNKN").put("E12","RACEDCLN").put("E13","HISORGIN").put("E14","FCSTATSV").put("E15","LCLFIPSSV").put("E16","TRIBESV").put("E17","DELINQNTSV").put("E18","EDLEVLSV").put("E19","SPECEDSV").put("E20","ILNASV").put("E21","ACSUPPSV").put("E22","PSEDSUPPSV").put("E23","CAREERSV").put("E24","EMPLYTRSV").put("E25","BUDGETSV").put("E26","HOUSEDSV").put("E27","HLTHEDSV").put("E28","FAMSUPPSV").put("E29","MENTORSV").put("E30","SILSV").put("E31","RMBRDFASV").put("E32","EDUCFINASV").put("E33","OTHRFINASV").put("E34","OUTCMRPT").put("E35","OUTCMDTE").put("E36","OUTCMFCS").put("E37","CURRFTE").put("E38","CURRPTE").put("E39","EMPLYSKLLS").put("E40","SOCSECRTY").put("E41","EDUCAID").put("E42","PUBFINAS").put("E43","PUBFOODAS").put("E44","PUBHOUSAS").put("E45","OTHRFINAS").put("E46","HIGHEDCERT").put("E47","CURRENROLL").put("E48","CNCTADULT").put("E49","HOMELESS").put("E50","SUBABUSE").put("E51","INCARC").put("E52","CHILDREN").put("E53","MARRIAGE").put("E54","MEDICAID").put("E55","OTHRHLTHIN").put("E56","MEDICALIN").put("E57","MENTLHLTHIN").put("E58","PRESCRIPIN")
.build();
}
