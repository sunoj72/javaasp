package javaasp.sp.fundamental.gson;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonBasic {

	public static void main(String[] args) {
		
		makeJson();
		System.out.println();
		makeJsonWihArray();
		System.out.println();
		parsingJson();
		System.out.println();
		json2Object();
		System.out.println();
		object2Json();

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
	
	public static void makeJsonWihArray() {
		Gson gson = new Gson();

	    JsonObject obj = new JsonObject();
	    obj.addProperty("name", "Dave");
	    obj.addProperty("department", "HR");
	    obj.addProperty("employeeNumber", 123);
	    
	    JsonObject arrObj = new JsonObject();
	    arrObj.addProperty("name", "Family1");
	    arrObj.addProperty("department", "TECH");
	    arrObj.addProperty("employeeNumber", 456);
	    JsonObject arrObj2 = new JsonObject();
	    arrObj2.addProperty("name", "Family2");
	    arrObj2.addProperty("department", "TECH2");
	    arrObj2.addProperty("employeeNumber", 567);
	    
	    JsonArray ja = new JsonArray();
	    ja.add(arrObj);
	    ja.add(arrObj2);
	    
	    obj.add("family", ja);

	    System.out.println(">> Obejct to Json");
	    String json = gson.toJson(obj);
	    
	    System.out.println(json);
	    
	    System.out.println(">> Json to Object");
	    Employee employees = gson.fromJson(json, Employee.class);
	    
	    System.out.println(employees);
	}
	
	public static void parsingJson() {
		String json = "{\"name\":\"Dave\",\"department\":\"HR\",\"employeeNumber\":123}";
		
	    JsonElement element = JsonParser.parseString(json);

	    String name = element.getAsJsonObject().get("name").getAsString();
	    String department = element.getAsJsonObject().get("department").getAsString();
	    String number =  element.getAsJsonObject().get("employeeNumber").getAsString();
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
	
	public static void object2Json() {

		Gson gson = new Gson();

	    Employee employee = new Employee("park", "techdepart",9, null);
	    
	    String obejctJson = gson.toJson(employee);
	    System.out.println(obejctJson);
	}
	
	

}

class Employee {
	
	private String name;
	private String department;
	private int employeeNumber;
	
	private Employee[] family;
	
	public Employee() {}
	
	public Employee(String name, String department, int employeeNumber, Employee[] family) {
		super();
		this.name = name;
		this.department = department;
		this.employeeNumber = employeeNumber;
		this.family = family;
	}
	
	
	public String getName() {
		return name;
	}

//	public void setName(String name) {
//		this.name = name;
//	}
	public String getDepartment() {
		return department;
	}
//	public void setDepartment(String department) {
//		this.department = department;
//	}
	public int getEmployeeNumber() {
		return employeeNumber;
	}
//	public void setEmployeeNumber(int employeeNumber) {
//		this.employeeNumber = employeeNumber;
//	}
	public Employee[] getFamily() {
		return family;
	}
//	public void setFamily(Employee[] family) {
//		this.family = family;
//	}
	
	@Override
	public String toString() {
		return "Employee [name=" + name + ", department=" + department + ", employeeNumber=" + employeeNumber
				+ ", family=" + Arrays.toString(family) + "]";
	}

}
