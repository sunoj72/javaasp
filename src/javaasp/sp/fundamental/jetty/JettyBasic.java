package javaasp.sp.fundamental.jetty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// gson Samples: https://hianna.tistory.com/629#gson13
public class JettyBasic {
	public static void main(String[] args) {
		
		System.out.println();
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

	    System.out.println("name=" + employee.Name);
	    System.out.println("department=" + employee.Department);
	    System.out.println("number=" + employee.EmployeeNumber);
	}
	
	public static void object2Json() {

//		Gson gson = new Gson();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	    Employee employee = new Employee("park", "techdepart",9, null);
	    employee.Id = "emp01";
	    
	    String obejctJson = gson.toJson(employee);
	    System.out.println(obejctJson);
	}
	
	public static void readFromFile() {
		Proxy proxy = null;
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader("./files/READFILE.JSON"));
			Gson gson = new Gson();
			proxy = gson.fromJson(reader, Proxy.class);
			System.out.println(proxy);
			
			if (reader != null) {
				reader.close();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File could not found.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile() {
		Proxy proxy = null;
		
		try(BufferedReader reader = new BufferedReader(new FileReader("./files/READFILE.JSON"));
			PrintWriter printWriter = new PrintWriter(new File("./files/WRITEFILE.JSON"))) {
			
			Gson gson = new Gson();
			proxy = gson.fromJson(reader, Proxy.class);
			printWriter.print(gson.toJson(proxy));
			
		} catch (FileNotFoundException e) {
			System.out.println("File could not found.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Employee {
	String Id;

	@Expose
	@SerializedName(value = "name")
	String Name;

	@Expose
	@SerializedName(value = "department")
	String Department;

	@Expose
	@SerializedName(value = "employeeNumber")
	int EmployeeNumber;
	
	@Expose
	@SerializedName(value = "family")
	Employee[] Family;
	
	public Employee() {}
	
	public Employee(String name, String department, int employeeNumber, Employee[] family) {
		super();
		this.Name = name;
		this.Department = department;
		this.EmployeeNumber = employeeNumber;
		this.Family = family;
	}
	
	@Override
	public String toString() {
		return "Employee [name=" + Name + ", department=" + Department + ", employeeNumber=" + EmployeeNumber
				+ ", family=" + Arrays.toString(Family) + "]";
	}
}

class RoutingTable {
	@SerializedName(value = "pathPrefix")
	String PathPrefix;
	
	@SerializedName(value = "url")
	String Url;
	
	@Override
	public String toString() {
		return "{pathPrefix:" + PathPrefix + ", url:" + Url + "}";
	}
}

class Proxy {
	int port = -1;
	List<RoutingTable> routes = new ArrayList<>();
	
	
	@Override
	public String toString() {
		if (routes != null) {
			return "Proxy{port=" + port + ", routes=" + Arrays.toString(routes.toArray()) + "}";
		} else {
			return "Proxy{port=" + port + ", routes=[]}";
		}
	}
	
}


