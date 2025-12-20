package com.codelearn.group.service;

import com.codelearn.group.model.Group;
import com.codelearn.group.repository.GroupRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final RabbitTemplate rabbitTemplate;

    public GroupService(GroupRepository groupRepository, RabbitTemplate rabbitTemplate) {
        this.groupRepository = groupRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Cacheable(value = "groups", key = "#id")
    @CircuitBreaker(name = "groupService")
    public Optional<Group> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    @CircuitBreaker(name = "groupService")
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @CircuitBreaker(name = "groupService")
    public List<Group> getGroupsByOwnerId(Long ownerId) {
        return groupRepository.findByOwnerId(ownerId);
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    @CircuitBreaker(name = "groupService")
    public Group createGroup(Group group) {
        Group savedGroup = groupRepository.save(group);
        rabbitTemplate.convertAndSend("group.exchange", "group.created", savedGroup);
        return savedGroup;
    }

    @Transactional
    @CacheEvict(value = "groups", key = "#id")
    @CircuitBreaker(name = "groupService")
    public Optional<Group> updateGroup(Long id, Group groupDetails) {
        return groupRepository.findById(id)
                .map(group -> {
                    group.setName(groupDetails.getName());
                    group.setDescription(groupDetails.getDescription());
                    group.setCategory(groupDetails.getCategory());
                    Group updatedGroup = groupRepository.save(group);
                    rabbitTemplate.convertAndSend("group.exchange", "group.updated", updatedGroup);
                    return updatedGroup;
                });
    }

    @Transactional
    @CacheEvict(value = "groups", key = "#id")
    @CircuitBreaker(name = "groupService")
    public boolean deleteGroup(Long id) {
        return groupRepository.findById(id)
                .map(group -> {
                    groupRepository.delete(group);
                    rabbitTemplate.convertAndSend("group.exchange", "group.deleted", id);
                    return true;
                }).orElse(false);
    }
}
