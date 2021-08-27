package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.Transmission;

public interface LoggingService {

    public void logPenaltyLetterGeneration(Transmission transmission, SiteUser currentUser);
}
