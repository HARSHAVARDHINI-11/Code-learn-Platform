package com.codelearn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreatePostRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Problem description is required")
    private String problem;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Language is required")
    private String language;
    
    private List<String> tags;
    
    private String difficulty = "Medium";
}
