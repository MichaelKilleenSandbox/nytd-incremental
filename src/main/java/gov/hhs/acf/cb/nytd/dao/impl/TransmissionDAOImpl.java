package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.TransmissionDAO;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Implementation class for Transmission data access object
 * @author 19714
 */
@Transactional
public class TransmissionDAOImpl extends HibernateDaoSupport implements TransmissionDAO {
    
    protected final Logger log = Logger.getLogger(getClass());

    /**
     * @author Adam Russell (18816)
     * @see TransmissionDAO#getTransmissionWithId(Long)
     */
    @Override
    public Transmission getTransmissionWithId(Long id) {
        Session session = getSessionFactory().getCurrentSession();

        Transmission resultTransmission;
        resultTransmission = session.get(Transmission.class, id);

        return resultTransmission;
    }

    /**
     * @author Adam Russell (18816)
     * @see TransmissionDAO#getActiveSubmittedTransmissions(State)
     */
    public List<Transmission> getActiveSubmittedTransmissions(State state) {
        List<Transmission> activeSubmissions = Collections.emptyList();
        try {
            Session session = getSessionFactory().getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Transmission> criteriaQuery = cb.createQuery(Transmission.class);
            Root<Transmission> root = criteriaQuery.from(Transmission.class);
            Predicate isActive = cb.equal(root.get("submissionStatus"), "Active");
            Predicate selectedState = cb.equal(root.get("state").get("id"), state.getId());

            criteriaQuery.
                    select(root).
                    where(cb.and(isActive, selectedState));
            activeSubmissions = session.createQuery(criteriaQuery).list();
        }
        catch (Exception e) {
            log.warn("Test");
        }
        return activeSubmissions;
    }

    @Override
    public List<Transmission> getActiveSubmittedTransmissions(State state, Long reportingPeriod) {
        List<Transmission> activeSubmissions = Collections.emptyList();
        try {
            Session session = getSessionFactory().getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Transmission> criteriaQuery = cb.createQuery(Transmission.class);
            Root<Transmission> root = criteriaQuery.from(Transmission.class);
            //Join<Transmission,State> stateJoin = root.join( Transmission. );
            Predicate isActive = cb.equal(root.get("submissionStatus"), "Active");
            Predicate selectedState = cb.equal(root.get("state").get("id"), state.getId());
            Predicate selectedReportingPeriod = cb.equal(root.get("reportingPeriod"), reportingPeriod);
            criteriaQuery.
                    select(root).
                    where(cb.and(isActive, selectedState, selectedReportingPeriod));

            activeSubmissions = session.createQuery(criteriaQuery).list();
        }
        catch (Exception e) {
            log.warn( "Incorrect data in parameters", e);
        }
        return activeSubmissions;
    }

    /**
     * @see TransmissionDAO#getTransmissionSelectMap(SiteUser)
     */
    @Override
    public Map<String, String> getTransmissionSelectMap(SiteUser siteUser) {
        Session session = getSessionFactory().getCurrentSession();
        Map<String, String> selectMap = new LinkedHashMap<>();
        String userRole = siteUser.getPrimaryUserRole().getDescription();
        String userSpecificClause = "";

        if (userRole.equals("State")) {
                userSpecificClause = "where state.id = :stateId";
        } else if (userRole.equals("Regional")) {
            userSpecificClause = "where state.id in (%s) and ("
                               + "transmission.submissionStatus = 'Active' or "
                               + "transmission.submissionStatus = 'Inactive') ";
            String statesInRegion = "select state.id "
                                  + "from State as state "
                                  + "join state.region as region "
                                  + "where region.id = %d ";
            statesInRegion = String.format(statesInRegion, siteUser.getRegion().getId());
            userSpecificClause = String.format(userSpecificClause, statesInRegion);
        } else {
                userSpecificClause = "where transmission.submissionStatus = 'Active' or "
                                   + "transmission.submissionStatus = 'Inactive' ";
        }

        String queryString = "select transmission "
                + "from Transmission as transmission "
                + "join transmission.state as state "
                + "%s "
                + " and (transmission.processingStatus <> 'Exited')"
                + "order by transmission.id desc ";
        queryString = String.format(queryString, userSpecificClause);

        Query query = session.createQuery(queryString);
        if (userRole.equals("State")) {
                query = query.setParameter("stateId", siteUser.getState().getId());
        }
        List<Transmission> queryResult = query.list();
        for (Transmission transmission : queryResult) {
                selectMap.put(String.valueOf(transmission.getId()), String.valueOf(transmission.getId()));
        }
        
        return selectMap;
    }

