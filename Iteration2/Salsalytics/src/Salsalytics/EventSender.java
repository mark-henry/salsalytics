package Salsalytics;

import java.util.Map;

public class EventSender {
	private static volatile EventSender INSTANCE;
	private Event event;
	private AsyncTaskSender ats;
	
	private EventSender() {
		event = new Event();
		ats = new AsyncTaskSender(event);
	}
	
	public static EventSender getInstance() {
		if(INSTANCE == null) {
			synchronized(EventSender.class) {
				if(INSTANCE == null) {
					INSTANCE = new EventSender();
				}
			}
		}
		
		return INSTANCE;
	}
	
	public void setURL(String URL) {
		event.setServer(URL);
	}
	
	public void sendData(String title, Map<String, String> attributes) {
		event.addData(title, attributes);	
		ats.execute(event.getServer());
	}
}
