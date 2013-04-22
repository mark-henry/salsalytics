package Salsalytics;

import java.util.Map;
import android.os.*;

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
        private static Event event;
        private static AsyncTaskSender ats;
        private static String urlName, appName;
        private static Map<String, String> constantMap;
        public static DeviceInformation DeviceInformationCollected;
        
        private EventSender() {
        	event = new Event(urlName, appName, constantMap);
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
        	private boolean androidVersionNumberCollected = true;
        	private boolean androidVersionCodenameCollected = false;
        	private boolean modelCollected = false;
        	private boolean manufactureCollected = false;
        	private boolean deviceNameCollected = false;
        	private boolean wirelessServiceProviderCollected = false;
        	
        	private String androidVersionNumber;
        	private String androidVersionCodename = Build.VERSION.CODENAME;
        	private String model = Build.MODEL;
        	private String manufacture = Build.MANUFACTURER;
        	private String deviceName = Build.DEVICE;
        	private String wirelessServiceProvider;
        	
        	
        	/**
			 * @return the androidVersionNumberCollected
			 */
			public boolean isAndroidVersionNumberCollected() {
				return androidVersionNumberCollected;
			}
			/**
			 * @param androidVersionNumberCollected the androidVersionNumberCollected to set
			 */
			public void setAndroidVersionNumberCollected(
					boolean androidVersionNumberCollected) {
				this.androidVersionNumberCollected = androidVersionNumberCollected;
			}
			/**
			 * @return the androidVersionCodenameCollected
			 */
			public boolean isAndroidVersionCodenameCollected() {
				return androidVersionCodenameCollected;
			}
			/**
			 * @param androidVersionCodenameCollected the androidVersionCodenameCollected to set
			 */
			public void setAndroidVersionCodenameCollected(
					boolean androidVersionCodenameCollected) {
				this.androidVersionCodenameCollected = androidVersionCodenameCollected;
			}
			/**
			 * @return the modelCollected
			 */
			public boolean isModelCollected() {
				return modelCollected;
			}
			/**
			 * @param modelCollected the modelCollected to set
			 */
			public void setModelCollected(boolean modelCollected) {
				this.modelCollected = modelCollected;
			}
			/**
			 * @return the manufactureCollected
			 */
			public boolean isManufactureCollected() {
				return manufactureCollected;
			}
			/**
			 * @param manufactureCollected the manufactureCollected to set
			 */
			public void setManufactureCollected(boolean manufactureCollected) {
				this.manufactureCollected = manufactureCollected;
			}
			/**
			 * @return the deviceNameCollected
			 */
			public boolean isDeviceNameCollected() {
				return deviceNameCollected;
			}
			/**
			 * @param deviceNameCollected the deviceNameCollected to set
			 */
			public void setDeviceNameCollected(boolean deviceNameCollected) {
				this.deviceNameCollected = deviceNameCollected;
			}
			/**
			 * @return the wirelessServiceProviderCollected
			 */
			public boolean isWirelessServiceProviderCollected() {
				return wirelessServiceProviderCollected;
			}
			/**
			 * @param wirelessServiceProviderCollected the wirelessServiceProviderCollected to set
			 */
			public void setWirelessServiceProviderCollected(
					boolean wirelessServiceProviderCollected) {
				this.wirelessServiceProviderCollected = wirelessServiceProviderCollected;
			}
        }
}