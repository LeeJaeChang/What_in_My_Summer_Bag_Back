package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.PackingItemListResponse;
import com.example.demo.dto.PackingItemResponse;
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
                        item(trip, "선글라스", 2),
                        item(trip, "아쿠아 슈즈", 4),
                        item(trip, "기초 제품", 0)));

        PackingItemListResponse response = tripService.getPurchaseList(memberId, tripId);

        // 전체 조회가 아니라 '체크 안 됨' 전용 쿼리를 사용해야 한다
        verify(packingItemRepository).findByTripIdAndCheckedFalseOrderBySortOrderAsc(tripId);
        assertThat(response.packingItems())
                .extracting(PackingItemResponse::name)
                .containsExactly("선글라스", "아쿠아 슈즈", "기초 제품");
        assertThat(response.packingItems())
                .extracting(PackingItemResponse::checked)
                .containsOnly(false);
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
        return trip;
    }

    private PackingItem item(Trip trip, String name, int sortOrder) {
        return new PackingItem(trip, name, PackingCategory.ETC, "reason", "icon", sortOrder);
    }
}
