package Salsalytics;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;


/**  
 * A unit test for the Event class.
 * 
 * @author Martin Silverio, msilverio324@gmail.com
 */
public class EventTest extends TestCase {

	Event event;
	
	public EventTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		event = new Event();
	}
	
	public void testSetAndGetServer() {
		String expectedURL = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
		String expectedURL2 = "http://www.giantbomb.com";
		
		event.setServer(expectedURL);
		String actualURL = event.getServer().toString();
		assertEquals(expectedURL, actualURL);
		
    	event.setServer(expectedURL2);
		String actualURL2 = event.getServer().toString();
		assertEquals(expectedURL2, actualURL2);
		
		
	}
	
	public void testAddData() {
		Map<String, String> data = new TreeMap<String, String>();
		String expectedQuery = "?SalsalyticsEventTitle=Testing&test1" +
				"=hello&test2=world&test3=foo&test4=%26value" +
				"&test5=%3A%2F%3F%23%26%3D";
		data.put("test1", "hello");
		data.put("test2", "world");
		data.put("test3", "foo");
		data.put("test4", "&value");
		data.put("test5", ":/?#&=");
		
		event.addData("Testing", data);
		
		assertEquals(expectedQuery, event.getQuery());
	}
	

}
