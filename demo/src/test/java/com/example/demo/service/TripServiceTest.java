package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.TripDetailResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.ActivityType;
import com.example.demo.entity.Member;
import com.example.demo.recommend.client.AiRecommendClient;
import com.example.demo.recommend.dto.AiRecommendResult;
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

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    // 이 테스트들은 날씨 조회 분기만 검증한다. AI 추천 내용은 TripServiceAiRecommendTest에서 다룬다.
    private static final AiRecommendResult EMPTY_AI_RESULT =
            new AiRecommendResult("여행 팁", List.of());

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
    void 예보_범위를_벗어나면_작년_날씨로_대체해서_여행을_생성한다() {
        TripService tripService =
                new TripService(tripRepository, packingItemRepository, memberRepository,
                        productLinkRepository, weatherService, aiRecommendClient);

        Member member = new Member(123L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        LocalDate startDate = LocalDate.now().plusDays(30);
        LocalDate endDate = startDate.plusDays(2);
        CreateTripRequest request = new CreateTripRequest("서울", startDate, endDate, List.of(ActivityType.SEA));

        when(weatherService.getWeather("서울", startDate, endDate))
                .thenThrow(new UnsupportedDateRangeException("예보 가능 범위를 벗어났습니다."));
        WeatherResponse lastYearWeather = new WeatherResponse(10.0, 18.0, 14.0, 30, "u1F327");
        when(weatherService.getLastYearWeather("서울", startDate, endDate))
                .thenReturn(lastYearWeather);
        when(aiRecommendClient.recommend(request, lastYearWeather)).thenReturn(EMPTY_AI_RESULT);

        TripDetailResponse response = tripService.createTrip(1L, request);

        assertThat(response.weather()).isEqualTo(lastYearWeather);
        verify(weatherService).getWeather("서울", startDate, endDate);
        verify(weatherService).getLastYearWeather("서울", startDate, endDate);
    }

    @Test
    void 예보_범위_이내면_작년_날씨를_조회하지_않는다() {
        TripService tripService =
                new TripService(tripRepository, packingItemRepository, memberRepository,
                        productLinkRepository, weatherService, aiRecommendClient);

        Member member = new Member(123L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate;
        CreateTripRequest request = new CreateTripRequest("서울", startDate, endDate, List.of(ActivityType.SEA));

        WeatherResponse forecastWeather = new WeatherResponse(20.0, 26.0, 24.0, 10, "u2600");
        when(weatherService.getWeather("서울", startDate, endDate)).thenReturn(forecastWeather);
        when(aiRecommendClient.recommend(request, forecastWeather)).thenReturn(EMPTY_AI_RESULT);

        TripDetailResponse response = tripService.createTrip(1L, request);

        assertThat(response.weather()).isEqualTo(forecastWeather);
        verify(weatherService, never()).getLastYearWeather("서울", startDate, endDate);
    }
}
