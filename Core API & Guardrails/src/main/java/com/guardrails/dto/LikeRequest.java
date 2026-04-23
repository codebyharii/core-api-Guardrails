package com.guardrails.dto;

import com.guardrails.domain.AuthorType;

public record LikeRequest(
        AuthorType actorType,
        Long actorId
) {
}
