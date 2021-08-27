package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;

import java.util.List;

/**
 * This service handles operations related to extended due date.
 */
public interface ExtendedDueDateService extends BaseService {

    /**
     * Get list of due date by due date data by search criteria.
     *
     * @param ExtendedDueDateSearch search
     * @return list of VwExtendedDueDate
     */
    List<VwExtendedDueDate> getExtendedDueDateData(ExtendedDueDateSearch search);

    /**
     * Get extended due date by due date id and extended due date id.
     *
     * @param Long dueDateID
     * @param Long extendedDueDateId
     * @return VwExtendedDueDate view extended due date
     */
    VwExtendedDueDate getExtendedDueDateByDueDateID(Long dueDateID, Long extendedDueDateId);

    /**
     * Save extended due date.
     *
     * @param List<ExtendedDueDate> list of extendedDueDate
     * @return String result status returned from dao
     */
    String saveExtendedDueDate(List<ExtendedDueDate> extendedDueDate);

    /**
     * Delete extended due date.
     *
     * @param ExtendedDueDate extendedDueDate
     * @return String result status returned from dao
     */
    String deleteExtendedDueDateData(ExtendedDueDate extendedDueDate);
        
    /**
     * Get a report period name by reporting period id.
     *
     * @param reportingPeriodId id
     * @return String reporting period name
     */
    String getReportPeriodNameById(Long reportingPeriodId);
    
    /**
     * Get a state name by state id.
     *
     * @param statId id
     * @return String state name
     */
    String getStateNameById(Long statId);
    
    /**
     * Get list of all state id.
     *
     * @return List<Long> state id list
     */
    List<Long> getAllStateIds();

}
