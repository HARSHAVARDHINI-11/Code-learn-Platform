package com.codelearn.service;

import com.codelearn.dto.request.AddCommentRequest;
import com.codelearn.dto.request.AddReplyRequest;
import com.codelearn.exception.ResourceNotFoundException;
import com.codelearn.model.Discussion;
import com.codelearn.model.User;
import com.codelearn.repository.DiscussionRepository;
import com.codelearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;

    public Discussion getDiscussionByPostId(String postId) {
        Discussion discussion = discussionRepository.findByPost(postId)
                .orElseGet(() -> {
                    Discussion newDiscussion = Discussion.builder()
                            .post(postId)
                            .comments(new ArrayList<>())
                            .build();
                    return discussionRepository.save(newDiscussion);
                });

        return populateDiscussionDetails(discussion);
    }

    public Discussion addComment(String userId, String postId, AddCommentRequest request) {
        Discussion discussion = discussionRepository.findByPost(postId)
                .orElseGet(() -> Discussion.builder()
                        .post(postId)
                        .comments(new ArrayList<>())
                        .build());

        Discussion.Comment comment = Discussion.Comment.builder()
                .id(UUID.randomUUID().toString())
                .user(userId)
                .content(request.getContent())
                .code(request.getCode() != null ? request.getCode() : "")
                .language(request.getLanguage() != null ? request.getLanguage() : "")
                .likes(new ArrayList<>())
                .replies(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        discussion.getComments().add(comment);
        discussion = discussionRepository.save(discussion);

        // Award points for participation
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setCodingScore(user.getCodingScore() + 5);
            userRepository.save(user);
        }

        log.info("Comment added by user {} to post {}", userId, postId);
        return populateDiscussionDetails(discussion);
    }

    public Discussion addReply(String userId, String postId, String commentId, AddReplyRequest request) {
        Discussion discussion = discussionRepository.findByPost(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "postId", postId));

        Discussion.Comment comment = discussion.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        Discussion.Reply reply = Discussion.Reply.builder()
                .id(UUID.randomUUID().toString())
                .user(userId)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        if (comment.getReplies() == null) {
            comment.setReplies(new ArrayList<>());
        }
        comment.getReplies().add(reply);

        discussion = discussionRepository.save(discussion);

        // Award points for participation
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setCodingScore(user.getCodingScore() + 3);
            userRepository.save(user);
        }

        log.info("Reply added by user {} to comment {} on post {}", userId, commentId, postId);
        return populateDiscussionDetails(discussion);
    }

    public Discussion likeComment(String userId, String postId, String commentId) {
        Discussion discussion = discussionRepository.findByPost(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "postId", postId));

        Discussion.Comment comment = discussion.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (comment.getLikes() == null) {
            comment.setLikes(new ArrayList<>());
        }

        int likeIndex = comment.getLikes().indexOf(userId);
        if (likeIndex > -1) {
            comment.getLikes().remove(likeIndex);
        } else {
            comment.getLikes().add(userId);
            // Award points to comment author
            User author = userRepository.findById(comment.getUser()).orElse(null);
            if (author != null) {
                author.setCodingScore(author.getCodingScore() + 1);
                userRepository.save(author);
            }
        }

        discussion = discussionRepository.save(discussion);
        return populateDiscussionDetails(discussion);
    }

    private Discussion populateDiscussionDetails(Discussion discussion) {
        if (discussion.getComments() != null) {
            discussion.getComments().forEach(comment -> {
                // Populate comment user details
                userRepository.findById(comment.getUser()).ifPresent(user -> {
                    comment.setUserDetails(Discussion.UserSummary.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .college(user.getCollege())
                            .codingScore(user.getCodingScore())
                            .build());
                });

                // Populate reply user details
                if (comment.getReplies() != null) {
                    comment.getReplies().forEach(reply -> {
                        userRepository.findById(reply.getUser()).ifPresent(user -> {
                            reply.setUserDetails(Discussion.UserSummary.builder()
                                    .id(user.getId())
                                    .name(user.getName())
                                    .email(user.getEmail())
                                    .build());
                        });
                    });
                }
            });
        }
        return discussion;
    }
}
