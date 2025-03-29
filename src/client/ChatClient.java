package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private ClientGUI clientGUI;

    public ChatClient(String serverAddress, int port, String clientName, ClientGUI clientGUI) throws IOException {
        this.clientName = clientName;
        this.clientGUI = clientGUI;
        socket = new Socket(serverAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        // Send client name first
        out.println(clientName);
        
        // Start a thread to listen for messages from server
        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                clientGUI.appendMessage(message);
            }
        } catch (IOException e) {
            clientGUI.appendMessage("Error reading from server: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                clientGUI.appendMessage("Error closing connection: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}