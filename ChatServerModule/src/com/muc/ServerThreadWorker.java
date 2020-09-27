package com.muc;

import java.io.IOException;
import java.io.OutputStream;
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
        OutputStream outputStream = clientSocket.getOutputStream();
        for (int i=0; i<10; i++) {
            outputStream.write(("Time now is" + new Date() + "\n").getBytes());
            Thread.sleep(1000);

        }
        clientSocket.close();
    }
}
