package com.acme.edu.chat.server;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.acme.edu.chat.Commands.*;

public class Message {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static Gson gson = new Gson();
    private String time;
    private String text;
    private String username;
    private MessageType typeCommand;

    Message(String msg) {
        if (msg.startsWith(HISTORY_COMMAND)) {
            this.typeCommand = MessageType.HISTORY;
        } else if (msg.startsWith(SEND_COMMAND)) {
            this.typeCommand = MessageType.SEND;
        } else if (msg.startsWith(CHANGE_ID_COMMAND)) {
            this.typeCommand = MessageType.CHANGEID;
        }

        this.text = msg.substring(msg.indexOf(' ') + 1);
        this.time = LocalDateTime.now().format(formatter);
        this.username = "";
    }

    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getFormattedMessage() {
        return time + " " + getFormattedUsername() + text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String getFormattedUsername() {
        if ("".equals(username)) {
            return "Anonymous:    ";
        }

        return username + ":    ";
    }

    MessageType getTypeCommand() {
        return typeCommand;
    }

    String getText() {
        return text;
    }

    static Message transformStringToMessage(String message) {
        return gson.fromJson(message, Message.class);
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
