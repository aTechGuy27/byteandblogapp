package com.byteandblog.controller;


import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteandblog.entity.BlogPost;
import com.byteandblog.repository.BlogPostRepository;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/blog")
public class BlogPostController {

    private static final Logger logger = LoggerFactory.getLogger(BlogPostController.class);

    @Autowired
    private BlogPostRepository blogPostRepository;

    @GetMapping
    public Page<BlogPost> getAllPosts(Pageable pageable) {
        logger.info("Fetching all blog posts, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BlogPost> posts = blogPostRepository.findAll(pageable);
        logger.debug("Retrieved {} blog posts", posts.getTotalElements());
        return posts;
    }

    @PostMapping
    public BlogPost createPost(@Valid @RequestBody BlogPost post) {
        logger.info("Creating new blog post with title: {}", post.getTitle());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        BlogPost savedPost = blogPostRepository.save(post);
        logger.info("Blog post created successfully with ID: {}", savedPost.getId());
        return savedPost;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable Long id) {
        logger.info("Fetching blog post with ID: {}", id);
        return blogPostRepository.findById(id)
                .map(post -> {
                    logger.debug("Blog post found: {}", post.getTitle());
                    return ResponseEntity.ok(post);
                })
                .orElseGet(() -> {
                    logger.warn("Blog post with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long id, @Valid @RequestBody BlogPost updatedPost) {
        logger.info("Updating blog post with ID: {}", id);
        return blogPostRepository.findById(id)
                .map(post -> {
                    logger.debug("Blog post found, updating title to: {}", updatedPost.getTitle());
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setUpdatedAt(LocalDateTime.now());
                    BlogPost savedPost = blogPostRepository.save(post);
                    logger.info("Blog post updated successfully with ID: {}", savedPost.getId());
                    return ResponseEntity.ok(savedPost);
                })
                .orElseGet(() -> {
                    logger.warn("Blog post with ID {} not found for update", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        logger.info("Deleting blog post with ID: {}", id);
        if (blogPostRepository.existsById(id)) {
            blogPostRepository.deleteById(id);
            logger.info("Blog post with ID {} deleted successfully", id);
            return ResponseEntity.ok().build();
        }
        logger.warn("Blog post with ID {} not found for deletion", id);
        return ResponseEntity.notFound().build();
    }
}