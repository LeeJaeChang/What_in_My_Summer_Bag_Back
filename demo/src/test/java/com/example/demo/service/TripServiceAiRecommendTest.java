package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.TripDetailResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.ActivityType;
import com.example.demo.entity.Member;
import com.example.demo.entity.Trip;
import com.example.demo.recommend.client.AiRecommendClient;
import com.example.demo.recommend.dto.AiPackingItem;
import com.example.demo.recommend.dto.AiRecommendResult;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.PackingItemRepository;
import com.example.demo.repository.ProductLinkRepository;
import com.example.demo.repository.TripRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** AI 추천 결과가 Trip/PackingItem으로 옮겨 담기는지 검증한다. */
@ExtendWith(MockitoExtension.class)
class TripServiceAiRecommendTest {

    private static final LocalDate START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate END_DATE = START_DATE.plusDays(2);
    private static final WeatherResponse WEATHER =
            new WeatherResponse(24.5, 33.0, 35.0, 10, "u2600");

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

    @Captor
    private ArgumentCaptor<Trip> tripCaptor;

    private TripService tripService;
    private CreateTripRequest request;

    @BeforeEach
    void setUp() {
        tripService = new TripService(tripRepository, packingItemRepository, memberRepository,
                productLinkRepository, weatherService, aiRecommendClient);
        request = new CreateTripRequest("부산", START_DATE, END_DATE, List.of(ActivityType.SEA));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member(123L)));
        when(weatherService.getWeather("부산", START_DATE, END_DATE)).thenReturn(WEATHER);
    }

    @Test
    void AI가_추천한_여행팁과_준비물을_여행에_담는다() {
        when(aiRecommendClient.recommend(any(), any())).thenReturn(new AiRecommendResult(
                "물놀이 후 체온 관리에 신경 쓰세요.",
                List.of(
                        new AiPackingItem("선크림", "SUN_PROTECTION", "u1F9F4", "sunscreen", "자외선이 강합니다.", 1),
                        new AiPackingItem("수영복", "CLOTHING", "u1FA71", "swimsuit", "해수욕을 합니다.", 2))));

        TripDetailResponse response = tripService.createTrip(1L, request);

        assertThat(response.travelTip()).isEqualTo("물놀이 후 체온 관리에 신경 쓰세요.");
        assertThat(response.packingItems())
                .extracting("name", "category", "iconKey", "sortOrder")
                .containsExactly(
                        tuple("선크림", "SUN_PROTECTION", "u1F9F4", 1),
                        tuple("수영복", "CLOTHING", "u1FA71", 2));

        verify(tripRepository).save(tripCaptor.capture());
        assertThat(tripCaptor.getValue().getPackingItems())
                .extracting("searchKeyword")
                .containsExactly("sunscreen", "swimsuit");
    }

    @Test
    void enum에_없는_category와_유효하지_않은_iconKey_searchKeyword를_흡수한다() {
        when(aiRecommendClient.recommend(any(), any())).thenReturn(new AiRecommendResult(
                "팁",
                List.of(new AiPackingItem(
                        "정체불명 준비물", "MEDICINE", "존재하지않는키", "made up keyword", "이유", 7))));

        TripDetailResponse response = tripService.createTrip(1L, request);

        assertThat(response.packingItems()).hasSize(1);
        // MEDICINE 은 PackingCategory 에 없는 값 — 500 대신 ETC 로 흡수한다.
        assertThat(response.packingItems().get(0).category()).isEqualTo("ETC");
        assertThat(response.packingItems().get(0).iconKey()).isEqualTo("u1F4E6");
        // AI가 준 sortOrder(7) 대신 응답 순서대로 1이 부여된다.
        assertThat(response.packingItems().get(0).sortOrder()).isEqualTo(1);

        verify(tripRepository).save(tripCaptor.capture());
        assertThat(tripCaptor.getValue().getPackingItems().get(0).getSearchKeyword()).isNull();
    }

    @Test
    void iconKey가_유효하지_않으면_준비물_이름으로_아이콘을_재추론한다() {
        when(aiRecommendClient.recommend(any(), any())).thenReturn(new AiRecommendResult(
                "팁",
                List.of(new AiPackingItem("여권", "DOCUMENTS", "u1F6C2", null, "해외 여행 필수", 1))));

        TripDetailResponse response = tripService.createTrip(1L, request);

        // u1F6C2 는 카탈로그에 없는 키 — 기본 아이콘이 아니라 label("신분증, 여권") 매칭으로 찾는다.
        assertThat(response.packingItems().get(0).iconKey()).isEqualTo("u1FAAA");
    }

    @Test
    void 준비물이_없어도_여행_생성은_성공한다() {
        when(aiRecommendClient.recommend(any(), any()))
                .thenReturn(new AiRecommendResult("팁", null));

        TripDetailResponse response = tripService.createTrip(1L, request);

        assertThat(response.packingItems()).isEmpty();
        assertThat(response.travelTip()).isEqualTo("팁");
    }
}
