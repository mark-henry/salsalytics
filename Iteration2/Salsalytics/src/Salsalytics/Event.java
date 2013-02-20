package Salsalytics;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

class Event {
	
	private static String url = "http://freshsalsaforce.appspot.com/freshsalsatothemax";
	private String charset = "UTF-8";
	private String query = "?SalsalyticsEventTitle=";
	//private Map<String, ArrayList<Pair>> kvPairs = new HashMap<String, ArrayList<Pair>>();
	/*
	 * Sends data to the google app engine site 
	 */
	int get() throws MalformedURLException, IOException {
		
		HttpURLConnection conn = (HttpURLConnection) new URL(url + query).openConnection();
		conn.setRequestProperty("Accept-Charset", charset);
		@SuppressWarnings("unused")
		InputStream response = new BufferedInputStream(
					conn.getInputStream());
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
	
	/*void addData(String title, String attributes) {
		title = encode(title);
		attributes = encode(attributes);
			
		attributes = attributes.replaceAll("%2C", "&");
		attributes = attributes.replaceAll("%3D", "=");

		query += title + "&" + attributes;
		
	}*/
	
	void addData(String title, Map<String,String> attributes) {
			query += title + "&" + buildQueryString(attributes);
		
	}
	
	/*void addData(String title, String key, String value) {
		
		if(kvPairs.containsKey(title)) {
			kvPairs.get(title).add(new Pair(key, value));
		}
		else {
			ArrayList<Pair> initPair = new ArrayList<Pair>();
			initPair.add(new Pair(key, value));
			kvPairs.put(title, initPair);
		}
		
	}*/
	
	 /**
     * Builds the querystring from a map of key value pairs.
     * @param urlParams Map of key value pairs to be put into they querystring.
     * @return The querystring with url encoded parameters.
     */
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
	
   /* private ArrayList<String> buildQueryString(Map<String, ArrayList<Pair>> urlParams) {
        StringBuilder query = new StringBuilder();
        //boolean first = true;
        ArrayList<String> queryList = new ArrayList<String>();
        
        for (Entry<String, ArrayList<Pair>> urlParam : urlParams.entrySet()) {
        	
        		query.append(urlParam.getKey());
        		for(int i = 0; i < urlParam.getValue().size(); i++) {
        			query.append("&");
        			query.append(urlParam.getValue().get(i).getKey() +
        					"=" + urlParam.getValue().get(i).getValue());
 	
        		}
        		
        		queryList.add(query.toString());
        		query.setLength(0); //Resets the StringBuilder
        		
                if (first) {
                        first = false;
                } else {
                        query.append("&");
                }
                query.append(encode(urlParam.getKey()) + "="
                                + encode(urlParam.getValue()));
        }
        return queryList;
    }*/
    
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
            		Log.d("Unsupported Encoding Operation", exc.getMessage());
                    return "";
            }
    }
    
    
}