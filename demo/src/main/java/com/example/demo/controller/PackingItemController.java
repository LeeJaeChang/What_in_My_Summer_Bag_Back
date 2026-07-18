package com.example.demo.controller;

import com.example.demo.auth.AuthService;
import com.example.demo.dto.PackingItemListResponse;
import com.example.demo.dto.PurchaseLinkResponse;
import com.example.demo.dto.PurchaseListResponse;
import com.example.demo.dto.TogglePackingItemRequest;
import com.example.demo.dto.TogglePackingItemResponse;
import com.example.demo.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/trips/{tripId}/packing-items")
public class PackingItemController {

    private final TripService tripService;
    private final AuthService authService;

    public PackingItemController(TripService tripService, AuthService authService) {
        this.tripService = tripService;
        this.authService = authService;
    }

    // 4.1 체크리스트 조회
    @GetMapping
    public ResponseEntity<PackingItemListResponse> getPackingItems(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long tripId) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(tripService.getPackingItems(memberId, tripId));
    }

    // 4.3 구매할 목록 조회 (구매하러 가기 → 준비되지 않은 물품만)
    @GetMapping("/purchase-list")
    public ResponseEntity<PurchaseListResponse> getPurchaseList(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long tripId) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(tripService.getPurchaseList(memberId, tripId));
    }

    // 7. 항목별 구매 링크 조회 (구매하기 버튼 → 브랜드 2개 바텀시트)
    @GetMapping("/{itemId}/purchase-links")
    public ResponseEntity<PurchaseLinkResponse> getPurchaseLinks(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long tripId,
            @PathVariable Long itemId) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(tripService.getPurchaseLinks(memberId, tripId, itemId));
    }

    // 4.2 체크리스트 체크/해제
    @PatchMapping("/{itemId}")
    public ResponseEntity<TogglePackingItemResponse> toggleItem(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long tripId,
            @PathVariable Long itemId,
            @RequestBody TogglePackingItemRequest request) {
        Long memberId = authService.resolveMemberId(authorization);
        return ResponseEntity.ok(tripService.toggleItem(memberId, tripId, itemId, request.checked()));
    }
}
