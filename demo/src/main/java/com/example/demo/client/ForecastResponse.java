package com.example.demo.client;

import java.util.List;

// OpenWeatherMap 5일/3시간 예보 API(/data/2.5/forecast) 응답.
// 무료 티어에 강수확률(pop)이 있는 게 이 엔드포인트뿐이라 실시간 날씨와 별도로 호출한다.
record ForecastResponse(List<ForecastEntry> list) {
}
