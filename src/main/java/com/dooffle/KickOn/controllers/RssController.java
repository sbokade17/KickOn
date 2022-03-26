package com.dooffle.KickOn.controllers;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/feeds")
public class RssController {


    @GetMapping
    public ResponseEntity<SyndFeed> getRssFeed() throws IOException, FeedException {
        URL feedSource = new URL("https://rss.art19.com/apology-line");
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        return ResponseEntity.status(HttpStatus.OK).body(feed);
    }
}
