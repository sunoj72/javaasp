package javaasp.sp.pattern.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadHandler {

	public static void main(String[] args) throws InterruptedException {
		//기본 쓰레드 활용
		testBaseThread();
		
		//쓰레드 Pool 활용한 쓰레드 활용
		testBaseThreadWithPool();
	}
	
	public static void testBaseThread() throws InterruptedException {
		
		List<Thread> actionList = new ArrayList<Thread>();
		for(int i=0; i<10; i++) {
			ThreadAction asyncAction = new ThreadAction();
			actionList.add(asyncAction);
			
			asyncAction.start();
		}

		//스레드 종료 시까지 대기 - 앞 실행했던 Thread가 종료 된 후에 main스레드 동작이 있을 때
		for(Thread action : actionList) {
			action.join();
		}

		//메인 스레드 작업
		System.out.println(String.format("[%s] : Main Thread End", Thread.currentThread().getName()));
	}
	
	public static void testBaseThreadWithPool() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		for(int i=0; i<10; i++) {
			ThreadAction asyncAction = new ThreadAction();
			executorService.submit(asyncAction);
		}
		
		//스레드 종료 시까지 대기 - 앞 실행했던 Thread가 종료 된 후에 main스레드 동작이 있을 때
		executorService.shutdown();
		//executorService.awaitTermination(10, TimeUnit.SECONDS);
		while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
		
		//메인 스레드 작업
		System.out.println(String.format("[%s] : Main Thread End With Pool", Thread.currentThread().getName()));
	}

}

class ThreadAction extends Thread {
	
	public ThreadAction() {
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			System.out.println(String.format("[%s] : Action Thread End", Thread.currentThread().getName()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
} 
