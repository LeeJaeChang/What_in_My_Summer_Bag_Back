package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.TripDetailResponse;
import com.example.demo.dto.TripListResponse;
import com.example.demo.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/trips")
public class TripController {

    private final TripService tripService;
    private final AuthService authService;

    public TripController(TripService tripService, AuthService authService) {
        this.tripService = tripService;
        this.authService = authService;
    }

    // 3.1 새로 추천 받기 (날씨 조회 + AI 준비물 생성은 서비스 TODO)
    @PostMapping
    public ResponseEntity<TripDetailResponse> createTrip(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateTripRequest request) {
        Long memberId = authService.resolveMemberId(authorization);
        TripDetailResponse result = tripService.createTrip(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 3.2 기존 추천 내용 보기 (목록)
    @GetMapping
    public ResponseEntity<TripListResponse> listTrips(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(tripService.listTrips(memberId, page, size));
    }

    // 3.3 Today's bag 상세 조회
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDetailResponse> getTrip(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long tripId) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(tripService.getTrip(memberId, tripId));
    }
}
