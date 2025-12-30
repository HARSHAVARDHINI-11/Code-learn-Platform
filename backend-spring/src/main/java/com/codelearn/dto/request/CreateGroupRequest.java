package com.codelearn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupRequest {
    
    @NotBlank(message = "Group name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private List<String> allowedEmails;
    
    private Boolean isPrivate = false;
}
