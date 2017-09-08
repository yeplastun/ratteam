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

    private Integer portToBook;

    ChatClient(String host, Integer port, Integer portToBook) {
        this.host = host;
        this.port = port;
        this.portToBook = portToBook;
    }

    void startChat() {
        try (
             Socket printerSocket = new Socket("127.0.0.1", portToBook);
             DataOutputStream printerOutput = new DataOutputStream(printerSocket.getOutputStream());
             DataInputStream printerInput = new DataInputStream(printerSocket.getInputStream());

             Socket socket = new Socket(host, port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream());
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Enter your nickname with command " + CHANGE_ID_COMMAND);
            String nickname = consoleInput.readLine();
            output.writeUTF(nickname);

            Thread receiver = new Thread(new ChatReceiver(input, printerOutput));
            receiver.setDaemon(true);
            receiver.start();
            new ChatSender(output, consoleInput).run();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }
    }
}