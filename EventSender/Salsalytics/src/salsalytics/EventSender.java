package salsalytics;

import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.util.Log;

/**
 * <p>
 * This is the public interface for the Salsalytics EventSender package. This
 * class is thread safe singleton that can be safely used from anywhere in the
 * users Android application via a static call. This class has only two 
 * required methods.  The first is setUrl.  This method sets the url of the 
 * Google App Engine Server in which to transfer data.  The second is sendData.
 * This menthod is used to trigger sending an event. Salsalytics uses the 
 * Application Name, if you choose to supply it, as a filter to separate the 
 * data into separate tabs in the Salsalytics Dashboard.
 * </p>
 * 
 * <p>
 * Optionally, you may specify a Constant Data map. If you choose to do so all
 * key-value pairs in the map you have submitted will be sent with the new data
 * collected on every event send. This may be useful as it allows you to submit
 * a specific project build number or code name (e.g. "Build 128" or "Alpha")
 * with every event so the event can later be easily identified or filtered by
 * it the Salsalytics Dashboard.
 * </p>
 * 
 * <p>
 * Device specific information is also collected for you by the Salsalytics
 * EventSender. Use the methods in EventSender's DeviceInfrormationColleced
 * object to set whether or not specific pieces of data about the device sent
 * along with your events. Once a specific piece of information is turned on,
 * such as Android Version number (e.g. 4.2.2), it will remain on and will
 * continue to be sent with each event until it is turned off.
 * </p>
 * 
 * <p>
 * Within Salsalytics the Application Name, Constant Data Map and your 
 * decisions about which of the build-in device information fields persists 
 * through the application losing focus or being force closed.  If the device 
 * using your application does not an Internet connection the entire event 
 * will be stored and sent later, when the device gains some sort of Internet
 * connection.  This feature is very useful for applications running on tablets
 * that may not have an always-on Internet connection.      
 * </p>
 * 
 * <p> Important Notes
 * <li>In order to send data, the URL must be set. (setURL and setAppName 
 * should both be called in your applications onCreate method).
 * <li>Event key's may not begin with a dollar sign ($) character.
 * <li>To enable percistant data in Salsalytics (which includes backing 
 * up later sending queries when no Internet is avalible)  you must call
 * EventSender's onPause method in your onPause and EventSender's onResume
 * method in your onResume method.   
 * </p>
 * 
 * <br>
 * <br>
 * <p>
 * Salsalytics uses the following permissions: 
 * <br>
 * <li>android.permission.INTERNET
 * <li>android.permission.ACCESS_NETWORK_STATE
 * </p>
 * 
 * <p> 
 * Salsalytics has many helpful info and error messages for debugging any issues 
 * you may encounter.  These log messages can be found under the tag 
 * "Salsalytics".  
 * </p>
 * 
 * @author Brandon Page, brpage@calpoly.edu
 */
public class EventSender {
	private static Event event;
	private static AsyncTaskSender ats;
	private static String urlName, appName;
	private static Map<String, String> constantMap = new TreeMap<String, String>();
	private final static String SERVER_URL_KEY = "$serverUrlKey";
	private final static String APP_NAME_KEY = "$appName";
	private final static String DEVICE_VERSION_KEY = "$Android Version Number";
	private final static String DEVICE_VER_CODE_KEY = "$Android Version Codename";
	private final static String DEVICE_MODEL_KEY = "$Model";
	private final static String DEVICE_MANUFACTURE_KEY = "$Manufacture";
	private final static String DEVICE_NAME_KEY = "$Device Name";
	private final static String DEVICE_SERVICE_KEY = "$Wireless Service Provider";
	private final static String NO_INTERNET_STORED_QUERIES = "failedQueries";
	private final static String SALSALYTICS_SHARED_PREFS = "Salsalytics SharedPreferences";
	private static String currentFailedQueries = "";
	private static Context hostContext;
	private static Scanner parser;

