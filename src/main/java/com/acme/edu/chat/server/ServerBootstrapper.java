package com.acme.edu.chat.server;

public class ServerBootstrapper {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(6666);
        server.start();
    }
}

