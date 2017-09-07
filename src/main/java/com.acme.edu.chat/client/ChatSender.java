package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatSender implements Runnable {
    private DataOutputStream outputStream;
    private BufferedReader consoleInput;


    public ChatSender(DataOutputStream outputStream, BufferedReader consoleInput) {
        this.outputStream = outputStream;
        this.consoleInput = consoleInput;
    }


    @Override
    public void run() {
        try {
            String message = consoleInput.readLine().trim();
            while (message.startsWith("/exit") || message.startsWith("/quit")) {
                if (message.length() > 150) {
                    System.out.println("Error: message should be shorter than 150 symbols.");
                    continue;
                }
                if (!(message.startsWith("/snd") || message.startsWith("/hist"))) {
                    System.out.println("Error: message should start with /snd or /hist");
                    continue;
                }
                outputStream.writeUTF(message);
                message = consoleInput.readLine().trim();
            }
        } catch (IOException ex) {
            System.out.println("Error on sending message: " + ex.getMessage());
        }
    }
}
