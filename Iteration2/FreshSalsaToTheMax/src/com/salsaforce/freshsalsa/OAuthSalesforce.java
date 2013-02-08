package com.salsaforce.freshsalsa;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class OAuthSalesforce {
	private String token = "";
	private String instanceUrl = "";

	private String buildQueryString(Map<String, String> urlParams) {
		StringBuilder query = new StringBuilder();
		boolean first = true;

		for (Entry<String, String> urlParam : urlParams.entrySet()) {
			if (first) {
				first = false;
			} else {
				query.append("&");
			}
			query.append(encode(urlParam.getKey()) + "="
					+ encode(urlParam.getValue()));
		}
		return query.toString();
	}

	/**
	 * Turns an InputStream into a String.
	 * 
	 * @param is
	 *            The Inputstream.
	 * @return The String equivalent of the InputStream or an empty String if it
	 *         is null.
	 */
	private String inputStreamToString(InputStream is) {
		String str = "";

		if (is != null) {
			Scanner scan = new Scanner(is).useDelimiter("\\A");
			if (scan.hasNext()) {
				str = scan.next();
			}
			scan.close();
		}
		return str;
	}

	public String doGet(String requestUrl, String query)
			throws MalformedURLException, Exception {
		URL url = null;
		HttpURLConnection connection = null;
		InputStream response = null;

		if (query == null || query == "") {
			url = new URL(requestUrl);
		} else {
			url = new URL(requestUrl + "?" + query);
		}
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		try {
			response = connection.getInputStream();
			return inputStreamToString(response);
		} catch (Exception exc) {
			String message = connection.getResponseCode() + " "
					+ inputStreamToString(connection.getErrorStream());
			throw new Exception(message, exc);
		}
	}

	public String doPost(String requestUrl, Map<String, String> headers,
			String query) throws MalformedURLException, IOException, Exception {
		URL url = new URL(requestUrl);
		System.out.println(requestUrl);
		if (query == null) {
			query = "";
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("charset", "UTF-8");
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				System.out.println(encode(header.getKey()) + " "
						+ header.getValue());
				connection.setRequestProperty(encode(header.getKey()),
						header.getValue());
			}
		}
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(query);
		wr.close();
		System.out.println(connection.getResponseCode());
		try {
			InputStream response = connection.getInputStream();
			return inputStreamToString(response);
		} catch (Exception exc) {
			String message = connection.getResponseCode() + " "
					+ inputStreamToString(connection.getErrorStream());
			throw new Exception(message, exc);
		}
	}

	public String encode(String urlParam) {
		String charset = "UTF-8";
		try {
			return URLEncoder.encode(urlParam, charset);
		} catch (UnsupportedEncodingException exc) {
			return "";
		}
	}

	private String getValueFromJson(String json, String key) {
		int start = json.indexOf(key) + key.length() + 3;
		int end = json.indexOf("\"", start);
		if (start < end && start < json.length() && end <= json.length()) {
			return json.substring(start, end);
		} else {
			return "";
		}
	}

	public String login(String client_id, String client_secret,
			String username, String password) {
		String requestUrl = "https://login.salesforce.com/services/oauth2/token";
		Map<String, String> query = new HashMap<String, String>();
		String responseBody = "";

		query.put("grant_type", "password");
		query.put("client_id", client_id);
		query.put("client_secret", client_secret);
		query.put("username", username);
		query.put("password", password);

		try {
			responseBody = doPost(requestUrl, null, buildQueryString(query));
			token = getValueFromJson(responseBody, "access_token");
			instanceUrl = getValueFromJson(responseBody, "instance_url");
			return responseBody;
		} catch (MalformedURLException e) {
			System.err.println("Bad Url");
			return "";
		} catch (IOException e) {
			System.err.println("Connection Error");
			return "";
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
			return "";
		}
	}

	public String createObject(String restApiUrl, String params,
			String contentType) {
		if (token == null || instanceUrl == null || token.compareTo("") == 0
				|| instanceUrl.compareTo("") == 0) {
			return "";
		}
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token);
		headers.put("Content-Type", contentType);
		try {
			return doPost(instanceUrl + restApiUrl, headers, params);
		} catch (Exception exc) {
			return exc.getMessage();
		}
	}
}