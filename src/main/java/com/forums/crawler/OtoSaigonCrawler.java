package com.forums.crawler;

import com.forums.comment.Comment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OtoSaigonCrawler {

    public List<Comment> getListComments(String url) throws IOException {
        List<Comment> listComments = new ArrayList<>();
        listComments.addAll(crawOnePageOnly(url));

        Optional<String> nextUrl = extractNextUrl(url);
        while (nextUrl.isPresent()) {
            url = nextUrl.get();
            System.out.println("crawling of url " + url);
            listComments.addAll(crawOnePageOnly(url));
            nextUrl = extractNextUrl(url);

            System.out.println("size of comment " + listComments.size());
        }

        return listComments;
    }

    private Optional<String> extractNextUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        Elements nextPage = document.getElementsByClass("pageNav-jump--next");

        if (nextPage.size() > 0) {
            return Optional.of(nextPage.get(0).absUrl("href"));
        }

        return Optional.empty();
    }

    // lấy tất cả comments
    private List<Comment> crawOnePageOnly(String url) throws IOException {
        List<Comment> listComments = new ArrayList<>();
        Document document = Jsoup.connect(url).get();
        document.outputSettings().prettyPrint(false);
        document.select("br").before("\\n");
        document.select("p").before("\\n");

        Elements elmCommentInfo = document.getElementsByClass("message message--post  js-post js-inlineModContainer  ");
        for (int i = 0; i < elmCommentInfo.size(); i++) {
            String userName = null;
            String userTitle = null;
            LocalDateTime dateCreated = null;
            String comment = null;
            Elements elmUsers = elmCommentInfo.get(i).getElementsByClass("message-name");
            userName = elmUsers.text();
            userTitle = elmCommentInfo.get(i).getElementsByClass("userTitle message-userTitle").text();

            Elements elmDates = elmCommentInfo.get(i).getElementsByClass("u-dt");
            String dateCreatedToString = elmDates.attr("datetime");
            dateCreated = convertStringToDateTime(dateCreatedToString);

            Elements elmComments = elmCommentInfo.get(i).getElementsByClass("bbWrapper");
            comment = extractString(elmComments.html());

            Comment commentInfo = new Comment(userName, userTitle, comment, dateCreated);
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
