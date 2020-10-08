package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Server.
 */
// Server class thread //
public class Server extends Thread {
    private final int serverPort;

    // List of Server Thread workers //
    private final ArrayList<ServerWorker> workerList = new ArrayList<>();

    /**
     * Instantiates a new Server.
     *
     * @param serverPort the server port
     */
    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets worker list.
     *
     * @return the worker list
     */
// Server Thread worker for accessing other Server Thread Workers //
    public List<ServerWorker> getWorkerList() {
        return workerList;
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
                ServerWorker worker = new ServerWorker(this, clientSocket);

                // Adding worker list //
                workerList.add(worker);
                worker.start();
            }

        // Exception handling //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove worker.
     *
     * @param serverWorker the server worker
     */
// Logoff exceptions //
    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
