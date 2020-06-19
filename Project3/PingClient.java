import java.net.*;
import java.util.Date;
/**
 * 
 * @author Jon Cobi Delfin
 *
 */
public class PingClient extends UDPPinger implements Runnable{
	
	private static final String HOST = "constance.cs.rutgers.edu"; //uncomment this and comment the other HOST to test provided ping server
//	private static final String HOST = "localhost";
	private static final int PORT = 5530;
	
	public static void main(String[] args) {
		PingClient pc = new PingClient();
		pc.run();
	}
	
	public void run() {
		try {
			long rtt[] = new long[10]; //array to store ping rtts
			System.out.println("Contacting host: " + HOST + " at port " + PORT);
			s = new DatagramSocket(5520);
			s.setSoTimeout(1000);
			for(int i = 0; i < 10; i++) {
				Date otime = new Date(); //time at sending packet
				String payload = "PING " + i + " " + otime; 
				PingMessage ping = null;
				try{
					ping = HOST.equals("localhost") ? new PingMessage(InetAddress.getLocalHost(), PORT, payload) : new PingMessage(InetAddress.getByName(HOST), PORT, payload);
				}catch(UnknownHostException e) {
					System.out.println("UnknownHostException: Shutting down client");
					return;
				}
				
				sendPing(ping);
				try {
					receivePing();
					Date ctime = new Date(); //time at receiving packet
					rtt[i] = ctime.getTime() - otime.getTime(); //compute rtt
					System.out.println("Received packet from " + ping.getIP() + " " + ping.getPort() + " " + ctime);
				} catch(SocketTimeoutException e) {
					System.out.println("SocketTimeoutException: Receive timed out");
					Date ctime = new Date();
					rtt[i] = ctime.getTime() - otime.getTime();
				}
			}
			s.setSoTimeout(5000);
			s.close();
			//Display ping info
			long min = rtt[0], max = rtt[0];
			double avg = 0;
			for(int i = 0; i < 10; i++) {
				if(rtt[i] < min) min = rtt[i];
				else if(rtt[i] > max) max = rtt[i];
				avg += rtt[i];
				System.out.println("PING " + i + ": " + ((rtt[i] == 1000) ? " false RTT: " : " true RTT: ") + rtt[i]);
			}
			avg /= 10;
			System.out.println("Minimum = " + min + "ms, Maximum = " + max + "ms, Average = " + avg + "ms");
		} catch(SocketException e) {
			e.printStackTrace();
		}
	}
}
