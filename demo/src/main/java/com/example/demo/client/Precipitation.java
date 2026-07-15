package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonProperty;

// OWM timemachine 응답의 rain/snow 필드. 5일 이상 지난 과거 조회는 그 날 하루 합산량이 "1h" 키에 담겨 온다.
public record Precipitation(@JsonProperty("1h") Double oneHour) {
}
