package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.acme.edu.chat.Commands.CHANGE_ID_COMMAND;

public class ChatClient {
    private String host;
    private Integer port;

    private BufferedReader reader;

    private Integer portToBook;

    public ChatClient(String host, Integer port, Integer portToBook) {
        this.host = host;
        this.port = port;
        this.portToBook = portToBook;

    }

    public ChatClient(String host, Integer port, Integer portToBook, BufferedReader reader) {
        this.host = host;
        this.port = port;
        this.portToBook = portToBook;
        this.reader = reader;
    }

    public void startChat() {
        try (
                Socket printerSocket = new Socket("127.0.0.1", portToBook);
                DataOutputStream printerOutput = new DataOutputStream(printerSocket.getOutputStream());
                DataInputStream printerInput = new DataInputStream(printerSocket.getInputStream());

                Socket socket = new Socket(host, port);
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

            Thread receiver = new Thread(new ChatReceiver(input, printerOutput));
            receiver.setDaemon(true);
            receiver.start();
            if (reader != null) {
                new ChatSender(output, reader).run();
            } else {
                new ChatSender(output, consoleInput).run();
            }
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }
    }
}