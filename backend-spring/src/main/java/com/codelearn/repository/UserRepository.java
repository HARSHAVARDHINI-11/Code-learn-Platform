package com.codelearn.repository;

import com.codelearn.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByCollegeOrderByCodingScoreDesc(String college);
    
    List<User> findByCollegeAndDepartmentOrderByCodingScoreDesc(String college, String department);
    
    List<User> findTop100ByOrderByCodingScoreDesc();
    
    List<User> findTop100ByCollegeOrderByCodingScoreDesc(String college);
    
    List<User> findTop100ByCollegeAndDepartmentOrderByCodingScoreDesc(String college, String department);
}
