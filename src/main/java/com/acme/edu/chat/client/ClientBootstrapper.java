package com.acme.edu.chat.client;

import java.io.IOException;

public class ClientBootstrapper {
    public static void main(String[] args) throws IOException {
        new ChatClient("127.0.0.1", 6666, Integer.parseInt(args[0])).startChat();
    }
}
