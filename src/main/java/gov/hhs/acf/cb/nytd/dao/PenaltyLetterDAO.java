package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.actions.penalty.PenaltySearch;
import gov.hhs.acf.cb.nytd.models.PenaltyLettersMetadata;

import java.util.List;

/**
 * Data Access Objects class related to penalty letter generation.
 */
public interface PenaltyLetterDAO {

    /**
    * Get the list of PenaltyLettersMetadata available to download. 
    * @return List<PenaltyLettersMetadata>
    */
    public List<PenaltyLettersMetadata> getPenaltyLetterMetadata();
    
    /**
    * Save the PenaltyLettersMetadata to the database table.
    * @param metadata as PenaltyLettersMetadata
    * @return PenaltyLettersMetadata
    */
    public PenaltyLettersMetadata savePenaltyLettersMetadata(PenaltyLettersMetadata metadata);
    
    /**
    * Search transmissions on generate penalty letters for download.
    * @param search as PenaltySearch
    * @return PenaltySearch
    */
    public PenaltySearch searchGeneratePenaltyLetters(PenaltySearch search);

}
