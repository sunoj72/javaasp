package javaasp.sp.y2021.quiz5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


public class Quiz5Servlet extends HttpServlet {
	
	private static final long serialVersionUID = -8840052554088651691L;
	
	private static String RESPONSE_RESULT_OK = "{\"Result\":\"OK\"}";
	private static String RESPONSE_MESSAGE_FORMAT = "{\"Result\":\"Ok\",\"MessageID\":\"%s\",\"Message\":\"%s\"}";
	
	private MessageService messageService = new MessageService();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitUri = requestUri.split("/");
		
		messageService.handlingProcessTimeout();
		
		if("RECEIVE".equals(splitUri[0])) {
			Message msg = messageService.receiveMessage(splitUri[1]);
			if(msg != null) {
				if("3".equals(msg.getStatus())) {
					resp.getWriter().write("{\"Result\":\"Service Unavailable\"}");
				} else {
					resp.getWriter().write(String.format(RESPONSE_MESSAGE_FORMAT, msg.getMessageId(), msg.getMessage()));
				}
			} else {
				resp.getWriter().write("{\"Result\":\"No Message\"}");
			}
		} else if("DLQ".equals(splitUri[0])) {
			Message msg = messageService.deadMessage(splitUri[1]);
			if(msg != null) {
				resp.getWriter().write(String.format("{\"Result\":\"Ok\",\"MessageID\":\"%s\",\"Message\":\"%s\"}", msg.getMessageId(), msg.getMessage()));
			} else {
				resp.getWriter().write("{\"Result\":\"No Message\"}");
			}
		}
		
		resp.setStatus(200);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitUri = requestUri.split("/");
		
		String bodyJson = getJsonFromReqeustBody(req);
//		System.out.println(bodyJson);
		
		resp.setStatus(200);
		
		messageService.handlingProcessTimeout();
		
		if("CREATE".equals(splitUri[0])) {
			
			Gson gson = new Gson();
			Map<String,Object> map = new HashMap<String,Object>();
			map = (Map<String,Object>) gson.fromJson(bodyJson, map.getClass());
			QueueFeature qf = 
					new QueueFeature(((Double)map.get("QueueSize")).intValue(), 
							((Double)map.get("ProcessTimeout")).intValue(), 
							((Double)map.get("MaxFailCount")).intValue(), 
							((Double)map.get("WaitTime")).intValue());
			
			boolean created = messageService.createMessageQueue(splitUri[1], qf);
			if(created) {
				resp.getWriter().write(RESPONSE_RESULT_OK);
			} else {
				resp.getWriter().write("{\"Result\":\"Queue Exist\"}");
			}

		} else if("SEND".equals(splitUri[0])) {

			Gson gson = new Gson();
			Map<String,Object> map = new HashMap<String,Object>();
			map = (Map<String,Object>) gson.fromJson(bodyJson, map.getClass());
			String sendMessage = map.get("Message").toString();
			
			boolean sended = messageService.sendMessage(splitUri[1], sendMessage);
			if(sended) {
				resp.getWriter().write(RESPONSE_RESULT_OK);
			} else {
				resp.getWriter().write("{\"Result\":\"Queue Full\"}");
			}
			
		} else if("ACK".equals(splitUri[0])) {
			messageService.ackMessage(splitUri[1],splitUri[2]);
			resp.getWriter().write(RESPONSE_RESULT_OK);
		} else if("FAIL".equals(splitUri[0])) {
			messageService.failMessage(splitUri[1],splitUri[2]);
			resp.getWriter().write(RESPONSE_RESULT_OK);
		} else if("SHUTDOWN".equals(splitUri[0])) {	
			messageService.shutdownMessage();
			resp.getWriter().write(RESPONSE_RESULT_OK);
			// TODO : close호출해야 컨슈머에서 정상 완료 >> respose응답하는 부분을 다 close호출할 수 있게 공통메소드로 만들필요 있음
			resp.getWriter().close();
			
			//시스템 종료
			System.exit(0);
			
		}
		
		//resp.setStatus(200);
	}
		
	public static String getJsonFromReqeustBody(HttpServletRequest request) throws IOException {
		 
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
 
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
 
        body = stringBuilder.toString();
        return body;
    }
	
}


