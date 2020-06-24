package javaasp.sp.pattern.externalprocess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class ExternalProcess {
	
	public static void main(String[] args) throws Exception {
		List<String> lineDatas = ExternalProcess.readFromExternalProcess("tasklist");
		
		//TODO : 데이터 처리
		
		ExternalProcess.writeToExternalProcess("./writeprogram.exe", lineDatas);
	}
	
	
	public static List<String> readFromExternalProcess(String command) throws Exception {
		Process theProcess = Runtime.getRuntime().exec(command);
	    BufferedReader inStream = new BufferedReader(new InputStreamReader( theProcess.getInputStream(),"euc-kr"));
	    
	    List<String> readData = new ArrayList<String>();
	    String line = null;
	    while ( ( line = inStream.readLine( ) ) != null ) {
	    	readData.add(line);
	    }
	    
	    return readData;		
	}
	
	public static void writeToExternalProcess(String command, List<String> writeData) throws Exception {
		Process theProcess = Runtime.getRuntime().exec(command);
		PrintWriter printWriter = new PrintWriter(theProcess.getOutputStream(), true);
		//BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(theProcess.getOutputStream(),"euc-kr"));
	    
		for(String oneData : writeData) {
			printWriter.println(oneData);
		}
	    
		printWriter.close();	
	}
}
