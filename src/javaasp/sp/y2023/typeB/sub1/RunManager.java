package com.lgcns.test;

import java.util.Scanner;

public class RunManager {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Worker ���� �� ���� Sample - �Ʒ� 2���� ������ ����� �����ϼ���.
		Worker worker = new Worker(0);
		worker.run("VIEW_AD1");
		
		int workerSize = 2;
		Worker[] workers = new Worker[workerSize];
		for(int i=0; i<workerSize; i++) {
			workers[i] = new Worker(i);
		}
		
		try(Scanner stdin = new Scanner(System.in)) {
			while(stdin.hasNextLine()) {
				String stdinLine = stdin.nextLine();
				String[] parsingLine = stdinLine.split(" ");
				String runResult = workers[Integer.parseInt(parsingLine[0])].run(parsingLine[1]);
				if( runResult != null) {
					System.out.println(runResult);
				}
			}			
		}
		
//		0 VIEW_AD1
//		1 VIEW_AD2
//		0 CLICK_AD1
//		1 CLICK_AD3
//		1 CLICK_AD2
	}
}
