package com.forum.comment.domain;

import com.forum.comment.CommentWithSourceTypeResponse;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "forum_comment")
@Table(name = "forum_comment")
@NamedNativeQuery(name = "comment_with_source_type_query",
        query = "select c.comment_id as commentId" +
                ", c.thread_id as threadId" +
                ", c.user_name as userName" +
                ", t.source_type as sourceType" +
                ", c.user_title as userTitle" +
                ", c.comment as comment" +
                ", c.created_at as createdAt from forum_comment as c" +
                ", forum_thread as t " +
                "where c.thread_id = t.thread_id " +
                "and t.source_type = :sourceTypeParam",
        resultSetMapping = "comment_with_source_type_dto")
@SqlResultSetMapping(
        name = "comment_with_source_type_dto",
        classes = @ConstructorResult(
                targetClass = CommentWithSourceTypeResponse.class,
                columns = {
                        @ColumnResult(name = "commentId", type = Long.class),
                        @ColumnResult(name = "threadId", type = Long.class),
                        @ColumnResult(name = "userName", type = String.class),
                        @ColumnResult(name = "sourceType", type = String.class),
                        @ColumnResult(name = "userTitle", type = String.class),
                        @ColumnResult(name = "comment", type = String.class),
                        @ColumnResult(name = "createdAt", type = LocalDateTime.class)
                }
        )
)

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    public Long commentId;

    @Column(name = "thread_id")
    public Long threadId;

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

    public Comment(Long threadId, String userName, String userTitle, String comment, LocalDateTime created) {
        this.threadId = threadId;
        this.userName = userName;
        this.userTitle = userTitle;
        this.comment = comment;
        this.createdAt = created;
    }

    public enum SourceType {
        oToSaiGon, tinhTe, voz
    }

}
