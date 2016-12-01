package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain {
	
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
		clientOutputStreams = new ArrayList<PrintWriter>();
		clientInputStreams = new ArrayList<BufferedReader>();
		
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clientOutputStreams.add(writer);
			clientInputStreams.add(reader);
			writer.println(clients);
			writer.flush();
			clients++;
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("got a connection");
		}
	}

	private void notifyClients(String message) {
		if(recipient == 3){
			for (PrintWriter writer : clientOutputStreams) {
				writer.println("Client " + message);
				writer.flush();
			}
			return;
		}	
		for(int a = 0; a < clientOutputStreams.size(); a++){
			if(a == recipient || a == sender){
				PrintWriter writer = clientOutputStreams.get(a);
				writer.println("Client " + message);
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
					String s = message.substring(0,1);
					recipient = Integer.parseInt(s);
					s = message.substring(1,2);
					sender = Integer.parseInt(s);
					message = message.substring(2,message.length());
					System.out.println("read " + message);			
					notifyClients(message);
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}