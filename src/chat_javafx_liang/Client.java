package chat_javafx_liang;

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


public class Client extends Application { 
	
	public static int cnt;
	private String name;
	
	// IO streams 
	DataOutputStream toServer = null; 
	DataInputStream fromServer = null;
	
	
	public Client(){
		
	}
	public Client(String nm)
	{
		name = nm;
	}

	@Override // Override the start method in the Application class 
	public void start(Stage primaryStage) { 
		
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
        names.add("Client A");
        names.add("Client B");
        names.add("Client C");
        
        ArrayList<RadioButton> radios = new ArrayList<RadioButton>();
        for(String x : names)
        {
//        	if(x.equals(name))
//			{
//        		names.remove(x);
//			}
//        	else
//        	{
        		RadioButton cl = new RadioButton();
        		radios.add(cl);
    			cl.setText(x);
    	        cl.setPrefWidth(65);
    	        cl.setLayoutX(xCoord);
    	        cl.setLayoutY(10);
    	        xCoord += 75;
    	      //cl.setDisable(true);
    	        
    	        top.getChildren().add(cl);
//        	}
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
		TextArea ta = new TextArea();		
		
// Bottom text input - Panel p to hold the label and text field 
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5, 5, 5, 5)); 
		paneForTextField.setStyle("-fx-border-color: red"); 
		TextField tf = new TextField(); 
		tf.setPromptText("Send Message...");
		paneForTextField.setCenter(tf); 
		
		//areas for displaying content
		mainPane.setTop(top);
		mainPane.setCenter(new ScrollPane(ta));
		mainPane.setBottom(paneForTextField);

		// Create a scene and place it in the stage 
		Scene scene = new Scene(mainPane, 450, 220); 
		primaryStage.setTitle("Client " + name); // Set the stage title 
		primaryStage.setScene(scene); // Place the scene in the stage 
		primaryStage.show(); // Display the stage 

		tf.setOnAction(e -> { 
			try { 
				// Get the radius from the text field 
				double radius = Double.parseDouble(tf.getText().trim()); 

				// Send the radius to the server 
				toServer.writeDouble(radius); 
				toServer.flush(); 

				// Get area from the server 
				double area = fromServer.readDouble(); 

				// Display to the text area 
				ta.appendText("Radius is " + radius + "\n"); 
				ta.appendText("Area received from the server is "
						+ area + '\n');

			} 
			catch (IOException ex) { 
				System.err.println(ex); 
			} 
		}); 

		try { 
			// Create a socket to connect to the server 
			@SuppressWarnings("resource")
			Socket socket = new Socket("localhost", 8000); 
			// Socket socket = new Socket("130.254.204.36", 8000); 
			// Socket socket = new Socket("drake.Armstrong.edu", 8000); 

			// Create an input stream to receive data from the server 
			fromServer = new DataInputStream(socket.getInputStream()); 

			// Create an output stream to send data to the server 
			toServer = new DataOutputStream(socket.getOutputStream()); 
		} 
		catch (IOException ex) { 
			ta.appendText(ex.toString() + '\n');
		}
	}
	
	public String updateActive(ArrayList<RadioButton> all)
	{
		String active = "";
		for(RadioButton temp : all)
		{
			if(temp.isSelected())
			{
				active = active + temp.getText() + " + ";  
			}	
		}
		if(!active.equals(""))
		{
			active = active.substring(0,active.length()-3);
		}
		return active;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
