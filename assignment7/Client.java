package assignment7;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets; 
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField; 
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage; 


public class Client extends Application{
	//private JTextArea incoming; 
	//private JTextField outgoing; 
	private BufferedReader reader; 
	private PrintWriter writer;
	//private String clientnm;
	private static int clients;
	private int recipient = 4;
	private int sender = 4;
	private int clientid;
	private TextArea ta;
	private TextField tf;
	
	// IO streams 
	DataOutputStream toServer = null; 
	DataInputStream fromServer = null;
	
	public Client(){
		//clientid = clients;
		//clients++;
	}
	
	public Client(String nm){
		//clientnm = nm;
		//clientid = clients;
		//clients++;
	}
	
	public int getClientID(){
		return clientid;
	}

	@Override // Override the start method in the Application class 
	public void start(Stage primaryStage) { 
		try{
			setUpNetworking();
			//clientnm = "Client " + clientid;
		}catch(Exception e){
			System.out.println("Error in setUpNetworking()");
		}
		
		// Top chat selection
		Pane top = new Pane();		
		
		//Friends Online
        Label friends = new Label("Friends: ");
        friends.setLayoutX(10);
        friends.setLayoutY(10);
        friends.setPrefWidth(50);
        top.getChildren().add(friends);
        
        //Chatting With
        Label curr = new Label("Chatting with: ");
        curr.setLayoutX(10);
        curr.setLayoutY(40);
        top.getChildren().add(curr);
		
		//get valid Online Friends
        int xCoord = 60;
        ArrayList<String> names = new ArrayList<String>();
        names.add("Client 0");
        names.add("Client 1");
        names.add("Client 2");
        
        ArrayList<RadioButton> radios = new ArrayList<RadioButton>();
        for(String x : names)
        {
        	if(x.equals("Client " + clientid))
			{
        		
			}
        	else
        	{
        		RadioButton cl = new RadioButton();
        		radios.add(cl);
    			cl.setText(x);
    	        cl.setPrefWidth(65);
    	        cl.setLayoutX(xCoord);
    	        cl.setLayoutY(10);
    	        xCoord += 75;
    	      //cl.setDisable(true);
    	        
    	        top.getChildren().add(cl);
        	}
        }
        
        for(RadioButton y : radios)
        {
        	y.setOnAction(new EventHandler<ActionEvent>() {
        		 
                @Override
                public void handle(ActionEvent event) {
                   curr.setText("Chatting with: " + updateActive(radios));
                }
            });
        }
        
        //Log Off Button  
        Button quit = new Button();
        quit.setText("Log Out");
        quit.setPrefWidth(80);
        quit.setLayoutX(360);
        quit.setLayoutY(10);
        top.getChildren().add(quit);
        
        //Quit Handle: quit program
        quit.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
               System.exit(0);
            }
        });
		
// Center text area
		BorderPane mainPane = new BorderPane(); 
		ta = new TextArea();		
		
// Bottom text input - Panel p to hold the label and text field 
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5, 5, 5, 5)); 
		paneForTextField.setStyle("-fx-border-color: red"); 
		tf = new TextField(); 
		tf.setPromptText("Send Message...");
		paneForTextField.setCenter(tf); 
		
		//areas for displaying content
		mainPane.setTop(top);
		mainPane.setCenter(new ScrollPane(ta));
		mainPane.setBottom(paneForTextField);

		// Create a scene and place it in the stage 
		Scene scene = new Scene(mainPane, 450, 220); 
		primaryStage.setTitle("Client " + clientid); // Set the stage title 
		primaryStage.setScene(scene); // Place the scene in the stage 
		primaryStage.show(); // Display the stage 

		tf.setOnAction(e -> { 
			try { 
				writer.println(recipient + "" + sender + "" + clientid + ": " + tf.getText()); 
				writer.flush();
				tf.setText(""); 
				tf.requestFocus();
			} 
			catch (Exception ex) { 
				System.err.println(ex); 
			} 
		}); 	
	}
	
	public String updateActive(ArrayList<RadioButton> all)
	{
		String active = "";
		int count = 0;
		for(RadioButton temp : all)
		{
			if(temp.isSelected())
			{
				count++;
				active = active + temp.getText() + " + "; 
				if(count > 1){
					recipient = 3;
				}
				else{
					String s = temp.getText();
					s = s.substring(s.length()-1,s.length());
					recipient = Integer.parseInt(s);
				}
			}	
		}
		if(!active.equals(""))
		{
			active = active.substring(0,active.length()-3);
		}
		return active;
	}
	
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource") Socket sock = new Socket("127.0.0.1", 4242); 
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream()); 
		System.out.println("networking established"); 
		String s = reader.readLine();
		clientid = Integer.parseInt(s);
		sender = clientid;
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
	}
	
	class IncomingReader implements Runnable {
		public void run() { 
			String message; 
			try {
				while ((message = reader.readLine()) != null) {
					ta.appendText(message + "\n");
				}
			} 
			catch (IOException ex) { 
				ex.printStackTrace(); 
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
