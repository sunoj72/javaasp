package com.lgcns.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RunManager {

	//버스 정보
	static List<BusLocation> busLocList = new ArrayList<BusLocation>();
	//정류장 정보
	static List<BusStation> stationList = new ArrayList<BusStation>();
	
	public static void main(String[] args) throws Exception {
		
		//2번문제 : 정류장 정보 - 라인 단위 객체화 - Util함수 사용
		stationList = getObjectListByFileLineWithStream("./INFILE/STATION.TXT", BusStation.class);
		
		//4번 문제//////////////////////////////////////////////
		// 1. 버스 정보를 socket 통신을 통해 받아옴
		// 2. 출력 신호(Mobile socket)시 socket으로 요청 결과(승객 도착 정보) 응답
		// 3. (옵션) Thread Pool 사용
		//////////////////////////////////////////////////////
		ExecutorService executorService = Executors.newFixedThreadPool(10); // Thread Pool 방식
		try(ServerSocket serverSocket = new ServerSocket(9876)) {
			while(true) {
				Socket client = serverSocket.accept();
				BusLocationReceiver receiver = new BusLocationReceiver(client);
				//receiver.start(); // 기본 Thread 방식
				executorService.submit(receiver); // Thread Pool 방식
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 작업 큐에 대기하고 있는 모든 작업을 처리한 뒤에 스레드풀을 종료 - 문제에 종료 없음이기 때문에 아래 호출 X
		//executorService.shutdown();
		
	}

	public static String createPassengerArrivalTimeAndSend(String passengerInfo) throws ParseException {
		String[] passengerInfos = passengerInfo.split("#");
		int passengerArrivalElapsedTime = getPassengerArriveTime(passengerInfos[0], Integer.parseInt(passengerInfos[1]));
		String passengerArrivalTime = getTimeAfterSec(busLocList.get(0).time,passengerArrivalElapsedTime);
		return passengerArrivalTime;
	}
	
	private static int getPassengerArriveTime(String stationName, int passengerLocation) {
		
		BusStation getonStation = null;
		for(int i=0; i<stationList.size(); i++) {
			if(stationList.get(i).location>passengerLocation) {
				getonStation = stationList.get(i);
				break;						
			}
		}
	
		int fastBusTime = Integer.MAX_VALUE;
		for(BusStation oneStation : stationList) {
			if(oneStation.name.equals(stationName)) {
				for(BusLocation oneBus : busLocList) {
					if(oneBus.location<getonStation.location) {
						int eslaptedTime = elapsedTimeToStation(oneBus,oneStation);
						if(eslaptedTime<fastBusTime) {
							fastBusTime = eslaptedTime;
						}
					}
				}
			}
		}
		
		return fastBusTime;
	}
	
	public static void createFastestArrivalBusInfoAndSendToExternalProgram() throws Exception {
		ArrayList<String> fastestArrivalBusList = new ArrayList<String>();
		for(BusStation oneStation : stationList) {
			BusLocation closetBus = null;
			int fastTimeToArrive = Integer.MAX_VALUE;
			
			// 정류장보다 뒤세 있는 버스 필터링
			List<BusLocation> busListBeforeStation =  busLocList.stream().filter(bus->bus.location<oneStation.location).collect(Collectors.toList());
			for(BusLocation oneBus : busListBeforeStation) {
				int timeToArrive = elapsedTimeToStation(oneBus, oneStation);
				if(fastTimeToArrive>timeToArrive) {
					closetBus = oneBus;
					fastTimeToArrive = timeToArrive;
				}
			}
			// 문자열 도착시간과 도착예정 초를 갖고 계산 후 문자열 변경
			String arriveTimeAfterSec = getTimeAfterSec(busLocList.get(0).time, fastTimeToArrive);
			// 도착 정보 문자열 저장
			fastestArrivalBusList.add(busLocList.get(0).time+"#"+oneStation.name+"#"+(closetBus==null?"NOBUS,00:00:00":closetBus.name+","+arriveTimeAfterSec));
		}
		Collections.sort(fastestArrivalBusList);
		
		//외부 프로그램에 데이터 전송
		transferStationArrivalTime(fastestArrivalBusList);
	}
	
	/**
	 * 외부 프로그램에 데이터 전송
	 */
	public static void transferStationArrivalTime(ArrayList<String> fastestArrivalBusList) throws IOException {
		Process theProcess = Runtime.getRuntime().exec("./SIGNAGE.EXE");
		BufferedWriter outStream =new BufferedWriter(new OutputStreamWriter(theProcess.getOutputStream()));
		
		for(String arriveInfo : fastestArrivalBusList) {
			outStream.write(arriveInfo+"\n");			
		}

		outStream.close();
	}
	
	private static String getTimeAfterSec(String timeString, int seconds) throws ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		long aferTime = timeFormat.parse(timeString).getTime()+(seconds*1000);
		return timeFormat.format(new Date(aferTime));
		
	}
	
	private static int elapsedTimeToStation(BusLocation bus, BusStation station) {
		
		int secondsToGo = 0;
		
		int orgianallyLocation = bus.location;
		while(bus.location<station.location) {
			secondsToGo++;
			
			bus.location += getValidSpeed(bus);
		}
		bus.location = orgianallyLocation;
		
		return secondsToGo;
	}
	
	public static int getValidSpeed(BusLocation preBus) {
		
		BusStation lastValidStation = null;
		for(BusStation oneStation : stationList) {
			if(preBus.location > oneStation.location) {
				lastValidStation = oneStation;
			} else {
				break;
			}
		}
		if(lastValidStation==null) {
			return 0;
		}
		
		return preBus.lastSpeed>lastValidStation.limitSpeed?lastValidStation.limitSpeed:preBus.lastSpeed;
		
	}
	
	public static void createArrivalInfoAndWriteFile() throws FileNotFoundException {
		//도착 정보 생성
		ArrayList<String> arrivalList = new ArrayList<String>();
		for(BusStation oneStation : stationList) {
			BusLocation closetBus = null;
			for(BusLocation oneBus : busLocList) {
				if(oneStation.location>oneBus.location) {
					closetBus = oneBus;
					continue;
				}
				break;
			}
			arrivalList.add(busLocList.get(0).time+"#"+oneStation.name+"#"+(closetBus==null?"NOBUS,00000":closetBus.name+","+String.format("%05d", oneStation.location-closetBus.location)));
		}
		
		//처리 결과 파일 쓰기
		PrintWriter printWriter = new PrintWriter(new File("./OUTFILE/ARRIVAL.TXT"));
		for(String oneArrival : arrivalList) {
			printWriter.println(oneArrival);
		}
		printWriter.close();
	}
	
	public static void createBusLocationInfoFromFile(String filePath) throws IOException {
		List<String> fileLineList = getLineListByFileLineWithStream(filePath);
		for(String fileLine : fileLineList) {
			if("PRINT".equals(fileLine)) {
				break;
			}
			
			//문자열 처리 후 데이터 객체화
			String[] lineDatas = fileLine.split("#");
			// 위치정보 정상인 경우 
			if(lineDatas.length > 1 ) {
				int busLocIndex = 0;
				for(int i=1; i<lineDatas.length; i++) {
					String[] busLocDatas = lineDatas[i].split(",");
					
					if(busLocList.size() < lineDatas.length-1) {
						busLocList.add(new BusLocation(lineDatas[0], busLocDatas[0], Integer.parseInt(busLocDatas[1]),0));
						
					} else {
						int lastSpeed = Integer.parseInt(busLocDatas[1]) - busLocList.get(busLocIndex).location;
						
						busLocList.get(busLocIndex).time = lineDatas[0];
						busLocList.get(busLocIndex).name =  busLocDatas[0];
						busLocList.get(busLocIndex).location = Integer.parseInt(busLocDatas[1]);
						busLocList.get(busLocIndex).lastSpeed = lastSpeed;
						
						busLocIndex++;
					}					
				}
			} else { // 위치정보 손실이 발생하는 경우 :  위치정보 손실을 고려한 최종 위치 계산
				ArrayList<BusLocation> newBusLocList = new ArrayList<BusLocation>();
				for(BusLocation preBus : busLocList) {
					int validSpeed = getValidSpeed(preBus);
					newBusLocList.add(new BusLocation(lineDatas[0], preBus.name, preBus.location+validSpeed, preBus.lastSpeed));
				}
				busLocList = newBusLocList;
			}
		}
		// 거리별로 정렬
		Collections.sort(busLocList);
	}
	
	public static void createPrePostBusInfoAndPrint() throws FileNotFoundException {
		//앞뒤버스 정보 생성
		ArrayList<String> prePostList = new ArrayList<String>();
		prePostList.add(busLocList.get(0).time
				+"#"+busLocList.get(0).name
				+"#"+busLocList.get(1).name+","+String.format("%05d", busLocList.get(1).location-busLocList.get(0).location)
				+"#NOBUS,00000");
		int i=1;
		for(; i<busLocList.size()-1; i++) {
			prePostList.add(busLocList.get(i).time
					+"#"+busLocList.get(i).name
					+"#"+busLocList.get(i+1).name+","+String.format("%05d", busLocList.get(i+1).location-busLocList.get(i).location)
					+"#"+busLocList.get(i-1).name+","+String.format("%05d", busLocList.get(i).location-busLocList.get(i-1).location)
					);
		}
		prePostList.add(busLocList.get(i).time
				+"#"+busLocList.get(i).name
				+"#NOBUS,00000"
				+"#"+busLocList.get(i-1).name+","+String.format("%05d", busLocList.get(i).location-busLocList.get(i-1).location)
				);
		//버스번호 정렬
		Collections.sort(prePostList);

		//처리 결과 파일 쓰기
		PrintWriter printWriter = new PrintWriter(new File("./OUTFILE/PREPOST.TXT"));
		for(String onePrePost : prePostList) {
			printWriter.println(onePrePost);
		}
		printWriter.close();
	}
	
	// Util - Stream을 사용해서 파일 라인 목록 가져오기
	public static List<String> getLineListByFileLineWithStream(String filePath) throws IOException {
		List<String> lineList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			lineList = stream.collect(Collectors.toList());
		}
		
		return lineList;
	}
	
	// Util - Stream을 사용해서 파일 라인별 객체 목록 - 모든 클래스에 대응하도록 추상화
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

}

