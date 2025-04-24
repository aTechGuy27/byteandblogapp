package com.byteandblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteandblog.entity.PortfolioItem;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
}
