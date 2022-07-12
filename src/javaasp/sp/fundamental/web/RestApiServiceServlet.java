package javaasp.sp.fundamental.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

public class RestApiServiceServlet extends HttpServlet {
//	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//		res.setStatus(200);
//		res.getWriter().write("{ \"key\":\"value\" }");
//	}
	
	private static Map<String, User> userList = new HashMap();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitUri = requestUri.split("/");
		
		String retUserId = splitUri[splitUri.length-1];
		
		Gson gson = new Gson();
		//전체조회
		if("users".equals(retUserId)) {
			//String retUserJson = gson.toJson(userList); //{"1234":{"id":1234,"name":"park"},"4567":{"id":4567,"name":"kim"}}
			String retUserJson = gson.toJson(userList.values());
			resp.setStatus(200);
			resp.getWriter().write(retUserJson);
		} else {
		// 아이디로 조회
			User retUser = userList.get(retUserId);
			if(retUser != null) {
				String retUserJson = gson.toJson(retUser);
	
				resp.setStatus(200);
				resp.getWriter().write(retUserJson);
			} else {
				//resp.getWriter().write("{ \"key\":\"Handling POST Action\" }");
				resp.getWriter().write("{ \"result\":\"No Result\" }");
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
//		String requestUri= req.getRequestURI().substring(1);
//		String[] splitUri = requestUri.split("/");
		
		// body의 json형태 데이터 사용자 생성
		String reqBody = HttpUtil.getJsonFromReqeustBody(req);
		System.out.println("HelloWorldServlet Request Body : " + reqBody);
		if(reqBody != null && reqBody.length()>0) {
			Gson gson = new Gson();
			User createUser = gson.fromJson(reqBody, User.class);
			userList.put(createUser.getId()+"", createUser);
		} else {
		// 파라미터 방식의 사용자 생성
			String idParam = req.getParameter("id");
			String nameParam = req.getParameter("name");
			
			User createUser = new User(Integer.parseInt(idParam), nameParam);
			userList.put(idParam, createUser);
		}

		resp.setStatus(200);
		resp.getWriter().write("{ \"result\":\"OK\" }");
	}
	
}

class User {
	
	private int id;
	private String name;
	
	public User() {}
	
	public User(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}

}
