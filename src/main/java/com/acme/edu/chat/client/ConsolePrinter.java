package com.acme.edu.chat.client;

import com.sun.corba.se.spi.activation.Server;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConsolePrinter {
    private ServerSocket serverSocket;

    ConsolePrinter(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        try (
                ServerSocket currentServerSocket = new ServerSocket(6667);
        ) {
            ConsolePrinter consolePrinter = new ConsolePrinter(currentServerSocket);
            consolePrinter.start();
        } catch (IOException e) {

        }
    }

    private void start() {
        try (
            Socket clientSocket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        ) {
            System.out.println(dataInputStream.readUTF());
        } catch(IOException e) {

        }
    }
}
