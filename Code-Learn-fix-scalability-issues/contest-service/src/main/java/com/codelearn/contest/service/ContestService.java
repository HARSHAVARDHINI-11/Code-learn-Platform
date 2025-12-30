package com.codelearn.contest.service;

import com.codelearn.contest.model.Contest;
import com.codelearn.contest.repository.ContestRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContestService {

    private final ContestRepository contestRepository;
    private final RabbitTemplate rabbitTemplate;

    public ContestService(ContestRepository contestRepository, RabbitTemplate rabbitTemplate) {
        this.contestRepository = contestRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Cacheable(value = "contests", key = "#id")
    @CircuitBreaker(name = "contestService")
    public Optional<Contest> getContestById(Long id) {
        return contestRepository.findById(id);
    }

    @CircuitBreaker(name = "contestService")
    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    @CircuitBreaker(name = "contestService")
    public List<Contest> getContestsByOrganizerId(Long organizerId) {
        return contestRepository.findByOrganizerId(organizerId);
    }

    @Transactional
    @CacheEvict(value = "contests", allEntries = true)
    @CircuitBreaker(name = "contestService")
    public Contest createContest(Contest contest) {
        Contest savedContest = contestRepository.save(contest);
        rabbitTemplate.convertAndSend("contest.exchange", "contest.created", savedContest);
        return savedContest;
    }

    @Transactional
    @CacheEvict(value = "contests", key = "#id")
    @CircuitBreaker(name = "contestService")
    public Optional<Contest> updateContest(Long id, Contest contestDetails) {
        return contestRepository.findById(id)
                .map(contest -> {
                    contest.setTitle(contestDetails.getTitle());
                    contest.setDescription(contestDetails.getDescription());
                    contest.setStartTime(contestDetails.getStartTime());
                    contest.setEndTime(contestDetails.getEndTime());
                    contest.setDifficulty(contestDetails.getDifficulty());
                    Contest updatedContest = contestRepository.save(contest);
                    rabbitTemplate.convertAndSend("contest.exchange", "contest.updated", updatedContest);
                    return updatedContest;
                });
    }

    @Transactional
    @CacheEvict(value = "contests", key = "#id")
    @CircuitBreaker(name = "contestService")
    public boolean deleteContest(Long id) {
        return contestRepository.findById(id)
                .map(contest -> {
                    contestRepository.delete(contest);
                    rabbitTemplate.convertAndSend("contest.exchange", "contest.deleted", id);
                    return true;
                }).orElse(false);
    }
}
