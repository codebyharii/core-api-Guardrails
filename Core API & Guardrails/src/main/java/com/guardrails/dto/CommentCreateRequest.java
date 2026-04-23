package com.guardrails.dto;

import com.guardrails.domain.AuthorType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotNull AuthorType authorType,
        @NotNull Long authorId,
        @NotBlank String content,
        Integer depthLevel,
        Long parentCommentId,
        Long targetHumanId
) {
}
