package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.penalty.PenaltySearch;
import gov.hhs.acf.cb.nytd.models.PenaltyLettersMetadata;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service class for PenaltyLetters
 */
public interface PenaltyLetterService {

    /**
     * Returns the hash map containing State and Region after marshalling
     * the response returned from the MLS Webservice 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public Map<String, String> getStateRegionContactInfoMap(String stateName,String regionName, String webServiceURL) throws ParserConfigurationException, SAXException;

    /**
    * Parse and write penalty letter contents based on transmission id and letter type
    * 
    * @param transmissionId as Long
    * @param letterType as String (Final | Initial)
    * @throws FileNotFoundException
    * @throws IOException
    * @throws SAXException
    * @throws ParserConfigurationException 
    * @throws XMLStreamException
    * @throws FactoryConfigurationError
    * @return null
    */
    String parseAndWritePenaltyLetter(Long transmissionId, String letterType) throws IOException, SAXException,
                ParserConfigurationException, XMLStreamException, FactoryConfigurationError;
    
    /**
    * Zip up penalty letters and delete the letters after zipping.
    * 
    * @param srcFiles as List<String>
    * @param zipFile as File
    * @throws IOException
    */
    void zipPenaltyLetters(List<String> srcFiles, File zipFile) throws IOException;
    
    /**
    * Return a list of PenaltyLettersMetadata objects representing 
    * previously submitted Penalty letters generation.
    *
    * @return list of PenaltyLettersMetadata
    */
    List<PenaltyLettersMetadata> getPlMetadata();
    
    /**
    * Save PenaltyLettersMetadata when job is executed to generate multiple penalty letters.
    * 
    * @param metadata as PenaltyLettersMetadata
    * @param user as SiteUser
    */
    void savePenaltyLettersMetadata(PenaltyLettersMetadata metadata, SiteUser user);

    /**
    * Search transmissions based on penalty search constructed on generate penalty letters action
    * 
    * @param search as PenaltySearch
    * @return PenaltySearch
    */
    PenaltySearch searchGeneratePenaltyLetters(PenaltySearch search);
}
