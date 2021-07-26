package javaasp.sp.y2021.jettyservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class CalculatorServlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String parameter1 = req.getParameter("action");
		String parameter2 = req.getParameter("value1");
		String parameter3 = req.getParameter("value2");
		
		// validate parameters.. 생략~~~
//		resp.setStatus(400);
//		resp.getWriter().write("{ \"resutl\":\"Bad Request\"");
		
		int p2 = Integer.valueOf(parameter2);
		int p3 = Integer.valueOf(parameter3);
		int result = 0;
		
		resp.setStatus(200);
		if("+".equals(parameter1)) {
			result = p2 + p3;
		} else if("-".equals(parameter1)) {
			result = p2 - p3;
		} else if("*".equals(parameter1)) {
			result = p2 * p3;
		} else if("/".equals(parameter1)) {
			result = p2 / p3;
		}
		
		resp.getWriter().write(String.format("{ \"result\":%d }", result));
		
	}

}
