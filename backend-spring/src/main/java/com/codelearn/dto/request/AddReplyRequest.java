package com.codelearn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddReplyRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
}
