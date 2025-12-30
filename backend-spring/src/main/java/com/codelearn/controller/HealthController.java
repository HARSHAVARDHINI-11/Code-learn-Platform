package com.codelearn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "API health check")
public class HealthController {

    @GetMapping("/")
    @Operation(summary = "API Info", description = "Returns API information")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CodeLearn Platform API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("documentation", "/swagger-ui.html");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns API health status")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "CodeLearn Platform is healthy");
        return ResponseEntity.ok(response);
    }
}
