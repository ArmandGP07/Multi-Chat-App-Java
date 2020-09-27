package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;

// Server class for handling the thread for multiple connections //
public class ServerThreadWorker extends Thread{

    private final Socket clientSocket;
    private String login = null;

    public ServerThreadWorker(Socket clientSocket) {
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
        OutputStream outputStream = clientSocket.getOutputStream();

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
                if ("quit".equalsIgnoreCase(cmd)) {
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

    // Handle Login method //
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
           String login = tokens[1];
           String password = tokens[2];

           // Login accepted //
            if ((login.equals("guest") && password.equals("guest")) || (login.equals("armando") && password.equals("armando"))) {
                String message = "login ok\n";
                outputStream.write(message.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                // Login denied //
            } else {
                String message = "login error\n";
                outputStream.write(message.getBytes());
            }

        }
    }
}
