package javaasp.sp.y2021.jettyservlet.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonBasic {

	public static void main(String[] args) {
		
		makeJson();
		
		parsingJson();
	    
		json2Object();

	}
	
	public static void makeJson() {
		Gson gson = new Gson();

	    JsonObject obj = new JsonObject();
	    obj.addProperty("name", "Dave");
	    obj.addProperty("department", "HR");
	    obj.addProperty("employeeNumber", 123);

	    String json = gson.toJson(obj);
	    
	    System.out.println(json);
	}
	
	public static void parsingJson() {
		String json = "{\"name\":\"Dave\",\"department\":\"HR\",\"employeeNumber\":123}";
		
	    JsonElement element = JsonParser.parseString(json);

	    String name = element.getAsJsonObject().get("name").getAsString();
	    String department = element.getAsJsonObject().get("department").getAsString();
	    String number =  element.getAsJsonObject().get("department").getAsString();
	    System.out.println("name=" + name);
	    System.out.println("department=" + department);
	    System.out.println("number=" + number);
	}
	
	public static void json2Object() {
		String json = "{\"name\":\"Dave\",\"department\":\"HR\",\"employeeNumber\":123}";

	    Gson gson = new Gson();

	    Employee employee = gson.fromJson(json, Employee.class);

	    System.out.println("name=" + employee.getName());
	    System.out.println("department=" + employee.getDepartment());
	    System.out.println("number=" + employee.getEmployeeNumber());
	}

}
