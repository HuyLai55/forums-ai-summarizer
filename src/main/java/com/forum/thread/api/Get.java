package com.forum.thread.api;

import com.forum.comment.domain.Comment;
import com.forum.thread.domain.Thread;
import com.forum.thread.domain.ThreadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("getThread")
@RequestMapping("api/v1/thread")
public class Get {
    @Autowired
    ThreadRepo threadRepo;
    @GetMapping("/{sourceTypeStr}")
    public Page<Thread> getAll(@PathVariable String sourceTypeStr, @RequestParam int page, @RequestParam int pageSize) {
        Comment.SourceType sourceType = Comment.SourceType.valueOf(sourceTypeStr);
        Page<Thread> threadPage = threadRepo.findThreadsPerPageWithCache(sourceType.toString(), page-1, pageSize);

        return threadPage;
    }
}
