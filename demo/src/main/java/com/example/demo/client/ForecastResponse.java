package com.example.demo.client;

import java.util.List;

// OpenWeatherMap 5일/3시간 예보 API(/data/2.5/forecast) 응답.
// 여행 기간(startDate~endDate)에 해당하는 3시간 슬롯들을 모아 최저/최고기온·강수확률·날씨 요약을 계산하는 데 쓴다.
public record ForecastResponse(List<ForecastEntry> list) {
}
