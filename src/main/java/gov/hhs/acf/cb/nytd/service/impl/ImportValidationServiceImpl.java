package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.ReportingPeriodDAO;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.NytdError;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.service.ComplianceService;
import gov.hhs.acf.cb.nytd.service.ImportValidationService;
import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ImportValidationServiceImpl extends BaseServiceImpl implements ImportValidationService {

    @Getter @Setter
    private TransmissionServiceP3 transmissionServiceP3;
    @Getter @Setter
    private StateDAO stateDAO;
    @Getter @Setter
    private ReportingPeriodDAO reportingPeriodDAO;
    @Getter @Setter
    private ComplianceService complianceService;

    @Override
    /**
     * Check the file name transmitted is a correct type. The logic is:
     * I should be allowed to only submit a regular or corrected file for the respective reporting period
     * i.e., regular file for the current reporting period and corrected file for the previous reporting period.
     * I should not be allowed to submit a subsequent file for the current regular reporting period.
     * I should not be allowed to submit a subsequent file for the current corrected reporting period.
     * I should not be allowed to submit a corrected file for the current regular reporting period.
     * I should not be allowed to submit a regular file for the past reporting period in the current regular reporting period.
     * I should not be allowed to submit a corrected file for the old reporting period in the current corrected reporting period.
     * Additionally, logic includes extended due date for particular state and reporting period.
     *
     * @param String file name
     * @return boolean valid or not
     */
    public boolean isValidFileType(String fileName) {

            boolean isValid = false;

            if (fileName.length() != 38) { //valid file name length
                return false;
            }

            String stateAbbreviation = fileName.substring(12, 14);
            Optional<State> stateByAbbreviation = stateDAO.findStateByAbbreviation(stateAbbreviation);
            if(!stateByAbbreviation.isPresent()) {
                return false;
            }
            State state = stateByAbbreviation.get();

            Long stateId = state.getId();
            String fileType = fileName.substring(21, 22);
            String reportYear = fileName.substring(16, 20);
            String reportPeriod = fileName.substring(15, 16);
            String reportingPeriod = reportYear + reportPeriod;
            Long reportingPeriodId = reportingPeriodDAO.getReportingPeriodIdByName(reportingPeriod);
            boolean hasActiveTransmissionForThisReportingPeriod =
                    transmissionServiceP3.stateHasActiveTransmission(state,reportingPeriodId);

            // check extended due date for a state
            ReportingPeriod reportingPeriodRegular = transmissionServiceP3.getCurrentReportingPeriodForState(stateId, "Regular", reportingPeriodId);
            String extendedRegularReportPeriod = reportingPeriodRegular.getName();

            ReportingPeriod reportingPeriodCorrected = transmissionServiceP3.getCurrentReportingPeriodForState(stateId, "Corrected", reportingPeriodId);
            String extendedCorrectedReportPeriod = reportingPeriodCorrected.getName();

            switch (fileType) {
                case "R": //Regular

                    if (!hasActiveTransmissionForThisReportingPeriod &&
                            (reportingPeriodId.compareTo(reportingPeriodRegular.getId()) == 0 ||
                                    reportingPeriodId.compareTo(reportingPeriodCorrected.getId())== 0)) {
                        isValid = true;
                    }
                    break;
                case "C": //Corrected
                    if (reportingPeriod.equals(extendedCorrectedReportPeriod)) {
                        isValid = true;
                    }
                    break;
                case "S": //Subsequent
                    if (!reportingPeriod.equals(extendedRegularReportPeriod) &&
                            !reportingPeriod.equals(extendedCorrectedReportPeriod)) {
                        isValid = true;
                    }
                    break;
                default:
            }

            if(log.isDebugEnabled()) {
                log.debug("State abbreviation in file name: " + stateAbbreviation);
                log.debug("stateId: " + stateId);
                log.debug("File type in file name: " + fileType);
                log.debug("Report period in file name: " + reportingPeriod);
                log.debug("reportingPeriodId: " + reportingPeriodId);
                log.debug("extendedRegularReportPeriod: " + extendedRegularReportPeriod);
                log.debug("extendedCorrectedReportPeriod: " + extendedCorrectedReportPeriod);
            }

            return isValid;
        }

    public boolean isNotWellFormedXML(Transmission trans) {
        boolean isNotWellFormed = false;
        List<NytdError> fileSubmissionErrors =
                complianceService.getErrorsForCategories(trans.getId(),
                        complianceService.getFileSubmissionStandardsCategories());
        Iterator<NytdError> errorItr = fileSubmissionErrors.iterator();

        while (errorItr.hasNext()) {
            NytdError error = errorItr.next();
            if (error.getComplianceCategory().getName().equalsIgnoreCase("File Format")
                    && error.getProblemDescription().getId() == 4) {
                isNotWellFormed = true;
                break;
            }
        }
        return isNotWellFormed;
    }
    }

