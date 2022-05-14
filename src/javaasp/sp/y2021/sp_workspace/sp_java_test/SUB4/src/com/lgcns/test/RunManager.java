package com.lgcns.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;


public class RunManager {

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		ServerConnector http = new ServerConnector(server);
		http.setHost("127.0.0.1");
		http.setPort(8080);
		server.addConnector(http);

		ServletHandler servletHandler = new ServletHandler();
		
		servletHandler.addServletWithMapping(MessageServlet.class, "/CREATE/*");
		servletHandler.addServletWithMapping(MessageServlet.class, "/SEND/*");
		servletHandler.addServletWithMapping(MessageServlet.class, "/RECEIVE/*");
		servletHandler.addServletWithMapping(MessageServlet.class, "/ACK/*");
		servletHandler.addServletWithMapping(MessageServlet.class, "/FAIL/*");
		servletHandler.addServletWithMapping(MessageServlet.class, "/DLQ/*");
		
		server.setHandler(servletHandler);

		server.start();
		server.join();

	}

}
