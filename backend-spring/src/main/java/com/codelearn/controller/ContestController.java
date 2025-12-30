package com.codelearn.controller;

import com.codelearn.dto.request.CreateContestRequest;
import com.codelearn.dto.request.SubmitSolutionRequest;
import com.codelearn.dto.response.MessageResponse;
import com.codelearn.dto.response.SubmissionResponse;
import com.codelearn.model.Contest;
import com.codelearn.service.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
@Tag(name = "Contests", description = "Coding contests and competitions management")
public class ContestController {

    private final ContestService contestService;

    @GetMapping
    @Operation(summary = "Get all contests", description = "Retrieves all contests")
    public ResponseEntity<List<Contest>> getAllContests() {
        return ResponseEntity.ok(contestService.getAllContests());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contest by ID", description = "Retrieves a specific contest by its ID")
    public ResponseEntity<Contest> getContestById(@PathVariable String id) {
        return ResponseEntity.ok(contestService.getContestById(id));
    }

    @PostMapping
    @Operation(summary = "Create a contest", description = "Creates a new coding contest")
    public ResponseEntity<Contest> createContest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateContestRequest request) {
        return ResponseEntity.ok(contestService.createContest(userDetails.getUsername(), request));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit solution", description = "Submits a solution to a contest problem")
    public ResponseEntity<SubmissionResponse> submitSolution(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody SubmitSolutionRequest request) {
        return ResponseEntity.ok(contestService.submitSolution(userDetails.getUsername(), id, request));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update contest status", description = "Updates the status of a contest based on time")
    public ResponseEntity<Contest> updateContestStatus(@PathVariable String id) {
        return ResponseEntity.ok(contestService.updateContestStatus(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contest", description = "Deletes a contest (creator only)")
    public ResponseEntity<MessageResponse> deleteContest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        return ResponseEntity.ok(contestService.deleteContest(userDetails.getUsername(), id));
    }
}
