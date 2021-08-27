/**
 * Filename: BaseServiceImpl.java
 * 
 * Copyright 2009, ICF International
 * Created: May 21, 2009
 * Author: 15178
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.models.Lookup;
import gov.hhs.acf.cb.nytd.service.BaseService;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author 15178
 */
@Transactional
public class BaseServiceImpl extends HibernateDaoSupport implements BaseService
{
	// logger
	protected final Logger log = Logger.getLogger(getClass());
	// JNDI data source
	protected DataSource dataSource;
	protected SessionFactory sessionFactory;

	/*
	 * return lookup based on type
	 * 
	 * @see
	 * gov.hhs.acf.cb.nytd.service.BaseService#findLookupByType(java.lang.String)
	 */
	public List<Lookup> findLookupByType(String type)
	{
		Session session = getSessionFactory().getCurrentSession();
		StringBuffer query = new StringBuffer("From Lookup where category = '");
		query.append(type);
		query.append("' order by sortorder");
		List<Lookup> lookups = session.createQuery(query.toString()).list();
		return lookups;
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	protected DetachedCriteria copyCriteria(DetachedCriteria criteria)
	{
		try
		{
			ByteArrayOutputStream baostream = new ByteArrayOutputStream();
			ObjectOutputStream oostream = new ObjectOutputStream(baostream);
			oostream.writeObject(criteria);
			oostream.flush();
			oostream.close();
			ByteArrayInputStream baistream = new ByteArrayInputStream(baostream.toByteArray());
			ObjectInputStream oistream = new ObjectInputStream(baistream);
			DetachedCriteria copy = (DetachedCriteria) oistream.readObject();
			oistream.close();
			return copy;
		}
		catch (Throwable t)
		{
			throw new HibernateException(t);
		}
	}

	protected Session getHibernateSession()
	{
		return getSessionFactory().getCurrentSession();
	}
}
