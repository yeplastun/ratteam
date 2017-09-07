package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static ConcurrentLinkedQueue<Socket> clientSockets = new ConcurrentLinkedQueue<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static Queue<Message> history = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6666);
            ServerSocket finalServerSocket = serverSocket;
            executorService.submit(() -> {
                while (true) {
                    final Socket clientSocket = finalServerSocket.accept();
                    clientSockets.add(clientSocket);
                    executorService.submit(processSocket(clientSocket));
                }
            });
        } catch (IOException e) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }

    @NotNull
    private static Runnable processSocket(Socket clientSocket) throws IOException {
        return () -> {
            {
                try (
                        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                        DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream())
                ) {
                    while (true) {
                        String msg = inputStream.readUTF();
                        System.out.println(msg);

                        commandMessageHandler(outputStream, msg);
                    }
                } catch (EOFException e) {
                    System.out.println("Connection with someone is lost");
                } catch (SocketException e) {
                    clientSockets.forEach(socket -> {
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static void commandMessageHandler(DataOutputStream outputStream, String msg) throws IOException {
        if (!msg.startsWith("/snd") && msg.startsWith("/hist") && !msg.startsWith("")) {
            outputStream.writeUTF("== Invalid Command ==");
            return;
        }

        Message tempMsg = new Message(msg);
        msg = tempMsg.getFormattingMessage();
        switch (tempMsg.getTypeCommand()) {
            case SEND:
                history.add(tempMsg);
                final String finalMsg = msg;
                executorService.submit(() -> clientSockets.forEach(socket -> {
                    try {
                        new DataOutputStream(socket.getOutputStream()).writeUTF(finalMsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
                break;
            case HISTORY:
                history.forEach(message -> {
                    try {
                        outputStream.writeUTF(message.getFormattingMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            default:
                outputStream.writeUTF("== Invalid Command ==");
        }
    }
}