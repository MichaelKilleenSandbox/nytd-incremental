package gov.hhs.acf.cb.nytd.actions.security;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.openid.connect.sdk.*;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

/**
 * Oauth2 action. This class invoke an initial auth webservice endpoint with 
 * OpenID Connect/oauth2 spec for the federal user via PIV login authentication. 
 * Upon successful auhentication, AMS (Identity Server) will issue an authorization code 
 * which will be used to invoke further webservice endpoints to process in federal login action
 * 
 * @author 17628
 */
public class Oauth2Action extends ActionSupport implements SessionAware
{
	protected transient Logger log = Logger.getLogger(getClass());
	private Map<String, Object> session;
	@Getter @Setter private String authUrl;
	
	/**
	 * Request authentication and authorization to obtain authorization code
	 * via authorization endpoint call.
	 * 
	 * @param none
	 * @return String success/error
	 */
	public String requestAuth()
	{
		log.info("Oatuh2Action piv login - federal user auth request");

		// Load properties file
		final Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/config/systemConfig.properties"));
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		// The client ID provisioned by the OpenID provider when the client was registered
		ClientID clientID = new ClientID(properties.getProperty("security.oauth2.client.registration.client-id"));
		
		// Generate random state string to securely pair the callback to this request
		State state = new State();
		
		// Generate nonce for the ID token
		Nonce nonce = new Nonce();
		
		// Define scope
		Scope scope = new Scope();
		scope.add("openid");
		scope.add("profile");
		scope.add("email");
		
		try {
			// The client callback URL
			URI callback = new URI(properties.getProperty("security.oauth2.client.registration.redirect-url"));

			 // Compose the OpenID authentication request (for the code flow)
			 AuthenticationRequest request = new AuthenticationRequest.Builder(
					 new ResponseType("code"),
					 scope,
					 clientID,
					 callback)
					 .endpointURI(new URI(properties.getProperty("security.oauth2.client.registration.auth-endpoint")))
					 .state(state)
					 .nonce(nonce)
					 .build();
		
			 log.info("auth endpoint is: " + request.toURI());
			 this.authUrl = request.toURI().toString();
			 
		} catch (URISyntaxException ex) {
			log.error("error in URI syntax: "+ex.getMessage());
			return Action.ERROR;
		}
		
		// Add to session attribute for later use to protect from request forgery
		this.session = ActionContext.getContext().getSession();
		session.put("state", state);

		return Action.SUCCESS;
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
	
	public final Map<String, Object> getSession()
	{
		return session;
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
