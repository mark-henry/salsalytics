package salsalytics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.TreeMap;

import android.util.Log;
import junit.framework.TestCase;

/**  
 * A unit test for the Event class.
 * 
 * @author Brandon Page brpage@calpoly.edu
 */
public class EventTest extends TestCase {

	Event bareEvent, bareEvent2, eventWithAppName;
	String expectedURL = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	String expectedURL2 = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	String emptyExpectedQuery = "?SalsalyticsEventTitle=Testing&AppName=all";
	String bareExpectedQuery = "?SalsalyticsEventTitle=Testing&AppName=all&test1" +
			"=hello&test2=world&test3=foo&test4=%26value" +
			"&test5=%3A%2F%3F%23%26%3D";
	public EventTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		bareEvent = new Event(expectedURL, null, null, null);
		bareEvent2 = new Event(expectedURL2, null, null, null);
	}
	
	public void testGetServer() {
		String actualURL = bareEvent.getServer().toString();
		assertEquals(expectedURL, actualURL);
		
		String actualURL2 = bareEvent2.getServer().toString();
		assertEquals(expectedURL2, actualURL2);
	}
	
	public void testEmpty() {
		bareEvent.addData("Testing", null);
		assertEquals(emptyExpectedQuery, bareEvent.getQuery());
	}
	
	public void testWithAppName() {
		bareEvent = new Event(expectedURL, "Test App", null, null);
		bareEvent.addData("EventWithAppName", new TreeMap());
		//TODO
	}
	
	public void testWithNullEventName() {
		bareEvent = new Event(expectedURL, "Test App", null, null);
		bareEvent.addData(null, new TreeMap());
		//TODO
	}
	
	public void testBareAddData() {
		Map<String, String> data = new TreeMap<String, String>();
		
		data.put("test1", "hello");
		data.put("test2", "world");
		data.put("test3", "foo");
		data.put("test4", "&value");
		data.put("test5", ":/?#&=");
		
		bareEvent.addData("Testing", data);
		assertEquals(bareExpectedQuery, bareEvent.getQuery());
	}
	
}
