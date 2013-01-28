import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map.Entry;

public class OAuthSalesforce {
	private String grant_type = "password";
	private String client_id = URLEncoder.encode("3MVG9y6x0357HlecfGyTDPCokSbHzObA_utCo6adVHBrDYsdyWJrSHI2kFNggsHrQfOVV1pRDqxjuCgZvVi05");
	private String client_secret = "4986454028622869431";
	private String username = URLEncoder.encode("msilverio324@gmail.com");
	private String password = "salsaforceg0";
	
	private String requestUrl = "https://login.salesforce.com/services/oauth2/token";
	private String charset = "UTF-8";
	
	private String urlParams = "grant_type=" + grant_type + "&" +
							   "client_id=" + client_id + "&" +
							   "client_secret=" + client_secret + "&" +
							   "username=" + username + "&" +
							   "password=" + password;
	
	private void post() throws MalformedURLException, IOException {
		/*HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setDoOutput(true); //Makes this a POST
		conn.setDoInput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept-Charset", charset);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParams.getBytes().length));
		conn.setUseCaches (false);
		conn.setRequestProperty("grant_type", grant_type);
		conn.setRequestProperty("client_id", client_id);
		conn.setRequestProperty("client_secret", client_secret);
		conn.setRequestProperty("username", username);
		conn.setRequestProperty("password", password);
		OutputStream output = null;
		try {
		     output = conn.getOutputStream();
		     output.write(query.getBytes(charset));
		} finally {
		     if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
		}
		
		InputStream response = conn.getInputStream();
		int status = conn.getResponseCode();
		System.out.println(status);
		for (Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
		    System.out.println(header.getKey() + "=" + header.getValue());
		}*/
		
		
		//String urlParameters = "param1=a&param2=b&param3=c";
		//String request = "http://example.com/index.php";
		URL url = new URL(requestUrl); 
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParams.getBytes().length));
		connection.setUseCaches (false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
		wr.writeBytes(urlParams);
		wr.flush();
		wr.close();
		int status = connection.getResponseCode();
		System.out.println(urlParams);
		//System.out.println();
		System.out.println(status);
		for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
		    System.out.println(header.getKey() + "=" + header.getValue());
		}
		connection.disconnect();
	}
	public static void main(String[] args) {
		try {
			new OAuthSalesforce().post();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}