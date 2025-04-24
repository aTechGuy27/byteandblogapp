package com.byteandblog.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@CrossOrigin(origins = "https://byteandblog.onrender.com")
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @GetMapping("/top-headlines")
    public ResponseEntity<?> getTopHeadlines() { // Removed @RequestParam parameters
        try {
            logger.info("Fetching top headlines");
            // Fetch NPR RSS feed
            String rssUrl = "https://www.npr.org/rss/rss.php?id=1001";
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(rssUrl)));

            // Get all articles
            List<SyndEntry> entries = feed.getEntries();
            int totalResults = entries.size();
            List<Map<String, Object>> articles = new ArrayList<>();

            // Pattern to extract image URL from content:encoded
            Pattern imagePattern = Pattern.compile("<img src='(.*?)'");

            // Map all entries to articles
            for (SyndEntry entry : entries) {
                Map<String, Object> article = new HashMap<>();
                article.put("title", entry.getTitle());
                article.put("description", entry.getDescription() != null ? entry.getDescription().getValue() : "");
                article.put("url", entry.getLink());
                article.put("publishedAt", entry.getPublishedDate() != null ? entry.getPublishedDate().toString() : "");
                article.put("source", Map.of("name", "NPR"));
                article.put("author", entry.getAuthor());

                // Extract image URL from content:encoded
                String contentEncoded = entry.getContents().isEmpty() ? "" : entry.getContents().get(0).getValue();
                String urlToImage = null;
                if (contentEncoded != null) {
                    Matcher matcher = imagePattern.matcher(contentEncoded);
                    if (matcher.find()) {
                        urlToImage = matcher.group(1);
                    }
                }
                article.put("urlToImage", urlToImage);

                articles.add(article);
            }

            // Construct response in NewsAPI-compatible format
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("totalResults", totalResults);
            response.put("articles", articles);

            logger.info("Successfully fetched top headlines");
            return ResponseEntity.ok(new ObjectMapper().writeValueAsString(response));
        } catch (Exception e) {
            logger.error("Failed to fetch top headlines: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to fetch news: " + e.getMessage());
        }
    }
}