class MessageService {

	//Message DB
	private static MessageDB messageDB = MessageDB.getInstance();
	private static volatile boolean shutdownStatus = false;
	
//	private static CheckProcessTimeout checkProcessTimeout = new CheckProcessTimeout(queues, queueFeatures, deadQueues);
//
	static {
		System.out.println("MessageService Start : Restore MessageDB");
		loadMessageDB();
//		checkProcessTimeout.start();
	}
	
	public boolean createMessageQueue(String queueName, QueueFeature queueFeature) {
		
		boolean isCreated = false;
		
		if(!messageDB.getQueues().containsKey(queueName)) {
			ArrayList<Message> newQueue = new ArrayList<>();
			messageDB.getQueues().put(queueName, newQueue);
			messageDB.getQueueFeatures().put(queueName, queueFeature);
			
			ArrayList<Message> newDeadQueue = new ArrayList<>();
			messageDB.getDeadQueues().put(queueName, newDeadQueue);
			
			isCreated = true;
		}
		
		return isCreated;
	}

	public boolean sendMessage(String queueName, String message) {
		boolean isSended = true;
		
		if(messageDB.getQueues().get(queueName).size() == messageDB.getQueueFeatures().get(queueName).getQueueSize()) {
			isSended = false;
		} else {
			messageDB.getQueues().get(queueName).add(new Message(UUID.randomUUID().toString(), message, "1"));
		}
		
		return isSended;
	}

	public Message receiveMessage(String queueName) {		
		return receiveMessage(queueName, true, 0);
	}
	
	public Message receiveMessage(String queueName, boolean wait, int waitingTime) {
		//수신대기 중에도 ProcessTimeout는 이루어져야 함.
		handlingProcessTimeout();
		
		ArrayList<Message> qm = messageDB.getQueues().get(queueName);
		if(qm != null) {
			Iterator<Message> qmi = qm.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();				
				if("1".equals(oneMsg.getStatus())) {
					oneMsg.setStatus("2");
					oneMsg.setReceiveTime(System.currentTimeMillis());
					return oneMsg;
				}
			}
		}
		
