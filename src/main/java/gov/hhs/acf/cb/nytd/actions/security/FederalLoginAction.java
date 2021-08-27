package gov.hhs.acf.cb.nytd.actions.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.nimbusds.jwt.*;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.token.*;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.op.*;
import com.nimbusds.openid.connect.sdk.token.*;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.PrimaryUserRole;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.service.LoginService;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 * Federal login action. This class process federal login with OpenID Connect/oauth2 
 * spec with a series of web service endpoint calls to validate the request/response
 * for the federal user via PIV login authentication.
 * 
 * @author 17628
 */
@SuppressWarnings("serial")
public class FederalLoginAction extends ActionSupport implements SessionAware
{
	protected transient Logger log = Logger.getLogger(getClass());
	private LoginService loginService;
	private Map<String, Object> session;
	@Getter @Setter private String username;
	@Getter @Setter private String email;
	@Getter @Setter private String successUrl;
	@Getter @Setter private HttpServletRequest request;
        
	/**
	 * Process federal user login. The flow is:
	 * 1. Handle response from authentication server to retrieve auth code
	 * 2. Token endpoint call and response handling to retrieve access token/access id/barer token
	 * 3. Decode the token response and verify the token
	 * 4. User info endpoint call and response handling to retrieve user profile
	 * 5. Match email in user info response to nytd user's email to grant login.
	 * 
	 * @params none
	 * @return String success/error
	 */
	public String processFederalLogin()
	{
		log.info("auth server redirect to FederalLoginAction");
		
		// Load properties file
		final Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/config/systemConfig.properties"));
		}
		catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		// Grab the redirect url with query string
		HttpServletRequest req = ServletActionContext.getRequest();
		String requestURI = req.getRequestURL().toString() + "?" + req.getQueryString();
		log.info("requestURI is: "+ requestURI);
		this.session = ActionContext.getContext().getSession();
		
		// Parse the auth response
		AuthenticationResponse authResp = null;
		try {
		  authResp = AuthenticationResponseParser.parse(new URI(requestURI));
		} catch (ParseException | URISyntaxException e) {
			log.error("error in parse auth response: "+e.getMessage());
			return Action.ERROR;
		}
		
		// Check the state to protect from request forgery
		if (!req.getParameter("state").equals(req.getSession().getAttribute("state").toString())) {
			log.error("error in request state");
			return Action.ERROR;
		}

		// Check if auth server return any errors
		if (authResp instanceof AuthenticationErrorResponse) {
		  ErrorObject error = ((AuthenticationErrorResponse) authResp).getErrorObject();
		  log.error("error in authentication response: "+error.getDescription());
		  return Action.ERROR;
		}

