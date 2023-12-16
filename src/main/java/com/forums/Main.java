package com.forums;


import com.forums.comment.Comment;
import com.forums.crawler.OtoSaigonCrawler;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException {
        Helper helper = new Helper();
        String url = "https://www.otosaigon.com/threads/ket-qua-thu-nghiem-mot-so-loai-film-cach-nhiet-08-2008-de-nghi-khong-mua-ban-quang-cao-o-bai-nay.1164815/page-2";
        OtoSaigonCrawler crawler = new OtoSaigonCrawler();
        List<Comment> list = crawler.getListComments(url);

        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        for(Comment comment : list) {
            out.println("------");
            out.println(comment.userName);
            out.println(comment.userTitle);
            out.println(comment.comment);
            out.println(comment.created);
        }

    }
}