package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int PORT = 1234;
    private List<ClientHandler> clients = new ArrayList<>();
    private ServerGUI serverGUI;

    public ChatServer(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverGUI.logMessage("Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                serverGUI.logMessage("New client connected: " + clientSocket);
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            serverGUI.logMessage("Server error: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        serverGUI.logMessage("Broadcasting: " + message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        serverGUI.logMessage("Client disconnected. Total clients: " + clients.size());
    }
}