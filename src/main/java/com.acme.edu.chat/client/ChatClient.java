package com.acme.edu.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    Socket clientSocket;
    BufferedWriter output;
    BufferedReader input;

    public ChatClient(Socket clientSocket, BufferedWriter output, BufferedReader input) {
        this.clientSocket = clientSocket;
        this.output = output;
        this.input = input;
    }

    public void startChat() {
        new Thread(new Receiver(input)).start();
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                output.write(consoleInput.readLine());
                output.newLine();
                //System.out.println(consoleInput.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 6666);
                BufferedWriter output = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            ChatClient client = new ChatClient(socket, output, input);
            client.startChat();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}