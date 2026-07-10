package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.dto.ChecklistResponse;
import com.example.demo.dto.CreateChecklistRequest;
import com.example.demo.dto.ToggleChecklistItemRequest;
import com.example.demo.dto.ToggleChecklistItemResponse;
import com.example.demo.service.ChecklistService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChecklistController {

    private final ChecklistService checklistService;
    private final AuthService authService;

    public ChecklistController(ChecklistService checklistService, AuthService authService) {
        this.checklistService = checklistService;
        this.authService = authService;
    }

    // 담당: 본인 (재창) — API 명세서 3번
    @PostMapping("/checklist")
    public ResponseEntity<Map<String, Object>> createChecklist(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateChecklistRequest request) {
        String userId = authService.resolveUserId(authorization);
        ChecklistResponse result = checklistService.createChecklist(userId, request.recommendationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", result));
    }

    // 담당: 본인 (재창) — API 명세서 4번
    @GetMapping("/checklist/{checklistId}")
    public ResponseEntity<Map<String, Object>> getChecklist(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String checklistId) {
        String userId = authService.resolveUserId(authorization);
        ChecklistResponse result = checklistService.getChecklist(userId, checklistId);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    // 담당: 본인 (재창) — API 명세서 5번
    @PatchMapping("/checklist/items/{checklistItemId}")
    public ResponseEntity<Map<String, Object>> toggleItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String checklistItemId,
            @RequestBody ToggleChecklistItemRequest request) {
        String userId = authService.resolveUserId(authorization);
        ToggleChecklistItemResponse result = checklistService.toggleItem(userId, checklistItemId, request.checked());
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
}
