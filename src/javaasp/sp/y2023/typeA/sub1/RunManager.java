package com.lgcns.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RunManager {

	public static void main(String[] args) throws FileNotFoundException {
				
		try(Scanner stdin = new Scanner(System.in)) {
			// 한라인씩 읽기
			String proxyName = stdin.nextLine();
			File proxyFile = new File("./" + proxyName + ".txt");
			if(proxyFile.exists()) {
				try(Scanner proxyFileSc = new Scanner(proxyFile)) {
					String serviceFileName = proxyFileSc.nextLine();
					Scanner serviceFileSc = new Scanner(new File(serviceFileName));
					System.out.println(serviceFileSc.nextLine());
				}
			}
		}			
	}

}
