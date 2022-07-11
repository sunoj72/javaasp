package javaasp.sp.fundamental.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class RestApiServiceServlet extends HttpServlet {
//	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//		res.setStatus(200);
//		res.getWriter().write("{ \"key\":\"value\" }");
//	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitUri = requestUri.split("/");
		
		resp.setStatus(200);
		resp.getWriter().write("{ \"key\":\"Handling GET Action\" }");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitUri = requestUri.split("/");
		
		resp.setStatus(200);
		resp.getWriter().write("{ \"key\":\"Handling POST Action\" }");
	}
	
}
