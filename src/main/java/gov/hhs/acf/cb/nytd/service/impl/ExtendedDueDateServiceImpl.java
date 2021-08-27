package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.dao.ExtendedDueDateDao;
import gov.hhs.acf.cb.nytd.dao.ReportingPeriodDAO;
import gov.hhs.acf.cb.nytd.dao.StateDAO;
import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.service.ExtendedDueDateService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements ExtendedDueDateService.
 * 
 * @see ExtendedDueDateService
 */
@Transactional
public class ExtendedDueDateServiceImpl extends BaseServiceImpl implements ExtendedDueDateService {
	
    private final ExtendedDueDateDao extendedDueDateDao;
    @Getter @Setter private ReportingPeriodDAO reportingPeriodDAO;
    @Getter @Setter private StateDAO stateDAO;
    
    /**
     * Constructor with extendedDueDateDao argument
     */
    public ExtendedDueDateServiceImpl(ExtendedDueDateDao extendedDueDateDao){
        super();
        this.extendedDueDateDao = extendedDueDateDao;
    }

    /**
     * @see ExtendedDueDateService#getExtendedDueDateData(ExtendedDueDateSearch)
     */
    @Override
    public List<VwExtendedDueDate> getExtendedDueDateData(ExtendedDueDateSearch search) {
        return extendedDueDateDao.getExtendedDueDateData(search);
    }

    /**
     * @see ExtendedDueDateService#getExtendedDueDateByDueDateID(Long, Long)
     */
    @Override
    public VwExtendedDueDate getExtendedDueDateByDueDateID(Long dueDateID, Long extendedDueDateId) {
        return extendedDueDateDao.getExtendedDueDateByDueDateID(dueDateID, extendedDueDateId);
    }

    /**
     * @see ExtendedDueDateService#saveExtendedDueDate(List<ExtendedDueDate>)
     */
    @Override
    public String saveExtendedDueDate(List<ExtendedDueDate> extendedDueDate) {
        return extendedDueDateDao.saveExtendedDueDate(extendedDueDate);
    }

    /**
     * @see ExtendedDueDateService#deleteExtendedDueDateData(ExtendedDueDate)
     */
    @Override
    public String deleteExtendedDueDateData(ExtendedDueDate extendedDueDate) {
        return extendedDueDateDao.deleteExtendedDueDateData(extendedDueDate);
    }
        
    /**
     * @see ExtendedDueDateService#getReportPeriodNameById(Long)
     */
    @Override
    public String getReportPeriodNameById(Long reportingPeriodId) {
        return reportingPeriodDAO.getReportPeriodName(reportingPeriodId);
    }
    
    /**
     * @see ExtendedDueDateService#getStateNameById(Long)
     */
    @Override
    public String getStateNameById(Long stateId) {
        return stateDAO.getStateName(stateId);
    }
    
    /**
     * @see ExtendedDueDateService#getAllStateIds()
     */
    @Override
    public List<Long> getAllStateIds() {
        List<Long> stateIdList = new ArrayList<>();
        Object[] allStateArray = stateDAO.getStateSelectMap().keySet().toArray();
        for (Object stateObj: allStateArray) {
            Long stateId = Long.valueOf(stateObj.toString());
            stateIdList.add(stateId);
        }
        return stateIdList;
    }

}
