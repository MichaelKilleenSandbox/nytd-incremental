package gov.hhs.acf.cb.nytd.service;

import java.io.File;
import java.util.List;

/**
 * Service to process file transmitted to NYTD system.
 * User: 13873
 * Date: Jun 15, 2010
 */
public interface ImportService extends BaseService {
    /**
     * Process a file transmitted.
     *
     * @param xmlFile transmitted file
     * @return list of object
     * @throws TransmissionException
     */
    List<Object> processFile(File xmlFile) throws TransmissionException;

    /**
     * Process rules for a transmitted file
     *
     * @param dto ImportDTO
     * @return list of object
     * @throws TransmissionException
     */
    List<Object> processRules(ImportDTO dto) throws TransmissionException;

    /**
     * Handle a file format error on a file transmitted.
     *
     * @param ex FileFormatException
     * @return file processed
     */
    File handleFileFormatError(FileFormatException ex);

    /**
     * Handle a processing error on a file transmitted.
     *
     * @param ex TransmissionException
     * @return file processed
     */
    File handleProcessingError(TransmissionException ex);
}
