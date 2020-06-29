package javaasp.sp.pattern.fileio;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 패턴
 * 1. 파일에서 라인 단위로 데이터를 읽고 라인 단위로 뭔가 처리해서 결과를 파일에 출력
 * 2. 라인 단위 데이터를 객체화해서 목록으로 저장
 * 3. 객체화 목록 데이터를 추가 처리(
 *
 */
public class FileIONObject {

	public static void main(String[] args) throws Exception {
		
		String filePath = "./READFILE.TXT";
		List<FileLineObject> objList = null;
		
		//1.1 파일 라인 단위로 읽어 라인별 객체화 하기 - 방법 1 : Scanner
		objList = getObjecListByFileLineWithScanner(filePath);
		
		//1.2 파일 라인 단위로 읽어 라인별 객체화 하기 - 방법 2 : Stream
		objList = getObjectListByFileLineWithStream(filePath);
		
		//1.2 파일 라인 단위로 읽어 라인별 객체화 하기 - 방법 2-1 : Stream
		// 추상화된 함수를 통해 하는 걸로 변경... 매번 생성 객체별 함수를 만들 필요가 없음
		objList = getObjectListByFileLineWithStream(filePath, FileLineObject.class);
				
		//TODO : 데이터 처리
		for(FileLineObject oneObject : objList) {
			//TODO : 라인 별 추가 작업
		}

		//처리 결과 파일 쓰기
		PrintWriter printWriter = new PrintWriter(new File("./WRITEFILE.TXT"));
		//3.1 for문 활용 출력
		for(FileLineObject oneObject : objList) {
			printWriter.println(oneObject);
		}
		//3.2 stream 활용 출력
		//objList.stream().forEach(o->printWriter.println(o));
		
		printWriter.close();
	}
	
	//Scanner를 사용해서 파일 라인별 객체 목록
	public static List<FileLineObject> getObjecListByFileLineWithScanner(String filePath) throws IOException {
		Scanner fs = new Scanner(new File(filePath));
		//사용자 입력 읽기
		//Scanner fs = new Scanner(System.io);
		
		//라인 단위 객체화(특정 문자로 문자 자르기)
		ArrayList<FileLineObject> objList = new ArrayList<FileLineObject>();
		//라인 단위 처리
		while(fs.hasNextLine()) {
			String fileLine = fs.nextLine();
			//문자열 처리 : StringTokenizer, Split(정규식), Matcher(정규식), substring(고정길이경우)
			String[] lineDatas = fileLine.split(",");
			objList.add(new FileLineObject(lineDatas[0], lineDatas[1], Integer.parseInt(lineDatas[2])));
		}
		fs.close();
		
		return objList;
	}

	//Stream을 사용해서 파일 라인별 객체 목록
	public static List<FileLineObject> getObjectListByFileLineWithStream(String filePath) throws IOException {
		List<FileLineObject> objList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			objList = stream.map(FileLineObject::new).collect(Collectors.toList());
		}
		
		return objList;
	}
	
	//Stream을 사용해서 파일 라인별 객체 목록 - 모든 클래스에 대응하도록 추상화.(전
	/**
	 * Stream을 사용해서 파일 라인별 객체 생성 - 모든 객체 타입에 대응하도록 타입 추상화
	 * <br> 전제 : 라인 문자열을 처리하기 위한 생성자가 존재해야 함(ex. ObjectClass(String line))
	 * @param <T> 객체 타입
	 * @param filePath 파일경로
	 * @param objectClass 생성 객체 타입
	 * @return 생성 인스턴스 목록
	 * @throws IOException
	 */
	public static <T> List<T> getObjectListByFileLineWithStream(String filePath, Class<T> objectClass) throws IOException {
		Stream<String> stream = Files.lines(Paths.get(filePath));
		List<T> objList = stream.map(line->{
			try {
				return objectClass.getConstructor(String.class).newInstance(line);
			} catch (Exception ex) { ex.printStackTrace(); }
			return null;
		}).collect(Collectors.toList());
		stream.close();
		
		return objList;
	}
	
	//Stream을 사용해서 파일 라인 목록 가져오기
	public static List<String> getLineListByFileLineWithStream(String filePath) throws IOException {
		List<String> lineList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			lineList = stream.collect(Collectors.toList());
		}
		
		return lineList;
	}	
	
}

/**
 * TODO : 라인별 객체 클래스 설계
 */
class FileLineObject implements Comparable<FileLineObject> {

	String id;
	String name;
	int age;

	public FileLineObject(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		
		id = lineDatas[0];
		name = lineDatas[1];
		age = Integer.parseInt(lineDatas[2]);
	}
	
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


