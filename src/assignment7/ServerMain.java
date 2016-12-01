/* ServerMain.java
 * EE422C Project 7 submission by
 * Casey Cotter
 * cbc2298
 * 16445
 * Max Fennis
 * maf3743
 * 16450
 * Slip days used: <1>
 * Fall 2016
 */

package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain {
	//necessary fields
	private ArrayList<PrintWriter> clientOutputStreams;
	private ArrayList<BufferedReader> clientInputStreams;
	private int clients = 0;
	private int recipient;
	private int sender;

	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		//lists that hold each clients input/output stream
		clientOutputStreams = new ArrayList<PrintWriter>();
		clientInputStreams = new ArrayList<BufferedReader>();
		
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			//look for client connecting to server
			Socket clientSocket = serverSock.accept();
			
			//creates and stores a writer/reader into the lists
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clientOutputStreams.add(writer);
			clientInputStreams.add(reader);
			
			//send the client id to the new client
			writer.println(clients);
			writer.flush();
			clients++;
			
			//start the clienthandler
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("got a connection");
		}
	}

	private void notifyClients(String message) {
		//remove the first two characters that contain the recipient and sender
		String s = message.substring(0,2);
		message = message.substring(2,message.length());
		
		//if group message, send message to all clients
		if(recipient == 3){
			for (PrintWriter writer : clientOutputStreams) {
				writer.println(s + "Client " + message);
				writer.flush();
			}
			return;
		}	
		
		//if not a group message, send the message only to the sender and recipient
		for(int a = 0; a < clientOutputStreams.size(); a++){
			if(a == recipient || a == sender){
				PrintWriter writer = clientOutputStreams.get(a);
				writer.println(s + "Client " + message);
				writer.flush();
			}
		}
		
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					//first char is the recipient
					String s = message.substring(0,1);
					recipient = Integer.parseInt(s);
					
					//second char is the sender
					s = message.substring(1,2);
					sender = Integer.parseInt(s);
					
					//s is the message to be sent
					s  = message.substring(2,message.length());
					System.out.println("read " + s);			
					notifyClients(message);
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}