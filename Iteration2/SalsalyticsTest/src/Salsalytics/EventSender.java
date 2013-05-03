package Salsalytics;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.*;
import android.preference.PreferenceManager;


/**
 * <p>This is the public interface for the Salsalytics EventSender package. This
 * class is thread safe singleton that can be safely used from anywhere 
 * in the users Android application.  This class has required methods: setUrl, which
 * sets the url for the Google App Engine Server in which to transfer data too,
 * and sendData, to trigger the send.  Salsalytics uses the Application Name, if you choose to 
 * supply it, as a filter to separate the data into separate tabs in the 
 * Salsalytics Dashboard.</p>
 * 
 * <p>Optionally, you may specify a Constant Data map.
 * If you choose to do so all key-value pairs in the map you have submitted will be sent with 
 * the new data collected on every event send.  This may be useful as it allows you to submit a 
 * specific project build number or code name (e.g. "Build 128" or "Alpha") with every event so 
 * the event can later be easily identified or filtered by it the Salsalytics Dashboard.</p>
 * 
 * <p>Device specific information is also collected for you by the Salsalytics EventSender.  Use
 * the methods in EventSender's DeviceInfrormationColleced object to set whether or not 
 * specific pieces of data about the device sent along with your events.  Android Version number
 * (e.g. 4.2.2) is the only piece of information defaulted to always sent.</p>
 * 
 * <p>Within Salsalytics the Application Name, Constant Data Map and
 * your decisions about which of the build-in device information
 * fields persists through the application losing focus.</p>
 * 
 * 
 * Important Notes 
 * <li> In order to send data, the URL must be set. 
 * <li> Key's may not begin with a dollar sign ($) character. 
 * 
 * 
 * @author Brandon Page, brpage@calpoly.edu
 */
public class EventSender extends Activity {
        private static Event event;
        private static AsyncTaskSender ats;
        private static String urlName, appName;
        private static Map<String, String> constantMap = new TreeMap<String, String>();
        private final String serverKey = "$serverUrlKey";
        private final String appNameKey = "$appName";
        private final static String androidVersionKey = "$Android Version Number";
        private final static String androidVersionCodeNameKey = "$Android Version Codename";
        private final static String modelKey = "$Model";
        private final static String manufactureKey = "$Manufacture";
        private final static String deviceNameKey = "$Device Name";
        private final static String serviceProviderKey = "$Wireless Service Provider";
        
        // Public access for client to decide what device info is reported
        public static DeviceInformation deviceInformationCollected = new DeviceInformation();
        
        private EventSender() {
        	event = new Event(urlName, appName, constantMap, deviceInformationCollected.getDeviceInfo());
        	ats = new AsyncTaskSender(event);
        }

        /**
         * This method sets the URL of the receiving page of Server 
         * to send data too.
         * 
         * @param URL a String representing the URL
         */
        public static void setURL(String URL) {
        	urlName = URL;
        }

        /**
         * <p>This method sends and Event to the server.</p>
         * 
         * ***REMEMBER*** In order to send data, the URL and App name must be set.
         * 
         * @param title a String representing the Title of the Event.
         * @param attributes a Map<String, String> containing the key-value 
         * pair attributes associated with the Event.   
         */
        public static void sendData(String title, Map<String, String> attributes) {
        	if(title != null) {
        		title = "Unnamed Event";
        	}
        	
        	new EventSender();
        	event.addData(title, attributes);
        	ats.execute(event.getServer());
        }
        
        
        /**
         *  <p>This method sets the App Name attribute of the even sender.</p>
         * 
         * @param appName the name of the app that the even sender is logging data for.
         */
        public static void setAppName(String applicationName) {
        	if(applicationName != null && !applicationName.equals(""))
        		appName = applicationName;
        }
        
        /**
         *  <p>This method adds constant attributes that will be sent with every Event.
         *	   An example use cause is to add a build or version number associated with
         *     the application.    
         *  </p>
         * 
         * @param constantData a Map<String, String> of key value-pair attributes to be
         * sent with each event.
         */
        public static void setConstantData(Map<String, String> constantData) {
        	constantMap = constantData;
        }
        
        public static DeviceInformation changeDeviceInformationCollected() {
        	return deviceInformationCollected;
        }
        
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
			 * @param androidVersionNumberCollected the androidVersionNumberCollected to set
			 */
			public void setAndroidVersionNumberCollected(
					boolean androidVersionNumberCollected) {
				this.isAndroidVersionNumberCollected = androidVersionNumberCollected;
			}
			
