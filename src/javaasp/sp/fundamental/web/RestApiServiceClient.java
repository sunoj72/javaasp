package javaasp.sp.fundamental.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;


public class RestApiServiceClient {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.start();
				
		// 1. POST 호출 방식 : 생성, 파라미터 방식
		ContentResponse contentResPost = httpClient.POST("http://127.0.0.1:8080/rest/users")
				.header("x-api-key", "apikey1234567890")
				.param("id","1234")
				.param("name","park")
				.send();
		System.out.println(contentResPost.getContentAsString());
		
		// 2. GET 호출 방식 : 조회
		ContentResponse contentResMethodGet = httpClient.newRequest("http://127.0.0.1:8080/rest/users/1234")
				.method(HttpMethod.GET)
				.header("x-api-key", "apikey1234567890")
				.send();
		System.out.println(contentResMethodGet.getContentAsString());

		
		// 1. POST 호출 방식 : 생성, Body데이터 방식
		ContentResponse contentResPost2 = httpClient.POST("http://127.0.0.1:8080/rest/users")
				.content(new StringContentProvider("{\"id\":\"4567\",\"name\":\"kim\"}","utf-8"))
				.send();
		System.out.println(contentResPost2.getContentAsString());
		
		// 2. GET 호출 방식 : 조회
		ContentResponse contentResGet = httpClient.GET("http://127.0.0.1:8080/rest/users/4567");
		System.out.println(contentResGet.getContentAsString());
		
		// 2. GET 호출 방식 : 전체 조회
		ContentResponse contentResGetAll = httpClient.GET("http://127.0.0.1:8080/rest/users");
		System.out.println(contentResGetAll.getContentAsString());
		
		JsonElement element = JsonParser.parseString(contentResGetAll.getContentAsString());
		// 리스트 형태 데이터 처리 #1
		JsonArray userArray = element.getAsJsonArray();
		//userArray.forEach(u -> System.out.println(u.getAsJsonObject()));
		for(int i=0; i<userArray.size(); i++) {
			System.out.println(">> " + userArray.get(i));
		}
		// 리스트 형태 데이터 처리 #2
		Gson gson = new Gson();
		List ul = gson.fromJson(contentResGetAll.getContentAsString(), List.class);
		//User one = gson.fromJson(ul.get(0).toString(),User.class);
		List<User> ulOjb = (List<User>) ul.stream().map(oj -> gson.fromJson(oj.toString(),User.class)).collect(Collectors.toList());
		System.out.println(">>> " + ulOjb.toString());
		
		
		httpClient.stop();
	}
}
