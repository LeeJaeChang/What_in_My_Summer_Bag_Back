package com.example.demo.recommend.dto;
import com.example.demo.recommend.domain.ActivityType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record RecommendRequest(

        @NotBlank(message = "여행지는 필수입니다.")
        String destination,

        @NotNull(message = "여행 시작일은 필수입니다.")
        @FutureOrPresent(message = "여행 시작일은 오늘 이후여야 합니다.")
        LocalDate startDate,

        @NotNull(message = "여행 종료일은 필수입니다.")
        @FutureOrPresent(message = "여행 종료일은 오늘 이후여야 합니다.")
        LocalDate endDate,

        @NotEmpty(message = "활동을 하나 이상 선택해야 합니다.")
        List<ActivityType> activityTypes
) {
}
