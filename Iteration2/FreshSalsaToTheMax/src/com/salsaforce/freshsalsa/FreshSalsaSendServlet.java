package com.salsaforce.freshsalsa;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.teamlazerbeez.crm.sf.rest.*;
import com.teamlazerbeez.crm.sf.soap.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;



@SuppressWarnings("serial")
public class FreshSalsaSendServlet extends HttpServlet {
	
	private static String client_id = "3MVG9y6x0357HlecfGyTDPCokSbHzObA_utCo6adVHBrDYsdyWJrSHI2kFNggsHrQfOVV1pRDqxjuCgZvVi05";
	private static String client_secret = "4986454028622869431";
	private static String username = "e_navis@yahoo.com";
	private static String password = "salsaforceg0o0Gxekj8JWjBftIBCQpMpyIcQ";
	private OAuthSalesforce auth = new OAuthSalesforce();
	private static String loginResp = "";
	
	public FreshSalsaSendServlet() {
		loginResp = auth.login(client_id, client_secret, username, password);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page sends the data in the datastore to salesforce. 47" +
				"");
		resp.getWriter().println(loginResp);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		StringBuilder sb;
		
		try {
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
			datastore.delete(txn, keysToDelete);
			
			txn.commit();
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	public void sendEvent(String name, String attributes, HttpServletResponse resp) throws IOException {
		resp.getWriter().println(name + "\n" + attributes);
		resp.getWriter().println(auth.createObject("/services/apexrest/EventAdder/", "{\"name\":\"" + name + "\", \"attributes\":\"" + attributes + "\"}", "application/json"));
	}
}
