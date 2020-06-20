package Project4;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Client class
 * @author Jon Cobi Delfin
 *
 */
public class Client {
	//Port number
	private static final int PORT = 5520;
	//Chunk size
	private static final int CHUNK_SIZE = 1024;
	//Server socket to connect to
	private static Socket sock = null;
	//Outputstream
	private static DataOutputStream out = null;
	//Text area to display communication
	private static JTextArea dialog;
	
	public static void main(String[] args) {
		new Client().runClient();
	}
	
	private void runClient() {
		JFrame frame = new JFrame();
		JPanel mainPanel = new JPanel();
		GroupLayout group = new GroupLayout(mainPanel);
		mainPanel.setLayout(group);
		
		JPanel topPanel = new JPanel();
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("MSWord", "doc", "docx"));
		topPanel.add(fc);

		FlowLayout flow = new FlowLayout();
		JPanel midPanel = new JPanel();
		JLabel label = new JLabel("Server address:");
		JTextField serverAddr = new JTextField();
//		serverAddr.setText("constance.cs.rutgers.edu");
		serverAddr.setText("localhost");
		serverAddr.setPreferredSize(new Dimension(200, 20));
		JButton connectBtn = new JButton("Connect and Upload");
		midPanel.setLayout(flow);
		midPanel.add(label);
		midPanel.add(serverAddr);
		midPanel.add(connectBtn);
		midPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		JLabel label2 = new JLabel("Error Messages");
		dialog = new JTextArea();
		dialog.setEditable(false);
		JScrollPane scroll = new JScrollPane(dialog);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(100, 200));
		
		group.setHorizontalGroup(
				group.createParallelGroup()
					.addComponent(topPanel)
					.addComponent(midPanel)
					.addComponent(label2)
					.addComponent(scroll)
		);
		
		group.setVerticalGroup(
				group.createSequentialGroup()
					.addComponent(topPanel)
					.addComponent(midPanel)
					.addComponent(label2)
					.addComponent(scroll)
		);
		
		//Add action listener to connect button
		connectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Get hostname
				String server = serverAddr.getText();
				if(server.isEmpty()) {
					dialog.append("Error. Server address cannot be empty.\n");
					return;
				}
				//Get file
				File f = fc.getSelectedFile();
				if(f == null) {
					dialog.append("Error. Filename cannot be empty.\n");
					return;
				} 
				//Attempt connection
				try {
					//Open socket
					if(server.equalsIgnoreCase("localhost")) sock = new Socket(InetAddress.getLocalHost(), PORT);
					else sock = new Socket(server, PORT);
					out = new DataOutputStream(sock.getOutputStream());
					dialog.append("Connected.\n");
					//Create input stream to receive message from server
					DataInputStream in = new DataInputStream(sock.getInputStream());
					
					//Get file details
					String fileName = f.getName();
					long fileSize = f.length();
					String filePath = f.getPath();
					
					//Write bytes to socket
					dialog.append("Sent file name: " + fileName + "\n");
					sendNullTerminatedString(fileName);
					dialog.append("Sent file length: " + fileSize + "\n");
					sendNullTerminatedString(Long.toString(fileSize));
					dialog.append("Sending file...\n");
					sendFile(filePath);
					dialog.append("File sent. Waiting for the server...\n");

					//Read response from server
					if(in.read() == '@') dialog.append("Upload O.K.\n");
					else dialog.append("Upload unsuccessful.\n");
					
					//Disconnect
					dialog.append("Disconnected\n");
					out.close();
				} catch(ConnectException ex) {
//					ex.printStackTrace();
					dialog.append("Error: ConnectException, cannot connect to host\n");
					return;
				} catch (UnknownHostException ex) {
//					ex.printStackTrace();
					dialog.append("Error: UnkownHostException, cannot connect to host\n");
					return;
				} catch(SocketException ex) {
//					ex.printStackTrace();
					dialog.append("Error: SocketException\n");
					return;
				} catch (IOException ex) {
//					ex.printStackTrace();
					dialog.append("Error: IOException\n");
					return;
				} 
			}
		});
		
		//Display frame
		frame.setTitle("Program 4 - a file uploader");
		frame.getContentPane().add(mainPanel);
		
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	/**
	 * Append null byte to input string. Input string can be filename or filesize
	 * @param s String to append null byte to
	 */
	private void sendNullTerminatedString(String s) {
		try {
			out.write(s.getBytes());
			out.write('\0'); //null terminate the string
		} catch (IOException e) {
			dialog.append("Error: IOException, couldn't send string\n");
//			e.printStackTrace();
		}
	}
	
	/**
	 * Send contents of file to server in bytes.
	 * @param fullPathName full path of file
	 */
	private void sendFile(String fullPathName) {
		try {
			File f = new File(fullPathName);
			FileInputStream in = new FileInputStream(f); //get bytes from file
			byte[] b = new byte[CHUNK_SIZE];
			int numBytes = 0;
			while( (numBytes = in.read(b)) > 0) {
				out.write(b, 0, numBytes);
			}
			out.write('\0'); //null terminate file
			
			in.close();
		} catch(IOException e) {
			dialog.append("Error: IOException, couldn't send file\n");
//			e.printStackTrace();
		}
		
	}
	
}
