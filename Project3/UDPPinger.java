import java.io.IOException;
import java.net.*;
/**
 * 
 * @author Jon Cobi Delfin
 *
 */
public class UDPPinger {
	
	public DatagramSocket s; //Socket for client to talk to server
	
	public void sendPing(PingMessage ping) {
		try {
			DatagramPacket udpPacket = new DatagramPacket(ping.getPayload().getBytes(), ping.getPayload().length(), ping.getIP(), ping.getPort());
			s.send(udpPacket);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PingMessage receivePing() throws SocketTimeoutException {
		try {
			byte buf[] = new byte[512];
			DatagramPacket rcv = new DatagramPacket(buf, 512);
			s.receive(rcv);
			PingMessage ret = new PingMessage(rcv.getAddress(), rcv.getPort(), rcv.getData().toString());
			return ret;
		}catch(SocketTimeoutException e) {
			throw new SocketTimeoutException();
		}catch(SocketException e) {
			e.printStackTrace();
			return null;
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
