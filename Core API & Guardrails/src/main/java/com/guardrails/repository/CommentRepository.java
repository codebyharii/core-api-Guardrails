package com.guardrails.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guardrails.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
