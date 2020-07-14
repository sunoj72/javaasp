package javaasp.sp.utilNpractice.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MapSorting {
	
	public static void main(String[] args) {
		
		// 참조 사이트(Map을 정렬(sorting)하는 다양한 방법) : https://codechacha.com/ko/java-sort-map/	
		
		Map<String, String> map = new LinkedHashMap<>();
		map.put("Nepal", "Kathmandu");
		map.put("United States", "Washington");
		map.put("India", "New Delhi");
		map.put("England", "London");
		map.put("Australia", "Canberra");
		
		//1.1 LinkedHashMap 이용하여 정렬 - Sort by key
		System.out.println("1.1 LinkedHashMap 이용하여 정렬 - Sort by key");
		Map<String, String> result = sortMapByKeyUsingLinkedHashMap(map);
		for (Map.Entry<String, String> entry : result.entrySet()) {
		    System.out.println("  Key: " + entry.getKey() + ", " + "Value: " + entry.getValue());
		}
		//1.2 LinkedHashMap 이용하여 정렬 - Sort by value
		System.out.println("1.2 LinkedHashMap 이용하여 정렬 - Sort by value");
		Map<String, String> resultByValue = sortMapByValueUsingLinkedHashMap(map);
		for (Map.Entry<String, String> entry : resultByValue.entrySet()) {
		    System.out.println("  Key: " + entry.getKey() + ", " + "Value: " + entry.getValue());
		}
		
		//2. TreeMap을 이용하여 정렬
		System.out.println("2. TreeMap을 이용하여 정렬");
		Comparator<String> comparatorReverse = (s1, s2)->s2.compareTo(s1);
		Map<String, String> treeMap = new TreeMap<>(comparatorReverse);
		treeMap.put("Nepal", "Kathmandu");
		treeMap.put("United States", "Washington");
		treeMap.put("India", "New Delhi");
		treeMap.put("England", "London");
		treeMap.put("Australia", "Canberra");
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
		    System.out.println("  Key: " + entry.getKey() + ", " + "Value: " + entry.getValue());
		}
		
		//3.1 List를 이용하여 정렬  - Sort by key
		System.out.println("3.1 List를 이용하여 정렬  - Sort by key");
		List<String> keyList = new ArrayList<>(map.keySet());
		keyList.sort((s1, s2)->s1.compareTo(s2));
		for (String key : keyList) {
		    System.out.println("  Key: " + key);
		}
		//3.2 List를 이용하여 정렬  - Sort by value
		System.out.println("3.2 List를 이용하여 정렬  - Sort by value");
		List<String> valueList = new ArrayList<>(map.values());
		valueList.sort(String::compareTo);
		for (String value : valueList) {
		    System.out.println("  Value: " + value);
		}
		
		//4.1 Stream을 이용하여 정렬 - Sort by key
		System.out.println("4.1 Stream을 이용하여 정렬 - Sort by key");
		List<Map.Entry<String, String>> entries =
		        map.entrySet().stream()
		                    .sorted(Map.Entry.comparingByKey())
		                    .collect(Collectors.toList());
		for (Map.Entry<String, String> entry : entries) {
		    System.out.println("  Key: " + entry.getKey() + ", " + "Value: " + entry.getValue());
		}

		//4.2 Stream을 이용하여 정렬 - Sort by value
		System.out.println("4.2 Stream을 이용하여 정렬 - Sort by value");
		entries = map.entrySet().stream()
		        .sorted(Map.Entry.comparingByValue())
		        .collect(Collectors.toList());
		for (Map.Entry<String, String> entry : entries) {
		    System.out.println("  Key: " + entry.getKey() + ", " + "Value: " + entry.getValue());
		}
		
		
		// 스트림을 이용한 Map 정렬
		//testMapSortingByStream();

		
	}
	
	/**
	 * Map.Entry를 리스트로 가져와 key 값으로 정렬하고, 정렬된 순서대로 LinkedHashMap에 추가
	 * @param map
	 * @return
	 */
	public static LinkedHashMap<String, String> sortMapByKeyUsingLinkedHashMap(Map<String, String> map) {
		LinkedList<Map.Entry<String, String>> entries = new LinkedList<>(map.entrySet());
	    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    for (Map.Entry<String, String> entry : entries) {
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
	
	/**
	 * Map.Entry를 리스트로 가져와 value 값으로 정렬하고, 정렬된 순서대로 LinkedHashMap에 추가
	 * @param map
	 * @return
	 */
	public static LinkedHashMap<String, String> sortMapByValueUsingLinkedHashMap(Map<String, String> map) {
	    List<Map.Entry<String, String>> entries = new LinkedList<>(map.entrySet());
	    Collections.sort(entries, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

	    LinkedHashMap<String, String> result = new LinkedHashMap<>();
	    for (Map.Entry<String, String> entry : entries) {
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
	
	
	private static void testMapSortingByStream() {
		Map<String, Integer> unsortMap = new HashMap<>();
        unsortMap.put("z", 10);
        unsortMap.put("b", 5);
        unsortMap.put("a", 6);
        unsortMap.put("c", 20);
        unsortMap.put("d", 1);
        unsortMap.put("e", 7);
        unsortMap.put("y", 8);
        unsortMap.put("n", 99);
        unsortMap.put("g", 50);
        unsortMap.put("m", 2);
        unsortMap.put("f", 9);

        System.out.println("Original Bty Key...");
        System.out.println(unsortMap);

        // sort by keys, a,b,c..., and return a new LinkedHashMap
        // toMap() will returns HashMap by default, we need LinkedHashMap to keep the order.
        Map<String, Integer> resultByKey = unsortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        // Not Recommend, but it works.
        //Alternative way to sort a Map by keys, and put it into the "result" map
        Map<String, Integer> resultByKey2 = new LinkedHashMap<>();
        unsortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(x -> resultByKey2.put(x.getKey(), x.getValue()));

        System.out.println("Sorted By Key...");
        System.out.println(resultByKey);
        System.out.println(resultByKey2);
        
        
        System.out.println();
        System.out.println("Original By Value...");
        System.out.println(unsortMap);

        //sort by values, and reserve it, 10,9,8,7,6...
        Map<String, Integer> resultByValue = unsortMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        //Alternative way
        Map<String, Integer> resultByValue2 = new LinkedHashMap<>();
        unsortMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> resultByValue2.put(x.getKey(), x.getValue()));

        System.out.println("Sorted By Value...");
        System.out.println(resultByValue);
        System.out.println(resultByValue2);
	}

}

class MapValueObject implements Comparable<MapValueObject>
{
	String id;
	String name;
	int age;
	int tall;	

	@Override
	public int compareTo(MapValueObject var1) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString() {
		return "MapValueObject [id=" + id + ", name=" + name + ", age=" + age + ", tall=" + tall + "]";
	}
}