		// Retrieve the authorization code, to use it later at the token endpoint
		AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) authResp;
		AuthorizationCode authCode = successResponse.getAuthorizationCode();
		log.info("authorization code: " + authCode);
		
		// Get Open Id tokens
		OIDCTokens tokens = getTokens(authCode, properties);
		if (tokens == null ) {
			log.error("error in getting OIDC tokens");
			return Action.ERROR;
		}
		// Verify tokens
		Boolean validTokens = verifyTokens(tokens, properties);
                log.info("validTokens: " + validTokens);
		
		// User info call
		JSONObject userInfo = null;
		if(Boolean.TRUE.equals(validTokens)) {
			userInfo = getUserInfo(tokens, properties);
			if (userInfo == null ) {
				log.error("error in getting user info");
				return Action.ERROR;
			} else {
                            log.info("email is: " + userInfo.get("email").toString());
                        }
		} else {
			log.error("error - tokens not valid");
			return Action.ERROR;
		}
		
		// Federal user login
		SiteUser federalUser = loginFederalUserByEmail(userInfo.get("email").toString());
		if (federalUser == null ) {
			log.error("error in getting federal user");
			return Action.ERROR;
		}
		log.info("logged in federalUser username is: " + federalUser.getUserName());
		
		// See struts.xml ${successUrl} to redirect to.
		this.successUrl = "dashboard";
		
		return Action.SUCCESS;
		
	}
	
	/**
	 * Allow user login to the system by creating the federal user's session
	 * with given email address.
	 * 
	 * @params email
	 * @return SiteUser siteUser
	 */
	public SiteUser loginFederalUserByEmail (String email) {
		log.info("In loginFederalUserByEmail(), email is: "+email);
		SiteUser federalSiteUser =null;
		
		// Check email address provided
		if (email != null && !email.trim().isEmpty()) {
			
			// Invalidate session if exists, and create new user session
			((SessionMap)this.session).invalidate();
			ServletActionContext.getRequest().getSession().invalidate();
			this.session = ActionContext.getContext().getSession();
			ServletActionContext.getRequest().getSession(true);
			session.put("AUTHENTICATED", true);
			log.info("New SessionId: "+ ServletActionContext.getRequest().getSession().getId());

			// Service method call to get federal user by email
			federalSiteUser = getLoginService().getFederalUserByEmail(email, session);
			if (federalSiteUser == null) {
				for (String errorMessage : getLoginService().getLoginErrors(session)) {
                                    this.addActionError(errorMessage);
				}
				return null;
			}
			
			// Service method call to load privileges.
			getLoginService().loadPrivileges(federalSiteUser);
			PrimaryUserRole primaryUserRole = federalSiteUser.getPrimaryUserRole();
			
			// Assign user type for the federal user
			String userType = "User";
			if (primaryUserRole != null) {
				if (primaryUserRole.getName().equalsIgnoreCase("CB Central Office Staff Member")) {
					userType = "Central office user";
				} else if (primaryUserRole.getName().equalsIgnoreCase("Regional Office User")) {
					userType = "Region " + federalSiteUser.getRegion().getRegionCode() + " office user";
				}
			}
			log.info( federalSiteUser.getUserName() + " with primary user role as " + userType+ " has logged in.");

			// Add session values
			session.put("loggedIn", "true");
			session.put("siteUser", federalSiteUser);
		}
		
		return federalSiteUser;
	}
	
	/**
	 * Get id token, access token, and bearer token in form of OIDCTokens
	 * object by invoking token endpoint with given authorization code 
	 * obtained from response in auth endpoint call.
	 * 
	 * @param AuthorizationCode authCode, Properties properties
	 * @return OIDCTokens
	 */
	private OIDCTokens getTokens(AuthorizationCode authCode, Properties properties) {
		URI tokenUri = null;
		URI redirectUri = null;
		
		// Get client id and client secret from properties file
		ClientID clientId = new ClientID(
				properties.getProperty("security.oauth2.client.registration.client-id"));
		Secret clientSecret = new Secret(
				properties.getProperty("security.oauth2.client.registration.client-secret"));
		try {
			// Get the token endpoint from properties file.
			tokenUri = new URI(
					properties.getProperty("security.oauth2.client.registration.token-endpoint"));
			log.info("tokenUri: " + tokenUri.toString());
			
			// Get the redirect uri from properties file.
			redirectUri = new URI(
					properties.getProperty("security.oauth2.client.registration.redirect-url"));
			log.info("redirectUri: " + redirectUri.toString());
		} catch (URISyntaxException ex) {
			log.error("error in URI syntax: "+ex.getMessage());
			return null;
		}
		
		// Construct OICD token request		
		TokenRequest tokenReq = new TokenRequest(
				tokenUri,
				new ClientSecretBasic(clientId, clientSecret),
				new AuthorizationCodeGrant(authCode, redirectUri));

		// Invoke token endpoint
		HTTPResponse tokenHTTPResp = null;
		try {
			tokenHTTPResp = tokenReq.toHTTPRequest().send();
		} catch (SerializeException | IOException e) {
			log.error("error in token web service call: "+ e.getMessage());
			return null;
		}

		// Parse and check response
		TokenResponse tokenResponse = null;
		try {
			tokenResponse = OIDCTokenResponseParser.parse(tokenHTTPResp);
		} catch (ParseException pe) {
			log.error("error in parse : "+ pe.getMessage());
			return null;
		}

		// Check errors in token response
		if (tokenResponse instanceof TokenErrorResponse) {
			ErrorObject error = ((TokenErrorResponse) tokenResponse).getErrorObject();
			log.error("error in token response : "+ error.getDescription());
			return null;
		}
		
		// Retrieve tokens from the response
		OIDCTokenResponse oidcResponse = (OIDCTokenResponse) tokenResponse;
		OIDCTokens oidcTokens = oidcResponse.getOIDCTokens();
		
		log.info("Access Token is: " + oidcTokens.getAccessToken().toJSONString());
		log.info("Bearer Access Token is: " + oidcTokens.getBearerAccessToken().toAuthorizationHeader());
		log.info("AccessId is: " + oidcTokens.getIDTokenString());
		
		return oidcTokens;
	}
	
	/**
	 * Verify tokens obtained from response in token endpoint call 
	 * 
	 * @param OIDCTokens oidcTokens, Properties properties
	 * @return Boolean
	 */
	private Boolean verifyTokens(OIDCTokens oidcTokens, Properties properties) {
		URI issuerUri = null;
		String providerInfo = null;
                InputStream stream = null;
		
		// Get issuer uri from properties file
		try {
			issuerUri = new URI(
					properties.getProperty("security.oauth2.client.registration.issuer-uri"));
			log.info("issuerUri: " + issuerUri.toString());
		} catch (URISyntaxException ex) {
			log.error("error in issuer URI syntax: "+ ex.getMessage());
			return false;
		}
                
		// Construct full issuer url
		try  {
			URL providerConfigurationURL = issuerUri.resolve("/.well-known/openid-configuration").toURL();
			stream = providerConfigurationURL.openStream();
		} catch (Exception e) {
			log.error("error in well known configuration endpoint: "+ e.getMessage());
			return false;
		}
		
		// Read all data from URL
		try (Scanner s = new Scanner(stream)) {
		  providerInfo = s.useDelimiter("\\A").hasNext() ? s.next() : "";
		  log.info("Open ID Provider Info: "+providerInfo);
		} catch (Exception e) {
			log.error("error in reading provider info: "+ e.getMessage());
			return false;
		}
		
		// Now, verify tokens
		Boolean isValid = false;
		RSAKey rsaJWK = null;
		RSAKey rsaPublicJWK = null;
		JWSSigner signer = null;
		JWTClaimsSet claimsSet = null;
		SignedJWT signedJWT = null;
		OIDCProviderMetadata providerMetadata = null;
		try {
			providerMetadata = OIDCProviderMetadata.parse(providerInfo);
			log.info("provider Metadata: "+providerMetadata);
			
			// RSA signatures require a public and private RSA key pair, the public key 
			// must be made known to the JWS recipient in order to verify the signatures
			rsaJWK = new RSAKeyGenerator(2048)
					.keyID(oidcTokens.getIDTokenString())
					.generate();
			rsaPublicJWK = rsaJWK.toPublicJWK();

			// Create RSA-signer with the private key
			signer = new RSASSASigner(rsaJWK);
			
			// Prepare JWT with claims set
			claimsSet = new JWTClaimsSet.Builder()
					.subject("subject")
					.issuer(issuerUri.toString())
					.expirationTime(new Date(new Date().getTime() + 60 * 1000))
					.build();
			signedJWT = new SignedJWT(
					new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
					claimsSet);

			// Compute the RSA signature
			signedJWT.sign(signer);
			String s = signedJWT.serialize();

			// On the consumer side, parse the JWS and verify its RSA signature
			signedJWT = SignedJWT.parse(s);
		} catch (JOSEException | ParseException | java.text.ParseException e) {
			log.error("error in key signature: "+ e.getMessage());
			return false;
		}
		
		JWSVerifier verifier = null;
		try {
			verifier = new RSASSAVerifier(rsaPublicJWK);
			isValid = Boolean.valueOf(signedJWT.verify(verifier));
		} catch (JOSEException je) {
			log.error("error in jose JWT verifier: "+ je.getMessage());
			return false;
		}
		log.info("isValid: " + isValid);
		
		return isValid;
	}
	
	/**
	 * Get user information in form of JSONObject by invoking UserInfo 
	 * endpoint with given tokens that had been verified.
	 * 
	 * @param OIDCTokens tokens, Properties properties
	 * @return JSONObject
	 */
	private JSONObject getUserInfo(OIDCTokens tokens, Properties properties) {
		URI userInfoUri = null;
		
		// Get user info endpoint from properties file
		try {
			userInfoUri = new URI(
					properties.getProperty("security.oauth2.client.registration.userinfo-endpoint"));
			log.info("userInfoUri: " + userInfoUri.toString());
		} catch (URISyntaxException ex) {
			log.error("error in user info URI syntax: "+ex.getMessage());
			return null;
		}
		
		// Construct user info request with the endpoint and bearer token.
		UserInfoRequest userInfoReq = new UserInfoRequest(
				userInfoUri, 
				tokens.getBearerAccessToken());
		
		// User info request
		HTTPResponse userInfoHTTPResp = null;
		try {
			userInfoHTTPResp = userInfoReq.toHTTPRequest().send();
		} catch (SerializeException | IOException e) {
			log.error("error in user info web service call: "+e.getMessage());
			return null;
		}
		
		// Parse user info response
		UserInfoResponse userInfoResponse = null;
		try {
			userInfoResponse = UserInfoResponse.parse(userInfoHTTPResp);
		} catch (ParseException e) {
			log.error("error in user info parse: "+e.getMessage());
			return null;
		}

		// Handle errors in response
		if (userInfoResponse instanceof UserInfoErrorResponse) {
			ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
			log.error("error in user info response: "+error.getDescription());
			return null;
		}

		// Handle success response
		UserInfoSuccessResponse userInfoSuccessResponse = userInfoResponse.toSuccessResponse();
		JSONObject claims = userInfoSuccessResponse.getUserInfo().toJSONObject();
		
		log.info("user info claims: " + claims);
		
		return claims;
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
	 * @param session the session to set
	 * @see org.apache.struts2.interceptor.SessionAware#setSession(Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void setSession(final Map session)
	{
		this.session = session;
	}
	
	public final Map<String, Object> getSession()
	{
		return session;
	}
	
}
