package com.acme.edu.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.lineSeparator;

class HistorySaver implements Closeable {

    private static final String filename = "history.txt";

    private BufferedReader reader;
    private BufferedWriter writer;

    public HistorySaver() {
        final File file = new File(filename);

        if (!file.exists()) {
            try {
                boolean isFileExists = file.createNewFile();
            } catch (IOException e) {
                System.out.println("Can't create reader or writer");
            }
        }

        try {
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(file, true));
            System.out.println("Logging to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Can't create reader or writer");
        }
    }

    void addToFile(Message message) {
        try {
            writer.append(message.toString()).append(lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Can't write to file " + filename);
        }
    }

    List<Message> loadHistory() {
        List<Message> messages = new LinkedList<>();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                messages.add(Message.transformStringToMessage(line));
            }
        } catch (IOException e) {
            System.out.println("Can't read from file " + filename);
        }
        return messages;
    }

    @Override
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Can't close buffered reader and writer");
        }
    }
}