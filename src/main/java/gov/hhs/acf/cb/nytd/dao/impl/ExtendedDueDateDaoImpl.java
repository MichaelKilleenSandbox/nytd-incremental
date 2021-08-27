package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.dao.ExtendedDueDateDao;
import gov.hhs.acf.cb.nytd.models.ExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements ExtendedDueDateDao.
 */
public class ExtendedDueDateDaoImpl extends HibernateDaoSupport implements ExtendedDueDateDao {
	
    protected final Logger log = Logger.getLogger(getClass());

    /**
    * @see ExtendedDueDateDao#getExtendedDueDateData(ExtendedDueDateSearch)
    */
    @Override
    public List<VwExtendedDueDate> getExtendedDueDateData(ExtendedDueDateSearch search) {
        Session session;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in getExtendedDueDateData()");
        }
        List<VwExtendedDueDate> list = new ArrayList<>();
        //TODO NEEDS TO BE UPGRADED TO javax.persistence.criteria.* (i.e using CriteriaBuilder/CriteriaQuery), org.hiberate.Criteria is deprecated
        DetachedCriteria criteria = DetachedCriteria.forClass(VwExtendedDueDate.class);

        try {
            criteria.add(Restrictions.disjunction()
                .add(Restrictions.isNull("deleteFlag"))
                .add(Restrictions.eq("deleteFlag", '0'))
            );
            if (null != StringUtils.trimToNull(search.getState()) && !search.getState().equalsIgnoreCase("0")) {
                    criteria.add(Restrictions.eq("eddStateId", Long.parseLong(search.getState())));
            }
            if (null != StringUtils.trimToNull(search.getReportingPeriod())
                            && !search.getReportingPeriod().equalsIgnoreCase("0")) {
                    criteria.add(Restrictions.eq("reportingPeriodId", Long.parseLong(search.getReportingPeriod())));
            }
            if ( search.getExtendedDueDate() != null ) {
                criteria.add(Restrictions.gt("extendedDueDate", search.getExtendedDueDate()));
            }

            criteria = sortExtendedDueDateData(search, criteria);

            Criteria resultsCriteria = criteria.getExecutableCriteria(session);

            resultsCriteria = paginateExtendedDueDateData(search, resultsCriteria);
            list = resultsCriteria.list();
            log.info("after query execution ********** " + list.size());
            if (search.getPageSize() == 0){
                resultsCriteria.setMaxResults(list.size());
            }
            search.setPageResults(list);
        } catch (Exception ex) {
            log.info("Exception inside method getSubmissionDueDateData in  >>>>>>>>>>>>>>>>>>>>>>>>" + ex.getMessage());
        }

