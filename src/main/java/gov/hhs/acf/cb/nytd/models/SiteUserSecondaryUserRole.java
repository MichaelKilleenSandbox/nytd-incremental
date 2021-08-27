package gov.hhs.acf.cb.nytd.models;

/**
 * SiteUserSecondaryUserRole allows us to treat user's roles as first class objects in the system.
 * (gives us the ability to track created by info, etc.)
 */
public class SiteUserSecondaryUserRole extends PersistentObject
{

	protected SiteUser siteUser;
	protected SecondaryUserRole secondaryUserRole;

	public SiteUserSecondaryUserRole()
	{
	}

	public SiteUserSecondaryUserRole(Long siteUserSecondaryUserRoleId)
	{
		this.id = siteUserSecondaryUserRoleId;
	}

	public SiteUser getSiteUser()
	{
		return siteUser;
	}

	public void setSiteUser(SiteUser siteUser)
	{
		this.siteUser = siteUser;
	}

	public SecondaryUserRole getSecondaryUserRole()
	{
		return secondaryUserRole;
	}

	public void setSecondaryUserRole(SecondaryUserRole secondaryUserRole)
	{
		this.secondaryUserRole = secondaryUserRole;
	}
}