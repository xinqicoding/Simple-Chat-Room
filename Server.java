
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server{
	ServerSocket ss = null;
	List<UserInfo> users=new ArrayList<UserInfo>();
	List<String> names = new ArrayList<String>();
	Map<String, String> blockList = new HashMap<String, String>();//ip black list
	Map<String, String> lastConnectedTimes = new HashMap<String, String>();
	static int count=0;
	static int LAST_HOUR = 60;//minute
	static int BLOCK_TIME = 60;//second
	private boolean run=true;

	public static void main(String[] args) throws Exception {
		if(args.length==1){
			Server server = new Server();
			server.start(args[0]);
		}else{
			Server server = new Server();
			server.start("4119");
		}
		
	}
	
	private boolean isOutTime(String time){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{   
			Date d1 = new Date(); 
			Date d2 = df.parse(lastConnectedTimes.get(time));
			long diff = d1.getTime() - d2.getTime();
			long number = diff / (1000);
			if(number>BLOCK_TIME){
				return true;
			}
		}catch (Exception e){   
		}   
		return false;
	}
	
	private List<String> getLastConnectedUsers(String time){
		this.LAST_HOUR = Integer.valueOf(time);
		List<String> users = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{   
			Date d1 = new Date(); 
			for(String userName : lastConnectedTimes.keySet()){
				Date d2 = df.parse(lastConnectedTimes.get(userName));
				long diff = d1.getTime() - d2.getTime();
				long number = diff / (1000 * 60 );
				if(number < LAST_HOUR){
					users.add(userName);
				}
			} 
		}catch (Exception e){   
		}   
		return users;
	}
	
	/**
	 * start server
	 * @throws Exception
	 */
	private void start(String port) throws Exception{
		System.out.println("server start...");
		ss = new ServerSocket(Integer.parseInt(port));
		final Authentication authentication = new Authentication();
		while(run){
				Socket socket = ss.accept();
				++count;
				final Socket s = socket;
				new Thread(){
					UserInfo user=null;
					InputStream is=null;
					ObjectInputStream ois=null;
					@Override
					public void run() {
						try {
							boolean flag=true;
							is = s.getInputStream();
							ois = new ObjectInputStream(is);
							while(flag){
								Message msg = (Message)(ois.readObject());
								if(user==null){
									if(!lastConnectedTimes.containsKey(msg.getFrom())){//is landed
										if(blockList.containsKey(s.getInetAddress().toString())){//is in black list
											String info = blockList.get(s.getInetAddress().toString());
											String[] tempInfo = info.split("#");
											if(Integer.valueOf(tempInfo[0])>1){
												if(isOutTime(tempInfo[1])){//
													blockList.remove(s.getInetAddress().toString());
													if(authentication.authenticate(msg.getFrom(), msg.getPassword())){//Validation success
														user=new UserInfo(s, msg.getFrom());
														System.out.println(msg.getFrom()+"landed��");
														System.out.println(s.getRemoteSocketAddress());
													}else{//Validation failure
														msg.setMsgType(MessageType.Error);
														msg.setContent("1");//User name or password error
														String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
														blockList.put(s.getInetAddress().toString(), "1#" + nowTime);
														SendMessageToClient(msg, s);
														flag = false;
													}
												}else{//
													msg.setMsgType(MessageType.Error);
													msg.setContent("2");//refuse
													String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
													blockList.put(s.getInetAddress().toString(), "1#" + nowTime);
													SendMessageToClient(msg, s);
													flag = false;
												}
											}else{//
												if(authentication.authenticate(msg.getFrom(), msg.getPassword())){//Validation success
													user=new UserInfo(s, msg.getFrom());
													System.out.println(msg.getFrom()+"landed");
												}else{//
													msg.setMsgType(MessageType.Error);
													msg.setContent("1");
													String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
													int times = Integer.valueOf(tempInfo[0])+1;
													blockList.put(s.getInetAddress().toString(), times + "#" + nowTime);
													SendMessageToClient(msg, s);
													flag = false;
												}
											}
										}else{ //
											if(authentication.authenticate(msg.getFrom(), msg.getPassword())){
												user=new UserInfo(s, msg.getFrom());
												System.out.println(msg.getFrom()+"landed");
												System.out.println(s.getRemoteSocketAddress());
											}else{//
												msg.setMsgType(MessageType.Error);
												msg.setContent("1");//
												String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
												blockList.put(s.getInetAddress().toString(), "1#" + nowTime);
												SendMessageToClient(msg, s);
												flag = false;
											}
										}
									}else{
										msg.setMsgType(MessageType.Error);
										msg.setContent("0");
										SendMessageToClient(msg, s);
										flag = false;
									}
								}
								if(flag){
									synchronized (this){
										if(!users.contains(user)){
											users.add(user);
											lastConnectedTimes.put(user.getName(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
											if(!names.contains(user.getName())){
												names.add(user.getName());
											}
										}
									}
									MessageType type = msg.getMsgType();
//									System.out.println("Type: "+type);
									if(type.equals(MessageType.Login)){
										String content = "Welcome to simple chat server!";
										msg.setContent(content);
										msg.setNames(names);
										SendToPersonal(msg);//Send to the lander
									}else if(type.equals(MessageType.Chat)){
											String content = msg.getFrom()+ ": " + msg.getContent();
											msg.setContent(content);
											if(msg.getChatState().equals(ChatState.Personal)){
												SendToPersonal(msg);
											}else{
												SendMessageToAll(msg);
											}
									}else if(type.equals(MessageType.Other)){
										String content = "";
										if(msg.getCommand().equals("whoelse")){
											for(UserInfo userInfo : users){
												if(!msg.getFrom().equals(userInfo.getName())){
													content = content + userInfo.getName() + "\n";
												}
											}
										}else if(msg.getCommand().contains("wholast")){
											String[] ss = msg.getCommand().split(" ");
											for(String userName : getLastConnectedUsers(ss[1])){
												if(!msg.getFrom().equals(userName)){
													content = content + userName + "\n";
												}
											}
										}
										
										msg.setContent(content);
										SendToPersonal(msg);
									}else if(type.equals(MessageType.Logout)){
										synchronized (this) {
											names.remove(user.getName());
											users.remove(user);
											count--;
											String content = "Goodbye!";
											msg.setContent(content);
											msg.setNames(names);
											SendToPersonal(msg);
											flag=false;
										}					
									}
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					
					}

					private synchronized void SendMessageToAll(Message msg) throws Exception{
						for(UserInfo s : users){
								ObjectOutputStream oos = new ObjectOutputStream(s.getSocket().getOutputStream());
								oos.writeObject(msg);
								oos.flush();
						}
					}
					
					private synchronized void SendMessageToClient(Message msg, Socket s) throws Exception{
						ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
						oos.writeObject(msg);
						oos.flush();
					}
					
					private synchronized void SendToPersonal(Message msg)throws Exception{
						int times=0;
						for(UserInfo s:users){
							if(s.getName().equals(msg.getTo())||s.getName().equals(msg.getFrom())){
								ObjectOutputStream oos = new ObjectOutputStream(s.getSocket().getOutputStream());
								oos.writeObject(msg);
								oos.flush();
								times++;
								if(times==2)break;
							}
						}
					}
					
				}.start();
		}
	}
}