	/*
	 * Public access for client to decide what device info is reported
	 */
	public static DeviceInformation deviceInformationCollected = new DeviceInformation();

	private EventSender() {
		event = new Event(urlName, appName, constantMap,
				deviceInformationCollected.getDeviceInfo());
		ats = new AsyncTaskSender(event);
	}

	/**
	 * This method sets the URL of the receiving page of Server to send data
	 * too.
	 * 
	 * @param URL
	 *            a String representing the URL
	 */
	public static void setURL(String URL) {
		urlName = URL;
	}

	/**
	 * <p>
	 * This method sends and Event to the server.
	 * </p>
	 * 
	 * ***REMEMBER*** In order to send data, a valid App Engine project URL must
	 * be set.
	 * 
	 * @param hostApplicationsContext
	 *   The context of the application using Salsalytics.  Typically
	 *   getBaseContext can be used 
	 * @param title
	 *  a String representing the Title of the Event.
	 * @param attributes
	 *  a Map<String, String> containing the key-value pair attributes
	 *  associated with the Event.
	 */
	public static void sendData(Context hostApplicationsContext, String title,
			Map<String, String> attributes) {
		EventSender.hostContext = hostApplicationsContext;
		new EventSender();
		event.addData(title, attributes);
		
		/*
		 * Try-catch needed because user could pass in a bad/null context
		 */
		try {
			if (internetAvalible()) {
				ats.execute(event.getServer());
				
				 if(currentFailedQueries != null && 
				  !currentFailedQueries.equals("")) { 
					 parser = new Scanner(currentFailedQueries);
				 
					 while(parser.hasNext()) {  
						 event = new Event(urlName, parser.next()); 
						 ats = new AsyncTaskSender(event);
						 ats.execute(new URL(urlName)); 
					 }	
				 
					 currentFailedQueries = ""; 
				 } 
			} 
			else {
				Log.i("Salsalytics",
						"Unable to send Event, no internet connection.");

				currentFailedQueries += event.getQuery() + " ";
			}
		} catch (Exception e) {
			Log.e("Salsalytics", "An error occurred while using the "
			 + "context passed in to accesss the newwork state. Message: \n"
			 + e.getMessage() + "\nStack Trace: "
			 + e.getStackTrace());

			/*
			 * Attempt to send the message anyway, at worst it dosen't send.
			 */
			ats.execute(event.getServer());
		}
	}

	/**
	 * Checks for an active Internet connection. (Not if a connection is
	 * Available). This can be 2/3/4G or WiFi.
	 * 
	 * @return true if an active Internet connection exits.
	 */
	private static boolean internetAvalible() {
		ConnectivityManager connectManager = (ConnectivityManager) hostContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = null;

		if (connectManager != null)
			netInfo = connectManager.getActiveNetworkInfo();

		return netInfo != null && netInfo.isConnected();
	}

	/**
	 * <p>
	 * This method sets the App Name attribute of the even sender.
	 * </p>
	 * 
	 * @param appName
	 *            the name of the app that the even sender is logging data for.
	 *            Be warned the app name "all" is reserved; any Events logged
	 *            with that application name or with no application name will
	 *            end up in the "Miscellaneous" section of the Salsalytics
	 *            Dashboard.
	 */
	public static void setAppName(String applicationName) {
		if (applicationName != null && !applicationName.equals(""))
			appName = applicationName;
	}

	/**
	 * <p>
	 * This method adds constant attributes that will be sent with every Event.
	 * An example use cause is to add a build or version number associated with
	 * the application.
	 * </p>
	 * 
	 * @param constantData
	 *            a Map<String, String> of key value-pair attributes to be sent
	 *            with each event.
	 */
	public static void setConstantData(Map<String, String> constantData) {
		constantMap = constantData;
	}

	/**
	 * This method gives access to a Salsalytics DeviceInformation Object.
	 * 
	 * @return a DeviceInformation object.
	 */
	public static DeviceInformation changeDeviceInformationCollected() {
		return deviceInformationCollected;
	}

