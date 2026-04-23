package com.guardrails.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guardrails.domain.AuthorType;
import com.guardrails.domain.Bot;
import com.guardrails.domain.Comment;
import com.guardrails.domain.Post;
import com.guardrails.dto.CommentCreateRequest;
import com.guardrails.dto.CommentResponse;
import com.guardrails.exception.GuardrailViolationException;
import com.guardrails.exception.ResourceNotFoundException;
import com.guardrails.repository.CommentRepository;
import com.guardrails.repository.PostRepository;

@Service
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthorResolver authorResolver;
    private final RedisGuardrailService redisGuardrailService;
    private final ViralityService viralityService;
    private final NotificationService notificationService;

    public CommentService(PostRepository postRepository,
                          CommentRepository commentRepository,
                          AuthorResolver authorResolver,
                          RedisGuardrailService redisGuardrailService,
                          ViralityService viralityService,
                          NotificationService notificationService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.authorResolver = authorResolver;
        this.redisGuardrailService = redisGuardrailService;
        this.viralityService = viralityService;
        this.notificationService = notificationService;
    }

    @Transactional
    public CommentResponse addComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        var author = authorResolver.resolve(request.authorType(), request.authorId());
        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found: " + request.parentCommentId()));
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new GuardrailViolationException("Parent comment does not belong to this post");
            }
        }

        int depthLevel = parentComment != null ? parentComment.getDepthLevel() + 1 : request.depthLevel() != null ? request.depthLevel() : 1;
        if (depthLevel > 20) {
            throw new GuardrailViolationException("Comment thread depth cannot exceed 20 levels");
        }

        if (author.authorType() == AuthorType.BOT) {
            Long targetHumanId = resolveTargetHumanId(request, post, parentComment);
            redisGuardrailService.reserveBotInteraction(postId, author.authorId(), targetHumanId);
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setParentComment(parentComment);
        comment.setContent(request.content());
        comment.setDepthLevel(depthLevel);

        if (author.authorType() == AuthorType.USER) {
            comment.setUserAuthor(author.user());
        } else {
            comment.setBotAuthor(author.bot());
        }

        Comment saved = commentRepository.save(comment);

        long score = viralityService.incrementVirality(postId, author.authorType() == AuthorType.BOT ? 1 : 50);

        if (author.authorType() == AuthorType.BOT) {
            Long targetHumanId = resolveTargetHumanId(request, post, parentComment);
            notificationService.recordBotInteraction(targetHumanId, buildBotNotification(author.bot(), saved));
        }

        return toResponse(saved, score);
    }

    private Long resolveTargetHumanId(CommentCreateRequest request, Post post, Comment parentComment) {
        if (request.targetHumanId() != null) {
            return request.targetHumanId();
        }
        if (parentComment != null && parentComment.getUserAuthor() != null) {
            return parentComment.getUserAuthor().getId();
        }
        if (post.getUserAuthor() != null) {
            return post.getUserAuthor().getId();
        }
        return null;
    }

    private String buildBotNotification(Bot bot, Comment comment) {
        String botName = bot == null ? "Bot" : bot.getName();
        return "Bot " + botName + " replied to your post";
    }

    private CommentResponse toResponse(Comment comment, long score) {
        String authorType = comment.getUserAuthor() != null ? "USER" : "BOT";
        Long authorId = comment.getUserAuthor() != null ? comment.getUserAuthor().getId() : comment.getBotAuthor().getId();
        Long parentId = comment.getParentComment() == null ? null : comment.getParentComment().getId();
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                parentId,
                comment.getContent(),
                comment.getDepthLevel(),
                comment.getCreatedAt(),
                authorType,
                authorId,
                score
        );
    }
}
