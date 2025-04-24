package com.byteandblog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteandblog.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // Added method to find by email
}
