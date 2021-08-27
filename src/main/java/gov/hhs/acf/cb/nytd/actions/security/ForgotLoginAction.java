package gov.hhs.acf.cb.nytd.actions.security;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.LoginService;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.service.UserService;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import gov.hhs.acf.cb.nytd.util.LoginHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Satinder Gill (18922)
 * @author Adam Russell (18816)
 */
public class ForgotLoginAction extends ActionSupport implements SessionAware
{
	// logger
	protected transient Logger log = Logger.getLogger(getClass());

	// services
	private UserService userService;
	private LoginService loginService;
	private MessageService messageServiceP3;

	// action properties
	private String userEmail;
	private String confirmation = "";
	private boolean includeHelpLink = false;
        
        // session
        private Map<String, Object> session;
        
        // constant
        private static final String EMAILVALID = "emailValid";

        /**
	 * Request account info when user forgot user id
	 * @return String success/error
	 */
	public final String forgotUsername()
	{
            // Get email address entered in the fogot user id form
            String forgotUserIdEmail = getUserEmail().trim();
            
            // Service call to get a list of users for forgot user id
            List<SiteUser> allUserListInForgotId = getLoginService().getAllActiveUserListByEmail(forgotUserIdEmail);
            log.info("All user list in forgot user id: " + allUserListInForgotId);
            
            // Get validation Message and check if forgot user id email is valid
            String forgotIdValidationMsg = getValidationMessage(forgotUserIdEmail, allUserListInForgotId);
            if (!forgotIdValidationMsg.equals(EMAILVALID)) {
                
                // Display the error message
                this.addActionError(forgotIdValidationMsg);
                return Action.ERROR;
                
            } else {
                
                // Email is valid, only an unique user for the requested email
                SiteUser siteUser = allUserListInForgotId.get(0);
                
                // Now check if the requested email is a federal user.
                if(LoginHelper.isFederalUser(siteUser)) {
                    this.addActionError("This email is associated with a federal user account, and this form is only for state user accounts.");
                    log.info("Checking whether Federal user " + siteUser.getUserName() + " is trying to use the forgot userid/password feature.");
                    return Action.ERROR;
                }
                
                // Sending out email for the state user
                log.info("SiteUSer: "+siteUser.getUserName()+" used the forgot username feature");
                Map<String, Object> namedParams = new HashMap<>();
                namedParams.put("firstName", siteUser.getFirstName());
                namedParams.put("lastName", siteUser.getLastName());
                namedParams.put("userName", siteUser.getUserName());
                namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
                namedParams.put("dateTime", DateUtil.getHourMintueSecondWithTimeZone(new GregorianCalendar()));
                Message systemMsg = messageServiceP3.createSystemMessage(MessageService.FORGOT_LOGIN_USERNAME, namedParams);
                List<String> emailAddresses = new ArrayList<>();
                emailAddresses.add(siteUser.getEmailAddress());
                messageServiceP3.sendEmailNotification(systemMsg, emailAddresses);
                setConfirmation("Your username has been sent to your email address. "
                    + "If you do not receive the email within 10 minutes " + "please contact ");
                setIncludeHelpLink(true);

            }
            
            return Action.SUCCESS;
	}