    /**
     * @see TransmissionDAO#getTransmissionSelectMap(Long, Long)
     */
    @Override
    public Map<String, String> getTransmissionSelectMap(Long stateId, Long reportPeriodId) {
        Session session = getSessionFactory().getCurrentSession();
        Map<String, String> selectMap = new LinkedHashMap<>();
        List<Transmission> queryResult;
        String query;
        query = "select transmission "
              + "from Transmission as transmission "
              + "join transmission.reportingPeriod as reportPeriod "
              + "join transmission.state as state "
              + "where state.id = :stateId "
              + "and reportPeriod.id = :reportPeriodId "
              + "order by transmission.id desc ";
        log.info("Inside getTransmissionSelectMap: "+ query+"*********************");
        queryResult = session.createQuery(query).setParameter("stateId", stateId).setParameter("reportPeriodId", reportPeriodId).list();
        for (Transmission transmission : queryResult) {
                selectMap.put(String.valueOf(transmission.getId()), String.valueOf(transmission.getId()));
        }
        
        return selectMap;
    }

    /**
     * @author Adam Russell (18816)
     * @see TransmissionDAO#getTransmissionCount(Long, Long, Boolean)
     */
    @Override
    public Long getTransmissionCount(Long stateId, Long reportPeriodId, Boolean onlySubmissions)
    {
        Session session = getSessionFactory().getCurrentSession();
        Long queryResult;
        String queryString;
        String submissionsOnlyClause = "";

        queryString = "select count(distinct transmission.id) "
                    + "from Transmission as transmission "
                    + "join transmission.reportingPeriod as reportPeriod "
                    + "join transmission.state as state "
                    + "where state.id = :stateId "
                    + "and reportPeriod.id = :reportPeriodId "
                    + "%s ";

        if (onlySubmissions) {
                submissionsOnlyClause = "and (transmission.submissionStatus = 'Active' "
                                      + "or transmission.submissionStatus = 'Inactive') ";
        }

        queryString = String.format(queryString, submissionsOnlyClause);
        queryResult = (Long) session.createQuery(queryString)
                                .setParameter("stateId", stateId)
                                .setParameter("reportPeriodId", reportPeriodId).uniqueResult();

        return queryResult;
    }

    /**
     * @see TransmissionDAO#deleteAll()
     */
    @Override
    @SuppressWarnings("unchecked")
    public void deleteAll() {
            Session session = getSessionFactory().getCurrentSession();
            List<Transmission> transmissions = (List<Transmission>) session.createQuery("from Transmission").list();

            for (Transmission transmission : transmissions) {
                    session.delete(transmission);
            }
    }

    /**
     * @see TransmissionDAO#withHighestId()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Transmission withHighestId() {
            Session session = getSessionFactory().getCurrentSession();
            List<Transmission> transmissions = (List<Transmission>) session.createQuery(
                            "from Transmission transmission order by transmission.id desc").list();
            if (!transmissions.isEmpty()) {
                    return transmissions.get(0);
            }
            
            return null;
    }

    /**
     * @see TransmissionDAO#updateTransmissionProcessingStatus(Transmission)
     */
    public Transmission updateTransmissionProcessingStatus(Transmission transmission)
    {
             transmission.setProcessingStatus(Constants.RULES_ENGINE_INITIATED);
             transmission.setUpdateBy(Constants.UPDATED_BY_WEB_APP);
             transmission.setUpdateDate(Calendar.getInstance());

             Session session = getSessionFactory().getCurrentSession();
             session.saveOrUpdate(transmission);
             return transmission;
    }
        
    /**
     * @see TransmissionDAO#updateTransmission(T)
     */
    public void updateTransmission(Transmission transmission) {
        if (transmission != null) {
            try {
                Session hibernateSession = getSessionFactory().getCurrentSession();
                hibernateSession.update(transmission);
            } catch (Exception e) {
                    log.error(e.getMessage(), e);
            }
        }
    }

    public String getSubmissionStatus(Long transmissionId) {
        return getTransmissionWithId(transmissionId).getSubmissionStatus();
    }

    public String getTransmissionType(Long transmissionId) {
        return getTransmissionWithId(transmissionId).getTransmissionType().getName();
    }

    public String getComplianceStatus(Long transmissionId) {
        return getTransmissionWithId(transmissionId).getComplianceStatus();
    }
}
