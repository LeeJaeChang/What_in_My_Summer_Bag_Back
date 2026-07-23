# What's in My Summer Bag — API 연동 가이드

프론트엔드 연동용 REST API 레퍼런스입니다.

- **Base URL**: `https://sumkit.up.railway.app`
- **공통 prefix**: 모든 API 경로는 `/api/v2/...`
- **Content-Type**: 요청/응답 모두 `application/json` (UTF-8)
- **공통 에러 포맷**: `{ "errorCode": "...", "message": "..." }`

---

## 인증 방식

1. **로그인**(`POST /api/v2/auth/login`)으로 `accessToken`을 받는다.
2. 이후 **보호된 API**는 모든 요청 헤더에 아래를 넣는다:
   ```
   Authorization: Bearer {accessToken}
   ```
3. 토큰이 없거나 유효하지 않으면 `401 UNAUTHORIZED`.

> ⚠️ 한글이 들어가는 쿼리 파라미터(예: `regions?q=서울`)는 **반드시 URL 인코딩**해서 보낸다. (인코딩 안 하면 서버 도달 전 400)

---

## 엔드포인트 요약

| # | Method | Path | 인증 | 설명 |
|---|--------|------|:---:|------|
| 1 | POST | `/api/v2/auth/login` | ✕ | 토스 로그인(인가코드 → accessToken) |
| 2 | GET | `/api/v2/members/me` | ✓ | 내 정보 조회**(프론트 구현 X)** |
| 3 | GET | `/api/v2/regions` | ✕ | 지원 지역 목록/검색 |
| 4 | POST | `/api/v2/trips` | ✓ | 여행 생성(+날씨+AI 준비물 추천) |
| 5 | GET | `/api/v2/trips` | ✓ | 내 여행 목록 |
| 6 | GET | `/api/v2/trips/{tripId}` | ✓ | 여행 상세 |
| 7 | GET | `/api/v2/trips/{tripId}/packing-items` | ✓ | 준비물 목록 |
| 8 | GET | `/api/v2/trips/{tripId}/packing-items/purchase-list` | ✓ | 구매 리스트(검색어 포함) |
| 9 | GET | `/api/v2/trips/{tripId}/packing-items/{itemId}/purchase-links` | ✓ | 상품 구매 링크 |
| 10 | PATCH | `/api/v2/trips/{tripId}/packing-items/{itemId}` | ✓ | 준비물 체크 토글 |

---

## 1. 로그인 — `POST /api/v2/auth/login`

인증 불필요.

**Request Body**
```json
{
  "authorizationCode": "토스 appLogin()이 반환한 일회성 인가코드",
  "referrer": "DEFAULT"
}
```
- `authorizationCode` (string, 필수): 토스 SDK가 발급한 인가코드. 유효 10분·일회성.
- `referrer` (string, 필수): `DEFAULT`(실제 토스 앱) | `SANDBOX`(테스트).

**Response 200**
```json
{ "memberId": 3, "accessToken": "eyJhbGc...", "isNewMember": true }
```
- `accessToken`: 이후 요청의 `Authorization: Bearer {accessToken}`에 사용.
- `isNewMember`: 첫 가입 여부(온보딩 분기용).

**주요 에러**: `401 TOSS_TOKEN_FAILED`, `400 INVALID_AUTH_CODE`, `400 MISSING_PARAMETER`

---

## 2. 내 정보 — `GET /api/v2/members/me`  🔒

**Response 200**
```json
{ "memberId": 3, "createdAt": "2026-07-23T14:07:42.581687" }
```

---

## 3. 지원 지역 — `GET /api/v2/regions`

인증 불필요. 여행 생성 시 `destination`으로 쓸 수 있는 지역 목록.

**Query Params**
- `q` (string, 선택): 검색어(부분 일치). 한글은 URL 인코딩 필수.

**Response 200**
```json
{ "count": 243, "regions": [ { "regionName": "서울" }, { "regionName": "부산" } ] }
```
- `q` 미지정 시 전체(243건), 지정 시 필터링 결과.

---

## 4. 여행 생성 — `POST /api/v2/trips`  🔒

날씨와 준비물은 서버가 자동 생성한다(요청 본문에 없음).

