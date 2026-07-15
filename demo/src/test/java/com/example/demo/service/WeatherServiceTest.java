package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.demo.client.ForecastEntry;
import com.example.demo.client.ForecastResponse;
import com.example.demo.client.GeocodingResult;
import com.example.demo.client.MainInfo;
import com.example.demo.client.OpenWeatherClient;
import com.example.demo.client.WeatherDescription;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.SupportedRegion;
import com.example.demo.icon.TdsWeatherIcon;
import com.example.demo.repository.SupportedRegionRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    private static final ZoneOffset KST = ZoneOffset.ofHours(9);

    @Mock
    private OpenWeatherClient openWeatherClient;

    @Mock
    private SupportedRegionRepository supportedRegionRepository;

    @Test
    void 강수확률이_가장_높은_시점의_condition으로_아이콘을_고른다() {
        WeatherService weatherService = new WeatherService(openWeatherClient, supportedRegionRepository);
        LocalDate startDate = LocalDate.now(KST).plusDays(1);

        when(supportedRegionRepository.findById("서울"))
                .thenReturn(Optional.of(new SupportedRegion("서울", "Seoul,KR")));

        when(openWeatherClient.geocode(anyString()))
                .thenReturn(Optional.of(new GeocodingResult("Seoul", 37.5665, 126.9780, "KR")));
        when(openWeatherClient.fetchForecast(anyDouble(), anyDouble()))
                .thenReturn(new ForecastResponse(List.of(
                        entryAt(startDate, 9, 800, 0.1),
                        entryAt(startDate, 12, 500, 0.8),
                        entryAt(startDate, 15, 801, 0.3))));

        WeatherResponse response = weatherService.getWeather("서울", startDate, startDate);

        assertThat(response.weatherIconKey()).isEqualTo(TdsWeatherIcon.RAIN.assetKey());
        assertThat(response.precipitationProbability()).isEqualTo(80);
    }

    @Test
    void 정의되지_않은_condition_코드는_흐림으로_대체된다() {
        WeatherService weatherService = new WeatherService(openWeatherClient, supportedRegionRepository);
        LocalDate startDate = LocalDate.now(KST).plusDays(1);

        when(supportedRegionRepository.findById("서울"))
                .thenReturn(Optional.of(new SupportedRegion("서울", "Seoul,KR")));

        when(openWeatherClient.geocode(anyString()))
                .thenReturn(Optional.of(new GeocodingResult("Seoul", 37.5665, 126.9780, "KR")));
        when(openWeatherClient.fetchForecast(anyDouble(), anyDouble()))
                .thenReturn(new ForecastResponse(List.of(
                        entryAt(startDate, 9, 999, 0.5))));

        WeatherResponse response = weatherService.getWeather("서울", startDate, startDate);

        assertThat(response.weatherIconKey()).isEqualTo(TdsWeatherIcon.CLOUDY.assetKey());
    }

    @Test
    void 맑음_그룹_경계값_800은_CLEAR로_분류된다() {
        WeatherService weatherService = new WeatherService(openWeatherClient, supportedRegionRepository);
        LocalDate startDate = LocalDate.now(KST).plusDays(1);

        when(supportedRegionRepository.findById("서울"))
                .thenReturn(Optional.of(new SupportedRegion("서울", "Seoul,KR")));

        when(openWeatherClient.geocode(anyString()))
                .thenReturn(Optional.of(new GeocodingResult("Seoul", 37.5665, 126.9780, "KR")));
        when(openWeatherClient.fetchForecast(anyDouble(), anyDouble()))
                .thenReturn(new ForecastResponse(List.of(
                        entryAt(startDate, 9, 800, 0.0))));

        WeatherResponse response = weatherService.getWeather("서울", startDate, startDate);

        assertThat(response.weatherIconKey()).isEqualTo(TdsWeatherIcon.CLEAR.assetKey());
    }

    private ForecastEntry entryAt(LocalDate date, int hour, int conditionId, double pop) {
        long dt = date.atStartOfDay(KST).plusHours(hour).toEpochSecond();
        MainInfo main = new MainInfo(25.0, 26.0, 22.0, 28.0);
        return new ForecastEntry(dt, main, List.of(new WeatherDescription(conditionId, "desc")), pop);
    }
}
