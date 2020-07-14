package javaasp.sp.pattern.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;


public class SocketHandler {

	private static final int SERVER_PORT = 9876;

	public static void main(String[] args) throws Exception {
		try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
			//클라이언트 연결 대기 - blocking
			Socket client = serverSocket.accept();
			
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
				//2.1 문자열(라인) 처리를 위한 inputstream/outputstream
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
				PrintWriter outputPrinter = new PrintWriter(new OutputStreamWriter(outputStream), true);
				//2.2 문자열(라인) 읽기
				System.out.println(inputReader.readLine());
				//2.3 문자열(라인) 쓰기
				outputPrinter.println("Output Result");
				outputPrinter.flush();
				///////////////////////////////////////////////////////////////////////////////
				
			}
		}	

	}

}
