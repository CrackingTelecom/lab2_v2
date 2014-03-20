package services;

import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import datatypes.Datagram;

public class TTPService {

	DatagramService datagramService;

	// simulate TCP
	public String srcIP;
	public String desIP;
	public short srcPort;
	public short desPort;

	// window and time interval
	int windowSize;
	int currentSize = 0;
	int timeout;
	
	

	/*
	 * sender's data structure
	 * 
	 * readyList stores all the grams that is ready to be sent sentList stores
	 * all the grams that is sent but not yet acked map is mapping ack to the
	 * corresponding Datagram. Note that ack is the id of a gram. time map is
	 * mapping ack to the send time
	 */

	LinkedList<Datagram> readyList = new LinkedList<Datagram>();
	LinkedList<Datagram> sentList = new LinkedList<Datagram>();
	HashMap<Integer, Datagram> map = new HashMap<Integer, Datagram>();
	HashMap<Integer, Integer> timeMap = new HashMap<Integer, Integer>();
	

	Thread sendThread;
	Thread receiveThread;

	/*
	 * receiver's data structure
	 */
	LinkedList<Datagram> receivedList = new LinkedList<Datagram>();

	public TTPService(short port, int windowSize, int timeout) throws SocketException {
		
		datagramService = new DatagramService(port,0);
		this.windowSize = windowSize;
		this.timeout = timeout;
	}

