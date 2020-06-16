package com.lgcns.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 3번 문제에서 변경/추가된 내용
 * 1. 외부 프로세스를 이용해서 메시지 변화 작업
 * 2. 타입별 메시지 출력
 *
 */
public class RunManager {

	public static void main(String[] args) throws Exception {
		//파일 읽기('.'경로를 붙임으로서 상대경로 처리)
		//파일 읽는 방법 : Scanner, ...
		Scanner fs = new Scanner(new File("./LOGFILE_B.TXT"));
		
		//객체 목록
		ArrayList<Message> msgList = new ArrayList<Message>();
		//타입별 개수 저장용 Map : key 기준으로 정렬이 필요한 경우 TreeMap 사용
		HashMap<String, Integer> msgTypeCount = new HashMap<String, Integer>();
		//키 오름차순
		//TreeMap<String, Integer> msgTypeCount = new TreeMap<String, Integer>();
		//연습 필요 : 키 내림차순, 값 오름차순/내림차순
		
		//한줄씩 읽기
		while(fs.hasNextLine()) {
			String msgLine = fs.nextLine();
			//문자열 처리 : StringTokenizer, Split(정규식), Matcher(정규식), substring(고정길이경우)
			//참조 : 공백 개수에 상관없이 공백으로 자르기 : line.split("\\s+");
			String[] msgInfo = msgLine.split("#");
			//객체화 후 목록에 저장
			msgList.add(new Message(msgInfo[0], msgInfo[1], msgInfo[2]));
			//타입별 개수 저장
			msgTypeCount.put(msgInfo[1], msgTypeCount.getOrDefault(msgInfo[1], 0)+1 );
		}
		
		//파일 쓰기(출력 파일:REPORT_3.TXT)
		PrintWriter printWriter = new PrintWriter(new File("./REPORT_3.TXT"));
		for(String key : msgTypeCount.keySet()) {
			printWriter.println(key+"#"+msgTypeCount.get(key));
		}
		printWriter.close();
		
		//순차적으로 타입별 파일 생성을 위해 타입 기준 정렬
		Collections.sort(msgList);
		
		//파일 쓰기(출력 파일:TYPELOG_3_타입.TXT)
		PrintWriter typeLogWriter = null;
		String lastType = null;
		for(Message oneMsg : msgList) {
			if(lastType == null || !oneMsg.type.equals(lastType)) {
				if(typeLogWriter != null) {
					typeLogWriter.close();
				}
				lastType = oneMsg.type;
				typeLogWriter = new PrintWriter(new File("./TYPELOG_3_" + oneMsg.type +".TXT"));
			}
			typeLogWriter.println(oneMsg.time+"#"+oneMsg.type+"#"+oneMsg.convertMessage);
		}
		typeLogWriter.close();
	}
	
}

/**
 * 라인 데이터 저장 클래스
 * 
 * 3번에서 추가된 점 : 
 *  1. 변환된 메시지 저장을 위한 필드 추가 : convertMessage
 *  2. message를 외부프로그램을 통해 변환하기
 *  3. 정렬 기능을 위해 Comparable 구현
 */
class Message implements Comparable<Message> {
	String time;
	String type;
	String message;
	String convertMessage;
	
	public Message(String time, String type, String message) throws Exception {
		super();
		this.time = time;
		this.type = type;
		this.message = message;
		this.convertMessage = convertMessage(this.message);
	}
	
	private String convertMessage(String message) throws Exception {
		Process convertMessageProcess = Runtime.getRuntime().exec("./CODECONV.EXE " + message);
	    BufferedReader inputStream = new BufferedReader(new InputStreamReader( convertMessageProcess.getInputStream(),"euc-kr"));  
	    String convertMessage = inputStream.readLine();

	    return convertMessage;
	}

	@Override
	public int compareTo(Message o) {
		return type.compareTo(o.type);
	}
}