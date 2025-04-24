package com.byteandblog.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.byteandblog.entity.BlogPost;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    @Cacheable("blogPosts")
    Page<BlogPost> findAll(Pageable pageable);
}
