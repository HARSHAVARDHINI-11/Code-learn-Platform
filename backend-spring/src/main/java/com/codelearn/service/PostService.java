package com.codelearn.service;

import com.codelearn.dto.request.CreatePostRequest;
import com.codelearn.dto.request.UpdatePostRequest;
import com.codelearn.exception.BadRequestException;
import com.codelearn.exception.ResourceNotFoundException;
import com.codelearn.exception.UnauthorizedException;
import com.codelearn.model.Post;
import com.codelearn.model.User;
import com.codelearn.repository.PostRepository;
import com.codelearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> getAllPosts(String language, String difficulty, String search, String sortBy) {
        List<Post> posts;

        if (search != null && !search.isEmpty()) {
            posts = postRepository.searchByTitleOrProblem(search);
        } else if (language != null && difficulty != null) {
            posts = postRepository.findByLanguageAndDifficultyOrderByCreatedAtDesc(language, difficulty);
        } else if (language != null) {
            posts = postRepository.findByLanguageOrderByCreatedAtDesc(language);
        } else if (difficulty != null) {
            posts = postRepository.findByDifficultyOrderByCreatedAtDesc(difficulty);
        } else if ("popular".equals(sortBy)) {
            posts = postRepository.findAllByOrderByViewsDescLikesDesc();
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        return posts.stream()
                .map(this::populateAuthorDetails)
                .collect(Collectors.toList());
    }

    public Post getPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        // Increment views
        post.setViews(post.getViews() + 1);
        postRepository.save(post);

        return populateAuthorDetails(post);
    }

    public Post createPost(String userId, CreatePostRequest request) {
        Post post = Post.builder()
                .author(userId)
                .title(request.getTitle().trim())
                .problem(request.getProblem())
                .code(request.getCode())
                .language(request.getLanguage())
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "Medium")
                .views(0)
                .likes(new ArrayList<>())
                .build();

        post = postRepository.save(post);

        // Award points for posting
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setCodingScore(user.getCodingScore() + 10);
            userRepository.save(user);
        }

        log.info("New post created by user {}: {}", userId, post.getTitle());
        return populateAuthorDetails(post);
    }

    public Post updatePost(String userId, String postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        // Check ownership
        if (!post.getAuthor().equals(userId)) {
            throw new UnauthorizedException("User not authorized");
        }

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getProblem() != null) post.setProblem(request.getProblem());
        if (request.getCode() != null) post.setCode(request.getCode());
        if (request.getLanguage() != null) post.setLanguage(request.getLanguage());
        if (request.getTags() != null) post.setTags(request.getTags());
        if (request.getDifficulty() != null) post.setDifficulty(request.getDifficulty());

        post = postRepository.save(post);
        return populateAuthorDetails(post);
    }

    public void deletePost(String userId, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        // Check ownership
        if (!post.getAuthor().equals(userId)) {
            throw new UnauthorizedException("User not authorized");
        }

        postRepository.delete(post);
        log.info("Post deleted: {}", postId);
    }

    public Post likePost(String userId, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        List<String> likes = post.getLikes();
        if (likes == null) {
            likes = new ArrayList<>();
        }

        int likeIndex = likes.indexOf(userId);
        if (likeIndex > -1) {
            likes.remove(likeIndex);
        } else {
            likes.add(userId);
            // Award points to author
            User author = userRepository.findById(post.getAuthor()).orElse(null);
            if (author != null) {
                author.setCodingScore(author.getCodingScore() + 2);
                userRepository.save(author);
            }
        }

        post.setLikes(likes);
        post = postRepository.save(post);
        return populateAuthorDetails(post);
    }

    private Post populateAuthorDetails(Post post) {
        if (post.getAuthor() != null) {
            userRepository.findById(post.getAuthor()).ifPresent(user -> {
                post.setAuthorDetails(Post.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .college(user.getCollege())
                        .codingScore(user.getCodingScore())
                        .bio(user.getBio())
                        .build());
            });
        }
        return post;
    }
}
