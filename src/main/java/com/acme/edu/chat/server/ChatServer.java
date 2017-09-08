package com.acme.edu.chat.server;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.acme.edu.chat.Commands.INVALID_COMMAND;

public class ChatServer {
    private final ReadWriteLock historyLock = new ReentrantReadWriteLock();
    private final Object broadcastMonitor = new Object();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private List<Message> history;
    private ConcurrentHashMap<Socket, DataOutputStream> dataOutStreams = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Socket, String> clientSockets = new ConcurrentHashMap<>();

    private int port;
    private HistorySaver historySaver;

    public ChatServer(int port) {
        this.port = port;
    }

    void start() {
        try (
                HistorySaver saver = new HistorySaver();
                ServerSocket serverSocket = new ServerSocket(port)
        ) {
            this.historySaver = saver;
            history = saver.loadHistory();
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                clientSockets.put(clientSocket, "");
                executorService.submit(processSocket(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Server socket exception: " + e.getMessage());
        }
    }

    @NotNull
    private Runnable processSocket(Socket clientSocket) {
        return () -> {
            {
                try (
                        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                        DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream())
                ) {
                    while (true) {
                        dataOutStreams.put(clientSocket, outputStream);

                        String msg = inputStream.readUTF();
                        System.out.println(msg);

                        handleCommand(clientSocket, outputStream, msg);
                    }
                } catch (IOException ex) {
                    try {
                        clientSocket.close();
                    } catch (IOException exx) {
                        System.out.println("Can't close client socket");
                    }
                } finally {
                    dataOutStreams.remove(clientSocket);
                    clientSockets.remove(clientSocket);
                }
            }
        };
    }

    private void handleCommand(Socket clientSocket, DataOutputStream outputStream, String rawMessage) throws IOException {
        Message message = new Message(rawMessage);
        message.setUsername(clientSockets.get(clientSocket));

        switch (message.getTypeCommand()) {
            case SEND:
                broadcastMessageAndSaveToHistory(message);
                break;
            case HISTORY:
                sendHistory(outputStream);
                break;
            case CHANGEID:
                String id = message.getText();
                clientSockets.put(clientSocket, id);
                outputStream.writeUTF("Your nickname is changed to " + id);
                break;
            default:
                outputStream.writeUTF(INVALID_COMMAND);
        }
    }

    private void broadcastMessageAndSaveToHistory(Message tempMessage) {
        historyLock.writeLock().lock();
        history.add(tempMessage);
        historySaver.addToFile(tempMessage);
        historyLock.writeLock().unlock();

        synchronized (broadcastMonitor) {
            clientSockets.keySet().forEach(socket -> {
                try {
                    dataOutStreams.get(socket).writeUTF(tempMessage.getFormattedMessage());
                } catch (IOException e) {
                    System.out.println("Can't write to client outputstream");
                }
            });
        }
    }

    private void sendHistory(DataOutputStream outputStream) {
        historyLock.readLock().lock();
        history.forEach(message -> {
            try {
                outputStream.writeUTF(message.getFormattedMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        historyLock.readLock().unlock();
    }
}