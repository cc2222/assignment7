package assignment7;

import assignment7.ServerMain.ClientHandler;

public class ClientMain {
	public static void main(String[] args) {
		try {
			Client a = new Client("A");
			Client b = new Client("B");
			Client c = new Client("C");
			a.main(args);
			b.main(args);
			c.main(args);
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		}
	} 
}
