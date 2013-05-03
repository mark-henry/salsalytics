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
        private static String urlName, appName = "";
        private static Map<String, String> constantMap;
        private final String serverKey = "$serverUrlKey";
        private final String appNameKey = "$appName";
        private final String androidVersionKey = "$Android Version Number";
        private final String androidVersionCodeNameKey = "$Android Version Codename";
        private final String modelKey = "$Model";
        private final String manufactureKey = "$Manufacture";
        private final String deviceNameKey = "$Device Name";
        private final String serviceProviderKey = "$Wireless Service Provider";
        
        // Public access to decide what device info is recorded
        public static DeviceInformation DeviceInformationCollected;
        
        private EventSender() {
        	event = new Event(urlName, appName, constantMap, DeviceInformationCollected.getDeviceInfo());
        	ats = new AsyncTaskSender(event);
        }

        private static EventSender getInstance() {
        	return new EventSender();
        }

        /**
         * This method sets the URL of the receiving page of Server 
         * to send data too.
         * 
         * @param URL a String representing the URL
         */
        public static void setURL(String URL) {
        	getInstance();
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
        	getInstance();
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
        public static void addConstantData(Map<String, String> constantData) {
        	constantMap = constantData;
        }
        
        private class DeviceInformation {
        	boolean androidVersionNumberCollected;
        	boolean androidVersionCodenameCollected;
        	boolean modelCollected;
        	boolean manufactureCollected;
        	boolean deviceNameCollected;
        	boolean wirelessServiceProviderCollected;
        	
        	private String androidVersionNumber;
        	private String androidVersionCodename;
        	private String model;
        	private String manufacture;
        	private String deviceName;
        	private String wirelessServiceProvider;
        	
        	
        	private DeviceInformation() {
        		androidVersionNumberCollected = true;
            	androidVersionCodenameCollected = false;
            	modelCollected = false;
            	manufactureCollected = false;
            	deviceNameCollected = false;
            	wirelessServiceProviderCollected = false;
            	
        		androidVersionNumber = Build.VERSION.INCREMENTAL;
        		androidVersionCodename = Build.VERSION.CODENAME;
        		model = Build.MODEL;
        		manufacture = Build.MANUFACTURER;
        		deviceName = Build.DEVICE;
        		wirelessServiceProvider = Build.BRAND;
        	}
        	
        	
        	
			/**
			 * @param androidVersionNumberCollected the androidVersionNumberCollected to set
			 */
			public void setAndroidVersionNumberCollected(
					boolean androidVersionNumberCollected) {
				this.androidVersionNumberCollected = androidVersionNumberCollected;
			}
			
			/**
			 * @param androidVersionCodenameCollected the androidVersionCodenameCollected to set
			 */
			public void setAndroidVersionCodenameCollected(
					boolean androidVersionCodenameCollected) {
				this.androidVersionCodenameCollected = androidVersionCodenameCollected;
			}
			
			/**
			 * @param modelCollected the modelCollected to set
			 */
			public void setModelCollected(boolean modelCollected) {
				this.modelCollected = modelCollected;
			}
			
			/**
			 * @param manufactureCollected the manufactureCollected to set
			 */
			public void setManufactureCollected(boolean manufactureCollected) {
				this.manufactureCollected = manufactureCollected;
			}
			
			/**
			 * @param deviceNameCollected the deviceNameCollected to set
			 */
			public void setDeviceNameCollected(boolean deviceNameCollected) {
				this.deviceNameCollected = deviceNameCollected;
			}
	
			/**
			 * @param wirelessServiceProviderCollected the wirelessServiceProviderCollected to set
			 */
			@SuppressWarnings("unused")
			public void setWirelessServiceProviderCollected(
					boolean wirelessServiceProviderCollected) {
				this.wirelessServiceProviderCollected = wirelessServiceProviderCollected;
			}
		
			private Map<String, String> getDeviceInfo() {
				Map<String, String>  selectedInfo = new TreeMap<String, String>();
				
				if(androidVersionNumberCollected)
					selectedInfo.put(androidVersionKey, this.androidVersionNumber);
            	if(androidVersionCodenameCollected)
            		selectedInfo.put(androidVersionCodeNameKey, this.androidVersionCodename);
            	if(modelCollected) 
            		selectedInfo.put(modelKey, this.model);
            	if(manufactureCollected)
            		selectedInfo.put(manufactureKey, this.manufacture);
            	if(deviceNameCollected)
            		selectedInfo.put(deviceNameKey, this.deviceName);
            	if(wirelessServiceProviderCollected)
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
        	
        	editor.putBoolean(androidVersionKey, DeviceInformationCollected.androidVersionNumberCollected);
        	editor.putBoolean(androidVersionCodeNameKey, DeviceInformationCollected.androidVersionCodenameCollected);
        	editor.putBoolean(modelKey, DeviceInformationCollected.modelCollected);
        	editor.putBoolean(manufactureKey, DeviceInformationCollected.manufactureCollected);
        	editor.putBoolean(deviceNameKey, DeviceInformationCollected.deviceNameCollected);
        	editor.putBoolean(serviceProviderKey, DeviceInformationCollected.wirelessServiceProviderCollected);
        	
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
        	
        	DeviceInformationCollected.setAndroidVersionNumberCollected((Boolean) allData.get(androidVersionKey));
        	allData.remove(androidVersionKey);
        	DeviceInformationCollected.setAndroidVersionCodenameCollected((Boolean) allData.get(androidVersionCodeNameKey));
        	allData.remove(androidVersionCodeNameKey);
        	DeviceInformationCollected.setDeviceNameCollected((Boolean) allData.get(deviceNameKey));
        	allData.remove(deviceNameKey);
        	DeviceInformationCollected.setManufactureCollected((Boolean) allData.get(manufactureKey));
        	allData.remove(manufactureKey);
        	DeviceInformationCollected.setModelCollected((Boolean) allData.get(modelKey));
        	allData.remove(modelKey);
        	DeviceInformationCollected.setAndroidVersionCodenameCollected((Boolean) allData.get(serviceProviderKey));
        	allData.remove(serviceProviderKey);
        	
        	constantMap.putAll((Map<String, String>) allData);
        }
}