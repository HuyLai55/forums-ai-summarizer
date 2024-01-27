package com.forum.service.crawler;

import com.forum.domain.Comment;
import com.forum.domain.ThreadSummary;
import com.forum.repo.CommentRepo;
import com.forum.domain.Thread;
import com.forum.repo.ThreadRepo;
import com.forum.repo.ThreadSummaryRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Component
public class VozCrawler {
    @Autowired
    ThreadRepo threadRepo;
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    ThreadSummaryRepo threadSummaryRepo;
    @Value("${user-agent}")
    String userAgent;

    public Thread getInfoThread(String url) throws IOException {
        Thread thread = threadRepo.findByThreadUrl(url);
        if (thread == null) {
            Document document = Jsoup.connect(url).userAgent(userAgent).get();
            thread = new Thread();
            thread.setThreadUrl(url);
            thread.setSource(Thread.Source.voz);
            String creator = document.getElementsByClass("username  u-concealed").text();
            thread.setCreator(creator);
            String threadTitle = document.getElementsByClass("p-title-value").text();
            thread.setTitle(threadTitle);
            LocalDateTime dateTimeThread = convertStringToDateTime(document.getElementsByClass("listInline listInline--bullet")
                    .select("li").select("a").select("time.u-dt").attr("datetime"));
            thread.setCreatedAt(dateTimeThread);

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
        Document document = Jsoup.connect(url).userAgent(userAgent).get();
        Elements nextPage = document.getElementsByClass("pageNav-jump pageNav-jump--next");
        if (nextPage.size() > 0) {
            return Optional.of(nextPage.get(0).absUrl("href"));
        }

        return Optional.empty();
    }

    // lấy tất cả comments
    private void crawlNewCommentOnePageOnly(String url, Thread thread) throws IOException {
        ThreadSummary threadSummary = threadSummaryRepo.findByThreadId(thread.getThreadId());
        Long commentsTokensLength = threadSummary.getCommentsTokensLength();
        List<Comment> commentsIsSaved = commentRepo.findByThreadId(thread.getThreadId());
        LocalDateTime timeLastCommentIsSaved = thread.getCreatedAt();
        if (!commentsIsSaved.isEmpty()) {
            timeLastCommentIsSaved = commentsIsSaved.get(commentsIsSaved.size() - 1).getCreatedAt();
        }
        Document document = Jsoup.connect(url).userAgent(userAgent).get();
        document.outputSettings().prettyPrint(false);
        document.select("br").before("\\n");
        document.select("p").before("\\n");

        Elements elmCommentInfo = document.getElementsByClass("message message--post js-post js-inlineModContainer  ");
        for (int i = 0; i < elmCommentInfo.size(); i++) {
            Elements elmDates = elmCommentInfo.get(i).getElementsByClass("message-attribution-main listInline ")
                    .select("li.u-concealed").select("a").select("time.u-dt");
            String dateCreatedToString = elmDates.attr("datetime");
            LocalDateTime dateCreated = convertStringToDateTime(dateCreatedToString);
            if (dateCreated.isAfter(timeLastCommentIsSaved)) {
                Comment comment = new Comment();
                comment.setThreadId(thread.getThreadId());
                comment.setCreatedAt(dateCreated);
                Elements elmUsers = elmCommentInfo.get(i).getElementsByClass("message-name");
                String userName = elmUsers.text();
                comment.setUsername(userName);
                String userRank = elmCommentInfo.get(i).getElementsByClass("userTitle message-userTitle").text();
                comment.setUserRank(userRank);
                Elements elmComments = elmCommentInfo.get(i).getElementsByClass("bbWrapper");
                String contentComment = extractString(elmComments.text());
                StringTokenizer stringTokenizer = new StringTokenizer(contentComment);
                Long tokensLengthInComment = (long) stringTokenizer.countTokens();
                commentsTokensLength += tokensLengthInComment;
                comment.setComment(contentComment);
                commentRepo.save(comment);
            }
            threadSummary.setCommentsTokensLength(commentsTokensLength);
            threadSummaryRepo.save(threadSummary);
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
