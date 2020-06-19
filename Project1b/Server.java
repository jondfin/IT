/*
 * @author Jon Cobi Delfin
 * CS 352
 * NetID jjd279
 */

import java.io.*;
import java.net.*;
public class Server {
	
	private static ServerSocket serv = null;
	private static PrintWriter pw = null;
	
	public static void main(String[] args) {
		Server s = new Server();
		System.out.println("Starting up server...");
		s.run();
		try {
			pw.close();
			serv.close();
		}catch(NullPointerException n) {
			System.out.println("Null pointer");
		}catch(Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			serv = new ServerSocket(5520);
			pw = new PrintWriter(new FileWriter("prog1b.log"), true);//log will be auto created if it doesnt exist
		}catch(BindException b) {
			System.out.println("Bind error, cannot establish server.");
			return;
		}catch(Exception e) {
			System.out.println("Error, cannot run server: ");
			e.printStackTrace();
			return;
		}
		System.out.println("Server established.");
		while(true) {
			Socket sock = null;
			ServerThread t = null;
			try {
				sock = serv.accept();
				t = new ServerThread(sock, pw);
				t.start();
			}catch(Exception e) {
				System.out.println("Error generating thread");
				e.printStackTrace();
				return;
			}
		}
	}
}
