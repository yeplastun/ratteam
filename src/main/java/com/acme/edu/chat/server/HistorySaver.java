package com.acme.edu.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
            try {
                new File(filename).createNewFile();
            } catch (IOException e1) {
                System.out.println("Unable to create history file.");
            }
            reader = null;
            writer = null;
        }
    }
    static HistorySaver getInstance() {
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

    public List<Message> loadHistory() throws FileNotFoundException {
        List<Message> messages = new LinkedList<>();

        if (writer != null) {
            try {
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(filename))
                );
            } catch (FileNotFoundException ignore) {
                System.out.println("No history file. Starting with empty history");
            }
        }

        String message;
        try {
            while ((message = reader.readLine()) != null && Objects.equals(message, "")) {
                messages.add(Message.fromString(message));
            }
        } catch (IOException e) {
            return messages;
        }

        if (reader != null) {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename))
            );
        }

        return messages;
    }
}
