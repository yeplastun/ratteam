package com.acme.edu.chat.client;


public class ClientBootstrapper {
    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1", 6666);

        chatClient.startChat();
    }
}