        /**
	 * Request password reset when user forgot password
	 * @return String success/error
	 */
	public final String forgotPassword() throws MalformedURLException
	{
             // Get email address entered in the fogot password form
            String forgotPwdEmail = getUserEmail().trim();
            
            // Service call to get a list of users for forgot password
            List<SiteUser> allUserListInForgotPwd = getLoginService().getAllActiveUserListByEmail(forgotPwdEmail);
            log.info("All user list in forgot password: " + allUserListInForgotPwd);
            
            // Get validation Message and check if forgot password email is valid
            String forgotPassValidationMsg = getValidationMessage(forgotPwdEmail, allUserListInForgotPwd);
            if (!forgotPassValidationMsg.equals(EMAILVALID)) {
                
                // Display the error message
                this.addActionError(forgotPassValidationMsg);
                return Action.ERROR;
                
            } else {
                
                // Only an unique user for the requested email
                SiteUser siteUser = allUserListInForgotPwd.get(0);
                
                // Check if the requested email is a federal user.
                if(LoginHelper.isFederalUser(siteUser)) {
                    this.addActionError("This email is associated with a federal user account, and this form is only for state user accounts.");
                    log.info("Checking whether Federal user " + siteUser.getUserName() + " is trying to use the forgot userid/password feature.");
                    return Action.ERROR;
                }
                    // Sending out email with password reset link for the state user
                    log.info("SiteUSer: "+siteUser.getUserName()+" used the forgot password feature");
                    HttpServletRequest request = ServletActionContext.getRequest();
                    String passwordChangeLink = userService.processPasswordChangeKey(siteUser, request);
                    Map<String, Object> namedParams = new HashMap<>();
                    namedParams.put("firstName", siteUser.getFirstName());
                    namedParams.put("lastName", siteUser.getLastName());
                    namedParams.put("passwordChangeLink", passwordChangeLink);
                    namedParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
                    namedParams.put("dateTime", DateUtil.getHourMintueSecondWithTimeZone(new GregorianCalendar()));
                    Message systemMsg = messageServiceP3.createSystemMessage(MessageService.FORGOT_LOGIN_PASSWORD, namedParams);
                    List<String> emailAddresses = new ArrayList<>();
                    emailAddresses.add(siteUser.getEmailAddress());
                    messageServiceP3.sendEmailNotification(systemMsg, emailAddresses);
                    setConfirmation("A link to reset your password has been sent to your email address. "
                                    + "If you do not receive the email within 10 minutes please contact ");
                    setIncludeHelpLink(true);     
		}

		return Action.SUCCESS;
	}

        /**
	 * @return the messageServiceP3
	 */
	public MessageService getMessageServiceP3()
	{
		return messageServiceP3;
	}

        /**
	 * @param messageServiceP3
	 *           the messageServiceP3 to set
	 */
	public void setMessageServiceP3(MessageService messageServiceP3)
	{
		this.messageServiceP3 = messageServiceP3;
	}

	/**
	 * @param userService
	 *           the service to set
	 */
	public final void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the service
	 */
	public final UserService getUserService()
	{
		return userService;
	}
	
	/**
	 * @param loginService the service to set
	 */
	public final void setLoginService(final LoginService loginService)
	{
		this.loginService = loginService;
	}

	/**
	 * @return the loginService
	 */
	public final LoginService getLoginService()
	{
		return loginService;
	}

        /**
	 * @param userEmail to set
	 */
	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

        /**
	 * @return the userEmail
	 */
	public String getUserEmail()
	{
		return userEmail;
	}

        /**
	 * @param confirmation to set
	 */
	public void setConfirmation(String confirmation)
	{
		this.confirmation = confirmation;
	}

        /**
	 * @return the confirmation
	 */
	public String getConfirmation()
	{
		return confirmation;
	}
	
        /**
	 * @param includeHelpLink to set
	 */
	public void setIncludeHelpLink (boolean flag) {
		includeHelpLink = flag;
	}
	
        /**
	 * @return the includeHelpLink
	 */
	public boolean getIncludeHelpLink() {
		return includeHelpLink;
	}
        
        /**
	 * @param session the session to set
	 * @see org.apache.struts2.interceptor.SessionAware#setSession(Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void setSession(final Map<String, Object> session)
	{
		this.session = session;
	}
	
        /**
	 * @return the session
	 */
	public final Map<String, Object> getSession()
	{
		return session;
	}
	
        // Check valid email pettern
	private boolean isValidEmail(String email)
	{
	    Pattern pattern =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(email);	    
	    return matcher.matches();	   
	}
        
        // Get message in validation
        private String getValidationMessage(String email, List<SiteUser> allUserList) {
            
            String msg = EMAILVALID;
            
            // Check first if email provided and valid email.
            if (StringUtils.isEmpty(email) || !isValidEmail(email)) {
                log.error("Email empty or invalid.");
		msg = "Email address is either empty or invalid. Please enter valid email address.";
                return msg;
            }

            // Check if email exist or exist multiple.
            if (allUserList.isEmpty()){
                log.error("No such user exists in the system.");
                msg = "No such user exists in the system";
                return msg;
            } else if (allUserList.size() > 1) {
                log.error("Multiple emails for the user exist in the system.");
		msg = "Multiple emails for the user exist in the system";
                return msg;
            }
            
            return msg;
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
