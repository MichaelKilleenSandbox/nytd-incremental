package gov.hhs.acf.cb.nytd.models;

/**
 * SiteUserStateRegionMapping allows us to treat user's roles as first class objects in the system.
 * (gives us the ability to track created by info, etc.)
 */
public class SiteUserStateRegionMapping extends PersistentObject
{

	protected SiteUser siteUser;
	protected State state;

	public SiteUserStateRegionMapping()
	{
	}

	public SiteUserStateRegionMapping(Long siteUserStateRegionMappingId)
	{
		this.id = siteUserStateRegionMappingId;
	}

	public SiteUser getSiteUser()
	{
		return siteUser;
	}

	public void setSiteUser(SiteUser siteUser)
	{
		this.siteUser = siteUser;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}
}