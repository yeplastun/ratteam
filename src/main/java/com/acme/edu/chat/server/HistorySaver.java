package com.acme.edu.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.lineSeparator;

public class HistorySaver {

    private static final HistorySaver INSTANCE = new HistorySaver();
    private static final String filename = "history.txt";
    private BufferedReader reader;
    private BufferedWriter writer;
    private HistorySaver() {
        boolean isFileExists = false;
        try {
            final File file = new File(filename);
            if (!file.exists()) {
                isFileExists = file.createNewFile();
            }
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF-8"))
            );
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filename), Charset.forName("UTF-8"))
            );
            System.out.println("Logging to: " + file.getAbsolutePath());
        } catch (IOException e) {
            reader = null;
            writer = null;
            if (isFileExists) {
                System.out.println("Unable to write to created history file.");

            } else {
                System.out.println("Unable to write to existing history file.");
            }
        }
    }
    static HistorySaver getInstance() {
        return INSTANCE;
    }

    public void addToFile(Message message) throws IOException {
        if (writer != null) {
            writer.write(message.toString() + lineSeparator());
            writer.flush();
        }
    }

    public List<Message> loadHistory() {
        List<Message> messages = new LinkedList<>();

        if (reader != null) {
            String message = "";
            try {
                while ((message = reader.readLine()) != null) {
                    messages.add(Message.fromString(message));
                }
            } catch (IOException e) {
                return messages;
            }
        }
        return messages;
    }
}
