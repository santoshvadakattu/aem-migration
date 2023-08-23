package com.example.handlingformsubmission;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class HttpUrlConnectionWithCredentials {
	public static void main(String[] args) {
		System.out.println("inside hte http url connection with credentials class");
	}

	public String hitURL(String reqPath) {

		try {
			// Specify the URL to which you want to send the GET request
			String urlString = "http://localhost:4502";
			if (reqPath == null)
				reqPath = "/content/service-oklahoma/us/en/test-migration/aem-content-for-migration.infinity.json";
			else
				reqPath = reqPath + ".infinity.json";
			urlString = urlString + reqPath;

			URL url = new URL(urlString);

			// Open a connection to the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set the request method to GET
			connection.setRequestMethod("GET");

			// Add Basic Authentication header
			String username = "admin";
			String password = "admin";
			String credentials = username + ":" + password;
			String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
			connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

			// Get the response code
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			// Read the response content
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder responseContent = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				responseContent.append(line);
			}
			reader.close();

			// Print the response content
			System.out.println("Response Content:");
			System.out.println(responseContent.toString());

			// Disconnect the connection
			connection.disconnect();

			return responseContent.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
