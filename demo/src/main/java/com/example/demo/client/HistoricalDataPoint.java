package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// OWM One Call 4.0 1-day 타임라인의 하루치 레코드.
// pop/weather는 일부 날짜에 대해 OWM 응답에서 통째로 빠지는 경우가 있어 nullable로 받는다.
public record HistoricalDataPoint(
        long dt,
        HistoricalTemperature temp,
        @JsonProperty("feels_like") HistoricalFeelsLike feelsLike,
        List<WeatherDescription> weather,
        Double pop
) {
}
