package com.forum.api;

import com.forum.comment.Comment;
import com.forum.crawler.OtoSaigonCrawler;
import com.forum.crawler.TinhTeCrawler;
import com.forum.crawler.VozCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController("crawlerInfo")
@RequestMapping("/api/v1/crawler/{sourceTypeStr}")
public class Crawler {
    @Autowired
    OtoSaigonCrawler otoSaigonCrawler;
    @Autowired
    TinhTeCrawler tinhTeCrawler;
    @Autowired
    VozCrawler vozCrawler;

    @GetMapping
    public List<Comment> get(@RequestParam String url, @PathVariable String sourceTypeStr) throws IOException {
        List<Comment> commentList = new ArrayList<>();
        if (url == null) {
            throw new IllegalArgumentException("url illegal exception");
        }
        Comment.SourceType sourceType = Comment.SourceType.valueOf(sourceTypeStr.substring(0, sourceTypeStr.length()));
        if (sourceType == Comment.SourceType.oToSaiGon) {
            commentList = otoSaigonCrawler.getListComments(url);
        } else if (sourceType == Comment.SourceType.tinhTe) {
            commentList = tinhTeCrawler.getListComments(url, url);
        } else if (sourceType == Comment.SourceType.voz) {
            commentList = vozCrawler.getListComments(url);
        }
        return commentList;
    }
}
