package com.guardrails.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guardrails.dto.CommentCreateRequest;
import com.guardrails.dto.CommentResponse;
import com.guardrails.dto.LikeRequest;
import com.guardrails.dto.LikeResponse;
import com.guardrails.dto.PostCreateRequest;
import com.guardrails.dto.PostResponse;
import com.guardrails.service.CommentService;
import com.guardrails.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(postId, request));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> likePost(@PathVariable Long postId, @RequestBody(required = false) LikeRequest request) {
        return ResponseEntity.ok(postService.likePost(postId, request));
    }
}
