package com.muc;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The type Login window.
 */
// Login window class //
public class LoginWindow extends JFrame {

    // Login, password, login button and client field //
    private final ChatClient client;
    /**
     * The Login field.
     */
    JTextField loginField = new JTextField();
    /**
     * The Password field.
     */
    JPasswordField passwordField = new JPasswordField();
    /**
     * The Login button.
     */
    JButton loginButton = new JButton("Login");

    /**
     * Instantiates a new Login window.
     */
    public LoginWindow() {
        super("Login");

        this.client = new ChatClient("localhost", 8818);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // login window and adding login, password and login button fields //
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(loginField);
        p.add(passwordField);
        p.add(loginButton);

        // Login button action //
        loginButton.addActionListener(e -> doLogin());

        getContentPane().add(p, BorderLayout.CENTER);

        pack();

        // Set window visible //
        setVisible(true);

    }

    // Method ofr doing login //
    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {

                UserListPane userListPane = new UserListPane(client);

                // User list window //
                JFrame frame = new JFrame("User list");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Window's width and height //
                frame.setSize(400, 600);

                frame.getContentPane().add(userListPane, BorderLayout.CENTER);

                // Make window visible //
                frame.setVisible(true);
                // Bring up user list window //
                setVisible(false);

            } else {

                // Show error message //
                JOptionPane.showMessageDialog(this, "Invalid login/password.");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        LoginWindow loginWin = new LoginWindow();
        loginWin.setVisible(true);
    }
}
