/**
 * @author Jon Cobi Delfin
 */

import java.net.*;
import java.io.*;

public class WebRequest extends Thread{

	private Socket sock = null;
	private HTTP req = null;
	private BufferedReader buf = null; //read request from client
	private DataOutputStream out = null;
	
	public WebRequest(Socket clientSock) {
		this.sock = clientSock;
	}
	
	@Override
	public void run() {
		try {
			buf = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new DataOutputStream(sock.getOutputStream());
			req = new HTTP(); 
		}catch(Exception e) {
			System.out.println(e);
		}
		//Thread loop
		while(true) {
			String s = "";
			String fileName = "";
			try {
				s = buf.readLine();
				fileName = req.parse(s);
				if(!req.fileExists) {
					String notFound = "";
					notFound = req.compose(404, "");
					notFound.concat("<!doctype html>"
									+ "<html>"
									+ "<head><title>NOT FOUND</title></head>"
									+ "<body>NOT FOUND</body>"
									+ "</html>");
					System.out.println(notFound);
					out.writeBytes(notFound);
					break;
				}else {
					String resp = req.compose(200, fileName);
					System.out.println(resp);
					byte[] b = new byte[1024];
					int numBytes = 0;
					out.writeBytes(resp); //write status line and headers
					while((numBytes = req.fileInputStream.read(b)) > 0) {
						out.write(b, 0, numBytes); //write body
					}
					break;
				}
			}catch(Exception e) {
				System.out.println(e);
				break;
			}
		}
		try {
			buf.close();
			out.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
}
