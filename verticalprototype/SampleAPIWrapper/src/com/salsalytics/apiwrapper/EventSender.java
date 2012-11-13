package com.salsalytics.apiwrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.teamlazerbeez.crm.sf.core.SObject;
import com.teamlazerbeez.crm.sf.rest.*;
import com.teamlazerbeez.crm.sf.soap.*;
import com.teamlazerbeez.crm.sf.soap.ApiException;
import com.teamlazerbeez.crm.sf.soap.jaxwsstub.metadata.ApexClass;

public class EventSender {
	static String orgID = "00DE0000000e66B";
	static String username = "";
	static String password = "";
	
	
	public static void main(String[] args) throws ApiException, MalformedURLException {
//		EventSender sender = new EventSender();
//		sender.sendEvent("", "");
		
		ConnectionPool<String> soapPool = new ConnectionPoolImpl<String>(orgID);
		RestConnectionPoolImpl<String> restPool = new RestConnectionPoolImpl<String>();
		ConnectionBundle bundle = soapPool.getConnectionBundle(orgID);
		
		soapPool.configureOrg(orgID, username, password, 5);
		BindingConfig bindingConfig = soapPool.getConnectionBundle(orgID).getBindingConfig();
		String host = new URL(bindingConfig.getPartnerServerUrl()).getHost();
		String token = bindingConfig.getSessionId();
		restPool.configureOrg(orgID, host, token);
		
		ApexConnection apexConn = bundle.getApexConnection();
		apexConn.executeAnonymous("EventAdder.addEvent(\"test_wrapper_login\", \"username:jdoe\");");
	}
	
	public void sendEvent(String title, String map) {
		StringBuffer buffer = new StringBuffer();
	    java.net.URL url;
	    String result;
	    String line;
	    String response = "";
	    URLConnection connection;
	
	    try {
	    	url = new URL( "https://na9.salesforce.com/services/data/" );
	    	connection = url.openConnection(  );
	    	connection.setRequestProperty("Authorization", " Bearer "  );
	    	connection.setDoOutput( true );
	
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    	while ((line = rd.readLine()) != null) {
	    		response += line;
	    	}
	    	rd.close();
	
	    	System.out.println( "\n *INDEX RESPONSE XML * \n\n" + response + "\n\n");            
	
	    }
	    catch(Exception e){
	    	System.out.println("Errors...");
	    }
	}
	
//	public void login() {
//		initParams = { 
//			    @WebInitParam(name = "clientId", value = 
//			            "3MVG9lKcPoNINVBJSoQsNCD.HHDdbugPsNXwwyFbgb47KWa_PTv"),
//			    @WebInitParam(name = "clientSecret", value = "5678471853609579508"),
//			    @WebInitParam(name = "redirectUri", value = 
//			            "https://localhost:8443/RestTest/oauth/_callback"),
//			    @WebInitParam(name = "environment", value = 
//			            "https://na1.salesforce.com/services/oauth2/token")  }
//			 
//			HttpClient httpclient = new HttpClient();
//			PostMethod post = new PostMethod(environment);
//			post.addParameter("code",code);
//			post.addParameter("grant_type","authorization_code");
//
//			   /** For session ID instead of OAuth 2.0, use "grant_type", "password" **/
//			post.addParameter("client_id",clientId);
//			post.addParameter("client_secret",clientSecret);
//			post.addParameter("redirect_uri",redirectUri);
//	}
	
//	public static void oAuthSessionProvider(String loginHost, String username,
//	        String password, String clientId, String secret)
//	        throws HttpException, IOException 
//	{
//	    // Set up an HTTP client that makes a connection to REST API.
//	    DefaultHttpClient client = new DefaultHttpClient();
//	    HttpParams params = client.getParams();
//	    HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
//	    params.setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 30000);
//
//	    // Set the SID.
//	    System.out.println("Logging in as " + username + " in environment " + loginHost);
//	    String baseUrl = loginHost + "/services/oauth2/token";
//	    // Send a post request to the OAuth URL.
//	    HttpPost oauthPost = new HttpPost(baseUrl);
//	    // The request body must contain these 5 values.
//	    List<BasicNameValuePair> parametersBody = new ArrayList<BasicNameValuePair>();
//	    parametersBody.add(new BasicNameValuePair("grant_type", "password"));
//	    parametersBody.add(new BasicNameValuePair("username", username));
//	    parametersBody.add(new BasicNameValuePair("password", password));
//	    parametersBody.add(new BasicNameValuePair("client_id", clientId));
//	    parametersBody.add(new BasicNameValuePair("client_secret", secret));
//	    oauthPost.setEntity(new UrlEncodedFormEntity(parametersBody, HTTP.UTF_8));
//
//	    // Execute the request.
//	    System.out.println("POST " + baseUrl + "...\n");
//	    HttpResponse response = client.execute(oauthPost);
//	    int code = response.getStatusLine().getStatusCode();
//	    Map<String, String> oauthLoginResponse = (Map<String, String>)
//	        JSON.parse(EntityUtils.toString(response.getEntity()));
//	    System.out.println("OAuth login response");
//	    for (Map.Entry<String, String> entry : oauthLoginResponse.entrySet()) 
//	    {
//	        System.out.println(String.format("  %s = %s", entry.getKey(), entry.getValue()));
//	    }
//	    System.out.println("");
//
//	    // Get user info.
//	    String userIdEndpoint = oauthLoginResponse.get("id");
//	    String accessToken = oauthLoginResponse.get("access_token");
//	    List<BasicNameValuePair> qsList = new ArrayList<BasicNameValuePair>();
//	    qsList.add(new BasicNameValuePair("oauth_token", accessToken));
//	    String queryString = URLEncodedUtils.format(qsList, HTTP.UTF_8);
//	    HttpGet userInfoRequest = new HttpGet(userIdEndpoint + "?" + queryString);
//	    HttpResponse userInfoResponse = client.execute(userInfoRequest);
//	    Map<String, Object> userInfo = (Map<String, Object>)
//	        JSON.parse(EntityUtils.toString(userInfoResponse.getEntity()));
//	    System.out.println("User info response");
//	    for (Map.Entry<String, Object> entry : userInfo.entrySet()) 
//	    {
//	        System.out.println(String.format("  %s = %s", entry.getKey(), entry.getValue()));
//	    }
//	    System.out.println("");
//
//	    // Use the user info in interesting ways.
//	    System.out.println("Username is " + userInfo.get("username"));
//	    System.out.println("User's email is " + userInfo.get("email"));
//	    Map<String, String> urls = (Map<String, String>)userInfo.get("urls");
//	    System.out.println("REST API url is " + urls.get("rest").replace("{version}", "26.0"));
//	}
	
}
