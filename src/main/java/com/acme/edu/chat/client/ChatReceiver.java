package com.acme.edu.chat.client;

import java.io.DataInputStream;
import java.io.IOException;

public class ChatReceiver implements Runnable {
    private DataInputStream inputStream;

    ChatReceiver(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // System.out.println(inputStream.readUTF());
                // send to client book
                String messageToClientBook = inputStream.readUTF();
            }
        } catch (IOException e) {
            System.out.println("No more data from server");
        }
    }
}