        return list;
    }

    /**
    * @see ExtendedDueDateDao#getExtendedDueDateByDueDateID(Long, Long)
    * 
    * this to fetch the data and to display in edit/view pages when click on view/edit links
    */
    @Override
    public VwExtendedDueDate getExtendedDueDateByDueDateID(Long dueDateID, Long extendedDueDateId) {
        Session session = null;
        VwExtendedDueDate extendedDueDateData = null;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in getExtendedDueDateByDueDateID()");
        }
       
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<VwExtendedDueDate> criteriaQuery = criteriaBuilder.createQuery(VwExtendedDueDate.class);
        Root<VwExtendedDueDate> root = criteriaQuery.from(VwExtendedDueDate.class);
        if(extendedDueDateId != null) {
            criteriaQuery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("id"), dueDateID),
                    criteriaBuilder.equal(root.get("extendedDueDateId"), extendedDueDateId)
                )
            );
        } else {
            criteriaQuery.where(
                criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("id"), dueDateID)
                )
            );
        }
        criteriaQuery.select(root);
        TypedQuery<VwExtendedDueDate> q = session.createQuery(criteriaQuery);
        try {
            extendedDueDateData = q.getSingleResult();
        } catch (HibernateException he) {
            log.error("error in query getExtendedDueDateByDueDateID: "+he.getMessage());			
        }
        
        return extendedDueDateData;
    }			

    /**
    * @see ExtendedDueDateDao#saveExtendedDueDate(List<ExtendedDueDate>)
    */
    @Override
    public String saveExtendedDueDate(List<ExtendedDueDate> extendedDueDateList) {
        String dataSavedStatus = "";
        Session session;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in saveExtendedDueDate()");
        }
        try {
            for(ExtendedDueDate extendedDueDate : extendedDueDateList) {
                session.saveOrUpdate(extendedDueDate);
            }
            dataSavedStatus = "SUCCESS";
        } catch(ConstraintViolationException cv) {
            if(cv.getErrorCode() == 1) {
                dataSavedStatus = "DUPLICATE";
            }
            log.info("Exception in saveExtendedDueDate ********** " +cv.getMessage());
        }
        
        return dataSavedStatus;
    }


    /**
    * @see ExtendedDueDateDao#deleteExtendedDueDateData(ExtendedDueDate>)
    */
    @Override
    public String deleteExtendedDueDateData(ExtendedDueDate extendedDueDate) {
        log.info("before deleteExtendedDueDateData ********** " +extendedDueDate.getId());
        String dataDeleteStatus = "";
        Session session = null; 
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in deleteExtendedDueDateData()");
        }
        try {
            session.saveOrUpdate(extendedDueDate);
            dataDeleteStatus = "SUCCESS";
        }catch(Exception se) {
            log.info("Exception in saveExtendedDueDate ********** " +se.getMessage());
            dataDeleteStatus = "FAIL";
        }
        
        return dataDeleteStatus;
    }

    /*
    * Data sorting based on selected column initially data will be displayed for all states and reporting periods,
    * a default column for data sorting is reporting period
    * @param ExtendedDueDateSearch search
    * @param DetachedCriteria criteria
    * @return DetachedCriteria criteria with sorting
    */
    private DetachedCriteria sortExtendedDueDateData(ExtendedDueDateSearch search, DetachedCriteria criteria) {
        if (search.getSortColumn() != null) {
            String columnName = "";
            if(search.getSortColumn().equalsIgnoreCase("startReportingDate")) {
                columnName = "startReportingDate";
            } else if(search.getSortColumn().equalsIgnoreCase("endReportingDate")) {
                columnName = "endReportingDate";
            } else if(search.getSortColumn().equalsIgnoreCase("reportingPeriodName")) {
                columnName = "reportingPeriodName";
            } else if(search.getSortColumn().equalsIgnoreCase("outcomeAge")) {
                columnName = "outComeAge";
            } else if(search.getSortColumn().equalsIgnoreCase("transmissionTypeName")) {
                columnName = "transmissionTypeName";
            } else if(search.getSortColumn().equalsIgnoreCase("submissionDate")) {
                columnName = "submissionDate";
            } else if(search.getSortColumn().equalsIgnoreCase("stateName")) {
                columnName = "eddStateName";
            } else if(search.getSortColumn().equalsIgnoreCase("updateBy")) {
                columnName = "extendedUpdateBy";
            } else if(search.getSortColumn().equalsIgnoreCase("updateDate")) {
                columnName = "extendedUpdateDate";
            } else {
                columnName = "extendedDueDate";
            }
            switch (search.getSortDirection()) {
                case ASC:
                    criteria.addOrder(Order.asc(columnName));
                    break; 
                case DESC:
                    criteria.addOrder(Order.desc(columnName));
                    break;
                default:
            }
        }
        
        return criteria;
    }
    
    /* 
    * To fetch data with search results , handle the logic for pagination, search criteria
    * @param ExtendedDueDateSearch search
    * @param DetachedCriteria resultsCriteria
    * @return Criteria resultsCriteria with pagination
    */
    private Criteria paginateExtendedDueDateData(ExtendedDueDateSearch search, Criteria resultsCriteria) {
        search.setRowCount(resultsCriteria.list().size());
        getPages(resultsCriteria,
                search.getPageSize(),
                search.getPage()
        );

        return resultsCriteria;
    }

    public static void getPages(Criteria resultsCriteria, int pageSize, int pageNum) {
        if (pageSize > 0) {
            int firstResult;
            if (pageNum == 0) {
                firstResult = 0;
            } else {
                firstResult = pageNum * pageSize;
            }
            resultsCriteria.setFirstResult(firstResult);
            resultsCriteria.setMaxResults(pageSize);
            resultsCriteria.setFetchSize(pageSize);
        }
    }
}
