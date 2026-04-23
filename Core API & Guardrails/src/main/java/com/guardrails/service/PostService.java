package com.guardrails.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guardrails.domain.AuthorType;
import com.guardrails.domain.Post;
import com.guardrails.dto.LikeRequest;
import com.guardrails.dto.LikeResponse;
import com.guardrails.dto.PostCreateRequest;
import com.guardrails.dto.PostResponse;
import com.guardrails.exception.ResourceNotFoundException;
import com.guardrails.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthorResolver authorResolver;
    private final ViralityService viralityService;

    public PostService(PostRepository postRepository, AuthorResolver authorResolver, ViralityService viralityService) {
        this.postRepository = postRepository;
        this.authorResolver = authorResolver;
        this.viralityService = viralityService;
    }

    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        var author = authorResolver.resolve(request.authorType(), request.authorId());

        Post post = new Post();
        post.setContent(request.content());
        if (author.authorType() == AuthorType.USER) {
            post.setUserAuthor(author.user());
        } else {
            post.setBotAuthor(author.bot());
        }

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
    }

    @Transactional
    public LikeResponse likePost(Long postId, LikeRequest request) {
        Post post = getPost(postId);
        long score = viralityService.incrementVirality(post.getId(), 20);
        return new LikeResponse(post.getId(), score);
    }

    @Transactional(readOnly = true)
    public PostResponse toResponse(Post post) {
        String authorType = post.getUserAuthor() != null ? "USER" : "BOT";
        Long authorId = post.getUserAuthor() != null ? post.getUserAuthor().getId() : post.getBotAuthor().getId();
        return new PostResponse(post.getId(), post.getContent(), post.getCreatedAt(), authorType, authorId, viralityService.currentViralityScore(post.getId()));
    }
}
