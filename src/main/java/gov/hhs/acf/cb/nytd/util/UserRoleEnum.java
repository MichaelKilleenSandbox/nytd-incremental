package gov.hhs.acf.cb.nytd.util;


/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Apr 22, 2010
 */
public enum UserRoleEnum
{
	ADMIN("System Administrator"), FEDERAL("CB Central Office Staff Member"), REGIONAL("Regional Office User"), STATE(
			"State User");

	private String roleName;

	UserRoleEnum(String roleName)
	{
		this.roleName = roleName;
	}

	public String getRoleName()
	{
		return roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public static UserRoleEnum getRole(String roleName)
	{
		for (UserRoleEnum role : UserRoleEnum.values())
		{
			if (role.getRoleName().equals(roleName))
			{
				return role;
			}
		}

		return null;
	}
}
