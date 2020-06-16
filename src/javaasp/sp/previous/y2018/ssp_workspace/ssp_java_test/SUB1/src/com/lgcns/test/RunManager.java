package com.lgcns.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
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
		
		//파일 읽기
		Scanner is = new Scanner(System.in);
		String fileName = is.nextLine();
		
		File readFile = new File("./BIGFILE/"+fileName);
		if(!readFile.exists()) {
			return;
		}
		
		Scanner fs = new Scanner(readFile);
		//라인 단위 객체화
		ArrayList<FileLineObject> lineList = new ArrayList<FileLineObject>();
		//라인 단위 처리
		while(fs.hasNextLine()) {
			String fileLine = fs.nextLine();
			if(lineList.size() == 0 || !lineList.get(lineList.size()-1).line.equals(fileLine)) {
				lineList.add(new FileLineObject(fileLine, 1));
			} else {
				lineList.get(lineList.size()-1).count++;
			}
		}

		//처리 결과 파일 쓰기
		PrintWriter printWriter = new PrintWriter(new File("./"+fileName+".TXT"));
		for(FileLineObject oneLine : lineList) {
			printWriter.println((oneLine.count>1?oneLine.count+"#":"")+oneLine.line);
		}
		printWriter.close();
	}
}

/**
 * TODO : 라인별 객체 클래스 설계
 */
class FileLineObject {

	String line;
	int count;
	
	public FileLineObject(String line, int count) {
		super();
		this.line = line;
		this.count = count;
	}

}
