package com.forum.service.crawler;

import com.forum.domain.Comment;
import com.forum.repo.CommentRepo;
import com.forum.domain.Thread;
import com.forum.repo.ThreadRepo;
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
import java.util.*;

@Component
public class TinhTeCrawler {
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    ThreadRepo threadRepo;

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
        Elements currentPageByClass = document.getElementsByClass("jsx-2305813501 page current");
        if (currentPageByClass.size() == 0) {
            return Optional.empty();
        }
        Element currentPage = currentPageByClass.get(0);
        String currentPageInText = currentPage.text();
        Integer currentPageInInt = Integer.valueOf(currentPageInText);
        Elements pageDifPageInBottom = document.getElementsByClass("jsx-2305813501 page ");

        for (int i = 0; i < pageDifPageInBottom.size() / 2; i++) {
            String pageDifInText = pageDifPageInBottom.get(i).text();
            Integer nextPageInInt = Integer.valueOf(pageDifInText);
            if (nextPageInInt == currentPageInInt + 1) {
                return Optional.of(urlPage1 + "/page-" + nextPageInInt);
            }
        }

        return Optional.empty();
    }

    private void crawlNewCommentOnePageOnly(String url, Thread thread) throws IOException {
        Document document = Jsoup.connect(url).get();
        List<Comment> commentsIsSaved = commentRepo.findByThreadId(thread.getThreadId());
        LocalDateTime timeLastCommentIsSaved = thread.getCreatedAt();
        if (!commentsIsSaved.isEmpty()) {
            timeLastCommentIsSaved = commentsIsSaved.get(commentsIsSaved.size() - 1).getCreatedAt();
        }

        Elements elmCommentInfo = document.getElementsByClass("jsx-691990575 thread-comment__box   ");
        for (int i = 0; i < elmCommentInfo.size(); i++) {
            if (elmCommentInfo.size() == 0) {
                throw new FileNotFoundException("thread have not comment");
            }
            Elements elmDates = elmCommentInfo.get(i).getElementsByClass("jsx-691990575 thread-comment__date");
            String dateCreatedToString = elmDates.get(0).text();
            LocalDateTime dateCreated = convertCommentTime(dateCreatedToString);
            if (dateCreated.isAfter(timeLastCommentIsSaved)) {
                Comment comment = new Comment();
                comment.setThreadId(thread.getThreadId());
                comment.setCreatedAt(dateCreated);
                String userName = elmCommentInfo.get(i).getElementsByClass("jsx-691990575 author-name").text();
                comment.setUsername(userName);
                String userRank = elmCommentInfo.get(i).getElementsByClass("jsx-691990575 author-rank").text();
                comment.setUserRank(userRank);
                Elements elmComments = elmCommentInfo.get(i).getElementsByClass("xf-body-paragraph");
                String contentComment = elmComments.text();
                comment.setComment(contentComment);
                commentRepo.save(comment);
            }
        }
    }

    private LocalDateTime convertStringToDateTime(String dateTimeInString) {
        String[] splitDateTime = dateTimeInString.split("/|\s|:");
        int day = Integer.parseInt(splitDateTime[0]);
        int month = Integer.parseInt(splitDateTime[1]);
        int year = Integer.parseInt(splitDateTime[2]);
        int hour = Integer.parseInt(splitDateTime[3]);
        int minute = Integer.parseInt(splitDateTime[4]);
        LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute);
        return time;
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
                if (commentTime.charAt(i) == Character.valueOf(' ')) {
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
