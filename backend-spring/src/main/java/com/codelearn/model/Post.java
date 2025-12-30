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
@Document(collection = "posts")
public class Post {
    
    @Id
    private String id;
    
    private String author;
    
    private String title;
    
    private String problem;
    
    private String code;
    
    private String language;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @Builder.Default
    private String difficulty = "Medium";
    
    @Builder.Default
    private List<String> likes = new ArrayList<>();
    
    @Builder.Default
    private Integer views = 0;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Transient field for populated author data
    private UserSummary authorDetails;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private String id;
        private String name;
        private String email;
        private String college;
        private Integer codingScore;
        private String bio;
    }
}
