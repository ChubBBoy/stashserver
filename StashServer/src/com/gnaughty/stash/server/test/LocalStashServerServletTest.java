package com.gnaughty.stash.server.test;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.gnaughty.stash.server.Account;
import org.json.JSONObject;

public class LocalStashServerServletTest {

	private String invokeServlet(String servletURLString, String jsonString) throws IOException {
		URL servletURL = new URL(servletURLString);
		HttpURLConnection connection = (HttpURLConnection) servletURL.openConnection();

		connection.setDoInput(true);  
		connection.setDoOutput(true);  
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
 
		OutputStream os = connection.getOutputStream();
		os.write(jsonString.getBytes());
		os.flush();
 
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ connection.getResponseCode() + " : message : " + connection.getResponseMessage());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
	 
		StringBuffer result = new StringBuffer();
		String text = null;
		while ((text = br.readLine()) != null) {
			result.append(text);
		}
 
		connection.disconnect();
		return result.toString();
	}

	// Test the register method
	@Test
	public void testAccountServletRegister(){
		String servletURLString = "http://localhost:8888/account/register";
//		String servletURLString = "http://stashserver.appspot.com/account/register";
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.accumulate(Account.FIRST_NAME, 1);
			jsonObject.accumulate(Account.MIDDLE_NAME, 1);
			jsonObject.accumulate(Account.LAST_NAME, 1);
			jsonObject.accumulate(Account.EMAIL, "netcharlie@gmail.com");
			String result = invokeServlet(servletURLString, jsonObject.toString());
			System.out.println(result);
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// Test the retrieve method
	@Test
	public void testAccountServletRetrieve(){
		String servletURLString = "http://localhost:8888/account/retrieve";
//		String servletURLString = "http://stashserver.appspot.com/account/retrieve";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.accumulate(Account.ID, 91);
			jsonObject.accumulate(Account.AUTHENTICATION_KEY, "a38e03db6eab48d4800a82b3ea14ab10");
			String result = invokeServlet(servletURLString, jsonObject.toString());
			System.out.println(result);
		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
}
