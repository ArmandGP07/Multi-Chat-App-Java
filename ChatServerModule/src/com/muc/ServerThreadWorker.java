package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

// Server class for handling the thread for multiple connections //
public class ServerThreadWorker extends Thread{

    private final Socket clientSocket;
    private final ServerConnections serverConnections;
    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();

    public ServerThreadWorker(ServerConnections serverConnections, Socket clientSocket) {
        this.serverConnections = serverConnections;
        this.clientSocket = clientSocket;
    }

    // Run method for thread //
    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method for handling client socket //
    private void handleClientSocket() throws IOException, InterruptedException {

        // Input stream for sending client data //
        InputStream inputStream = clientSocket.getInputStream();

        // Output stream for reading client data //
        clientSocket.getOutputStream();
        this.outputStream = clientSocket.getOutputStream();

        // Buffer reader //
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {

            // Split line in individual tokens //
            String[] tokens = StringUtils.split(line);

            // if method for not causing null pointer exceptions //
            if (tokens != null && tokens.length > 0) {

                // First token //
                String cmd = tokens[0];

                // Logoff command //
                if ("logoff".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;

                // Login command //
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);

                // Message command //
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);

                // Join topic/group chat command //
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);

                // Leave topic/group chat command //
                } else if ("leave".equalsIgnoreCase(cmd)){
                    handleLeave(tokens);

                } else {

                    // Unknown string for not identifying command //
                   String msg = "unknown " + cmd + "\n";
                   outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    // Method for leaving topic/group chat //
    private void handleLeave(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);
        }
    }

    // Boolean for testing if user is member of topic/group chat //
    public boolean memberOfTopic (String topic) {
        return topicSet.contains(topic);
    }

    // Method for handling Join group chat/topic //
    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
        }
    }

    // Method for handling message command //
    private void handleMessage(String[] tokens) throws IOException {
        String sendMsgTo = tokens[1];
        String msgBody = tokens[2];

        boolean isTopic = (sendMsgTo.charAt(0) == '#');

        // Iterating through list of server thread worker //
        List<ServerThreadWorker> threadWorkerList = serverConnections.getThreadWorkerList();

        // Sending messages ability //
        for (ServerThreadWorker threadWorker : threadWorkerList) {
            if (isTopic) {
                if (threadWorker.memberOfTopic(sendMsgTo)) {

                    // Group chat/topic format //
                    String outMsg = "msg " + sendMsgTo + ": " + login + " " + msgBody + "\n";
                    threadWorker.send(outMsg);
                }
            }else {
                if (sendMsgTo.equalsIgnoreCase(threadWorker.getLogin())) {

                    // One-to-one message format //
                    String outMsg = "msg " + login + " " + msgBody + "\n";
                    threadWorker.send(outMsg);
                }
            }
        }
    }

    // Logoff method //
    private void handleLogoff() throws IOException {
        serverConnections.removeThreadWorker(this);

        // Iterating through list of server thread worker //
        List<ServerThreadWorker> threadWorkerList = serverConnections.getThreadWorkerList();

        // Send other online users current user's status (offline) //
        String onlineMessage = "offline " + login + "\n";
        for (ServerThreadWorker threadWorker : threadWorkerList) {
            if (!login.equals(threadWorker.getLogin())) {
                threadWorker.send(onlineMessage);
            }
        }
        clientSocket.close();
    }

    // Expose login to Server Thread Workers //
    public String getLogin() {
        return login;
    }

    // Handle Login method //
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
           String login = tokens[1];
           String password = tokens[2];

           // Login accepted //
            if ((login.equals("guest") && password.equals("guest")) || (login.equals("armando") && password.equals("armando"))) {
                String message = "login successful\n";
                outputStream.write(message.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                // Iterating through list of server thread worker //
                List<ServerThreadWorker> threadWorkerList = serverConnections.getThreadWorkerList();

                // Send current user all other online logins //
                for (ServerThreadWorker threadWorker : threadWorkerList) {
                    if (threadWorker.getLogin() != null) {
                        if (!login.equals(threadWorker.getLogin())) {
                            String onlineMessage2 = "online " + threadWorker.getLogin() + "\n";
                            send(onlineMessage2);
                        }
                    }
                }

                // Send other online users current user's status (online) //
                String onlineMessage = "online " + login + "\n";
                for (ServerThreadWorker threadWorker : threadWorkerList) {
                    if (!login.equals(threadWorker.getLogin())) {
                        threadWorker.send(onlineMessage);
                    }
                }

                // Login denied //
            } else {
                String message = "login error\n";
                outputStream.write(message.getBytes());
            }

        }
    }

    // Send user online method //
    private void send(String onlineMessage) throws IOException {

        if (login != null) {
            outputStream.write(onlineMessage.getBytes());
        }
    }
}
