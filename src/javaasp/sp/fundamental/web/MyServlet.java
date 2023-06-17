package javaasp.sp.fundamental.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class MyServlet extends HttpServlet {
//	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//		res.setStatus(200);
//		res.getWriter().write("{ \"key\":\"value\" }");
//	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(200);
		resp.getWriter().write("{ \"key\":\"Handling GET Action\" }");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String parameter1 = req.getParameter("p1");
		
		resp.setStatus(200);
		if(parameter1 == null) { 
			resp.getWriter().write("{ \"key\":\"Handling POST Action\" }");
		} else {
			resp.getWriter().write("{ \"key\":\"Handling POST Action : " + parameter1 + "\"\" }");
		}
		
	}
	
	
	
	
}
