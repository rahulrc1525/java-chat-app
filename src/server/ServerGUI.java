package server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame {
    private JTextArea logArea;
    private JButton startButton;
    private JButton clearLogButton;
    private JLabel statusLabel;
    private ChatServer chatServer;

    public ServerGUI() {
        setTitle("Chat Server Control Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set default font for better visibility
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.font", new Font("Segoe UI", Font.BOLD, 14));
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Status panel (north)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel("Server Status: Not Running");
        statusLabel.setForeground(new Color(200, 0, 0));
        statusPanel.add(statusLabel);
        
        // Log area (center)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Server Log"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Button panel (south)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        // Create buttons with better contrast
        startButton = new JButton("Start Server");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.BLACK); // Changed to black for visibility
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        
        clearLogButton = new JButton("Clear Log");
        clearLogButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearLogButton.setBackground(new Color(33, 150, 243));
        clearLogButton.setForeground(Color.BLACK); // Changed to black for visibility
        clearLogButton.setFocusPainted(false);
        clearLogButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        buttonPanel.add(startButton);
        buttonPanel.add(clearLogButton);
        
        // Add components to main panel
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }

    private void startServer() {
        startButton.setEnabled(false);
        startButton.setText("Server Running");
        startButton.setBackground(new Color(56, 142, 60));
        statusLabel.setText("Server Status: Running");
        statusLabel.setForeground(new Color(0, 150, 0));
        
        chatServer = new ChatServer(this);
        new Thread(() -> chatServer.start()).start();
    }

    public void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now().withNano(0) + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel for consistent appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ServerGUI());
    }
}