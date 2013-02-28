package com.salsaforce.freshsalsa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class SendToSalesforce implements Getable {
	
	private OAuthSalesforce auth = new OAuthSalesforce();
	private static String loginResp = "";
	
	public SendToSalesforce() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query("Config");
		List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		if (results.size() == 0) {
			//Not configured
			return;
		}
		String client_id = (String)results.get(0).getProperty("clientID");
		String client_secret = (String)results.get(0).getProperty("clientSecret");
		String username = (String)results.get(0).getProperty("username");
		String password = (String)results.get(0).getProperty("password");
		String securityToken = (String)results.get(0).getProperty("token");
		loginResp = auth.login(client_id, client_secret, username, password, securityToken);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page sends the data in the datastore to salesforce. 51" +
				"");
		resp.getWriter().println(loginResp);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		StringBuilder sb;

		Query q = new Query("Event");
		List<Entity> results = 
			datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		for (Entity curr : results)
		{
			sb = new StringBuilder();
			String title = "";
			Map<String,Object> props = curr.getProperties();
			
			for (String key : props.keySet())
			{
				if (key.equals("SalsalyticsEventTitle")) 
				{
					title = props.get(key) + "";
				}
				else
				{
					sb.append(key + ":" + props.get(key) + ",");
				}
			}
			if (!title.equals(""))
			{
				if (sb.length() > 0 && (sb.charAt(sb.length()-1)) == ',')
				{
					sb.setLength(sb.length()-1);
				}
				sendEvent(title, sb.toString(), resp);
			}
		}
		
		List<Key> keysToDelete = new ArrayList<Key>();
		for (Entity e : results) {
			keysToDelete.add(e.getKey());
		}
		datastore.delete(keysToDelete);
	}
	
	private void sendEvent(String name, String attributes, HttpServletResponse resp) throws IOException {
		resp.getWriter().println(name + "\n" + attributes);
		resp.getWriter().println(auth.createObject("/services/apexrest/salsaforce/EventAdder/", "{\"name\":\"" + name + "\", \"attributes\":\"" + attributes + "\"}", "application/json"));
	}
}
