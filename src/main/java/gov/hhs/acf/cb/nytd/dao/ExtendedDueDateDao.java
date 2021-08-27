package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;

import java.util.List;

/**
 * Data Access Objects class related to extended due date.
 */
public interface ExtendedDueDateDao {

    /**
    * Returns list of VwExtendedDueDate by extended due date search.
    * @param ExtendedDueDateSearch search
    * @return List<VwExtendedDueDate>
    */
    List<VwExtendedDueDate> getExtendedDueDateData(ExtendedDueDateSearch search);

    /**
    * Returns VwExtendedDueDate by due date id and extended due date id.
    * @param Long dueDateID
    * @param Long extendedDueDateId
    * @return VwExtendedDueDate
    */
    VwExtendedDueDate getExtendedDueDateByDueDateID(Long dueDateID, Long extendedDueDateId);

    /**
    * Save list of extended due date.
    * @param List<ExtendedDueDate> extendedDueDate
    * @return String as result status
    */
    String saveExtendedDueDate(List<ExtendedDueDate> extendedDueDate);

    /**
    * Delete extended due date.
    * @param ExtendedDueDate extendedDueDate
    * @return String as result status
    */
    String deleteExtendedDueDateData(ExtendedDueDate extendedDueDate);

}
