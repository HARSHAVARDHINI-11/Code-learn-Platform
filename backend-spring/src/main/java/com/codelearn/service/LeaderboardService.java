package com.codelearn.service;

import com.codelearn.dto.response.LeaderboardResponse;
import com.codelearn.dto.response.UserResponse;
import com.codelearn.exception.ResourceNotFoundException;
import com.codelearn.model.Group;
import com.codelearn.model.User;
import com.codelearn.repository.GroupRepository;
import com.codelearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public LeaderboardResponse getCollegeLeaderboard(String userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<User> users = userRepository.findTop100ByCollegeOrderByCodingScoreDesc(currentUser.getCollege());
        List<UserResponse> leaderboard = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        int userRank = IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId().equals(userId))
                .findFirst()
                .orElse(-1) + 1;

        return LeaderboardResponse.builder()
                .leaderboard(leaderboard)
                .currentUser(LeaderboardResponse.CurrentUserRank.builder()
                        .id(currentUser.getId())
                        .name(currentUser.getName())
                        .email(currentUser.getEmail())
                        .college(currentUser.getCollege())
                        .department(currentUser.getDepartment())
                        .year(currentUser.getYear())
                        .codingScore(currentUser.getCodingScore())
                        .rank(userRank > 0 ? userRank : "Not Ranked")
                        .build())
                .build();
    }

    public LeaderboardResponse getGlobalLeaderboard(String userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<User> users = userRepository.findTop100ByOrderByCodingScoreDesc();
        List<UserResponse> leaderboard = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        int userRank = IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId().equals(userId))
                .findFirst()
                .orElse(-1) + 1;

        return LeaderboardResponse.builder()
                .leaderboard(leaderboard)
                .currentUser(LeaderboardResponse.CurrentUserRank.builder()
                        .id(currentUser.getId())
                        .name(currentUser.getName())
                        .email(currentUser.getEmail())
                        .college(currentUser.getCollege())
                        .department(currentUser.getDepartment())
                        .year(currentUser.getYear())
                        .codingScore(currentUser.getCodingScore())
                        .rank(userRank > 0 ? userRank : "Not Ranked")
                        .build())
                .build();
    }

    public List<Group> getGroupLeaderboard() {
        List<Group> groups = groupRepository.findTop50ByOrderByGroupScoreDesc();
        return groups.stream()
                .map(group -> {
                    // Populate member details
                    if (group.getMembers() != null) {
                        List<Group.GroupMemberDetails> memberDetails = group.getMembers().stream()
                                .map(member -> {
                                    Group.GroupMemberDetails details = Group.GroupMemberDetails.builder()
                                            .user(member.getUser())
                                            .role(member.getRole())
                                            .build();
                                    userRepository.findById(member.getUser()).ifPresent(user -> {
                                        details.setUserDetails(Group.UserSummary.builder()
                                                .id(user.getId())
                                                .name(user.getName())
                                                .codingScore(user.getCodingScore())
                                                .build());
                                    });
                                    return details;
                                })
                                .collect(Collectors.toList());
                        group.setMemberDetails(memberDetails);
                    }
                    return group;
                })
                .collect(Collectors.toList());
    }

    public LeaderboardResponse getDepartmentLeaderboard(String userId, String department) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<User> users = userRepository.findTop100ByCollegeAndDepartmentOrderByCodingScoreDesc(
                currentUser.getCollege(), department);
        List<UserResponse> leaderboard = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        int userRank = IntStream.range(0, users.size())
                .filter(i -> users.get(i).getId().equals(userId))
                .findFirst()
                .orElse(-1) + 1;

        return LeaderboardResponse.builder()
                .leaderboard(leaderboard)
                .currentUser(LeaderboardResponse.CurrentUserRank.builder()
                        .id(currentUser.getId())
                        .name(currentUser.getName())
                        .email(currentUser.getEmail())
                        .college(currentUser.getCollege())
                        .department(currentUser.getDepartment())
                        .year(currentUser.getYear())
                        .codingScore(currentUser.getCodingScore())
                        .rank(userRank > 0 ? userRank : "Not Ranked")
                        .build())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .college(user.getCollege())
                .department(user.getDepartment())
                .year(user.getYear())
                .codingScore(user.getCodingScore())
                .build();
    }
}
