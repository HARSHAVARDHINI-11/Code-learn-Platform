package com.codelearn.repository;

import com.codelearn.model.Contest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends MongoRepository<Contest, String> {
    
    List<Contest> findAllByOrderByStartTimeDesc();
    
    List<Contest> findByStatus(String status);
    
    List<Contest> findByCreator(String creatorId);
}
