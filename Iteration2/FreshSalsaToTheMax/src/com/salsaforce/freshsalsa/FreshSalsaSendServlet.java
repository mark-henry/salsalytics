package com.salsaforce.freshsalsa;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;

@SuppressWarnings("serial")
public class FreshSalsaSendServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page sends the data in the datastore to salesforce.");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		
		try {
			Query q = new Query("Event");
			List<Entity> results = 
				datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
			for (Entity curr : results)
			{
				Map<String,Object> props = curr.getProperties();
				for (String key : props.keySet())
				{
					resp.getWriter().println(key + " " + props.get(key));
				}
			}
			txn.commit();
		}
		finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
		
		String title = "";
		Entity event = new Entity("Event");
		
		
		for (Object key : req.getParameterMap().keySet()) {
			if (key instanceof String) {
				String sKey = (String) key;
				resp.getWriter().println(sKey + " " + req.getParameter(sKey));
				if (sKey.equals("SalsalyticsEventTitle"))
				{
					title = req.getParameter(sKey);
				}
				else 
				{
					event.setProperty(sKey, req.getParameter(sKey));
				}
			}
		}
		if (!title.trim().equals(""))
		{
			event.setProperty("SalsalyticsEventTitle", title);
			datastore.put(event);
		}
		
	}
	
}
