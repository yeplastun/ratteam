package com.acme.edu.chat.client;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatReceiver implements Runnable {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    ChatReceiver(DataInputStream inputStream, DataOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                outputStream.writeUTF(inputStream.readUTF());
            }
        } catch (IOException e) {
            System.out.println("No more data from server");
        }
    }
}
