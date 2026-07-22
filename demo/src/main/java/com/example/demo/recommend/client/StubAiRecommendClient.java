package com.example.demo.recommend.client;

import com.example.demo.dto.CreateTripRequest;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.recommend.dto.AiPackingItem;
import com.example.demo.recommend.dto.AiRecommendResult;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 개발/테스트용 AI 추천 스텁. 실제 구현은 {@link GeminiRecommendClient}.
 *
 * 활성화: application.properties 의 ai.mode=stub (미설정 시 기본값은 gemini). Gemini 무료 티어는
 * 하루 20회 제한이라 프론트 연동이나 수동 API 테스트 중 금방 429가 나므로, 할당량을 쓰지 않고
 * 고정 응답으로 나머지 플로우(저장·조회·체크·구매 링크)를 확인할 때 쓴다.
 *
 * StubAuthService 와 동일하게 두 구현이 동시에 빈으로 뜨지 않도록 조건부로 등록한다.
 */
@Component
@ConditionalOnProperty(name = "ai.mode", havingValue = "stub")
public class StubAiRecommendClient implements AiRecommendClient {

    @Override
    public AiRecommendResult recommend(
            CreateTripRequest request,
            WeatherResponse weather
    ) {
        return new AiRecommendResult(
                "[스텁] %s 여행은 자외선이 강하니 오전 활동을 추천합니다. 최고 %.1f도까지 오릅니다."
                        .formatted(request.destination(), weather.temperatureMax()),
                List.of(
                        // 구매 링크가 매핑된 정상 항목
                        new AiPackingItem("선크림", "SUN_PROTECTION", "u1F9F4", "sunscreen",
                                "자외선 지수가 높습니다.", 1),
                        new AiPackingItem("수영복", "WATER", "u1FA71", "swimsuit",
                                "해수욕 활동이 있습니다.", 2),
                        // searchKeyword 가 없는 항목 — 구매 링크 조회 시 404 가 나야 한다(여권 등)
                        new AiPackingItem("여권", "DOCUMENTS", "u1F6C2", null,
                                "해외 여행 시 필수입니다.", 3)));
    }
}
