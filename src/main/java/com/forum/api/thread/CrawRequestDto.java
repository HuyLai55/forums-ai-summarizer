package com.forum.api.thread;

import com.forum.domain.Thread;

public class CrawRequestDto {
    private String url;
    private Thread.Source source;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Thread.Source getSource() {
        return source;
    }

    public void setSource(Thread.Source source) {
        this.source = source;
    }
}
