package com.codelearn.contest.repository;

import com.codelearn.contest.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    List<Contest> findByOrganizerId(Long organizerId);
    List<Contest> findByDifficulty(String difficulty);
}
