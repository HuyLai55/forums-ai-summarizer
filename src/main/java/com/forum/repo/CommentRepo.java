package com.forum.repo;

import com.forum.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CommentRepo extends CrudRepository<Comment, Long>, PagingAndSortingRepository<Comment, Long> {
    List<Comment> findByThreadId(Long threadId, Pageable pageable);

    List<Comment> findByThreadId(Long threadId);
}
