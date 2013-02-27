package com.salsaforce.freshsalsa;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class ReceiveEvents implements Getable {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("This page prints out the key value pairs on the querystring line by line.");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
