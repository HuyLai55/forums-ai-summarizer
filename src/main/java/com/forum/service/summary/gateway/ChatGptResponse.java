package com.forum.service.summary.gateway;

import java.util.ArrayList;
import java.util.List;

public class ChatGptResponse {
    public List<Choice> choices = new ArrayList<>();
    public Usage usage;
}
