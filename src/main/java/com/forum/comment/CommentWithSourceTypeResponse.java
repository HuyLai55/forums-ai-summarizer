package com.forum.comment;

import jakarta.persistence.*;

import java.time.LocalDateTime;


public class CommentWithSourceTypeResponse {
    public Long commentId;
    public Long threadId;
    public String sourceType;
    public String userName;
    public String userTitle;
    public String comment;
    public LocalDateTime createdAt;

    public CommentWithSourceTypeResponse() {
    }

//    public CommentWithSourceTypeResponse(Map<String, Object> r) {
//        this.commentId = (Long) r.get("comment_id");
//        this.threadId = (Long) r.get("thread_id");
//        this.sourceType = (String) r.get("source_type");
//        this.userName = (String) r.get("user_name");
//        this.userTitle = (String) r.get("user_title");
//        this.comment = (String) r.get("comment");
//        this.createdAt = ((Timestamp) r.get("created_at")).toLocalDateTime();
//    }
}
