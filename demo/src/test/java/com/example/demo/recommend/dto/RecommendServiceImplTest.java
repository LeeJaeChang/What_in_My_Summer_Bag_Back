package com.example.demo.recommend.dto;

import com.example.demo.recommend.domain.ActivityType;
import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.RecommendResponse;
import com.example.demo.recommend.service.RecommendService;
import com.example.demo.recommend.service.RecommendServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendServiceImplTest {

    private final RecommendService recommendService =
            new RecommendServiceImpl();

    @Test
    void returnsTemporaryRecommendation() {
        RecommendRequest request = new RecommendRequest(
                "강릉",
                LocalDate.of(2026, 7, 15),
                ActivityType.SEA
        );

        RecommendResponse response =
                recommendService.recommend(request);

        assertThat(response.recommendationId())
                .isEqualTo("temp-id");

        assertThat(response.weatherSummary().temperature())
                .isEqualTo(33);

        assertThat(response.weatherSummary().precipitationProbability())
                .isEqualTo(10);

        assertThat(response.weatherSummary().condition())
                .isEqualTo("맑음");

        assertThat(response.items())
                .hasSize(1);

        assertThat(response.items().get(0).itemId())
                .isEqualTo("0001");

        assertThat(response.items().get(0).name())
                .isEqualTo("선크림");

        assertThat(response.items().get(0).essential())
                .isTrue();

        assertThat(response.items().get(0).reason())
                .isEqualTo("자외선이 강해요.");
    }
}