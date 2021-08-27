package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.DatumDAO;
import gov.hhs.acf.cb.nytd.models.Datum;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @see DatumDAO
 */
@Transactional
public class DatumDAOImpl extends HibernateDaoSupport implements DatumDAO
{
	protected final Logger log = Logger.getLogger(getClass());

	/**
	 * @see DatumDAO#getDataForRecord(Long)
	 * @author Adam Russell (18816)
	 */
	@Override
	public List<Datum> getDataForRecord(Long transmissionRecordId)
	{
		assert(transmissionRecordId != null);
		Session session = getSessionFactory().getCurrentSession();
		List<Datum> queryResult;
		String query;
		query = "select datum "
		      + "from Datum as datum "
		      + "join datum.transmissionRecord as record "
		      + "join datum.element as element "
		      + "where record.id = :recordId "
		      + "order by element.name asc ";
		queryResult = session.createQuery(query).setParameter("recordId", transmissionRecordId).list();
		return queryResult;
	}
}
