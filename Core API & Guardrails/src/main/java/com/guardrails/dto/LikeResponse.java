package com.guardrails.dto;

public record LikeResponse(
        Long postId,
        long viralityScore
) {
}
