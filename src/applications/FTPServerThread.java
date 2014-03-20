package applications;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import services.TTPService;

public class FTPServerThread implements Runnable{
	
	TTPService ttp;
	public FTPServerThread(TTPService ttp)
	{
		this.ttp = ttp;
	}
	
	public void run()
	{
		try {
			Object request = ttp.receive();
			
			String targetPath = request.toString();
			
			Object file = getFile(targetPath);
			
			ttp.send(file);
			
			System.out.println("send file");
			
			ttp.close();
			
			System.out.println("subthread finish");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Object getFile(String path) throws IOException
	{
		//System.out.println(path);
		FileReader read = new FileReader(path);
		BufferedReader br = new BufferedReader(read);
	    String row;
	    String content = "";
	    while((row = br.readLine())!=null){
	    	content += row;
	    	System.out.println(row);
	    }
		   
		return content;
	}

}
