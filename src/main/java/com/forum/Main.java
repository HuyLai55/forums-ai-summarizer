package com.forum;


import com.forum.comment.Comment;
import com.forum.crawler.OtoSaigonCrawler;
import com.forum.crawler.TinhTeCrawler;
import com.forum.crawler.VozCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.PrintStream;
import java.util.List;


@SpringBootApplication(
        scanBasePackages = {"com.forum"}
)
public class Main {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context =  SpringApplication.run(Main.class, args);

        String url = "https://voz.vn/t/nho-ae-tu-van-con-tv-65inch-best-p-p-thoi-diem-hien-tai.878335/";

        VozCrawler vozCrawler = context.getBean(VozCrawler.class);
        List<Comment> list = vozCrawler.getListComments(url);

        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        for (Comment comment : list) {
            out.println("------");
            out.println(comment.userName);
            out.println(comment.userTitle);
            out.println(comment.comment);
            out.println(comment.createdAt);
        }
    }
}