package com.forum.service.summary;

import com.forum.domain.Comment;
import com.forum.domain.Thread;
import com.forum.repo.CommentRepo;
import com.forum.repo.ThreadRepo;
import com.forum.service.summary.gateway.ChatGptGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;


@Component
public class OpenAISummarize {
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    ThreadRepo threadRepo;
    @Autowired
    ChatGptGateway gateway;

    public String summarize(String threadUrl) throws IOException, InterruptedException {
        Thread thread = threadRepo.findByThreadUrl(threadUrl);
        if (thread == null) {
            throw new IllegalStateException("thread not existed");
        }

        List<Comment> listComment = commentRepo.findByThreadId(thread.getThreadId());
        StringBuilder summarization = new StringBuilder();
        String comments = "";
        int count;
        String prompt;
        int i = 0;

        while (i < listComment.size()) {
            comments += listComment.get(i).getComment() + " ";
            StringTokenizer stringTokenizer = new StringTokenizer(comments);
            count = stringTokenizer.countTokens();
            if (i == listComment.size() - 1) {
                prompt = "Tóm tắt những bình luận này: \n" + comments;
                summarization.append(gateway.makeCall(prompt)).append("\n");
            }
            if (count > 4000) {
                int posLastComment = comments.lastIndexOf(listComment.get(i).getComment());
                comments = comments.substring(posLastComment);
                prompt = "Tóm tắt những bình luận này: \n" + comments;
                summarization.append(gateway.makeCall(prompt)).append("\n");
                comments = "";
                i--;
            }
            i++;
        }

        return summarization.toString();
    }
}
