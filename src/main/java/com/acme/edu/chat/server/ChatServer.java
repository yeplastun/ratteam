package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static com.acme.edu.chat.Commands.*;

public class ChatServer {
    private static Object monitorHistory = new Object();
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static List<Message> history = null;
    private static ConcurrentHashMap<Socket, DataOutputStream> dataOutStr = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Socket, String> clientSockets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            history = new LinkedList<>();

            serverSocket = new ServerSocket(6666);
            ServerSocket finalServerSocket = serverSocket;
            while (true) {
                final Socket clientSocket = finalServerSocket.accept();
                clientSockets.put(clientSocket, "");
                executorService.submit(processSocket(clientSocket));
            }
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
                        dataOutStr.put(clientSocket, outputStream);

                        String msg = inputStream.readUTF();
                        System.out.println(msg);

                        commandMessageHandler(clientSocket, outputStream, msg);
                    }
                } catch (EOFException | SocketException e) {
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    clientSockets.remove(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static void commandMessageHandler(Socket clientSocket, DataOutputStream outputStream, String msg) throws IOException {
        if (!msg.startsWith(SEND_COMMAND) && msg.startsWith(HISTORY_COMMAND) && !msg.startsWith("")) {
            outputStream.writeUTF(INVALID_COMMAND);
            return;
        }

        Message tempMsg = new Message(msg);
        tempMsg.setUsername(clientSockets.get(clientSocket));
        msg = tempMsg.getFormattingMessage();

        switch (tempMsg.getTypeCommand()) {
            case SEND:
                broadcastMessage(msg, tempMsg);
                break;
            case HISTORY:
                sendHistory(outputStream);
                break;
            case CHANGEID:
                clientSockets.put(clientSocket, msg);
                break;
            default:
                outputStream.writeUTF(INVALID_COMMAND);
        }
    }

    private static void broadcastMessage(String msg, Message tempMsg) {
        synchronized (monitorHistory) {
            history.add(tempMsg);
        }

        final String finalMsg = msg;
        executorService.submit(() -> clientSockets.keySet().forEach(socket -> {
            try {
                dataOutStr.get(socket).writeUTF(finalMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private static synchronized void sendHistory(DataOutputStream outputStream) {
        history.forEach(message -> {
            try {
                outputStream.writeUTF(message.getFormattingMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}