	/**
	 * This object represents the device specific information you want collected
	 * with each Event sent.
	 * 
	 * @author Brandon Page (brpage@calpoly.edu)
	 */
	public static class DeviceInformation {
		private boolean isAndroidVersionNumberCollected = false;
		private boolean isAndroidVersionCodenameCollected = false;
		private boolean isModelCollected = false;
		private boolean isManufactureCollected = false;
		private boolean isDeviceNameCollected = false;
		private boolean isWirelessServiceProviderCollected = false;

		private String androidVersionNumber = Build.VERSION.RELEASE;
		private String androidVersionCodename = Build.VERSION.CODENAME;
		private String model = Build.MODEL;
		private String manufacture = Build.MANUFACTURER;
		private String deviceName = Build.DEVICE;
		private String wirelessServiceProvider = Build.BRAND;

		/**
		 * @param androidVersionNumberCollected
		 *            the androidVersionNumberCollected to set
		 */
		public void setAndroidVersionNumberCollected(
				boolean androidVersionNumberCollected) {
			this.isAndroidVersionNumberCollected = androidVersionNumberCollected;
		}

		/**
		 * @param androidVersionCodenameCollected
		 *            the androidVersionCodenameCollected to set
		 */
		public void setAndroidVersionCodenameCollected(
				boolean androidVersionCodenameCollected) {
			this.isAndroidVersionCodenameCollected = androidVersionCodenameCollected;
		}

		/**
		 * @param modelCollected
		 *            the modelCollected to set
		 */
		public void setModelCollected(boolean modelCollected) {
			this.isModelCollected = modelCollected;
		}

		/**
		 * @param manufactureCollected
		 *            the manufactureCollected to set
		 */
		public void setManufactureCollected(boolean manufactureCollected) {
			this.isManufactureCollected = manufactureCollected;
		}

		/**
		 * @param deviceNameCollected
		 *            the deviceNameCollected to set
		 */
		public void setDeviceNameCollected(boolean deviceNameCollected) {
			this.isDeviceNameCollected = deviceNameCollected;
		}

		/**
		 * @param wirelessServiceProviderCollected
		 *            the wirelessServiceProviderCollected to set
		 */
		public void setWirelessServiceProviderCollected(
				boolean wirelessServiceProviderCollected) {
			this.isWirelessServiceProviderCollected = wirelessServiceProviderCollected;
		}

		/**
		 * This method is for internal and testing use only.
		 * 
		 * @return a map of the device info collected
		 */
		protected TreeMap<String, String> getDeviceInfo() {
			TreeMap<String, String> selectedInfo = new TreeMap<String, String>();

			if (isAndroidVersionNumberCollected)
				selectedInfo.put(DEVICE_VERSION_KEY, this.androidVersionNumber);
			if (isAndroidVersionCodenameCollected)
				selectedInfo.put(DEVICE_VER_CODE_KEY,
						this.androidVersionCodename);
			if (isModelCollected)
				selectedInfo.put(DEVICE_MODEL_KEY, this.model);
			if (isManufactureCollected)
				selectedInfo.put(DEVICE_MANUFACTURE_KEY, this.manufacture);
			if (isDeviceNameCollected)
				selectedInfo.put(DEVICE_NAME_KEY, this.deviceName);
			if (isWirelessServiceProviderCollected)
				selectedInfo.put(DEVICE_SERVICE_KEY,
						this.wirelessServiceProvider);

			return selectedInfo;
		}
	}

