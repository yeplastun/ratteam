package com.acme.edu.chat.client;


import java.io.IOException;
import java.net.Socket;

public class ClientBootstrapper {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";

        int port = 6666;
        Socket socket = new Socket(host, port);
        ChatClient chatClient = new ChatClient("127.0.0.1", 6666);

        chatClient.startChat();
    }
}
