package gov.hhs.acf.cb.nytd.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import gov.hhs.acf.cb.nytd.models.PrimaryUserRole;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.service.LookupService;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.interceptor.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Apr 23, 2010
 */
public abstract class SearchAction<T extends PaginatedSearch> extends ActionSupport implements Preparable,
		ApplicationAware, SessionAware, HttpParametersAware, ServletRequestAware, ServletResponseAware
{
	// j2ee framework objects
	protected Map<String, Object> application;
	protected Map<String, Object> session;
	protected HttpParameters parameters;
	protected HttpServletRequest servletRequest;
	protected HttpServletResponse servletResponse;

	// service objects
	protected LookupService lookupService;

	// define the user properties globally
	protected SiteUser user;
	protected PrimaryUserRole primaryUserRole;
	protected String userName;
	protected String userType;
	protected List<State> statesForUser;
	protected List<State> statesInRegion;

	/**
	 * Generic version of search getter to support calcSortDirection Subclasses
	 * define the concrete search class returned
	 */
	protected abstract T getPaginatedSearch();

	public LookupService getLookupService()
	{
		return lookupService;
	}

	public void setLookupService(LookupService lookupService)
	{
		this.lookupService = lookupService;
	}

	public SiteUser getUser()
	{
		return user;
	}

	public void setUser(SiteUser user)
	{
		this.user = user;
	}

	public PrimaryUserRole getPrimaryUserRole()
	{
		return primaryUserRole;
	}

	public void setPrimaryUserRole(PrimaryUserRole primaryUserRole)
	{
		this.primaryUserRole = primaryUserRole;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserType()
	{
		return userType;
	}

	public void setUserType(String userType)
	{
		this.userType = userType;
	}

	public List<State> getStatesForUser()
	{
		return statesForUser;
	}

	public void setStatesForUser(List<State> statesForUser)
	{
		this.statesForUser = statesForUser;
	}

	public List<State> getStatesInRegion()
	{
		return statesInRegion;
	}

	public void setStatesInRegion(List<State> statesInRegion)
	{
		this.statesInRegion = statesInRegion;
	}

	public Map<String, Object> getApplication()
	{
		return application;
	}

	public void setApplication(Map<String, Object> stringObjectMap)
	{
		this.application = stringObjectMap;
	}

	public Map<String, Object> getSession()
	{
		return session;
	}

	public void setSession(Map<String, Object> stringObjectMap)
	{
		this.session = stringObjectMap;
	}

	public HttpParameters getParameters()
	{
		return parameters;
	}

	public void setParameters(HttpParameters parameters)
	{
		this.parameters = parameters;
	}

	public HttpServletRequest getServletRequest()
	{
		return servletRequest;
	}

	public void setServletRequest(HttpServletRequest httpServletRequest)
	{
		this.servletRequest = httpServletRequest;
	}

	public HttpServletResponse getServletResponse()
	{
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse httpServletResponse)
	{
		this.servletResponse = httpServletResponse;
	}

	public String calcSortDirection(String sortColumn)
	{
		PaginatedSearch.SortDirection asc = PaginatedSearch.SortDirection.ASC;
		PaginatedSearch.SortDirection desc = PaginatedSearch.SortDirection.DESC;
		PaginatedSearch.SortDirection sortDirection = getPaginatedSearch().getSortDirection();

		if (sortColumn.equals(getPaginatedSearch().getSortColumn()))
		{
			sortDirection = sortDirection.equals(asc) ? desc : asc;
		}
		else
		{
			sortDirection = asc;
		}

		return sortDirection.name();
	}

	public String calcSortDirection(String sortAssociation, String sortColumn)
	{
		PaginatedSearch.SortDirection asc = PaginatedSearch.SortDirection.ASC;
		PaginatedSearch.SortDirection desc = PaginatedSearch.SortDirection.DESC;
		PaginatedSearch.SortDirection sortDirection = getPaginatedSearch().getSortDirection();

		if (sortAssociation.equals(getPaginatedSearch().getSortAssociation())
				&& sortColumn.equals(getPaginatedSearch().getSortColumn()))
		{
			sortDirection = sortDirection.equals(asc) ? desc : asc;
		}
		else
		{
			sortDirection = asc;
		}

		return sortDirection.name();
	}

	public String formatDateAndTimezone(int format, Calendar date)
	{
		return DateUtil.formatDateAndTimezone(format, date);
	}

	public String formatPercent(Number percentValue)
	{
		String percentFormat = "#0.00'%'";
		DecimalFormat format = new DecimalFormat(percentFormat);
		return format.format(percentValue.doubleValue());
	}

	public void prepare()
	{
		this.user = (SiteUser) getSession().get(SiteUser.SESSION_KEY);
		this.primaryUserRole = user.getPrimaryUserRole();
		this.userName = user.getFirstName();
		this.userType = getPrimaryUserRole().getDescription();
		this.statesForUser = lookupService.getUserStates(this.user);
	}

	public String urlEncode(String value)
	{
		return CommonFunctions.urlEncode(value);
	}
	
	public String urlDecode(String value)
	{
		return CommonFunctions.urlDecode(value);
	}
}
