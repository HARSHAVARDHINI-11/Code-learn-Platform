package com.codelearn.controller;

import com.codelearn.dto.request.CreateGroupRequest;
import com.codelearn.dto.request.JoinGroupRequest;
import com.codelearn.dto.response.MessageResponse;
import com.codelearn.model.Group;
import com.codelearn.service.GroupService;
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
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Study groups and team management")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    @Operation(summary = "Get user's groups", description = "Retrieves all groups the current user is a member of")
    public ResponseEntity<List<Group>> getUserGroups(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(groupService.getUserGroups(userDetails.getUsername()));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all public groups", description = "Retrieves all public groups")
    public ResponseEntity<List<Group>> getAllPublicGroups() {
        return ResponseEntity.ok(groupService.getAllPublicGroups());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group by ID", description = "Retrieves a specific group by its ID")
    public ResponseEntity<Group> getGroupById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        return ResponseEntity.ok(groupService.getGroupById(id, userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Create a group", description = "Creates a new study group")
    public ResponseEntity<Group> createGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(userDetails.getUsername(), request));
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a group", description = "Joins an existing group")
    public ResponseEntity<Group> joinGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id,
            @RequestBody(required = false) JoinGroupRequest request) {
        return ResponseEntity.ok(groupService.joinGroup(
                userDetails.getUsername(), id, request != null ? request : new JoinGroupRequest()));
    }

    @DeleteMapping("/{id}/leave")
    @Operation(summary = "Leave a group", description = "Leaves a group")
    public ResponseEntity<MessageResponse> leaveGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        return ResponseEntity.ok(groupService.leaveGroup(userDetails.getUsername(), id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a group", description = "Deletes a group (creator only)")
    public ResponseEntity<MessageResponse> deleteGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        return ResponseEntity.ok(groupService.deleteGroup(userDetails.getUsername(), id));
    }
}
