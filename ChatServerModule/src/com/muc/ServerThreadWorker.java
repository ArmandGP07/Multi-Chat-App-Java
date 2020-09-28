package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;

// Server class for handling the thread for multiple connections //
public class ServerThreadWorker extends Thread{

    private final Socket clientSocket;
    private final ServerConnections serverConnections;
    private String login = null;
    private OutputStream outputStream;

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

                // Logoff protocol //
                if ("logoff".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;

                // Login protocol //
                } else if ("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);

                } else {

                    // Unknown string for not identifying command //
                   String msg = "unknown " + cmd + "\n";
                   outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    // Logoff method //
    private void handleLogoff() throws IOException {

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
