package com.acme.edu.chat.client;

import java.io.DataInputStream;
import java.io.IOException;

public class Receiver implements Runnable {
    private DataInputStream input;

    public Receiver(DataInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(input.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
