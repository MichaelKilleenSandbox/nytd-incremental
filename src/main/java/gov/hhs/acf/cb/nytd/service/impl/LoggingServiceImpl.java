package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.GenerateLetterLogDAO;
import gov.hhs.acf.cb.nytd.models.GenerateLetterLog;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.service.LoggingService;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class LoggingServiceImpl implements LoggingService {

    @Getter @Setter private GenerateLetterLogDAO generateLetterLogDAO;

    @Override
    public void logPenaltyLetterGeneration(Transmission transmission, SiteUser currentUser) {

        generateLetterLogDAO.save(GenerateLetterLog.builder()
                .transmission(transmission)
                .transmissionType(transmission.getTransmissionType())
                .complianceStatus(transmission.getComplianceStatus())
                .reportingPeriod(transmission.getReportingPeriod())
                .siteUser(currentUser)
                .state(transmission.getState())
                .dateTimestamp(LocalDateTime.now())
                .build());

    }
}
