package salsalytics;

import java.util.TreeMap;
import salsalytics.EventSender.DeviceInformation;
import android.os.Build;
import junit.framework.TestCase;

/**  
 * A unit test for the Event class.
 * 
 * @author Brandon Page brpage@calpoly.edu
 */
public class EventTest extends TestCase {

	Event testEvent, testEvent2, eventWithAppName;
	
	String expectedURL = 
	 "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	String expectedURL2 = 
	 "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	String emptyExpectedQuery = 
	 "?SalsalyticsEventTitle=Testing&AppName=all";
	String bareExpectedQuery = "?SalsalyticsEventTitle=Testing&AppName=all" +
	 "&test1=hello&test2=world&test3=foo&test4=%26value" + 
	 "&test5=%3A%2F%3F%23%26%3D";
	String appNameQuery = "?SalsalyticsEventTitle=EventWithAppName" + 
	 "&AppName=Test App";
	String nullEventNameQuery = "?SalsalyticsEventTitle=Unnamed Event" + 
	 "&AppName=Test App&a=test+key&oneMore=testKey-Value";
	String justConstDataQuery = "?SalsalyticsEventTitle=Unnamed Event" + 
	 "&AppName=Test App&one+more=for+the+road&this+key=has+a+constant+value";
	String attributesAndConstQuery = "?SalsalyticsEventTitle=Testing" + 
	 "&AppName=Test App&one+more=for+the+road&this+key=has+a+constant+value" +
	 "&test1=hello&test2=world&test3=foo&test4=%26value" + 
	 "&test5=%3A%2F%3F%23%26%3D";
	String devInfoQuery = "?SalsalyticsEventTitle=Unnamed Event" + 
	 "&AppName=Test App&%24Android+Version+Codename=REL&%24" + 
	 "Android+Version+Number=4.1.2&%24Device+Name=m7wls&%24" + 
	 "Manufacture=HTC&%24Model=HTCONE&%24Wireless+Service+Provider=htc";

	
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
		assertEquals(nullEventNameQuery, testEvent.getQuery());
	}
	
	public void testLotsOfAddData() {
		TreeMap<String, String> data = new TreeMap<String, String>();
		data.put("test1", "hello");
		data.put("test2", "world");
		data.put("test3", "foo");
		data.put("test4", "&value");
		data.put("test5", ":/?#&=");
		
		testEvent.addData("Testing", data);
		assertEquals(bareExpectedQuery, testEvent.getQuery());
	}
	
	public void testAddConstantData() {
		TreeMap<String, String> constData = new TreeMap<String, String>();
		constData.put("this key", "has a constant value");
		constData.put("one more", "for the road");
		testEvent = new Event(expectedURL, "Test App", constData, null);
		
		testEvent.addData(null, null);
		assertEquals(justConstDataQuery, testEvent.getQuery());
	}
	
	public void testAtrributesAndConstData() {
		TreeMap<String, String> data = new TreeMap<String, String>();
		data.put("test1", "hello");
		data.put("test2", "world");
		data.put("test3", "foo");
		data.put("test4", "&value");
		data.put("test5", ":/?#&=");
		
		TreeMap<String, String> constData = new TreeMap<String, String>();
		constData.put("this key", "has a constant value");
		constData.put("one more", "for the road");
		
		testEvent = new Event(expectedURL, "Test App", constData, null);
		testEvent.addData("Testing", data);
		assertEquals(attributesAndConstQuery, testEvent.getQuery());
	}
	
	/**
	 * This test will ONLY work on the Sprint HTC One (my current 
	 * favorite test device) because the query constructed is 
	 * device dependent.  When run with other devices the test will
	 * not actually assert anything. 
	 * 
	 */
	public void testDeviceInfo() {
		if(Build.DEVICE.equals("m7wls")) {
			DeviceInformation devInfo = new DeviceInformation();
			devInfo.setAndroidVersionCodenameCollected(true);
			devInfo.setAndroidVersionNumberCollected(true);
			devInfo.setDeviceNameCollected(true);
			devInfo.setManufactureCollected(true);
			devInfo.setModelCollected(true);
			devInfo.setWirelessServiceProviderCollected(true);
		
			testEvent = new Event(expectedURL, "Test App", null, 
			 devInfo.getDeviceInfo());
			testEvent.addData(null, null);
			assertEquals(devInfoQuery, testEvent.getQuery());
		}
		
		//TODO add more devices
	}
}
