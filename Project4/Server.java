package Project4;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Server Class
 * @author Jon Cobi Delfin
 *
 */
public class Server {
	//Servr socket
	private static ServerSocket serv = null;
	//Socket to communicate with client
	private static Socket sock = null;
	//Port number
	private static final int PORT = 5520;
	//Chunk size
	private static final int CHUNK_SIZE = 1024;
	//Stream to read input from socket
	private static DataInputStream in = null;
	
	public static void main(String[] args) {
		new Server().run();
	}
	
	private void run() {
		//Create new server socket
		try {
			serv = new ServerSocket(PORT);
		} catch(Exception e) {
			System.out.println("Error creating server socket");
			e.printStackTrace();
			return;
		}
		System.out.println("Server running...");
		while(true) {
			System.out.println("Waiting for a connection...");
			//Attempt connection
			try {
				sock = serv.accept();
			} catch (Exception e) {
				System.out.println("Error accepting connection...");
				e.printStackTrace();
			}
			//Connection successful
			System.out.println("Got a connection: " + new Date());
			System.out.println("Connected to " + serv.getInetAddress() + " Port: " + serv.getLocalPort());

			//Read bytes
			try {
				in = new DataInputStream(sock.getInputStream());
				//Get filename
				String fileName = getNullTerminatedString().trim();
				System.out.println("Got file name: " + fileName);
				//Get filesize
				String fileSize = getNullTerminatedString().trim();
				System.out.println("File size: " + fileSize);

				//Stream to write to socket
				DataOutputStream out = new DataOutputStream(sock.getOutputStream());

				//Get file contents
				if(getFile(fileName, Long.parseLong(fileSize)) == false) out.write('x'); //fail
				else out.write('@'); //success
				
				out.close();
				//Close socket and listen for new connections
				sock.close();
			} catch(IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	/**
	 * Read bytes from inputstream until a null byte is reached.
	 * @return String received
	 */
	private String getNullTerminatedString() {
		byte[] bytes = new byte[CHUNK_SIZE];
		byte b;
		try {
			//Read byte by byte
			int i = 0;
			while( (b = in.readByte()) !=  '\0') {
				bytes[i] = b;
				i++;
			}
			return new String(bytes);
		} catch(Exception e) {
			System.out.println("Error getting string");
			e.printStackTrace();
			return null;
		}
	}
	 
	/**
	 * Read the file bytes from the socket and create a new file locally
	 * @param fileName Name of file
	 * @param size Size of file
	 */
	private boolean getFile(String fileName, long size) {
		try {
			//Create new file
			File newfile = new File(fileName);
			if(!newfile.exists()) {
				newfile.createNewFile();
			}
			//Create a stream to write bytes to file
			FileOutputStream file = new FileOutputStream(newfile);
			byte[] bytes = new byte[CHUNK_SIZE];
			int c = 0;
			while( (c = in.read(bytes, 0, CHUNK_SIZE) ) > 0) {
				if(c < CHUNK_SIZE) {
					file.write(bytes, 0, c-1);
					break;
				}
				file.write(bytes, 0, c);
			}
			System.out.println("Got the file.");	
			file.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
