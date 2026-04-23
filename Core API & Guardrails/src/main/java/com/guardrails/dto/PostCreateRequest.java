package com.guardrails.dto;

import com.guardrails.domain.AuthorType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCreateRequest(
        @NotNull AuthorType authorType,
        @NotNull Long authorId,
        @NotBlank String content
) {
}
