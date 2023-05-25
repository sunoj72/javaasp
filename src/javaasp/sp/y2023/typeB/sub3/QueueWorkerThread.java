package com.lgcns.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;

import com.google.gson.Gson;

public class QueueWorkerThread extends Thread {

	private Worker worker;
			
	private String inputQueueURI;
	private String outputQueueURI;
	
	public QueueWorkerThread(int queueNo, String inputQueueURI, String outputQueueURI) {
		this.worker = new Worker(queueNo);
		this.inputQueueURI = inputQueueURI;
		this.outputQueueURI = outputQueueURI;
	}

	@Override
	public void run() {

		try {
			HttpClient httpClient = new HttpClient();
			httpClient.start();
			
			Gson gson = new Gson();
			
			while(true) {
				ContentResponse queueRes = httpClient.GET(inputQueueURI);
//				System.out.println(queueRes.getContentAsString());
				QueueResInfo queueResInfo = gson.fromJson(queueRes.getContentAsString(), QueueResInfo.class);
				
				String runResult = worker.run(queueResInfo.timestamp, queueResInfo.value);
				if( runResult != null) { //{"result":"%s"}
					//System.out.println(String.format("{\"result\":\"%s\"}", runResult));
					String outQueResult = String.format("{\"result\":\"%s\"}", runResult);
					HttpClient httpOutClient = new HttpClient();
					httpOutClient.start();
					ContentResponse outputQueueRes = 
							httpClient.POST(outputQueueURI)
							.content(new StringContentProvider(outQueResult,"utf-8"))
							.send();
				}
			}
			
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
