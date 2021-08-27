package gov.hhs.acf.cb.nytd.models;

// Generated May 20, 2009 10:16:43 AM by Hibernate Tools 3.2.4.GA

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/*
 * PrimaryUserRole generated by hbm2java
 */
public class PrimaryUserRole extends PersistentObject
{
	private State state;
	private Region region;
	private String name;
	private Boolean roleType;

	private Set<DerivedRole> derivedRoles = new HashSet<DerivedRole>(0);
	private Set<SiteUser> siteUsers = new HashSet<SiteUser>(0);

	public PrimaryUserRole()
	{
	}

	public PrimaryUserRole(Long primaryUserRoleid)
	{
		this.id = primaryUserRoleid;
	}

	public PrimaryUserRole(Long primaryUserRoleId, State state, Region region, String name, Boolean roleType,
			Calendar createdDate, String createdBy, Calendar updateDate, String updateBy, String description,
			Set<DerivedRole> derivedRoles, Set<SiteUser> siteUsers)
	{
		this.id = primaryUserRoleId;
		this.state = state;
		this.region = region;
		this.name = name;
		this.roleType = roleType;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.updateDate = updateDate;
		this.updateBy = updateBy;
		this.description = description;
		this.derivedRoles = derivedRoles;
		this.siteUsers = siteUsers;
	}

	public State getState()
	{
		return this.state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public Region getRegion()
	{
		return this.region;
	}

	public void setRegion(Region region)
	{
		this.region = region;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Boolean getRoleType()
	{
		return this.roleType;
	}

	public void setRoleType(Boolean roleType)
	{
		this.roleType = roleType;
	}

	public Set<DerivedRole> getDerivedRoles()
	{
		return this.derivedRoles;
	}

	public void setDerivedRoles(Set<DerivedRole> derivedRoles)
	{
		this.derivedRoles = derivedRoles;
	}

	public Set<SiteUser> getSiteUsers()
	{
		return this.siteUsers;
	}

	public void setSiteUsers(Set<SiteUser> siteUsers)
	{
		this.siteUsers = siteUsers;
	}

}
