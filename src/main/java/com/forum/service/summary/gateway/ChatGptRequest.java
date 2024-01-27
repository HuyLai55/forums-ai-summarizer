package com.forum.service.summary.gateway;

import java.util.ArrayList;
import java.util.List;

public class ChatGptRequest {
    public String model = "gpt-3.5-turbo";
    public List<Message> messages = new ArrayList();

    public ChatGptRequest() {

    }

    public ChatGptRequest(String contents) {
        this.messages = List.of(new Message(contents));
    }
}
