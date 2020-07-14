package javaasp.sp.pattern.ZQuiz.quiz2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 문제2번 패턴 기본
 * - 문제1번 유지 : 문제에서 제시하는 파일에서 라인 단위로 정보를 읽어 특정 데이터 처리 후 결과를 파일에 출력
 * - 문제2번 : 2.1 추가 파일을 주고 라인 단위로 정보를 읽은 후에 문제1번에서 생성한 데이터와 조합해서 동작한 후 데이터 처리 후 결과를 파일에 출력
 * - 문제2번 : 2.2 추가 정보를 외부 데이터에서 읽은 후에 문제1번에서 생성한 데이터와 조합해서 동작한 후 데이터 처리 후 결과를 파일에 출력 
 */
public class Quiz2Pattern1 {

	static final String InputFilePath = "./INPUT/READFILE.TXT";
	static final String OutputFilePath = "./OUTPUT/WRITEFILE.TXT";
	static List<FileLineObject1> obj1List;
	
	static final String InputFilePath2 = "./INPUT/READFILE2.TXT";
	static final String InputExternalProgramPath = "./INPUT/EXTPRO.EXE";
	static final String OutputFilePath2 = "./OUTPUT/WRITEFILE2.TXT";
	static List<LineObject2> obj2List;
	
	public static void main(String[] args) throws Exception {
		//문제1번 : 파일 라인 단위로 읽어 라인별 객체화 하기
		obj1List = getObjectListByFileLineWithStream(InputFilePath, FileLineObject1.class);
		//문제1번 :  라인 단위로 객체화한 데이터를 갖고 추가 액션
		Object actionResult = actionWithObjectList();
		//문제1번 : 액션을 통해 생성된 데이터를 파일에 출력
		printResultToFile(actionResult);
		
		//문제2.1번 : 파일 라인 단위로 읽어 라인별 객체화 하기
		obj2List = getObjectListByFileLineWithStream(InputFilePath2, LineObject2.class);
		//문제2.2번 : 파일 라인 단위로 읽어 라인별 객체화 하기
		obj2List = getObjectListByFileLineFromExternalProgram(InputExternalProgramPath, LineObject2.class);
		//문제2번 추가 동작
		Object result = actionWithObjectListCombination();
		//문제2번 : 액션을 통해 생성된 데이터를 파일에 출력
		printResultToFile2(result);

	}

	static Object actionWithObjectListCombination() {
		Object actionResult = null;
		// 데이터 처리 액션
		for(FileLineObject1 oneObject1 : obj1List) {
			for(LineObject2 oneObject2 : obj2List) {
				// actionResult 생성
			}
		}
		
		return actionResult;
	}
	
	static void printResultToFile2(Object result) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath2));
		//for문 활용 출력
		printWriter.println("Quiz2 Result");
		
		printWriter.close();
	}

	static Object actionWithObjectList() throws FileNotFoundException {
		Object actionResult = null;
		// 데이터 처리 액션
		for(FileLineObject1 oneObject : obj1List) {
			// actionResult 생성
		}
		return actionResult;
	}

	//3. 결과 파일 출력
	static void printResultToFile(Object printData) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath));
		//for문 활용 출력
		for(FileLineObject1 oneObject : obj1List) {
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
	
	/**
	 * 외부 프로그램을 통해서 데이터 읽기 - 스트링 라인 목록
	 * @param command
	 * @return
	 * @throws Exception
	 */
	static List<String> getStringListByFileLineFromExternalProgram(String command) throws Exception {
		Process theProcess = Runtime.getRuntime().exec(command);
	    BufferedReader inStream = new BufferedReader(new InputStreamReader( theProcess.getInputStream(),"euc-kr"));
	    List<String> readData = new ArrayList<String>();
	    String line = null;
	    while ( ( line = inStream.readLine( ) ) != null ) {
	    	readData.add(line);
	    }
	    return readData;		
	}
	
	/**
	 * 외부 프로그램을 통해서 데이터 읽기 - 라인 단위 객채화 후 객체 목록
	 * @param <T> 리턴 객체 타입
	 * @param command
	 * @param objectClass
	 * @return
	 * @throws Exception
	 */
	static <T> List<T> getObjectListByFileLineFromExternalProgram(String command, Class<T> objectClass) throws Exception {
		Process theProcess = Runtime.getRuntime().exec(command);
	    BufferedReader inStream = new BufferedReader(new InputStreamReader( theProcess.getInputStream(),"euc-kr"));
	    List<String> readData = new ArrayList<String>();
	    String line = null;
	    while ( ( line = inStream.readLine( ) ) != null ) {
	    	readData.add(line);
	    }
	    
	    List<T> objList = readData.stream().map(s->{
			try {
				return objectClass.getConstructor(String.class).newInstance(s);
			} catch (Exception ex) { ex.printStackTrace(); }
			return null;
		}).collect(Collectors.toList());
		
	    return objList;		
	}

}

class FileLineObject1 implements Comparable<FileLineObject1> {
	String id;
	String name;
	int age;

	public FileLineObject1(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		
		id = lineDatas[0];
		name = lineDatas[1];
		age = Integer.parseInt(lineDatas[2]);
	}
	
	public FileLineObject1(String id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}


	@Override
	public int compareTo(FileLineObject1 var1) {
		// 아이디로 오름 차순
		return id.compareTo(var1.id);
	}
	
	@Override
	public String toString() {
		return "FileLineObject1 [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}

class LineObject2 implements Comparable<LineObject2> {
	
	public LineObject2(String parseLine) {
		String[] lineDatas = parseLine.split(",");
	}

	@Override
	public int compareTo(LineObject2 var1) {
		return 0;
	}

	@Override
	public String toString() {
		return "LineObject2 []";
	}
}
