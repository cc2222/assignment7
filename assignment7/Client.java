package assignment7;

import java.io.*; 
import java.net.*; 
import javax.swing.*; 
import java.awt.*; 
import java.awt.event.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

//import assignment7.ClientMain.IncomingReader;
//import assignment7.ClientMain.SendButtonListener;

public class Client {
	private JTextArea incoming; 
	private JTextField outgoing; 
	private BufferedReader reader; 
	private PrintWriter writer;
	private String clientnm;
	
	public Client(String nm){
		clientnm = nm;
	}
	
	public void run() throws Exception {
		initView(); 
		setUpNetworking();
	} 

	private void initView() {
		JFrame frame = new JFrame("Client " + clientnm); 
		JPanel mainPanel = new JPanel(); 
		incoming = new JTextArea(15, 50); 
		incoming.setLineWrap(true); 
		incoming.setWrapStyleWord(true); 
		incoming.setEditable(false); 
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		outgoing = new JTextField(20); 
		JButton sendButton = new JButton("Send"); 
		sendButton.addActionListener(new SendButtonListener()); 
		mainPanel.add(qScroller); 
		mainPanel.add(outgoing); 
		mainPanel.add(sendButton); 
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel); 
		frame.setSize(650, 500); 
		frame.setVisible(true);
	} 
	
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource") Socket sock = new Socket("127.0.0.1", 4242); 
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream()); 
		System.out.println("networking established"); 
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
	}
	
	class SendButtonListener implements ActionListener { 
		public void actionPerformed(ActionEvent ev) {
			writer.println(clientnm + ": " + outgoing.getText()); 
			writer.flush();
			outgoing.setText(""); 
			outgoing.requestFocus();
		}
	}
	
	class IncomingReader implements Runnable {
		public void run() { 
			String message; 
			try {
				while ((message = reader.readLine()) != null) {
					incoming.append(message + "\n"); 
				}
			} 
			catch (IOException ex) { 
				ex.printStackTrace(); 
			}
		}
	}
}
