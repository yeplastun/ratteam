package com.acme.edu.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BookPrinter {
    private ServerSocket serverSocket;

    BookPrinter(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        try (
                ServerSocket currentServerSocket = new ServerSocket(6668);
        ) {
            BookPrinter bookPrinter = new BookPrinter(currentServerSocket);
            bookPrinter.start();
        } catch (IOException e) {

        }
    }

    private void start() {
        try (
            Socket clientSocket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        ) {
            while(true) {
                System.out.println(dataInputStream.readUTF());
            }
        } catch(IOException e) {

        }
    }
}
