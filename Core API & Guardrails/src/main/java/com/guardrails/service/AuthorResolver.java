package com.guardrails.service;

import org.springframework.stereotype.Service;

import com.guardrails.domain.AuthorType;
import com.guardrails.domain.Bot;
import com.guardrails.domain.User;
import com.guardrails.exception.ResourceNotFoundException;
import com.guardrails.repository.BotRepository;
import com.guardrails.repository.UserRepository;

@Service
public class AuthorResolver {

    private final UserRepository userRepository;
    private final BotRepository botRepository;

    public AuthorResolver(UserRepository userRepository, BotRepository botRepository) {
        this.userRepository = userRepository;
        this.botRepository = botRepository;
    }

    public AuthorLookup resolve(AuthorType authorType, Long authorId) {
        if (authorType == AuthorType.USER) {
            User user = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorId));
            return new AuthorLookup(authorType, authorId, user, null);
        }
        Bot bot = botRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Bot not found: " + authorId));
        return new AuthorLookup(authorType, authorId, null, bot);
    }
}
