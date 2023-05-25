package com.lgcns.test;

import java.util.Scanner;

public class RunManager {

	public static void main(String[] args) {
		
		int workerSize = 2;
		Worker[] workers = new Worker[workerSize];
		for(int i=0; i<workerSize; i++) {
			workers[i] = new Worker(i);
		}
		
		try(Scanner stdin = new Scanner(System.in)) {
			while(stdin.hasNextLine()) {
				String stdinLine = stdin.nextLine();
				String[] parsingLine = stdinLine.split(" ");
				String runResult = workers[Integer.parseInt(parsingLine[1])].run(Integer.parseInt(parsingLine[0]), parsingLine[2]);
				if( runResult != null) {
					System.out.println(runResult);
				}
			}			
		}
/*				
1000 0 VIEW_AD1
1500 0 VIEW_AD2
2000 1 VIEW_AD3
2500 1 VIEW_AD4
4000 0 CLICK_AD1
5000 1 CLICK_AD4
6000 0 CLICK_AD2
7000 1 CLICK_AD3
*/
	}

}
