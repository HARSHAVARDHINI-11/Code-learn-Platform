package com.codelearn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitSolutionRequest {
    
    @NotNull(message = "Problem index is required")
    private Integer problemIndex;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Language is required")
    private String language;
}
