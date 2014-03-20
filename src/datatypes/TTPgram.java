package datatypes;

public class TTPgram {
	
	int seqNum;

	int ackNum;

	boolean frag;
	
	Datagram datagram;
	
	
	
	public TTPgram() {
		datagram = new Datagram();
		seqNum = 0;
		ackNum = 0;
		frag = false;
	}

	public TTPgram(String srcaddr, String dstaddr, short srcport,
			short dstport, short size, short checksum, Object data,int seqNum,int ackNum,boolean frag) {
		datagram = new Datagram(srcaddr,dstaddr,srcport,dstport,size,checksum,data);
		this.seqNum = seqNum;
		this.ackNum = ackNum;
		this.frag = frag;
	}
	
	
	
	
	public void setFrag(boolean b)
	{
		this.frag = b;
	}
	
	public boolean getFrag()
	{
		return this.frag;
	}
	public void setSeq(int seq) {
		this.seqNum = seq;
	}

	public int getSeq() {
		return this.seqNum;
	}

	public void setAck(int ack) {
		this.ackNum = ack;
	}

	public int getAck() {
		return this.ackNum;
	}

	/**
	 * @return the srcaddr
	 */
	public String getSrcaddr() {
		return datagram.srcaddr;
	}

	/**
	 * @param srcaddr
	 *            the srcaddr to set
	 */
	public void setSrcaddr(String srcaddr) {
		datagram.srcaddr = srcaddr;
	}

	/**
	 * @return the dstaddr
	 */
	public String getDstaddr() {
		return datagram.dstaddr;
	}

	/**
	 * @param dstaddr
	 *            the dstaddr to set
	 */
	public void setDstaddr(String dstaddr) {
		datagram.dstaddr = dstaddr;
	}

	/**
	 * @return the srcport
	 */
	public short getSrcport() {
		return datagram.srcport;
	}

	/**
	 * @param srcport
	 *            the srcport to set
	 */
	public void setSrcport(short srcport) {
		datagram.srcport = srcport;
	}

	/**
	 * @return the dstport
	 */
	public short getDstport() {
		return datagram.dstport;
	}

	/**
	 * @param dstport
	 *            the dstport to set
	 */
	public void setDstport(short dstport) {
		datagram.dstport = dstport;
	}

	/**
	 * @return the size
	 */
	public short getSize() {
		return datagram.size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(short size) {
		datagram.size = size;
	}

	/**
	 * @return the checksum
	 */
	public short getChecksum() {
		return datagram.checksum;
	}

	/**
	 * @param checksum
	 *            the checksum to set
	 */
	public void setChecksum(short checksum) {
		datagram.checksum = checksum;
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return datagram.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		datagram.data = data;
	}

}
