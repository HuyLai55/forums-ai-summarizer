package com.forum.thread.domain;

import com.forum.comment.domain.Comment;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "forum_thread")
@Table(name = "forum_thread")
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    public Long threadId;

    @Column(name = "source_type")
    public String sourceType;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "title")
    public String title;

    @Column(name = "list_comments")
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Column(name = "created_At")
    public LocalDateTime createdAt;

    public Thread() {
    }

    public Thread(String sourceType, String title) {
        this.sourceType = sourceType;
        this.title = title;
    }

    public Thread(String sourceType, String userName, String title, LocalDateTime createdAt) {
        this.sourceType = sourceType;
        this.userName = userName;
        this.title = title;
        this.createdAt = createdAt;
    }
}