	/**
	 * 
	 * @param datagram
	 * @throws IOException
	 * 
	 *             send function first partition the input data into several
	 *             Datagrams according to its size. Then, it start three threads
	 *             who are working simultaneously. sendThread is for sending the
	 *             grams in the list one by one as if the current window is not
	 *             full receiveThread is for receiving the ack and decide if a
	 *             gram should be resent timeThread is for monitoring if any
	 *             sent but not yes acked gram is timeout. If so, it resent the
	 *             gram immediately
	 * @throws InterruptedException 
	 */
	public void send(Object data) throws IOException {

		// here do some segmentation
		partition(data);

		sendThread = new Thread(new sendThread());
		receiveThread = new Thread(new receiveThread());
		
		sendThread.start();
		receiveThread.start();
		
		
		try {
			sendThread.join();
			receiveThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * sendThread is a thread implemented runnable interface it sends all the
	 * grams in the readyList one by one as well as there is space in the window
	 * it then 1. adds the sent grams to sent list 2. add the currentSize in the
	 * window 3. map the ack of the gram to itself 4. map the ack of the gram to
	 * its sent time Note ack of a gram is the sum of seq and size, this ack is
	 * the expected ack from the receiver. It is actually the id of the gram
	 * 
	 * @author Hao
	 * 
	 */
	private class sendThread implements Runnable {
		public void run() {
			try {
				while (!readyList.isEmpty()) {
					if (currentSize < windowSize) {
						Datagram gram = readyList.removeFirst();
						datagramService.sendDatagram(gram);
						sentList.add(gram);
						currentSize++;

						int ack = gram.getSeq() + gram.getSize();
						map.put(ack, gram);

						Date date = new Date();
						timeMap.put(ack, (int) date.getTime());
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * receiverThread is to receive all the acks from the receiver and act
	 * correspondingly it loops until getting the last ack which is marked with
	 * frag field It uses GoBackN as the following logic: 1. if the ack is with
	 * the same with the expecting one, the transmission is done, delete all the
	 * records 2. if the ack is smaller then the expected one, this means this
	 * is a duplicate ack, just skip it 3. if the ack is larger then the
	 * expected one, some grams or acks are lost. Then resend all the grams in
	 * the sentList and update the corresponding records
	 * 
	 * @author Hao
	 * 
	 */
	private class receiveThread implements Runnable {
		public void run() {
			try {
				runTimer();
				
				while (true) {
					Datagram gram = datagramService.receiveDatagram();

					int waitingAck = sentList.getFirst().getSeq()
							+ sentList.getFirst().getSize();
					if (gram.getAck() == waitingAck) {
						sentList.removeFirst();
						map.remove(gram.getAck());
						timeMap.remove(waitingAck);

						currentSize--;

						if (gram.getFrag() == false)
							break;
					}

					else if (gram.getAck() < waitingAck)
						;
					else {
						for (int i = 0; i < sentList.size(); i++) {
							datagramService.sendDatagram(sentList.get(i));
							int waitingAck0 = sentList.get(i).getSeq()
									+ sentList.get(i).getSize();
							Date date = new Date();
							timeMap.put(waitingAck0, (int) date.getTime());
						}
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * runTimer starts a Timer to check if the sent but not yet acked grams are timeout.
	 * If so, just resend it immediately.
	 * 
	 * @author Hao
	 * 
	 */
	private void runTimer() {
		Timer timer = new Timer(true);

		TimerTask task = new TimerTask() {
			public void run() {
				try {
					Date date = new Date();
					for (Integer ack : timeMap.keySet()) {
						int time = timeMap.get(ack);
						if (date.getTime() > time + timeout) {
							datagramService.sendDatagram(map.get(ack));
							timeMap.put(ack, (int) date.getTime());
						}
					}
				} catch (Exception e) {

				}

			}
		};
		timer.schedule(task, 0, 500);
	}

	/**
	 * partition takes one original gram as input and partition it according to
	 * the max size of the payload. It uses seq to indicate the order of the
	 * grams and use frag to indicate the end gram
	 * 
	 * @param datagram
	 */
	private void partition(Object data) {
		String str = data.toString();
		int seq = 0;

		while (str.length() != 0) {
			Datagram newGram = new Datagram();
			newGram.setDstaddr(desIP);
			newGram.setDstport(desPort);
			newGram.setSrcaddr(srcIP);
			newGram.setSrcport(srcPort);
			
			newGram.setChecksum((short) 0);

			newGram.setSeq(seq);
			newGram.setAck(0);

			if (str.length() >= 1460) {
				newGram.setData((Object) str.substring(0, 1460));
				newGram.setSize((short) 1460);
				str = str.substring(1460);
				seq += 1460;

				newGram.setFrag(true);
			} else {
				newGram.setData((Object) str);
				newGram.setSize((short) str.length());
				str = "";
				seq += str.length();

				newGram.setFrag(false);
			}
			
			Object d = newGram.getData();
			newGram.setChecksum((short) makeChecksum(d));

			readyList.add(newGram);
		}
	}

	/**
	 * makeChecksum calculate the checksum of the data
	 * 
	 * @param Object data
	 */
	private short makeChecksum(Object d) {
		short checksum = 0;
		String data = d.toString();
		byte[] dataBytes = data.getBytes();
		
		for (int i = 0; i < dataBytes.length; i++) {
			checksum += (short) dataBytes[i];
		}
		while ( checksum / 0xff != 0) {
			checksum = (short) (checksum & 0xff + checksum >> 8);
		}
		checksum = (short) ~checksum;
		
		return checksum;
	}
	
	/**
	 * validateChecksum validate the checksum of the received data
	 * 
	 * @param Object d, short checksum
	 */
	public boolean validateChecksum(Object d, short checksum) {
		short newChecksum = 0;
		String data = d.toString();
		byte[] dataBytes = data.getBytes();
		
		for (int i = 0; i < dataBytes.length; i++) {
			newChecksum += (short) dataBytes[i];
		}
		while ( newChecksum / 0xff != 0) {
			newChecksum = (short) (newChecksum & 0xff + newChecksum >> 8);
		}
		
		if ((newChecksum & 0xff) + (checksum & 0xff) == 0xff) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * receive function is for receiving grams and send acks according to
	 * GoBackN. It is used in receiver side.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object receive() throws IOException, ClassNotFoundException {

		int lastAck = 0;
		while (true) {
			// receive the datagrams and check checksum first
			Datagram gram = datagramService.receiveDatagram();
			System.out.println(gram.getData());
			short checksum = gram.getChecksum();
			if (!validateChecksum(gram.getData(), checksum)) {
				return null;
			}
			
			// if this is the first gram, add it directly
			if (receivedList.isEmpty()) {
				receivedList.add(gram);
				lastAck = gram.getSeq() + gram.getSize();
			} else if (gram.getSeq() == lastAck) {  // if this is not the first one, the only
													// condition it can be added to the list is
													// the equation of seq and the expected ack
				receivedList.add(gram);
				lastAck = gram.getSeq() + gram.getSize();
			}
			

			// build a return gram and fill the Ack field
			Datagram returnGram = new Datagram();
			returnGram.setSrcaddr(this.srcIP);
			returnGram.setDstaddr(this.desIP);
			returnGram.setSrcport(this.srcPort);
			returnGram.setDstport(this.desPort);
			returnGram.setAck(lastAck);
			returnGram.setFrag(gram.getFrag());
			returnGram.setChecksum(makeChecksum(returnGram));

			datagramService.sendDatagram(returnGram);

			// if this gram is the last segment, break the loop
			if (gram.getFrag() == false)
				break;
		}

		// reassemble the grams in the list
		Datagram datagram = reassemble(receivedList);
		
		return datagram.getData();

	}

	/**
	 * reassemble is to assemble the partitioned grams into one final output
	 * because the list is ordered by seq, we can just add them one by one
	 * 
	 * @param list
	 * @return
	 */
	public Datagram reassemble(LinkedList<Datagram> list) {

		Datagram assemble = new Datagram();
		String string = "";
		for (Datagram gram : list) {
			Object data = gram.getData();
			String str = data.toString();
			string += str;
		}

		assemble.setData((Object) (string));

		assemble.setSrcaddr(this.desIP);
		assemble.setDstaddr(this.srcIP);
		assemble.setSrcport(this.desPort);
		assemble.setDstport(this.srcPort);
		assemble.setChecksum(makeChecksum(assemble));

		return assemble;
	}

	public void connect(String srcIP, String desIP, short srcPort, short desPort)
			throws SocketException {
		this.srcIP = srcIP;
		this.desIP = desIP;
		this.srcPort = srcPort;
		this.desPort = desPort;
	}
	
	public void accept() throws IOException, ClassNotFoundException
	{
		Datagram datagram = datagramService.receiveDatagram();
		this.srcIP = datagram.getDstaddr();
		this.desIP = datagram.getSrcaddr();
		this.srcPort = datagram.getDstport();
		this.desPort = datagram.getSrcport();
	}

	public void close() {
		sendThread.stop();
		receiveThread.stop();
	}

}
