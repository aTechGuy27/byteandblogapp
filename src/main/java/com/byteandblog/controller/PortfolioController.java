package com.byteandblog.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byteandblog.entity.PortfolioItem;
import com.byteandblog.repository.PortfolioItemRepository;
import com.byteandblog.service.FileUploadService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @Autowired
    private PortfolioItemRepository portfolioItemRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public List<PortfolioItem> getAllItems() {
        logger.info("Fetching all portfolio items");
        List<PortfolioItem> items = portfolioItemRepository.findAll();
        logger.debug("Retrieved {} portfolio items", items.size());
        return items;
    }

    @PostMapping
    public ResponseEntity<PortfolioItem> createItem(
            @Valid @RequestPart("item") PortfolioItem item,
            @RequestPart("image") MultipartFile image) throws Exception {
        logger.info("Creating new portfolio item with title: {}", item.getTitle());
        try {
            item.setImageUrl(fileUploadService.uploadFile(image));
            item.setCreatedAt(LocalDateTime.now());
            PortfolioItem savedItem = portfolioItemRepository.save(item);
            logger.info("Portfolio item created successfully with ID: {}", savedItem.getId());
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            logger.error("Failed to create portfolio item: {}", e.getMessage(), e);
            throw e; // Re-throw the exception to maintain existing behavior
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioItem> getItemById(@PathVariable Long id) {
        logger.info("Fetching portfolio item with ID: {}", id);
        return portfolioItemRepository.findById(id)
                .map(item -> {
                    logger.debug("Portfolio item found: {}", item.getTitle());
                    return ResponseEntity.ok(item);
                })
                .orElseGet(() -> {
                    logger.warn("Portfolio item with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        logger.info("Deleting portfolio item with ID: {}", id);
        if (portfolioItemRepository.existsById(id)) {
            portfolioItemRepository.deleteById(id);
            logger.info("Portfolio item with ID {} deleted successfully", id);
            return ResponseEntity.ok().build();
        }
        logger.warn("Portfolio item with ID {} not found for deletion", id);
        return ResponseEntity.notFound().build();
    }
}