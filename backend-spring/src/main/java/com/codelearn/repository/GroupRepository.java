package com.codelearn.repository;

import com.codelearn.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    
    @Query("{ 'members.user': ?0 }")
    List<Group> findByMemberUserId(String userId);
    
    List<Group> findByIsPrivateFalseOrderByCreatedAtDesc();
    
    Optional<Group> findByInviteCode(String inviteCode);
    
    List<Group> findTop50ByOrderByGroupScoreDesc();
    
    boolean existsByInviteCode(String inviteCode);
}
