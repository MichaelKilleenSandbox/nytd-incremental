package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;

import java.util.List;

public interface ExtendedDueDateMessageService extends BaseService {

    void extendedDueDateCreated(VwExtendedDueDate extndueDateData, List<ExtendedDueDate> extendedDueDate);

    void extendedDueDateDeleted( VwExtendedDueDate extendedDueDateData, List<ExtendedDueDate> extendedDueDate);

    void extendedDueDateEdited(VwExtendedDueDate extendedDueDateData, List<ExtendedDueDate> extendedDueDate);

}

