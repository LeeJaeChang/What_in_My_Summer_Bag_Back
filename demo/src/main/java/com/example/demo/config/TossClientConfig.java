package com.example.demo.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.util.Base64;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 앱인토스(파트너 서버 → 앱인토스 서버) 통신용 RestClient.
 *
 * 이 구간은 mTLS 가 필수라 클라이언트 인증서를 실은 SSLContext 로 HttpClient 를 만든다.
 * toss.mtls.key-store 가 비어 있으면(로컬/테스트) 기본 SSL 로 동작한다 — 실제 토스 호출은 실패하므로
 * 운영에서는 반드시 인증서를 주입할 것.
 */
@Configuration
public class TossClientConfig {

    @Bean
    public RestClient tossRestClient(
            ResourceLoader resourceLoader,
            @Value("${toss.base-url}") String baseUrl,
            @Value("${toss.mtls.key-store:}") String keyStoreLocation,
            @Value("${toss.mtls.key-store-base64:}") String keyStoreBase64,
            @Value("${toss.mtls.key-store-password:}") String keyStorePassword,
            @Value("${toss.mtls.trust-store:}") String trustStoreLocation,
            @Value("${toss.mtls.trust-store-password:}") String trustStorePassword) {

        HttpClient.Builder httpClientBuilder = HttpClient.newBuilder();
        // 로컬은 파일 경로(key-store), 클라우드(Railway 등)는 파일을 못 올리므로 base64 환경변수로 주입한다.
        if (!keyStoreBase64.isBlank() || !keyStoreLocation.isBlank()) {
            httpClientBuilder.sslContext(sslContext(
                    resourceLoader, keyStoreLocation, keyStoreBase64, keyStorePassword,
                    trustStoreLocation, trustStorePassword));
        }

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClientBuilder.build()))
                .build();
    }

    private SSLContext sslContext(
            ResourceLoader resourceLoader,
            String keyStoreLocation, String keyStoreBase64, String keyStorePassword,
            String trustStoreLocation, String trustStorePassword) {
        String source = keyStoreBase64.isBlank() ? keyStoreLocation : "(base64 env)";
        try {
            char[] keyPassword = keyStorePassword.toCharArray();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(loadClientKeyStore(resourceLoader, keyStoreLocation, keyStoreBase64, keyPassword), keyPassword);

            // 트러스트스토어를 지정하지 않으면 JDK 기본 CA 를 쓴다(토스는 공인 인증서라 보통 불필요).
            TrustManagerFactory tmf = null;
            if (!trustStoreLocation.isBlank()) {
                tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(loadKeyStore(resourceLoader, trustStoreLocation, trustStorePassword.toCharArray()));
            }

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(), tmf == null ? null : tmf.getTrustManagers(), null);
            return context;
        } catch (Exception e) {
            // 인증서 설정이 잘못됐으면 기동 시점에 바로 실패시킨다(런타임에 로그인만 조용히 깨지는 것보다 낫다).
            throw new IllegalStateException("토스 mTLS 인증서 설정에 실패했습니다: " + source, e);
        }
    }

    /** 클라이언트 keystore 는 base64 환경변수(우선) 또는 파일 경로에서 읽는다. */
    private KeyStore loadClientKeyStore(
            ResourceLoader resourceLoader, String location, String base64, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = openClientKeyStore(resourceLoader, location, base64)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }

    private InputStream openClientKeyStore(ResourceLoader resourceLoader, String location, String base64)
            throws IOException {
        if (!base64.isBlank()) {
            // 개행이 섞여도 견디도록 MIME 디코더 사용(openssl base64 는 64자마다 줄바꿈).
            return new ByteArrayInputStream(Base64.getMimeDecoder().decode(base64.strip()));
        }
        return resourceLoader.getResource(location).getInputStream();
    }

    private KeyStore loadKeyStore(ResourceLoader resourceLoader, String location, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = resourceLoader.getResource(location).getInputStream()) {
            keyStore.load(in, password);
        }
        return keyStore;
    }
}
