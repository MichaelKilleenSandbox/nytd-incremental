package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.actions.penalty.PenaltySearch;
import gov.hhs.acf.cb.nytd.dao.PenaltyLetterDAO;
import gov.hhs.acf.cb.nytd.models.PenaltyLettersMetadata;
import gov.hhs.acf.cb.nytd.models.helper.VwTransmissionStatus;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implements PenaltyLetterDAO.
 */
@Transactional
public class PenaltyLetterDAOImpl extends HibernateDaoSupport implements PenaltyLetterDAO {

     protected final Logger log = Logger.getLogger(getClass());
     
     private static final String STATE = "state";
     private static final String REPORTING_PERIOD = "reportingPeriod";
     private static final String SUBMITTED_DATE = "submittedDate";
     private static final String SUBMISSION_STATUS = "submissionStatus";

    /**
    * @see PenaltyLetterDAO#getPenaltyLetterMetadata()
    */
    @Override
    public List<PenaltyLettersMetadata> getPenaltyLetterMetadata() {
        
        List<PenaltyLettersMetadata> resultList = new ArrayList<>();
        Session session = null;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in getPenaltyLetterMetadata()");
        }
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<PenaltyLettersMetadata> criteriaQuery = criteriaBuilder.createQuery(PenaltyLettersMetadata.class);
        Root<PenaltyLettersMetadata> root = criteriaQuery.from(PenaltyLettersMetadata.class);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));
        criteriaQuery.select(root);
        TypedQuery<PenaltyLettersMetadata> q = session.createQuery(criteriaQuery);	

        try {
            resultList= q.getResultList();
        } catch (HibernateException he) {
            log.error("error in query getPenaltyLetterMetadata: "+he.getMessage());
        }
        
        return resultList;
    }
    
    /**
    * @see PenaltyLetterDAO#savesavePenaltyLettersMetadata(PenaltyLettersMetadata)
    */
    @Override
    public PenaltyLettersMetadata savePenaltyLettersMetadata(PenaltyLettersMetadata metadata) {

        Session session = null;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in savePenaltyLettersMetadata()");
        }
        session.saveOrUpdate(metadata);
        
        return metadata;
    }
    
    /**
     * @see PenaltyLetterDAO#searchGeneratePenaltyLetters(PenaltySearch)
     */
    @Override
    // TODO: Mutsuo 8/6/2021 SonarQube - Cognitive Complexity: revised requirement to show Active plus most recent Inactive
    //       Regular submission for each state/report period causing extra complexity with inner loop, perhaps better to
    //       do the logic by creating a new db view from performance perspective as well.
    public PenaltySearch searchGeneratePenaltyLetters(PenaltySearch search) {
        Session session;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in searchGeneratePenaltyLetters()");
        }
        List<VwTransmissionStatus> transmissionList = null;
        DetachedCriteria criteria = DetachedCriteria.forClass(VwTransmissionStatus.class);

        // generatePenaltiesLetters to get the results with selected states
        Collection<String> selectedStates = search.getSelectedStates().values();
        if (!selectedStates.isEmpty()) {
            criteria.add(Restrictions.in(STATE, selectedStates));
        }

        // restrict to specified reporting periods
        Collection<String> names = search.getSelectedReportingPeriods();
        if (names != null && !names.isEmpty()) {
            criteria.add(Restrictions.in(REPORTING_PERIOD, names));
        }

        // restrict results to submitted transmissions
        if (search.isViewSubmissionsOnly()) {
            criteria.add(Restrictions.isNotNull(SUBMITTED_DATE));
        }
        
        if (search.isViewActiveSubmissionsOnly()) {
            criteria.add(Restrictions.disjunction().add(Restrictions.eq(SUBMISSION_STATUS, "Active")).add(
                    Restrictions.eq(SUBMISSION_STATUS, "active")));
        } else {
            // add active as well as only the most recent inactive regular submission for default search
            Disjunction disjunction = Restrictions.disjunction();
            criteria.add(disjunction);
            for (String rpName : names) {
                for (String stName : selectedStates ) {
                    DetachedCriteria subSelect = DetachedCriteria.forClass(VwTransmissionStatus.class);
                    subSelect.add(Restrictions.isNotNull(SUBMITTED_DATE));
                    subSelect.add(Restrictions.eq(REPORTING_PERIOD, rpName));
                    subSelect.add(Restrictions.eq(STATE, stName));
                    subSelect.add(Restrictions.eq("transmissionType", "Regular"));
                    subSelect.setProjection(Projections.max(SUBMITTED_DATE));
                    subSelect.add(Restrictions.disjunction()
                            .add(Restrictions.eq(SUBMISSION_STATUS, "Inactive"))
                            .add(Restrictions.eq(SUBMISSION_STATUS, "inactive"))
                    );
                    disjunction.add(Restrictions.disjunction().add(Property.forName(SUBMITTED_DATE).eq(subSelect)));
                }
            }
            disjunction.add(Restrictions.disjunction()
                    .add(Restrictions.eq(SUBMISSION_STATUS, "Active"))
                    .add(Restrictions.eq(SUBMISSION_STATUS, "active"))
            );
        }

        // execute count query to return row count
        criteria.setProjection(Projections.rowCount());
        Criteria countCriteria = criteria.getExecutableCriteria(session);
        Long lRowCount = (Long)countCriteria.uniqueResult();
        search.setRowCount(lRowCount.intValue());
        criteria.setProjection(null);
        criteria.setResultTransformer(org.hibernate.criterion.CriteriaSpecification.ROOT_ENTITY);

        // add sort
        if (search.getSortColumn() != null) {
            switch (search.getSortDirection()) {
                case ASC:
                    criteria.addOrder(Order.asc(search.getSortColumn()));
                    break;
                case DESC:
                    criteria.addOrder(Order.desc(search.getSortColumn()));
                    break;
                default:
            }
        } else {
            // default sort order
            criteria.addOrder(Order.desc(REPORTING_PERIOD));
            search.setSortColumn(REPORTING_PERIOD);
            search.setSortDirection(PaginatedSearch.SortDirection.DESC);
        }

        // execute result query. limit results if page is not empty
        Criteria resultsCriteria = criteria.getExecutableCriteria(session);
        ExtendedDueDateDaoImpl.getPages(resultsCriteria, search.getPageSize(), search.getPage());
        transmissionList = resultsCriteria.list();
        search.setPageResults(transmissionList);
        
        return search;
    }

}
