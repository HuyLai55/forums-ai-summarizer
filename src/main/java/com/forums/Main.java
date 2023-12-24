package com.forums;


import com.forums.comment.Comment;
import com.forums.crawler.TinhTeCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;


@SpringBootApplication(
        scanBasePackages = {"com.forums"}
)
public class Main {
    static Connection conn;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
        String url = "https://tinhte.vn/thread/insta360-dong-bo-thong-so-tap-luyen-cua-garmin-va-apple-watch-vao-video.3749873/";
        TinhTeCrawler tinhTeCrawler = new TinhTeCrawler();
        List<Comment> list = tinhTeCrawler.getListComments(url, url);

        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        for (Comment comment : list) {
            out.println("------");
            out.println(comment.userName);
            out.println(comment.userTitle);
            out.println(comment.comment);
            out.println(comment.createdAt);
        }

        String urlDB = "jdbc:sqlite:sqlite_master";
        conn = DriverManager.getConnection(urlDB);
        Main app = new Main();
        app.checkTablesThread();
        app.checkTablesComment();
    }

    private void checkTablesComment() {
        System.out.println("Check table comment");
        String sql = "CREATE TABLE IF NOT EXISTS forum_comment (" +
                "comment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_name nvarchar(128) NOT NULL," +
                "user_title nvarchar(64) NOT NULL," +
                "comment text NOT NULL," +
                "created_at text NOT NULL" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void checkTablesThread() {
        System.out.println("Check table thread");
        String sql = "CREATE TABLE IF NOT EXISTS forum_thread (" +
                "thread_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_name nvarchar(128) NOT NULL," +
                "title nvarchar(256) NOT NULL," +
                "created_at text NOT NULL" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception err) {
            System.out.println(err);
        }
    }
}