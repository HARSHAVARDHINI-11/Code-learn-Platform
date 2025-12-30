package com.codelearn.controller;

import com.codelearn.dto.request.CreatePostRequest;
import com.codelearn.dto.request.UpdatePostRequest;
import com.codelearn.dto.response.MessageResponse;
import com.codelearn.model.Post;
import com.codelearn.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Code posts and solutions management")
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieves all posts with optional filtering and sorting")
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(postService.getAllPosts(language, difficulty, search, sortBy));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieves a specific post by its ID")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    @Operation(summary = "Create a post", description = "Creates a new code post")
    public ResponseEntity<Post> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(postService.createPost(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a post", description = "Updates an existing post")
    public ResponseEntity<Post> updatePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestBody UpdatePostRequest request) {
        return ResponseEntity.ok(postService.updatePost(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post", description = "Deletes a post")
    public ResponseEntity<MessageResponse> deletePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        postService.deletePost(userDetails.getUsername(), id);
        return ResponseEntity.ok(new MessageResponse("Post deleted"));
    }

    @PutMapping("/{id}/like")
    @Operation(summary = "Like/Unlike a post", description = "Toggles like status on a post")
    public ResponseEntity<Post> likePost(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        return ResponseEntity.ok(postService.likePost(userDetails.getUsername(), id));
    }
}
