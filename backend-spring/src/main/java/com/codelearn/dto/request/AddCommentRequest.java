package com.codelearn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private String code;
    
    private String language;
}
