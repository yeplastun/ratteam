package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient {
    private String host;
    private Integer port;

    public ChatClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void startChat() {
        try (Socket socket = new Socket(host, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream());
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            new Thread(new ChatReceiver(input)).start();
            new ChatSender(output, consoleInput).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("127.0.0.1", 6666);
        client.startChat();
    }

}