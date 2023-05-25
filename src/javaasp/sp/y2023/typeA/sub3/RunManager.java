package com.lgcns.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RunManager {
	
	private static HashMap<String, ProxyInfo> proxyMap = new HashMap<>();

	public static void main(String[] args) throws Exception {

		// proxy 서버s 띄우기
		startProxyServiceData();

//		try(Scanner stdin = new Scanner(System.in)) {
//			// 한라인씩 읽기 Proxy-2 /notice
//			String[] proxyServicePath = stdin.nextLine().split(" ");
//			
//			parsingProxyServiceData();
//			
//			String serviceFilePath = getServicePath(proxyServicePath[0], proxyServicePath[1]);
//			
//			Scanner serviceFileSc = new Scanner(new File(serviceFilePath));
//			System.out.println(serviceFileSc.nextLine());
//			
//			System.out.println("");			
//			
//		}

	}
	
	private static void startProxyServiceData() throws Exception {
		File proxyFileDir = new File(".");
		File[] proxyFiles = proxyFileDir.listFiles(new FileFilter() {			
			@Override
			public boolean accept(File arg0) {
				// TODO Auto-generated method stub
				return arg0.getName().startsWith("Proxy-");
			}
		});
		
		ArrayList<ProxyServer> proxyServerList = new ArrayList<>();
		for(File oneproxyFile : proxyFiles) {
			ProxyServer proxyServer = new ProxyServer(oneproxyFile);
			proxyServerList.add(proxyServer);
			proxyServer.startServer();
		}
		for(ProxyServer proxyServer : proxyServerList ) {
			proxyServer.joinServer();
		}
		
		System.out.println("ProxyServers Started!!");
	}
	
	
//	private static void parsingProxyServiceData() throws FileNotFoundException {
//		
//		File proxyFileDir = new File(".");
//		File[] proxyFiles = proxyFileDir.listFiles(new FileFilter() {			
//			@Override
//			public boolean accept(File arg0) {
//				// TODO Auto-generated method stub
//				return arg0.getName().startsWith("Proxy-");
//			}
//		});
//		
//		for(File oneproxyFile : proxyFiles) {
//			String proxyName = oneproxyFile.getName().replace(".txt", "");
//			if(!proxyMap.containsKey(proxyName)) {
//				proxyMap.put(proxyName, new HashMap<String, String>());
//				
//				try(Scanner proxyFileSc = new Scanner(oneproxyFile)) {
//					while(proxyFileSc.hasNextLine()) {
//						// 한라인씩 읽기
//						String svcPathFileLine = proxyFileSc.nextLine();
//						// 한라인 문자열 파싱 ///front#Service-A.txt
//						String[] svcPathFile = svcPathFileLine.split("#");
//						//System.out.println(String.join(",", parsingLine));
//						proxyMap.get(proxyName).put(svcPathFile[0], svcPathFile[1]);
//					}			
//				}			
//			}
//		}
//		
//	}
	
//	private static String getServicePath(String proxyName, String svcPath) {
//		
//		String svcFilePath = proxyMap.get(proxyName).get(svcPath);
//		
//		svcFilePath = svcFilePath.startsWith("Service-")?svcFilePath:getServicePath(svcFilePath.replace(".txt", ""), svcPath);
//		
//		return svcFilePath;
//	}

}
