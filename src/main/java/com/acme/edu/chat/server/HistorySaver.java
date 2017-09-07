package com.acme.edu.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.lineSeparator;

public class HistorySaver {

    private static final HistorySaver INSTANCE = new HistorySaver();
    private static final String filename = "history.txt";
    private BufferedReader reader;
    private BufferedWriter writer;
    private HistorySaver() {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename))
            );
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filename))
            );
        } catch (FileNotFoundException e) {
            reader = null;
            writer = null;
        }
    }
    HistorySaver getInstance() {
        return INSTANCE;
    }
    public synchronized void addToFile(Message message) throws IOException {
        if (reader != null) {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename))
            );
        }
        writer.write(message.toString() + lineSeparator());
        writer.flush();
    }

    public List<Message> loadHistory() throws IOException {
        if (writer != null) {
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filename))
            );
        }

        List<Message> messages = new LinkedList<>();

        String message;
        while ((message = reader.readLine()) != null) {
            messages.add(Message.fromString(message));
        }

        return messages;
    }
}
