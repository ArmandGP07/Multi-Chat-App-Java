package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

// User list pane class //
public class UserListPane extends JPanel implements UserStatusListener {

    // Client, JList and DefaultListModel fields //
    private final ChatClient client;
    private JList <String> userListUI;
    private DefaultListModel <String> userListModel;


    // User list pane's client constructor //
    public UserListPane(ChatClient client) {
        this.client = client;
        this.client.addUserStatusListener(this);

        // UI //
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        // Mouse event for selecting users //
        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = userListUI.getSelectedValue();
                    MessagePane messagePane = new MessagePane(client, login);

                    // Message pane window effect //
                    JFrame f = new JFrame("Message: " + login);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 500);
                    f.getContentPane().add(messagePane, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);

        UserListPane userListPane = new UserListPane(client);

        // User list window //
        JFrame frame = new JFrame("User list");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window's width and height //
        frame.setSize(400, 600);

        frame.getContentPane().add(userListPane, BorderLayout.CENTER);

        // Make window visible //
        frame.setVisible(true);

        if (client.connect()) {
            try {
                client.login("Bruce", "Bruce");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Online method callback //
    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    // Offline method callback //
    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
