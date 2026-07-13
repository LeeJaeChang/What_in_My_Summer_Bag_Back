package com.example.demo.dto;

import com.example.demo.entity.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * POST /trips 요청. 날씨(weather)와 준비물(packingItems)은 서버가 날씨 API + AI로 생성하므로
 * 요청 본문에는 없다.
 */
public record CreateTripRequest(
        @NotBlank String destination,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotEmpty List<ActivityType> activityTypes
) {
}
