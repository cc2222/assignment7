/* ClientMain.java
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
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage; 


public class ClientMain extends Application{
	//necessary fields
	private BufferedReader reader; 
	private PrintWriter writer;
	private static int clients;
	private int recipient = 4;
	private int sender = 4;
	private int clientid;
	private TextArea ta;
	private TextField tf;
	private ArrayList<TextArea> allTA;
	private BorderPane mainPane;
	private TextArea tA, tG, tB, tCurr;
	private ScrollPane tap, tAp, tBp, tGp;
	private boolean both;
	
	public ClientMain(){

	}
	
	//return the clients id #
	public int getClientID(){
		return clientid;
	}

	@Override // Override the start method in the Application class 
	public void start(Stage primaryStage) { 
		try{
			setUpNetworking();
		}catch(Exception e){
			System.out.println("Error in setUpNetworking()");
		}
		
		// Top chat selection
		Pane top = new Pane();		
		
		//Friends Online
        Label friends = new Label("Friends: ");
        friends.setLayoutX(10);
        friends.setLayoutY(10);
        friends.setPrefWidth(60);
        top.getChildren().add(friends);
        
        //Chatting With
        Label curr = new Label("Chatting with: ");
        curr.setLayoutX(10);
        curr.setLayoutY(40);
        top.getChildren().add(curr);
		
		//get valid Online clients
        int xCoord = 80;
        ArrayList<String> names = new ArrayList<String>();
        names.add("Client 0");
        names.add("Client 1");
        names.add("Client 2");
        
        //chatroom selectors
        ArrayList<RadioButton> radios = new ArrayList<RadioButton>();
        for(String x : names){
        	if(!(x.equals("Client " + clientid))){
        		RadioButton cl = new RadioButton();
        		radios.add(cl);
    			cl.setText(x);
    	        cl.setPrefWidth(85);
    	        cl.setLayoutX(xCoord);
    	        cl.setLayoutY(10);
    	        xCoord += 85;
    	        top.getChildren().add(cl);
        	}
        }
        
        //Log Off Button  
        Button quit = new Button();
        quit.setText("Log Out");
        quit.setPrefWidth(80);
        quit.setLayoutX(460);
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
        mainPane = new BorderPane();
		ta = new TextArea();
		tA = new TextArea();
		tB = new TextArea();
		tG = new TextArea();
		tap = new ScrollPane(ta);
		tAp = new ScrollPane(tA);
		tBp = new ScrollPane(tB);
		tGp = new ScrollPane(tG);
		
		
// Bottom text input - Panel p to hold the label and text field 
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5, 5, 5, 5)); 
		paneForTextField.setStyle("-fx-border-color: red"); 
		tf = new TextField(); 
		tf.setPromptText("Send Message...");
		paneForTextField.setCenter(tf); 
		
		//areas for displaying content
		mainPane.setTop(top);
		mainPane.setCenter(tap);
		mainPane.setBottom(paneForTextField);

		// Create a scene and place it in the stage 
		Scene scene = new Scene(mainPane, 550, 320); 
		primaryStage.setTitle("Client " + clientid); // Set the stage title 
		primaryStage.setScene(scene); // Place the scene in the stage 
		primaryStage.show(); // Display the stage 

		tf.setOnAction(e -> { 
			try { 
				if(clientid == 0){
					if(recipient == 1){
						tA.appendText("Client " + clientid + ": " + tf.getText() + "\n");
					}
					if(recipient == 2){
						tB.appendText("Client " + clientid + ": " + tf.getText() + "\n");
					}
				}
				else if(clientid == 1){
					if(recipient == 0){
						tA.appendText("Client " + clientid + ": " + tf.getText() + "\n");
					}
					if(recipient == 2){
						tB.appendText("Client " + clientid + ": " + tf.getText() + "\n");
					}
				}
				else if(clientid == 2){
					if(recipient == 0){
						tA.appendText("Client " + clientid + ": " + tf.getText() + "\n");
					}
					if(recipient == 1){
						tB.appendText("Client " + clientid + ": " + tf.getText() + "\n");
					}
				}
				
				writer.println(recipient + "" + sender + "" + clientid + ": " + tf.getText()); 
				writer.flush();
				
				tf.setText(""); 
				tf.requestFocus();
			} 
			catch (Exception ex) { 
				System.err.println(ex); 
			} 
		});
		
		for(RadioButton y : radios){
        	y.setOnAction(new EventHandler<ActionEvent>() {
        		 
                @Override
                public void handle(ActionEvent event) {
                   curr.setText("Chatting with: " + updateActive(radios));
                   
                }
            });
        }
	}
	
	public String updateActive(ArrayList<RadioButton> all){
		String active = "";
		int count = 0;
		both = false;
		for(RadioButton temp : all){
			if(temp.isSelected()){
				count++;
				active = active + temp.getText() + " + ";
				
				if(clientid == 0){
					if(temp.getText().contains("1")){
						mainPane.setCenter(tAp);
						
					}
					if(temp.getText().contains("2")){
						mainPane.setCenter(tBp);
					}
				}
				else if(clientid == 1){
					if(temp.getText().contains("0")){
						mainPane.setCenter(tAp);
					}
					if(temp.getText().contains("2")){
						mainPane.setCenter(tBp);
					}
				}
				else if(clientid == 2){
					if(temp.getText().contains("0")){
						mainPane.setCenter(tAp);
					}
					if(temp.getText().contains("1")){
						mainPane.setCenter(tBp);
					}
				}
				
				if(count > 1){
					both = true;
					recipient = 3;
				}
				else{
					String s = temp.getText();
					s = s.substring(s.length()-1,s.length());
					recipient = Integer.parseInt(s);
				}
			}	
		}
		
		//grouptext
		if(both == true){
			mainPane.setCenter(tGp);
		}
		
		if(!active.equals("")){
			active = active.substring(0,active.length()-3);
			return active;
		}
     	mainPane.setCenter(tap);
		return active;
	}
	
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource") Socket sock = new Socket("127.0.0.1", 4242); 
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader); 
		writer = new PrintWriter(sock.getOutputStream()); 
		System.out.println("networking established"); 
		
		//gets the client id from server
		String s = reader.readLine();
		clientid = Integer.parseInt(s);
		sender = clientid;
		
		//creates the reader for the client
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
	}
	
	class IncomingReader implements Runnable {
		public void run() { 
			String message; 
			try {
				while ((message = reader.readLine()) != null) {
					String s = message.substring(0,2); // 1st recip (0-3), 2nd sender (0-2)
					message = message.substring(2,message.length());
					
					//check to see which text area to append to
					if(s.substring(0,1).equals("3")){
						tG.appendText(message + "\n");
						
					}
					else{
						if(clientid == 0){
							if(s.substring(1,2).equals("1")){
								tA.appendText(message + "\n");
								
							}
							if(s.substring(1,2).equals("2")){
								tB.appendText(message + "\n");
							}
						}
						else if(clientid == 1){
							if(s.substring(1,2).equals("0")){
								tA.appendText(message + "\n");
							}
							if(s.substring(1,2).equals("2")){
								tB.appendText(message + "\n");
							}
						}
						else if(clientid == 2){
							if(s.substring(1,2).equals("0")){
								tA.appendText(message + "\n");
							}
							if(s.substring(1,2).equals("1")){
								tB.appendText(message + "\n");
							}
						}
					}
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
