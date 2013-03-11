package com.salsaforce.freshsalsa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class SetConfig implements Postable {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("Config");
		List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
		List<Key> keysToDelete = new ArrayList<Key>();
		for (Entity e : results) {
			keysToDelete.add(e.getKey());
		}
		datastore.delete(keysToDelete);
		
		Entity config = new Entity("Config");
		
		config.setProperty("username", req.getParameter("username"));
		config.setProperty("password", req.getParameter("password"));
		config.setProperty("token", req.getParameter("token"));
		config.setProperty("clientID", req.getParameter("clientID"));
		config.setProperty("clientSecret", req.getParameter("clientSecret"));
		datastore.put(config);
	}
}
