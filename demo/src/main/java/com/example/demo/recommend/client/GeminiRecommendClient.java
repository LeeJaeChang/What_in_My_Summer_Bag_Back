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
"searchKeyword": "sunscreen",
"reason": "추천 이유",
"sortOrder": 1
}
]
}

searchKeyword 허용 목록:
[
  "sunscreen",
  "sun hat",
  "cooling arm sleeves",
  "aloe vera gel",
  "swimsuit",
  "beach towel",
  "waterproof phone pouch",
  "aqua shoes",
  "dry bag",
  "snorkel set",
  "socks",
  "pajamas",
  "sandals",
  "lightweight summer top",
  "lightweight summer pants",
  "windbreaker jacket",
  "travel toothbrush toiletry set",
  "travel skincare set",
  "shower head filter",
  "portable bidet wipes",
  "disposable toilet seat cover",
  "travel laundry detergent sheets",
  "bandages",
  "waterproof antiseptic ointment",
  "mosquito repellent spray",
  "mosquito repellent patch",
  "bed bug spray",
  "phone charger cable",
  "wireless earbuds",
  "mini power bank",
  "universal travel adapter",
  "portable mini fan",
  "3 in 1 charging cable",
  "ziplock bags",
  "umbrella parasol",
  "packing cubes",
  "travel neck pillow",
  "sleep mask",
  "luggage scale",
  "phone lanyard strap",
  "footrest hammock"
]

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
13. 모든 준비물에는 searchKeyword 필드를 반드시 포함하세요.
14. searchKeyword는 반드시 위의 searchKeyword 허용 목록에 있는 값 중 하나를 그대로 사용하세요.
15. searchKeyword의 철자, 띄어쓰기, 대소문자, 단수·복수 형태를 변경하지 마세요.
16. 허용 목록에 준비물과 의미가 일치하거나 가장 가까운 값이 있으면 해당 값을 사용하세요.
17. 허용 목록에 적절한 값이 없으면 searchKeyword를 null로 설정하세요.
18. 허용 목록에 없는 새로운 searchKeyword를 임의로 생성하지 마세요.
19. 준비물 이름을 영어로 번역하여 새로운 searchKeyword를 만들지 마세요.
20. searchKeyword에는 빈 문자열을 사용하지 마세요. 허용된 문자열 또는 null만 사용하세요.
21. JSON을 출력하기 전에 모든 searchKeyword가 허용 목록에 정확히 포함되어 있는지 확인하세요.
22. 허용 목록에 없는 searchKeyword가 생성되었다면 반드시 null로 변경하세요.
    """
                .formatted(
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