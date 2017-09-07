package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver implements Runnable {
    BufferedReader input;

    public Receiver(BufferedReader input) {
        this.input = input;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(input.readLine());
            } catch (IOException e) {
//                throw new Exception(e);
                e.printStackTrace();
            }
        }
    }
}
