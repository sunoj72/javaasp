package javaasp.sp.y2021.quiz3;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class quiz3 {
	
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
