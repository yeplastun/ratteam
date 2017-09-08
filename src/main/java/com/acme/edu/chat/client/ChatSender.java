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
                    System.out.println("Error: message should be shorter than " + maxMessageLength + " symbols");
                    message = consoleInput.readLine().trim();
                    continue;
                }

                if (!(message.startsWith(SEND_COMMAND)
                        || message.startsWith(HISTORY_COMMAND)
                        || message.startsWith(CHANGE_ID_COMMAND))) {
                    System.out.println("Error: message should start with " +
                            SEND_COMMAND + " or " + HISTORY_COMMAND + " or " + CHANGE_ID_COMMAND);
                    message = consoleInput.readLine().trim();
                    continue;
                }
                if ((message.equals(SEND_COMMAND) || message.equals(CHANGE_ID_COMMAND))) {
                    System.out.println("Message can't be empty");
                    message = consoleInput.readLine().trim();
                    continue;
                }

                outputStream.writeUTF(message);
                message = consoleInput.readLine().trim();
            }
            System.out.println("Terminated");
        } catch (IOException ex) {
            System.out.println("Error on sending message, server is unavailable");
        }
    }
}
