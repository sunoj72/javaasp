package javaasp.sp.y2021.jettyservlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;


public class MyServletClient {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		ContentResponse contentRes = httpClient.newRequest("http://127.0.0.1:8080/helloworld").method(HttpMethod.GET).send();
		System.out.println(contentRes.getContentAsString());
		
		//JsonElement jsonElement = JsonParser.parseString("{ \"key\":\"value\" }");
		JsonElement jsonElement = JsonParser.parseString(contentRes.getContentAsString());
		System.out.println(jsonElement.toString());
		httpClient.stop();

	}
}
