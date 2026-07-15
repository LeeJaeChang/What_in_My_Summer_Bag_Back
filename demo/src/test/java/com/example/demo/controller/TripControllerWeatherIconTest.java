package com.example.demo.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.auth.AuthService;
import com.example.demo.dto.TripDetailResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.icon.TdsWeatherIcon;
import com.example.demo.service.TripService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// TripController가 실제 HTTP 요청/JSON 직렬화 경로에서 weatherIconKey를 제대로 내려주는지 확인한다.
// DB/OpenWeatherMap 실호출 없이 TripService만 목으로 대체한 슬라이스 테스트.
@WebMvcTest(TripController.class)
class TripControllerWeatherIconTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @MockitoBean
    private AuthService authService;

    @Test
    void trip_상세_응답_JSON에_weatherIconKey가_포함된다() throws Exception {
        when(authService.resolveMemberId(anyString())).thenReturn(1L);
        WeatherResponse weather = new WeatherResponse(22.5, 28.0, 27.0, 80, TdsWeatherIcon.RAIN.assetKey());
        TripDetailResponse detail = new TripDetailResponse(
                1L, "서울", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                weather, List.of("SEA"), null, List.of());
        when(tripService.getTrip(anyLong(), anyLong())).thenReturn(detail);

        mockMvc.perform(get("/api/v2/trips/1").header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weather.weatherIconKey", is(TdsWeatherIcon.RAIN.assetKey())))
                .andExpect(jsonPath("$.weather.precipitationProbability", is(80)));
    }
}
