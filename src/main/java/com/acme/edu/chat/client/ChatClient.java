package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ChatClient {
    private String host;
    private Integer port;

    ChatClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    void startChat() {
        try (Socket socket = new Socket(host, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream());
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            Thread receiver = new Thread(new ChatReceiver(input));
            receiver.setDaemon(true);
            receiver.start();
            new ChatSender(output, consoleInput).run();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }
    }
}