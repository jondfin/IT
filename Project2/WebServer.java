/**
 * @author Jon Cobi Delfin
 */

import java.net.*;

public class WebServer {
	
	private static ServerSocket serv = null;
	
	public static void main(String[] args) {
		WebServer s = new WebServer();
		System.out.println("Starting up server...");
		s.run();
		
		try {
			serv.close();
		}catch(Exception e) {
			System.out.println("Error closing server");
			System.out.println(e);
		}
	}
	
	public void run() {
		try {
			serv = new ServerSocket(5520);
		}catch(Exception e) {
			System.out.println("Error creating server socket");
			System.out.println(e);
			return;
		}
		System.out.println("Server established\n");
		//Server loop
		while(true) {
			Socket sock = null;
			WebRequest client = null;
			try {
				sock = serv.accept();
				client = new WebRequest(sock);
				System.out.println("Got a new connection");
				client.start();
			}catch(Exception e) {
				System.out.println("Error creating connection");
				System.out.println(e);
				return;
			}
		}
	}
}
