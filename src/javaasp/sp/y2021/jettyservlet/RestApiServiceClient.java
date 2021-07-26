package javaasp.sp.y2021.jettyservlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpMethod;


public class RestApiServiceClient {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		
		// 1. GET,POST 호출 방식
		ContentResponse contentRes = httpClient.newRequest("http://127.0.0.1:8080/rest/user/70782")
				.method(HttpMethod.POST)
				.header("x-api-key", "apikey1234567890")
				.send();
		System.out.println(contentRes.getContentAsString());
		
		httpClient.stop();
	}
}
