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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byteandblog.entity.Comment;
import com.byteandblog.repository.CommentRepository;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPost(@PathVariable Long postId) {
        logger.info("Fetching comments for post ID: {}", postId);
        List<Comment> comments = commentRepository.findByPostId(postId);
        logger.debug("Retrieved {} comments for post ID: {}", comments.size(), postId);
        return comments;
    }

    @PostMapping
    public Comment createComment(@Valid @RequestBody Comment comment) {
        logger.info("Creating new comment for post ID: {}", comment.getId());
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully with ID: {}", savedComment.getId());
        return savedComment;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        logger.info("Deleting comment with ID: {}", id);
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            logger.info("Comment with ID {} deleted successfully", id);
            return ResponseEntity.ok().build();
        }
        logger.warn("Comment with ID {} not found for deletion", id);
        return ResponseEntity.notFound().build();
    }
}