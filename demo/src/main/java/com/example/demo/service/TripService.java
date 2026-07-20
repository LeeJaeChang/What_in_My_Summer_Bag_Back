package com.example.demo.service;

import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.PackingItemListResponse;
import com.example.demo.dto.PackingItemResponse;
import com.example.demo.dto.PurchaseItemResponse;
import com.example.demo.dto.PurchaseLinkResponse;
import com.example.demo.dto.PurchaseListResponse;
import com.example.demo.dto.TogglePackingItemResponse;
import com.example.demo.dto.TripDetailResponse;
import com.example.demo.dto.TripListResponse;
import com.example.demo.dto.TripSummaryResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.ActivityType;
import com.example.demo.entity.Member;
import com.example.demo.entity.PackingItem;
import com.example.demo.entity.ProductLink;
import com.example.demo.entity.Trip;
import com.example.demo.entity.TripActivity;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PackingItemRepository;
import com.example.demo.repository.ProductLinkRepository;
import com.example.demo.repository.TripRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final PackingItemRepository packingItemRepository;
    private final MemberRepository memberRepository;
    private final ProductLinkRepository productLinkRepository;
    private final WeatherService weatherService;

    public TripService(TripRepository tripRepository,
                       PackingItemRepository packingItemRepository,
                       MemberRepository memberRepository,
                       ProductLinkRepository productLinkRepository,
                       WeatherService weatherService) {
        this.tripRepository = tripRepository;
        this.packingItemRepository = packingItemRepository;
        this.memberRepository = memberRepository;
        this.productLinkRepository = productLinkRepository;
        this.weatherService = weatherService;
    }

    // POST /trips — 새로 추천 받기
    @Transactional
    public TripDetailResponse createTrip(Long memberId, CreateTripRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 member_id: " + memberId));

        Trip trip = new Trip(member, request.destination(), request.startDate(), request.endDate());
        for (ActivityType activityType : request.activityTypes()) {
            trip.getActivities().add(new TripActivity(trip, activityType));
        }

        WeatherResponse weather;
        try {
            weather = weatherService.getWeather(
                    request.destination(), request.startDate(), request.endDate());
        } catch (UnsupportedDateRangeException e) {
            // 예보 가능 범위(5일)를 벗어난 여행 — 작년 같은 기간의 실측 날씨로 근사한다.
            weather = weatherService.getLastYearWeather(
                    request.destination(), request.startDate(), request.endDate());
        }
        trip.applyWeather(
                weather.temperatureMin(),
                weather.temperatureMax(),
                weather.precipitationProbability(),
                weather.temperaturePerceived(),
                null,
                weather.weatherIconKey());

        // TODO(AI 담당): 날씨 + 활동 프롬프트로 여행 Tip과 PackingItem 목록 생성 후 trip.getPackingItems()에 추가.
        //   (지금은 Trip/TripActivity + 날씨만 채우고 여행 Tip·준비물은 비워둔 껍데기)

        tripRepository.save(trip);
        return toDetail(trip);
    }

    // GET /trips — 기존 추천 목록
    @Transactional(readOnly = true)
    public TripListResponse listTrips(Long memberId, int page, int size) {
        Page<Trip> trips = tripRepository.findByMemberId(
                memberId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<TripSummaryResponse> summaries = trips.getContent().stream()
                .map(t -> new TripSummaryResponse(
                        t.getId(), t.getDestination(), t.getStartDate(), t.getEndDate(),
                        t.getWeatherIconKey(), t.getCreatedAt()))
                .toList();
        return new TripListResponse(summaries, trips.getTotalElements());
    }

    // GET /trips/{tripId} — Today's bag 상세
    @Transactional(readOnly = true)
    public TripDetailResponse getTrip(Long memberId, Long tripId) {
        return toDetail(loadOwnedTrip(memberId, tripId));
    }

    // GET /trips/{tripId}/packing-items — 체크리스트 조회
    @Transactional(readOnly = true)
    public PackingItemListResponse getPackingItems(Long memberId, Long tripId) {
        loadOwnedTrip(memberId, tripId);
        List<PackingItemResponse> items = packingItemRepository.findByTripIdOrderBySortOrderAsc(tripId).stream()
                .map(this::toItemResponse)
                .toList();
        return new PackingItemListResponse(items);
    }

    // GET /trips/{tripId}/packing-items/purchase-list — 구매할 목록(체크 안 된 준비물만)
    @Transactional(readOnly = true)
    public PurchaseListResponse getPurchaseList(Long memberId, Long tripId) {
        Trip trip = loadOwnedTrip(memberId, tripId);
        List<PurchaseItemResponse> items =
                packingItemRepository.findByTripIdAndCheckedFalseOrderBySortOrderAsc(tripId).stream()
                        .map(this::toPurchaseItemResponse)
                        .toList();
        return new PurchaseListResponse(
                trip.getId(),
                trip.getDestination(),
                toWeather(trip),
                toActivityNames(trip),
                trip.getTravelTip(),
                items);
    }

    // PATCH /trips/{tripId}/packing-items/{itemId} — 체크/해제
    @Transactional
    public TogglePackingItemResponse toggleItem(Long memberId, Long tripId, Long itemId, boolean checked) {
        PackingItem item = loadOwnedItem(memberId, tripId, itemId);

        item.setChecked(checked);
        packingItemRepository.save(item);
        return new TogglePackingItemResponse(item.getId(), item.isChecked());
    }

    // GET /trips/{tripId}/packing-items/{itemId}/purchase-links — 항목별 브랜드 상품 링크
    @Transactional(readOnly = true)
    public PurchaseLinkResponse getPurchaseLinks(Long memberId, Long tripId, Long itemId) {
        PackingItem item = loadOwnedItem(memberId, tripId, itemId);

        // AI가 searchKeyword를 못 채운 항목은 조회할 링크 자체가 없다.
        String keyword = item.getSearchKeyword();
        if (keyword == null || keyword.isBlank()) {
            throw new PurchaseLinkNotFoundException("search_keyword 가 없는 항목입니다: " + itemId);
        }

        ProductLink link = productLinkRepository.findById(keyword)
                .orElseThrow(() -> new PurchaseLinkNotFoundException(
                        "상품 링크가 매핑되지 않은 search_keyword: " + keyword));

        return new PurchaseLinkResponse(
                item.getId(),
                item.getName(),
                keyword,
                item.getName(),
                link.getBrand1Name(),
                link.getLink1Url(),
                link.getLink1Image(),
                link.getBrand2Name(),
                link.getLink2Url(),
                link.getLink2Image());
    }

    private Trip loadOwnedTrip(Long memberId, Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("존재하지 않는 trip_id: " + tripId));
        if (!trip.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("다른 회원의 여행 계획입니다.");
        }
        return trip;
    }

    // trip 소유권 + item이 그 trip 소속인지까지 검증한다.
    private PackingItem loadOwnedItem(Long memberId, Long tripId, Long itemId) {
        loadOwnedTrip(memberId, tripId);
        PackingItem item = packingItemRepository.findById(itemId)
                .orElseThrow(() -> new PackingItemNotFoundException("존재하지 않는 packing_item_id: " + itemId));
        if (!item.getTrip().getId().equals(tripId)) {
            throw new PackingItemNotFoundException("해당 여행 계획에 속하지 않는 항목입니다: " + itemId);
        }
        return item;
    }

    private TripDetailResponse toDetail(Trip trip) {
        WeatherResponse weather = toWeather(trip);
        List<String> activities = toActivityNames(trip);
        List<PackingItemResponse> items = trip.getPackingItems().stream()
                .map(this::toItemResponse)
                .toList();
        return new TripDetailResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                weather,
                activities,
                trip.getTravelTip(),
                items);
    }

    private PackingItemResponse toItemResponse(PackingItem it) {
        return new PackingItemResponse(
                it.getId(),
                it.getName(),
                it.getCategory() != null ? it.getCategory().name() : null,
                it.getReason(),
                it.getIconKey(),
                it.isChecked(),
                it.getSortOrder());
    }

    private WeatherResponse toWeather(Trip trip) {
        return new WeatherResponse(
                trip.getTemperatureMin(),
                trip.getTemperatureMax(),
                trip.getTemperaturePerceived(),
                trip.getPrecipitationProbability(),
                trip.getWeatherIconKey());
    }

    private PurchaseItemResponse toPurchaseItemResponse(PackingItem it) {
        return new PurchaseItemResponse(
                it.getId(),
                it.getName(),
                it.getCategory() != null ? it.getCategory().name() : null,
                it.getReason(),
                it.getIconKey(),
                it.getSearchKeyword(),
                it.isChecked(),
                it.getSortOrder());
    }

    private List<String> toActivityNames(Trip trip) {
        return trip.getActivities().stream()
                .map(a -> a.getActivityType().name())
                .toList();
    }
}
