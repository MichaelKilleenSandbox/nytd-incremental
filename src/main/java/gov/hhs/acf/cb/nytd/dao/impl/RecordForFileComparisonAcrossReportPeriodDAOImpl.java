package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.RecordForFileComparisonAcrossReportPeriodDAO;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriods;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

@Transactional
public class RecordForFileComparisonAcrossReportPeriodDAOImpl extends HibernateDaoSupport implements
        RecordForFileComparisonAcrossReportPeriodDAO {
    protected final Logger log = Logger.getLogger(getClass());


    /**
     * @see RecordForFileComparisonAcrossReportPeriodDAO#getRecordsForFileComparisonAcrossReportPeriod(Long, Long)
     * @author Adam Russell (18816)
     */

    @Override
    public List<FileAdvisoryAcrossReportPeriods> getRecordsForFileComparisonAcrossReportPeriod(
            Long transmissionId, Long siteUserId)
    {
        assert (transmissionId != null && siteUserId != null);
        Session dbSession = getSessionFactory().getCurrentSession();
        EntityManager em = dbSession.getEntityManagerFactory().createEntityManager();
        StoredProcedureQuery q = em.createNamedStoredProcedureQuery("spCrossReportPdCompare");

        q.setParameter(2, transmissionId);
        q.setParameter(3, siteUserId);

        List<FileAdvisoryAcrossReportPeriods> queryResult = q.getResultList();
        return queryResult;

    }
}
