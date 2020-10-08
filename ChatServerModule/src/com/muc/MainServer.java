package com.muc;

/**
 * The type Main server.
 */
// Main Server class //
public class MainServer {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(String[] args){

        // Port used for Server Socket //
        int port = 8818;

        // Server class instance //
        Server server = new Server(port);

        server.start();
    }
}
