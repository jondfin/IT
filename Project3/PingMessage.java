import java.net.*;
/**
 * 
 * @author Jon Cobi Delfin
 *
 */
public class PingMessage {
	private InetAddress addr;
	private int port;
	private String payload;
	
	public PingMessage(InetAddress addr, int port, String payload) {
		this.addr = addr;
		this.port = port;
		this.payload = payload;
	}
	public InetAddress getIP() { //get the destination IP address
		return addr;
	}
	public int getPort() { //get the destination port number
		return port;
	}
	public String getPayload() { //get the content of the payload
		return payload;
	}
}
