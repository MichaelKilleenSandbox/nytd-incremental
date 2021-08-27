package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.models.DerivedRole;
import gov.hhs.acf.cb.nytd.models.SecondaryUserRole;
import gov.hhs.acf.cb.nytd.models.SiteUserSecondaryUserRole;

import java.io.Serializable;
import java.util.Set;

public class ExtSecondaryUserRole implements Serializable, SecondaryUserRole.Client {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SecondaryUserRole baseModel;
	
	private enum SecondaryRole {
		MANAGER("Manager", "(state users only)"),
		DATA_ANALYST("Data Analyst", "(federal users only)"),
		STATE_AUTH_OFF("State Authorized Official", "(state users only)");
		
		public final String roleName;
		public final String descriptor;
		
		private SecondaryRole (String roleName, String descriptor) {
			this.roleName = roleName;
			this.descriptor = descriptor;
		}
		
		public static SecondaryRole resolveRoleName(String roleName) {
			SecondaryRole resolved = null;
			for (SecondaryRole sr : SecondaryRole.values() ) {
				if (roleName.equalsIgnoreCase(sr.roleName)) {
					resolved = sr;
					break;
				}
			}
			return resolved;
		}
	}
	
	public ExtSecondaryUserRole(SecondaryUserRole baseModel)
	{
		this.baseModel = baseModel;
	}

	public String getLabel()
	{
		StringBuilder label = new StringBuilder(baseModel.getName());
		SecondaryRole sr = SecondaryRole.resolveRoleName(baseModel.getName());
		if (sr != null) label.append(" ").append(sr.descriptor); 
		return label.toString();
	}

	@Override
	public String getName() {
		return baseModel.getName();
	}

	@Override
	public Set<DerivedRole> getDerivedRoles() {
		return baseModel.getDerivedRoles();
	}

	@Override
	public Set<SiteUserSecondaryUserRole> getSiteUserSecondaryUserRoles() {
		return baseModel.getSiteUserSecondaryUserRoles();
	}
	
}
