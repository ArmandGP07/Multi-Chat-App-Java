package com.muc;

// Main Server class //
public class MainServer {

    public static void main(String[] args){

        // Port used for Server Socket //
        int port = 9999;

        // Server class instance //
        ServerConnections server = new ServerConnections(port);

        server.start();
    }
}
