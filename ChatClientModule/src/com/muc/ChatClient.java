package com.muc;

import java.io.*;
import java.net.Socket;

// Chat client class for interface with the server //
public class ChatClient {

    // Server name, server port, socket, server in, server out and buffered in fields //
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;


    public ChatClient(String serverName, int serverPort){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }
    public static void main(String[] args) throws IOException {

        // Client instance //
        ChatClient client = new ChatClient("localhost", 8818);
        if(!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection successful");

            // client login //
            if (client.login("guest", "guest")) {
                System.out.println("Login successful");

            } else {
                System.err.println("Login failed");
            }
        }
    }

    // API login Method //
    private boolean login(String login, String password) throws IOException {
        String cmd = "login" + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if ("login successful".equalsIgnoreCase(response)) {
            return true;
        } else {
            return false;
        }
    }

    // Connect method for connecting to the server using a socket //
    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
