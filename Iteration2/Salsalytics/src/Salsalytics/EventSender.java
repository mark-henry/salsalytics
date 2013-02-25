package Salsalytics;

import java.util.Map;

/**
 * <p>EventSender is the public interface for the Salsalytics wrapper. This
 * class is thread safe singleton that can be safely used from anywhere 
 * in the users Android application.  This class has two methods one to 
 * set the Server in which to transfur data too and one to send data. </p>
 * 
 * ***IMPORTANT*** In order to send data, the URL must be set.
 * 
 * @author Brandon Page, brpage@calpoly.edu
 */
public class EventSender {
	private static volatile EventSender INSTANCE;
	private Event event;
	private AsyncTaskSender ats;

	private EventSender() {
		event = new Event();
		ats = new AsyncTaskSender(event);
	}

	public static EventSender getInstance() {
		if (INSTANCE == null) {
			synchronized (EventSender.class) {
				if (INSTANCE == null) {
					INSTANCE = new EventSender();
				}
			}
		}

		return INSTANCE;
	}

	/**
	 * This method sets the URL of the receiving page of Server 
	 * to send data too.
	 * 
	 * @param URL a String representing the URL
	 */
	public void setURL(String URL) {
		event.setServer(URL);
	}

	/**
	 * <p>This method sends and Event to the server.</p>
	 * 
	 * ***REMEMBER*** In order to send data, the URL must be set.
	 * 
	 * @param title a String representing the Title of the Event.
	 * @param attributes a Map<String, String> containing the key-value 
	 * pair attributes associated with the Event.   
	 */
	public void sendData(String title, Map<String, String> attributes) {
		event.addData(title, attributes);
		ats.execute(event.getServer());
	}
}
