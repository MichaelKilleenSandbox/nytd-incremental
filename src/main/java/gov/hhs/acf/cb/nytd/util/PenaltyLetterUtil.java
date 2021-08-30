package gov.hhs.acf.cb.nytd.util;

import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class for Penalty Letters
 */
public class PenaltyLetterUtil
{

	// default logger
	protected Logger log = Logger.getLogger(getClass());
	private static final Map<String,String> suggestionsMap = new HashMap<>();
	private static final Map<String,String> regions = new HashMap<>();
	private static final Map<String,String> regionIDs = new HashMap<>();
	
	public void writeWhiteLine(XMLStreamWriter out, int count)
	{
		try {
			for(int i=0;i<count;i++)
			{
				out.writeStartElement("w:p");// <w:p>
					out.writeAttribute("wsp:rsidR", "008E4E4C");
					out.writeAttribute("wsp:rsidRDefault", "008E4E4C");
					out.writeAttribute("wsp:rsidP", "008E4E4C");
						out.writeStartElement("w:pPr");
							out.writeStartElement("w:rPr");
								out.writeStartElement("w:sz");
									out.writeAttribute(Constants.VAL_TAG_QNAME, "24");
								out.writeEndElement();
							out.writeEndElement();
						out.writeEndElement();
				out.writeEndElement();
			
			}
			
		} catch (XMLStreamException e)
			{
				log.error(e.getMessage(), e);
		}
	}

	/**
	 * This will create the bulleted list in the penalty letter
	 * at zero indentation
	 *
	 * @param out - XML Stream Writer
	 * @param bulletList - list of bullets
	 */
	public void createBulletedList(XMLStreamWriter out, List<String> bulletList) {
		createBulletedList(out, bulletList, "0");
	}

