package com.forums.crawler;

import com.forums.comment.Comment;
import com.forums.comment.CommentRepo;
import com.forums.thread.Thread;
import com.forums.thread.ThreadRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class TinhTeCrawler {
    CommentRepo commentRepo;
    ThreadRepo threadRepo;

    public List<Comment> getListComments(String url, String urlPage1) throws IOException {
        List<Comment> commentList = new ArrayList<>();
        commentList.addAll(crawOnePageOnly(url));
        Optional<String> nextPage = extractNextUrl(url, urlPage1);
        while (nextPage.isPresent()) {
            url = nextPage.get();
            System.out.println("crawling of url " + url);
            commentList.addAll(crawOnePageOnly(url)); //crawling next Page
            nextPage = extractNextUrl(url, urlPage1);
            System.out.println("size of comment " + commentList.size());
        }

        return commentList;
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
        System.out.println("current Page: " + currentPageInInt);
        Elements pageDifPageInBottom = document.getElementsByClass("jsx-2305813501 page ");

        for (int i = 0; i < pageDifPageInBottom.size() / 2; i++) {
            String pageDifInText = pageDifPageInBottom.get(i).text();
            System.out.println("dif page: " + pageDifInText);
            Integer nextPageInInt = Integer.valueOf(pageDifInText);
            if (nextPageInInt == currentPageInInt + 1) {
                return Optional.of(urlPage1 + "/page-" + nextPageInInt);
            }
        }

        return Optional.empty();
    }

    private List<Comment> crawOnePageOnly(String url) throws IOException {
        List<Comment> listComments = new ArrayList<>();
        Document document = Jsoup.connect(url).get();

        String threadUserName = document.getElementsByClass("jsx-89440 author-name").select("a").text();
        System.out.println("thread user name: " + threadUserName);
        String threadTitle = document.getElementsByClass("jsx-89440 thread-title").text();
        System.out.println("thread title: " + threadTitle);
        LocalDateTime dateTimeThread = convertStringToDateTime(document.getElementsByClass("jsx-89440 date").text());
        System.out.println("date thread: " + dateTimeThread);
        Thread thread = new Thread(threadUserName, threadTitle, dateTimeThread);

        Elements elmCommentInfo = document.getElementsByClass("jsx-691990575 thread-comment__box   ");

        for (int i = 0; i < elmCommentInfo.size(); i++) {
            if (elmCommentInfo.size() == 0) {
                throw new FileNotFoundException("thread have not comment");
            }
            String userName = elmCommentInfo.get(i).getElementsByClass("jsx-691990575 author-name").text();
            String userTitle = elmCommentInfo.get(i).getElementsByClass("jsx-691990575 author-rank").text();

            Elements elmDates = elmCommentInfo.get(i).getElementsByClass("jsx-691990575 thread-comment__date");

            String dateCreatedToString = elmDates.get(0).text();
            LocalDateTime dateCreated = convertCommentTime(dateTimeThread, dateCreatedToString);

            Elements elmComments = elmCommentInfo.get(i).getElementsByClass("xf-body-paragraph");
            String comment = elmComments.text();

            Comment commentInfo = new Comment(userName, userTitle, comment, dateCreated);
            commentRepo.save(commentInfo);
            listComments.add(commentInfo);
        }
        threadRepo.save(thread);

        return listComments;
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
    private LocalDateTime convertCommentTime(LocalDateTime timeCreatedThread, String commentTime) {
        LocalDateTime commentTimeInLocal = timeCreatedThread;
        int commentTimeTypeInt = 0;

        if (commentTime.contains("một")) {
            if (commentTime.contains("phút")) {
                commentTimeInLocal = timeCreatedThread.plusMinutes(1);
            } else if (commentTime.contains("giờ")) {
                commentTimeInLocal = timeCreatedThread.plusHours(1);
            } else if (commentTime.contains("tháng")) {
                commentTimeInLocal = timeCreatedThread.plusHours(1);
            } else if (commentTime.contains("năm")) {
                commentTimeInLocal = timeCreatedThread.plusHours(1);
            }
        } else {
            for (int i = 1; i < commentTime.length(); i++) {
                if (commentTime.charAt(i) == Character.valueOf(' ')) {
                    commentTimeTypeInt = Integer.parseInt(commentTime.substring(0, i));
                }
            }
            if (commentTime.contains("phút")) {
                commentTimeInLocal = timeCreatedThread.plusMinutes(commentTimeTypeInt);
            } else if (commentTime.contains("giờ")) {
                commentTimeInLocal = timeCreatedThread.plusHours(commentTimeTypeInt);
            } else if (commentTime.contains("tháng")) {
                commentTimeInLocal = timeCreatedThread.plusHours(commentTimeTypeInt);
            } else if (commentTime.contains("năm")) {
                commentTimeInLocal = timeCreatedThread.plusHours(commentTimeTypeInt);
            }
        }

        if (commentTime.contains("phút")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = timeCreatedThread.plusMinutes(1);
            } else {
                commentTimeInLocal = timeCreatedThread.plusMinutes(commentTimeTypeInt);
            }
        } else if (commentTime.contains("giờ")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = timeCreatedThread.plusHours(1);
            } else {
                commentTimeInLocal = timeCreatedThread.plusHours(commentTimeTypeInt);
            }
        } else if (commentTime.contains("tháng")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = timeCreatedThread.plusHours(1);
            } else {
                commentTimeInLocal = timeCreatedThread.plusHours(commentTimeTypeInt);
            }
        } else if (commentTime.contains("năm")) {
            if (commentTime.contains("một")) {
                commentTimeInLocal = timeCreatedThread.plusHours(1);
            } else {
                commentTimeInLocal = timeCreatedThread.plusHours(commentTimeTypeInt);
            }
        }

        return commentTimeInLocal;
    }
}
