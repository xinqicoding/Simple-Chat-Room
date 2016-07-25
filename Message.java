
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private String from;//sender
	private String password;
	private String content;
	private Date sendTime;
	private String command;//
	private MessageType msgType;
	private ChatState chatState;
	private String to;//reciver
	private List<String> names = new ArrayList<String>();//save all the user
	
	public Message() {
		super();
	}
	
	public Message(String from, String content, MessageType msgType) {
		super();
		this.from = from;
		this.content = content;
		this.msgType = msgType;
	}
	
	public Message(String from, String password, String content, MessageType msgType,
			ChatState chatState, String to) {
		super();
		this.from = from;
		this.password = password;
		this.content = content;
		this.msgType = msgType;
		this.chatState = chatState;
		this.to = to;
	}
	
	public Message(String from, String password, String content, MessageType msgType) {
		super();
		this.from = from;
		this.password = password;
		this.content = content;
		this.msgType = msgType;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

	public MessageType getMsgType() {
		return msgType;
	}
	public void setMsgType(MessageType msgType) {
		this.msgType = msgType;
	}
	public ChatState getChatState() {
		return chatState;
	}
	public void setChatState(ChatState chatState) {
		this.chatState = chatState;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
