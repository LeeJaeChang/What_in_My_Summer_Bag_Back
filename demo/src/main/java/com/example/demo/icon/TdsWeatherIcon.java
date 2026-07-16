package com.example.demo.icon;

import java.util.Optional;

/**
 * OpenWeatherMap(OWM) 날씨 상태 코드 → TDS 날씨 아이콘 키 매핑 카탈로그.
 *
 * 검증 계층 전용: 엔티티(Trip.weatherIconKey)에는 String으로 저장하며 이 enum을
 * @Enumerated로 쓰지 않는다. OWM 코드로부터 아이콘 키를 유도하거나(fromOwmCode),
 * 외부/LLM이 준 키가 유효한 TDS 키인지 검증하는(from/isValid) 용도.
 */
public enum TdsWeatherIcon {

    CLEAR("u2600"),          // ☀️ 맑음 (OWM 800)
    FEW_CLOUDS("u26C5"),     // ⛅ 구름 조금 (OWM 801~802)
    CLOUDY("u2601"),         // ☁️ 흐림 (OWM 803~804)
    DRIZZLE("u1F326"),       // 🌦️ 이슬비/소나기 (OWM 3xx)
    RAIN("u1F327"),          // 🌧️ 비 (OWM 5xx)
    THUNDERSTORM("u26C8"),   // ⛈️ 천둥번개 (OWM 2xx)
    SNOW("u1F328"),          // 🌨️ 눈 (OWM 6xx)
    MIST("u1F32B");          // 🌫️ 안개/미세먼지 (OWM 7xx)

    private final String assetKey;

    TdsWeatherIcon(String assetKey) {
        this.assetKey = assetKey;
    }

    public String assetKey() {
        return assetKey;
    }

    /**
     * OWM 날씨 상태 코드(2xx~8xx)를 아이콘으로 매핑한다.
     * 정의되지 않은 코드는 흐림(CLOUDY)으로 처리한다.
     */
    public static TdsWeatherIcon fromOwmCode(int code) {
        if (code == 800) {
            return CLEAR;
        }
        if (code == 801 || code == 802) {
            return FEW_CLOUDS;
        }
        if (code == 803 || code == 804) {
            return CLOUDY;
        }
        switch (code / 100) {
            case 2:
                return THUNDERSTORM;
            case 3:
                return DRIZZLE;
            case 5:
                return RAIN;
            case 6:
                return SNOW;
            case 7:
                return MIST;
            default:
                return CLOUDY;
        }
    }

    /** 문자열 키가 유효한 TDS 날씨 아이콘 키이면 해당 상수를, 아니면 빈 Optional을 반환한다. */
    public static Optional<TdsWeatherIcon> from(String assetKey) {
        for (TdsWeatherIcon icon : values()) {
            if (icon.assetKey.equals(assetKey)) {
                return Optional.of(icon);
            }
        }
        return Optional.empty();
    }

    public static boolean isValid(String assetKey) {
        return from(assetKey).isPresent();
    }
}
