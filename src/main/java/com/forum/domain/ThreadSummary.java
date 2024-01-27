package com.forum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_thread_summary")
public class ThreadSummary {
    @Id
    @Column(name = "thread_id")
    private Long threadId;

    @Column(name = "comments_tokens_length")
    private Long commentsTokensLength;

    @Column(name = "comments_summary")
    private String commentsSummary;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ThreadSummary() {

    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getCommentsTokensLength() {
        return commentsTokensLength;
    }

    public void setCommentsTokensLength(Long commentsTokensLength) {
        this.commentsTokensLength = commentsTokensLength;
    }

    public String getCommentsSummary() {
        return commentsSummary;
    }

    public void setCommentsSummary(String commentsSummary) {
        this.commentsSummary = commentsSummary;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
