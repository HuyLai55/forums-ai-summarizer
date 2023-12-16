package com.forums.comment;

import java.time.LocalDateTime;

public class Comment {
    public Long id;
    public String userName;
    public String userTitle;
    public String comment;
    public LocalDateTime created;

    public Comment() {

    }

    public Comment(String userName, String userTitle, String comment, LocalDateTime created) {
        this.userName = userName;
        this.userTitle = userTitle;
        this.comment = comment;
        this.created = created;
    }

}
