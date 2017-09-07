package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static Queue<Socket> clientSockets = new ConcurrentLinkedQueue<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static Queue<String> history = new ConcurrentLinkedQueue<>();

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
        } finally {
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
                        if (msg.startsWith("/snd")) {
                            // handle str
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            msg = LocalDateTime.now().format(formatter) + "\t" + msg.substring(msg.indexOf(' ') + 1);
                            history.add(msg);

                            String finalMsg = msg;
                            executorService.submit(() -> clientSockets.forEach(socket -> {
                                try {
                                    new DataOutputStream(socket.getOutputStream()).writeUTF(finalMsg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }));
                        } else if (msg.startsWith("/hist")) {
                            history.forEach(message -> {
                                try {
                                    outputStream.writeUTF(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            outputStream.writeUTF("== Invalid Command ==");
                        }
                    }
                } catch (EOFException e) {

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
}