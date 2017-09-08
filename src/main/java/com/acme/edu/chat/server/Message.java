package com.acme.edu.chat.server;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.acme.edu.chat.Commands.HISTORY_COMMAND;
import static com.acme.edu.chat.Commands.SEND_COMMAND;

public class Message {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static Gson gson = new Gson();


    private String time;
    private String text;
    private String username;

    private MessageType typeCommand;

    public String getUsername() {
        return username;
    }

    static Message transformStringToMessage(String message) {
        return gson.fromJson(message, Message.class);
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
    void setUsername(String username) {
        this.username = username;
    }

    String getFormattingMessage() {
        return time + " " + getFormattingUsername()+ text;
    }

    public void setText(String text) {
        this.text = text;
    }
    private String getFormattingUsername() {
        if (Objects.equals(username, "")) {
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
    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
