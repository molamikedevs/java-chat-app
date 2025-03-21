# Java Chat Application

A simple Java-based chat application that allows multiple clients to connect to a central server and exchange messages in real-time. The application uses JavaFX for the user interface and socket programming for client-server communication.

---

## Features
- **Server**: Manages client connections and broadcasts messages to all connected clients.
- **Client**: Connects to the server, sends messages, and receives messages from other clients.
- **User Interface**: Built using JavaFX with a simple and intuitive design.
- **Styling**: Messages from the current user are displayed in one color, while messages from other users are displayed in another color for easy differentiation.

---

## Prerequisites
Before running the application, ensure the following are installed:
- **Java Development Kit (JDK) 11 or higher**.
- **JavaFX**: Included in JDK 11+ or available as a separate library.
- **Eclipse IDE** (or any other Java IDE).

---

## Project Structure
To set up the project, create the following folder and file structure:



### Folder and File Descriptions
1. **`src/client/ChatClient.java`**:
   - The client application that connects to the server, sends messages, and receives messages from other clients.
   - Uses JavaFX for the user interface.

2. **`src/server/ChatServer.java`**:
   - The server application that manages client connections and broadcasts messages to all connected clients.
   - Listens on port `12345` for incoming client connections.

3. **`src/utils/Constants.java`**:
   - Contains constants for the server address (`localhost`) and port (`12345`).

4. **`resources/styles.css`**:
   - Defines the styling for the chat interface, including colors for sent and received messages.

5. **`README.md`**:
   - This file, containing instructions for setting up and running the project.

---

## How to Set Up and Run the Application

### 1. Create the Project Structure
1. Create a folder named `java-chat-app`.
2. Inside `java-chat-app`, create the following subfolders:
   - `src/client`
   - `src/server`
   - `src/utils`
   - `resources`

### 2. Add the Source Files
1. **`ChatClient.java`**:
   - Place this file in the `src/client` folder.
   - This file contains the client application code.

2. **`ChatServer.java`**:
   - Place this file in the `src/server` folder.
   - This file contains the server application code.

3. **`Constants.java`**:
   - Place this file in the `src/utils` folder.
   - This file contains the server address and port constants.

4. **`styles.css`**:
   - Place this file in the `resources` folder.
   - This file defines the styling for the chat interface.

### 3. Import the Project into Eclipse
1. Open **Eclipse IDE**.
2. Go to **File > Import**.
3. Select **Existing Projects into Workspace** under the **General** folder.
4. Click **Next**, then browse to the `java-chat-app` folder.
5. Select the `java-chat-app` folder and click **Finish**.

### 4. Run the Server
1. In Eclipse, navigate to the `server` package (`src/server`).
2. Open the `ChatServer.java` file.
3. Right-click on the file and select **Run As > Java Application**.
   - The server will start and display the message: `Chat Server started...`.

### 5. Run the Client
1. In Eclipse, navigate to the `client` package (`src/client`).
2. Open the `ChatClient.java` file.
3. Right-click on the file and select **Run As > Java Application**.
   - A new chat client window will open.
   - Repeat this step to start multiple clients.

### 6. Start Chatting
- Each client will be assigned a unique user ID upon connecting to the server.
- Type messages in the input field and press `Enter` or click `Send` to broadcast them to all connected clients.
- Messages sent by the current user will appear as `You: <message>`.
- Messages received from other users will appear as `Client X: <message>`.

---

## Detailed Explanation of the Application

### Server Workflow
1. The server starts and listens for incoming client connections on port `12345`.
2. When a client connects, the server assigns a unique user ID to the client.
3. The server broadcasts messages from one client to all other connected clients.

### Client Workflow
1. The client connects to the server using the address and port defined in `Constants.java`.
2. The client sends messages to the server, which are then broadcasted to all other clients.
3. The client receives messages from other clients and displays them in the chat area.

### User Interface
- The chat interface is built using JavaFX.
- Messages sent by the current user are styled in one color (e.g., light gray).
- Messages received from other users are styled in another color (e.g., black).

---

## Troubleshooting

### Common Issues
1. **Server Not Starting**:
   - Ensure no other application is using port `12345`.
   - Check that the `ChatServer` class is running.

2. **Client Not Connecting**:
   - Ensure the server is running before starting the client.
   - Verify that the server address and port in `Constants.java` are correct.

3. **UI Layout Issues**:
   - If the input field and send button are not visible, ensure that the `TextFlow` is wrapped in a `ScrollPane` and that the layout is properly configured.

4. **Socket Errors**:
   - If you see `SocketException: Socket closed`, ensure that the server and client handle disconnections gracefully.


