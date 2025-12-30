package com.codelearn.service;

import com.codelearn.dto.request.CreateGroupRequest;
import com.codelearn.dto.request.JoinGroupRequest;
import com.codelearn.dto.response.MessageResponse;
import com.codelearn.exception.BadRequestException;
import com.codelearn.exception.ResourceNotFoundException;
import com.codelearn.exception.UnauthorizedException;
import com.codelearn.model.Group;
import com.codelearn.model.User;
import com.codelearn.repository.GroupRepository;
import com.codelearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<Group> getUserGroups(String userId) {
        List<Group> groups = groupRepository.findByMemberUserId(userId);
        return groups.stream()
                .map(this::populateGroupDetails)
                .collect(Collectors.toList());
    }

    public List<Group> getAllPublicGroups() {
        List<Group> groups = groupRepository.findByIsPrivateFalseOrderByCreatedAtDesc();
        return groups.stream()
                .map(this::populateGroupDetails)
                .collect(Collectors.toList());
    }

    public Group getGroupById(String groupId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        return populateGroupDetails(group);
    }

    public Group createGroup(String userId, CreateGroupRequest request) {
        // Generate unique invite code
        String inviteCode = UUID.randomUUID().toString().substring(0, 12);
        while (groupRepository.existsByInviteCode(inviteCode)) {
            inviteCode = UUID.randomUUID().toString().substring(0, 12);
        }

        List<Group.GroupMember> members = new ArrayList<>();
        members.add(Group.GroupMember.builder()
                .user(userId)
                .role("admin")
                .joinedAt(LocalDateTime.now())
                .build());

        Group group = Group.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .creator(userId)
                .inviteCode(inviteCode)
                .allowedEmails(request.getAllowedEmails() != null ? request.getAllowedEmails() : new ArrayList<>())
                .isPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false)
                .members(members)
                .groupScore(0)
                .build();

        group = groupRepository.save(group);

        // Add group to user's groups
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            if (user.getGroups() == null) {
                user.setGroups(new ArrayList<>());
            }
            user.getGroups().add(group.getId());
            userRepository.save(user);
        }

        log.info("New group created by user {}: {}", userId, group.getName());
        return populateGroupDetails(group);
    }

    public Group joinGroup(String userId, String groupId, JoinGroupRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        // Check if already a member
        boolean isMember = group.getMembers().stream()
                .anyMatch(m -> m.getUser().equals(userId));
        if (isMember) {
            throw new BadRequestException("Already a member of this group");
        }

        // Check invite code if private
        if (group.getIsPrivate() && (request.getInviteCode() == null || 
                !group.getInviteCode().equals(request.getInviteCode()))) {
            throw new BadRequestException("Invalid invite code");
        }

        // Check allowed emails if specified
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (group.getAllowedEmails() != null && !group.getAllowedEmails().isEmpty() &&
                !group.getAllowedEmails().contains(user.getEmail().toLowerCase())) {
            throw new BadRequestException("Your email is not allowed to join this group");
        }

        // Add member
        group.getMembers().add(Group.GroupMember.builder()
                .user(userId)
                .role("member")
                .joinedAt(LocalDateTime.now())
                .build());

        group = groupRepository.save(group);

        // Add group to user's groups
        if (user.getGroups() == null) {
            user.setGroups(new ArrayList<>());
        }
        user.getGroups().add(group.getId());
        userRepository.save(user);

        log.info("User {} joined group {}", userId, group.getName());
        return populateGroupDetails(group);
    }

    public MessageResponse leaveGroup(String userId, String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        // Check if user is creator
        if (group.getCreator().equals(userId)) {
            throw new BadRequestException("Creator cannot leave the group. Delete it instead.");
        }

        // Remove member
        group.setMembers(group.getMembers().stream()
                .filter(m -> !m.getUser().equals(userId))
                .collect(Collectors.toList()));
        groupRepository.save(group);

        // Remove group from user's groups
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getGroups() != null) {
            user.getGroups().remove(groupId);
            userRepository.save(user);
        }

        log.info("User {} left group {}", userId, group.getName());
        return new MessageResponse("Left the group successfully");
    }

    public MessageResponse deleteGroup(String userId, String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        // Check if user is creator
        if (!group.getCreator().equals(userId)) {
            throw new UnauthorizedException("Not authorized");
        }

        // Remove group from all members' groups
        for (Group.GroupMember member : group.getMembers()) {
            User user = userRepository.findById(member.getUser()).orElse(null);
            if (user != null && user.getGroups() != null) {
                user.getGroups().remove(groupId);
                userRepository.save(user);
            }
        }

        groupRepository.delete(group);
        log.info("Group deleted: {}", groupId);
        return new MessageResponse("Group deleted");
    }

    private Group populateGroupDetails(Group group) {
        // Populate creator details
        if (group.getCreator() != null) {
            userRepository.findById(group.getCreator()).ifPresent(user -> {
                group.setCreatorDetails(Group.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .college(user.getCollege())
                        .build());
            });
        }

        // Populate member details
        if (group.getMembers() != null) {
            List<Group.GroupMemberDetails> memberDetails = group.getMembers().stream()
                    .map(member -> {
                        Group.GroupMemberDetails details = Group.GroupMemberDetails.builder()
                                .user(member.getUser())
                                .role(member.getRole())
                                .joinedAt(member.getJoinedAt())
                                .build();
                        
                        userRepository.findById(member.getUser()).ifPresent(user -> {
                            details.setUserDetails(Group.UserSummary.builder()
                                    .id(user.getId())
                                    .name(user.getName())
                                    .email(user.getEmail())
                                    .college(user.getCollege())
                                    .department(user.getDepartment())
                                    .codingScore(user.getCodingScore())
                                    .build());
                        });
                        
                        return details;
                    })
                    .collect(Collectors.toList());
            group.setMemberDetails(memberDetails);
        }

        return group;
    }
}
