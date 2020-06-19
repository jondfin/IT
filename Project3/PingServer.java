import java.net.*;
import java.util.*;

/**
 * 
 * @author Jon Cobi Delfin
 *
 */
public class PingServer {
	private static final int PORT_NUMBER = 5530;
	private static final int PACKET_SIZE = 512;
	private static final int DOUBLE = 2;
	private static final int AVERAGE_DELAY = 0;
	private static final double LOSS_RATE = 0.3;
	
	private static Random random;
	private static DatagramSocket udpSocket;
	
	public static void main(String[] args) {
		System.out.println("Ping Server running...");
		PingServer p = new PingServer();
		try {
			udpSocket = new DatagramSocket(PORT_NUMBER, InetAddress.getLocalHost());
		} catch(Exception e) {
			e.printStackTrace();
		}
		p.run();
		System.out.println("Ping Server stopping...");
	} 
	
	public void run() {
		while(true) {
			System.out.println("Waiting for UDP packet...");
			for(int i = 0; i < 10; i++) {
				byte[] buff = new byte[PACKET_SIZE];
				DatagramPacket inpacket = new DatagramPacket(buff, PACKET_SIZE);
				try{
					random = new Random(new Date().getTime());
					double r = random.nextDouble();
					
					udpSocket.receive(inpacket);
					System.out.println("Received from: " + inpacket.getAddress() + " PING " + i + " " + new Date().getTime());
					
					//Simulate transmission delay
					Thread.sleep((int)(r * DOUBLE * AVERAGE_DELAY));
					
					//Simulate packet loss
					if(r < LOSS_RATE) {
						System.out.println("Packet loss...reply not sent.");
					}else {
						//Send outpacket
						DatagramPacket outpacket = new DatagramPacket(buff, PACKET_SIZE, inpacket.getAddress(), inpacket.getPort());
						udpSocket.send(outpacket);
						System.out.println("Reply sent.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
