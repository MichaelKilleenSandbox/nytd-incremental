package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.SiteUserDAO;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implements SiteUserDAO.
 *
 * @author 17628
 * @see SiteUserDAO
 */
@Transactional
public class SiteUserDAOImpl extends HibernateDaoSupport implements SiteUserDAO {
    protected final Logger log = Logger.getLogger(getClass());

    @Override
    public void deleteAll() {
        Session session = getSession("session factory is null in deleteAll()");
        List<?> objects = session.createQuery("from SiteUser").list();

        for (Object object : objects) {
            session.delete(object);
        }
    }

    /**
     * @see siteUserDAO#getFederalUserListByEmail(String)
     */
    @Override
    public List<SiteUser> getFederalUserListByEmail(final String email) {
        Session session = getSession("session factory is null in getFederalUserListByEmail()");

        List<SiteUser> siteUserList = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        Integer cb = 2;
        Integer ro = 3;
        List<Long> roleIdList = Arrays.asList(
                cb.longValue(), //CB Central Office Staff Member
                ro.longValue()); //Regional Office User

        CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
        Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
        Expression<String> roleIdExpression = root.get("primaryUserRole");
        Predicate roleIdPredicate = roleIdExpression.in(roleIdList);
        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("emailAddress")), email.toLowerCase()),
                        criteriaBuilder.equal(root.get("isDeleted"), false),
                        roleIdPredicate
                )
        );
        criteriaQuery.select(root);
        TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);
        try {
            siteUserList = q.getResultList();
        }
        catch (HibernateException he) {
            log.error("error in query getFederalUserListByEmail: " + he.getMessage());
        }

        return siteUserList;
    }

    /**
     * @see siteUserDAO#getFederalUserListByEmail(String)
     */
    @Override
    public List<SiteUser> getAllUserListByEmail(final String email) {
        Session session = getSession("session factory is null in getAllUserListByEmail()");

        List<SiteUser> siteUserList = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
        Root<SiteUser> root = criteriaQuery.from(SiteUser.class);

        criteriaQuery.where(
                criteriaBuilder.and(
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("emailAddress")), email.toLowerCase()),
                        criteriaBuilder.equal(root.get("isDeleted"), false)
                )
        );
        criteriaQuery.select(root);
        TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);
        try {
            siteUserList = q.getResultList();
        }
        catch (HibernateException he) {
            log.error("error in query getAllUserListByEmail: " + he.getMessage());
        }

        return siteUserList;
    }

    @Override
    public List<SiteUser> findAllByRegion(Long regionId) {

        try {
            Session session = getSession("session factory is null in findAllByRegion()");

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
            Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
            root.fetch("region", JoinType.INNER);
            Predicate regionalUser = criteriaBuilder.equal(root.get("primaryUserRole").get("id"), 3);
            Predicate theRegion = criteriaBuilder.equal(root.get("region").get("id"), regionId);
            Predicate isNotDeleted = criteriaBuilder.equal(root.get("isDeleted"), false);

            criteriaQuery.
                    select(root).
                    where(criteriaBuilder.and(regionalUser, theRegion, isNotDeleted));
            TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);

            List<SiteUser> siteUserList = q.getResultList();
            return siteUserList;
        }
        catch (HibernateException hibernateException) {
            log.error(hibernateException.getMessage(), hibernateException);
            return Collections.emptyList();
        }
    }

    @Override
    public List<SiteUser> findAllByStateId(Long stateId) {
        try {

            Session session = getSession("session factory is null in findAllByStateId()");

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
            Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
            root.fetch("state", JoinType.INNER);
            Predicate stateUser = criteriaBuilder.equal(root.get("primaryUserRole").get("id"), 4);
            Predicate theState = criteriaBuilder.equal(root.get("state").get("id"), stateId);
            Predicate isNotDeleted = criteriaBuilder.equal(root.get("isDeleted"), false);

            criteriaQuery.
                    select(root).
                    where(criteriaBuilder.and(stateUser, theState, isNotDeleted));
            TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);

            List<SiteUser> siteUserList = q.getResultList();
            return siteUserList;
        }
        catch (HibernateException hibernateException) {
            log.error(hibernateException.getMessage(), hibernateException);
            return Collections.emptyList();
        }
    }

    @Override
    public List<SiteUser> findAllByPrimaryRole(Long role) {
        try {

            Session session = getSession("session factory is null in findAllByPrimaryRole()");

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
            Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
            Predicate stateUser = criteriaBuilder.equal(root.get("primaryUserRole").get("id"), role);
            Predicate isNotDeleted = criteriaBuilder.equal(root.get("isDeleted"), false);

            criteriaQuery.
                    select(root).
                    where(criteriaBuilder.and(stateUser, isNotDeleted));
            TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);

            List<SiteUser> siteUserList = q.getResultList();
            return siteUserList;
        }
        catch (HibernateException hibernateException) {
            log.error(hibernateException.getMessage(), hibernateException);
            return Collections.emptyList();
        }
    }

    @Override
    public List<SiteUser> findByStateAndPrimaryRole(Long stateId, Long role) {
        try {

            Session session = getSession("session factory is null in findAllByStateId()");

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<SiteUser> criteriaQuery = criteriaBuilder.createQuery(SiteUser.class);
            Root<SiteUser> root = criteriaQuery.from(SiteUser.class);
            root.fetch("state", JoinType.INNER);
            Predicate stateUser = criteriaBuilder.equal(root.get("primaryUserRole").get("id"), role);
            Predicate theState = criteriaBuilder.equal(root.get("state").get("id"), stateId);
            Predicate isNotDeleted = criteriaBuilder.equal(root.get("isDeleted"), false);

            criteriaQuery.
                    select(root).
                    where(criteriaBuilder.and(stateUser, theState, isNotDeleted));
            TypedQuery<SiteUser> q = session.createQuery(criteriaQuery);

            List<SiteUser> siteUserList = q.getResultList();
            return siteUserList;
        }
        catch (HibernateException hibernateException) {
            log.error(hibernateException.getMessage(), hibernateException);
            return Collections.emptyList();
        }
    }


    private Session getSession(String exceptionMessage) {
        Session session;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException(exceptionMessage);
        }
        return session;
    }


}