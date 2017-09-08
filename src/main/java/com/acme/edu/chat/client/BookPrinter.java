package com.acme.edu.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BookPrinter {
    private ServerSocket serverSocket;

    private BookPrinter(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        try (
                ServerSocket currentServerSocket = new ServerSocket(Integer.parseInt(args[0]));
        ) {
            BookPrinter bookPrinter = new BookPrinter(currentServerSocket);
            bookPrinter.start();
        } catch (IOException e) {
            System.out.println("Book printer goes wrong! We cant't say <<all right>>!");
        }
    }

    private void start() throws IOException {
        try (Socket clientSocket = serverSocket.accept()) {
            try (DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream())) {
                try (DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream())) {
                    while (true) {
                        System.out.println(dataInputStream.readUTF());
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
