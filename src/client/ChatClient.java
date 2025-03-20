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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private TextFlow chatArea;
    private TextField inputField;
    private int userId;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        chatArea = new TextFlow();
        chatArea.setLineSpacing(5);

        ScrollPane scrollPane = new ScrollPane(chatArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        scrollPane.vvalueProperty().bind(chatArea.heightProperty());

        inputField = new TextField();
        Button sendButton = new Button("Send");

        HBox inputBox = new HBox(5, inputField, sendButton);
        root.getChildren().addAll(scrollPane, inputBox);
        Scene scene = new Scene(root, 400, 300);

        URL cssUrl = getClass().getResource("/styles.css");
        if (cssUrl == null) {
            System.err.println("CSS file not found!");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        connectToServer();
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            Text sentMessage = new Text("You: " + message + "\n");
            sentMessage.getStyleClass().add("my-message");
            chatArea.getChildren().add(sentMessage);

            out.println(message);
            inputField.clear();
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket(Constants.SERVER_ADDRESS, Constants.SERVER_PORT); // Use Constants
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String userIdMessage = in.readLine();
            if (userIdMessage.startsWith("Your user ID is: ")) {
                userId = Integer.parseInt(userIdMessage.substring("Your user ID is: ".length()));
                Platform.runLater(() -> {
                    Text connectedMessage = new Text("Connected to server. Your user ID is: " + userId + "\n");
                    chatArea.getChildren().add(connectedMessage);
                });
            }

            Thread readerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        String finalServerMessage = serverMessage;
                        Platform.runLater(() -> {
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
                Text errorMessage = new Text("Failed to connect to the server. Please ensure the server is running and try again.\n");
                chatArea.getChildren().add(errorMessage);
            });
        }
    }

    @Override
    public void stop() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}