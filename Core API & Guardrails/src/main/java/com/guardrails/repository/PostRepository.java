package com.guardrails.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guardrails.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
