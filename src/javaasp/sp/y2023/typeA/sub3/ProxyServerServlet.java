package com.lgcns.test;

	
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;

public class ProxyServerServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2938031983474037632L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.setStatus(200);
//		resp.getWriter().write("{ \"key\":\"Handling GET Action\" }");
		
		try {
			doCommon(req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	

//		String reqBody = HttpUtil.getJsonFromReqeustBody(req);
//		System.out.println("HelloWorldServlet Request Body : " + reqBody);
		
//		resp.setStatus(200);
//		resp.getWriter().write("{ \"key\":\"Handling POST Action\" }");
		
		try {
			doCommon(req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void doCommon(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		System.out.println(req.getLocalPort() + req.getRequestURI() + req.getQueryString());
		
		String requestUrl = "";
		String requestQueryString = req.getQueryString()==null?"":"?"+req.getQueryString();
		HttpMethod httpMethod = "GET".equals(req.getMethod())?HttpMethod.GET:HttpMethod.POST;
		if(5001 == req.getLocalPort()) {
			if("/front".equals(req.getRequestURI())) {
				requestUrl = "http://127.0.0.1:8081/front" + requestQueryString;
			} else if("/auth".equals(req.getRequestURI())) {
				requestUrl = "http://127.0.0.1:5002/auth"+ requestQueryString;
			}			
		} else if(5002 == req.getLocalPort() &&  req.getRequestURI().startsWith("/auth")) {
			requestUrl = "http://127.0.0.1:8082"+ req.getRequestURI() + requestQueryString;
		}
		
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		
		// 1. GET,POST 호출 방식
		Request request = httpClient.newRequest(requestUrl)
				.method(httpMethod);
		if(httpMethod == HttpMethod.POST) {
			request = request.content(new StringContentProvider(HttpUtil.getJsonFromReqeustBody(req)));
		}
		
		ContentResponse contentRes = request.send();
		
//		ContentResponse contentRes = httpClient.newRequest(requestUrl)
//				.method(httpMethod)
//				.content(new StringContentProvider("{\"username\":\"parkty\",\"password\":\"123456\"}","utf-8"))
//				.send();
		String resString = contentRes.getContentAsString();
//		System.out.println(resString);
//		contentRes.getStatus();
//		contentRes.getcon
		
//		if(req.getRequestURI().contains("lgcns")) {
//			contentRes.getHeaders();
//		}		
//		Content-Type: application/json
		resp.setStatus(contentRes.getStatus());
//		resp.setHeader("Content-Type", "application/json");
		resp.getWriter().write(resString);

	}
	
}
