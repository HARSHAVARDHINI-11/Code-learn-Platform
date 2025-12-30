package com.codelearn.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdatePostRequest {
    private String title;
    private String problem;
    private String code;
    private String language;
    private List<String> tags;
    private String difficulty;
}
