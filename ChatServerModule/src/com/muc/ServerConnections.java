package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Server connections  class thread //
public class ServerConnections extends Thread{
    private final int serverPort;

    // List of Server Thread workers //
    private ArrayList <ServerThreadWorker> threadWorkerList = new ArrayList<>();

    public ServerConnections(int serverPort) {
        this.serverPort = serverPort;
    }

    // Server Thread worker for accessing other Server Thread Workers //
    public List<ServerThreadWorker> getThreadWorkerList() {
        return threadWorkerList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            // Loop for the accept method //
            while(true) {
                System.out.println("About to accept client connection...");

                // Accept method to create connection between server and client //
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                // Thread worker instance for handling communication in the socket //
                ServerThreadWorker threadWorker = new ServerThreadWorker(this, clientSocket);

                // Adding worker list //
                threadWorkerList.add(threadWorker);

                threadWorker.start();
            }

            // Exception handling //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Logoff exceptions //
    public void removeThreadWorker(ServerThreadWorker serverThreadWorker) {
        threadWorkerList.remove(serverThreadWorker);
    }
}
