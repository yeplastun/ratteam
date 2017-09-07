package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient {
    private Socket clientSocket;
    private DataOutputStream output;
    private DataInputStream input;

    public ChatClient(Socket clientSocket, DataOutputStream output, DataInputStream input) {
        this.clientSocket = clientSocket;
        this.output = output;
        this.input = input;
    }

    public void startChat() {
        new Thread(new Receiver(input)).start();
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                final String message = consoleInput.readLine();
                output.writeUTF(message);
                //System.out.println(consoleInput.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 6666);
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                DataInputStream input = new DataInputStream(socket.getInputStream());
        ) {
            ChatClient client = new ChatClient(socket, output, input);
            client.startChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}