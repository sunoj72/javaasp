package javaasp.sp.y2021.quiz4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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


public class Quiz4Servlet extends HttpServlet {
	
	private static final long serialVersionUID = -8840052554088651691L;
	
	private static String RESPONSE_RESULT_OK = "{\"Result\":\"OK\"}";
	private static String RESPONSE_MESSAGE_FORMAT = "{\"Result\":\"Ok\",\"MessageID\":\"%s\",\"Message\":\"%s\"}";
	
	private MessageService messageService = new MessageService();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitUri = requestUri.split("/");
		
		if("RECEIVE".equals(splitUri[0])) {
			Message msg = messageService.receiveMessage(splitUri[1]);
			if(msg != null) {
				resp.getWriter().write(String.format(RESPONSE_MESSAGE_FORMAT, msg.getMessageId(), msg.getMessage()));
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
		String[] splitLine = requestUri.split("/");
		
		String bodyJson = getJsonFromReqeustBody(req);
//		System.out.println(bodyJson);
		
		if("CREATE".equals(splitLine[0])) {
			
			Gson gson = new Gson();
			Map<String,Object> map = new HashMap<String,Object>();
			map = (Map<String,Object>) gson.fromJson(bodyJson, map.getClass());
			QueueFeature qf = 
					new QueueFeature(((Double)map.get("QueueSize")).intValue(), 
							((Double)map.get("ProcessTimeout")).intValue(), 
							((Double)map.get("MaxFailCount")).intValue(), 
							((Double)map.get("WaitTime")).intValue());
			
			boolean created = messageService.createMessageQueue(splitLine[1], qf);
			if(created) {
				resp.getWriter().write(RESPONSE_RESULT_OK);
			} else {
				resp.getWriter().write("{\"Result\":\"Queue Exist\"}");
			}

		} else if("SEND".equals(splitLine[0])) {
			
			Gson gson = new Gson();
			Map<String,Object> map = new HashMap<String,Object>();
			map = (Map<String,Object>) gson.fromJson(bodyJson, map.getClass());
			String sendMessage = map.get("Message").toString();
			
			boolean sended = messageService.sendMessage(splitLine[1], sendMessage);
			if(sended) {
				resp.getWriter().write(RESPONSE_RESULT_OK);
			} else {
				resp.getWriter().write("{\"Result\":\"Queue Full\"}");
			}
			
		} else if("ACK".equals(splitLine[0])) {
			messageService.ackMessage(splitLine[1],splitLine[2]);
			resp.getWriter().write(RESPONSE_RESULT_OK);
		} else if("FAIL".equals(splitLine[0])) {
			messageService.failMessage(splitLine[1],splitLine[2]);
			resp.getWriter().write(RESPONSE_RESULT_OK);
		}
		
		resp.setStatus(200);
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

	public static ConcurrentHashMap<String,  ArrayList<Message>> queues = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String,  QueueFeature> queueFeatures = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String,  ArrayList<Message>> deadQueues = new ConcurrentHashMap<>();
	
	private static CheckProcessTimeout checkProcessTimeout = new CheckProcessTimeout(queues, queueFeatures, deadQueues);
	
	static {
		checkProcessTimeout.start();
	}
	
	public boolean createMessageQueue(String queueName, QueueFeature queueFeature) {
		
		boolean isCreated = false;
		
		if(!queues.containsKey(queueName)) {
			ArrayList<Message> newQueue = new ArrayList<>();
			queues.put(queueName, newQueue);
			queueFeatures.put(queueName, queueFeature);
			
			ArrayList<Message> newDeadQueue = new ArrayList<>();
			deadQueues.put(queueName, newDeadQueue);
			
			isCreated = true;
		}
		
		return isCreated;
	}
	
	public void printQueueForDebugging() {
		System.out.println("PrintQueue [queues=" + queues + ", deadQueues=" + deadQueues + "]");
		
	}

	public boolean sendMessage(String queueName, String message) {
		boolean isSended = true;
		
		if(queues.get(queueName).size() == queueFeatures.get(queueName).getQueueSize()) {
			isSended = false;
		} else {
			queues.get(queueName).add(new Message(UUID.randomUUID().toString(), message, "1"));
		}
		
		return isSended;
	}

	public Message receiveMessage(String queueName) {		
		return receiveMessage(queueName, true);
	}
	
	public Message receiveMessage(String queueName, boolean wait) {
		ArrayList<Message> qm = queues.get(queueName);
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
		if(wait && queueFeatures.get(queueName).getWaitTime()>0) {
			try {
				Thread.sleep(queueFeatures.get(queueName).getWaitTime() * 1000);
				return receiveMessage(queueName, false);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public void ackMessage(String queueName, String messageId) {
		ArrayList<Message> qm = queues.get(queueName);
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
		ArrayList<Message> qm = queues.get(queueName);
		if(qm != null) {
			Iterator<Message> qmi = qm.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();
				if(messageId.equals(oneMsg.getMessageId())) {
					if(queueFeatures.get(queueName).getMaxFailCount() < oneMsg.getReceiveFailCount()) {
						deadQueues.get(queueName).add(oneMsg);
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
		
		ArrayList<Message> dqm = deadQueues.get(queueName);

		if(dqm != null && dqm.size()>0) {
			returnDaedMsg = dqm.get(0);
			dqm.remove(0);
		}
		
		return returnDaedMsg;
	}
	
}

class CheckProcessTimeout extends Thread {

	private ConcurrentHashMap<String,  ArrayList<Message>> queues;
	private ConcurrentHashMap<String,  QueueFeature> queueFeatures;
	
	private ConcurrentHashMap<String,  ArrayList<Message>> deadQueues;
	
	public CheckProcessTimeout(ConcurrentHashMap<String, ArrayList<Message>> queues,
			ConcurrentHashMap<String,  QueueFeature> queueFeatures,
			ConcurrentHashMap<String, ArrayList<Message>> deadQueues) {
		super();
		this.queues = queues;
		this.queueFeatures = queueFeatures;
		this.deadQueues = deadQueues;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {e.printStackTrace(); }
			
			checkProcessTimeoutHandling();
		}
	}
	
	private void checkProcessTimeoutHandling() {
		queues.forEach((queueName, queue)-> {
			Iterator<Message> qmi = queue.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();
				if("2".equals(oneMsg.getStatus()) && oneMsg.getReceiveTime()>0 
						&& (oneMsg.getReceiveTime()+(queueFeatures.get(queueName).getProcessTimeout()*1000) > System.currentTimeMillis()))
				{
					if(queueFeatures.get(queueName).getMaxFailCount() < oneMsg.getReceiveFailCount()) {
						deadQueues.get(queueName).add(oneMsg);
						qmi.remove();
					} else {
						oneMsg.setStatus("1");
						oneMsg.setReceiveTime(0);
						oneMsg.setReceiveFailCount(oneMsg.getReceiveFailCount()+1);
					}
					return;
				}
			}
		});
	}
	
}

class Message {
	
	private String messageId;
	private String message;
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

class QueueFeature {
	
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
