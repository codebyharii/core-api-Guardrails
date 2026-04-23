package com.guardrails.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String authorType,
        Long authorId,
        long viralityScore
) {
}
