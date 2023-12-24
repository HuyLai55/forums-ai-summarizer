package com.forum.comment;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    public Long commentId;

    @Column(name = "user_name")
    public String userName;

    @Column(name = "user_title")
    public String userTitle;

    @Column(name = "comment")
    public String comment;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    public Comment() {

    }

    public Comment(String userName, String userTitle, String comment, LocalDateTime created) {
        this.userName = userName;
        this.userTitle = userTitle;
        this.comment = comment;
        this.createdAt = created;
    }

}
