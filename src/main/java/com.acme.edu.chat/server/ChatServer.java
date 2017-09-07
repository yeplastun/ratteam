package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                executorService.submit(acceptMessage(clientSocket));
            }
        });
    }

    @NotNull
    private static Runnable acceptMessage(Socket clientSocket) {
        return () -> {
            try (
                    BufferedWriter outputStream = new BufferedWriter(
                            new OutputStreamWriter(clientSocket.getOutputStream()));
                    BufferedReader inputStream = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String msg = inputStream.readLine();

                if (msg.startsWith("/snd")) {
                    // handle str
                    msg = LocalDateTime.now() + "\t" + msg;
                    history.add(msg);

                    String finalMsg = msg;
                    executorService.submit(() -> {
                        clientSockets.forEach(socket -> {
                            try {
                                new BufferedWriter(
                                        new OutputStreamWriter(socket.getOutputStream())
                                ).write(finalMsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                } else if (msg.startsWith("/hist")) {
                    history.forEach(message -> {
                        try {
                            outputStream.write(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    outputStream.write("Invalid Command!!!!!!!!!!!!!!!!!!!!!!!!!!!!11111");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
