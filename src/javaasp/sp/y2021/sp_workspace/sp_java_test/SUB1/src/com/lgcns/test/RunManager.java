package com.lgcns.test;

import java.util.ArrayList;
import java.util.Scanner;

public class RunManager {

	public static ArrayList<String> queue = new ArrayList<>();
	
	public static void main(String[] args) {
		
		try (Scanner fs = new Scanner(System.in)) {
			
			while(fs.hasNextLine()) {
				String userinputLine = fs.nextLine();
				
				String[] splitLine = userinputLine.split(" ");
				if("SEND".equals(splitLine[0])) {
					queue.add(splitLine[1]);
				} else if("RECEIVE".equals(splitLine[0])) {
					if(queue.size()>0) {
						System.out.println(queue.get(0));
						queue.remove(0);
					}
				}
			}
		}

	}

}
