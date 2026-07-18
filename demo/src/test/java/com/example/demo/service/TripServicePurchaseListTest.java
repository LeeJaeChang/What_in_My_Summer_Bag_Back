package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.PurchaseItemResponse;
import com.example.demo.dto.PurchaseListResponse;
import com.example.demo.entity.Member;
import com.example.demo.entity.PackingCategory;
import com.example.demo.entity.PackingItem;
import com.example.demo.entity.Trip;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PackingItemRepository;
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
    private WeatherService weatherService;

    @Test
    void 구매할_목록은_체크되지_않은_준비물만_정렬순서대로_반환한다() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository, weatherService);

        long memberId = 1L;
        long tripId = 10L;
        Trip trip = ownedTrip(memberId, tripId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        // 리포지토리 파생 쿼리가 이미 체크 안 된 항목만 정렬해서 돌려준다
        when(packingItemRepository.findByTripIdAndCheckedFalseOrderBySortOrderAsc(tripId))
                .thenReturn(List.of(
                        item(trip, "선글라스", 2, "선글라스"),
                        item(trip, "아쿠아 슈즈", 4, "아쿠아슈즈"),
                        item(trip, "기초 제품", 0, "기초화장품")));

        PurchaseListResponse response = tripService.getPurchaseList(memberId, tripId);

        // 전체 조회가 아니라 '체크 안 됨' 전용 쿼리를 사용해야 한다
        verify(packingItemRepository).findByTripIdAndCheckedFalseOrderBySortOrderAsc(tripId);
        // 구매할 목록 화면 상단 '오늘의 날씨' 카드용으로 날씨 요약/팁/아이콘도 함께 내려간다
        assertThat(response.travelTip()).isEqualTo("자외선이 강해요. 가볍고 시원한 아이템을 챙겨보세요");
        assertThat(response.weather()).isNotNull();
        assertThat(response.weather().weatherIconKey()).isEqualTo("CLEAR");
        assertThat(response.weather().temperatureMax()).isEqualTo(29.0);
        assertThat(response.packingItems())
                .extracting(PurchaseItemResponse::name)
                .containsExactly("선글라스", "아쿠아 슈즈", "기초 제품");
        assertThat(response.packingItems())
                .extracting(PurchaseItemResponse::checked)
                .containsOnly(false);
        // 구매 링크 조회용 search_keyword가 항목별로 함께 내려간다
        assertThat(response.packingItems())
                .extracting(PurchaseItemResponse::searchKeyword)
                .containsExactly("선글라스", "아쿠아슈즈", "기초화장품");
    }

    @Test
    void 다른_회원의_여행이면_구매할_목록을_조회할_수_없다() {
        TripService tripService = new TripService(
                tripRepository, packingItemRepository, memberRepository, weatherService);

        long ownerId = 1L;
        long otherMemberId = 2L;
        long tripId = 10L;
        Trip trip = ownedTrip(ownerId, tripId);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> tripService.getPurchaseList(otherMemberId, tripId))
                .isInstanceOf(ForbiddenException.class);
    }

    private Trip ownedTrip(long memberId, long tripId) {
        Member member = new Member(1000L, "tester");
        ReflectionTestUtils.setField(member, "id", memberId);
        Trip trip = new Trip(member, "제주", LocalDate.now(), LocalDate.now().plusDays(2));
        ReflectionTestUtils.setField(trip, "id", tripId);
        trip.applyWeather(24.0, 29.0, 10, 31.0,
                "자외선이 강해요. 가볍고 시원한 아이템을 챙겨보세요", "CLEAR");
        return trip;
    }

    private PackingItem item(Trip trip, String name, int sortOrder, String searchKeyword) {
        PackingItem item = new PackingItem(trip, name, PackingCategory.ETC, "reason", "icon", sortOrder);
        item.setSearchKeyword(searchKeyword);
        return item;
    }
}
