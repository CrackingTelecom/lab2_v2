package applications;

import java.io.IOException;

import services.TTPService;

public class FTPserver {

	public static void main(String[] args) {
		
		if(args.length != 3) {
			printUsage();
		}
		
		short port = Short.parseShort(args[0]);
		int window = Integer.parseInt(args[1]);
		int timeout = Integer.parseInt(args[2]);
		inputCheck(port, window, timeout);

		System.out.println("Starting 740Ftp server...");
		
		try {
			TTPService ttpService = new TTPService((short) port, window, timeout);
			
			while (true) {
				System.out.println("waiting for connection...");

				/*
				 * Accept a connection for further communication with the
				 * client. This connection is sure for this client. Further
				 * connections are still accepted on server
				 */
				ttpService.accept();

				System.out.println("Accpeted new connection from "
						+ ttpService.desIP + ":" + ttpService.desPort);

				FTPServerThread serverThread = new FTPServerThread(ttpService);
				Thread thread = new Thread(serverThread);
				thread.start();
				thread.join();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	private static void printUsage() {
		System.out.println("Usage: server <Port> <Sender/receiver window size> <Retransmission timer interval>");
		System.exit(-1);
	}
	
	private static void inputCheck(int port, int windowSizeN, int timeout) {
		if (port > 65535 || port < 1024) {
			System.err.println("Port number must be in between 1024 and 65535");
			System.exit(1);
		}
		if (windowSizeN < 0) {
			System.err.println("Window size must be larger than 0");
			System.exit(1);
		}
	}

}
