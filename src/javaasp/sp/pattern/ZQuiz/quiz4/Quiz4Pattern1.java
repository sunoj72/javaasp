package javaasp.sp.pattern.ZQuiz.quiz4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 문제2번 패턴 기본
 * - 문제1번 유지 : 문제에서 제시하는 파일에서 라인 단위로 정보를 읽어 특정 데이터 처리 후 결과를 파일에 출력
 * - 문제2번 : 2.1 추가 파일을 주고 라인 단위로 정보를 읽은 후에 문제1번에서 생성한 데이터와 조합해서 동작한 후 데이터 처리 후 결과를 파일에 출력
 * - 문제2번 : 2.2 추가 정보를 외부 데이터에서 읽은 후에 문제1번에서 생성한 데이터와 조합해서 동작한 후 데이터 처리 후 결과를 파일에 출력
 * - 문제3번 : 3.1 추가 정보를 네트워크 통신을 통해 읽고 문제1,2번에서 생성한 데이터와 조합해서 동작한 후 데이터 처리 후 결과를 소켓 클라이언트에 전송 
 *              데이터 처리 로직의 난위도가 급상승
 * - 문제4번 : 4.1 성능 향상을 위한 멀티 실행 환경(Thread)
 * - 문제4번 : 4.2 추가 로직 또는 신규 자바 기술 주제(Reflection... ) 로 로직 처리 
 */
public class Quiz4Pattern1 {

	static final String InputFilePath = "./INPUT/READFILE.TXT";
	static final String OutputFilePath = "./OUTPUT/WRITEFILE.TXT";
	static List<FileLineObject1> obj1List;
	
	static final String InputFilePath2 = "./INPUT/READFILE2.TXT";
	static final String InputExternalProgramPath = "./INPUT/EXTPRO.EXE";
	static final String OutputFilePath2 = "./OUTPUT/WRITEFILE2.TXT";
	static List<LineObject2> obj2List;
	
	private static final int SERVER_PORT = 9876;
	static List<SocketLineObject3> obj3ListFromSocket;
	static final String ExternalJarPath = "./Reflection.jar"; 
	
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
		
		//문제3번 : 소켓을 통해 데이터 전달 받음 - 쓰레드 안에서 처리
		//문제4.1번 : 쓰레드를 활용한 멀티 소켓 처리 - 쓰레드풀 적용
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
			//클라이언트 연결 대기 - blocking
			Socket client = serverSocket.accept();
			//클라이언트 별로 Thead로 처리 작업 할당
			SocketClientHandler clientHandler = new SocketClientHandler(client);
			executorService.submit(clientHandler);
		}
		
		//여기서는 socket.accept(); 에 블러킹이 컬려 여기까지 오지 않음
//		//스레드 종료 시까지 대기 - 앞 실행했던 Thread가 종료 된 후에 main스레드 동작이 있을 때
//		executorService.shutdown();
//		//executorService.awaitTermination(10, TimeUnit.SECONDS);
//		while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
//            System.out.println("Not yet. Still waiting for termination");
//        }
	}

	static void readFromNetworkSocket(Socket client) throws IOException {
		//byte처리를 위한 inputstream/outputstream
		try(InputStream inputStream = client.getInputStream();
				OutputStream outputStream = client.getOutputStream()) {
		
			// stream 처리
//			byte[] inputBytes = new byte[30];
//			//1.1 stream 읽기
//			int byteLength = client.getInputStream().read(inputBytes);
//			System.out.println(new String(Arrays.copyOf(inputBytes, byteLength)));
//			//1.2 stream 쓰기
//			outputStream.write("Output Result".getBytes());
//			outputStream.flush();
			
			obj3ListFromSocket = new ArrayList<SocketLineObject3>();
			///////////////////////////////////////////////////////////////////////////////
			//2.1 문자열(라인) 처리를 위한 inputstream/outputstream
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter outputPrinter = new PrintWriter(new OutputStreamWriter(outputStream), true);
			//2.2 문자열(라인) 읽기
			String line = null;
            while((line=inputReader.readLine()) != null) {
            	obj3ListFromSocket.add(new SocketLineObject3(line));
			}
			//2.3 문자열(라인) 쓰기
			outputPrinter.println("Output Result");
			outputPrinter.flush();
			///////////////////////////////////////////////////////////////////////////////
			
		}
	}
	
	static Object actionWithObjectListCombination2() throws Exception {
		Object actionResult = null;
		// 데이터 처리 액션 - 처리 로직의 난위도가 급상승
		for(FileLineObject1 oneObject1 : obj1List) {
			for(LineObject2 oneObject2 : obj2List) {
				for(SocketLineObject3 oneObject3 : obj3ListFromSocket) {
					ationWithAddedLoginOrTech();
					// actionResult 생성
				}
			}
		}
		
		return actionResult;
	}
	
	//문제4.2번 : 추가 로직 또는 신규 자바 기술 주제(Reflection... ) 로 로직 처리 
	static Object ationWithAddedLoginOrTech() throws Exception {
		//문제4.2번 : 외부 jar파일 클래스 메소스 호출
		File jarFile = new File(ExternalJarPath);
		
		URL classURL = new URL("jar:" + jarFile.toURI().toURL() + "!/");
		URLClassLoader classLoader = new URLClassLoader(new URL[] {classURL});
		
		Class<?> c = classLoader.loadClass("Calculator");
		Constructor<?> constructor = c.getConstructor(new Class[]{});
		Object object = constructor.newInstance(new Object[]{});
		
		Method method = c.getMethod("add", new Class[]{Integer.TYPE, Integer.TYPE});
		Object returnValue = method.invoke(object, 1, 2);
		System.out.println(returnValue);
		
		return returnValue;
	}
	
	static void printResultToSocketClient(Socket client, Object result) throws Exception {
		PrintWriter outputPrinter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
		outputPrinter.println("Output Result");
		outputPrinter.flush();
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

class SocketLineObject3 implements Comparable<SocketLineObject3> {
	
	public SocketLineObject3(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		// ...
	}

	@Override
	public int compareTo(SocketLineObject3 var1) {
		// TODO Auto-generated method stub
		return 0;
	}
}

class SocketClientHandler extends Thread {
	
	Socket client;
	
	public SocketClientHandler(Socket client) {
		this.client = client;		
	}

	@Override
	public void run() {
		try {
			//문제3번 : 소켓에서 데이터 읽기
			Quiz4Pattern1.readFromNetworkSocket(client);
			//문제3번 추가 동작
			Object result3 = Quiz4Pattern1.actionWithObjectListCombination2();
			//문제3번 : 액션을 통해 생성된 데이터를 파일에 출력
			Quiz4Pattern1.printResultToSocketClient(client, result3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} 
