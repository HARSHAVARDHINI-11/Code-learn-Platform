package com.codelearn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    private String college;
    
    private String department;
    
    private Integer year;
    
    @Builder.Default
    private Integer codingScore = 0;
    
    @Builder.Default
    private String avatar = "";
    
    @Builder.Default
    private String bio = "";
    
    @Builder.Default
    private List<String> skills = new ArrayList<>();
    
    @Builder.Default
    private List<String> groups = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
}
