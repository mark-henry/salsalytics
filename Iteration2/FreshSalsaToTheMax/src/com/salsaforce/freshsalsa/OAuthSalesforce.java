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

/**
 * This class provides a way to log into Salesforce and create sObjects using the rest api.
 * @author Cory Karras
 * @version 1.0
 */
public class OAuthSalesforce {
	private String token = "";
	private String instanceUrl = "";

	/**
	 * Builds the querystring from a map of key value pairs.
	 * @param urlParams Map of key value pairs to be put into they querystring.
	 * @return The querystring with url encoded parameters.
	 */
	public String buildQueryString(Map<String, String> urlParams) {
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

	/**
	 * Sends a post to a url.
	 * @param requestUrl The url to post to.
	 * @param headers Map of headers for the request.
	 * @param body What should be written to the body of the post.
	 * @return The response from the request that was sent.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws Exception
	 */
	public String doPost(String requestUrl, Map<String, String> headers,
			String body) throws MalformedURLException, IOException, Exception {
		URL url = new URL(requestUrl);
		System.out.println(requestUrl);
		if (body == null) {
			body = "";
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
		wr.writeBytes(body);
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

	/**
	 * Wrapper function for url encoding things for the querystring.
	 * @param urlParam A parameter to encode.
	 * @return The encoded parameter or an empty string on error.
	 */
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

	/**
	 * Logs into Salesforce using the provided credentials and sets the token and instanceUrl. This must be called before creating objects.
	 * @param client_id The user's client id.
	 * @param client_secret The user's client_secret.
	 * @param username The user's username.
	 * @param password The user's password.
	 * @param securityToken Token emailed from salesforce when user changes password.
	 * @return The response from the server.
	 */
	public String login(String client_id, String client_secret,
			String username, String password, String securityToken) {
		String requestUrl = "https://login.salesforce.com/services/oauth2/token";
		Map<String, String> query = new HashMap<String, String>();
		String responseBody = "";

		query.put("grant_type", "password");
		query.put("client_id", client_id);
		query.put("client_secret", client_secret);
		query.put("username", username);
		query.put("password", password + securityToken);

		try {
			responseBody = doPost(requestUrl, null, buildQueryString(query));
			token = getValueFromJson(responseBody, "access_token");
			instanceUrl = getValueFromJson(responseBody, "instance_url");
			/* Workaround for token expiring message. (Bug: SALS-62 - 2/8/2013) */
			if (token.compareTo("") == 0) {
				query.put("password", password);
				doPost(requestUrl, null, buildQueryString(query));
				query.put("password", password + securityToken);
				responseBody = doPost(requestUrl, null, buildQueryString(query));
			}
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

	/**
	 * Creates an object in salesforce.
	 * @param restApiUrl The part of the rest api to call.
	 * @param body The representation of the object to send to salesforce.
	 * @param contentType The content type of the body.
	 * @return The results from the request.
	 */
	public String createObject(String restApiUrl, String body,
			String contentType) {
		if (token == null || instanceUrl == null || token.compareTo("") == 0
				|| instanceUrl.compareTo("") == 0) {
			return "";
		}
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token);
		headers.put("Content-Type", contentType);
		try {
			return doPost(instanceUrl + restApiUrl, headers, body);
		} catch (Exception exc) {
			return exc.getMessage();
		}
	}
}