	/**
	 * This will create the bulleted list in the penalty letter
	 * 
	 * @param out - XML Stream Writer
	 * @param bulletList - list of bullets
	 * @param level  - list level
	 */
	public void createBulletedList(XMLStreamWriter out, List<String> bulletList, String level)
	{
		List<String> bulletItems = bulletList;
		for (String bulletText : bulletItems)
		{
			try
			{
				out.writeStartElement("w:p");// <w:p>
				out.writeAttribute("wsp:rsidR", "00554C74");
				out.writeAttribute("wsp:rsidRDefault", "00554C74");
				out.writeAttribute("wsp:rsidP", "00554C74");
				out.writeStartElement("w:pPr");// <w:pPr>
				out.writeStartElement("w:listPr");// <w:listPr>
				out.writeStartElement("w:ilvl");// <w:ilvl>
				out.writeAttribute(Constants.VAL_TAG_QNAME, level);
				out.writeEndElement(); // </w:ilvl>
				out.writeStartElement("w:ilfo");// <w:ilfo>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "2");
				out.writeEndElement(); // </w:ilfo>
				out.writeStartElement("wx:t");// <wx:t>
				out.writeAttribute("wx:val", "�");
				out.writeEndElement(); // </w:t>
				out.writeStartElement("wx:font");// <wx:font>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "Symbol");
				out.writeEndElement();// </wx:font>
				out.writeEndElement();// </w:listPr>
				out.writeEndElement();// </w:pPr>
				out.writeStartElement("w:r");// <w:r>
				out.writeStartElement("w:rPr");// <w:rPr>
				out.writeStartElement("w:sz"); // <w:sz>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "24");// <w:sz w:val="24">
				out.writeEndElement();// </w:sz>
				out.writeStartElement("w:sz-cs"); //<w:sz-cs w:val="24">
				out.writeAttribute(Constants.VAL_TAG_QNAME, "24"); //<w:sz-cs w:val="24"> 
				out.writeEndElement();//</w:sz-cs>
				out.writeEndElement();// </w:rPr>

				out.writeStartElement("w:t");// <w:t>
				// <w:t>added bulleted item generically</w:t>
				// this text will be replaced with the text from the article/law
				out.writeCharacters(bulletText);
				out.writeEndElement();// </w:t>
				out.writeEndElement();// </w:r>
				out.writeEndElement();// </w:p>

			}
			catch (XMLStreamException e)
			{
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * This will create the detailed bulleted list in the penalty letter
	 * 
	 * @param out
	 * @param bulletList
	 */
	public void createDataElementList(XMLStreamWriter out, List<String> bulletList)
	{
		List<String> bulletItems = bulletList;
		for (String bulletText : bulletItems)
		{
			try
			{

				out.writeStartElement("w:p");// <w:p>
				out.writeStartElement("w:pPr");// <w:pPr>
				out.writeStartElement("w:pStyle");// <w:pStyle>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "NoSpacing");
				out.writeEndElement(); // </w:pStyle>
				out.writeCharacters("\n");
				out.writeStartElement("w:tabs");// <w:tabs>
				out.writeStartElement("w:tab");
				out.writeAttribute(Constants.VAL_TAG_QNAME, "left");
				out.writeAttribute("w:pos", "3600");
				out.writeEndElement(); // </w:tab>
				out.writeCharacters("\n");
				out.writeEndElement(); // </w:tabs>
				out.writeCharacters("\n");
				out.writeStartElement("w:ind");// <wx:t>
				out.writeAttribute("w:left", "360");
				out.writeEndElement(); // </w:t>
				out.writeCharacters("\n");
				out.writeStartElement("w:rPr");// <w:rPr>
				out.writeStartElement("w:rFonts"); // <w:rFonts>
				out.writeAttribute("w:ascii", "Times New Roman"); // <w:rFonts
				// w:ascii="Times New Roman">
				out.writeAttribute("w:h-ansi", "Times New Roman"); // <w:rFonts
				// w:ascii="Times New Roman"
				// w:h-ansi="Times New Roman">
				out.writeEndElement(); // </w:rFonts>
				out.writeCharacters("\n");
				out.writeStartElement("wx:font"); // <wx:font>
				out.writeAttribute("wx:val", "Times New Roman"); // <wx:font
				// wx:val="Times New Roman">
				out.writeEndElement(); // </wx:font>
				out.writeCharacters("\n");
				
				out.writeStartElement("w:sz"); // <w:sz>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "24");// <w:sz w:val="24">
				out.writeEndElement();// </w:sz>
				out.writeCharacters("\n");
				out.writeStartElement("w:sz-cs"); // <w:sz>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "24");// <w:sz w:val="24">
				out.writeEndElement();
				out.writeCharacters("\n");
				out.writeEndElement();// </w:rPr>
				out.writeCharacters("\n");
				out.writeEndElement(); // </w:pPr>
				out.writeCharacters("\n");
				out.writeStartElement("w:r"); // <w:r>
				out.writeAttribute("wsp:rsidRPr", "0034517C");
				out.writeStartElement("w:rPr");// <w:rPr>
				out.writeStartElement("w:rFonts"); // <w:rFonts>
				out.writeAttribute("w:ascii", "Times New Roman"); // <w:rFonts
				// w:ascii="Times New Roman">
				out.writeAttribute("w:h-ansi", "Times New Roman"); // <w:rFonts
				// w:ascii="Times New Roman"
				// w:h-ansi="Times New Roman">
				out.writeEndElement(); // </w:rFonts>
				out.writeCharacters("\n");
				out.writeStartElement("wx:font"); // <wx:font>
				out.writeAttribute("wx:val", "Times New Roman"); // <wx:font
				// wx:val="Times New Roman">
				out.writeEndElement(); // </wx:font>
				out.writeCharacters("\n");
				out.writeStartElement("w:sz"); // <w:sz>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "24");// <w:sz w:val="24">
				out.writeEndElement();// </w:sz>
				out.writeCharacters("\n");
				out.writeStartElement("w:sz-cs"); // <w:sz>
				out.writeAttribute(Constants.VAL_TAG_QNAME, "24");// <w:sz w:val="24">
				out.writeEndElement();
				out.writeCharacters("\n");
				out.writeEndElement();// </w:rPr>
				out.writeCharacters("\n");
				out.writeStartElement("w:t");// <w:t>
				// <w:t>added bulleted item generically</w:t>
				// this text will be replaced with the text from the article/law
				out.writeCharacters(bulletText);
				out.writeEndElement();// </w:t>
				out.writeCharacters("\n");
				out.writeEndElement();// </w:r>
				out.writeCharacters("\n");
				out.writeEndElement();// </w:p>

			}
			catch (XMLStreamException e)
			{
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Returns the date in format MM-dd-yyyy
	 * 
	 * @return
	 */
	public String getDate()
	{
		Calendar now = Calendar.getInstance();
		Date date = now.getTime();
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");

		return dateFormat.format(date);
	}

	/**
	 * Creates the file name for the Penalty Letter
	 * Eg.VA_NYTD_2013A_Initial_Compliance_04042013.doc
	 * 
	 * @param servletRequest
	 * @return
	 */
	public String getFileName(HttpServletRequest servletRequest)
	{
		String stateAbbr = servletRequest.getParameter("stateAbbr");
		String letterType = servletRequest.getParameter("letterType");
		String reportPeriodName = servletRequest.getParameter("reportPeriodName");
		// State_NYTD_ReportPeriod_Intial/Final_Compliance_MMDDYYYY.doc
		String date = getDate();
		String fileName = stateAbbr + "_NYTD_" + reportPeriodName + "_" + letterType + "_Compliance_" + date;
		return fileName;
	}
	
	/*
	 *	Returns the Penalty Template number 1-6 based on the amount of the penalty and type
	 * 
	 * 
	 */
	public int getPenaltyLetterTemplateNumber(Double penaltyAmt, Properties properties, String transmissionType,
			HttpServletRequest servletRequest)
	{
		int templateNumber = 0;
		// Get the Penalty Amount from the request
		if (StringUtils.isNotBlank(servletRequest.getParameter("penaltyAmt")))
		{
			penaltyAmt = Double.valueOf(servletRequest.getParameter("penaltyAmt"));
		}

		// if the transmission is regular
		if (StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION))
		{
			// if the letter type is initial
			if (StringUtils.equals(servletRequest.getParameter("letterType"),
					Constants.PENALTY_LETTER_TYPE_INITIAL))
			{
				// if the penalty amount is 0.00 then pick the Regular Initial
				// Compliant Template
				if (penaltyAmt == 0.00)
				{
					/*fileName = properties
							.getProperty("penaltyLetter.Template.Regular.Initial.Determination.Compliant");*/
					templateNumber = 1;
				}
				// if the penalty amount > 0.00 then pick the Regular Initial
				// Non-Compliant Template
				else if (penaltyAmt > 0.00)
				{
					/*fileName = properties
							.getProperty("penaltyLetter.Template.Regular.Initial.Determination.NonCompliant.With.Data");*/
					templateNumber = 2;
				}
			}
			// if the letter type is final
			if (StringUtils.equals(servletRequest.getParameter("letterType"),
					Constants.PENALTY_LETTER_TYPE_FINAL))
			{
				// select the Final and Non Compliant template with no corrected
				// file
				/*fileName = properties
						.getProperty("penaltyLetter.Template.Regular.Final.Determination.NonCompliant.No.Corrected");*/
				templateNumber = 4;
			}
		}
		// if the transmission type is Corrected
		else if (StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION))
		{

			if (penaltyAmt > 0.00)
			{
				/*fileName = properties
						.getProperty("penaltyLetter.Template.Corrected.Final.Determination.NonCompliant.With.Corrected");*/
				templateNumber = 5;
			}
			else if (penaltyAmt == 0.00)
			{
				/*fileName = properties
						.getProperty("penaltyLetter.Template.Corrected.Final.Determination.Compliant");*/
				templateNumber = 6;
			}
		}
		return templateNumber;
	}

	/**
	 * Returns the template name to be selected based on the penalty Amount and
	 * the transmission type.
	 * 
	 * @param penaltyAmt
	 * @param properties
	 * @return
	 */
	public String getPenaltyLetterTemplate(Double penaltyAmt, Properties properties, String transmissionType,
			HttpServletRequest servletRequest)
	{
		String fileName = "";
		// Get the Penalty Amount from the request
		if (StringUtils.isNotBlank(servletRequest.getParameter("penaltyAmt")))
		{
			penaltyAmt = Double.valueOf(servletRequest.getParameter("penaltyAmt"));
		}

		// if the transmission is regular
		if (StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION))
		{
			// if the letter type is initial
			if (StringUtils.equals(servletRequest.getParameter("letterType"),
					Constants.PENALTY_LETTER_TYPE_INITIAL))
			{
				// if the penalty amount is 0.00 then pick the Regular Initial
				// Compliant Template
				if (penaltyAmt == 0.00)
				{
					fileName = properties
							.getProperty("penaltyLetter.Template.Regular.Initial.Determination.Compliant");
				}
				// if the penalty amount > 0.00 then pick the Regular Initial
				// Non-Compliant Template
				else if (penaltyAmt > 0.00)
				{
					fileName = properties
							.getProperty("penaltyLetter.Template.Regular.Initial.Determination.NonCompliant.With.Data");
				}
			}
			// if the letter type is final
			if (StringUtils.equals(servletRequest.getParameter("letterType"),
					Constants.PENALTY_LETTER_TYPE_FINAL))
			{
				// select the Final and Non Compliant template with no corrected
				// file
				fileName = properties
						.getProperty("penaltyLetter.Template.Regular.Final.Determination.NonCompliant.No.Corrected");
			}
		}
            	// if the transmission type is Corrected
		else if (StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION))
		{

			if (penaltyAmt > 0.00)
			{
				fileName = properties
						.getProperty("penaltyLetter.Template.Corrected.Final.Determination.NonCompliant.With.Corrected");
			}
			else if (penaltyAmt == 0.00)
			{
				fileName = properties
						.getProperty("penaltyLetter.Template.Corrected.Final.Determination.Compliant");
			}
		}
		return fileName;
	}
        
        /*
         * Returns the template name to be selected based on the penalty Amount and the transmission type 
         * and letter type for multiple penalty letters download without servlet request.
         * 
         * @param penaltyAmt
         * @param properties
         * @param transmissionType
         * @param letterType
         * @return fileName as String
         */
	public String getPenaltyLetterTemplate(Double penaltyAmt, Properties properties, String transmissionType, String letterType) {
            String fileName = "";
            // if the transmission is regular
            if (StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION)) {
                // if the letter type is initial
                if (StringUtils.equals(letterType, Constants.PENALTY_LETTER_TYPE_INITIAL)){
                    // if the penalty amount is 0.00 then pick the Regular Initial Compliant Template
                    if (penaltyAmt == 0.00) {
                        fileName = properties.getProperty("penaltyLetter.Template.Regular.Initial.Determination.Compliant");
                    }
                    // if the penalty amount > 0.00 then pick the Regular Initial Non-Compliant Template
                    else if (penaltyAmt > 0.00) {
                        fileName = properties.getProperty("penaltyLetter.Template.Regular.Initial.Determination.NonCompliant.With.Data");
                    }
                }
                // if the letter type is final
                if (StringUtils.equals(letterType, Constants.PENALTY_LETTER_TYPE_FINAL)) {
                        // select the Final and Non Compliant template with no corrected file
                    fileName = properties.getProperty("penaltyLetter.Template.Regular.Final.Determination.NonCompliant.No.Corrected");
                }
            // if the transmission type is Corrected
            } else if (StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION)) {
                if (penaltyAmt > 0.00) {
                    fileName = properties.getProperty("penaltyLetter.Template.Corrected.Final.Determination.NonCompliant.With.Corrected");
                } else if (penaltyAmt == 0.00) {
                    fileName = properties.getProperty("penaltyLetter.Template.Corrected.Final.Determination.Compliant");
                }
            }
            
            return fileName;
	}
        
