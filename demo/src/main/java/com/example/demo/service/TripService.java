package com.example.demo.service;

import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.PackingItemResponse;
import com.example.demo.dto.TogglePackingItemResponse;
import com.example.demo.dto.TripResponse;
import com.example.demo.entity.ActivityType;
import com.example.demo.entity.Member;
import com.example.demo.entity.PackingItem;
import com.example.demo.entity.Trip;
import com.example.demo.entity.TripActivity;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PackingItemRepository;
import com.example.demo.repository.TripRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final PackingItemRepository packingItemRepository;
    private final MemberRepository memberRepository;

    public TripService(TripRepository tripRepository,
                       PackingItemRepository packingItemRepository,
                       MemberRepository memberRepository) {
        this.tripRepository = tripRepository;
        this.packingItemRepository = packingItemRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public TripResponse createTrip(Long memberId, CreateTripRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 member_id: " + memberId));

        Trip trip = new Trip(member, request.destination(), request.startDate(), request.endDate());
        for (ActivityType activityType : request.activities()) {
            trip.getActivities().add(new TripActivity(trip, activityType));
        }

        // TODO(날씨): 여행지·날짜로 날씨를 조회해 trip.applyWeather(...)로 채운다. (별도 파트 연동)
        // TODO(준비물): PackingItem은 추후 AI가 생성한다. 지금은 비워둔 채로 Trip만 만든다.
        //   AI 연동이 붙으면 생성 결과를 trip.getPackingItems()에 추가하도록 여기서 채운다.

        tripRepository.save(trip);
        return toResponse(trip);
    }

    @Transactional(readOnly = true)
    public TripResponse getTrip(Long memberId, Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("존재하지 않는 trip_id: " + tripId));
        if (!trip.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("다른 사용자의 여행 계획입니다.");
        }
        return toResponse(trip);
    }

    @Transactional
    public TogglePackingItemResponse toggleItem(Long memberId, Long packingItemId, boolean checked) {
        PackingItem item = packingItemRepository.findById(packingItemId)
                .orElseThrow(() -> new PackingItemNotFoundException("존재하지 않는 packing_item_id: " + packingItemId));
        if (!item.getTrip().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("다른 사용자의 항목입니다.");
        }

        item.setChecked(checked);
        packingItemRepository.save(item);

        return new TogglePackingItemResponse(
                item.getId(),
                item.isChecked(),
                readinessPercent(item.getTrip().getPackingItems())
        );
    }

    // TODO: "전체 항목 기준" vs "필수 항목 기준" — 명세 확정되면 여기만 바꾸면 됨.
    // 지금은 전체 항목 기준(단순 checked/total)으로 구현.
    private int readinessPercent(List<PackingItem> items) {
        if (items.isEmpty()) {
            return 0;
        }
        long checkedCount = items.stream().filter(PackingItem::isChecked).count();
        return (int) Math.round(100.0 * checkedCount / items.size());
    }

    private TripResponse toResponse(Trip trip) {
        List<String> activities = trip.getActivities().stream()
                .map(a -> a.getActivityType().name())
                .toList();
        List<PackingItemResponse> itemResponses = trip.getPackingItems().stream()
                .map(it -> new PackingItemResponse(
                        it.getId(),
                        it.getName(),
                        it.getCategory() != null ? it.getCategory().name() : null,
                        it.isChecked()))
                .toList();
        return new TripResponse(
                trip.getMember().getId(),
                trip.getId(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                activities,
                itemResponses,
                readinessPercent(trip.getPackingItems())
        );
    }
}
