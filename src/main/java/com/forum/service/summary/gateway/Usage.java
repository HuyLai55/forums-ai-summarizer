package com.forum.service.summary.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usage {
    @JsonProperty("prompt_tokens")
    public long promptTokens;
    @JsonProperty("completion_tokens")
    public long completionTokens;
    @JsonProperty("total_tokens")
    public long totalTokens;
}
