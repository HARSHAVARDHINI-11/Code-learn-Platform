package com.codelearn.repository;

import com.codelearn.model.Discussion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscussionRepository extends MongoRepository<Discussion, String> {
    
    Optional<Discussion> findByPost(String postId);
}
