/*
	Project 1a Knock Knock Client
	by Jon Cobi Delfin
	NetID jjd279
*/
import java.io.*;
import java.net.*;

import javax.swing.JFrame;

import java.awt.event.*;

public class KnockKnockClient extends JFrame{

	static Socket socket = null;
	static BufferedReader buf = null;
	static PrintWriter pw = null;
	
	public static void main(String[] args) {
		new KnockKnockClient().createGUI();
	}
	
	public void createGUI() {
		//Create JFrame
		JFrame frame = new JFrame("Program 1a Knock Knock Client");
		
		//Create components
		javax.swing.JPanel panel = new javax.swing.JPanel();
		javax.swing.JLabel ipLabel = new javax.swing.JLabel("IP address");
		javax.swing.JTextField ipTextField = new javax.swing.JTextField();
		ipTextField.setText("constance.cs.rutgers.edu");
		ipTextField.setPreferredSize(new java.awt.Dimension(200, 20)); //ensure that this text field isnt affected by
		ipTextField.setMinimumSize(new java.awt.Dimension(200, 20));   //the connect button changing text
		ipTextField.setMaximumSize(new java.awt.Dimension(200, 20));
		
		javax.swing.JLabel portLabel = new javax.swing.JLabel("Port Number");
		javax.swing.JTextField portTextField = new javax.swing.JTextField();
		portTextField.setText("5520");
		
		javax.swing.JButton connectButton = new javax.swing.JButton("Connect");
		connectButton.setPreferredSize(new java.awt.Dimension(100, 30)); //keeps the connect button the same size
		connectButton.setMinimumSize(new java.awt.Dimension(100, 30));   //whenever isit changes between connect and disconnect
		connectButton.setMaximumSize(new java.awt.Dimension(100, 30));
		
		javax.swing.JLabel msgLabel = new javax.swing.JLabel("Message to Server");
		javax.swing.JTextField msgTextField = new javax.swing.JTextField();
		javax.swing.JButton msgButton = new javax.swing.JButton();
		msgButton.setText("Send");
		
		javax.swing.JLabel csLabel = new javax.swing.JLabel("Client/Server Communication");
		javax.swing.JTextArea csTextArea = new javax.swing.JTextArea();
		csTextArea.setEditable(false);
		
		//Add functionality to the connect button
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Check on the current state of the button
				String t = connectButton.getText();
				if(t.equals("Connect")) {
					//Connect to server
					String ip = ipTextField.getText();
					int port = Integer.parseInt(portTextField.getText());

					//Attempt connection
					try {
						socket = new Socket(ip, port);
						buf = new BufferedReader(new InputStreamReader(socket.getInputStream())); //buffered reader to handle incoming messages
						pw = new PrintWriter(socket.getOutputStream(), true); //printwriter to send messages to server
						System.out.println("Successful connection to " + ip + " using port " + port);
						System.out.println(socket);
						System.out.println(buf);
						System.out.println(pw);
						csTextArea.append("Connected\n");
						
						connectButton.setText("Disconnect");
					}catch(IOException i) {
						csTextArea.append("I/O error, cannot connect to server\n");
						i.printStackTrace();
						System.out.println("I/O error, cannot connect to server");
					}catch(IllegalArgumentException a) {
						csTextArea.append("Either the IP address or the port is invalid. Please enter valid values.\n");
						System.out.println("Bad values");
					}
				}
				else if(t.equals("Disconnect")){
					try {
						socket.close();
						buf.close();
						pw.close();
						System.out.println("Successfully disconnected");
						csTextArea.append("Disconnected\n");
					}catch(NullPointerException n) {
						csTextArea.append("Error disconnecting from server. Null pointer\n");
						System.out.println("Socket is null\n" + socket);
					}catch(IOException ex) {
						csTextArea.append("Error disconnecting from server. I/O error\n");
						System.out.println(ex);
					}
					connectButton.setText("Connect");
				}
			}
		});
		
		//Add functionality to the message button
		msgButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				//Check if client is connected to server
				if(connectButton.getText().equals("Connect")) {
					csTextArea.append("You are not connected to a server\n");
				}else {
					System.out.println(socket);
					String client = msgTextField.getText();
					//Send message to server
					System.out.println("Message: " + client);
					pw.println(client);
					csTextArea.append("Client: " + client + "\n");
					String server;
					try{ //Attempt to read incoming messages from server
						server = buf.readLine();
						csTextArea.append("Server: " + server + "\n");
					}catch(IOException i) {
						csTextArea.append("Error reading message from server\n");
						System.out.println("Error reading from server");
					}
					if(client.equalsIgnoreCase("Quit")) { 
						try {
							socket.close();
							buf.close();
							pw.close();
							System.out.println("Successfully disconnected");
							csTextArea.append("Disconnected\n");
						}catch(NullPointerException n) {
							csTextArea.append("Error disconnecting from server. Null pointer.\n");
							System.out.println("Socket is null\n" + socket);
						}catch(IOException ex) {
							csTextArea.append("Error disconnecting from server. I/O exception\n");
							System.out.println(ex);
						}
						connectButton.setText("Connect");
					}
				}
			}
		});
		
		//Arrange components using GridBag layout
		panel.setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints ipc = new java.awt.GridBagConstraints();
		ipc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		ipc.gridx = 0;
		ipc.gridy = 0;
		panel.add(ipLabel, ipc);
		
		ipc.gridx = 1;
		ipc.gridwidth = 2;
		panel.add(ipTextField, ipc);
		
		ipc.gridx = 0;
		ipc.gridy = 1;
		ipc.gridwidth = 1;
		ipc.insets = new java.awt.Insets(10, 0, 10, 0);
		panel.add(portLabel, ipc);
		
		ipc.gridx = 1;
		ipc.gridwidth = 2;
		panel.add(portTextField, ipc);
		
		ipc.gridx = 3;
		ipc.gridwidth = 1;
		ipc.ipadx = 30;
		ipc.insets = new java.awt.Insets(0, 30, 0, 50);
		panel.add(connectButton, ipc);
		
		ipc.gridx = 0;
		ipc.gridy = 2;
		ipc.insets = new java.awt.Insets(0, 0, 10, 0);
		panel.add(msgLabel, ipc);
		
		ipc.gridy = 3;
		ipc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		panel.add(msgTextField, ipc);
		
		ipc.gridy = 4;
		ipc.gridwidth = 1;
		panel.add(msgButton, ipc);
		
		ipc.gridy = 5;
		panel.add(csLabel, ipc);
		
		ipc.gridy = 6;
		ipc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		ipc.ipady = 400;
		panel.add(new javax.swing.JScrollPane(csTextArea), ipc);
		
		//Add components to frame
		frame.getContentPane().add(panel); 	 
		
		
		//Show frame
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
}
