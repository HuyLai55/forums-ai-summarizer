
package com.forum.api.thread;

import com.forum.repo.ThreadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("GetThreadSummary")
@RequestMapping("api/v1/threads")
public class GetSummary {
    @Autowired
    ThreadRepo threadRepo;

    @GetMapping("/{url}/summary")
    public String getSummary(@PathVariable String url) {
        return "";
    }
}
