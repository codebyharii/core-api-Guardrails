package com.guardrails.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        Long parentCommentId,
        String content,
        int depthLevel,
        LocalDateTime createdAt,
        String authorType,
        Long authorId,
        long viralityScore
) {
}
