package com.codelearn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private List<UserResponse> leaderboard;
    private CurrentUserRank currentUser;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentUserRank {
        private String id;
        private String name;
        private String email;
        private String college;
        private String department;
        private Integer year;
        private Integer codingScore;
        private Object rank; // Can be Integer or "Not Ranked"
    }
}
