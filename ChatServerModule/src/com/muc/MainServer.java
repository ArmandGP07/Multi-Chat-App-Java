package com.muc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

// Main Server class //
public class MainServer {
    public static void main(String[] args){

        // Port used for Server Socket //
        int port = 9999;
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            // Loop for the accept method //
            while(true) {
                System.out.println("About to accept client connection...");

                // Accept method to create connection between server and client //
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                // Thread for having multiple connections //
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            handleClientSocket(clientSocket);

                        // Exceptions for thread //
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
            }
            
        // Exception handling //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method for handling client socket //
    private static void handleClientSocket(Socket clientSocket) throws IOException, InterruptedException {
        OutputStream outputStream = clientSocket.getOutputStream();
        for (int i=0; i<10; i++) {
            outputStream.write(("Time now is" + new Date() + "\n").getBytes());
            Thread.sleep(1000);

        }
        clientSocket.close();
    }
}
