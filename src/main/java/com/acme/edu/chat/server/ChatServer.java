package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.acme.edu.chat.Commands.*;

public class ChatServer {
    private final Object historyMonitor = new Object();
    private final Object broadcastMonitor = new Object();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private List<Message> history;
    private ConcurrentHashMap<Socket, DataOutputStream> dataOutStr = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Socket, String> clientSockets = new ConcurrentHashMap<>();

    private int port;
    private HistorySaver saver;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try (
                HistorySaver saver = new HistorySaver();
                ServerSocket serverSocket = new ServerSocket(port);
        ) {
            this.saver = saver;
            history = saver.loadHistory();
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                clientSockets.put(clientSocket, "");
                executorService.submit(processSocket(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private Runnable processSocket(Socket clientSocket) throws IOException {
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

                        handleCommand(clientSocket, outputStream, msg);
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

    private void handleCommand(Socket clientSocket, DataOutputStream outputStream, String msg) throws IOException {
        if (!msg.startsWith(SEND_COMMAND) && msg.startsWith(HISTORY_COMMAND) && !msg.startsWith("")) {
            outputStream.writeUTF(INVALID_COMMAND);
            return;
        }

        Message tempMsg = new Message(msg);
        tempMsg.setUsername(clientSockets.get(clientSocket));
        msg = tempMsg.getFormattingMessage();

        switch (tempMsg.getTypeCommand()) {
            case SEND:
                broadcastMessageAndSaveToHistory(msg, tempMsg);
                break;
            case HISTORY:
                sendHistory(outputStream);
                break;
            case CHANGEID:
                msg = tempMsg.getText();
                clientSockets.put(clientSocket, msg);

                outputStream.writeUTF("Your nickname is changed to " + msg);

                break;
            default:
                outputStream.writeUTF(INVALID_COMMAND);
        }
    }

    private void broadcastMessageAndSaveToHistory(String msg, Message tempMsg) {
        synchronized (historyMonitor) {
            history.add(tempMsg);
            saver.addToFile(tempMsg);
        }

        final String finalMsg = msg;

        synchronized (broadcastMonitor) {
            clientSockets.keySet().forEach(socket -> {
                try {
                    dataOutStr.get(socket).writeUTF(finalMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void sendHistory(DataOutputStream outputStream) {
        synchronized (historyMonitor) {
            history.forEach(message -> {
                try {
                    outputStream.writeUTF(message.getFormattingMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}