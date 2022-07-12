package javaasp.sp.fundamental.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;


public class MyServletClient {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		
		// 1. GET,POST 호출 방식
		ContentResponse contentRes = httpClient.newRequest("http://127.0.0.1:8080/helloworld")
				.method(HttpMethod.GET)
				.header("x-api-key", "apikey1234567890")
				.send();
		System.out.println(contentRes.getContentAsString());
		ContentResponse contentRes2 = httpClient.GET("http://127.0.0.1:8080/helloworld");
		System.out.println(contentRes2.getContentAsString());
		//ContentResponse contentRes3 = httpClient.newRequest("http://127.0.0.1:8080/helloworld").method(HttpMethod.POST).send();
		// POST with request body
		ContentResponse contentRes3 = 
				httpClient.POST("http://127.0.0.1:8080/helloworld")
				.content(new StringContentProvider("{\"username\":\"parkty\",\"password\":\"123456\"}","utf-8"))
				.send();
		System.out.println(contentRes3.getContentAsString());
		
		// 2. Parameter 전달
		ContentResponse contentRes4 = httpClient.POST("http://127.0.0.1:8080/calculator")
				.param("action", "+")
				.param("value1", "3")
				.param("value2", "7")
				.attribute("att1", "true")
				.send();
		
		System.out.println(contentRes4.getContentAsString());
		
		//JsonElement jsonElement = JsonParser.parseString("{ \"key\":\"value\" }");
		JsonElement jsonElement = JsonParser.parseString(contentRes4.getContentAsString());
		System.out.println(jsonElement.toString());
		
		int resultValue = jsonElement.getAsJsonObject().get("result").getAsInt();
		System.out.println(resultValue);
		
		//3. Non-Blocking APIs
		httpClient.newRequest("http://127.0.0.1:8080/helloworld")
			.timeout(3, TimeUnit.SECONDS)
				.onResponseContent((response, buffer) -> {
					byte[] responsBuffer = new byte[buffer.remaining()];
					buffer.get(responsBuffer);
					String responseContent = new String(responsBuffer);
					System.out.println("ASync Result : " + responseContent);
				})
				.send(new Response.CompleteListener() {
					@Override
					public void onComplete(Result arg0) {					
						System.out.println(arg0);
					}
				});
		//4. hooks for all possible request and response events
		httpClient.newRequest("http://127.0.0.1:8080/helloworld")
			// Add request hooks
	        .onRequestQueued(request -> {System.out.println("onRequestQueued : " + request); })
	        .onRequestBegin(request -> { System.out.println("onRequestBegin : " + request); })
	        //... // More request hooks available
	        // Add response hooks
	        .onResponseBegin(response -> { System.out.println("onResponseBegin : " + response); })
	        .onResponseHeaders(response -> { System.out.println("onResponseHeaders : " + response); })
	        .onResponseContent((response, buffer) -> { System.out.println("onResponseContent : " + response); })
	        //... // More response hooks available
	        .send(result -> { System.out.println("send : " + result); });
		
//		httpClient.newRequest("http://127.0.0.1:8080/helloworld")
//			.send(result -> { /* Your logic here */ });
		
		httpClient.stop();
	}
}
