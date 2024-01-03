package com.forum.service.crawler;

import com.forum.domain.Comment;
import com.forum.repo.CommentRepo;
import com.forum.domain.Thread;
import com.forum.repo.ThreadRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class OtoSaigonCrawler {
    @Autowired
    ThreadRepo threadRepo;
    @Autowired
    CommentRepo commentRepo;

    public Thread crawlThreadInfo(String url) throws IOException {
        Thread thread = threadRepo.findByThreadUrl(url);
        if (thread == null) {
            thread = new Thread();
            thread.setSource(Thread.Source.otosaigon);
            thread.setThreadUrl(url);

            Document document = Jsoup.connect(url).get();
            Elements elmComments = document.getElementsByClass("message message--post  js-post js-inlineModContainer  ");
            Elements elmUser = elmComments.get(0).getElementsByClass("message-name");
            thread.setCreator(elmUser.text());

            Elements elmDate = elmComments.get(0).getElementsByClass("u-dt");
            String dateCreatedToString = elmDate.attr("datetime");
            LocalDateTime createdAt = convertStringToDateTime(dateCreatedToString);
            thread.setCreatedAt(createdAt);

            thread.setTitle(document.getElementsByClass("p-title-value").text());
            thread = threadRepo.save(thread);
        }

        return thread;
    }

    @Async
    public void crawlComments(String url, Thread thread) throws IOException {
        crawlNewCommentOnePageOnly(url, thread);
        Optional<String> nextPage = extractNextUrl(url);
        while (nextPage.isPresent()) {
            url = nextPage.get();
            crawlNewCommentOnePageOnly(url, thread); //crawling next Page
            nextPage = extractNextUrl(url);
        }
    }


    private Optional<String> extractNextUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        Elements nextPage = document.getElementsByClass("pageNav-jump pageNav-jump--next");

        if (nextPage.size() > 0) {
            return Optional.of(nextPage.get(0).absUrl("href"));
        }

        return Optional.empty();
    }

    // lấy tất cả comments
    private void crawlNewCommentOnePageOnly(String url, Thread thread) throws IOException {
        Document document = Jsoup.connect(url).get();
        document.outputSettings().prettyPrint(false);
        document.select("br").before("\\n");
        document.select("p").before("\\n");
        List<Comment> commentsIsSaved = commentRepo.findByThreadId(thread.getThreadId());
        LocalDateTime timeLastComment = thread.getCreatedAt();
        if (!commentsIsSaved.isEmpty()) {
            timeLastComment = commentsIsSaved.get(commentsIsSaved.size() - 1).getCreatedAt();
        }

        Elements elmComments = document.getElementsByClass("message message--post  js-post js-inlineModContainer  ");
        for (int i = 0; i < elmComments.size(); i++) {
            Elements elmDates = elmComments.get(i).getElementsByClass("u-dt");
            String dateCreatedToString = elmDates.attr("datetime");
            LocalDateTime timeOfCommentIsCrawling = convertStringToDateTime(dateCreatedToString);
            if (timeOfCommentIsCrawling.isAfter(timeLastComment)) {
                Comment comment = new Comment();
                comment.setThreadId(thread.getThreadId());
                Elements elmUsers = elmComments.get(i).getElementsByClass("message-name");
                comment.setUsername(elmUsers.text());
                comment.setUserRank(elmComments.get(i).getElementsByClass("userTitle message-userTitle").text());
                comment.setCreatedAt(timeOfCommentIsCrawling);
                Elements elmComment = elmComments.get(i).getElementsByClass("bbWrapper");
                comment.setComment(extractString(elmComment.text()));
                commentRepo.save(comment);
            }
        }
    }

    private String removeLetter(String time) {
        String timeAfterRemoveCharT = time.replace("T", " ");
        return timeAfterRemoveCharT.substring(0, 19);
    }

    private LocalDateTime convertStringToDateTime(String dateTimeInString) {
        String dateTimeAfterRemoveLetter = removeLetter(dateTimeInString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeAfterRemoveLetter, formatter);
    }

    private String extractString(String strHTML) {
        strHTML = strHTML.replaceAll("\\\\n", "\n");
        return Jsoup.clean(strHTML, "", Safelist.none());
    }
}

