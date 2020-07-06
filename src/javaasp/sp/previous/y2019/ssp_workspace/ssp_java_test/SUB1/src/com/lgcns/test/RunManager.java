package com.lgcns.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunManager {
	
	//버스 정보
	static ArrayList<BusLocation> busLocList = new ArrayList<BusLocation>();

	public static void main(String[] args) throws Exception {
		
//		//파일 읽기
//		Scanner fs = new Scanner(new File("./INFILE/LOCATION.TXT"));
//		
//		//라인 단위 객체화(특정 문자로 문자 자르기)
//		ArrayList<BusLocation> busLocList = new ArrayList<BusLocation>();
//		//라인 단위 처리
//		String lastLine = null;
//		while(fs.hasNextLine()) {
//			String fileLine = fs.nextLine();
//			if(!"PRINT".equals(fileLine)) {
//				lastLine = fileLine;
//				continue;
//			}
//			
//			//문자열 처리 후 데이터 객체화
//			String[] lineDatas = lastLine.split("#");
//			for(int i=1; i<lineDatas.length; i++) {
//				String[] busLocDatas = lineDatas[i].split(",");
//				busLocList.add(new BusLocation(lineDatas[0], busLocDatas[0], Integer.parseInt(busLocDatas[1])));
//			}
//		}
//		// 거리별로 정렬
//		Collections.sort(busLocList);
		
//		//앞뒤버스 정보 생성
//		ArrayList<String> prePostList = new ArrayList<String>();
//		prePostList.add(busLocList.get(0).time
//				+"#"+busLocList.get(0).name
//				+"#"+busLocList.get(1).name+","+String.format("%05d", busLocList.get(1).location-busLocList.get(0).location)
//				+"#NOBUS,00000");
//		int i=1;
//		for(; i<busLocList.size()-1; i++) {
//			prePostList.add(busLocList.get(i).time
//					+"#"+busLocList.get(i).name
//					+"#"+busLocList.get(i+1).name+","+String.format("%05d", busLocList.get(i+1).location-busLocList.get(i).location)
//					+"#"+busLocList.get(i-1).name+","+String.format("%05d", busLocList.get(i).location-busLocList.get(i-1).location)
//					);
//		}
//		prePostList.add(busLocList.get(i).time
//				+"#"+busLocList.get(i).name
//				+"#NOBUS,00000"
//				+"#"+busLocList.get(i-1).name+","+String.format("%05d", busLocList.get(i).location-busLocList.get(i-1).location)
//				);
//		//버스번호 정렬
//		Collections.sort(prePostList);
//
//		//처리 결과 파일 쓰기
//		PrintWriter printWriter = new PrintWriter(new File("./OUTFILE/PREPOST.TXT"));
//		for(String onePrePost : prePostList) {
//			printWriter.println(onePrePost);
//		}
//		printWriter.close();
		
		//////////////////////////////////////////////////////
		// 1. 사용 변수는 static 변수로 변경해서 어디서든 접근이 가능하도록 수정
		// 2. 각 기능별로 모듈(함수)화 해서 처리(함수도 static 함수로 어디서든 접근 가능하도록)
		//////////////////////////////////////////////////////
		//라인 단위 객체화(특정 문자로 문자 자르기)
		createBusLocationInfoFromFile("./INFILE/LOCATION.TXT");
		//앞뒤버스 정보 생성
		createArrivalInfoAndWriteFile();
	}
	
	
	public static void createBusLocationInfoFromFile(String filePath) throws IOException {
		String lastLine = null;
		List<String> fileLineList = getLineListByFileLineWithStream(filePath);
		for(String fileLine : fileLineList) {
			if(!"PRINT".equals(fileLine)) {
				lastLine = fileLine;
				continue;
			}
			
			//문자열 처리 후 데이터 객체화
			String[] lineDatas = lastLine.split("#");
			for(int i=1; i<lineDatas.length; i++) {
				String[] busLocDatas = lineDatas[i].split(",");
				busLocList.add(new BusLocation(lineDatas[0], busLocDatas[0], Integer.parseInt(busLocDatas[1])));
			}
		}
		// 거리별로 정렬
		Collections.sort(busLocList);
	}
	
	public static void createArrivalInfoAndWriteFile() throws FileNotFoundException {
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
	
	//Stream을 사용해서 파일 라인 목록 가져오기
	public static List<String> getLineListByFileLineWithStream(String filePath) throws IOException {
		List<String> lineList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			lineList = stream.collect(Collectors.toList());
		}
		
		return lineList;
	}
	
	//Stream을 사용해서 파일 라인별 객체 목록 - 모든 클래스에 대응하도록 추상화.(전
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
	String name;
	int location;
	
	public BusLocation(String time, String name, int location) {
		super();
		this.time = time;
		this.name = name;
		this.location = location;
	}

	@Override
	public int compareTo(BusLocation var1) {
		// 아이디로 오름 차순
		return location- var1.location;
	}
}


