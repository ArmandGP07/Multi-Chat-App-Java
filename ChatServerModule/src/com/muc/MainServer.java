package com.muc;

// Main Server class //
public class MainServer {

    public static void main(String[] args){

        // Port used for Server Socket //
        int port = 8818;

        // Server class instance //
        Server server = new Server(port);

        server.start();
    }
}