class BusLocation implements Comparable<BusLocation> {

	String time;
	int timeSeconds;
	String name;
	int location;
	int lastSpeed;

	public BusLocation(String time, String name, int location, int lastSpeed) {
		super();
		this.time = time;
		this.name = name;
		this.location = location;
		this.lastSpeed = lastSpeed;
	}

	@Override
	public int compareTo(BusLocation var1) {
		return location- var1.location;
	}
}

class BusStation implements Comparable<BusStation> {

	String name;
	int location;
	int limitSpeed;

	public BusStation(String fileLine) {
		String[] lineDatas = fileLine.split("#");
		this.name = lineDatas[0];
		this.location = Integer.parseInt(lineDatas[1]);
		this.limitSpeed = Integer.parseInt(lineDatas[2])*1000/(60*60);
	}
	
	public BusStation(String name, int location, int limitSpeed) {
		this.name = name;
		this.location = location;
		this.limitSpeed = limitSpeed*1000/(60*60);
	}

	@Override
	public int compareTo(BusStation var1) {
		return location- var1.location;
	}
}


/**
 * Client로부터 실제 데이터 받아 처리하는 프로그램
 *
 */
class BusLocationReceiver extends Thread {
	
	private Socket client;

	public BusLocationReceiver(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		BusLocation lastBusLocation = new BusLocation("", "", 0, 0);
		try {
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String busName = clientInput.readLine();
			System.out.println("Client Connected : " + busName);
			if("MOBILE".equals(busName)) {
				String printCommand = clientInput.readLine();
				String passengerInfo = clientInput.readLine();
				String passengerArriavlTime = RunManager.createPassengerArrivalTimeAndSend(passengerInfo);
				PrintWriter mobileOutput = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
				mobileOutput.print(passengerArriavlTime);
				mobileOutput.close();
				
				// 거리별로 정렬
				Collections.sort(RunManager.busLocList);
				//1번문제 : 출력
				RunManager.createPrePostBusInfoAndPrint();
				//2번문제 : 출력
				RunManager.createArrivalInfoAndWriteFile();
				//문제3번 :  정류장도착예정시각 계산 후 외부 프로그램에게 처리 결과 전송
				RunManager.createFastestArrivalBusInfoAndSendToExternalProgram();
				
			} else {
				String line = null;
				while((line=clientInput.readLine()) != null) {
					//문자열 처리 후 데이터 객체화
					String[] lineDatas = line.split("#");
					// 위치정보 정상인 경우 
					if(lineDatas.length > 1 ) {
						int location = Integer.parseInt(lineDatas[1]);
						lastBusLocation = new BusLocation(lineDatas[0], busName, location, location-lastBusLocation.location);
					} else { // 손실된 경우
						int validSpeed = RunManager.getValidSpeed(lastBusLocation);
						lastBusLocation = new BusLocation(lineDatas[0], busName, lastBusLocation.location+validSpeed, validSpeed);
					}
				}
				RunManager.busLocList.add(lastBusLocation);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
