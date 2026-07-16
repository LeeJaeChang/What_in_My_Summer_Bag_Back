/*package com.example.demo.recommend.dto;

import com.example.demo.recommend.domain.ActivityType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendRequestTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .registerModule(new JavaTimeModule());

    @Test
    void convertsJsonToRecommendRequest() throws Exception {
        String json = """
                {
                  "region_name": "강릉",
                  "travel_date": "2026-07-15",
                  "activity_type": "SEA"
                }
                """;

        RecommendRequest request =
                objectMapper.readValue(json, RecommendRequest.class);

        assertThat(request.regionName()).isEqualTo("강릉");
        assertThat(request.travelDate().toString()).isEqualTo("2026-07-15");
        assertThat(request.activityType()).isEqualTo(ActivityType.SEA);
    }
}*/
