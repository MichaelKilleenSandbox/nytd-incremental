package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.models.Region;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.service.LookupService;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;
import org.hibernate.Query;
import org.hibernate.Session;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 7, 2010
 */
public class LookupServiceImpl extends BaseServiceImpl implements LookupService
{

	@Override
	public List<ReportingPeriod> getReportingPeriods()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getReportingPeriods");

		return qry.list();
	}

	@Override
	public List<SiteUser> getFederalUsers()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getRoleUsers");
		qry.setParameter("description", "Federal");

		return qry.list();
	}

	@Override
	public List<State> getRegionStates(Region region)
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getRegionStates");
		qry.setParameter("region", region);

		return qry.list();
	}

	@Override
	public List<SiteUser> getRegionUsers(Region region, State state)
	{
		// Only add regional users whose states are in site user state region mapping table
		// so that messages will be sent only to those states
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getRegionUsers");
		qry.setParameter("regionId", region.getId());
		qry.setParameter("stateId", state.getId());
		List<SiteUser> regionUserList = qry.list();
		return regionUserList;
	}

	@Override
	public Region getStateRegion(State state)
	{
		Query qry = getHibernateSession().getNamedQuery("getStateRegion");
		qry.setParameter("stateId", state.getId());

		return (Region) qry.uniqueResult();
	}

	@Override
	public List<State> getStates()
	{
		Query qry = getHibernateSession().getNamedQuery("getStates");

		return qry.list();
	}

	@Override
	public List<SiteUser> getStateUsers(State state)
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getStateUsers");
		qry.setParameter("stateId", state.getId());

		return qry.list();
	}

	@Override
	public List<SiteUser> getSystemAdminUsers()
	{
		Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getRoleUsers");
		qry.setParameter("description", "SA");

		return qry.list();
	}

	public List<State> getUserStates(SiteUser user)
	{
		List<State> states = new ArrayList<State>();
		UserRoleEnum role = UserRoleEnum.getRole(user.getPrimaryUserRole().getName());
		switch (role)
		{
			case STATE:
				states.add(user.getState());
				break;
			case REGIONAL:
				states.addAll(getRegionStates(user.getRegion()));
				break;
			default:
				states.addAll(getStates());
		}

		return states;
	}
	
	public Calendar getSubmissionDeadline(ReportingPeriod period,Long transmissionTypeId)
	{
		Calendar calendar = null;
		//TODO: move query to DAO
		Session session = getSessionFactory().getCurrentSession();
		String queryString = "select submissionDate "
	            + "from DueDate "
	            + "where reportingPeriodid = :reportingperiodid "
	            + "and transmissionTypeid = :transmissiontypeid ";
		List result = session.createQuery(queryString)
	              .setParameter("reportingperiodid", period.getId())
	              .setParameter("transmissiontypeid", transmissionTypeId.intValue())
	              .list();
		DateFormat format = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		calendar = Calendar.getInstance();
		Date date;
		try {
			date = (Date) format.parse(result.get(0).toString());
			calendar.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return calendar;
	}
}
