package com.forum.api.thread;

import com.forum.service.crawler.OtoSaigonCrawler;
import com.forum.service.crawler.TinhTeCrawler;
import com.forum.service.crawler.VozCrawler;
import com.forum.domain.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("crawlThread")
@RequestMapping("/api/v1/threads")
public class Crawl {
    @Autowired
    OtoSaigonCrawler otoSaigonCrawler;
    @Autowired
    TinhTeCrawler tinhTeCrawler;
    @Autowired
    VozCrawler vozCrawler;

    @PostMapping
    public Thread crawl(@RequestBody CrawRequestDto request) throws IOException {
        Thread thread = null;
        if (request.getSource() == Thread.Source.otosaigon) {
            thread = otoSaigonCrawler.crawlThreadInfo(request.getUrl());
            otoSaigonCrawler.crawlComments(request.getUrl(), thread);
        } else if (request.getSource() == Thread.Source.tinhte) {
            thread = tinhTeCrawler.crawlThreadInfo(request.getUrl());
            tinhTeCrawler.crawlComments(request.getUrl(), thread);
        } else if (request.getSource() == Thread.Source.voz) {
            thread = vozCrawler.getInfoThread(request.getUrl());
            vozCrawler.crawlComments(request.getUrl(), thread);
        }

        if (thread == null) {
            throw new RuntimeException("not support");
        }

        return thread;
    }
}
