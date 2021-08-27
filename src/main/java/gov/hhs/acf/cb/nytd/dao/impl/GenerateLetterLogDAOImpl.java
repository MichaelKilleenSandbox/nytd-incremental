package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.GenerateLetterLogDAO;
import gov.hhs.acf.cb.nytd.models.GenerateLetterLog;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GenerateLetterLogDAOImpl extends HibernateDaoSupport implements GenerateLetterLogDAO {

    protected static Logger LOG = Logger.getLogger(GenerateLetterLogDAOImpl.class);

    @Override
    public GenerateLetterLog save(GenerateLetterLog log) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(log.toString());
        }

        Session session = getSessionFactory().getCurrentSession();
        session.save(log);
        return log;
    }
}
