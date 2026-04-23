package com.guardrails.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guardrails.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
