package com.forum.crawler;

import com.forum.comment.Comment;
import com.forum.comment.CommentRepo;
import com.forum.thread.Thread;
import com.forum.thread.ThreadRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class VozCrawler {
    @Autowired
    ThreadRepo threadRepo;
    @Autowired
    CommentRepo commentRepo;
    @Value("${user-agent}")
    String userAgent;

    public List<Comment> getListComments(String url) throws IOException {
        Thread infoThreadInWeb = getInfoThread(url);
        Thread thread = threadRepo.findOne(ThreadRepo.Spec.byUserAndTitle(infoThreadInWeb.userName, infoThreadInWeb.title)).orElse(null);
        List<Comment> commentIsSaved;
        int sizeOfCommentIsSaved = 0;
        if (thread != null) {
            commentIsSaved = commentRepo.findAll(CommentRepo.Spec.byThreadId(thread.threadId));
            sizeOfCommentIsSaved = commentIsSaved.size();
        }

        List<Comment> commentList = new ArrayList<>();
        if (thread == null) {
            thread = threadRepo.save(infoThreadInWeb);
        }
        commentList.addAll(crawOnePageOnly(url, thread.threadId));
        Optional<String> nextPage = extractNextUrl(url);
        while (nextPage.isPresent()) {
            url = nextPage.get();
            commentList.addAll(crawOnePageOnly(url, thread.threadId)); //crawling next Page
            nextPage = extractNextUrl(url);
        }
        int amountNewComment = commentList.size() - sizeOfCommentIsSaved;
        if (amountNewComment > 0) {
            for (int i = 0; i < amountNewComment; i++) {
                commentRepo.save(commentList.get(sizeOfCommentIsSaved + i));
            }
        }

        return commentList;
    }

    private Thread getInfoThread(String url) throws IOException {
        Document document = Jsoup.connect(url).userAgent(userAgent).get();
        String threadUserName = document.getElementsByClass("username  u-concealed").text();
        String threadTitle = document.getElementsByClass("p-title-value").text();
        LocalDateTime dateTimeThread = convertStringToDateTime(document.getElementsByClass("listInline listInline--bullet")
                .select("li").select("a").select("time.u-dt").attr("datetime"));

        return new Thread(threadUserName, threadTitle, dateTimeThread);
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
    private List<Comment> crawOnePageOnly(String url, Long threadId) throws IOException {
        List<Comment> listComments = new ArrayList<>();
        Document document = Jsoup.connect(url).userAgent(userAgent).get();
        document.outputSettings().prettyPrint(false);
        document.select("br").before("\\n");
        document.select("p").before("\\n");

        Elements elmCommentInfo = document.getElementsByClass("message message--post js-post js-inlineModContainer  ");
        for (int i = 0; i < elmCommentInfo.size(); i++) {
            Elements elmUsers = elmCommentInfo.get(i).getElementsByClass("message-name");
            String userName = elmUsers.text();
            String userTitle = elmCommentInfo.get(i).getElementsByClass("userTitle message-userTitle").text();

            Elements elmDates = elmCommentInfo.get(i).getElementsByClass("message-attribution-main listInline ")
                    .select("li.u-concealed").select("a").select("time.u-dt");
            String dateCreatedToString = elmDates.attr("datetime");
            LocalDateTime dateCreated = convertStringToDateTime(dateCreatedToString);

            Elements elmComments = elmCommentInfo.get(i).getElementsByClass("bbWrapper");
            String comment = extractString(elmComments.text());

            Comment commentInfo = new Comment(threadId, userName, userTitle, comment, dateCreated);
            listComments.add(commentInfo);
        }
        return listComments;
    }

    private String removeLetter(String time) {
        String timeAfterRemoveCharT = time.replace("T", " ");
        String timeAfterRemoveZoneId = timeAfterRemoveCharT.substring(0, 19);
        return timeAfterRemoveZoneId;
    }

    private LocalDateTime convertStringToDateTime(String dateTimeInString) {
        String dateTimeAfterRemoveLetter = removeLetter(dateTimeInString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTimeInLocal = LocalDateTime.parse(dateTimeAfterRemoveLetter, formatter);
        return dateTimeInLocal;
    }

    private String extractString(String strHTML) {
        strHTML = strHTML.replaceAll("\\\\n", "\n");
        String strWithNewLines = Jsoup.clean(strHTML, "", Safelist.none());
        return strWithNewLines;
    }
}
