package com.forum.service.summary.gateway;

public class Message {
    public String role = "user";
    public String content = "";

    public Message() {
    }

    public Message(String content) {
        this.content = content;
    }
}