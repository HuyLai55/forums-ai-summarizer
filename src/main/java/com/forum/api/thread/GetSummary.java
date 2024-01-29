
package com.forum.api.thread;

import com.forum.domain.Thread;
import com.forum.domain.ThreadSummary;
import com.forum.repo.ThreadRepo;
import com.forum.repo.ThreadSummaryRepo;
import com.forum.service.summary.OpenAISummarize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("GetThreadSummary")
@RequestMapping("/api/v1/threads")
public class GetSummary {
    @Autowired
    OpenAISummarize openAISummarize;
    @Autowired
    ThreadSummaryRepo threadSummaryRepo;
    @Autowired
    ThreadRepo threadRepo;

    @PostMapping("/summary")
    public String getSummary(@RequestParam String url) throws IOException, InterruptedException {
        String summarizationOfComments = openAISummarize.summarize(url);
        Thread thread = threadRepo.findByThreadUrl(url);
        ThreadSummary threadSummary = threadSummaryRepo.findByThreadId(thread.getThreadId());
        threadSummary.setCommentsSummary(summarizationOfComments);
        threadSummaryRepo.save(threadSummary);
        return summarizationOfComments;
    }
}
