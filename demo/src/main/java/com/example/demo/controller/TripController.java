package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.TogglePackingItemRequest;
import com.example.demo.dto.TogglePackingItemResponse;
import com.example.demo.dto.TripResponse;
import com.example.demo.service.TripService;
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
public class TripController {

    private final TripService tripService;
    private final AuthService authService;

    public TripController(TripService tripService, AuthService authService) {
        this.tripService = tripService;
        this.authService = authService;
    }

    // 여행 계획 생성 (준비물은 추후 AI가 생성 — 생성 시점엔 비어 있음)
    @PostMapping("/trips")
    public ResponseEntity<Map<String, Object>> createTrip(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateTripRequest request) {
        Long memberId = authService.resolveMemberId(authorization);
        TripResponse result = tripService.createTrip(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true, "data", result));
    }

    // 여행 계획 + 준비물 조회
    @GetMapping("/trips/{tripId}")
    public ResponseEntity<Map<String, Object>> getTrip(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long tripId) {
        Long memberId = authService.resolveMemberId(authorization);
        TripResponse result = tripService.getTrip(memberId, tripId);
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    // 준비물 체크 토글
    @PatchMapping("/packing-items/{packingItemId}")
    public ResponseEntity<Map<String, Object>> toggleItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long packingItemId,
            @RequestBody TogglePackingItemRequest request) {
        Long memberId = authService.resolveMemberId(authorization);
        TogglePackingItemResponse result = tripService.toggleItem(memberId, packingItemId, request.checked());
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }
}
