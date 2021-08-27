package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.Transmission;

public interface ImportValidationService {

    boolean isValidFileType(String fileName);

    boolean isNotWellFormedXML(Transmission trans);
}
