package com.guardrails.service;

import com.guardrails.domain.AuthorType;
import com.guardrails.domain.Bot;
import com.guardrails.domain.User;

public record AuthorLookup(AuthorType authorType, Long authorId, User user, Bot bot) {
}
