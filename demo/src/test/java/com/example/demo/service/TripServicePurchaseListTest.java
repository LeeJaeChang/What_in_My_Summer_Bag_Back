package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.PurchaseItemResponse;
import com.example.demo.dto.PurchaseLinkResponse;
import com.example.demo.dto.PurchaseListResponse;
import com.example.demo.entity.ActivityType;
import com.example.demo.entity.Member;
import com.example.demo.entity.PackingCategory;
import com.example.demo.entity.PackingItem;
import com.example.demo.entity.ProductLink;
import com.example.demo.entity.Trip;
import com.example.demo.entity.TripActivity;
import com.example.demo.recommend.client.AiRecommendClient;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PackingItemRepository;
import com.example.demo.repository.ProductLinkRepository;
import com.example.demo.repository.TripRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TripServicePurchaseListTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private PackingItemRepository packingItemRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductLinkRepository productLinkRepository;

    @Mock
    private WeatherService weatherService;

    @Mock
    private AiRecommendClient aiRecommendClient;

    @Test
    void 구매할_목록은_체크되지_않은_준비물만_정렬순서대로_반환한다() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository,
                productLinkRepository, weatherService, aiRecommendClient);

        long memberId = 1L;
        long tripId = 10L;
        Trip trip = ownedTrip(memberId, tripId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        // 리포지토리 파생 쿼리가 이미 체크 안 된 항목만 정렬해서 돌려준다
        when(packingItemRepository.findByTripIdAndCheckedFalseOrderBySortOrderAsc(tripId))
                .thenReturn(List.of(
                        item(trip, "선크림", 2, "sunscreen"),
                        item(trip, "아쿠아 슈즈", 4, "aqua shoes"),
                        item(trip, "기초 제품", 0, "travel skincare set")));

        PurchaseListResponse response = tripService.getPurchaseList(memberId, tripId);

        // 전체 조회가 아니라 '체크 안 됨' 전용 쿼리를 사용해야 한다
        verify(packingItemRepository).findByTripIdAndCheckedFalseOrderBySortOrderAsc(tripId);
        // 구매할 목록 화면 상단 '오늘의 날씨' 카드용으로 날씨 요약/팁/아이콘도 함께 내려간다
        assertThat(response.travelTip()).isEqualTo("자외선이 강해요. 가볍고 시원한 아이템을 챙겨보세요");
        assertThat(response.weather()).isNotNull();
        assertThat(response.weather().weatherIconKey()).isEqualTo("CLEAR");
        assertThat(response.weather().temperatureMax()).isEqualTo(29.0);
        // 화면 상단 카드에 쓰는 여행 정보도 함께 내려간다
        assertThat(response.tripId()).isEqualTo(tripId);
        assertThat(response.destination()).isEqualTo("제주");
        assertThat(response.activities()).containsExactly("SEA", "FOOD_TOUR");
        assertThat(response.items())
                .extracting(PurchaseItemResponse::name)
                .containsExactly("선크림", "아쿠아 슈즈", "기초 제품");
        assertThat(response.items())
                .extracting(PurchaseItemResponse::checked)
                .containsOnly(false);
        // 구매 링크 조회용 search_keyword가 항목별로 함께 내려간다
        assertThat(response.items())
                .extracting(PurchaseItemResponse::searchKeyword)
                .containsExactly("sunscreen", "aqua shoes", "travel skincare set");
        // iconKey는 searchKeyword와 역할이 다른 별도 필드다
        assertThat(response.items())
                .extracting(PurchaseItemResponse::iconKey)
                .containsOnly("icon");
    }

    @Test
    void 다른_회원의_여행이면_구매할_목록을_조회할_수_없다() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository,
                productLinkRepository, weatherService, aiRecommendClient);

        long ownerId = 1L;
        long otherMemberId = 2L;
        long tripId = 10L;
        Trip trip = ownedTrip(ownerId, tripId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> tripService.getPurchaseList(otherMemberId, tripId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void 구매_링크는_항목의_search_keyword로_브랜드_2개를_반환한다() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository,
                productLinkRepository, weatherService, aiRecommendClient);

        long memberId = 1L;
        long tripId = 10L;
        long itemId = 101L;
        Trip trip = ownedTrip(memberId, tripId);
        PackingItem item = item(trip, "선크림", 1, "sunscreen");
        ReflectionTestUtils.setField(item, "id", itemId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(packingItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(productLinkRepository.findById("sunscreen")).thenReturn(Optional.of(new ProductLink(
                "sunscreen",
                "미샤 선크림", "https://shop.example.com/missha", "https://cdn.example.com/missha.png",
                "에스쁘아 선크림", "https://shop.example.com/espoir", "https://cdn.example.com/espoir.png")));

        PurchaseLinkResponse response = tripService.getPurchaseLinks(memberId, tripId, itemId);

        assertThat(response.itemId()).isEqualTo(itemId);
        assertThat(response.itemName()).isEqualTo("선크림");
        assertThat(response.searchKeyword()).isEqualTo("sunscreen");
        assertThat(response.title()).isEqualTo("선크림");
        assertThat(response.brand1Name()).isEqualTo("미샤 선크림");
        assertThat(response.link1Url()).isEqualTo("https://shop.example.com/missha");
        // 매핑은 항상 2개라 2번 필드는 null이 되지 않는다
        assertThat(response.brand2Name()).isEqualTo("에스쁘아 선크림");
        assertThat(response.link2Image()).isEqualTo("https://cdn.example.com/espoir.png");
    }

    @Test
    void 매핑된_상품_링크가_없으면_PURCHASE_LINK_NOT_FOUND() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository,
                productLinkRepository, weatherService, aiRecommendClient);

        long memberId = 1L;
        long tripId = 10L;
        long itemId = 101L;
        Trip trip = ownedTrip(memberId, tripId);
        PackingItem item = item(trip, "선크림", 1, "sunscreen");
        ReflectionTestUtils.setField(item, "id", itemId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(packingItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        // 키워드는 유효하지만 아직 링크가 매핑되지 않은 경우
        when(productLinkRepository.findById("sunscreen")).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> tripService.getPurchaseLinks(memberId, tripId, itemId))
                .isInstanceOf(PurchaseLinkNotFoundException.class);
    }

    @Test
    void 다른_여행의_항목이면_구매_링크를_조회할_수_없다() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository,
                productLinkRepository, weatherService, aiRecommendClient);

        long memberId = 1L;
        long tripId = 10L;
        long itemId = 101L;
        Trip trip = ownedTrip(memberId, tripId);
        Trip otherTrip = ownedTrip(memberId, 11L);
        PackingItem item = item(otherTrip, "선크림", 1, "sunscreen");
        ReflectionTestUtils.setField(item, "id", itemId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(packingItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> tripService.getPurchaseLinks(memberId, tripId, itemId))
                .isInstanceOf(PackingItemNotFoundException.class);
    }

    private Trip ownedTrip(long memberId, long tripId) {
        Member member = new Member(1000L);
        ReflectionTestUtils.setField(member, "id", memberId);
        Trip trip = new Trip(member, "제주", LocalDate.now(), LocalDate.now().plusDays(2));
        ReflectionTestUtils.setField(trip, "id", tripId);
        trip.applyWeather(24.0, 29.0, 10, 31.0,
                "자외선이 강해요. 가볍고 시원한 아이템을 챙겨보세요", "CLEAR");
        trip.getActivities().add(new TripActivity(trip, ActivityType.SEA));
        trip.getActivities().add(new TripActivity(trip, ActivityType.FOOD_TOUR));
        return trip;
    }

    private PackingItem item(Trip trip, String name, int sortOrder, String searchKeyword) {
        PackingItem item = new PackingItem(trip, name, PackingCategory.ETC, "reason", "icon", sortOrder);
        item.setSearchKeyword(searchKeyword);
        return item;
    }
}
