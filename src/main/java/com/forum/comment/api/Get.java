package com.forum.comment.api;

import com.forum.comment.CommentWithSourceTypeResponse;
import com.forum.comment.domain.Comment;
import com.forum.comment.domain.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController("getComment")
@RequestMapping("/api/v1/comment")
public class Get {
    @Autowired
    CommentRepo commentRepo;
    @GetMapping
    public Page<Comment> getAllByThreadId(@RequestParam Long threadId, @RequestParam int page, @RequestParam int pageSize) {
        Page<Comment> commentPage = commentRepo.findThreadsByTheadIdPerPageWithCache(threadId, page - 1, pageSize);
        return commentPage;
    }

    @GetMapping("/{sourceTypeStr}")
    public Page<CommentWithSourceTypeResponse> getAllBySourceType(@PathVariable String sourceTypeStr, @RequestParam int page, @RequestParam int pageSize) {
        Comment.SourceType sourceType = Comment.SourceType.valueOf(sourceTypeStr);
        Page<CommentWithSourceTypeResponse> commentPage = commentRepo.findThreadsBySourceTypePerPageWithCache(sourceType.toString(), page - 1, pageSize);
        return commentPage;
    }
}
