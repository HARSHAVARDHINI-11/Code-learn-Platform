package com.codelearn.repository;

import com.codelearn.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    List<Post> findByLanguageOrderByCreatedAtDesc(String language);
    
    List<Post> findByDifficultyOrderByCreatedAtDesc(String difficulty);
    
    List<Post> findByLanguageAndDifficultyOrderByCreatedAtDesc(String language, String difficulty);
    
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'problem': { $regex: ?0, $options: 'i' } } ] }")
    List<Post> searchByTitleOrProblem(String searchTerm);
    
    List<Post> findAllByOrderByCreatedAtDesc();
    
    List<Post> findAllByOrderByViewsDescLikesDesc();
    
    List<Post> findByAuthor(String authorId);
}
