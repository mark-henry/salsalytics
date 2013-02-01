import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class OAuthSalesforce {
    private static String grant_type = "password";
    private static String client_id = "3MVG9y6x0357HlecfGyTDPCokSbHzObA_utCo6adVHBrDYsdyWJrSHI2kFNggsHrQfOVV1pRDqxjuCgZvVi05";
    private static String client_secret = "4986454028622869431";
    private static String username = "msilverio324@gmail.com";
    private static String password = "salsaforceg0";
    
    private String charset = "UTF-8";
    private String requestUrl = "https://login.salesforce.com/services/oauth2/token";
    private String urlParams = "grant_type=" + grant_type + "&" +
                               "client_id=" + client_id + "&" +
                               "client_secret=" + client_secret + "&" +
                               "username=" + username + "&" +
                               "password=" + password;
    public OAuthSalesforce() {
    	CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }
    
    private String encodeQueryMap(Map<String, String> urlParams) {
    	StringBuilder query = new StringBuilder();
    	boolean first = true;
    	
    	for (Entry<String, String> urlParam : urlParams.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                query.append("&");
            }
            query.append(encode(urlParam.getKey()) + "=" + encode(urlParam.getValue()));
        }
    	return query.toString();
    }
    
    public String doGet(String requestUrl, Map<String, String> urlParams) throws MalformedURLException, IOException {
    	String body = "";
        String query = "";
        
        query = encodeQueryMap(urlParams);
        URL url = new URL(requestUrl + "?" + query);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream response = connection.getInputStream();
        System.out.println(connection.getResponseCode());
        Scanner scan = new Scanner(response).useDelimiter("\\A");
        if (scan.hasNext()) {
        	body = scan.next();
        }
        scan.close();
        return body;
    }
    
    public String doPost(String requestUrl, Map<String, String> urlParams, Map<String, String> headers, String queryString) throws MalformedURLException, IOException {
        String body = "";
        String query = "";
        if (urlParams != null) {
        	query = encodeQueryMap(urlParams);
        }
        else {
        	query = queryString;
        }
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false); 
        connection.setRequestMethod("POST"); 
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(query.getBytes().length));
        if (headers != null) {
		    for (Entry<String, String> header : headers.entrySet()) {
		    	connection.setRequestProperty(encode(header.getKey()), header.getValue());
		    }
        }
        connection.setUseCaches (false);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
        wr.writeBytes(query);
        wr.flush();
        wr.close();
        System.out.println(connection.getResponseCode());
        InputStream is = null;
        try {
        	is = connection.getInputStream();
        	Scanner scan = new Scanner(is).useDelimiter("\\A");
	        if (scan.hasNext()) {
	        	body = scan.next();
	        }
	        scan.close();
        }
        catch (Exception exc) {
        	Scanner scan = new Scanner(connection.getErrorStream()).useDelimiter("\\A");
            if (scan.hasNext()) {
            	System.out.println(scan.next());
            }
        }
        
        return body;
    }
    public String encode(String urlParam) {
        String charset = "UTF-8";
        try {
            return URLEncoder.encode(urlParam, charset);
        }
        catch (UnsupportedEncodingException exc) {
            return "";
        }
    }
    public static void main(String[] args) {
        String requestUrl = "https://login.salesforce.com/services/oauth2/token";
        String responceBody = "";
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("grant_type", grant_type);
        urlParams.put("client_id", client_id);
        urlParams.put("client_secret", client_secret);
        urlParams.put("username", username);
        urlParams.put("password", password);
        System.out.println("Entering Post");
        try {
            responceBody = new OAuthSalesforce().doPost(requestUrl, urlParams, null, "");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (responceBody.compareTo("") != 0) {
        	System.out.println("Entering Json parser");
        	int ind = responceBody.indexOf("access_token");
        	ind += 15;
        	String token = responceBody.substring(ind);
        	token = token.substring(0, token.length() - 2);
        	System.out.println(responceBody);
        	System.out.println(token);

        	HashMap<String, String> headers = new HashMap<String, String>();
        	headers.put("Authorization", "Bearer " + token);
        	headers.put("Content-Type", "application/json");
        	String params = "{\"Name\":\"test\"}";
        	try {
				String resp = new OAuthSalesforce().doPost("https://na9.salesforce.com/services/data/v20.0/sobjects/Account/", null, headers, params);
				System.out.println(resp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    }
}