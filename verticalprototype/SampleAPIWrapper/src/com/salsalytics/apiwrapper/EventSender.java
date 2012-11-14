package com.salsalytics.apiwrapper;

import java.net.URL;
import com.teamlazerbeez.crm.sf.rest.*;
import com.teamlazerbeez.crm.sf.soap.*;

public class EventSender {
	static String orgID = "00DE0000000e66B";
	static String username = "msilverio324@gmail.com";
	static String password = "salsaforceg0";
	
	

	
	
	
	
	public static void main(String[] args) {
		EventSender sender = new EventSender();
		sender.sendEvent("test_login", "Gender:Male");
	} 
	
	public void sendEvent(String name, String attributes) {
		ConnectionPool<String> soapPool = new ConnectionPoolImpl<String>(orgID);
		RestConnectionPoolImpl<String> restPool = new RestConnectionPoolImpl<String>();
		
		try {
			soapPool.configureOrg(orgID, username, password, 5);
			BindingConfig bindingConfig = soapPool.getConnectionBundle(orgID).getBindingConfig();
			String host = new URL(bindingConfig.getPartnerServerUrl()).getHost();
			String token = bindingConfig.getSessionId();
			ConnectionBundle bundle = soapPool.getConnectionBundle(orgID);
			restPool.configureOrg(orgID, host, token);
			ApexConnection apexConn = bundle.getApexConnection();
			ExecuteAnonResult result = apexConn.executeAnonymous("EventAdder.addEvent('" + name + "', '" + attributes + "');");
			System.out.println(result.isCompiled());
		}
		catch (Exception exc) {
			System.err.println("Hax error: " + exc.getClass().toString() + ": "+ exc.getMessage());
		}
	}
}
