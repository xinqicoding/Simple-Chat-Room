

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Authentication {

	private Map<String, String> userInfos;
//	private String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();//The client uses this path
	private String path = "";//myeclipse use this path
	
//	private String userInfoPath = "../resources/user_pass.txt";//The client uses this path
	private String userInfoPath = "resources/user_pass.txt";//myeclipse use this path

	public Authentication() {
		if(path.startsWith("/")){
			path = path.substring(1);
		}
		userInfos = new HashMap<String, String>();
//		System.out.println(path+userInfoPath);
		Set<String> userInfoTemp = readFile(path+userInfoPath);
		for(String userInfo : userInfoTemp){
			String[] temp = userInfo.split(" ");
			userInfos.put(temp[0], temp[1]);
		}

	}

	public boolean authenticate(String username, String password) {

		if(userInfos.containsKey(username)&&password.equals(userInfos.get(username))){
			System.out.println("Validation success！");
			return true;
		}
		System.out.println(username + "Validation failure");
		return false;
	}

	/**
	 * File reading method for reading user information
	 * @param filePath
	 * @return
	 */
	private Set<String> readFile(String filePath) {

		Set<String> result = new HashSet<String>();

		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // To determine whether a file exists
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println("Could not find the specified file.");
			}
		} catch (Exception e) {
			System.out.println("Error reading file contents");
			e.printStackTrace();
		}
		
		return result;

	}

}
