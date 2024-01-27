
package com.forum.api.comment;

import com.forum.domain.Comment;
import com.forum.repo.CommentRepo;
import com.forum.domain.Thread;
import com.forum.repo.ThreadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("GetComments")
@RequestMapping("/api/v1/threads")
public class Get {
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    ThreadRepo threadRepo;

    @GetMapping("/comments")
    public List<Comment> get(
            @RequestParam String url,
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int pageSize) {
        Thread thread = threadRepo.findByThreadUrl(url);
        System.out.println("url: " + url);
        Pageable sortedByCreatedAt = PageRequest.of(page - 1, pageSize, Sort.by("createdAt"));

        return commentRepo.findByThreadId(thread.getThreadId(), sortedByCreatedAt);
    }

    @GetMapping("/{threadId}/comments")
    public String getByThreadId(@PathVariable Long threadId) {
        List<Comment> list = commentRepo.findByThreadId(threadId);
        StringBuilder comments = new StringBuilder();
        for (Comment c : list) {
            comments.append(c.getComment());
        }
        return comments.toString();
    }
}
