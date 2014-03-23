package applications;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import services.TTPService;

public class FTPclient {

	public static void main(String[] args) throws UnknownHostException {

		if(args.length != 5) {
			printUsage();
		}
		
		short srcPort = Short.parseShort(args[0]);
		short desPort = Short.parseShort(args[1]);
		int windowSize = Integer.parseInt(args[2]);
		int timeout = Integer.parseInt(args[3]);
		String filename = args[4];
		inputCheck(srcPort, desPort, timeout);

		InetAddress addr = InetAddress.getLocalHost();
		String srcIP = addr.getHostAddress().toString();
		String desIP = srcIP;
		
		System.out.println("Starting 740Ftp client ...");

		try {
			TTPService ttp = new TTPService((short) srcPort, windowSize, timeout);
			Object data = filename;
			System.out.println("Requesting file: " + filename);
			
			ttp.connect(srcIP, desIP, srcPort, desPort);
			ttp.send(data);
			Object file = ttp.receive();
			
			File localFile = new File("Received_" + filename);
	        if(!localFile.exists()) {
	        	 localFile.createNewFile();
	        }
	        FileOutputStream out = new FileOutputStream(localFile);
	        out.write(file.toString().getBytes());
	        out.close();
			System.out.println("Received file from server, saved as: " + filename);
			
			ttp.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private static void printUsage() {
		System.out.println("Usage: server <localport> <serverport> <window size> <Retransmission timer interval> <filename>\n");
		System.exit(-1);
	}
	
	private static void inputCheck(short localPort, short serverPort, int retransmissionInterval) {
		if (localPort > 65535 || localPort < 1024 || serverPort > 65535 || serverPort < 1024) {
			System.err.println("Port number must be in between 1024 and 65535");
			System.exit(1);
		}
	}

}
