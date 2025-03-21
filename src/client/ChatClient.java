package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import java.io.*;
import java.net.*;
import java.net.URL;
import utils.Constants; // Import the Constants class

public class ChatClient extends Application {
    private Socket socket; // Socket for the server connection
    private PrintWriter out; // Output stream to send messages to the server
    private BufferedReader in; // Input stream to receive messages from the server
    private TextFlow chatArea; // UI component to display chat messages
    private TextField inputField; // UI component for user input
    private int userId; // Unique ID assigned by the server

    @Override
    public void start(Stage primaryStage) {
        // Set up the UI layout
        VBox root = new VBox(10);
        chatArea = new TextFlow();
        chatArea.setLineSpacing(5);

        // Add a scroll pane to the chat area
        ScrollPane scrollPane = new ScrollPane(chatArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        scrollPane.vvalueProperty().bind(chatArea.heightProperty());

        // Set up the input field and send button
        inputField = new TextField();
        Button sendButton = new Button("Send");

        HBox inputBox = new HBox(5, inputField, sendButton);
        root.getChildren().addAll(scrollPane, inputBox);
        Scene scene = new Scene(root, 400, 300);

        // Load CSS for styling (if available)
        URL cssUrl = getClass().getResource("/styles.css");
        if (cssUrl == null) {
            System.err.println("CSS file not found!");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Set up the primary stage
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set up event handlers for sending messages
        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        // Connect to the server
        connectToServer();
    }

    /**
     * Sends a message to the server.
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // Display the sent message in the chat area
            Text sentMessage = new Text("You: " + message + "\n");
            sentMessage.getStyleClass().add("my-message");
            chatArea.getChildren().add(sentMessage);

            // Send the message to the server
            out.println(message);
            inputField.clear();
        }
    }

    /**
     * Connects to the server and initializes communication.
     */
    private void connectToServer() {
        try {
            // Connect to the server using the address and port from Constants
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Read the user ID assigned by the server
            String userIdMessage = in.readLine();
            if (userIdMessage.startsWith("Your user ID is: ")) {
                userId = Integer.parseInt(userIdMessage.substring("Your user ID is: ".length()));
                Platform.runLater(() -> {
                    // Display the connection message in the chat area
                    Text connectedMessage = new Text("Connected to server. Your user ID is: " + userId + "\n");
                    chatArea.getChildren().add(connectedMessage);
                });
            }

            // Start a thread to listen for messages from the server
            Thread readerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        String finalServerMessage = serverMessage;
                        Platform.runLater(() -> {
                            // Display received messages in the chat area
                            Text receivedMessage = new Text(finalServerMessage + "\n");
                            receivedMessage.getStyleClass().add("other-message");
                            chatArea.getChildren().add(receivedMessage);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                // Display an error message if the connection fails
                Text errorMessage = new Text("Failed to connect to the server. Please ensure the server is running and try again.\n");
                chatArea.getChildren().add(errorMessage);
            });
        }
    }

    @Override
    public void stop() {
        // Clean up resources when the application stops
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}