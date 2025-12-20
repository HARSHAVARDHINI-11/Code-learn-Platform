package com.codelearn.user.service;

import com.codelearn.user.model.User;
import com.codelearn.user.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public UserService(UserRepository userRepository, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Cacheable(value = "users", key = "#id")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @CircuitBreaker(name = "userService")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    @CircuitBreaker(name = "userService")
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        // Publish user created event to RabbitMQ
        rabbitTemplate.convertAndSend("user.exchange", "user.created", savedUser);
        return savedUser;
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    @CircuitBreaker(name = "userService")
    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setEmail(userDetails.getEmail());
                    user.setFirstName(userDetails.getFirstName());
                    user.setLastName(userDetails.getLastName());
                    user.setBio(userDetails.getBio());
                    User updatedUser = userRepository.save(user);
                    // Publish user updated event to RabbitMQ
                    rabbitTemplate.convertAndSend("user.exchange", "user.updated", updatedUser);
                    return updatedUser;
                });
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    @CircuitBreaker(name = "userService")
    public boolean deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    // Publish user deleted event to RabbitMQ
                    rabbitTemplate.convertAndSend("user.exchange", "user.deleted", id);
                    return true;
                }).orElse(false);
    }

    public Optional<User> getUserFallback(Long id, Exception ex) {
        return Optional.empty();
    }
}
