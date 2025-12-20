package com.codelearn.post.service;

import com.codelearn.post.model.Post;
import com.codelearn.post.repository.PostRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final RabbitTemplate rabbitTemplate;

    public PostService(PostRepository postRepository, RabbitTemplate rabbitTemplate) {
        this.postRepository = postRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Cacheable(value = "posts", key = "#id")
    @CircuitBreaker(name = "postService")
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @CircuitBreaker(name = "postService")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @CircuitBreaker(name = "postService")
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    @CircuitBreaker(name = "postService")
    public Post createPost(Post post) {
        Post savedPost = postRepository.save(post);
        rabbitTemplate.convertAndSend("post.exchange", "post.created", savedPost);
        return savedPost;
    }

    @Transactional
    @CacheEvict(value = "posts", key = "#id")
    @CircuitBreaker(name = "postService")
    public Optional<Post> updatePost(Long id, Post postDetails) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(postDetails.getTitle());
                    post.setContent(postDetails.getContent());
                    post.setTags(postDetails.getTags());
                    Post updatedPost = postRepository.save(post);
                    rabbitTemplate.convertAndSend("post.exchange", "post.updated", updatedPost);
                    return updatedPost;
                });
    }

    @Transactional
    @CacheEvict(value = "posts", key = "#id")
    @CircuitBreaker(name = "postService")
    public boolean deletePost(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    postRepository.delete(post);
                    rabbitTemplate.convertAndSend("post.exchange", "post.deleted", id);
                    return true;
                }).orElse(false);
    }
}
