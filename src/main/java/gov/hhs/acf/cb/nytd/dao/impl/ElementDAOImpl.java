package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.ElementDAO;
import gov.hhs.acf.cb.nytd.models.Element;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElementDAOImpl extends HibernateDaoSupport implements ElementDAO
{
	/**
	 * @see ElementDAO#getElementSelectMap()
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getElementSelectMap()
	{
		Session session = getSessionFactory().getCurrentSession();
		Map<String, String> selectMap;
		List<Object[]> queryResult;
		String query;
		
		query = "select element.id, element.name, element.description "
		      + "from Element as element "
		      + "order by element.sort asc ";
		queryResult = session.createQuery(query).list();
		
		selectMap = new LinkedHashMap<String, String>();
		
		for (Object[] pair : queryResult)
		{
			selectMap.put(String.valueOf(pair[0]),
			              String.format("%s %s",
			                            String.valueOf(pair[1]),
			                            String.valueOf(pair[2])));
		}
		
		return selectMap;
	}
	
	/**
	 * @see ElementDAO#getElementSelectMapForFrequencies()
	 * @author Adam Russell (18816)
	 */
	@Override
	public Map<String, String> getElementSelectMapForFrequencies()
	{
		Map<String, String> selectMap = getElementSelectMap();
		
		// Remove elements based on their id. Yes, this is a hack.
		selectMap.remove("3");
		selectMap.remove("4");
		selectMap.remove("35");
		selectMap.remove("15");
		selectMap.remove("1");
		
		return selectMap;
	}
	
	/**
	 * @see ElementDAO#getElementByName(String)
	 * @author Adam Russell (18816)
	 */
	@Override
	public Element getElementByName(String name)
	{
		Session session = getSessionFactory().getCurrentSession();
		Query query;
		Element result;
		String queryString;
		
		queryString = "select element "
		            + "from Element as element "
		            + "where element.name = :elementName ";
		
		query = session.createQuery(queryString).setParameter("elementName", name);
		result = (Element) query.uniqueResult();
		return result;
	}
}
