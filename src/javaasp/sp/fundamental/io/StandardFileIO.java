package javaasp.sp.fundamental.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class StandardFileIO {

	public static void main(String[] args) throws Exception {

		String filePath = "";
		
		//표준입출력 : 사용자입력을 통해 데이터 생성
		standardIo();
		
		//파일입출력
		fileio();
		
		//파일입출력 + 라인별 객체화
		fileioNObejct();
		
		// 파일 라인단위로 객체화해서 가져오기
		filePath = "./files/READFILETOOBJECT.TXT";
		System.out.println(">>>파일 라인단위로 객체화해서 가져오기");
		List<FileLineObject> objList = getObjectListByFileLineWithStream(filePath, FileLineObject.class);
		System.out.println(objList.toString());
		
		// 파일 전체 읽기
		filePath = "./files/READFILE.TXT";
		System.out.println(">>> 파일 전체 읽기 1");
		String content = readContentOfFileUsingFilesAllLines(filePath);
		System.out.println(content);
		System.out.println(">>> 파일 전체 읽기 2");
		String content2 = readContentOfFileUsingFilesAllBytes(filePath);
		System.out.println(content2);

	}
	
	/**
	 * 표준 입출력 <br/>
	 * 표준입력(사용자입력:키보드)으로 들어온 사용자 입력 데이터를 라인 단위로 읽어서
	 * 토크나이징(파싱,스플릿)해서 컬렉션(List,Set,Map)에 데이터를 저장한 후 특정 조건 일 때 컬렉션에 있는 데이터를 표준출력(모니터)로 출력하기 <br/>
	 * <br/> 
	 * 예제 시나리오 : 사용자가 입력한 '키#값'형태의 텍스트를 Map저장하고 '키'형태로만 들어오는 경우 Map에서 값을 조회해서 출력
	 */
	public static void standardIo() {
		
		HashMap<String, String> hmData = new HashMap<String, String>();
		// 표준입출력(사용자 콘솔 입력)
		try(Scanner stdin = new Scanner(System.in)) {
			while(stdin.hasNextLine()) {
				// 한라인씩 읽기
				String stdinLine = stdin.nextLine();
				System.out.println(stdinLine);
				// 한라인 문자열 파싱
				String[] parsingLine = stdinLine.split("#");
				System.out.println(String.join(",", parsingLine));
				// 파싱한 문자열 컬렉션 데이터 저장/조회
				if(parsingLine.length == 2) {
					// 컬렉션 데이터 저장
					hmData.put(parsingLine[0], parsingLine[1]);
				} else {
					// 조건 조회
					System.out.println(hmData.get(parsingLine[0]));
				}
				
			}			
		}
	}
	
	/**
	 * 표준 입출력 <br/>
	 * 파일을 라인 단위로 읽어서 토크나이징(파싱,스플릿)해서 컬렉션(List,Set,Map)에 데이터를 저장한 후 특정 조건 일 때 컬렉션에 있는 데이터를 파일로 출력하기 <br/>
	 * <br/>
	 * <br/>
	 * 예제 시나리오 : 파일을 라인단위로 읽고 '키#값'형태의 라인 데이터를 Map저장하고 '키'형태로만 들어오는 경우 Map에서 값을 조회해서 새로운 파일에 출력
	 */
	public static void fileio() throws FileNotFoundException {
		
		HashMap<String, String> hmData = new HashMap<String, String>();
		// 표준입출력(사용자 콘솔 입력)
		String filePath = "./files/READFILE.TXT";
		
		try(Scanner stdin = new Scanner(new File(filePath));
				PrintWriter printWriter = new PrintWriter(new File("./files/WRITEFILE.TXT"))) {
			while(stdin.hasNextLine()) {
				// 한라인씩 읽기
				String stdinLine = stdin.nextLine();
				//System.out.println(stdinLine);
				// 한라인 문자열 파싱
				String[] parsingLine = stdinLine.split("#");
				//System.out.println(String.join(",", parsingLine));
				// 파싱한 문자열 컬렉션 데이터 저장/조회
				if(parsingLine.length == 2) {
					// 컬렉션 데이터 저장
					hmData.put(parsingLine[0], parsingLine[1]);
				} else {
					//처리 결과 파일 쓰기
					printWriter.println(parsingLine[0]);
					// 처리 결과 화면 출력
					System.out.println(parsingLine[0] + ">" + hmData.get(parsingLine[0]));
				}
				
			}			
		}
	}
		
	/**
	 * 표준 입출력 <br/>
	 * 파일을 라인 단위로 읽어서 토크나이징(파싱,스플릿)해서 컬렉션(List,Set,Map)에 데이터를 저장한 후 특정 조건 일 때 컬렉션에 있는 데이터를 파일로 출력하기 <br/>
	 * + 라인단위 데이터를 객체화해서 저장 <br/>
	 * <br/>
	 * <br/>
	 * 예제 시나리오 : 파일을 라인단위로 읽고 '키#값'형태의 라인 데이터를 Map저장하고 '키'형태로만 들어오는 경우 Map에서 값을 조회해서 '키#값' 형태로 파일에 쓰기<br/>
	 * + 종료(exit) 명령 시 모든 리스트의 데이터를 파일에 출력하고 종료
	 */
	public static void fileioNObejct() throws FileNotFoundException {
		
		//Map
		HashMap<String, FileLineObject> hmData = new HashMap<String, FileLineObject>();
		//List
		List<FileLineObject> listData = new ArrayList<FileLineObject>();
		// 표준입출력(사용자 콘솔 입력)
		String filePath = "./files/READFILE.TXT";
		
		
		try(Scanner stdin = new Scanner(new File(filePath));
				PrintWriter printWriter = new PrintWriter(new File("./files/WRITEFILE.TXT"));
				PrintWriter printWriterAll = new PrintWriter(new File("./files/WRITEFILEALL.TXT"))) {
			while(stdin.hasNextLine()) {
				// 한라인씩 읽기
				String stdinLine = stdin.nextLine();
				//System.out.println(stdinLine);
				// 한라인 문자열 파싱
				String[] parsingLine = stdinLine.split("#");
				//System.out.println(String.join(",", parsingLine));
				// 파싱한 문자열 컬렉션 데이터 저장/조회
				if(parsingLine.length == 2) {
					// 컬렉션(Map)에 데이터 저장
					hmData.put(parsingLine[0], new FileLineObject(stdinLine));
					// 컬렉션(List)에 데이터 저장
					listData.add(new FileLineObject(parsingLine[0],parsingLine[1]));
				} else if(parsingLine.length == 1 && "EXIT".equalsIgnoreCase(stdinLine)){
					//처리 결과 파일 쓰기
					//3.1 for문 활용 출력
					for(FileLineObject oneObject : listData) {
						printWriterAll.println(oneObject);
					}
					//3.2 stream 활용 출력
					//objList.stream().forEach(o->printWriterAll.println(o));
					
					//printWriterAll.close();
					return;
					
				} else {
					// 조건 조회
					System.out.println(parsingLine[0] + ">" + hmData.get(parsingLine[0]));
					//조회 결과 파일 쓰기
					printWriter.println(hmData.get(parsingLine[0]));
				}
				
			}			
		}
	}
	
	//Stream을 사용해서 파일 라인별 객체 목록 - 모든 클래스에 대응하도록 추상화.(라인 문자열 처리 생성자가 존재해야 함)
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
		//List<String> stream = Files.readAllLines(Paths.get(filePath));
		List<T> objList = stream.map(line->{
			try {
				return objectClass.getConstructor(String.class).newInstance(line);
			} catch (Exception ex) { ex.printStackTrace(); }
			return null;
		}).collect(Collectors.toList());
		stream.close();
		
		return objList;
	}
	
	/**
	 * 파일 전체 내용을 한 문자열로 읽기 #1
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readContentOfFileUsingFilesAllLines(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		 
        String content = null;
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            content = String.join(System.lineSeparator(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(content);
        return content;
	}
	
	/**
	 * 파일 전체 내용을 한 문자열로 읽기 #2
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readContentOfFileUsingFilesAllBytes(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		 
        String content = null;
        try {
        	byte[] encoded = Files.readAllBytes(path);
            content = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(content);
        return content;
	}

}

/**
 * 라인별 객체 클래스 설계
 */
class FileLineObject {

	String id;
	String name;

	public FileLineObject(String parseLine) {
		String[] lineDatas = parseLine.split("#");
		
		id = lineDatas[0];
		name = lineDatas[1];
	}
	
	public FileLineObject(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "FileLineObject [id=" + id + ", name=" + name + "]";
	}
}


/**
 * 라인별 객체 클래스 설계
 */
class FileLineObjectComparable implements Comparable<FileLineObject> {

	String id;
	String name;

	public FileLineObjectComparable(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		
		id = lineDatas[0];
		name = lineDatas[1];
	}
	
	public FileLineObjectComparable(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int compareTo(FileLineObject var1) {
		// 아이디로 오름 차순
		return id.compareTo(var1.id);
	}
}
