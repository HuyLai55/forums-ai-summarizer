package com.forums;


import com.forums.comment.Comment;
import com.forums.crawler.TinhTeCrawler;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://tinhte.vn/thread/minh-nang-cap-goc-lam-viec-voi-ban-nang-ha-di-dong.3748105";
        TinhTeCrawler tinhTeCrawler = new TinhTeCrawler();
        List<Comment> list = tinhTeCrawler.getListComments(url, url);

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