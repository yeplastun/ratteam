package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.acme.edu.chat.Commands.*;

public class ChatSender implements Runnable {
    private DataOutputStream outputStream;
    private BufferedReader consoleInput;

    ChatSender(DataOutputStream outputStream, BufferedReader consoleInput) {
        this.outputStream = outputStream;
        this.consoleInput = consoleInput;
    }

    @Override
    public void run() {
        try {
            String message = consoleInput.readLine().trim();
            final int maxMessageLength = 150;

            while (!message.startsWith(EXIT_COMMAND) && !message.startsWith(QUIT_COMMAND)) {
                if (message.length() > maxMessageLength) {
                    System.out.println("Error: message should be shorter than 150 symbols.");
                    message = consoleInput.readLine().trim();
                    continue;
                }
                if (!(message.startsWith(SEND_COMMAND) || message.startsWith(HISTORY_COMMAND))) {
                    System.out.println("Error: message should start with /snd or /hist");
                    message = consoleInput.readLine().trim();
                    continue;
                }
                outputStream.writeUTF(message);
                message = consoleInput.readLine().trim();
            }
            System.out.println("Terminated.");
        } catch (IOException ex) {
            System.out.println("Error on sending message: " + ex.getMessage());
        }
    }
}
