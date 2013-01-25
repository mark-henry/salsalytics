package Salsalytics;

public enum EventSender {
	INSTANCE;
	private Event event = new Event();
	private AsyncTaskSender ats = new AsyncTaskSender(event);
	
	public void setURL(String URL) {
		event.setServer(URL);
	}
	
	public void logData(String title, String attributes) {
		event.addData(title, attributes);	
		ats.execute(event.getServer());
	}
}
