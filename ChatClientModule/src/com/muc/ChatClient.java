package com.muc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// Chat client class for interface with the server //
public class ChatClient {

    // Server name, server port, socket, server in and server out fields //
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;



    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    public static void main(String[] args) {

        // Client instance //
        ChatClient client = new ChatClient("localhost", 9999);
        if(!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection successful");
        }
    }

    // Connect method for connecting to the server using a socket //
    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
