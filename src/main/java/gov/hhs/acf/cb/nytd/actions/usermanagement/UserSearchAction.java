/**
 * Filename: UserSearchAction.java
 * 
 * Copyright 2009, ICF International
 * Created: Dec 9, 2009
 * Author: 15670
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
package gov.hhs.acf.cb.nytd.actions.usermanagement;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;
import gov.hhs.acf.cb.nytd.service.LookupService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.Constants;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 15670 (Douglas W Sjoquist) Derived from ManagedUserAction
 */
@SuppressWarnings("serial")
public class UserSearchAction extends ActionSupport implements SessionAware, ApplicationAware,
		ServletRequestAware, ServletResponseAware
{
	protected Logger log = Logger.getLogger(getClass());

	public static final String RESPONSE_USER_SEARCH_RESULTS = "userSearchResults";

	private LookupService lookupService;
	private UserService userService;

	// kluge to use constants in JSP
	private String allValues = Constants.ALL_VALUES;
	private String cbUser = Constants.CBUSER;
	private String regionalUser = Constants.REGIONALUSER;
	private String stateUser = Constants.STATEUSER;

	private String cbPrimaryRoleSelected = Constants.FALSE_STR;
	private String regionalPrimaryRoleSelected = Constants.FALSE_STR;
	private String statePrimaryRoleSelected = Constants.FALSE_STR;

	private String userNameFilter;
	private String firstNameFilter;
	private String lastNameFilter;
	private String emailAddressFilter;
	private String stateFilter;
	private String regionFilter;
	private String[] primaryRoleNameFilters;
	private String[] secondaryRoleNameFilters;

	private boolean searchButtonClicked = false;

	private Map<String, Object> session;
	private Map<String, Object> application;
	private HttpServletRequest request;
	private HttpServletResponse response;

	private List<SiteUser> userSearchResultList;
	private List<SiteUserWrapper> siteUserWrapperList;

	private String columnSelected;
	private boolean orderByDescending;

	/**
	 * Executes action.
	 * 
	 * @return
	 */
	public final String displayManageUserPage()
	{
		log.debug("UserSearchAction.displayManageUserPage");
		// viewTypeOfUserList = userService.getTypeOfUserList();
		return Action.SUCCESS;
	}

	public final String userSearch()
	{
		// push list of states on value stack for use in JSP
		List<State> states = lookupService.getStates();
		ValueStack stack = ActionContext.getContext().getValueStack();
		stack.set("states", states);

		log.debug("UserSearchAction.userSearch");
		searchButtonClicked = true;

		SiteUser loggedInUser = getLoggedInUser();
		if (loggedInUser == null)
		{
			return Action.INPUT;
		}
		log.debug("\tcbUser = " + cbUser);
		log.debug("\tuserNameFilter = " + userNameFilter);
		log.debug("\tfirstNameFilter = " + firstNameFilter);
		log.debug("\tlastNameFilter = " + lastNameFilter);
		log.debug("\temailAddressFilter = " + emailAddressFilter);

		log.debug("\tprimaryRoleNameFilters = " + Arrays.toString(primaryRoleNameFilters));
		log.debug("\tstateFilter = " + stateFilter);
		log.debug("\tregionFilter = " + regionFilter);

		log.debug("\tsecondaryRoleNameFilters = " + Arrays.toString(secondaryRoleNameFilters));

		log.debug("\tcbPrimaryRoleSelected = " + cbPrimaryRoleSelected);
		log.debug("\tregionalPrimaryRoleSelected = " + regionalPrimaryRoleSelected);
		log.debug("\tstatePrimaryRoleSelected = " + statePrimaryRoleSelected);

		userSearchResultList = new ArrayList<SiteUser>();
		siteUserWrapperList = new ArrayList<SiteUserWrapper>();

		String[] usePrimaryRoles = null;
		String[] userSecondaryRoles = null;
		String useState = null;
		String useRegion = null;
		boolean excludeTestUsers = false;

		/* TODO: (dsjoquist) the filtering of users that are viewable based on
		   logged in user should happen in the service layer, not here */
		userSecondaryRoles = ((secondaryRoleNameFilters != null) && (secondaryRoleNameFilters.length == 1) && secondaryRoleNameFilters[0]
				.equals("false")) ? null : secondaryRoleNameFilters;
		if (loggedInUser.hasPrivilege(Constants.PRIV_CAN_ADMIN_ALL_USERS))
		{
			// if no primaryRoles are selected, then the primaryRoles array has a single value of "false"
			usePrimaryRoles = ((primaryRoleNameFilters != null) && (primaryRoleNameFilters.length == 1) && primaryRoleNameFilters[0]
					.equals("false")) ? null : primaryRoleNameFilters;
			// if we selected "All" for state or region as sysop, use null
			useState = allValues.equals(stateFilter) ? null : stateFilter;
			useRegion = allValues.equals(regionFilter) ? null : regionFilter;
		}
		else if (loggedInUser.hasPrivilege(Constants.PRIV_CAN_ADMIN_OFFICE_USERS))
		{
			if (loggedInUser.isCBUser())
			{
				usePrimaryRoles = new String[] { Constants.CBUSER };
				useRegion = null;
				useState = null;
			}
			else if (loggedInUser.isRegionalUser())
			{
				usePrimaryRoles = new String[] { Constants.REGIONALUSER };
				useRegion = loggedInUser.getRegion().getRegion();
				useState = null;
			}
			else if (loggedInUser.isStateUser())
			{
				usePrimaryRoles = new String[] { Constants.STATEUSER };
				useRegion = null;
				useState = loggedInUser.getState().getStateName();
			}
			excludeTestUsers = true;
		}
		else
		{
			return Action.INPUT;
		}

		// Following lines of code is meant to implement table sort functionality
		setOrderByDescending(false);

		setColumnSelected(request.getParameter("columnSelected"));
		if (columnSelected != null && !columnSelected.isEmpty())
		{
			String previousColSelected = null;

			if (session.get("previous_col_selected") != null)
			{
				previousColSelected = (String) session.get("previous_col_selected");
			}
			// if the same column is clicked again
			if (previousColSelected != null && previousColSelected.equalsIgnoreCase(columnSelected))
			{

				if (session.get("sorting_order") != null)
				{
					log.debug("Inside UserSearchAction.userSearch(), orderByDescending: " + orderByDescending);
					log.debug("Inside UserSearchAction.userSearch(),the sorting_order "
							+ session.get("sorting_order"));
					if ((session.get("sorting_order").toString().equalsIgnoreCase("false")))
					{
						orderByDescending = true;
					}
					else
						orderByDescending = false;
				}
			}
			session.put("previous_col_selected", columnSelected);
			session.put("sorting_order", orderByDescending);

		}
		else
		{
			columnSelected = "userName";
			orderByDescending = false;
			session.put("previous_col_selected", columnSelected);
			session.put("sorting_order", orderByDescending);
		}
		log.debug("columnSelected:"+columnSelected);

		List<SiteUser> list = userService.getUserSearchResultList(userNameFilter, firstNameFilter,
				lastNameFilter, emailAddressFilter, usePrimaryRoles, useRegion, useState, userSecondaryRoles,
				columnSelected, orderByDescending, excludeTestUsers);
		log.debug("build siteUser lists from list: " + list.size());
		for (SiteUser siteUser : list)
		{
			boolean viewable = (loggedInUser != null) && loggedInUser.canView(siteUser);
			log.debug("siteUser = " + siteUser);
			log.debug("\tloggedInUser = " + loggedInUser);
			log.debug("\tviewable = " + viewable);
			if (viewable)
			{
				userSearchResultList.add(siteUser);
				boolean editable = loggedInUser.canEdit(siteUser);
				log.debug("\teditable = " + editable);
				siteUserWrapperList.add(new SiteUserWrapper(siteUser, editable));
			}
		}

		return RESPONSE_USER_SEARCH_RESULTS;
	}

	public String clearUserSearch()
	{
		this.userNameFilter = "";
		this.firstNameFilter = "";
		this.lastNameFilter = "";
		this.emailAddressFilter = "";
		this.regionFilter = "All";
		this.stateFilter = "All";
		this.primaryRoleNameFilters = (String[]) application.get("primaryRoleNameFilters");
		this.secondaryRoleNameFilters = (String[]) application.get("secondaryRoleNameFilters");
		this.cbPrimaryRoleSelected = "";
		this.regionalPrimaryRoleSelected = "";
		this.statePrimaryRoleSelected = "";
		userSearch();
		return Action.SUCCESS;
	}

	public boolean canEdit(SiteUser siteUser)
	{
		SiteUser loggedInUser = getLoggedInUser();
		return (loggedInUser != null) && loggedInUser.canEdit(siteUser);
	}

	private SiteUser getLoggedInUser()
	{
		return (SiteUser) session.get("siteUser");
	}

	public LookupService getLookupService()
	{
		return lookupService;
	}

	public void setLookupService(LookupService lookupService)
	{
		this.lookupService = lookupService;
	}

	public final void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public final UserService getUserService()
	{
		return this.userService;
	}

	public final Map<String, Object> getSession()
	{
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.interceptor.SessionAware#setSession(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSession(Map session)
	{
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts2.interceptor.ApplicationAware#setApplication(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setApplication(Map application)
	{
		this.application = application;

	}

	public final Map<String, Object> getApplication()
	{
		return application;
	}

	public void setSearchButtonClicked(boolean searchButtonClicked)
	{
		this.searchButtonClicked = searchButtonClicked;
	}

	public boolean isSearchButtonClicked()
	{
		return searchButtonClicked;
	}

	public void setUserSearchResultList(List<SiteUser> userSearchResultList)
	{
		this.userSearchResultList = userSearchResultList;
	}

	public List<SiteUser> getUserSearchResultList()
	{
		return userSearchResultList;
	}

	public List<SiteUserWrapper> getSiteUserWrapperList()
	{
		return siteUserWrapperList;
	}

	public String getUserNameFilter()
	{
		return userNameFilter;
	}

	public void setUserNameFilter(String userNameFilter)
	{
		this.userNameFilter = userNameFilter;
	}

	public String getFirstNameFilter()
	{
		return firstNameFilter;
	}

	public void setFirstNameFilter(String firstNameFilter)
	{
		this.firstNameFilter = firstNameFilter;
	}

	public String getLastNameFilter()
	{
		return lastNameFilter;
	}

	public void setLastNameFilter(String lastNameFilter)
	{
		this.lastNameFilter = lastNameFilter;
	}

	public String getEmailAddressFilter()
	{
		return emailAddressFilter;
	}

	public void setEmailAddressFilter(String emailAddressFilter)
	{
		this.emailAddressFilter = emailAddressFilter;
	}

	public void setStateFilter(String stateFilter)
	{
		this.stateFilter = stateFilter;
	}

	public String getStateFilter()
	{
		return stateFilter;
	}

	public void setRegionFilter(String regionFilter)
	{
		this.regionFilter = regionFilter;
	}

	public String getRegionFilter()
	{
		return regionFilter;
	}

	public String[] getPrimaryRoleNameFilters()
	{
		return primaryRoleNameFilters;
	}

	/**
	 * When setting the primary role names that are selected, update the
	 * ???PrimaryRoleSelected flags so that we can set the checked state of the
	 * individual checkboxes in the view.
	 * 
	 * @param primaryRoleNameFilters
	 */
	public void setPrimaryRoleNameFilters(String[] primaryRoleNameFilters)
	{
		log.debug("UserSearchAction.setPrimaryRoleNames");
		log.debug("\tselectedPrimaryRoles = " + Arrays.toString(primaryRoleNameFilters));
		this.primaryRoleNameFilters = primaryRoleNameFilters;
		cbPrimaryRoleSelected = Constants.FALSE_STR;
		regionalPrimaryRoleSelected = Constants.FALSE_STR;
		statePrimaryRoleSelected = Constants.FALSE_STR;
		if (primaryRoleNameFilters != null)
		{
			for (String selectedPrimaryRole : primaryRoleNameFilters)
			{
				if (cbUser.equals(selectedPrimaryRole))
				{
					cbPrimaryRoleSelected = Constants.TRUE_STR;
				}
				else if (regionalUser.equals(selectedPrimaryRole))
				{
					regionalPrimaryRoleSelected = Constants.TRUE_STR;
				}
				else if (stateUser.equals(selectedPrimaryRole))
				{
					statePrimaryRoleSelected = Constants.TRUE_STR;
				}
			}
		}
	}

	public String[] getSecondaryRoleNameFilters()
	{
		return secondaryRoleNameFilters;
	}

	public void setSecondaryRoleNameFilters(String[] secondaryRoleNameFilters)
	{
		this.secondaryRoleNameFilters = secondaryRoleNameFilters;
	}

	/**
	 * Keeping a flag value for each of the three primary role names allows us to
	 * handle the checkboxes for these items individually in the struts view.
	 * 
	 * @return "true" or "false" depending on whether the primary role "CB ..."
	 *         was selected
	 */
	public String getCbPrimaryRoleSelected()
	{
		return cbPrimaryRoleSelected;
	}

	public void setCbPrimaryRoleSelected(String selected)
	{
		cbPrimaryRoleSelected = selected;
	}

	/**
	 * Keeping a flag value for each of the three primary role names allows us to
	 * handle the checkboxes for these items individually in the struts view.
	 * 
	 * @return "true" or "false" depending on whether the primary role
	 *         "Regional ..." was selected
	 */
	public String getRegionalPrimaryRoleSelected()
	{
		return regionalPrimaryRoleSelected;
	}

	public void setRegionalPrimaryRoleSelected(String selected)
	{
		regionalPrimaryRoleSelected = selected;
	}

	/**
	 * Keeping a flag value for each of the three primary role names allows us to
	 * handle the checkboxes for these items individually in the struts view.
	 * 
	 * @return "true" or "false" depending on whether the primary role
	 *         "State ..." was selected
	 */
	public String getStatePrimaryRoleSelected()
	{
		return statePrimaryRoleSelected;
	}

	public void setStatePrimaryRoleSelected(String selected)
	{
		statePrimaryRoleSelected = selected;
	}
	
	/**
	 * @return local read-only property initialized to Constants.ALL_VALUES so we
	 *         can use it as text in the struts view
	 */
	public String getAllValues()
	{
		return allValues;
	}

	/**
	 * @return local read-only property initialized to Constants.CBUSER so we can
	 *         use it as text in the struts view
	 */
	public String getCbUser()
	{
		return cbUser;
	}

	/**
	 * @return local read-only property initialized to Constants.REGIONALUSER so
	 *         we can use it as text in the struts view
	 */
	public String getRegionalUser()
	{
		return regionalUser;
	}

	/**
	 * @return local read-only property initialized to Constants.STATEUSER so we
	 *         can use it as text in the struts view
	 */
	public String getStateUser()
	{
		return stateUser;
	}

	class SiteUserWrapper
	{
		private SiteUser siteUser;
		private boolean editable;

		SiteUserWrapper(SiteUser siteUser, boolean editable)
		{
			this.siteUser = siteUser;
			this.editable = editable;
		}

		public SiteUser getSiteUser()
		{
			return siteUser;
		}

		public boolean isEditable()
		{
			return editable;
		}
	}

	/**
	 * @return the columnSelected
	 */
	public String getColumnSelected()
	{
		return columnSelected;
	}

	/**
	 * @param columnSelected
	 *           the columnSelected to set
	 */
	public void setColumnSelected(String columnSelected)
	{
		this.columnSelected = columnSelected;
	}

	/**
	 * @return the orderByDescending
	 */
	public boolean isOrderByDescending()
	{
		return orderByDescending;
	}

	/**
	 * @param orderByDescending
	 *           the orderByDescending to set
	 */
	public void setOrderByDescending(boolean orderByDescending)
	{
		this.orderByDescending = orderByDescending;
	}

	public final void setServletRequest(final HttpServletRequest request)
	{
		this.request = request;
	}

	public final HttpServletRequest getServletRequest()
	{
		return request;
	}

	public HttpServletResponse getServletResponse()
	{
		return response;
	}

	public void setServletResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	public void prepare() throws Exception
	{
		// push list of states on value stack for use in JSP
		List<State> states = lookupService.getStates();
		ValueStack stack = ActionContext.getContext().getValueStack();
		stack.set("states", states);
	}
}