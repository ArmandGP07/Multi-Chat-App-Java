package com.muc;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The type Message pane.
 */
// Message pane class //
public class MessagePane extends JPanel implements MessageListener {
    private final String login;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JTextField inputField = new JTextField();

    /**
     * Instantiates a new Message pane.
     *
     * @param client the client
     * @param login  the login
     */
    public MessagePane(ChatClient client, String login) {
        this.login = login;

        client.addMessageListener(this);

        setLayout(new BorderLayout());
        JList<String> messageList = new JList<>(listModel);
        add(new JScrollPane(messageList), BorderLayout.CENTER);

        // Message text box //
        add(inputField, BorderLayout.SOUTH);

        // Event listener ofr input field //
        inputField.addActionListener(e -> {

            // Sending message to client //
            try {
                String text = inputField.getText();
                client.msg(login, text);

                // Add text to conversation //
                listModel.addElement("You: " + text);

                // Empty text field reset //
                inputField.setText("");

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        if (login.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + ": " + msgBody;
            listModel.addElement(line);
        }
    }
}
