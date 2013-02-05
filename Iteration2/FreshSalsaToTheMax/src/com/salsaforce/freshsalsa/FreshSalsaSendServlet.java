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
	private static String username = "msilverio324@gmail.com";
	private static String password = "salsaforceg0";
	private OAuthSalesforce auth = new OAuthSalesforce();
	
	public FreshSalsaSendServlet() {
		auth.login(client_id, client_secret, username, password);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page sends the data in the datastore to salesforce. 37" +
				"");
		resp.getWriter().println(InetAddress.getLocalHost().getHostAddress());
		sendEvent("hi","there", resp);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		StringBuilder sb = new StringBuilder();
		
		try {
			Query q = new Query("Event");
			List<Entity> results = 
				datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			for (Entity curr : results)
			{
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
						resp.getWriter().println(key + " " + props.get(key));
					}
				}
				if (!title.equals(""))
				{
					if (sb.length() > 0 && (sb.charAt(sb.length()-1)) == ',')
					{
						sb.setLength(sb.length()-1);
					}
					//sendEvent(title, sb.toString(), resp);
				}
			}
			
			// TODO: Clear datastore of elements that were sent
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
		auth.createObject("/services/data/v20.0/sobjects/Account/", "{\"Name\":\"testSomethingElse\"}", "application/json");
	}
}
