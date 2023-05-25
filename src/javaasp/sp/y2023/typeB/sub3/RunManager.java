package com.lgcns.test;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import com.google.gson.Gson;

public class RunManager {

	public static void main(String[] args) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		
		// 1. GET,POST 호출 방식 http://127.0.0.1:8080/queueInfo
		ContentResponse contentRes = httpClient.GET("http://127.0.0.1:8080/queueInfo");
//		System.out.println(contentRes.getContentAsString());
		
		Gson gson = new Gson();
		QueueInfo queueInfo = gson.fromJson(contentRes.getContentAsString(), QueueInfo.class);
		
		int workerSize = queueInfo.inputQueueCount;
		QueueWorkerThread[] qwts = new QueueWorkerThread[workerSize];
		for(int i=0; i<workerSize; i++) {
			QueueWorkerThread qwt = new QueueWorkerThread(i, queueInfo.inputQueueURIs[i], queueInfo.outputQueueURI);
			qwts[i] = qwt;
			qwt.start();
		}
		
		for(QueueWorkerThread qwt : qwts) {
			qwt.join();
		}

	}

}
