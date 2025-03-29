package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton emojiButton;
    private JLabel userLabel;
    private ChatClient chatClient;
    private String username;

    public ClientGUI() {
        setTitle("Chat Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set default font for better visibility
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("Label.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 14));
        
        // Get username
        username = showUsernameDialog();
        if (username == null) {
            System.exit(0);
        }
        
        // Initialize chat client
        try {
            chatClient = new ChatClient("localhost", 1234, username, this);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error connecting to server:\n" + e.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // User info panel (north)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBackground(new Color(240, 240, 240));
        userLabel = new JLabel("Logged in as: " + username);
        userLabel.setForeground(new Color(33, 150, 243));
        userPanel.add(userLabel);
        
        // Chat area (center)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Chat Messages"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Input panel (south)
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(new Color(240, 240, 240));
        
        messageField = new JTextField();
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Emoji button with proper font
        emojiButton = new JButton("😊");
        emojiButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        emojiButton.setBackground(new Color(255, 193, 7));
        emojiButton.setForeground(Color.BLACK);
        emojiButton.setFocusPainted(false);
        emojiButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        emojiButton.addActionListener(e -> showEmojiPicker());
        
        // Send button
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(76, 175, 80));
        sendButton.setForeground(Color.BLACK);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendButton.addActionListener(e -> sendMessage());
        
        buttonPanel.add(emojiButton);
        buttonPanel.add(sendButton);
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Add components to main panel
        mainPanel.add(userPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // Add listeners
        messageField.addActionListener(e -> sendMessage());
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chatClient.disconnect();
            }
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    private String showUsernameDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Enter your username:"), BorderLayout.NORTH);
        
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(usernameField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            null, 
            panel, 
            "Chat Login", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = usernameField.getText().trim();
            return name.isEmpty() ? "Anonymous" : name;
        }
        return null;
    }
    
    private void showEmojiPicker() {
        // Using Unicode code points for emojis to ensure proper display
        String[] emojis = {
            new String(Character.toChars(0x1F60A)), // 😊
            new String(Character.toChars(0x1F602)), // 😂
            new String(Character.toChars(0x2764)),  // ❤
            new String(Character.toChars(0x1F44D)), // 👍
            new String(Character.toChars(0x1F44B)), // 👋
            new String(Character.toChars(0x1F389)), // 🎉
            new String(Character.toChars(0x1F60E)), // 😎
            new String(Character.toChars(0x1F914)), // 🤔
            new String(Character.toChars(0x1F64F)), // 🙏
            new String(Character.toChars(0x1F525))  // 🔥
        };
        
        JPanel emojiPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        for (String emoji : emojis) {
            JButton emojiBtn = new JButton(emoji);
            emojiBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            emojiBtn.addActionListener(e -> {
                messageField.setText(messageField.getText() + emoji);
                messageField.requestFocus();
            });
            emojiPanel.add(emojiBtn);
        }
        
        JOptionPane.showMessageDialog(this, emojiPanel, "Select Emoji", JOptionPane.PLAIN_MESSAGE);
    }
    
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatClient.sendMessage(message);
            messageField.setText("");
        }
        messageField.requestFocus();
    }
    
    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            // Different styling for system messages
            if (message.contains("has joined") || message.contains("has left")) {
                chatArea.append("[System] " + message + "\n");
            } else {
                chatArea.append(message + "\n");
            }
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel for consistent appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}