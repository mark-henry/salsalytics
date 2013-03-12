package com.salsaforce.freshsalsa;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OAuthSalesforceTest {
	
	private OAuthSalesforce oAuth;

	@Before
	public void setUp() {
		oAuth = new OAuthSalesforce();
	}
	
	@After
	public void tearDown() {
		oAuth = null;
	}
	
	@Test
	public void testBuildQueryString() {
		Map<String, String> urlParams = new TreeMap<String, String>();
		assertEquals("", oAuth.buildQueryString(urlParams));
		urlParams.put("hello", "world");
		assertEquals("hello=world", oAuth.buildQueryString(urlParams));
		urlParams.put("param", "value");
		assertEquals("hello=world&param=value", oAuth.buildQueryString(urlParams));
		urlParams = new TreeMap<String, String>();
		urlParams.put("&", "?");
		assertEquals("%26=%3F", oAuth.buildQueryString(urlParams));
		urlParams.put("=", "hi");
		assertEquals("%26=%3F&%3D=hi", oAuth.buildQueryString(urlParams));
	}

	@Test
	public void testDoPost() {
		try {
			String result = oAuth.doPost("http://www.google.com", null, null);
		}
		catch (Exception exc) {
			assertEquals("405", exc.getMessage().substring(0, 3));
		}
		try {
			String result = oAuth.doPost("http://www.yahoo.com", null, null);
			assertEquals("<!DOCTYPE html>", result.substring(0, 15));
		}
		catch (Exception exc) {
			fail(exc.getMessage());
		}
	}

	@Test
	public void testEncode() {
		String result = oAuth.encode("&=hi+");
		assertEquals("%26%3Dhi%2B", result);
	}

	@Test
	public void testLogin() {
//		String result = oAuth.login(client_id, client_secret, username, password, securityToken);
		String result = oAuth.login("3MVG9y6x0357HlecfGyTDPCokSbHzObA_utCo6adVHBrDYsdyWJrSHI2kFNggsHrQfOVV1pRDqxjuCgZvVi05", "4986454028622869431", "e_navis@yahoo.com", "salsaforceg0", "o0Gxekj8JWjBftIBCQpMpyIcQ");
		assertEquals("{\"id\":\"https://login.salesforc", result.substring(0, 30));
	}

	@Test
	public void testCreateObject() {
//		oAuth.login(client_id, client_secret, username, password, securityToken);
		oAuth.login("3MVG9y6x0357HlecfGyTDPCokSbHzObA_utCo6adVHBrDYsdyWJrSHI2kFNggsHrQfOVV1pRDqxjuCgZvVi05", "4986454028622869431", "e_navis@yahoo.com", "salsaforceg0", "o0Gxekj8JWjBftIBCQpMpyIcQ");
		String response = oAuth.createObject("/services/apexrest/salsaforce/EventAdder/", "{\"name\":\"unitTest\", \"attributes\":\"test1:here\"}", "application/json");
		assertEquals("", response);
	}
}