**Request Body**
```json
{
  "destination": "서울",
  "startDate": "2026-07-24",
  "endDate": "2026-07-26",
  "activityTypes": ["SIGHTSEEING", "FOOD_TOUR"]
}
```
- `destination` (string, 필수): `/regions`의 `regionName` 중 하나.
- `startDate` / `endDate` (string `YYYY-MM-DD`, 필수):
  - `startDate`는 **오늘 이후**여야 함.
  - `endDate >= startDate`.
  - 날씨 예보 특성상 **가까운 미래(약 5일 이내)** 범위를 권장.
- `activityTypes` (array, 필수·비어있으면 안 됨): 아래 `ActivityType` 값들.

**Response 200** (`TripDetailResponse`)
```json
{
  "tripId": 39,
  "destination": "서울",
  "startDate": "2026-07-24",
  "endDate": "2026-07-26",
  "weather": {
    "temperatureMin": 23.6,
    "temperatureMax": 30.7,
    "temperaturePerceived": 28.9,
    "precipitationProbability": 100,
    "weatherIconKey": "u1F327"
  },
  "activities": ["SIGHTSEEING", "FOOD_TOUR"],
  "travelTip": "서울 여행은 자외선이 강하니 오전 활동을 추천합니다. ...",
  "packingItems": [
    { "id": 189, "name": "선크림", "category": "SUN_PROTECTION", "reason": "자외선 지수가 높습니다.", "iconKey": "u1F9F4", "checked": false, "sortOrder": 1 }
  ]
}
```

**주요 에러**: `400 INVALID_DATE_RANGE`, `400 INVALID_REGION`, `400 MISSING_PARAMETER`, `500 WEATHER_FETCH_FAILED`, `500 AI_RECOMMEND_FAILED`

---

## 5. 여행 목록 — `GET /api/v2/trips`  🔒

**Query Params**
- `page` (int, 기본 `0`), `size` (int, 기본 `10`)

**Response 200** (`TripListResponse`)
```json
{
  "trips": [
    { "tripId": 39, "destination": "서울", "startDate": "2026-07-24", "endDate": "2026-07-26", "weatherIconKey": "u1F327", "createdAt": "2026-07-23T14:07:42.827728" }
  ],
  "totalCount": 1
}
```

---

## 6. 여행 상세 — `GET /api/v2/trips/{tripId}`  🔒

**Response 200**: 4번과 동일한 `TripDetailResponse`.

**주요 에러**: `404 TRIP_NOT_FOUND`, `403 FORBIDDEN_TRIP_ACCESS`(남의 여행)

---

## 7. 준비물 목록 — `GET /api/v2/trips/{tripId}/packing-items`  🔒

**Response 200** (`PackingItemListResponse`)
```json
{
  "packingItems": [
    { "id": 189, "name": "선크림", "category": "SUN_PROTECTION", "reason": "자외선 지수가 높습니다.", "iconKey": "u1F9F4", "checked": false, "sortOrder": 1 },
    { "id": 190, "name": "수영복", "category": "WATER", "reason": "해수욕 활동이 있습니다.", "iconKey": "u1FA71", "checked": false, "sortOrder": 2 }
  ]
}
```

---

## 8. 구매 리스트 — `GET /api/v2/trips/{tripId}/packing-items/purchase-list`  🔒

준비물 + 각 항목의 쇼핑 검색어(`searchKeyword`)를 포함.

**Response 200** (`PurchaseListResponse`)
```json
{
  "tripId": 39,
  "destination": "서울",
  "weather": { "temperatureMin": 23.6, "temperatureMax": 30.7, "temperaturePerceived": 28.9, "precipitationProbability": 100, "weatherIconKey": "u1F327" },
  "activities": ["SIGHTSEEING", "FOOD_TOUR"],
  "travelTip": "...",
  "items": [
    { "id": 189, "name": "선크림", "category": "SUN_PROTECTION", "reason": "자외선 지수가 높습니다.", "iconKey": "u1F9F4", "searchKeyword": "sunscreen", "checked": false, "sortOrder": 1 }
  ]
}
```

---