			/**
			 * @param androidVersionCodenameCollected the androidVersionCodenameCollected to set
			 */
			public void setAndroidVersionCodenameCollected(
					boolean androidVersionCodenameCollected) {
				this.isAndroidVersionCodenameCollected = androidVersionCodenameCollected;
			}
			
			/**
			 * @param modelCollected the modelCollected to set
			 */
			public void setModelCollected(boolean modelCollected) {
				this.isModelCollected = modelCollected;
			}
			
			/**
			 * @param manufactureCollected the manufactureCollected to set
			 */
			public void setManufactureCollected(boolean manufactureCollected) {
				this.isManufactureCollected = manufactureCollected;
			}
			
			/**
			 * @param deviceNameCollected the deviceNameCollected to set
			 */
			public void setDeviceNameCollected(boolean deviceNameCollected) {
				this.isDeviceNameCollected = deviceNameCollected;
			}
	
			/**
			 * @param wirelessServiceProviderCollected the wirelessServiceProviderCollected to set
			 */
			public void setWirelessServiceProviderCollected(
					boolean wirelessServiceProviderCollected) {
				this.isWirelessServiceProviderCollected = wirelessServiceProviderCollected;
			}
		
			protected TreeMap<String, String> getDeviceInfo() {
				TreeMap<String, String>  selectedInfo = new TreeMap<String, String>();
				
				if(isAndroidVersionNumberCollected)
					selectedInfo.put(androidVersionKey, this.androidVersionNumber);
            	if(isAndroidVersionCodenameCollected)
            		selectedInfo.put(androidVersionCodeNameKey, this.androidVersionCodename);
            	if(isModelCollected) 
            		selectedInfo.put(modelKey, this.model);
            	if(isManufactureCollected)
            		selectedInfo.put(manufactureKey, this.manufacture);
            	if(isDeviceNameCollected)
            		selectedInfo.put(deviceNameKey, this.deviceName);
            	if(isWirelessServiceProviderCollected)
            		selectedInfo.put(serviceProviderKey, this.wirelessServiceProvider);
				
				return selectedInfo;
			}
        }
        
       @Override
        protected void onPause() {
        	super.onPause();
        	
        	Set<String> keySet = constantMap.keySet();
        	SharedPreferences salsalyticsPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	SharedPreferences.Editor editor = salsalyticsPrefs.edit();
        	
        	editor.clear();
        	editor.putString(serverKey, urlName);
        	editor.putString(appNameKey, appName);
        	
        	editor.putBoolean(androidVersionKey, deviceInformationCollected.isAndroidVersionNumberCollected);
        	editor.putBoolean(androidVersionCodeNameKey, deviceInformationCollected.isAndroidVersionCodenameCollected);
        	editor.putBoolean(modelKey, deviceInformationCollected.isModelCollected);
        	editor.putBoolean(manufactureKey, deviceInformationCollected.isManufactureCollected);
        	editor.putBoolean(deviceNameKey, deviceInformationCollected.isDeviceNameCollected);
        	editor.putBoolean(serviceProviderKey, deviceInformationCollected.isWirelessServiceProviderCollected);
        	
        	for(String key : keySet) {
        		editor.putString(key, constantMap.get(key));
        	}
        	
        	editor.commit();
        }
        
		@SuppressWarnings("unchecked")
		@Override
        protected void onResume() {
        	super.onResume();
        	
        	SharedPreferences salsalyticsPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());        	
			Map<String, ?> allData = salsalyticsPrefs.getAll();
        	
        	urlName = (String) allData.get(serverKey);
        	allData.remove(serverKey);
        	appName = (String) allData.get(appNameKey);
        	allData.remove(appNameKey);
        	
        	deviceInformationCollected.setAndroidVersionNumberCollected((Boolean) allData.get(androidVersionKey));
        	allData.remove(androidVersionKey);
        	deviceInformationCollected.setAndroidVersionCodenameCollected((Boolean) allData.get(androidVersionCodeNameKey));
        	allData.remove(androidVersionCodeNameKey);
        	deviceInformationCollected.setDeviceNameCollected((Boolean) allData.get(deviceNameKey));
        	allData.remove(deviceNameKey);
        	deviceInformationCollected.setManufactureCollected((Boolean) allData.get(manufactureKey));
        	allData.remove(manufactureKey);
        	deviceInformationCollected.setModelCollected((Boolean) allData.get(modelKey));
        	allData.remove(modelKey);
        	deviceInformationCollected.setAndroidVersionCodenameCollected((Boolean) allData.get(serviceProviderKey));
        	allData.remove(serviceProviderKey);
        	
        	constantMap.putAll((Map<String, String>) allData);
        }
}