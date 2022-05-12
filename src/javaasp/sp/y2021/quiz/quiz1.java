package javaasp.sp.y2021.quiz;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class quiz1 {

	public static ArrayList<String> queue = new ArrayList();
	
	public static void main1(String[] args) {
		Scanner fs = new Scanner(System.in);
		
		while(fs.hasNextLine()) {
			String userinputLine = fs.nextLine();
			
			String[] splitLine = userinputLine.split(" ");
			if("SEND".equals(splitLine[0])) {
				queue.add(splitLine[1]);
			} else if("RECEIVE".equals(splitLine[0])) {
				if(queue.size()>0) {
					System.out.println(queue.get(0));
					queue.remove(0);
				}
			}
		}

	}
	
	public static void main(String[] args) {
		Scanner fs = new Scanner(System.in);
		
		while(fs.hasNextLine()) {
			String userinputLine = fs.nextLine();
			
			StringTokenizer tokens = new StringTokenizer(userinputLine);
			
			String command = tokens.nextToken();
			if("SEND".equals(command)) {
				queue.add(tokens.nextToken());
			} else if("RECEIVE".equals(command)) {
				if(queue.size()>0) {
					System.out.println(queue.get(0));
					queue.remove(0);
				}
			}
		}

	}

}
