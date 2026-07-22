# Railway 배포 가이드

이 백엔드(Spring Boot 4 / Java 17 / Gradle)를 Railway에 배포하는 절차입니다.
빌드는 저장소의 `Dockerfile`을 사용합니다.

---

## 1. 서비스 생성

1. Railway → **New Project → Deploy from GitHub repo** → 이 저장소 선택
2. 서비스 **Settings → Root Directory** 를 **`demo`** 로 지정
   (Spring 프로젝트가 저장소 루트가 아니라 `demo/` 하위에 있음)
3. Builder는 `Dockerfile` 자동 감지됨(별도 설정 불필요)

## 2. PostgreSQL 추가

1. 프로젝트에 **New → Database → PostgreSQL** 추가
2. Flyway 마이그레이션(`V1__init.sql` 등)이 앱 기동 시 자동 실행되어 스키마가 생성됨

## 3. 환경변수 설정

서비스 **Variables** 탭에 아래를 등록합니다.

### DB (Railway Postgres 참조 변수 사용)
```
DB_HOST=${{Postgres.PGHOST}}
DB_PORT=${{Postgres.PGPORT}}
DB_NAME=${{Postgres.PGDATABASE}}
DB_USERNAME=${{Postgres.PGUSER}}
DB_PASSWORD=${{Postgres.PGPASSWORD}}
```
> Postgres 서비스 이름이 `Postgres`가 아니면 `${{실제이름.PGHOST}}` 형태로 맞추세요.

### 인증 / mTLS
```
AUTH_MODE=toss
TOSS_MTLS_KEY_STORE_BASE64=<아래 4번에서 생성한 base64 문자열>
TOSS_MTLS_KEY_STORE_PASSWORD=<.p12 비밀번호>
```
> ⚠️ 파일 경로(`TOSS_MTLS_KEY_STORE`)는 클라우드에서 쓰지 않습니다. **base64 변수만** 넣으세요.

### 외부 API / JWT
```
OPENWEATHER_API_KEY=<발급받은 키>
GEMINI_API_KEY=<발급받은 키>
JWT_SECRET=<운영용 강한 랜덤 값, 32바이트 이상>
```

> `PORT`는 Railway가 자동 주입하므로 직접 넣지 않습니다.
> (앱은 `server.port=${PORT:8080}`로 이를 사용)

## 4. mTLS 인증서를 base64로 변환 (로컬에서 실행)

`.p12` 파일을 base64 문자열로 만들어 `TOSS_MTLS_KEY_STORE_BASE64` 에 붙여넣습니다.

```bash
# 저장소 demo/ 에서 실행
base64 -i secrets/toss-client.p12 | tr -d '\n' | pbcopy   # macOS: 클립보드로 복사
# 또는 파일로:
base64 -i secrets/toss-client.p12 > toss-client.p12.b64
```

> 🔐 이 base64 값은 **개인키가 포함된 민감정보**입니다. Git에 커밋하거나 로그·채팅에 남기지 마세요.
> Railway Variables 입력창에만 붙여넣고, 생성한 `.b64` 파일은 삭제하세요.

## 5. 배포 & 확인

1. 저장소에 push하면 Railway가 자동 빌드/배포
2. 로그에서 `Started ... in N seconds` 확인
3. 서비스 도메인(Settings → Networking → Generate Domain)으로 헬스 체크

---

## 인증서 없이 임시로 띄우려면
개발/데모 목적이면 `AUTH_MODE=stub` 으로 두면 mTLS 없이 기동됩니다(실제 토스 로그인은 안 됨).

## 남은 이슈
토스 응답 `4050 "등록된 미니앱이 아닙니다"` 는 콘솔 측 미니앱 등록/활성화가 끝나야 해소됩니다.
mTLS 파이프라인 자체는 정상 동작함을 확인했습니다.
