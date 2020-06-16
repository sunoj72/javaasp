package com.lgcns.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class RunManager {

public static void main(String[] args) throws Exception {
		
		LinkedHashMap<String, String> a = new LinkedHashMap<String, String>();
		
		//파일 읽기
		Scanner fs = new Scanner(new File("./INFILE/LOCATION.TXT"));
		
		//라인 단위 객체화(특정 문자로 문자 자르기)
		ArrayList<BusLocation> busLocList = new ArrayList<BusLocation>();
		//라인 단위 처리
		String lastLine = null;
		while(fs.hasNextLine()) {
			String fileLine = fs.nextLine();
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


