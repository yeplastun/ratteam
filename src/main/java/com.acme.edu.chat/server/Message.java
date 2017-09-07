package com.acme.edu.chat.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String time;
    private String text;

    private String typeCommand;

    public Message(String msg) {
        if (msg.startsWith("/hist")) {
            this.typeCommand = "/hist";
        } else {
            this.typeCommand = msg.substring(0, msg.indexOf(' '));
        }

        this.text = msg.substring(msg.indexOf(' ') + 1);
        this.time = LocalDateTime.now().format(formatter);
    }

    public String getTime() {
        return time;
    }

    public String getFormattingMessage() {
        return time + "\t" + text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTypeCommand() {
        return typeCommand;
    }
}
