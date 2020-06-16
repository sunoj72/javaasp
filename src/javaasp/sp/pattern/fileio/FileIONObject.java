package javaasp.sp.pattern.fileio;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * 패턴
 * 1. 파일에서 라인 단위로 데이터를 읽고 라인 단위로 뭔가 처리해서 결과를 파일에 출력
 * 2. 라인 단위 데이터를 객체화해서 목록으로 저장
 * 3. 객체화 목록 데이터를 추가 처리(
 *
 */
public class FileIONObject {

	public static void main(String[] args) throws Exception {
		
		//파일 읽기
		Scanner fs = new Scanner(new File("./READFILE.TXT"));
		//사용자 입력 읽기
		//Scanner fs = new Scanner(System.io);
		
		//라인 단위 객체화(특정 문자로 문자 자르기)
		ArrayList<FileLineObject> lineList = new ArrayList<FileLineObject>();
		//라인 단위 처리
		while(fs.hasNextLine()) {
			String fileLine = fs.nextLine();
			//문자열 처리 : StringTokenizer, Split(정규식), Matcher(정규식), substring(고정길이경우)
			String[] lineDatas = fileLine.split(",");
			lineList.add(new FileLineObject(lineDatas[0], lineDatas[1], Integer.parseInt(lineDatas[2])));
		}
		
		//TODO : 데이터 처리
		for(FileLineObject oneObject : lineList) {
			//TODO : 라인 별 추가 작업
		}

		//처리 결과 파일 쓰기
		PrintWriter printWriter = new PrintWriter(new File("./WRITEFILE.TXT"));
		for(FileLineObject oneObject : lineList) {
			printWriter.println(oneObject);
		}
		printWriter.close();
	}
}

/**
 * TODO : 라인별 객체 클래스 설계
 */
class FileLineObject implements Comparable<FileLineObject> {

	String id;
	String name;
	int age;

	public FileLineObject(String id, String name, int age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}


	@Override
	public int compareTo(FileLineObject var1) {
		// 아이디로 오름 차순
		return id.compareTo(var1.id);
	}
}


