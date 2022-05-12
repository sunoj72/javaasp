package javaasp.sp.y2021.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import javaasp.sp.y2021.jettyservlet.CalculatorServlet;
import javaasp.sp.y2021.jettyservlet.HelloWorldServlet;

public class quiz3 {

//	public static HashMap<String,  ArrayList<String>> queues = new HashMap();
//	public static HashMap<String,  Integer> queueSize = new HashMap();
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		ServerConnector http = new ServerConnector(server);
		http.setHost("127.0.0.1");
		http.setPort(8080);
		server.addConnector(http);

		ServletHandler servletHandler = new ServletHandler();
		
		servletHandler.addServletWithMapping(Quiz3Servlet.class, "/CREATE/*");
		servletHandler.addServletWithMapping(Quiz3Servlet.class, "/SEND/*");
		servletHandler.addServletWithMapping(Quiz3Servlet.class, "/RECEIVE/*");
		servletHandler.addServletWithMapping(Quiz3Servlet.class, "/ACK/*");
		servletHandler.addServletWithMapping(Quiz3Servlet.class, "/FAIL/*");
		
		server.setHandler(servletHandler);

		server.start();
		server.join();

	}

}
