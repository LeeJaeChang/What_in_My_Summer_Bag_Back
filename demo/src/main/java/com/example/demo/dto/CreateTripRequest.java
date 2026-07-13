package com.example.demo.dto;

import com.example.demo.entity.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 여행 계획 생성 요청. 준비물(PackingItem)은 추후 AI가 생성하므로 여기서 받지 않는다.
 * 날씨 필드도 서버가 별도 날씨 조회로 채우므로 요청 본문에는 없다.
 */
public record CreateTripRequest(
        @NotBlank String destination,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotEmpty List<ActivityType> activities
) {
}
