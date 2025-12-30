package com.codelearn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String college;
    private String department;
    private Integer year;
    private Integer codingScore;
    private String avatar;
    private String bio;
    private List<String> skills;
    private List<String> groups;
    private LocalDateTime createdAt;
}