## 9. 구매 링크 — `GET /api/v2/trips/{tripId}/packing-items/{itemId}/purchase-links`  🔒

특정 준비물의 쿠팡 상품 링크(최대 2개).

**Response 200** (`PurchaseLinkResponse`)
```json
{
  "itemId": 189,
  "itemName": "선크림",
  "searchKeyword": "sunscreen",
  "title": "선크림",
  "brand1Name": "미샤 선크림",
  "link1Url": "https://link.coupang.com/a/...",
  "link1Image": "https://thumbnail7.coupangcdn.com/...",
  "brand2Name": "에스쁘아 선크림",
  "link2Url": "https://link.coupang.com/a/...",
  "link2Image": "https://thumbnail12.coupangcdn.com/..."
}
```

**주요 에러**: `404 PURCHASE_LINK_NOT_FOUND`, `404 PACKING_ITEM_NOT_FOUND`

---

## 10. 준비물 체크 토글 — `PATCH /api/v2/trips/{tripId}/packing-items/{itemId}`  🔒

**Request Body**
```json
{ "checked": true }
```

**Response 200** (`TogglePackingItemResponse`)
```json
{ "id": 189, "checked": true }
```

**주요 에러**: `404 PACKING_ITEM_NOT_FOUND`, `403 FORBIDDEN_TRIP_ACCESS`

---

## Enum 값

### ActivityType (여행 활동)
`SEA`(해양), `CAMPING`(캠핑), `HIKING`(등산), `SIGHTSEEING`(관광), `SHOPPING`(쇼핑), `FOOD_TOUR`(맛집투어), `DRIVING`(드라이브)

### PackingCategory (준비물 카테고리 — 응답의 `category` 필드)
`CLOTHING`, `DOCUMENTS`, `ELECTRONICS`, `HEALTH`, `SUN_PROTECTION`, `TOILETRIES`, `WATER`, `ETC`

> `iconKey` / `weatherIconKey`는 TDS 아이콘 키(예: `u1F327`) 문자열이다. 프론트 아이콘 매핑에 사용.

---

## 에러 코드 표

| HTTP | errorCode | 발생 상황 |
|:---:|-----------|-----------|
| 400 | `MISSING_PARAMETER` | 필수 파라미터 누락 |
| 400 | `INVALID_DATE_RANGE` | 날짜 범위 오류(과거 시작일, 종료<시작, 미지원 범위) |
| 400 | `INVALID_REGION` | 지원하지 않는 지역 |
| 400 | `INVALID_QUERY` | 검색어 형식 오류 |
| 400 | `INVALID_AUTH_CODE` | 인가코드 만료/재사용 |
| 400 | `BAD_REQUEST` | 기타 잘못된 요청 |
| 401 | `UNAUTHORIZED` | 토큰 없음/유효하지 않음 |
| 401 | `TOSS_TOKEN_FAILED` | 토스 토큰 교환 실패 |
| 403 | `FORBIDDEN_TRIP_ACCESS` | 다른 회원의 여행 접근 |
| 404 | `TRIP_NOT_FOUND` | 없는 여행 |
| 404 | `PACKING_ITEM_NOT_FOUND` | 없는 준비물 |
| 404 | `PURCHASE_LINK_NOT_FOUND` | 구매 링크 없음 |
| 404 | `MEMBER_NOT_FOUND` | 없는 회원 |
| 500 | `WEATHER_FETCH_FAILED` | 날씨 API 실패 |
| 500 | `AI_RECOMMEND_FAILED` | AI 추천 실패 |

---

## 연동 흐름 예시

```
1) 토스 SDK appLogin() → authorizationCode 획득
2) POST /auth/login { authorizationCode, referrer:"DEFAULT" } → accessToken 저장
3) GET /regions 로 지역 목록 표시
4) POST /trips { destination, startDate, endDate, activityTypes } → tripId + 준비물
5) GET /trips (목록), GET /trips/{id} (상세)
6) 준비물 체크: PATCH /trips/{id}/packing-items/{itemId} { checked }
7) 구매: GET .../purchase-list, GET .../{itemId}/purchase-links
```

모든 보호 API 요청에는 `Authorization: Bearer {accessToken}` 헤더 필수.
