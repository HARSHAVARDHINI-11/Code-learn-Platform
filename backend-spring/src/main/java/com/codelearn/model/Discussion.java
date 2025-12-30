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
@Document(collection = "discussions")
public class Discussion {
    
    @Id
    private String id;
    
    private String post;
    
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private String id;
        private String user;
        private String content;
        @Builder.Default
        private String code = "";
        @Builder.Default
        private String language = "";
        @Builder.Default
        private List<String> likes = new ArrayList<>();
        @Builder.Default
        private List<Reply> replies = new ArrayList<>();
        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
        
        // Transient
        private UserSummary userDetails;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reply {
        private String id;
        private String user;
        private String content;
        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
        
        // Transient
        private UserSummary userDetails;
    }
    
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
    }
}
