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

	Event testEvent, testEvent2, eventWithAppName;
	
	String expectedURL = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	String expectedURL2 = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	String emptyExpectedQuery = "?SalsalyticsEventTitle=Testing&AppName=all";
	String bareExpectedQuery = "?SalsalyticsEventTitle=Testing&AppName=all&test1" +
			"=hello&test2=world&test3=foo&test4=%26value" +
			"&test5=%3A%2F%3F%23%26%3D";
	String appNameQuery = "?SalsalyticsEventTitle=EventWithAppName&AppName=Test App";
	String nullEventName = "?SalsalyticsEventTitle=Unnamed Event&AppName=Test App&a=test+key&oneMore=testKey-Value";

	public EventTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		testEvent = new Event(expectedURL, null, null, null);
		testEvent2 = new Event(expectedURL2, null, null, null);
	}
	
	public void testGetServer() {
		String actualURL = testEvent.getServer().toString();
		assertEquals(expectedURL, actualURL);
		
		String actualURL2 = testEvent2.getServer().toString();
		assertEquals(expectedURL2, actualURL2);
	}
	
	public void testEmpty() {
		testEvent.addData("Testing", null);
		assertEquals(emptyExpectedQuery, testEvent.getQuery());
	}
	
	public void testWithAppName() {
		testEvent = new Event(expectedURL, "Test App", null, null);
		testEvent.addData("EventWithAppName", new TreeMap<String, String>());
		assertEquals(appNameQuery, testEvent.getQuery());
	}
	
	public void testWithNullEventName() {
		testEvent = new Event(expectedURL, "Test App", null, null);
		TreeMap<String, String> testArgs = new TreeMap<String, String>();
		testArgs.put("a", "test key");
		testArgs.put("oneMore", "testKey-Value");
		
		testEvent.addData(null, testArgs);
		assertEquals(nullEventName, testEvent.getQuery());
	}
	
	public void testBareAddData() {
		Map<String, String> data = new TreeMap<String, String>();
		
		data.put("test1", "hello");
		data.put("test2", "world");
		data.put("test3", "foo");
		data.put("test4", "&value");
		data.put("test5", ":/?#&=");
		
		testEvent.addData("Testing", data);
		assertEquals(bareExpectedQuery, testEvent.getQuery());
	}
	
}
