package com.byteandblog.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "https://byteandblog.onrender.com/")
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

            // Create RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            // Add browser-like headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://byteandblogapp.onrender.com");

            // Create HTTP entity with headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the request with headers
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            logger.info("Successfully fetched top headlines");
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
        	logger.error("Failed to fetch top headlines: {}", e.getMessage(), e);
            // Fallback to mock data
            String mockResponse = """
                {
                  "status": "ok",
                  "totalResults": 36,
                  "articles": [
                    {
                      "source": { "id": null, "name": "Mock News" },
                      "author": "Mock Author",
                      "title": "Mock News Article 1",
                      "description": "This is a mock news article for testing purposes.",
                      "url": "https://example.com/mock-news-1",
                      "urlToImage": "https://via.placeholder.com/150",
                      "publishedAt": "2025-04-25T12:00:00Z",
                      "content": "This is a mock news article content."
                    },
                    {
                      "source": { "id": null, "name": "Mock News" },
                      "author": "Mock Author",
                      "title": "Mock News Article 2",
                      "description": "This is another mock news article for testing purposes.",
                      "url": "https://example.com/mock-news-2",
                      "urlToImage": "https://via.placeholder.com/150",
                      "publishedAt": "2025-04-25T12:00:00Z",
                      "content": "This is another mock news article content."
                    }
                  ]
                }
                """;
            return ResponseEntity.ok(mockResponse);
        }
    }
}
