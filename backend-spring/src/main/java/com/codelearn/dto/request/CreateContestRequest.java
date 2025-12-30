package com.codelearn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateContestRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private List<String> participatingGroups;
    
    private List<ProblemRequest> problems;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "Duration is required")
    private Integer duration; // in minutes
    
    @Data
    public static class ProblemRequest {
        private String title;
        private String description;
        private String difficulty;
        private Integer points;
        private List<TestCaseRequest> testCases;
    }
    
    @Data
    public static class TestCaseRequest {
        private String input;
        private String output;
    }
}
