package server;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import utils.Constants; // Import the Constants class

public class ChatServer {
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat Server started...");
        try (ServerSocket serverSocket = new ServerSocket(Constants.SERVER_PORT)) { // Use Constants
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(String message, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler handler : clientHandlers) {
                if (handler != sender) {
                    handler.sendMessage(message);
                }
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int userId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                userId = clientHandlers.size();
                out.println("Your user ID is: " + userId);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from user " + userId + ": " + message);
                    broadcastMessage("Client " + userId + ": " + message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cleanup();
            }
        }

        private void cleanup() {
            try {
                if (out != null) clientHandlers.remove(this);
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Client " + userId + " disconnected.");
        }
    }
}