package com.byteandblog.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.byteandblog.entity.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
	
	@Cacheable("contactMessage")
    Page<ContactMessage> findAll(Pageable pageable);
}
