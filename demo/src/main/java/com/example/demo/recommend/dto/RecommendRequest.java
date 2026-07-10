package com.example.demo.recommend.dto;
import com.example.demo.recommend.domain.ActivityType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record RecommendRequest(

        @JsonProperty("region_name")
        String regionName,

        @JsonProperty("travel_date")
        LocalDate travelDate,

        @JsonProperty("activity_type")
        ActivityType activityType
) {
}
