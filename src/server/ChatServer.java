package server;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import utils.Constants;

public class ChatServer {

    // A thread-safe set to store all connected client handlers
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat Server started...");

        // Start the server on the specified port
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) {
            // Continuously listen for new client connections
            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");

                // Create a new handler for the client and add it to the set
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                // Start the client handler thread
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     *
     * @param message The message to broadcast.
     * @param sender  The client handler that sent the message.
     */
    private static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                if (handler != sender) {
                    handler.sendMessage(message);
                }
            }
        }
    }

    /**
     * Inner class to handle communication with a single client.
     */
    private static class ClientHandler extends Thread {
        private Socket socket; // Socket for the client connection
        private PrintWriter out; // Output stream to send messages to the client
        private BufferedReader in; // Input stream to receive messages from the client
        private int userId; // Unique ID for the client

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Sends a message to the client.
         *
         * @param message The message to send.
         */
        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            try {
                // Initialize input and output streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Assign a unique user ID to the client
                userId = clientHandlers.size();
                out.println("Your user ID is: " + userId);

                // Continuously read messages from the client
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from user " + userId + ": " + message);

                    // Broadcast the received message to all other clients
                    broadcastMessage("Client " + userId + ": " + message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Clean up resources when the client disconnects
                cleanup();
            }
        }

        /**
         * Cleans up resources when the client disconnects.
         */
        private void cleanup() {
            try {
                // Remove the client handler from the set and close the socket
                if (out != null) clientHandlers.remove(this);
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client " + userId + " disconnected.");
        }
    }
}