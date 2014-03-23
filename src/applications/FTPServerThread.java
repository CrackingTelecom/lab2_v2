package applications;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import services.TTPService;

public class FTPServerThread implements Runnable{
	
	TTPService ttp;
	public FTPServerThread(TTPService ttp) {
		this.ttp = ttp;
	}
	
	public void run() {
		try {
			Object request = ttp.receive();
			String targetPath = request.toString();
			Object file = getFile(targetPath);
			ttp.send(file);
			System.out.println("Send file: " + targetPath);
			ttp.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	public Object getFile(String path) throws IOException {
		FileReader read = new FileReader(path);
		BufferedReader br = new BufferedReader(read);
	    String row;
	    String content = "";
	    while((row = br.readLine())!=null){
	    	content += row;
	    }
		   
		return content;
	}

}
