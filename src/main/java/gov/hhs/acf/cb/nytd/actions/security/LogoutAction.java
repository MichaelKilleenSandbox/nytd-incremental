package gov.hhs.acf.cb.nytd.actions.security;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.LoginService;
import gov.hhs.acf.cb.nytd.util.LoginHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;


/**
 * Logs a user out, removing appropriate objects from session.
 * 
 * @author Adam Russell (18816)
 */
@SuppressWarnings("serial")
public class LogoutAction extends ActionSupport implements SessionAware {
        private static final String SITEUSER = "siteUser";
	protected transient Logger log = Logger.getLogger(getClass());
	@SuppressWarnings("unchecked")
	private Map<String, Object> session;
	@Getter @Setter private LoginService loginService;

	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS upon completion
	 */
        @Override
	public final String execute() {
            SiteUser siteUser = null; 		
            if (session.containsKey(SITEUSER)) {
                siteUser = (SiteUser) session.get(SITEUSER);
            }	
            if (siteUser != null) {
                // Log the logout.
                String userType = LoginHelper.getUserType(siteUser);
                log.info(userType + " " + siteUser.getUserName() + " has logged out.");
            }
            session.remove("loggedIn");
            session.remove(SITEUSER);
            return Action.SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void setSession(final Map<String, Object> session) {
            this.session = session;
	}
        
        // Handler to read serializable objects (i.e.) session, request etc.
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.info("reading serialization object");
            in.defaultReadObject();
        }
        
        // Handler to write serializable objects (i.e.) session, request etc.
        private void writeObject(ObjectOutputStream out) throws IOException {
            log.info("writing serialization object");
            out.defaultWriteObject();
        }

}
