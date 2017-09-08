package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.acme.edu.chat.Commands.CHANGE_ID_COMMAND;

class ChatClient {
    private String host;
    private Integer port;

    ChatClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    void startChat() {
        try (Socket socket = new Socket(host, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream());
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String nickname = "";
            while (!nickname.startsWith(CHANGE_ID_COMMAND)) {
                System.out.println("Enter your nickname with command " + CHANGE_ID_COMMAND);
                nickname = consoleInput.readLine();
            }
            output.writeUTF(nickname.trim());

            Thread receiver = new Thread(new ChatReceiver(input));
            receiver.setDaemon(true);
            receiver.start();
            new ChatSender(output, consoleInput).run();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }
    }
}