package com.lgcns.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RunManager {

	// 정류장 정보 목록 - 라인 단위 객체화(특정 문자로 문자 자르기) ==> static 변수로 수정
	private static List<BusStation> stationList = new ArrayList<BusStation>();
	public static List<BusLocation> busLocList = new ArrayList<BusLocation>();
	// 모바일 접속 소켓
	public static boolean mobilePrintAvailableStatus;
	public static Socket mobileSocket;
	public static BufferedReader mobileInputStream;
	public static PrintWriter mobileOutputStream;
	
	// 접속 차량
	public static BusLocation resRequstBus;
	public static boolean resRequstPrintAvailableStatus;
	public static Socket resRequstSocket;
	public static BufferedReader resRequstInputStream;
	public static PrintWriter resRequstOutputStream;
	

	public static void main(String[] args) throws Exception {
		
		//정류장 위치 정보 읽기 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//라인 단위 처리 ==> 4번 문제 : NIO와 Stream을 활용한 객체 생성 함수 활용
		stationList = getObjectListByFileLineWithStream("./INFILE/STATION.TXT", BusStation.class);
				
		//파일 읽기 - 정류장 위치 정보
//		Scanner fs = new Scanner(new File("./INFILE/STATION.TXT"));
	
		//라인 단위 객체화(특정 문자로 문자 자르기) ==> static 변수로 수정
//		ArrayList<BusStation> stationList = new ArrayList<BusStation>();
		//라인 단위 처리 ==> NIO와 Stream을 활용한 객체 생성 함수 활용
//		while(fs.hasNextLine()) {
//			//문자열 처리 후 데이터 객체화
//			String[] lineDatas = fs.nextLine().split("#");
//			stationList.add(new BusStation(lineDatas[0], Integer.parseInt(lineDatas[1]), Integer.parseInt(lineDatas[2])));
//		}
		
		//정류장 위치 정보 읽기 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		//Bus 위치 정보 읽기 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//파일 읽기 - Bus 위치 정보 ==> 4번 문제 : 클라이언트(CLIENT.EXE)로부터 위치정보를 수신
		//4번 문제 : 모바일접속 통신이 제일 마지막에 오는 것은 아님 : 최종 모바일접속의 PRINT명령이 제일 마지막에 오는 것은 확실
		BusLocationReceiverHandler busLocationReceiverHandler = new BusLocationReceiverHandler();
		//4번 문제 : 작업 후 클라이언트(CLIENT.EXE)로부터 위치정보를 수신을 중지? : 문제 평가는 프로그램 종료 없음?? 
//		busLocationReceiverHandler.setDaemon(true);
		busLocationReceiverHandler.start();
		
		//모바일 요청 응답 가능 상태 판단
		while(!mobilePrintAvailableStatus) {
			Thread.sleep(1000);			
		}
		String printCommand = RunManager.mobileInputStream.readLine();
//		System.out.println("Mobile Command : " + printCommand);
		String passengerInfo = mobileInputStream.readLine();

		//접속 차량 응답 가능 상태 판단
		while(!resRequstPrintAvailableStatus) {
			Thread.sleep(1000);			
		}
		
//		int socketcount = 0;
//		BufferedReader mobileInputStream = null;
//		PrintWriter mobileOutputStream = null;
//		try(ServerSocket serverSocket = new ServerSocket(9876)) {
//			while(!isMobileResponse) {
//				Socket client = serverSocket.accept();
//				socketcount++;
//				
//				BufferedReader intStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
//				String busName = intStream.readLine();
//				System.out.println(socketcount + ":" + busName);
//				if("MOBILE".equals(busName)) {
//					mobileInputStream = intStream;
//					mobileOutputStream = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
//				} else {
//					BusLocationReceiver receiver = new BusLocationReceiver(busName, intStream);
//					receiver.start();
//				}
//			}
//		}
		// 거리별로 정렬
		Collections.sort(busLocList);
		//Bus 위치 정보 읽기 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		
		
		//문제1번 : 앞뒤버스 정보 생성 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		//문제1번 : 앞뒤버스 정보 생성 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		
		//문제2번 : 정류장별 최근접 도착예정차량 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		printWriter = new PrintWriter(new File("./OUTFILE/ARRIVAL.TXT"));
		for(String oneArrival : arrivalList) {
			printWriter.println(oneArrival);
		}
		printWriter.close();
		//문제2번 : 정류장별 최근접 도착예정차량 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		
		//문제3번 :  정류장도착예정시각 계산 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
		
		//처리 결과 파일 쓰기 - 외부 프로그램 이용
		transferStationArrivalTime(fastestArrivalBusList);
		//문제3번 :  정류장도착예정시각 계산 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		
		//문제 4번 : 승객도착시간 계산 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		String[] passengerInfos = passengerInfo.split("#");
		int passengerArrivalElapsedTime = getPassengerArriveTime(passengerInfos[0], Integer.parseInt(passengerInfos[1]));
		String passengerArrivalTime = getTimeAfterSec(busLocList.get(0).time,passengerArrivalElapsedTime);
		mobileOutputStream.print(passengerArrivalTime);
		mobileOutputStream.close();
		//문제 4번 : 승객도착시간 계산 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		
		//문제 5번 : 요청 차량과 시간 기준으로 가장 가까운 선/후행차량을 찾고 시간을 계산 시작+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		String fastPreBusInfo = getFastPreBusByTime(resRequstBus);
		String fastPostBusInfo = getFastPostBusByTime(resRequstBus);
		resRequstOutputStream.print(fastPreBusInfo+"#"+fastPostBusInfo);
		resRequstOutputStream.close();
		//문제 5번 : 요청 차량과 시간 기준으로 가장 가까운 선/후행차량을 찾고 시간을 계산 종료+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
	}
	
	private static String getFastPreBusByTime(BusLocation pivotBus) {
		
		BusLocation fastPreBus = null;
		int fastBusTime = Integer.MAX_VALUE;
		for(BusLocation oneBus : busLocList) {
			if(oneBus.location>pivotBus.location) {
				int eslaptedTime = elapsedTimeToStation(oneBus,pivotBus.location);
				if(eslaptedTime<fastBusTime) {
					fastPreBus = oneBus;
					fastBusTime = eslaptedTime;
				}
			}
		}
		if(fastPreBus==null) {
			return "NOBUS#00:00:00";
		}
		
		return fastPreBus.name+"#"+getTimeForSec(fastBusTime);
	}
	
	private static String getFastPostBusByTime(BusLocation pivotBus) {
	
		BusLocation fastPostBus = null;
		int fastBusTime = Integer.MAX_VALUE;
		for(BusLocation oneBus : busLocList) {
			if(oneBus.location<pivotBus.location) {
				int eslaptedTime = elapsedTimeToStation(oneBus,pivotBus.location);
				if(eslaptedTime<fastBusTime) {
					fastPostBus = oneBus;
					fastBusTime = eslaptedTime;
				}
			}
		}
		if(fastPostBus==null) {
			return "NOBUS#00:00:00";
		}
		
		return fastPostBus.name+"#"+getTimeForSec(fastBusTime);
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
	
	/**
	 * 외부 프로그램에 데이터 전송
	 */
	private static void transferStationArrivalTime(ArrayList<String> fastestArrivalBusList) throws IOException {
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
		
//		String[] timeSp = timeString.split(":");
//		int converSec = Integer.parseInt(timeSp[0])*3600+Integer.parseInt(timeSp[1])*60+Integer.parseInt(timeSp[2]) + seconds;
//		String convertedSecStr = String.format("%02d", (converSec/3600))+":";
//		converSec = converSec%3600;
//		convertedSecStr += String.format("%02d", (converSec/60))+":";
//		converSec = converSec%60;
//		convertedSecStr += String.format("%02d", converSec);
//		
//		return convertedSecStr;
		
	}
	
	private static String getTimeForSec(int seconds) {
		
		int converSec = seconds;
		String convertedSecStr = String.format("%02d", (converSec/3600))+":";
		converSec = converSec%3600;
		convertedSecStr += String.format("%02d", (converSec/60))+":";
		converSec = converSec%60;
		convertedSecStr += String.format("%02d", converSec);
		
		return convertedSecStr;
		
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
	
	private static int elapsedTimeToStation(BusLocation bus, int pivotLocation) {
		
		int secondsToGo = 0;
		
		int orgianallyLocation = bus.location;
		while(bus.location<pivotLocation) {
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
	
	//Stream을 사용해서 파일 라인별 객체 목록
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
 * Client와 통신하기 위한 소켓 처리 프로그램
 *
 */
class BusLocationReceiverHandler extends Thread {

	@Override
	public void run() {
		int socketcount = 0;
		try(ServerSocket serverSocket = new ServerSocket(9876)) {
			while(true) {
				Socket client = serverSocket.accept();
				socketcount++;
				
				BufferedReader intStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String busName = intStream.readLine();
				System.out.println(socketcount + ":" + busName);
				if("MOBILE".equals(busName)) {
					RunManager.mobileInputStream = intStream;
					RunManager.mobileOutputStream = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
					RunManager.mobilePrintAvailableStatus = true;
				} else {
					BusLocationReceiver receiver = new BusLocationReceiver(busName, client, intStream);
					receiver.start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/**
 * Client로부터 실제 데이터 받아 처리하는 프로그램
 *
 */
class BusLocationReceiver extends Thread {
	
	private String busName;
	private Socket socket;
	private BufferedReader clientInput;
	
	public BusLocationReceiver(String busName, Socket socket, BufferedReader clientInput) {
		this.busName = busName;
		this.socket = socket;
		this.clientInput = clientInput;
	}

	@Override
	public void run() {
		String line = null;
		BusLocation lastBusLocation = new BusLocation("", "", 0, 0);
		
		try {
			while((line=clientInput.readLine()) != null) {
//				System.out.println(this.busName + "> " + line);
				if("PRINT".equals(line)) {
					RunManager.resRequstBus = lastBusLocation;
					RunManager.resRequstInputStream = clientInput;
					RunManager.resRequstOutputStream = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
					RunManager.resRequstPrintAvailableStatus = true;
					break;
				}
				//문자열 처리 후 데이터 객체화
				String[] lineDatas = line.split("#");
				// 위치정보 정상인 경우 
				if(lineDatas.length > 1 ) {
					int location = Integer.parseInt(lineDatas[1]);
					lastBusLocation = new BusLocation(lineDatas[0], this.busName, location, location-lastBusLocation.location);
				} else { // 손실된 경우
					int validSpeed = RunManager.getValidSpeed(lastBusLocation);
					lastBusLocation = new BusLocation(lineDatas[0], this.busName, lastBusLocation.location+validSpeed, validSpeed);
				}
			}
			
			RunManager.busLocList.add(lastBusLocation);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
