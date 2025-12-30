package com.codelearn.controller;

import com.codelearn.dto.response.LeaderboardResponse;
import com.codelearn.model.Group;
import com.codelearn.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Rankings and leaderboard management")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/college")
    @Operation(summary = "Get college leaderboard", description = "Retrieves top coders from the user's college")
    public ResponseEntity<LeaderboardResponse> getCollegeLeaderboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getCollegeLeaderboard(userDetails.getUsername()));
    }

    @GetMapping("/global")
    @Operation(summary = "Get global leaderboard", description = "Retrieves top coders globally")
    public ResponseEntity<LeaderboardResponse> getGlobalLeaderboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(userDetails.getUsername()));
    }

    @GetMapping("/groups")
    @Operation(summary = "Get group leaderboard", description = "Retrieves top performing groups")
    public ResponseEntity<List<Group>> getGroupLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getGroupLeaderboard());
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get department leaderboard", description = "Retrieves top coders from a specific department")
    public ResponseEntity<LeaderboardResponse> getDepartmentLeaderboard(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String department) {
        return ResponseEntity.ok(leaderboardService.getDepartmentLeaderboard(
                userDetails.getUsername(), department));
    }
}
