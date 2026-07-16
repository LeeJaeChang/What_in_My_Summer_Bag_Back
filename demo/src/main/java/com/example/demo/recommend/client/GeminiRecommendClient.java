package com.example.demo.recommend.client;

import com.example.demo.recommend.dto.AiRecommendResult;
import com.example.demo.recommend.dto.RecommendRequest;
import com.example.demo.recommend.dto.WeatherSummaryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Component;

@Component
public class GeminiRecommendClient implements AiRecommendClient {

    private static final String MODEL_NAME = "gemini-3.5-flash";

    private final Client client;
    private final ObjectMapper objectMapper;

    public GeminiRecommendClient(ObjectMapper objectMapper) {
        this.client = new Client();
        this.objectMapper = objectMapper;
    }

    @Override
    public AiRecommendResult recommend(
            RecommendRequest request,
            WeatherSummaryResponse weather
    ) {
        String prompt = createPrompt(request, weather);

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .responseMimeType("application/json")
                        .build();

        GenerateContentResponse response =
                client.models.generateContent(
                        MODEL_NAME,
                        prompt,
                        config
                );

        String responseText = response.text();

        if (responseText == null || responseText.isBlank()) {
            throw new IllegalStateException(
                    "Gemini 추천 응답이 비어 있습니다."
            );
        }

        return parseResponse(responseText);
    }

    private AiRecommendResult parseResponse(String responseText) {
        try {
            return objectMapper.readValue(
                    responseText,
                    AiRecommendResult.class
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Gemini 응답을 변환할 수 없습니다.",
                    e
            );
        }
    }

    private String createPrompt(
            RecommendRequest request,
            WeatherSummaryResponse weather
    ) {
        return """
당신은 여행 준비물 추천 도우미입니다.

다음 여행 조건과 날씨를 분석해서 여행 팁과 준비물을 추천하세요.

여행지: %s
여행 시작일: %s
여행 종료일: %s
활동 종류: %s
예상 최저기온: %.1f도
예상 최고기온: %.1f도
체감기온: %.1f도
강수 확률: %d%%

반드시 아래 JSON 형식으로만 응답하세요.
JSON 이외의 설명이나 마크다운은 작성하지 마세요.

{
  "travelTip": "여행 팁",
  "packingItems": [
    {
      "name": "준비물 이름",
      "category": "CLOTHING",
      "iconKey": "u1F9F4",
      "reason": "추천 이유",
      "sortOrder": 1
    }
  ]
}

규칙:
1. 준비물은 3개 이상 8개 이하로 추천하세요.
2. 준비물 이름은 중복되지 않아야 합니다.
3. 추천 이유는 날씨 또는 활동과 연결해서 작성하세요.
4. sortOrder는 1부터 순서대로 부여하세요.
5. travelTip과 reason은 한국어로 작성하세요.
6. category는 아래 값 중 하나만 사용하세요.
   CLOTHING, ELECTRONICS, TOILETRIES,
   DOCUMENTS, MEDICINE, ETC
7. 여행지가 대한민국 국내 지역이면 국내 여행에 적합한 준비물을 중심으로 추천하세요.
8. 국내 여행에는 여권, 비자, 해외용 변환 플러그, 해외 유심처럼 불필요한 해외여행 준비물을 추천하지 마세요.
9. 여행지가 해외 지역이면 해외여행 준비물을 우선 고려하세요.
10. 해외여행인 경우 여권, 비자 필요 여부, 해외 결제수단, 현지 통화,
    여행자 보험, 유심 또는 eSIM, 변환 플러그 등을 여행지 특성에 맞게 고려하세요.
11. 비자가 필요하지 않은 여행지에는 비자를 필수 준비물처럼 추천하지 마세요.
12. 입력된 여행지, 날씨, 활동과 직접 관련 없는 준비물은 추천하지 마세요.
""".formatted(
                request.destination(),
                request.startDate(),
                request.endDate(),
                request.activityTypes(),
                weather.temperatureMin(),
                weather.temperatureMax(),
                weather.temperaturePerceived(),
                weather.precipitationProbability()
        );
    }
}