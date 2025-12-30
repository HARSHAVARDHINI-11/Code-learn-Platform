package com.codelearn.service;

import com.codelearn.dto.request.CreateContestRequest;
import com.codelearn.dto.request.SubmitSolutionRequest;
import com.codelearn.dto.response.MessageResponse;
import com.codelearn.dto.response.SubmissionResponse;
import com.codelearn.exception.BadRequestException;
import com.codelearn.exception.ResourceNotFoundException;
import com.codelearn.exception.UnauthorizedException;
import com.codelearn.model.Contest;
import com.codelearn.model.Group;
import com.codelearn.model.User;
import com.codelearn.repository.ContestRepository;
import com.codelearn.repository.GroupRepository;
import com.codelearn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestService {

    private final ContestRepository contestRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<Contest> getAllContests() {
        List<Contest> contests = contestRepository.findAllByOrderByStartTimeDesc();
        return contests.stream()
                .map(this::populateContestDetails)
                .collect(Collectors.toList());
    }

    public Contest getContestById(String contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", contestId));
        return populateContestDetails(contest);
    }

    public Contest createContest(String userId, CreateContestRequest request) {
        LocalDateTime endTime = request.getStartTime().plusMinutes(request.getDuration());

        List<Contest.ParticipatingGroup> participatingGroups = new ArrayList<>();
        if (request.getParticipatingGroups() != null) {
            participatingGroups = request.getParticipatingGroups().stream()
                    .map(groupId -> Contest.ParticipatingGroup.builder()
                            .group(groupId)
                            .score(0)
                            .build())
                    .collect(Collectors.toList());
        }

        List<Contest.Problem> problems = new ArrayList<>();
        if (request.getProblems() != null) {
            problems = request.getProblems().stream()
                    .map(p -> {
                        List<Contest.TestCase> testCases = new ArrayList<>();
                        if (p.getTestCases() != null) {
                            testCases = p.getTestCases().stream()
                                    .map(tc -> Contest.TestCase.builder()
                                            .input(tc.getInput())
                                            .output(tc.getOutput())
                                            .build())
                                    .collect(Collectors.toList());
                        }
                        return Contest.Problem.builder()
                                .title(p.getTitle())
                                .description(p.getDescription())
                                .difficulty(p.getDifficulty())
                                .points(p.getPoints())
                                .testCases(testCases)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        String status = LocalDateTime.now().isAfter(request.getStartTime()) ? "ongoing" : "upcoming";

        Contest contest = Contest.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .creator(userId)
                .participatingGroups(participatingGroups)
                .problems(problems)
                .startTime(request.getStartTime())
                .endTime(endTime)
                .duration(request.getDuration())
                .status(status)
                .submissions(new ArrayList<>())
                .build();

        contest = contestRepository.save(contest);
        log.info("New contest created by user {}: {}", userId, contest.getTitle());
        return populateContestDetails(contest);
    }

    public SubmissionResponse submitSolution(String userId, String contestId, SubmitSolutionRequest request) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", contestId));

        // Check if contest is ongoing
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(contest.getStartTime()) || now.isAfter(contest.getEndTime())) {
            throw new BadRequestException("Contest is not currently active");
        }

        // Find user's group
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Contest.ParticipatingGroup userGroup = null;
        for (Contest.ParticipatingGroup pg : contest.getParticipatingGroups()) {
            if (user.getGroups() != null && user.getGroups().contains(pg.getGroup())) {
                userGroup = pg;
                break;
            }
        }

        if (userGroup == null) {
            throw new BadRequestException("You are not part of any participating group");
        }

        // Calculate score
        Contest.Problem problem = contest.getProblems().get(request.getProblemIndex());
        int score = problem.getPoints() != null ? problem.getPoints() : 100;

        // Add submission
        contest.getSubmissions().add(Contest.Submission.builder()
                .user(userId)
                .group(userGroup.getGroup())
                .problem(request.getProblemIndex())
                .code(request.getCode())
                .language(request.getLanguage())
                .score(score)
                .submittedAt(LocalDateTime.now())
                .build());

        // Update group score
        userGroup.setScore(userGroup.getScore() + score);

        // Update user coding score
        user.setCodingScore(user.getCodingScore() + score);
        userRepository.save(user);

        // Update group total score
        Group group = groupRepository.findById(userGroup.getGroup()).orElse(null);
        if (group != null) {
            group.setGroupScore(group.getGroupScore() + score);
            groupRepository.save(group);
        }

        contestRepository.save(contest);
        log.info("Solution submitted by user {} for contest {}", userId, contestId);

        return SubmissionResponse.builder()
                .message("Submission successful")
                .score(score)
                .build();
    }

    public Contest updateContestStatus(String contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", contestId));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(contest.getStartTime())) {
            contest.setStatus("upcoming");
        } else if (now.isAfter(contest.getStartTime()) && now.isBefore(contest.getEndTime())) {
            contest.setStatus("ongoing");
        } else {
            contest.setStatus("completed");
        }

        contest = contestRepository.save(contest);
        return populateContestDetails(contest);
    }

    public MessageResponse deleteContest(String userId, String contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", contestId));

        // Check if user is creator
        if (!contest.getCreator().equals(userId)) {
            throw new UnauthorizedException("Not authorized");
        }

        contestRepository.delete(contest);
        log.info("Contest deleted: {}", contestId);
        return new MessageResponse("Contest deleted");
    }

    private Contest populateContestDetails(Contest contest) {
        // Populate creator details
        if (contest.getCreator() != null) {
            userRepository.findById(contest.getCreator()).ifPresent(user -> {
                contest.setCreatorDetails(Contest.UserSummary.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build());
            });
        }

        // Populate participating group details
        if (contest.getParticipatingGroups() != null) {
            contest.getParticipatingGroups().forEach(pg -> {
                groupRepository.findById(pg.getGroup()).ifPresent(group -> {
                    pg.setGroupDetails(Contest.GroupSummary.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .memberCount(group.getMembers() != null ? group.getMembers().size() : 0)
                            .build());
                });
            });
        }

        // Populate submission details
        if (contest.getSubmissions() != null) {
            contest.getSubmissions().forEach(sub -> {
                userRepository.findById(sub.getUser()).ifPresent(user -> {
                    sub.setUserDetails(Contest.UserSummary.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build());
                });
                groupRepository.findById(sub.getGroup()).ifPresent(group -> {
                    sub.setGroupDetails(Contest.GroupSummary.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .build());
                });
            });
        }

        return contest;
    }
}
