package javaasp.sp.fundamental.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListSorting {

	public static void main(String[] args) {
		
		Comparator<String> byWeight = (o1, o2) -> o1.compareTo(o2);
		
		List<String> list = Arrays.asList("a","c","b", "bb");
		Predicate<String> p = s -> list.add(s);
		Consumer<String> c = s -> list.add(s);
		
		System.out.println(list);
		// 오름차순(기본) 정렬
//		list.sort((s1,s2) -> s1.compareTo(s2));
		list.sort(Comparator.comparing(String::toString));
		System.out.println(list);
		// 내림차순(역순) 정렬
		list.sort(Comparator.comparing(String::toString).reversed());
		System.out.println(list);
		// 2개 기준 내림차순(역순) 정렬
		list.sort(Comparator.comparing(String::toString).reversed().thenComparing(String::intern));
		System.out.println(list);
		
		// 리스트를 맵 형태 데이터 변환(예. 문자열 길이 기준 맵 데이터생성)
		Map<Integer, List<String>> mapByLength = 
				list.stream().collect(Collectors.groupingBy(String::length));
		System.out.println(mapByLength);
		
		List<Employee> employees = 
				Arrays.asList(
						new Employee("1", "parkty", "M", 29, "sunim", false),
						new Employee("2", "kimgil", "F", 40, "chackim", true),
						new Employee("3", "leeschul", "F", 56, "sawon", false),
						new Employee("4", "parkty", "M", 33, "zchackim", true)
						);
		
		// 오브젝트 리스트를 2개(나이,이름) 기준 정렬
		List<Employee> sortedEmpls = employees.stream()
					.sorted(
							Comparator.comparing(Employee::getAge).thenComparing(Employee::getName)
					).toList();
		System.out.println(sortedEmpls);
		
		// 오브젝트 리스트를 2개(나이,이름) 기준 정렬 후에 성별로 구분
		Map<String, List<Employee>> emplBySex = employees.stream()
							.sorted(
									Comparator.comparing(Employee::getAge).thenComparing(Employee::getName)
							)
		.collect(Collectors.groupingBy(Employee::getSex));
		System.out.println(emplBySex);
		
		emplBySex = employees.stream()
				.sorted(
						Comparator.comparing(Employee::getAge).thenComparing(Employee::getName).reversed()
				)
		.collect(Collectors.groupingBy(Employee::getSex));
		System.out.println(emplBySex);
	}

}

class Employee
{
	private String id;
	private String name;
	private String sex;
	private int age;
	private String jikchack;
	private boolean isMarried;
	
	public Employee(String id, String name, String sex, int age, String jikchack, boolean isMarried) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.jikchack = jikchack;
		this.isMarried = isMarried;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getJikchack() {
		return jikchack;
	}
	public void setJikchack(String jikchack) {
		this.jikchack = jikchack;
	}
	public boolean isMarried() {
		return isMarried;
	}
	public void setMarried(boolean isMarried) {
		this.isMarried = isMarried;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", sex=" + sex + ", age=" + age + ", jikchack=" + jikchack
				+ ", isMarried=" + isMarried + "]";
	}
}
