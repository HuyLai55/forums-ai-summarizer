package com.forum.thread;

import jakarta.persistence.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_thread")
public class Thread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    public Long threadId;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "title")
    public String title;

    @Column(name = "created_At")
    public LocalDateTime createdAt;

    public Thread(){}

    public Thread(String title) {
        this.title = title;
    }

    public Thread(String userName, String title, LocalDateTime createdAt) {
        this.userName = userName;
        this.title = title;
        this.createdAt = createdAt;
    }
}
