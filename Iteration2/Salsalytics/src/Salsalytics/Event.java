package Salsalytics;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

class Event {
	
	private static String url = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	private String charset = "UTF-8";
	private String query = "?SalsalyticsEventTitle=";
	
	/*
	 * Sends data to the google app engine site 
	 */
	int get() throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url + query)
				.openConnection();
		conn.setRequestProperty("Accept-Charset", charset);

		@SuppressWarnings("unused")
		InputStream response = new BufferedInputStream(conn.getInputStream());
		int status = conn.getResponseCode();
		query = "?SalsalyticsEventTitle=";
		
		return status;
	}

	void setServer(String serverName) {
		url = serverName;
	}

	URL getServer() {
		URL realUrl;
		try {
			realUrl = new URL(url);
			return realUrl;
		} catch (MalformedURLException e) {
			Log.e("Malformed String", e.getMessage());
		}
		
		return null;
	}
	
	void addData(String title, String attributes) {
		try {
			title = java.net.URLEncoder.encode(title, this.charset);
			attributes = java.net.URLEncoder.encode(attributes, this.charset);
			
			attributes = attributes.replaceAll("%2C", "&");
			attributes = attributes.replaceAll("%3D", "=");

			query += title + "&" + attributes;
		} catch (UnsupportedEncodingException e) {
			Log.d("Unsupported Encoding Operation", e.getMessage());
		}
	}
}