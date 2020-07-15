package javaasp.sp.pattern.ZQuiz.quiz1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 문제1번 패턴 기본
 * - 문제에서 제시하는 파일에서 라인 단위로 정보를 읽어 특정 데이터 처리 후 결과를 파일에 출력
 */
public class Quiz1Pattern {

	static final String InputFilePath = "./INPUT/READFILE.TXT";
	static final String OutputFilePath = "./OUTPUT/WRITEFILE.TXT";
	
	static List<FileLineObject> objList;
	
	public static void main(String[] args) throws Exception {
		//1. 파일 라인 단위로 읽어 라인별 객체화 하기
		objList = getObjectListByFileLineWithStream(InputFilePath, FileLineObject.class);
		
		//2. 라인 단위로 객체화한 데이터를 갖고 추가 액션
		Object actionResult = actionWithObjectList();
		
		//3. 액션을 통해 생성된 데이터를 파일에 출력
		printResultToFile(actionResult);

	}

	//2. 라인 단위로 객체화한 데이터를 갖고 추가 액션
	static Object actionWithObjectList() throws FileNotFoundException {
		Object actionResult = null;
		// 데이터 처리 액션
		for(FileLineObject oneObject : objList) {
			// actionResult 생성
		}
		return actionResult;
	}

	//3. 결과 파일 출력
	static void printResultToFile(Object printData) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath));
		//for문 활용 출력
		for(FileLineObject oneObject : objList) {
			printWriter.println(oneObject);
		}
		//stream 활용 출력
		//objList.stream().forEach(o->printWriter.println(o));
		printWriter.close();
	}

	//Stream을 사용해서 파일 라인별 객체 목록
	static <T> List<T> getObjectListByFileLineWithStream(String filePath, Class<T> objectClass) throws IOException {
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
	static List<String> getLineListByFileLineWithStream(String filePath) throws IOException {
		List<String> lineList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			lineList = stream.collect(Collectors.toList());
		}
		
		return lineList;
	}

}

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
		this.id = id;
		this.name = name;
		this.age = age;
	}


	@Override
	public int compareTo(FileLineObject var1) {
		// 아이디로 오름 차순
		return id.compareTo(var1.id);
	}
	
	@Override
	public String toString() {
		return "FileLineObject [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}
