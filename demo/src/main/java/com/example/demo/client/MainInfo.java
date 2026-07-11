package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MainInfo(double temp, @JsonProperty("feels_like") double feelsLike) {
}
