package com.forum.service.crawler;

import com.forum.domain.Comment;
import com.forum.domain.Thread;
import com.forum.domain.ThreadSummary;
import com.forum.repo.CommentRepo;
import com.forum.repo.ThreadRepo;
import com.forum.repo.ThreadSummaryRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Component
public class TinhTeCrawler {
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    ThreadRepo threadRepo;
    @Autowired
    ThreadSummaryRepo threadSummaryRepo;

    public Thread crawlThreadInfo(String url) throws IOException {
        Thread thread = threadRepo.findByThreadUrl(url);
        if (thread == null) {
            thread = new Thread();
            thread.setSource(Thread.Source.tinhte);
            thread.setThreadUrl(url);

            Document document = Jsoup.connect(url).get();
            thread.setCreator(document.getElementsByClass("jsx-89440 author-name").select("a").text());
            thread.setTitle(document.getElementsByClass("jsx-89440 thread-title").text());
            thread.setCreatedAt(convertStringToDateTime(document.getElementsByClass("jsx-89440 date").text()));

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
        String urlPage1 = url;
        crawlNewCommentOnePageOnly(url, thread);
        Optional<String> nextPage = extractNextUrl(url, urlPage1);
        while (nextPage.isPresent()) {
            url = nextPage.get();
            crawlNewCommentOnePageOnly(url, thread); //crawling next Page
            nextPage = extractNextUrl(url, urlPage1);
        }
    }

    Optional<String> extractNextUrl(String url, String urlPage1) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements currentPageElements = document.getElementsByClass("jsx-2305813501 page current");
        if (currentPageElements.size() == 0) {
            return Optional.empty();
        }
        Element currentPageElement = currentPageElements.get(0);
        String formatStringCurrentPage = currentPageElement.text();
        int currentPage = Integer.parseInt(formatStringCurrentPage);
        Elements pageDifPageInBottom = document.getElementsByClass("jsx-2305813501 page ");

        for (int i = 0; i < pageDifPageInBottom.size() / 2; i++) {
            String pageDifInText = pageDifPageInBottom.get(i).text();
            int nextPage = Integer.parseInt(pageDifInText);
            if (nextPage == currentPage + 1) {
                return Optional.of(urlPage1 + "/page-" + nextPage);
            }
        }

        return Optional.empty();
    }

    private void crawlNewCommentOnePageOnly(String url, Thread thread) throws IOException {
        Document document = Jsoup.connect(url).get();
        ThreadSummary threadSummary = threadSummaryRepo.findByThreadId(thread.getThreadId());
        Long commentsTokensLength = threadSummary.getCommentsTokensLength();
        List<Comment> commentsIsSaved = commentRepo.findByThreadId(thread.getThreadId());
        LocalDateTime timeLastCommentIsSaved = thread.getCreatedAt();
        if (!commentsIsSaved.isEmpty()) {
            timeLastCommentIsSaved = commentsIsSaved.get(commentsIsSaved.size() - 1).getCreatedAt();
        }

        Elements commentElements = document.getElementsByClass("jsx-691990575 thread-comment__box   ");
        for (int i = 0; i < commentElements.size(); i++) {
            if (commentElements.size() == 0) {
                throw new FileNotFoundException("thread have not comment");
            }
            Elements dateElements = commentElements.get(i).getElementsByClass("jsx-691990575 thread-comment__date");
            String formatStringDate = dateElements.get(0).text();
            LocalDateTime commentsDateCreatedAt = convertCommentTime(formatStringDate);
            if (commentsDateCreatedAt.isAfter(timeLastCommentIsSaved)) {
                Comment comment = new Comment();
                comment.setThreadId(thread.getThreadId());
                comment.setCreatedAt(commentsDateCreatedAt);
                String userName = commentElements.get(i).getElementsByClass("jsx-691990575 author-name").text();
                comment.setUsername(userName);
                String userRank = commentElements.get(i).getElementsByClass("jsx-691990575 author-rank").text();
                comment.setUserRank(userRank);
                Elements commentElement = commentElements.get(i).getElementsByClass("xf-body-paragraph");
                String contentComment = commentElement.text();
                StringTokenizer stringTokenizer = new StringTokenizer(contentComment);
                long commentTokenLength = stringTokenizer.countTokens();
                commentsTokensLength += commentTokenLength;
                comment.setComment(contentComment);
                commentRepo.save(comment);
            }
            threadSummary.setCommentsTokensLength(commentsTokensLength);
            threadSummaryRepo.save(threadSummary);
        }
    }

    private LocalDateTime convertStringToDateTime(String dateTimeInString) {
        String[] splitDateTime = dateTimeInString.split("/|\s|:");
        int day = Integer.parseInt(splitDateTime[0]);
        int month = Integer.parseInt(splitDateTime[1]);
        int year = Integer.parseInt(splitDateTime[2]);
        int hour = Integer.parseInt(splitDateTime[3]);
        int minute = Integer.parseInt(splitDateTime[4]);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    //comment time is time from thread is created
    private LocalDateTime convertCommentTime(String commentTime) {
        LocalDateTime commentTimeInLocal = LocalDateTime.now();
        int commentTimeTypeInt = 0;

        if (commentTime.contains("một")) {
            if (commentTime.contains("phút")) {
                commentTimeInLocal = LocalDateTime.now().minusMinutes(1);
            } else if (commentTime.contains("giờ")) {
                commentTimeInLocal = LocalDateTime.now().minusHours(1);
            } else if (commentTime.contains("tháng")) {
                commentTimeInLocal = LocalDateTime.now().minusMonths(1);
            } else if (commentTime.contains("năm")) {
                commentTimeInLocal = LocalDateTime.now().minusYears(1);
            }
        } else {
            for (int i = 1; i < commentTime.length(); i++) {
                if (commentTime.charAt(i) == (' ')) {
                    commentTimeTypeInt = Integer.parseInt(commentTime.substring(0, i));
                }
            }
            if (commentTime.contains("phút")) {
                commentTimeInLocal = LocalDateTime.now().minusMinutes(commentTimeTypeInt);
            } else if (commentTime.contains("giờ")) {
                commentTimeInLocal = LocalDateTime.now().minusHours(commentTimeTypeInt);
            } else if (commentTime.contains("tháng")) {
                commentTimeInLocal = LocalDateTime.now().minusMonths(commentTimeTypeInt);
            } else if (commentTime.contains("năm")) {
                commentTimeInLocal = LocalDateTime.now().minusYears(commentTimeTypeInt);
            }
        }

        if (commentTime.contains("phút")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = LocalDateTime.now().minusMinutes(1);
            } else {
                commentTimeInLocal = LocalDateTime.now().minusMinutes(commentTimeTypeInt);
            }
        } else if (commentTime.contains("giờ")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = LocalDateTime.now().minusHours(1);
            } else {
                commentTimeInLocal = LocalDateTime.now().minusHours(commentTimeTypeInt);
            }
        } else if (commentTime.contains("tháng")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = LocalDateTime.now().minusMonths(1);
            } else {
                commentTimeInLocal = LocalDateTime.now().minusMonths(commentTimeTypeInt);
            }
        } else if (commentTime.contains("năm")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = LocalDateTime.now().minusYears(1);
            } else {
                commentTimeInLocal = LocalDateTime.now().minusYears(commentTimeTypeInt);
            }
        }

        return commentTimeInLocal;
    }
}