		// message 수신 대기
		if(wait && messageDB.getQueueFeatures().get(queueName).getWaitTime()>0) {
			if(shutdownStatus) {
				return new Message("shutdown", "shutdown", "3");
			}
			
			int interval = 100;
			int maxWaitingTime = messageDB.getQueueFeatures().get(queueName).getWaitTime() * 1000;
			int totalWaitingTime = waitingTime+interval;
			boolean stillWait =  totalWaitingTime < maxWaitingTime;
			try {
				// MOCK.EXE가 없는 메시지를 달라고 하네..??
				// MOCK.EXE 출력 : 테스트 실패! : 요청 : RECEIVE/PLAY      서버가 응답하지 않거나 응답이 느립니다. 요구되는 응답시간 : 2.2 초 이내
				// 그래서 아래 임시로 테스트 데이터 전송해봤더니.. 없는 메시지가 와야 한다네.ㅠㅠ
				// MOCK.EXE 출력 : 테스트 실패! : 응답 Message 값이 일치하지 않습니다. Expected Result : 'Message #100', but Yours : 'temp'
//				stillWait =  totalWaitingTime<1500;
//				if(!stillWait ) {
//					return new Message("aaa", "temp", "2");
//				}
				Thread.sleep(interval);
				
				return receiveMessage(queueName, stillWait, totalWaitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public void ackMessage(String queueName, String messageId) {
		ArrayList<Message> qm = messageDB.getQueues().get(queueName);
		if(qm != null) {
			Iterator<Message> qmi = qm.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();
				if(messageId.equals(oneMsg.getMessageId())) {
					qmi.remove();
				}
			}
		}
	}
	
	public void failMessage(String queueName, String messageId) {
		ArrayList<Message> qm = messageDB.getQueues().get(queueName);
		if(qm != null) {
			Iterator<Message> qmi = qm.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();
				if(messageId.equals(oneMsg.getMessageId())) {
					if(messageDB.getQueueFeatures().get(queueName).getMaxFailCount() <= oneMsg.getReceiveFailCount()) {
						messageDB.getDeadQueues().get(queueName).add(oneMsg);
						qmi.remove();
					} else {
						oneMsg.setStatus("1");
						oneMsg.setReceiveTime(0);
						oneMsg.setReceiveFailCount(oneMsg.getReceiveFailCount()+1);
					}
					return;
				}
			}
		}
	}
	
	public Message deadMessage(String queueName) {
		Message returnDaedMsg = null;
			
		ArrayList<Message> dqm = messageDB.getDeadQueues().get(queueName);
		if(dqm != null && dqm.size()>0) {
			returnDaedMsg = dqm.get(0);
			dqm.remove(0);
		}
		
		return returnDaedMsg;
	}
	
	public synchronized void handlingProcessTimeout() {
		messageDB.getQueues().forEach((queueName, queue)-> {
			if(messageDB.getQueueFeatures().get(queueName).getProcessTimeout()>0) {			
				Iterator<Message> qmi = queue.iterator();
				while(qmi.hasNext()) {
					Message oneMsg = qmi.next();
					
					long expireTime = oneMsg.getReceiveTime()+messageDB.getQueueFeatures().get(queueName).getProcessTimeout()*1000;
					long ct = System.currentTimeMillis();
					if("2".equals(oneMsg.getStatus()) && oneMsg.getReceiveTime()>0 && expireTime < ct) {
						
						if(messageDB.getQueueFeatures().get(queueName).getMaxFailCount() == oneMsg.getReceiveFailCount()) {
							messageDB.getDeadQueues().get(queueName).add(oneMsg);
							qmi.remove();
						} else {
							oneMsg.setStatus("1");
							oneMsg.setReceiveTime(0);
							oneMsg.setReceiveFailCount(oneMsg.getReceiveFailCount()+1);
						}
					}
				}
			}
		});
		
	}
	
	public void shutdownMessage() {
		shutdownStatus = true;
		
		//수신한 모든 메시지 완료 간주해서 Queue에서 삭제
		messageDB.getQueues().forEach((queueName, queue)-> {		
			Iterator<Message> qmi = queue.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();
				if("2".equals(oneMsg.getStatus())) {
					qmi.remove(); 
				}
			}
		});
		
		saveMessageDB();
	}
	
	private void saveMessageDB() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
        	
            fos = new FileOutputStream("./msgdb.ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(messageDB);
            
            System.out.println("saveMessageDB : " + messageDB);
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {oos.close();} catch (Exception e) { e.printStackTrace();}
            }
            if (fos != null) {
                try {fos.close();} catch (Exception e) {e.printStackTrace();
                }
            }
        }
    }
	
	private static void loadMessageDB() {
		FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
        	File serfile = new File("./msgdb.ser");
        	if(serfile.exists()) {
	            fis = new FileInputStream(serfile);
	            ois = new ObjectInputStream(fis);
	            Object obj = ois.readObject();
	            MessageDB deseMsgDb = (MessageDB)obj;
	            messageDB = deseMsgDb;
	            
	            shutdownStatus = false;
	            
	            serfile.delete();
	            
	            System.out.println("loadMessageDB : " + messageDB);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {ois.close();} catch (Exception e) {e.printStackTrace();}}
        }
        if (fis != null) {
            try {fis.close();} catch (Exception e) {e.printStackTrace();}
        }
    }
	
}

/** 
 * 사용하지 않음
 */
