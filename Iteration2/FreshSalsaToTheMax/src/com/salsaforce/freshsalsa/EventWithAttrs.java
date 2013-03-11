package com.salsaforce.freshsalsa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;

public class EventWithAttrs {
	public String title;
    public Map<String, String> attrs;
    
    public EventWithAttrs(Entity event) {
    	title = "";
    	attrs = new HashMap<String, String>();
    	
		for (Entry<String, Object> property : event.getProperties().entrySet()) {
			if (property.getKey().equals("SalsalyticsEventTitle")) {
				title = (String) property.getValue();
			}
			else {
				attrs.put(property.getKey(), (String) property.getValue());
			}
		}
    }
}
