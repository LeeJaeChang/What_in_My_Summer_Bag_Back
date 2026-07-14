현재까지 구현한 내용은 다음과 같습니다.

RecommendRequest, RecommendResponse를 최종 API 구조에 맞게 수정
여행지, 시작일, 종료일, 활동 목록을 요청값으로 받도록 변경
날씨 응답 DTO와 준비물 응답 DTO 구성
WeatherClient, AiRecommendClient를 분리해 외부 API 연동 구조 구성
Google AI Studio에서 Gemini API 키를 발급받아 실제 AI 연동
Gemini Java SDK를 추가하고 GeminiRecommendClient 구현
여행 조건과 날씨 정보를 프롬프트로 만들어 Gemini에 전달
Gemini가 여행 팁과 준비물 이름, 카테고리, 추천 이유, 정렬 순서를 JSON 형식으로 반환하도록 구현
Gemini 응답 JSON을 Java DTO로 변환
AI 응답 DTO를 최종 API 응답 DTO로 변환
API 키는 코드가 아닌 IntelliJ 환경변수로 관리
Postman에서 실제 Gemini 추천 응답 정상 동작 확인

현재 흐름은 다음과 같습니다.

여행 조건 입력
→ 날씨 정보 조회
→ 여행 조건과 날씨를 프롬프트로 생성
→ Google AI Studio의 Gemini API 호출
→ 여행 팁과 준비물 추천 JSON 수신
→ Java DTO로 변환
→ 최종 API 응답 반환

현재 날씨 정보는 MockWeatherClient의 임시 데이터를 사용하고 있습니다.
