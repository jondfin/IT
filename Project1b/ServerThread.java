/*
 * @author Jon Cobi Delfin
 * CS 352
 * NetID jjd279
 */
import java.io.*;
import java.net.*;
import java.util.*;

class ServerThread extends Thread{
	
	private Socket sock = null;
	private PrintWriter io = null;
	private PrintWriter log = null;
	private BufferedReader buf = null;
	
	public ServerThread(Socket clientSock, PrintWriter logfile) {
		this.sock = clientSock;
		this.log = logfile;
	}
	
	public void run() {
		this.log.println("Got a connection: " + new Date().toString() + " Address: " + sock.getInetAddress() + " Port: " + sock.getPort());
		try {
			io = new PrintWriter(this.sock.getOutputStream(), true);
			buf = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}catch(Exception e) {
			this.log.println(e);
			return;
		}
		//Thread loop
		while(true) {
			String s = "";
			//Read line from client
			try {
				s = buf.readLine();
				//Kill connection between client and server
				if(s == null || s.equalsIgnoreCase("quit")) {
					this.log.println("Connection closed on " + sock.getPort());
					try {
						io.println("Goodbye!");
						io.close(); //close printwriter to client
						this.sock.close(); //close socket
					}catch(Exception e) {
						this.log.println(e + " on port: " + sock.getPort());
						return;
					}
					return;
				}
				//Encrypt message
				String encryptedMessage = new String(encrypt(s));
				io.println(encryptedMessage); //send message back to client
			}catch(SocketException se) {
				this.log.println(se + " on port: " + sock.getPort());
				return;
			}catch(Exception e) {
				this.log.println(e + " on port " + sock.getPort());
				return;
			}
		}
	}
	
	/*
	 * Pattern is C1 C2 C2 C1 C2
	 * C1 = 5
	 * C2 = 19
	 * ASCII Uppercase ranges from 65-90
	 * ASCII Lowercase ranges from 97-122
	 */
	private  char[] encrypt(String message) {
		char encryptedMessage[] = new char[message.length()];
		int j = 0; //keep track of which cipher to use
		
		for(int i = 0; i < message.length(); i++) {
			int remainder = 0;
			if(j == 5) j = 0;
			if(!Character.isLetter(message.charAt(i))) {
				//Ignore special characters
				encryptedMessage[i] = message.charAt(i);
			}else if(j == 0 || j == 3) {
				//Use C1
				if(Character.isUpperCase(message.charAt(i))) {
					if(message.charAt(i) + 5 > 90) {
						remainder = 5 - (90 - message.charAt(i));
						encryptedMessage[i] = (char)(65 + remainder - 1);
					}else encryptedMessage[i] = (char)(message.charAt(i) + 5);
				}else {
					if(message.charAt(i) + 5 > 122) {
						remainder = 5 - (122 - message.charAt(i));
						encryptedMessage[i] = (char)(97 + remainder - 1);
					}else encryptedMessage[i] = (char)(message.charAt(i) + 5);
				}
				j++;
			}else {
				//Use C2
				if(Character.isUpperCase(message.charAt(i))) {
					if(message.charAt(i) + 19 > 90) {
						remainder = 19 - (90 - message.charAt(i));
						encryptedMessage[i] = (char)(65 + remainder - 1);
					}else encryptedMessage[i] = (char)(message.charAt(i) + 19);
				}else {
					if(message.charAt(i) + 19 > 122) {
						remainder = 19 - (122 - message.charAt(i));
						encryptedMessage[i] = (char)(97 + remainder - 1);
					}else encryptedMessage[i] = (char)(message.charAt(i) + 19);
				}
				j++;
			}
//			System.out.println("Original: " + message.charAt(i) + " " + (int)message.charAt(i) + " Encrypted: " + encryptedMessage[i] + " " + (int)encryptedMessage[i]);
		}
		return encryptedMessage;
	}
}
