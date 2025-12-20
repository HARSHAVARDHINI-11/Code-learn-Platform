package com.codelearn.contest.controller;

import com.codelearn.contest.model.Contest;
import com.codelearn.contest.service.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @GetMapping
    public ResponseEntity<List<Contest>> getAllContests() {
        return ResponseEntity.ok(contestService.getAllContests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contest> getContestById(@PathVariable Long id) {
        return contestService.getContestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Contest>> getContestsByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(contestService.getContestsByOrganizerId(organizerId));
    }

    @PostMapping
    public ResponseEntity<Contest> createContest(@RequestBody Contest contest) {
        Contest createdContest = contestService.createContest(contest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contest> updateContest(@PathVariable Long id, @RequestBody Contest contest) {
        return contestService.updateContest(id, contest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContest(@PathVariable Long id) {
        return contestService.deleteContest(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
