package javaasp.sp.y2021.quiz3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class Quiz3Servlet extends HttpServlet {
	
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
		}
		resp.setStatus(200);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		
		String requestUri= req.getRequestURI().substring(1);
		String[] splitLine = requestUri.split("/");
		
		resp.setStatus(200);
		
		if("CREATE".equals(splitLine[0])) {
			
			String bodyJson = getJsonFromReqeustBody(req);
			
			Gson gson = new Gson();
			Map<String,Object> map = new HashMap<String,Object>();
			map = (Map<String,Object>) gson.fromJson(bodyJson, map.getClass());
			int queueSize = ((Double)map.get("QueueSize")).intValue();
			
			boolean created = messageService.createMessageQueue(splitLine[1], queueSize);
			if(created) {
				resp.getWriter().write(RESPONSE_RESULT_OK);
			} else {
				resp.getWriter().write("{\"Result\":\"Queue Exist\"}");
			}

		} else if("SEND".equals(splitLine[0])) {
			
			String bodyJson = getJsonFromReqeustBody(req);
			
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

	public static HashMap<String,  ArrayList<Message>> queues = new HashMap<>();
	public static HashMap<String,  Integer> queuesSize = new HashMap<>();

	public boolean createMessageQueue(String queueName, int queueSize) {
		
		boolean isCreated = false;
		
		if(!queues.containsKey(queueName)) {
			ArrayList<Message> newQueue = new ArrayList<>();
			queues.put(queueName, newQueue);
			queuesSize.put(queueName, queueSize);
			
			isCreated = true;
		}
		
		return isCreated;
	}
	
	public boolean sendMessage(String queueName, String message) {
		boolean isSended = true;
		
		if(queues.get(queueName).size() == queuesSize.get(queueName)) {
			isSended = false;
		} else {
			queues.get(queueName).add(new Message(UUID.randomUUID().toString(), message, "1"));
		}
		
		return isSended;
	}

	public Message receiveMessage(String queueName) {
		ArrayList<Message> qm = queues.get(queueName);
		if(qm != null) {
			Iterator<Message> qmi = qm.iterator();
			while(qmi.hasNext()) {
				Message oneMsg = qmi.next();
				if("1".equals(oneMsg.getStatus())) {
					oneMsg.setStatus("2");
					return oneMsg;
				}
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
					oneMsg.setStatus("1");
					return;
				}
			}
		}
	}
	
}

class Message {
	
	private String messageId;
	private String message;
	private String status; 
	
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
}
