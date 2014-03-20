package applications;

import java.io.IOException;
import java.net.SocketException;

import services.TTPService;

public class FTPclient {

	public static void main(String[] args) {

		try {

			System.out.println("Starting client ...");

			TTPService ttp = new TTPService((short)8061,100, 1000);

			String srcIP = "localhost";
			String desIP = "localhost";
			short srcPort = 8061;
			short desPort = 8060;


			Object data = "/test.txt";
			
			ttp.connect(srcIP, desIP, srcPort, desPort);
			ttp.send(data);
			Object file = ttp.receive();
			
			System.out.println("receive " + file);
			
			ttp.close();

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
