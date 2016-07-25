
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client{
	
	
    //net
    static Socket socket;
    static String name;
    static String password;
    static ObjectOutputStream oos;
    static boolean loginFlag = false;
    static String trueIp = null;
    static String truePort = null;
    
    public static void main(String[] args) {
    	try {
	    	if(args.length==2){
	    		trueIp = args[0];
	    		truePort = args[1];
	    		Client client = new Client();
				client.ConnectServer(trueIp, truePort);
				Thread.sleep(2000);
				while(true){
					client.listen();
				}
			}else{
				Client client = new Client();
	        	client.ConnectServer("127.0.0.1", "4119");
	        	while(true){
					client.listen();
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
	}
    
    /**
	 * send message private
	 * @throws Exception
	 */
	public void SendToPersonalMessage(String content, String to)throws Exception{
		//Determine if you connect to the server
		if(socket == null){
			System.out.println("No connection server or disconnect server");
			return;
		}
		//send message
		if(content == null || content.trim().equals("")){
			System.out.println("Can't send an empty string");
		}
		Message msg = new Message(name, password, content, MessageType.Chat, ChatState.Personal, to);
		oos.writeObject(msg);
		oos.flush();
	}    
    
	/**
	 * Mass message
	 * @throws Exception
	 */
	public void SendToAllMessage(String content)throws Exception{
		//is connect
		if(socket == null){
			System.out.println("No connection server or disconnect server");
			return;
		}
		//send message
		if(content == null || content.trim().equals("")){
			System.out.println("Can't send an empty string");
		}
		Message msg = new Message(name, password, content, MessageType.Chat, ChatState.Group, null);
		oos.writeObject(msg);
		oos.flush();
	}
	
	/**
	 * Send query command
	 * @throws Exception
	 */
	public void SendToServer(String command)throws Exception{
		//Determine if you connect to the server
		if(socket == null){
			System.out.println("No connection server or disconnect server");
			return;
		}
		Message msg = new Message(name, password, "command", MessageType.Other, ChatState.Personal, name);
		msg.setCommand(command);
		oos.writeObject(msg);
		oos.flush();
	}
	
	/**
	 * Connect server
	 * @throws Exception
	 */
	public void ConnectServer(String ip, String portStr)throws Exception{
		//The pop-up box, which prompts the user to enter the IP address of the server.
		int cnt = 0;
		do{
			if(++cnt == 4){
				System.out.println("Sorry, your input has exceeded 3 times the \n program will automatically exit!");
				System.exit(0);
			}
			System.out.println("Procedures to receive information:"+ip+" "+portStr);
			socket=new Socket(ip, Integer.parseInt(portStr));
			System.out.print("Username: ");
			BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
			name = strin.readLine();
			System.out.print("Password: ");
			password = strin.readLine();
			OutputStream os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			Message msg = new Message(name,password,null,MessageType.Login);
			oos.writeObject(msg);
			oos.flush();
		    
		}while(socket == null);
//		After successfully creating a connection, a new thread is started to receive messages
		new Thread(){
			@Override
			public void run() {
				try {
					boolean flag=true;
					while(flag){
						InputStream is = socket.getInputStream();
						ObjectInputStream ois = new ObjectInputStream(is);
						Message msg = (Message)(ois.readObject());
						MessageType type = msg.getMsgType();
//						System.out.println("type: " + type);
						if(type.equals(MessageType.Login)){
							String content = msg.getContent();
							System.out.println(content);
							if(content.equals("Welcome to simple chat server!")){
								loginFlag = true;
							}
							System.out.print("Command: ");
						}else if(type.equals(MessageType.Chat)){
							String content = msg.getContent();
							System.out.println("");
							System.out.println(content);
							System.out.print("Command: ");
						}else if(type.equals(MessageType.Other)){
							String content = msg.getContent();
							System.out.println("");
							System.out.println(content);
							System.out.print("Command: ");
						}else if(type.equals(MessageType.Error)){
							if("0".equals(msg.getContent())){
								System.out.println("This user has logged in, and is not allowed to log in");
								System.exit(0);
							}else if("1".equals(msg.getContent())){
								System.out.println("User name or password error, please log in again");
								try {
							    	if(null!=trueIp && null!=truePort){
							    		Client client = new Client();
										client.ConnectServer(trueIp, truePort);
										while(true){
											Thread.sleep(2000);
											client.listen();
										}
									}else{
										Client client = new Client();
							        	client.ConnectServer("127.0.0.1", "4119");
							        	Thread.sleep(2000);
										while(true){
											Thread.sleep(2000);
											client.listen();
										}
									}
						    	} catch (Exception e) {
									e.printStackTrace();
								}
							}else if("2".equals(msg.getContent())){
								System.out.println("User name or password error over three times in a short time, not allowed to continue to log in");
								System.exit(0);
							}
						}else if(type.equals(MessageType.Logout)){
							String content = msg.getContent();
							System.out.println(content);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}.start();
	}
	
	/**
	 * cut the server
	 * @throws Exception
	 */
	public void BreakServer()throws Exception{
		if(socket!=null){
			Message msg=new Message(name, null, MessageType.Logout);
			oos.writeObject(msg);
			oos.flush();
			socket=null;
		}
	}
	
	/**
	 * exit
	 * @throws Exception
	 */
	public void ExitProgram()throws Exception{
		if(socket!=null){
			Message msg=new Message(name, null, MessageType.Logout);
			oos.writeObject(msg);
			oos.flush();
			socket=null;
		}
		System.exit(0);
	}
	
	/**
	 * Console monitor
	 */
	private void listen(){
		try {
			BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				// System.out.print("Command: ");
				String str = strin.readLine();
				if(str.startsWith("whoelse")){ //Displays name of other connected users
					try {
						SendToServer("whoelse");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(str.startsWith("wholast")){ //Displays name of those users connected within the last <number>minutes. Let 0 < number < 60
					try {
						SendToServer(str);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(str.startsWith("broadcast")){//Broadcasts <message>
					if(str.contains("user ") && str.contains("message ")){//Broadcasts <message> to the list of users
						try {
							String content = (String)str.subSequence(str.indexOf("message ") + 8, str.length());
							String temp = (String)str.subSequence(str.indexOf("user ") + 5, str.indexOf("message "));
							String[] users = temp.split(" ");
							for(String userName : users){
								SendToPersonalMessage(content, userName);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(str.contains("message ")){//Broadcasts <message> to all connected users
						try {
							String content = (String)str.subSequence(str.indexOf("message ") + 8, str.length());
							SendToAllMessage(content);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else if(str.startsWith("message")){//Private <message> to a <user>
					try {
						String[] contents = str.split(" ");
						String userName = contents[1];
						String content = str.substring(contents[0].length() + contents[1].length() + 2);
						SendToPersonalMessage(content, userName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(str.startsWith("logout")){//Log out this user.
					try {
						ExitProgram();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					System.out.println("Error: This command cannot be recognized");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
