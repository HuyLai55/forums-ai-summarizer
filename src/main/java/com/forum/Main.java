package com.forum;


import com.forum.comment.Comment;
import com.forum.crawler.TinhTeCrawler;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;


@SpringBootApplication(
        scanBasePackages = {"com.forum"}
)
public class Main {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context =  SpringApplication.run(Main.class, args);

        String url = "https://tinhte.vn/thread/minh-nang-cap-goc-lam-viec-voi-ban-nang-ha-di-dong.3748105";

        TinhTeCrawler tinhTeCrawler = context.getBean(TinhTeCrawler.class);
        List<Comment> list = tinhTeCrawler.getListComments(url, url);

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