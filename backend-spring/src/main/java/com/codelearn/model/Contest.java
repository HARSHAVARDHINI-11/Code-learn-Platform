package com.codelearn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contests")
public class Contest {
    
    @Id
    private String id;
    
    private String title;
    
    private String description;
    
    private String creator;
    
    @Builder.Default
    private List<ParticipatingGroup> participatingGroups = new ArrayList<>();
    
    @Builder.Default
    private List<Problem> problems = new ArrayList<>();
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer duration; // in minutes
    
    @Builder.Default
    private String status = "upcoming"; // upcoming, ongoing, completed
    
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Transient fields
    private UserSummary creatorDetails;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipatingGroup {
        private String group;
        @Builder.Default
        private Integer score = 0;
        private GroupSummary groupDetails;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Problem {
        private String title;
        private String description;
        private String difficulty; // Easy, Medium, Hard
        private Integer points;
        @Builder.Default
        private List<TestCase> testCases = new ArrayList<>();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCase {
        private String input;
        private String output;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Submission {
        private String user;
        private String group;
        private Integer problem;
        private String code;
        private String language;
        private Integer score;
        private LocalDateTime submittedAt;
        private UserSummary userDetails;
        private GroupSummary groupDetails;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private String id;
        private String name;
        private String email;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupSummary {
        private String id;
        private String name;
        private Integer memberCount;
    }
}
