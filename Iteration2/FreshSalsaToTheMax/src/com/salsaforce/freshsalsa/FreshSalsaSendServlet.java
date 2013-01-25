package com.salsaforce.freshsalsa;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.teamlazerbeez.crm.sf.rest.*;
import com.teamlazerbeez.crm.sf.soap.*;

import java.net.URL;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;



@SuppressWarnings("serial")
public class FreshSalsaSendServlet extends HttpServlet {
	
	static String orgID = "00DE0000000e66B";
	static String username = "msilverio324@gmail.com";
	static String password = "salsaforceg0";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page sends the data in the datastore to salesforce. 3");
		
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
					sendEvent(title,sb.toString(), resp);
				}
			}
			txn.commit();
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	public void sendEvent(String name, String attributes, HttpServletResponse resp) {
		try {
			ConnectionPool<String> soapPool = new ConnectionPoolImpl<String>(orgID);
			RestConnectionPoolImpl<String> restPool = new RestConnectionPoolImpl<String>();
		}
		catch (Exception exc) 
		{
			try {
				resp.getWriter().print(exc.getStackTrace());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		try {
			soapPool.configureOrg(orgID, username, password, 5);
			BindingConfig bindingConfig = soapPool.getConnectionBundle(orgID).getBindingConfig();
			String host = new URL(bindingConfig.getPartnerServerUrl()).getHost();
			String token = bindingConfig.getSessionId();
			ConnectionBundle bundle = soapPool.getConnectionBundle(orgID);
			restPool.configureOrg(orgID, host, token);
			ApexConnection apexConn = bundle.getApexConnection();
			ExecuteAnonResult result = apexConn.executeAnonymous("EventAdder.addEvent('" + name + "', '" + attributes + "');");
			System.out.println(result.isCompiled());
		}
		catch (Exception exc) {
			System.err.println("Hax error: " + exc.getClass().toString() + ": "+ exc.getMessage());
		}*/
	}
}
