package assignment7;

public class ClientMain {
	public static void main(String[] args) {
		try {
			Client a = new Client("A");
			//Client b = new Client("B");
			//Client c = new Client("C");
			a.run();
			//b.run();
			//c.run();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		}
	} 
}
