package com.lgcns.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 4번 문제에서 변경/추가된 내용
 * 1. 메시지 변환과 출력을 병렬 처리(Thread)
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
		PrintWriter printWriter = new PrintWriter(new File("./REPORT_4.TXT"));
		for(String key : msgTypeCount.keySet()) {
			printWriter.println(key+"#"+msgTypeCount.get(key));
		}
		printWriter.close();
		
		//순차적으로 타입별 파일 생성을 위해 타입 기준 정렬
//		Collections.sort(msgList); //3번 문제 삭제
		
		//파일 쓰기(출력 파일:TYPELOG_4_타입.TXT)
		//변환과 쓰기 부분을 병렬로 처리
		ArrayList<MsgConvertNWrite> msgTypePrinterThread = new ArrayList<MsgConvertNWrite>();
		for(String typeKey : msgTypeCount.keySet()) {
			PrintWriter typeLogWriter = new PrintWriter(new File("./TYPELOG_4_" + typeKey +".TXT"));
			//msgList를 타입별로 필터링 해서 보내줘도 될듯 하지만 그냥 출력 시 필터링 하는걸로...
			MsgConvertNWrite convWriteThread = new MsgConvertNWrite(typeKey,msgList,typeLogWriter);
			msgTypePrinterThread.add(convWriteThread);
			convWriteThread.start();
		}
		
		//모든 스레드 종료 시까지 대기 : 모든 작업 후에 추가 작업이 없으면 궂이 할 필요는 없음
		for(MsgConvertNWrite oneWorker : msgTypePrinterThread) {
			oneWorker.join();
		}

	}
	
}

/**
 * 라인 데이터 저장 클래스
 * 
 * 4번에서 추가된 점 : 
 *  1. message를 외부프로그램을 통해 변환하기를 외부에서 호출하도록 메소드(convertMessage)를 public으로 변경
 *  2. 정렬 기능을 위해 Comparable 구현 (삭제해도 무방하지만 그냥 소스상으로 유지)
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
		//this.convertMessage = convertMessage(this.message); //3번 문제 삭제
	}
	
	public String convertMessage(String message) throws Exception {
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

/**
 * 변환과  쓰기 작업 병렬 처리 쓰레드
 */
class MsgConvertNWrite extends Thread {
	
	private String type;
	private ArrayList<Message> msgList;
	private PrintWriter typeLogWriter;

	public MsgConvertNWrite(String type, ArrayList<Message> msgList, PrintWriter typeLogWriter) {
		super();
		this.type = type;
		this.msgList = msgList;
		this.typeLogWriter = typeLogWriter;
	}

	@Override
	public void run() {
		for(Message oneMsg : msgList) {
			if(this.type.equals(oneMsg.type)) {
				try {
					oneMsg.convertMessage = oneMsg.convertMessage(oneMsg.message);
					typeLogWriter.println(oneMsg.time+"#"+oneMsg.type+"#"+oneMsg.convertMessage);
//					System.out.println(Thread.currentThread().getName() + ":" + oneMsg.time+"#"+oneMsg.type+"#"+oneMsg.convertMessage);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		typeLogWriter.close();
	}
} 