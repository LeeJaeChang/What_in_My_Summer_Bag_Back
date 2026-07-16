package com.example.demo.client;

import java.util.List;

public record ForecastEntry(long dt, MainInfo main, List<WeatherDescription> weather, double pop) {
}
