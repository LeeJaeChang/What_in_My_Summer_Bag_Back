## 로컬 개발 환경 세팅

### 1. 준비물
- JDK 17
- 로컬 PostgreSQL (버전 무관, 개발용이면 최신 안정 버전 권장)

### 2. 환경변수 설정

민감한 값(DB 계정, API 키 등)은 Git에 올리지 않고 각자 로컬의 `.env` 파일로 관리한다.

```bash
cd demo
cp .env.example .env
```

`.env` 파일을 열어서 본인 로컬 값으로 채운다.

| 변수 | 설명 |
| --- | --- |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | 로컬 PostgreSQL 접속 정보 |
| `TOSS_CLIENT_ID`, `TOSS_CLIENT_SECRET` | 앱인토스(Toss) 연동 키 (지금은 스텁, 미사용) |
| `OPENWEATHER_API_KEY` | [OpenWeatherMap](https://openweathermap.org/api) 발급 키 |
| `AI_API_KEY` | AI 추천 기능용 API 키 |

`.env`는 각자 컴퓨터에만 존재하며 Git에 커밋되지 않는다 (`.gitignore` 처리됨). 값이 서로 달라도 문제없다.

### 3. 로컬 DB 생성

`.env`에 적은 `DB_NAME`으로 데이터베이스를 미리 만들어둬야 한다 (테이블/스키마는 Flyway가 앱 실행 시 자동 생성).

```bash
psql -U postgres -h localhost -c "CREATE DATABASE summerbag;"
```

### 4. 실행

```bash
cd demo
./gradlew bootRun
```

정상 기동되면 `http://localhost:8080`에서 응답한다.
