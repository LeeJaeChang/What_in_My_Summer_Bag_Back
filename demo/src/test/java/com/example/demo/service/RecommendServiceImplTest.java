package com.example.demo.service;

import com.example.demo.entity.ActivityType;
import com.example.demo.entity.PackingCategory;
import com.example.demo.recommend.client.AiRecommendClient;
import com.example.demo.recommend.client.WeatherClient;
import com.example.demo.recommend.dto.AiPackingItem;
import com.example.demo.recommend.dto.AiRecommendResult;
import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.RecommendResponse;
import com.example.demo.recommend.dto.WeatherSummaryResponse;
import com.example.demo.recommend.service.RecommendServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecommendServiceImplTest {

    private AiRecommendClient aiRecommendClient;
    private WeatherClient weatherClient;
    private RecommendServiceImpl recommendService;

    @BeforeEach
    void setUp() {
        aiRecommendClient = mock(AiRecommendClient.class);
        weatherClient = mock(WeatherClient.class);

        recommendService = new RecommendServiceImpl(
                aiRecommendClient,
                weatherClient
        );
    }

    @Test
    void 유효한_아이콘키는_그대로_반환한다() {
        RecommendResponse response = executeRecommend("u1F9F4");

        assertThat(response.packingItems())
                .hasSize(1);

        assertThat(response.packingItems().get(0).iconKey())
                .isEqualTo("u1F9F4");
    }

    @Test
    void 유효하지_않은_아이콘키는_기본값으로_변경한다() {
        RecommendResponse response = executeRecommend("invalid-icon");

        assertThat(response.packingItems())
                .hasSize(1);

        assertThat(response.packingItems().get(0).iconKey())
                .isEqualTo("u1F4E6");
    }

    @Test
    void 아이콘키가_null이면_기본값으로_변경한다() {
        RecommendResponse response = executeRecommend(null);

        assertThat(response.packingItems().get(0).iconKey())
                .isEqualTo("u1F4E6");
    }

    private RecommendResponse executeRecommend(String iconKey) {
        RecommendRequest request = createRequest();
        WeatherSummaryResponse weather = createWeather();

        AiRecommendResult aiResult = new AiRecommendResult(
                "여행 팁",
                List.of(
                        new AiPackingItem(
                                "선크림",
                                PackingCategory.SUN_PROTECTION,
                                iconKey,
                                "자외선 차단을 위해 필요해요",
                                1
                        )
                )
        );

        when(weatherClient.getWeather(request))
                .thenReturn(weather);

        when(aiRecommendClient.recommend(request, weather))
                .thenReturn(aiResult);

        return recommendService.recommend(request);
    }

    private RecommendRequest createRequest() {
        return new RecommendRequest(
                "부산",
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 7, 22),
                List.of(ActivityType.SEA)
        );
    }

    private WeatherSummaryResponse createWeather() {
        return new WeatherSummaryResponse(
                24.0,
                30.0,
                32.0,
                15
        );
    }
}