//class CheckProcessTimeout extends Thread {
//
//	private ConcurrentHashMap<String,  ArrayList<Message>> queues;
//	private ConcurrentHashMap<String,  QueueFeature> queueFeatures;
//	
//	private ConcurrentHashMap<String,  ArrayList<Message>> deadQueues;
//	
//	public CheckProcessTimeout(ConcurrentHashMap<String, ArrayList<Message>> queues,
//			ConcurrentHashMap<String,  QueueFeature> queueFeatures,
//			ConcurrentHashMap<String, ArrayList<Message>> deadQueues) {
//		super();
//		this.queues = queues;
//		this.queueFeatures = queueFeatures;
//		this.deadQueues = deadQueues;
//	}
//	
//	@Override
//	public void run() {
//		while(true) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {e.printStackTrace(); }
//		
//			checkProcessTimeoutHandling();
//		}
//	}
//	
//	private void checkProcessTimeoutHandling() {
//		queues.forEach((queueName, queue)-> {					
//			if(queueFeatures.get(queueName).getProcessTimeout()>0) {
//				Iterator<Message> qmi = queue.iterator();
//				while(qmi.hasNext()) {
//					Message oneMsg = qmi.next();
//					
//					long expireTime = oneMsg.getReceiveTime()+queueFeatures.get(queueName).getProcessTimeout()*1000;
//					long ct = System.currentTimeMillis();
//					if("2".equals(oneMsg.getStatus()) && oneMsg.getReceiveTime()>0 && expireTime < ct) {						
//						if(queueFeatures.get(queueName).getMaxFailCount() < oneMsg.getReceiveFailCount()) {
//							deadQueues.get(queueName).add(oneMsg);
//							qmi.remove();
//						} else {
//							oneMsg.setStatus("1");
//							oneMsg.setReceiveTime(0);
//							oneMsg.setReceiveFailCount(oneMsg.getReceiveFailCount()+1);
//						}
//						return;
//					}
//				}
//			}
//		});
//	}
//	
//}


/**
 * MVC - Model
 */
class MessageDB implements Serializable {

	private static final long serialVersionUID = -1769720408478449239L;
	
	private ConcurrentHashMap<String,  ArrayList<Message>> queues = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String,  QueueFeature> queueFeatures = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String,  ArrayList<Message>> deadQueues = new ConcurrentHashMap<>();
	
	private MessageDB() {}
	
	private static class MessageDBLazyHolder  {
		private static final MessageDB INSTANCE = new MessageDB();
	}
	
	public static MessageDB getInstance() {
		return MessageDBLazyHolder.INSTANCE;
	}
	
	public ConcurrentHashMap<String, ArrayList<Message>> getQueues() {
		return queues;
	}

	public void setQueues(ConcurrentHashMap<String, ArrayList<Message>> queues) {
		this.queues = queues;
	}

	public ConcurrentHashMap<String, QueueFeature> getQueueFeatures() {
		return queueFeatures;
	}

	public void setQueueFeatures(ConcurrentHashMap<String, QueueFeature> queueFeatures) {
		this.queueFeatures = queueFeatures;
	}

	public ConcurrentHashMap<String, ArrayList<Message>> getDeadQueues() {
		return deadQueues;
	}

	public void setDeadQueues(ConcurrentHashMap<String, ArrayList<Message>> deadQueues) {
		this.deadQueues = deadQueues;
	}

	@Override
	public String toString() {
		return "MessageDB [queues=" + queues + ", queueFeatures=" + queueFeatures + ", deadQueues=" + deadQueues + "]";
	}
	
}

class Message implements Serializable {
	
	private static final long serialVersionUID = 4990731653242190882L;
	
	private String messageId;
	private String message;
	/**
	 * 1 : send, 2 : receive. 3 : shutdown
	 */
	private String status;
	private long receiveTime;
	private int receiveFailCount;

	public Message(String messageId, String message, String status) {
		this.messageId = messageId;
		this.message = message;
		this.status = status;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}
	public int getReceiveFailCount() {
		return receiveFailCount;
	}
	public void setReceiveFailCount(int receiveFailCount) {
		this.receiveFailCount = receiveFailCount;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", message=" + message + ", status=" + status + ", receiveTime="
				+ receiveTime + ", receiveFailCount=" + receiveFailCount + "]";
	}

}

class QueueFeature implements Serializable {
	
	private static final long serialVersionUID = 1940577136805617170L;
	
	private int queueSize;
	private int processTimeout;
	private int maxFailCount;
	private int waitTime;
	
	public QueueFeature(int queueSize, int processTimeout, int maxFailCount, int waitTime) {
		super();
		this.queueSize = queueSize;
		this.processTimeout = processTimeout;
		this.maxFailCount = maxFailCount;
		this.waitTime = waitTime;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public int getProcessTimeout() {
		return processTimeout;
	}

	public void setProcessTimeout(int processTimeout) {
		this.processTimeout = processTimeout;
	}

	public int getMaxFailCount() {
		return maxFailCount;
	}

	public void setMaxFailCount(int maxFailCount) {
		this.maxFailCount = maxFailCount;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}
}
