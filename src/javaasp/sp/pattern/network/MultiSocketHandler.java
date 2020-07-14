package javaasp.sp.pattern.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MultiSocketHandler {
	
	private static final int SERVER_PORT = 9876;

	public static void main(String[] args) throws Exception {
		//1. 쓰레드를 활용한 멀티 소켓 처리 - 기본
		handleMultiSocketWithBasicThread();
		//2. 쓰레드를 활용한 멀티 소켓 처리 - 쓰레드풀 적용
		handleMultiSocketWithThreadPool();
		
		//메인 스레드 작업
		System.out.println(String.format("[%s] : Main Thread End", Thread.currentThread().getName()));
	}
	
	/**
	 * 쓰레드를 활용한 멀티 소켓 처리 - 기본
	 * @throws Exception
	 */
	public static void handleMultiSocketWithBasicThread() throws Exception {
		// 클라이언트 처리 스레드 리스트
		List<Thread> clientHandlerList = new ArrayList<Thread>();
		try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
			//클라이언트 연결 대기 - blocking
			Socket client = serverSocket.accept();
			//클라이언트 별로 Thead로 처리 작업 할당
			SocketClientHandler clientHandler = new SocketClientHandler(client);
			clientHandlerList.add(clientHandler);
			clientHandler.start();
		}
		
		//스레드 종료 시까지 대기 - 앞 실행했던 Thread가 종료 된 후에 main스레드 동작이 있을 때
		for(Thread action : clientHandlerList) {
			action.join();
		}
		
	}
	
	/**
	 * 쓰레드를 활용한 멀티 소켓 처리 - 쓰레드풀 적용
	 * @throws InterruptedException
	 */
	public static void handleMultiSocketWithThreadPool() throws Exception {
		// 쓰레드 풀 생성
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
			//클라이언트 연결 대기 - blocking
			Socket client = serverSocket.accept();
			//클라이언트 별로 Thead로 처리 작업 할당
			SocketClientHandler clientHandler = new SocketClientHandler(client);
			executorService.submit(clientHandler);
		}
		
		//스레드 종료 시까지 대기 - 앞 실행했던 Thread가 종료 된 후에 main스레드 동작이 있을 때
		executorService.shutdown();
		//executorService.awaitTermination(10, TimeUnit.SECONDS);
		while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
	}
}

class SocketClientHandler extends Thread {
	
	Socket client;
	
	public SocketClientHandler(Socket client) {
		this.client = client;		
	}

	@Override
	public void run() {
		try {
			//byte처리를 위한 inputstream/outputstream
			try(InputStream inputStream = client.getInputStream();
					OutputStream outputStream = client.getOutputStream()) {
			
				byte[] inputBytes = new byte[30];
				//1.1 stream 읽기
				int byteLength = client.getInputStream().read(inputBytes);
				System.out.println(new String(Arrays.copyOf(inputBytes, byteLength)));
				//1.2 stream 쓰기
				outputStream.write("Output Result".getBytes());
				outputStream.flush();
				
				///////////////////////////////////////////////////////////////////////////////
				//2.1 문자열(라인) 처리를 위한 inputReader/outputPrinter
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
				PrintWriter outputPrinter = new PrintWriter(new OutputStreamWriter(outputStream), true);
				//2.2 문자열(라인) 읽기 - BufferedReader
				System.out.println(inputReader.readLine());
				//2.3 문자열(라인) 쓰기 - PrintWriter
				outputPrinter.println("Output Result");
				outputPrinter.flush();
				///////////////////////////////////////////////////////////////////////////////
			}
			System.out.println(String.format("[%s] : Action Thread End", Thread.currentThread().getName()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} 
