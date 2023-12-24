package com.forum.comment;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepo extends CrudRepository<Comment, Long> {
}
