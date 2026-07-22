# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper + 빌드 스크립트 먼저 복사(레이어 캐시)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

# 소스 복사 후 bootJar 생성(테스트는 DB 필요하므로 이미지 빌드 단계에선 제외)
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

# ---- Run stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Railway 는 $PORT 를 주입한다. application.properties 의 server.port=${PORT:8080} 가 이를 사용.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
