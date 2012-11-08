package com.salsalytics.apiwrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class EventSender {
	
	public static void main(String[] args) {
		EventSender sender = new EventSender();
		sender.sendEvent("", "");
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
}
