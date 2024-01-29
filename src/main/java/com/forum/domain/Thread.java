package com.forum.domain;

import jakarta.persistence.*;
import org.checkerframework.common.aliasing.qual.Unique;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_thread")
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    private Long threadId;

    @Unique
    @Column(name = "thread_url")
    private String threadUrl;

    @Column(name = "source")
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "creator")
    private String creator;

    @Column(name = "title")
    private String title;

    @Column(name = "created_At")
    private LocalDateTime createdAt;

    public Thread() {
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getThreadUrl() {
        return threadUrl;
    }

    public void setThreadUrl(String threadUrl) {
        this.threadUrl = threadUrl;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum Source {
        otosaigon, tinhte, voz
    }
}
