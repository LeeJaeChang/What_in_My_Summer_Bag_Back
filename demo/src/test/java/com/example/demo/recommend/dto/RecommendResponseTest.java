package com.example.demo.recommend.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 추천_응답을_JSON으로_변환한다() throws Exception {
        WeatherSummaryResponse weatherSummary =
                new WeatherSummaryResponse(
                        33,
                        10,
                        "맑음"
                );

        List<RecommendItemResponse> items = List.of(
                new RecommendItemResponse(
                        "0001",
                        "선크림",
                        true,
                        "자외선 지수가 매우 높아요"
                )
        );

        RecommendResponse response =
                new RecommendResponse(
                        "rec_20260709_001",
                        weatherSummary,
                        items
                );

        String json = objectMapper.writeValueAsString(response);

        System.out.println(json);

        assertThat(json).contains("\"recommendation_id\"");
        assertThat(json).contains("\"weather_summary\"");
        assertThat(json).contains("\"precipitation_probability\"");
        assertThat(json).contains("\"is_essential\"");
    }
}
