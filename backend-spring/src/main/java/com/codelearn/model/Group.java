package com.codelearn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "groups")
public class Group {
    
    @Id
    private String id;
    
    private String name;
    
    private String description;
    
    private String creator;
    
    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();
    
    @Indexed(unique = true)
    private String inviteCode;
    
    @Builder.Default
    private List<String> allowedEmails = new ArrayList<>();
    
    @Builder.Default
    private Integer groupScore = 0;
    
    @Builder.Default
    private Boolean isPrivate = false;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Transient fields for populated data
    private UserSummary creatorDetails;
    private List<GroupMemberDetails> memberDetails;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMember {
        private String user;
        @Builder.Default
        private String role = "member";
        @Builder.Default
        private LocalDateTime joinedAt = LocalDateTime.now();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMemberDetails {
        private String user;
        private String role;
        private LocalDateTime joinedAt;
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
        private String department;
        private Integer codingScore;
    }
}
