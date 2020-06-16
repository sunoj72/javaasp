package com.lgcns.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 1번 문제와 동일(1번에서 명시적으로 타입별로 확정해서 카운트 했다면 달라짐)
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
		
		//파일 쓰기(출력 파일:REPORT_2.TXT)
		PrintWriter printWriter = new PrintWriter(new File("./REPORT_2.TXT"));
		for(String key : msgTypeCount.keySet()) {
			printWriter.println(key+"#"+msgTypeCount.get(key));
		}
		printWriter.close();
	}
	
}

/**
 * 라인 데이터 저장 클래스
 */
class Message {
	String time;
	String type;
	String message;
	
	public Message(String time, String type, String message) {
		super();
		this.time = time;
		this.type = type;
		this.message = message;
	}
	
}