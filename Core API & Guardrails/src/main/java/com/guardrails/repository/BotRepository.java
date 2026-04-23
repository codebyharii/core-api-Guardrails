package com.guardrails.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guardrails.domain.Bot;

public interface BotRepository extends JpaRepository<Bot, Long> {
}