	/**
	 * This method backs up data (user choices and submissions) when the host
	 * application loses focus.
	 * 
	 * This method needs to be called in the host applications onPause method
	 * for data to persist.  This method should be called in the host 
	 * application's onPause method.  
	 * 
	 */
	public static void onPause(Context hostContex) {

		try {
			Set<String> keySet = constantMap.keySet();
			SharedPreferences salsalyticsPrefs = hostContext
			 .getSharedPreferences(SALSALYTICS_SHARED_PREFS, 0);
			SharedPreferences.Editor editor = salsalyticsPrefs.edit();

			editor.clear();
			editor.putString(SERVER_URL_KEY, urlName);
			editor.putString(APP_NAME_KEY, appName);
			editor.putString(NO_INTERNET_STORED_QUERIES, currentFailedQueries);

			editor.putBoolean(DEVICE_VERSION_KEY,
					deviceInformationCollected.isAndroidVersionNumberCollected);
			editor.putBoolean(
					DEVICE_VER_CODE_KEY,
					deviceInformationCollected.isAndroidVersionCodenameCollected);
			editor.putBoolean(DEVICE_MODEL_KEY,
					deviceInformationCollected.isModelCollected);
			editor.putBoolean(DEVICE_MANUFACTURE_KEY,
					deviceInformationCollected.isManufactureCollected);
			editor.putBoolean(DEVICE_NAME_KEY,
					deviceInformationCollected.isDeviceNameCollected);
			editor.putBoolean(
					DEVICE_SERVICE_KEY,
					deviceInformationCollected.isWirelessServiceProviderCollected);

			for (String key : keySet) {
				editor.putString(key, constantMap.get(key));
			}
			
			editor.commit();
		} catch (Exception e) {
			Log.i("Salsalytics",
					"Salsalytics backup skipped. (Could be bad context)");
		}
	}

	/**
	 * This method restores data (user choices and submissions) when the host
	 * application loses focus.  This method should be called in the host 
	 * application's onResume method.  
	 */
	@SuppressWarnings("unchecked")
	public static void onResume(Context hostContext) {
	
		try {
			new EventSender();

			SharedPreferences salsalyticsPrefs = hostContext
			 .getSharedPreferences(SALSALYTICS_SHARED_PREFS,
			 Context.MODE_PRIVATE);

			Map<String, ?> allData = salsalyticsPrefs.getAll();

			if(allData.get(SERVER_URL_KEY) != null)
				urlName = (String) allData.get(SERVER_URL_KEY);
			allData.remove(SERVER_URL_KEY);
			if(allData.get(APP_NAME_KEY) != null)
				appName = (String) allData.get(APP_NAME_KEY);
			allData.remove(APP_NAME_KEY);
			if(allData.get(NO_INTERNET_STORED_QUERIES) != null)
				currentFailedQueries = (String) allData
				 .get(NO_INTERNET_STORED_QUERIES);
			allData.remove(NO_INTERNET_STORED_QUERIES);

			deviceInformationCollected
					.setAndroidVersionNumberCollected((allData
							.get(DEVICE_VERSION_KEY) == Boolean.TRUE));
			allData.remove(DEVICE_VERSION_KEY);
			deviceInformationCollected
					.setAndroidVersionCodenameCollected((allData
							.get(DEVICE_VER_CODE_KEY) == Boolean.TRUE));
			allData.remove(DEVICE_VER_CODE_KEY);
			deviceInformationCollected.setDeviceNameCollected((allData
					.get(DEVICE_NAME_KEY) == Boolean.TRUE));
			allData.remove(DEVICE_NAME_KEY);
			deviceInformationCollected.setManufactureCollected((allData
					.get(DEVICE_MANUFACTURE_KEY) == Boolean.TRUE));
			allData.remove(DEVICE_MANUFACTURE_KEY);
			deviceInformationCollected.setModelCollected((allData
					.get(DEVICE_MODEL_KEY) == Boolean.TRUE));
			allData.remove(DEVICE_MODEL_KEY);
			deviceInformationCollected
					.setAndroidVersionCodenameCollected((allData
							.get(DEVICE_SERVICE_KEY) == Boolean.TRUE));
			allData.remove(DEVICE_SERVICE_KEY);

			constantMap.putAll((Map<String, String>) allData);

		} catch (Exception e) {
			Log.i("Salsalytics", "Skipping restore.  (Could be a bad context)");
		}
	}
}