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

                // Thread worker instance for handling communication in the socket //
                ServerThreadWorker threadWorker = new ServerThreadWorker(clientSocket);
                threadWorker.start();
            }

        // Exception handling //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
