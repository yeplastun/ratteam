package com.acme.edu.chat.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.acme.edu.chat.Commands.*;

public class Message {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String time;
    private String text;
    private String username;

    private MessageType typeCommand;

    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    Message(String msg) {
        if (msg.startsWith(HISTORY_COMMAND)) {
            this.typeCommand = MessageType.HISTORY;
        } else if (msg.startsWith(SEND_COMMAND)){
            this.typeCommand = MessageType.SEND;
        } else if (msg.startsWith("/chid")) {
            this.typeCommand = MessageType.CHANGEID;
        }

        this.text = msg.substring(msg.indexOf(' ') + 1);
        this.time = LocalDateTime.now().format(formatter);
        this.username = "";
    }

    public String getTime() {
        return time;
    }

    private String getFormattingUsername() {
        if (username == "") {
            return "Anonymous:    ";
        }

        return username + ":    ";
    }

    String getFormattingMessage() {
        return time + " " + getFormattingUsername()+ text;
    }

    public void setText(String text) {
        this.text = text;
    }

    MessageType getTypeCommand() {
        return typeCommand;
    }

    public String getText() {
        return text;
    }
}
