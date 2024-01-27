package com.forum.service.summary.gateway;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatGptGateway {
    String apiKey = "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";
    String url = "https://api.openai.com/v1/chat/completions";

    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    HttpClient httpClient = HttpClient.newHttpClient();

    public List<String> makeCall(String contents) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(new ChatGptRequest(contents))))
                .build();

        // Response from ChatGPT
        String response = httpClient
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body();

        ChatGptResponse gptResponse = mapper.readValue(response, ChatGptResponse.class);

        return gptResponse.choices.stream().map(c -> c.message.content).collect(Collectors.toList());
    }
}