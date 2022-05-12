package javaasp.sp.y2021.quiz5;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class quiz5 {
	
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		ServerConnector http = new ServerConnector(server);
		http.setHost("127.0.0.1");
		http.setPort(8080);
		server.addConnector(http);

		ServletHandler servletHandler = new ServletHandler();
		
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/CREATE/*");
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/SEND/*");
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/RECEIVE/*");
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/ACK/*");
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/FAIL/*");
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/DLQ/*");
		servletHandler.addServletWithMapping(Quiz5Servlet.class, "/SHUTDOWN");
		
		server.setHandler(servletHandler);

		server.start();
		server.join();

	}

}
