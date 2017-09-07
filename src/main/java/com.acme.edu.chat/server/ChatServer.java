package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.lineSeparator;

public class ChatServer {
    private static ConcurrentLinkedQueue<Socket> clientSockets = new ConcurrentLinkedQueue<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static Queue<String> history = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        executorService.submit(() -> {
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                executorService.submit(processSocket(clientSocket));
            }
        });
    }

    @NotNull
    private static Runnable processSocket(Socket clientSocket) {
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
                            msg = LocalDateTime.now() + "\t" + msg;
                            history.add(msg);

                            String finalMsg = msg;
                            executorService.submit(() -> {
                                clientSockets.forEach(socket -> {
                                    try {
                                        new DataOutputStream(socket.getOutputStream()).writeUTF(finalMsg);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            });
                        } else if (msg.startsWith("/hist")) {
                            history.forEach(message -> {
                                try {
                                    outputStream.writeUTF(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            outputStream.writeUTF("Invalid Command!!!!!!!!!!!!!!!!!!!!!!!!!!!!11111" + lineSeparator());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
