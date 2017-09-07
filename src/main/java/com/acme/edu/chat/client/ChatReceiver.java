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
                System.out.println(inputStream.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
