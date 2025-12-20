package com.codelearn.group.repository;

import com.codelearn.group.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByOwnerId(Long ownerId);
    List<Group> findByCategory(String category);
}
