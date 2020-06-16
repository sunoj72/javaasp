package com.lgcns.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * 패턴
 * 1. 파일에서 라인 단위로 데이터를 읽고 라인 단위로 뭔가 처리해서 결과를 파일에 출력
 * 2. 라인 단위 데이터를 객체화해서 목록으로 저장
 * 3. 객체화 목록 데이터를 추가 처리(
 *
 */
public class RunManager {

	public static void main(String[] args) throws Exception {
		
		BufferedWriter outStream = null;
		BufferedReader intStream = null;
		
		try(ServerSocket serverSocket = new ServerSocket(9876)){
			
			Socket socket = serverSocket.accept();
			
			outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			intStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//파일 읽기
//			Scanner is = new Scanner(System.in);
//			String fileName = is.nextLine();
			char[] inputBytes = new char[1024];
			intStream.read(inputBytes);
			String[] inputFileNameNKeyword = new String(inputBytes).trim().split("#");
			String fileName = inputFileNameNKeyword[0];
			
			
			File readFile = searchFile(new File("./BIGFILE/"), fileName);
			if(readFile == null) {
				return;
			}
			
			//처리 후 결과 전송
			int fromLine = 1;
			while(fromLine>0) {
				//라인 단위 객체화
				ArrayList<FileLineObject> lineList = readFile(readFile, fromLine, inputFileNameNKeyword[1]);
				//처리 결과 Client에 보내기
				fromLine = sendLineToCient(lineList, outStream, intStream, socket);
			}
			
			socket.close();
		}		
	}
	
	private static File searchFile(File rootDir, String searchFileName) {
		
		File[] files = rootDir.listFiles();
		for(File oneFile : files) {
			if(oneFile.isFile() && oneFile.getName().equals(searchFileName)) {
				return oneFile;
			} else {
				File sf = searchFile(oneFile, searchFileName);
				if(sf != null) {
					return sf;
				}
			}
		}
		
		return null;		
	}
	
	private static ArrayList<FileLineObject> readFile(File readFile, int fromLine, String encryptionKeyword) throws Exception {
		ArrayList<FileLineObject> lineList = new ArrayList<FileLineObject>();
		Scanner fs = new Scanner(readFile);
		int currentLine = 1;
		//라인 단위 처리
		while(fs.hasNextLine()) {
			String fileLine = fs.nextLine();
			if(currentLine<fromLine) {
				currentLine++;
				continue;
			}
			if(lineList.size() == 0 || !lineList.get(lineList.size()-1).line.equals(fileLine)) {
				lineList.add(new FileLineObject(fileLine, 1, encryptionKeyword));
			} else {
				lineList.get(lineList.size()-1).count++;
			}
			
			currentLine++;
		}
		
		return lineList;
	}
	
	private static int sendLineToCient(ArrayList<FileLineObject> lineList, BufferedWriter outStream, BufferedReader intStream, Socket socket) throws IOException {
		for(FileLineObject oneLine : lineList) {
//			String sendMessage = (oneLine.count>1?oneLine.count+"#":"")+oneLine.ceaserLine+"\n";
			String sendMessage = (oneLine.count>1?oneLine.count+"#":"")+oneLine.encryptionByKeyword+"\n";
			
			socket.getOutputStream().write(sendMessage.getBytes());
			
			byte[] inputBytes = new byte[8];
			socket.getInputStream().read(inputBytes);
			String resMsg = new String(inputBytes).trim();
			if("ACK".equals(resMsg)) {
				continue;
			} else if("ERR".equals(resMsg)) {
				while("ERR".equals(resMsg)) {
					socket.getOutputStream().write(sendMessage.getBytes());
					socket.getInputStream().read(inputBytes);
					resMsg = new String(inputBytes).trim();
				}
			} else { //NUMBER
				return Integer.parseInt(resMsg);
			}
		}
		
		return 0;
	}
}

/**
 * TODO : 라인별 객체 클래스 설계
 */
class FileLineObject {

	String line;
	int count;
	String compressedLine;
	String encryptedByCeaser;
	
	String encryptionKeyword;
	ArrayList<Character> encryptionKeywordChars = new ArrayList<Character>();
	String encryptionByKeyword;
	
	
	public FileLineObject(String line, int count, String encryptionKeyword) {
		super();
		this.line = line;
		this.count = count;
		
		this.encryptionKeyword = encryptionKeyword;
		char[] chars = this.encryptionKeyword.toCharArray();
		for(char oneChar : chars) {
			encryptionKeywordChars.add(oneChar);
		}
		
		compressLine();
		//encryptByCeaser();
		encryptByKeyword();
	}
	
	public void compressLine() {
		StringBuilder sb = new StringBuilder();
		int charCount = 0;
		Character lastChar = null;
		for(int i=0; i<this.line.length(); i++) {
			if(lastChar==null || lastChar.equals(this.line.charAt(i))) {
				charCount++;
			} else {
				if(charCount>2) {
					sb.append(charCount);
				} else if(charCount==2) {
					sb.append(lastChar);
				}
				sb.append(lastChar);
				charCount = 1;
			}
			lastChar = this.line.charAt(i);
		}
		//마지막 char처리
		if(charCount>2) {
			sb.append(charCount);
		} else if(charCount==2) {
			sb.append(lastChar);
		}
		sb.append(lastChar);
		
		this.compressedLine = sb.toString();
		
	}
	
	public void encryptByCeaser() {
		char[] encLine = new char[this.compressedLine.length()];
		for(int i=0; i<this.compressedLine.length(); i++) {
			if(this.compressedLine.charAt(i)>= 'A' && this.compressedLine.charAt(i)<= 'Z') {
				encLine[i] = (char)(this.compressedLine.charAt(i)-5);
				if(encLine[i]<'A') {
					encLine[i] = (char)(encLine[i]+26);
				}
			} else {
				encLine[i] = this.compressedLine.charAt(i);
			}
		}
		encryptedByCeaser = new String(encLine);
	}
	
	public void encryptByKeyword() {
		char[] encLine = new char[this.compressedLine.length()];
		for(int i=0; i<this.compressedLine.length(); i++) {
			if(!this.encryptionKeywordChars.contains(this.compressedLine.charAt(i))) {
				encLine[i] = this.compressedLine.charAt(i);
			}
		}
		Arrays.sort(encLine);
		this.encryptionByKeyword = this.encryptionKeyword + new String(encLine).trim();
	}
		

}
