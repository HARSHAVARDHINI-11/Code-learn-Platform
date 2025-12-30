package com.codelearn.controller;

import com.codelearn.dto.request.AddCommentRequest;
import com.codelearn.dto.request.AddReplyRequest;
import com.codelearn.model.Discussion;
import com.codelearn.service.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
@Tag(name = "Discussions", description = "Post discussions and comments management")
public class DiscussionController {

    private final DiscussionService discussionService;

    @GetMapping("/{postId}")
    @Operation(summary = "Get discussion for a post", description = "Retrieves all comments and replies for a post")
    public ResponseEntity<Discussion> getDiscussionByPostId(@PathVariable String postId) {
        return ResponseEntity.ok(discussionService.getDiscussionByPostId(postId));
    }

    @PostMapping("/{postId}/comment")
    @Operation(summary = "Add a comment", description = "Adds a new comment to a post's discussion")
    public ResponseEntity<Discussion> addComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(discussionService.addComment(userDetails.getUsername(), postId, request));
    }

    @PostMapping("/{postId}/comment/{commentId}/reply")
    @Operation(summary = "Reply to a comment", description = "Adds a reply to an existing comment")
    public ResponseEntity<Discussion> addReply(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @PathVariable String commentId,
            @Valid @RequestBody AddReplyRequest request) {
        return ResponseEntity.ok(discussionService.addReply(
                userDetails.getUsername(), postId, commentId, request));
    }

    @PutMapping("/{postId}/comment/{commentId}/like")
    @Operation(summary = "Like/Unlike a comment", description = "Toggles like status on a comment")
    public ResponseEntity<Discussion> likeComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String postId,
            @PathVariable String commentId) {
        return ResponseEntity.ok(discussionService.likeComment(
                userDetails.getUsername(), postId, commentId));
    }
}
