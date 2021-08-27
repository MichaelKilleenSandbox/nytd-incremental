package gov.hhs.acf.cb.nytd.util;

import gov.hhs.acf.cb.nytd.models.PrimaryUserRole;
import gov.hhs.acf.cb.nytd.models.SiteUser;

public class LoginHelper {

        // SonarLint - Utility classes should not have public constructors, 
        // hence, at least one non-public constructor should be defined
        private LoginHelper() {
            throw new IllegalStateException("Utility class");
        }
        
	/**
	 * CB SharePoint Task#253 -Fix to prevent federal users from using the forgot user id/password function
	 * Checks if the user is federal user and if yes returns true; otherwise false
	 * @param user
	 * @return
	 */
	public static boolean isFederalUser(SiteUser user){
		
		boolean federaluser=false;

		if(user!=null){
			PrimaryUserRole primaryUserRole = user.getPrimaryUserRole();
			if (primaryUserRole != null)
			{
				String primaryUserRoleName = primaryUserRole.getName();
				if (primaryUserRoleName.equalsIgnoreCase(Constants.CBUSER) 
						|| primaryUserRoleName.equalsIgnoreCase(Constants.REGIONALUSER))
				{
					federaluser = true;
				}			
			}
		}
		return federaluser;
	}
        
        /**
	 * Get user type for the site user
	 * @param SiteUser siteUser
	 * @return String userType
	 */
        public static String getUserType(SiteUser siteUser) {
        PrimaryUserRole primaryUserRole = siteUser.getPrimaryUserRole();
        String userType = "User";
            if (primaryUserRole != null) {
                if (primaryUserRole.getName().equalsIgnoreCase("System Administrator")) {
                    userType = "System administrator";
                } else if (primaryUserRole.getName().equalsIgnoreCase("CB Central Office Staff Member")) {
                    userType = "Central office user";
                } else if (primaryUserRole.getName().equalsIgnoreCase("Regional Office User")) {
                    userType = "Region " + siteUser.getRegion().getRegionCode() + " office user";
                } else if (primaryUserRole.getName().equalsIgnoreCase("State User")) {
                     userType = siteUser.getState().getAbbreviation() + " user";
                }
            }
            return userType;
        }
	

}
