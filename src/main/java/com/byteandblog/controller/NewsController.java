package com.byteandblog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "https://byteandblogapp.onrender.com")
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Value("${news.api.key}")
    private String newsApiKey;

    @GetMapping("/top-headlines")
    public ResponseEntity<?> getTopHeadlines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "us") String country) {
        try {
            logger.info("Fetching top headlines: page={}, pageSize={}, country={}", page, pageSize, country);
            String url = String.format(
                    "https://newsapi.org/v2/top-headlines?apiKey=%s&page=%d&pageSize=%d&country=%s",
                    newsApiKey, page, pageSize, country);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            logger.info("Successfully fetched top headlines");
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            logger.error("Failed to fetch top headlines: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed to fetch news: " + e.getMessage());
        }
    }
}
