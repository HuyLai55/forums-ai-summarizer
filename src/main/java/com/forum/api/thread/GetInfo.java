
package com.forum.api.thread;

import com.forum.domain.Thread;
import com.forum.repo.ThreadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("GetThread")
@RequestMapping("/api/v1/threads")
public class GetInfo {
    @Autowired
    ThreadRepo threadRepo;

    @GetMapping
    public Thread get(@RequestParam String url) {
        return threadRepo.findByThreadUrl(url);
    }
}
