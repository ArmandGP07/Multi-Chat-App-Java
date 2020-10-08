package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The type Chat client.
 */
// Chat client class for interface with the server //
public class ChatClient {

    // Server name, server port, socket, server in, server out and buffered in fields //
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private final ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private final ArrayList<MessageListener> messageListeners = new ArrayList<>();

    /**
     * Instantiates a new Chat client.
     *
     * @param serverName the server name
     * @param serverPort the server port
     */
    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
// Client instance //
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener((fromLogin, msgBody) -> System.out.println("You got a message from " + fromLogin + " ===>" + msgBody));

        if (!client.connect()) {
            System.err.println("Connect failed.");
        } else {
            System.out.println("Connect successful");

            // client login //
            if (client.login("guest", "guest")) {
                System.out.println("Login successful");

                client.msg("jim", "Hello World!");
            } else {
                System.err.println("Login failed");
            }

            //client.logoff();
        }
    }

    /**
     * Msg.
     *
     * @param sendMsgTo the send msg to
     * @param msgBody   the msg body
     * @throws IOException the io exception
     */
    public void msg(String sendMsgTo, String msgBody) throws IOException {
        String cmd = "msg " + sendMsgTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    /**
     * Login boolean.
     *
     * @param login    the login
     * @param password the password
     * @return the boolean
     * @throws IOException the io exception
     */
// API login Method //
    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    // Message Reader method //
    private void startMessageReader() {
        Thread t = new Thread(this::readMessageLoop);
        t.start();
    }

    // Message reading loop method //
    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method for handling messages //
    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    // Method for handling offline status //
    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    // Method for handling online status //
    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    /**
     * Connect boolean.
     *
     * @return the boolean
     */
// Connect method for connecting to the server using a socket //
    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            InputStream serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add user status listener.
     *
     * @param listener the listener
     */
// Adding user status listener method //
    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    /**
     * Add message listener.
     *
     * @param listener the listener
     */
// Adding message listener method //
    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

}
