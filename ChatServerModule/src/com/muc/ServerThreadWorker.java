package com.muc;

import java.io.*;
import java.net.Socket;
import java.util.Date;

// Server class for handling the thread for multiple connections //
public class ServerThreadWorker extends Thread{

    private final Socket clientSocket;

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
            if ("quit".equalsIgnoreCase(line)) {
                break;
            }
            String message = "You tipped: " + line + "\n";
            outputStream.write(message.getBytes());
        }

        clientSocket.close();
    }
}
