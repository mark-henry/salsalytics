package com.example.testapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;

public class Event {

	private static String url = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	private String charset = "UTF-8";
	private String query = "?";

	/*
	 * Sends data to the google app engine site 
	 */
	public int get() throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url + query)
				.openConnection();
		conn.setDoOutput(true); // Makes this a POST
		conn.setRequestProperty("Accept-Charset", charset);

		@SuppressWarnings("unused")
		InputStream response = new BufferedInputStream(conn.getInputStream());
		int status = conn.getResponseCode();
		for (Entry<String, List<String>> header : conn.getHeaderFields()
				.entrySet()) {
			System.out.println(header.getKey() + "=" + header.getValue());
		}

		return status;
	}

	@SuppressWarnings("unused")
	private void setServer(String serverName) {
		url = serverName;
	}

	protected String getServer() {
		return url;
	}

	public void addData(String name, String attributes) {
		try {
			name = java.net.URLEncoder.encode(name, this.charset);
			attributes = java.net.URLEncoder.encode(attributes, this.charset);
			
			query = name + "=" + attributes + "&";
		} catch (UnsupportedEncodingException e) {
			Log.d("Unsupported Encoding Operation", e.getMessage());
		}
	}

	// do we want this?
	@Override
	public String toString() {
		return url + query;
	}
}