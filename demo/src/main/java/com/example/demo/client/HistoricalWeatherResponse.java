package com.example.demo.client;

import java.util.List;

// OpenWeatherMap One Call API 3.0 timemachine(/data/3.0/onecall/timemachine) 응답.
// 5일 이상 지난 과거 날짜를 조회하면 data에 그 날 하루를 대표하는 원소 하나만 들어온다.
public record HistoricalWeatherResponse(double lat, double lon, List<HistoricalDataPoint> data) {
}
