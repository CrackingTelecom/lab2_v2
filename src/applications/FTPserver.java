package applications;

import java.io.IOException;

import services.TTPService;

public class FTPserver {

	public static void main(String[] args) {

		try {
			int window = 1000;
			int timeout = 2000;
			TTPService ttpService = new TTPService((short) 8060, window,
					timeout);
			System.out.println("Starting server...");

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

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
