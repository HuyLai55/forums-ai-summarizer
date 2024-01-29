package com.forum.service.crawler;

import com.forum.domain.Comment;
import com.forum.domain.Thread;
import com.forum.domain.ThreadSummary;
import com.forum.repo.CommentRepo;
import com.forum.repo.ThreadRepo;
import com.forum.repo.ThreadSummaryRepo;
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
import java.util.StringTokenizer;

@Component
public class OtoSaigonCrawler {
    @Autowired
    ThreadRepo threadRepo;
    @Autowired
    ThreadSummaryRepo threadSummaryRepo;
    @Autowired
    CommentRepo commentRepo;

    public Thread crawlThreadInfo(String url) throws IOException {
        Thread thread = threadRepo.findByThreadUrl(url);
        if (thread == null) {
            thread = new Thread();
            thread.setSource(Thread.Source.otosaigon);
            thread.setThreadUrl(url);

            Document document = Jsoup.connect(url).get();
            Elements commentElements = document.getElementsByClass("message message--post  js-post js-inlineModContainer  ");
            Elements userElements = commentElements.get(0).getElementsByClass("message-name");
            thread.setCreator(userElements.text());
            thread.setTitle(document.getElementsByClass("p-title-value").text());

            Elements dateElement = commentElements.get(0).getElementsByClass("u-dt");
            String formatStringDate = dateElement.attr("datetime");
            LocalDateTime threadIsCreatedAt = convertStringToDateTime(formatStringDate);
            thread.setCreatedAt(threadIsCreatedAt);

            thread = threadRepo.save(thread);

            ThreadSummary threadSummary = new ThreadSummary();
            threadSummary.setThreadId(thread.getThreadId());
            threadSummary.setCommentsTokensLength(0L);

            threadSummaryRepo.save(threadSummary);
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
        ThreadSummary threadSummary = threadSummaryRepo.findByThreadId(thread.getThreadId());
        Long commentsTokensLength = threadSummary.getCommentsTokensLength();
        List<Comment> commentsIsSaved = commentRepo.findByThreadId(thread.getThreadId());
        LocalDateTime timeLastComment = thread.getCreatedAt();
        if (!commentsIsSaved.isEmpty()) {
            timeLastComment = commentsIsSaved.get(commentsIsSaved.size() - 1).getCreatedAt();
        }

        Elements commentElements = document.getElementsByClass("message message--post  js-post js-inlineModContainer  ");
        for (int i = 0; i < commentElements.size(); i++) {
            Elements dateElements = commentElements.get(i).getElementsByClass("u-dt");
            String formatStringDate = dateElements.attr("datetime");
            LocalDateTime commentTimeIsCrawling = convertStringToDateTime(formatStringDate);
            if (commentTimeIsCrawling.isAfter(timeLastComment)) {
                Comment comment = new Comment();
                comment.setThreadId(thread.getThreadId());
                Elements userElements = commentElements.get(i).getElementsByClass("message-name");
                comment.setUsername(userElements.text());
                comment.setUserRank(commentElements.get(i).getElementsByClass("userTitle message-userTitle").text());
                comment.setCreatedAt(commentTimeIsCrawling);
                Elements commentElement = commentElements.get(i).getElementsByClass("bbWrapper");
                String content = extractString(commentElement.text());
                StringTokenizer stringTokenizer = new StringTokenizer(content);
                long commentTokenLength = stringTokenizer.countTokens();
                commentsTokensLength += commentTokenLength;
                comment.setComment(content);
                commentRepo.save(comment);
            }
        }
        threadSummary.setCommentsTokensLength(commentsTokensLength);
        threadSummaryRepo.save(threadSummary);
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

