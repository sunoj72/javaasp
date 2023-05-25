package com.lgcns.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.gson.Gson;

public class ProxyServer {	
	
	private int port;
	private Route[] routes;
	
	private Server proxyServer;
	
	public ProxyServer(File proxyServerInfo) throws IOException {
		
		String proxyInfoJson = readContentOfFileUsingFilesAllBytes(proxyServerInfo.getPath());
		Gson gson = new Gson();
		ProxyInfo proxyInfo = gson.fromJson(proxyInfoJson, ProxyInfo.class);
		
		port = proxyInfo.port;
		routes = proxyInfo.routes;		
	}
	
	
	public void startServer() throws Exception {
		
		proxyServer = new Server();
		ServerConnector http = new ServerConnector(proxyServer);
		http.setHost("127.0.0.1");
		http.setPort(port);
		proxyServer.addConnector(http);

		ServletHandler servletHandler = new ServletHandler();
		// 1. get, post test
		servletHandler.addServletWithMapping(ProxyServerServlet.class, "/*");
//		//2. paramger test
//		servletHandler.addServletWithMapping(CalculatorServlet.class, "/calculator");
//		//3. paramger test
//		servletHandler.addServletWithMapping(RestApiServiceServlet.class, "/rest/*");
		
		
		proxyServer.setHandler(servletHandler);

		proxyServer.start();
		
	}
	
	public void joinServer() throws InterruptedException {
		proxyServer.join();
	}
	
	public void callService(String servicePath) {
		
	}
	
	public static String readContentOfFileUsingFilesAllBytes(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		 
        String content = null;
        try {
        	byte[] encoded = Files.readAllBytes(path);
            content = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(content);
        return content;
	}
	
}