        /*
         * Returns the Penalty Template number 1-6 based on the amount of the penalty, transmission type and letter type without servelet request
         * 
         * @param penaltyAmt
         * @param transmissionType
         * @param letterType
         * @return templateNumber as int
         */
        public int getPenaltyLetterTemplateNumber(Double penaltyAmt, String transmissionType, String letterType) {
            int templateNumber = 0;

            // if the transmission is regular
            if (StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION)) {
                // if the letter type is initial
                if (StringUtils.equals(letterType, Constants.PENALTY_LETTER_TYPE_INITIAL)) {
                    // if the penalty amount is 0.00 then pick the Regular Initial Compliant Template
                    if (penaltyAmt == 0.00) {
                        templateNumber = 1;
                    // if the penalty amount > 0.00 then pick the Regular Initial Non-Compliant Template
                    } else if (penaltyAmt > 0.00) {
                        templateNumber = 2;
                    }  
                }
                // if the letter type is final
                if (StringUtils.equals(letterType, Constants.PENALTY_LETTER_TYPE_FINAL)) {
                    // select the Final and Non Compliant template with no corrected file
                    templateNumber = 4;
                }
            // if the transmission type is Corrected
            } else if (StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION))  {
                if (penaltyAmt > 0.00) {
                    templateNumber = 5;
                } else if (penaltyAmt == 0.00) {
                    templateNumber = 6;
                }
            }
            
            return templateNumber;
	}
	
	public static Map<String,String> getNonComplianceSuggestionsMap()
	{
		if(suggestionsMap != null && suggestionsMap.isEmpty())
		{
			suggestionsMap.put("File Error free Information", Constants.FILE_ERROR_FREE_INFO);
			suggestionsMap.put("File Submission Standard Timely Data OR File Format", Constants.FILE_SUBMISSION_STANDARD_TIMELY_DATA_OR_FILE_FORMAT);
			//suggestionsMap.put("File Submission Standard File Format", Constants.FILE_SUBMISSION_STANDARD_FILE_FORMAT);
			
			suggestionsMap.put("Data Error free Information", Constants.DATA_ERROR_FREE_INFO);
			suggestionsMap.put("Outcomes Universe",Constants.OUTCOMES_UNIVERSE_MSG);
			suggestionsMap.put("Outcomes Participation - Discharged Youth",Constants.OUTCOMES_PARTICIPATION_DISCHARGED);
			suggestionsMap.put("Outcomes Participation - Foster Care Youth",Constants.OUTCOMES_PARTICIPATION_FOSTERCARE);
			suggestionsMap.put("Outcomes Participation - Foster Care Youth 2",Constants.OUTCOMES_PARTICIPATION_FOSTERCARE_AND);
	//		suggestionsMap.put("File Error free Information_SAMPLED", Constants.FILE_ERROR_FREE_INFO_SAMPLED);
	//		suggestionsMap.put("Data Error free Information_SAMPLED", Constants.DATA_ERROR_FREE_INFO_SAMPLED);
			suggestionsMap.put("Outcomes Universe_SAMPLED",Constants.OUTCOMES_UNIVERSE_MSG_SAMPLED);
			suggestionsMap.put("Outcomes Participation - Discharged Youth_SAMPLED",Constants.OUTCOMES_PARTICIPATION_DISCHARGED_SAMPLED);
			suggestionsMap.put("Outcomes Participation - Foster Care Youth_SAMPLED",Constants.OUTCOMES_PARTICIPATION_FOSTERCARE_SAMPLED);
			suggestionsMap.put("Outcomes Participation - Foster Care Youth_SAMPLED 2",Constants.OUTCOMES_PARTICIPATION_FOSTERCARE_SAMPLED_AND);
		}
			
		return suggestionsMap;
	}
	
	public static Map<String,String> getRegions()
	{
		if(regions != null && regions.isEmpty())
		{
				regions.put("Region 1", "RegionI");
				regions.put("Region 2", "RegionII");
				regions.put("Region 3", "RegionIII");
				regions.put("Region 4", "RegionIV");
				regions.put("Region 5", "RegionV");
				regions.put("Region 6", "RegionVI");
				regions.put("Region 7", "RegionVII");
				regions.put("Region 8", "RegionVIII");
				regions.put("Region 9", "RegionIX");
				regions.put("Region 10","RegionX");
			/*regions.put("Region 1", "I");
			regions.put("Region 2", "II");
			regions.put("Region 3", "III");
			regions.put("Region 4", "IV");
			regions.put("Region 5", "V");
			regions.put("Region 6", "VI");
			regions.put("Region 7", "VII");
			regions.put("Region 8", "VIII");
			regions.put("Region 9", "IX");
			regions.put("Region 10","X");*/
		}
		return regions;
	}
	
	public static Map<String,String> getRegionIDs()
	{
		if(regionIDs != null && regionIDs.isEmpty())
		{
				
			regionIDs.put("Region 1", "1");
			regionIDs.put("Region 2", "2");
			regionIDs.put("Region 3", "3");
			regionIDs.put("Region 4", "4");
			regionIDs.put("Region 5", "5");
			regionIDs.put("Region 6", "6");
			regionIDs.put("Region 7", "7");
			regionIDs.put("Region 8", "8");
			regionIDs.put("Region 9", "9");
			regionIDs.put("Region 10","10");
			/*regionIDs.put("Region 1", "I");
			regionIDs.put("Region 2", "II");
			regionIDs.put("Region 3", "III");
			regionIDs.put("Region 4", "IV");
			regionIDs.put("Region 5", "V");
			regionIDs.put("Region 6", "VI");
			regionIDs.put("Region 7", "VII");
			regionIDs.put("Region 8", "VIII");
			regionIDs.put("Region 9", "IX");
			regionIDs.put("Region 10","X");*/
		}
		return regionIDs;
	}
        
        public class SmartXmlTagWriter {
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
    
        public interface TagWriteCommand {
		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException;
	}
        
        public class StartTagWriter implements TagWriteCommand {
		private String qName;

		public StartTagWriter(String qName) {
			this.qName = qName;
		}

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeStartElement(qName);
		}
	}

	public class EndTagWriter implements TagWriteCommand {

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeEndElement();
		}
	}

	public class TagAttrbWriter implements TagWriteCommand {
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

	public class TagContentWriter implements TagWriteCommand {
		private String content;

		public TagContentWriter(String content) {
			this.content = content;
		}

		public void write(XMLStreamWriter xmlStrWriter) throws XMLStreamException {
			xmlStrWriter.writeCharacters(content);
		}
	}
        
        /**
        * find .doc files in the directory
        * @param dirName as String
        * @return array list of the .doc filename as File[]
        */
        public File[] findDocFilesInDir(String dirName){
           try {
               File dir = new File(new URI(dirName));
               return dir.listFiles(new FilenameFilter() { 
                   public boolean accept(File dir, String filename) {
                       return filename.endsWith(".doc"); 
                   }
               });
           } catch (URISyntaxException e) {
               log.error(e.getMessage());
           }
           return new File[0];
        }
        
        /**
        * Determine if user can generate penalty letter given transmission type
        * @param transmissionType as String
        * @param user as SiteUser
        * @return boolean value
        */
        public static boolean canGeneratePenaltyLetter(String transmissionType, SiteUser user) {
            return (user.isSecondaryStateRoleManager()
                    || user.isSecondaryStateRoleStateAuthorized()
                    || user.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS))
                &&
                    (StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION)
                    || StringUtils.equals(transmissionType, Constants.CORRECTED_TRANSMISSION));
        }
        
        /**
        * Determine if user can generate initial penalty letter given transmission type
        * @param transmissionId as String
        * @param user as SiteUser
        * @param transmissionService as TransmissionServiceP3
        * @return boolean value
        */
        public static boolean canGenerateInitialLetter(String transmissionId, SiteUser user, TransmissionServiceP3 transmissionService) {
            Transmission trans = transmissionService.getTransmission(Long.parseLong(transmissionId));
            String transmissionType = trans.getTransmissionType().getName();
            if (canGeneratePenaltyLetter(transmissionType, user)){
                return user.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS)
                        &&
                        StringUtils.equals(transmissionType, Constants.REGULAR_TRANSMISSION);
            }
            return false;
        }
        
        /**
        * Determine if user can generate final penalty letter given transmission id
        * @param transmissionId as String
        * @param user as SiteUser
        * @param transmissionService as TransmissionServiceP3
        * @return boolean value
        */
        public static boolean canGenerateFinalLetter(String transmissionId, SiteUser user, TransmissionServiceP3 transmissionService) {
            Transmission trans = transmissionService.getTransmission(Long.parseLong(transmissionId));
            String transmissionType = trans.getTransmissionType().getName();
            if (canGeneratePenaltyLetter(transmissionType, user)){
                return user.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS);
            }
            return false;
        }
	
}
