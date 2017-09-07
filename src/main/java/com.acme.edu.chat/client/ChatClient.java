package com.acme.edu.chat.client;

import java.io.*;
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

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}