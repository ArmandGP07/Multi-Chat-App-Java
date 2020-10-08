package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

// Server class for handling the thread for multiple connections //
public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private final HashSet<String> topicSet = new HashSet<>();

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    // Run method for thread //
    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method for handling client socket //
    private void handleClientSocket() throws IOException {

        // Input stream for sending client data //
        InputStream inputStream = clientSocket.getInputStream();

        // Output stream for reading client data //
        this.outputStream = clientSocket.getOutputStream();

        // Buffer reader //
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ( (line = reader.readLine()) != null) {

            // Split line in individual tokens //
            String[] tokens = StringUtils.split(line);

            // if method for not causing null pointer exceptions //
            if (tokens != null && tokens.length > 0) {

                // First token //
                String cmd = tokens[0];

                // Logoff command //
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
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
                } else if ("leave".equalsIgnoreCase(cmd)) {
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
    public boolean isMemberOfTopic(String topic) {
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
        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        // Iterating through list of server thread worker //
        List<ServerWorker> workerList = server.getWorkerList();

        // Sending messages ability //
        for(ServerWorker worker : workerList) {
            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {

                    // Group chat/topic format //
                    String outMsg = "msg " + sendTo + ":" + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {

                    // One-to-one message format //
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }

    // Logoff method //
    private void handleLogoff() throws IOException {
        server.removeWorker(this);

        // Iterating through list of server thread worker //
        List<ServerWorker> workerList = server.getWorkerList();

        // Send other online users current user's status (offline) //
        String onlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(onlineMsg);
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
            if ((login.equals("Bruce") && password.equals("Bruce")) || (login.equals("Clark") && password.equals("Clark")) || (login.equals("Pepe") && password.equals("Pepe")) ) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                // Iterating through list of server thread worker //
                List<ServerWorker> workerList = server.getWorkerList();

                // Send current user all other online logins //
                for(ServerWorker worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                // Send other online users current user's status (online) //
                String onlineMsg = "online " + login + "\n";
                for(ServerWorker worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }

            // Login denied //
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    // Send user online method //
    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}
