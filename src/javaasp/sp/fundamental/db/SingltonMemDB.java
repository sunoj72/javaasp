package javaasp.sp.fundamental.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SingltonMemDB implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, ArrayList<EmployeeTable>> db = new HashMap<String, ArrayList<EmployeeTable>>();

	private SingltonMemDB() {}

	private static class SingletonHelper {
		private static final SingltonMemDB SINGLETON = new SingltonMemDB();
	}
	
	public static SingltonMemDB getInstance(){
	    return SingletonHelper.SINGLETON;
	}
	
	public List<EmployeeTable> selectTableAll(String tableName) {
		return db.get(tableName);		
	}
	
	public List<EmployeeTable> selectTableByDepartment(String tableName, String department) {
		List<EmployeeTable> selTable = db.get(tableName).stream()
				.filter(empTle->"itpart".equals(empTle.department))
				.collect(Collectors.toList());
		
		return selTable;		
	}
}

class EmployeeTable{
	public String id;
	public String name;
	public int age;
	public String department;
}