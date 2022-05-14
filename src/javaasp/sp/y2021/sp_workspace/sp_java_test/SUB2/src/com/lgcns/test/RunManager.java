package com.lgcns.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RunManager {

	public static HashMap<String,  ArrayList<String>> queues = new HashMap<>();
	public static HashMap<String,  Integer> queueSize = new HashMap<>();
	
	public static void main(String[] args) {
		
		try (Scanner fs = new Scanner(System.in)) {
			
			while(fs.hasNextLine()) {
				String userinputLine = fs.nextLine();
				
				String[] splitLine = userinputLine.split(" ");
				if("CREATE".equals(splitLine[0])) {
					if(queues.containsKey(splitLine[1])) {
						System.out.println("Queue Exist");
					} else {
						ArrayList<String> newQueue = new ArrayList<>();
						queues.put(splitLine[1], newQueue);
						queueSize.put(splitLine[1], Integer.parseInt(splitLine[2]));
					}
				} else if("SEND".equals(splitLine[0])) {
					if(queues.get(splitLine[1]).size() == queueSize.get(splitLine[1])) {
						System.out.println("Queue Full");
					} else {
						queues.get(splitLine[1]).add(splitLine[2]);
					}
				} else if("RECEIVE".equals(splitLine[0])) {
					if(queues.get(splitLine[1]).size()>0) {
						System.out.println(queues.get(splitLine[1]).get(0));
						queues.get(splitLine[1]).remove(0);
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

}
