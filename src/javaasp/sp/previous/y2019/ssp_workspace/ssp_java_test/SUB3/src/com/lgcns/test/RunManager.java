package com.lgcns.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RunManager {

	// 정류장 정보 목록 - 라인 단위 객체화(특정 문자로 문자 자르기) ==> static 변수로 수정
	private static ArrayList<BusStation> stationList = new ArrayList<BusStation>();
	
	public static void main(String[] args) throws Exception {
		
		////////////////////////////////////////////////////////////////////////////////
		//파일 읽기 - 정류장 위치 정보
		Scanner fs = new Scanner(new File("./INFILE/STATION.TXT"));
	
		//라인 단위 객체화(특정 문자로 문자 자르기) ==> static 변수로 수정
//		ArrayList<BusStation> stationList = new ArrayList<BusStation>();
		//라인 단위 처리
		while(fs.hasNextLine()) {
		//문자열 처리 후 데이터 객체화
		String[] lineDatas = fs.nextLine().split("#");
			stationList.add(new BusStation(lineDatas[0], Integer.parseInt(lineDatas[1]), Integer.parseInt(lineDatas[2])));
		}
		////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////////
		//파일 읽기 - Bus 위치 정보
		fs = new Scanner(new File("./INFILE/LOCATION.TXT"));
		
		//라인 단위 객체화(특정 문자로 문자 자르기)
		ArrayList<BusLocation> busLocList = new ArrayList<BusLocation>();
		//라인 단위 처리
		while(fs.hasNextLine()) {
			String fileLine = fs.nextLine();
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
		////////////////////////////////////////////////////////////////////////////////
		
		// 거리별로 정렬
		Collections.sort(busLocList);
		
		//문제1번 : 앞뒤버스 정보 생성
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
		
		//문제2번 : 정류장별 최근접 도착예정차량
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
		
		
		//문제3번 :  정류장도착예정시각 계산
		ArrayList<String> fastestArrivalBusList = new ArrayList<String>();
		for(BusStation oneStation : stationList) {
			BusLocation closetBus = null;
			int fastTimeToArrive = Integer.MAX_VALUE;
			
			// 정류장보다 뒤세 있는 버스 필터링
			List<BusLocation> busListBeforeStation =  busLocList.stream().filter(bus->bus.location<oneStation.location).collect(Collectors.toList());
			for(BusLocation oneBus : busListBeforeStation) {
				int timeToArrive = eslapedTimeToStation(oneBus, oneStation);
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
	
	private static String getTimeAfterSec(String timeString, int seconds) {
		
		String[] timeSp = timeString.split(":");
		int converSec = Integer.parseInt(timeSp[0])*3600+Integer.parseInt(timeSp[1])*60+Integer.parseInt(timeSp[2]) + seconds;
		String convertedSecStr = String.format("%02d", (converSec/3600))+":";
		converSec = converSec%3600;
		convertedSecStr += String.format("%02d", (converSec/60))+":";
		converSec = converSec%60;
		convertedSecStr += String.format("%02d", converSec);
		
		return convertedSecStr;
		
	}
	
	private static int eslapedTimeToStation(BusLocation bus, BusStation station) {
		
		int secondsToGo = 0;
		
		int orgianallyLocation = bus.location;
		while(bus.location<station.location) {
			secondsToGo++;
			
			bus.location += getValidSpeed(bus);
		}
		bus.location = orgianallyLocation;
		
		return secondsToGo;
	}
	
	private static int getValidSpeed(BusLocation preBus) {
		
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

	public BusStation(String name, int location, int limitSpeed) {
		super();
		this.name = name;
		this.location = location;
		this.limitSpeed = limitSpeed*1000/(60*60);
	}

	@Override
	public int compareTo(BusStation var1) {
		return location- var1.location;
	}
}


