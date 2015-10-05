package com.tadpole.api;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestApiTest {
	private CloseableHttpClient httpClient;

	@Before
	public void setUp() {
		httpClient = HttpClients.createDefault();
	}

	@After
	public void shutDown() throws IOException {
		httpClient.close();
	}

	@Test
	public void normalRequest() throws ClientProtocolException, IOException {
		String uri = "http://54.153.73.132:8080/tadpoleapi/rest/base/billy/test?value=1";
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("TDB_ACCESS_KEY", "6792671e-9b4e-45c2-92f9-199339c143c7");
		request.addHeader("TDB_SECRET_KEY", "d468d4e6-daec-4144-b2a0-4add38b72d4a");

		HttpResponse httpResponse = httpClient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(200, statusCode);

		BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

		// convert json's string to json's object
		String line;
		StringBuilder output = new StringBuilder();
		while ((line = br.readLine()) != null) {
			output.append(line);
		}
		
		JSONArray array = (JSONArray) JSONValue.parse(output.toString());
		JSONObject obj = (JSONObject) ((JSONArray)array.get(0)).get(0);
		assertEquals("1", obj.get("albumid"));
		assertEquals("1", obj.get("artistid"));
		assertEquals("For Those About To Rock We Salute You", obj.get("title"));
	}

	@Test
	public void invalidKey() throws ClientProtocolException, IOException {
		String uri = "http://54.153.73.132:8080/tadpoleapi/rest/base/billy/test?value=1";
		HttpUriRequest request = new HttpGet(uri);

		HttpResponse httpResponse = httpClient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(401, statusCode);

		request.addHeader("TDB_ACCESS_KEY", "xxxxxxxx");
		request.addHeader("TDB_SECRET_KEY", "xxxxxxxx");

		httpResponse = httpClient.execute(request);

		statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(401, statusCode);
	}

	@Test
	public void invalidUrl() throws ClientProtocolException, IOException {
		String uri = "http://54.153.73.132:8080/tadpoleapi/rest/base/xxxxx";
		HttpUriRequest request = new HttpGet(uri);
		request.addHeader("TDB_ACCESS_KEY", "6792671e-9b4e-45c2-92f9-199339c143c7");
		request.addHeader("TDB_SECRET_KEY", "d468d4e6-daec-4144-b2a0-4add38b72d4a");

		HttpResponse httpResponse = httpClient.execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(404, statusCode);
	}

}
